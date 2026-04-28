package com.houserental.service.impl;

import com.houserental.common.exception.BusinessException;
import com.houserental.common.result.PageResult;
import com.houserental.common.result.ResultCode;
import com.houserental.common.utils.SecurityUtils;
import com.houserental.dto.HouseQueryRequest;
import com.houserental.entity.AuditLog;
import com.houserental.entity.Favorite;
import com.houserental.entity.House;
import com.houserental.mapper.FavoriteMapper;
import com.houserental.mapper.HouseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.houserental.service.AuditLogService;
import com.houserental.service.HouseService;
import com.houserental.service.FileService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 房源服务实现类 */
@Service
@RequiredArgsConstructor
public class HouseServiceImpl implements HouseService {

    private static final Logger log = LoggerFactory.getLogger(HouseServiceImpl.class);
    private final HouseMapper houseMapper;
    private final FavoriteMapper favoriteMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final FileService fileService;
    private final AuditLogService auditLogService;


    private static final String FAVORITE_SET = "favorite:user:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "houses", allEntries = true)
    public House publish(House house) {
        house.setHouseNo(generateHouseNo());
        house.setLandlordId(SecurityUtils.getCurrentUserId());
        house.setStatus(2);
        house.setAuditStatus(0);
        house.setViewCount(0);
        house.setFavoriteCount(0);
        house.setAppointmentCount(0);
        
        houseMapper.insert(house);
        
        syncToElasticsearch(house);
        
        clearSearchCache();
        
        return house;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "houses", key = "#house.id")
    public House update(House house) {
        House existHouse = getById(house.getId());
        
        if (!existHouse.getLandlordId().equals(SecurityUtils.getCurrentUserId())) {
            throw new BusinessException("无权修改此房源");
        }

        updateHouseFields(existHouse, house);
        houseMapper.updateById(existHouse);
        
        syncToElasticsearch(existHouse);
        
        clearSearchCache();
        
        return existHouse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "houses", key = "#id")
    public void delete(Long id) {
        House house = getById(id);
        
        if (!house.getLandlordId().equals(SecurityUtils.getCurrentUserId())) {
            throw new BusinessException("无权删除此房源");
        }

        house.setIsDeleted(1);
        house.setStatus(2);
        houseMapper.updateById(house);
        
        deleteFromElasticsearch(id);
        
        clearSearchCache();
    }

    @Override
    public House getById(Long id) {
        log.info("获取房源详情, id: {}", id);
        House house = houseMapper.selectById(id);
        if (house == null || house.getIsDeleted() != 0) {
            log.warn("房源不存在或已删除, id: {}", id);
            throw new BusinessException(ResultCode.HOUSE_NOT_FOUND);
        }
        log.info("获取房源成功, id: {}, title: {}", id, house.getTitle());
        return house;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PageResult<House> search(HouseQueryRequest request) {
        String cacheKey = generateSearchCacheKey(request);
        PageResult<House> cachedResult = (PageResult<House>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            log.debug("使用缓存结果, cacheKey={}", cacheKey);
            return cachedResult;
        }

        log.info("执行房源搜索, status={}, city={}, district={}, rentWay={}, sortBy={}", 
                request.getStatus(), request.getCity(), request.getDistrict(), 
                request.getRentWay(), request.getSortBy());

        IPage<House> page = new Page<>(request.getCurrent(), request.getSize());

        QueryWrapper<House> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        
        if (request.getStatus() != null && request.getStatus() >= 0) {
            wrapper.eq("status", request.getStatus());
        }

        if (request.getCity() != null && !request.getCity().trim().isEmpty()) {
            wrapper.eq("city", request.getCity());
        }
        if (request.getDistrict() != null && !request.getDistrict().trim().isEmpty()) {
            wrapper.eq("district", request.getDistrict());
        }
        if (request.getHouseType() != null && !request.getHouseType().trim().isEmpty()) {
            wrapper.like("house_type", request.getHouseType());
        }
        if (request.getMinPrice() != null && !request.getMinPrice().trim().isEmpty()) {
            wrapper.ge("rent_price", new BigDecimal(request.getMinPrice()));
        }
        if (request.getMaxPrice() != null && !request.getMaxPrice().trim().isEmpty()) {
            wrapper.le("rent_price", new BigDecimal(request.getMaxPrice()));
        }
        if (request.getMinArea() != null && !request.getMinArea().trim().isEmpty()) {
            wrapper.ge("area", new BigDecimal(request.getMinArea()));
        }
        if (request.getMaxArea() != null && !request.getMaxArea().trim().isEmpty()) {
            wrapper.le("area", new BigDecimal(request.getMaxArea()));
        }
        if (request.getRentWay() != null) {
            wrapper.eq("rent_way", request.getRentWay());
            log.info("添加租赁方式筛选条件: rentWay={}", request.getRentWay());
        }

        if ("rentPrice".equals(request.getSortBy())) {
            wrapper.orderByAsc("rent_price");
        } else if ("area".equals(request.getSortBy())) {
            wrapper.orderByDesc("area");
        } else if ("viewCount".equals(request.getSortBy())) {
            wrapper.orderByDesc("view_count");
        } else {
            wrapper.orderByDesc("create_time");
        }

        IPage<House> result = houseMapper.selectPage(page, wrapper);
        
        log.info("搜索结果: 总数={}, 当前页记录数={}", result.getTotal(), result.getRecords().size());
        
        // 打印所有记录的ID和对象哈希码用于调试
        StringBuilder ids = new StringBuilder("房源ID列表: ");
        StringBuilder objects = new StringBuilder("对象哈希码列表: ");
        for (House h : result.getRecords()) {
            ids.append(h.getId()).append(", ");
            objects.append(System.identityHashCode(h)).append(", ");
        }
        log.info(ids.toString());
        log.info(objects.toString());
        
        // 检查是否有重复的ID
        java.util.Set<Long> uniqueIds = new java.util.HashSet<>();
        boolean hasDuplicateId = false;
        for (House h : result.getRecords()) {
            if (!uniqueIds.add(h.getId())) {
                hasDuplicateId = true;
                log.error("发现重复的房源ID: {}", h.getId());
                break;
            }
        }
        if (hasDuplicateId) {
            log.error("警告: 查询结果中存在重复的房源ID！");
        }
        
        // 检查是否是同一个对象引用
        java.util.Set<Integer> uniqueObjects = new java.util.HashSet<>();
        boolean hasSameObject = false;
        for (House h : result.getRecords()) {
            if (!uniqueObjects.add(System.identityHashCode(h))) {
                hasSameObject = true;
                log.error("发现重复的对象引用，哈希码: {}", System.identityHashCode(h));
                break;
            }
        }
        if (hasSameObject) {
            log.error("警告: 查询结果中存在相同的对象引用！");
        }

        PageResult<House> pageResult = PageResult.build(
                request.getCurrent().longValue(),
                request.getSize().longValue(),
                result.getTotal(),
                result.getRecords()
        );
        
        redisTemplate.opsForValue().set(cacheKey, pageResult, 5, TimeUnit.MINUTES);
        return pageResult;
    }
    
    /**
     * 生成搜索缓存 */
    private String generateSearchCacheKey(HouseQueryRequest request) {
        StringBuilder key = new StringBuilder("house:search:");
        key.append("page=").append(request.getCurrent()).append(":");
        key.append("size=").append(request.getSize()).append(":");
        key.append("status=").append(request.getStatus() != null ? request.getStatus() : "null").append(":");
        key.append("city=").append(request.getCity() != null ? request.getCity() : "null").append(":");
        key.append("district=").append(request.getDistrict() != null ? request.getDistrict() : "null").append(":");
        key.append("type=").append(request.getHouseType() != null ? request.getHouseType() : "null").append(":");
        key.append("minPrice=").append(request.getMinPrice() != null ? request.getMinPrice() : "null").append(":");
        key.append("maxPrice=").append(request.getMaxPrice() != null ? request.getMaxPrice() : "null").append(":");
        key.append("minArea=").append(request.getMinArea() != null ? request.getMinArea() : "null").append(":");
        key.append("maxArea=").append(request.getMaxArea() != null ? request.getMaxArea() : "null").append(":");
        key.append("rentWay=").append(request.getRentWay() != null ? request.getRentWay() : "null").append(":");
        key.append("sortBy=").append(request.getSortBy() != null ? request.getSortBy() : "createTime");
        return key.toString();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<House> getRecommendHouses(int limit) {
        String cacheKey = "house:recommend:" + limit;
        List<House> houses = (List<House>) redisTemplate.opsForValue().get(cacheKey);
        if (houses != null) {
            return houses;
        }
        
        houses = houseMapper.selectRecommendHouses(limit);
        redisTemplate.opsForValue().set(cacheKey, houses, 10, TimeUnit.MINUTES);
        return houses;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<House> getLatestHouses(int limit) {
        String cacheKey = "house:latest:" + limit;
        List<House> houses = (List<House>) redisTemplate.opsForValue().get(cacheKey);
        if (houses != null) {
            return houses;
        }
        
        houses = houseMapper.selectLatestHouses(limit);
        redisTemplate.opsForValue().set(cacheKey, houses, 10, TimeUnit.MINUTES);
        return houses;
    }

    @Override
    public List<House> getByLandlordId(Long landlordId) {
        return houseMapper.selectByLandlordId(landlordId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "houses", key = "#houseId")
    public House audit(Long houseId, Integer auditStatus, String auditRemark, Long auditorId) {
        House house = getById(houseId);
        
        Integer beforeStatus = house.getStatus();
        Integer beforeAuditStatus = house.getAuditStatus();
        
        house.setAuditStatus(auditStatus);
        house.setAuditRemark(auditRemark);
        house.setAuditorId(auditorId);
        house.setAuditTime(LocalDateTime.now());
        
        if (auditStatus == 1) {
            house.setStatus(0);
            syncToElasticsearch(house);
        } else if (auditStatus == 2) {
            deleteFromElasticsearch(houseId);
        }

        houseMapper.updateById(house);
        
        AuditLog auditLog = new AuditLog();
        auditLog.setHouseId(houseId);
        auditLog.setAuditorId(auditorId);
        auditLog.setAuditorName("审核人");
        auditLog.setBeforeStatus(beforeStatus);
        auditLog.setAfterStatus(house.getStatus());
        auditLog.setBeforeAuditStatus(beforeAuditStatus);
        auditLog.setAfterAuditStatus(auditStatus);
        auditLog.setAuditRemark(auditRemark);
        auditLog.setAuditResult(auditStatus);
        auditLogService.save(auditLog);
        
        clearSearchCache();
        
        return house;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "houses", key = "#id")
    public void online(Long id) {
        House house = getById(id);
        
        if (house.getAuditStatus() != 1) {
            throw new BusinessException("房源未通过审核，无法上架");
        }

        house.setStatus(0);
        houseMapper.updateById(house);
        
        syncToElasticsearch(house);
        
        clearSearchCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "houses", key = "#id")
    public void offline(Long id) {
        House house = getById(id);
        house.setStatus(2);
        houseMapper.updateById(house);
        
        deleteFromElasticsearch(id);
        
        clearSearchCache();
    }

    @Override
    public void incrementViewCount(Long id) {
        houseMapper.incrementViewCount(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFavorite(Long userId, Long houseId) {
        String key = FAVORITE_SET + userId;
        redisTemplate.opsForSet().add(key, houseId);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
        
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setHouseId(houseId);
        favoriteMapper.insert(favorite);
        
        houseMapper.incrementFavoriteCount(houseId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(Long userId, Long houseId) {
        String key = FAVORITE_SET + userId;
        redisTemplate.opsForSet().remove(key, houseId);
        
        favoriteMapper.deleteByUserIdAndHouseId(userId, houseId);
        
        houseMapper.decrementFavoriteCount(houseId);
    }

    @Override
    public boolean isFavorited(Long userId, Long houseId) {
        String key = FAVORITE_SET + userId;
        Boolean isMember = redisTemplate.opsForSet().isMember(key, houseId);
        
        if (Boolean.TRUE.equals(isMember)) {
            return true;
        }
        
        Favorite favorite = favoriteMapper.selectByUserIdAndHouseId(userId, houseId);
        if (favorite != null) {
            redisTemplate.opsForSet().add(key, houseId);
            redisTemplate.expire(key, 30, TimeUnit.DAYS);
            return true;
        }
        
        return false;
    }

    @Override
    public List<Long> getFavorites(Long userId) {
        String key = FAVORITE_SET + userId;
        Set<Object> members = redisTemplate.opsForSet().members(key);
        
        if (members != null && !members.isEmpty()) {
            return members.stream().map(obj -> Long.valueOf(obj.toString())).collect(java.util.stream.Collectors.toList());
        }
        
        List<Long> houseIds = favoriteMapper.selectHouseIdsByUserId(userId);
        if (houseIds != null && !houseIds.isEmpty()) {
            for (Long houseId : houseIds) {
                redisTemplate.opsForSet().add(key, houseId);
            }
        }
        
        return houseIds != null ? houseIds : java.util.Collections.emptyList();
    }

    @Override
    public PageResult<House> getFavoriteHouses(int page, int size, Long userId) {
        List<Long> favoriteIds = getFavorites(userId);
        
        if (favoriteIds == null || favoriteIds.isEmpty()) {
            return PageResult.build((long) page, (long) size, 0L, java.util.Collections.emptyList());
        }
        
        List<House> houses = houseMapper.selectBatchIds(favoriteIds);
        
        int start = (page - 1) * size;
        int end = Math.min(start + size, houses.size());
        
        List<House> pageList;
        if (start >= houses.size()) {
            pageList = java.util.Collections.emptyList();
        } else {
            pageList = houses.subList(start, end);
        }
        
        return PageResult.build((long) page, (long) size, (long) houses.size(), pageList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "houses", key = "#houseId")
    public List<String> uploadHouseImages(Long houseId, List<MultipartFile> files) {
        House house = getById(houseId);
        
        if (!house.getLandlordId().equals(SecurityUtils.getCurrentUserId())) {
            throw new BusinessException("无权操作此房源");
        }

        List<String> uploadedFiles = fileService.uploadBatch(files);
        log.info("图片上传到MinIO成功, houseId={}, uploadedFiles={}", houseId, uploadedFiles);
        
        try {
            List<String> existingImages = new ArrayList<>();
            if (house.getImages() != null && !house.getImages().isEmpty()) {
                JSONArray jsonArray = JSON.parseArray(house.getImages());
                for (Object item : jsonArray) {
                    existingImages.add(item.toString());
                }
            }
            
            existingImages.addAll(uploadedFiles);
            house.setImages(JSON.toJSONString(existingImages));
            
            if (existingImages.size() == uploadedFiles.size()) {
                house.setCoverImage(uploadedFiles.get(0));
            }
            
            houseMapper.updateById(house);
            log.info("数据库更新成功 houseId={}", houseId);
            return uploadedFiles;
            
        } catch (Exception e) {
            log.error("数据库更新失败，开始回滚MinIO文件, houseId={}, error={}", houseId, e.getMessage(), e);
            
            for (String fileName : uploadedFiles) {
                try {
                    fileService.delete(fileName);
                    log.info("已删除MinIO文件: {}", fileName);
                } catch (Exception deleteEx) {
                    log.error("删除MinIO文件失败, fileName={}, error={}", fileName, deleteEx.getMessage(), deleteEx);
                }
            }
            
            throw new BusinessException("图片上传失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "houses", key = "#houseId")
    public void setCoverImage(Long houseId, String imageName) {
        House house = getById(houseId);
        
        if (!house.getLandlordId().equals(SecurityUtils.getCurrentUserId())) {
            throw new BusinessException("无权操作此房源");
        }

        List<String> existingImages = new ArrayList<>();
        if (house.getImages() != null && !house.getImages().isEmpty()) {
            JSONArray jsonArray = JSON.parseArray(house.getImages());
            for (Object item : jsonArray) {
                existingImages.add(item.toString());
            }
        }
        
        if (!existingImages.contains(imageName)) {
            throw new BusinessException("图片不存在于房源图片列表中");
        }
        
        house.setCoverImage(imageName);
        houseMapper.updateById(house);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "houses", key = "#houseId")
    public void deleteHouseImage(Long houseId, String imageName) {
        House house = getById(houseId);
        
        if (!house.getLandlordId().equals(SecurityUtils.getCurrentUserId())) {
            throw new BusinessException("无权操作此房源");
        }

        fileService.delete(imageName);
        
        List<String> existingImages = new ArrayList<>();
        if (house.getImages() != null && !house.getImages().isEmpty()) {
            JSONArray jsonArray = JSON.parseArray(house.getImages());
            for (Object item : jsonArray) {
                existingImages.add(item.toString());
            }
        }
        
        existingImages.remove(imageName);
        house.setImages(JSON.toJSONString(existingImages));
        
        if (imageName.equals(house.getCoverImage())) {
            if (!existingImages.isEmpty()) {
                house.setCoverImage(existingImages.get(0));
            } else {
                house.setCoverImage(null);
            }
        }
        
        houseMapper.updateById(house);
    }

    private String generateHouseNo() {
        return "H" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private void clearSearchCache() {
        Set<String> keys = redisTemplate.keys("house:search:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    private void updateHouseFields(House existHouse, House house) {
        if (house.getTitle() != null) existHouse.setTitle(house.getTitle());
        if (house.getDescription() != null) existHouse.setDescription(house.getDescription());
        if (house.getProvince() != null) existHouse.setProvince(house.getProvince());
        if (house.getCity() != null) existHouse.setCity(house.getCity());
        if (house.getDistrict() != null) existHouse.setDistrict(house.getDistrict());
        if (house.getStreet() != null) existHouse.setStreet(house.getStreet());
        if (house.getAddress() != null) existHouse.setAddress(house.getAddress());
        if (house.getLongitude() != null) existHouse.setLongitude(house.getLongitude());
        if (house.getLatitude() != null) existHouse.setLatitude(house.getLatitude());
        if (house.getHouseType() != null) existHouse.setHouseType(house.getHouseType());
        if (house.getRoomCount() != null) existHouse.setRoomCount(house.getRoomCount());
        if (house.getHallCount() != null) existHouse.setHallCount(house.getHallCount());
        if (house.getBathroomCount() != null) existHouse.setBathroomCount(house.getBathroomCount());
        if (house.getArea() != null) existHouse.setArea(house.getArea());
        if (house.getFloor() != null) existHouse.setFloor(house.getFloor());
        if (house.getTotalFloor() != null) existHouse.setTotalFloor(house.getTotalFloor());
        if (house.getHasElevator() != null) existHouse.setHasElevator(house.getHasElevator());
        if (house.getDecoration() != null) existHouse.setDecoration(house.getDecoration());
        if (house.getOrientation() != null) existHouse.setOrientation(house.getOrientation());
        if (house.getRentPrice() != null) existHouse.setRentPrice(house.getRentPrice());
        if (house.getDepositMonth() != null) existHouse.setDepositMonth(house.getDepositMonth());
        if (house.getPaymentWay() != null) existHouse.setPaymentWay(house.getPaymentWay());
        if (house.getRentWay() != null) existHouse.setRentWay(house.getRentWay());
        if (house.getFacilities() != null) existHouse.setFacilities(house.getFacilities());
        if (house.getImages() != null) existHouse.setImages(house.getImages());
        if (house.getCoverImage() != null) existHouse.setCoverImage(house.getCoverImage());
        if (house.getContactName() != null) existHouse.setContactName(house.getContactName());
        if (house.getContactPhone() != null) existHouse.setContactPhone(house.getContactPhone());
        if (house.getViewTimeType() != null) existHouse.setViewTimeType(house.getViewTimeType());
        if (house.getAvailableDate() != null) existHouse.setAvailableDate(house.getAvailableDate());
        if (house.getMinLeaseTerm() != null) existHouse.setMinLeaseTerm(house.getMinLeaseTerm());
        if (house.getStatus() != null) existHouse.setStatus(house.getStatus());
        if (house.getAuditStatus() != null) existHouse.setAuditStatus(house.getAuditStatus());
    }

    /**
     * 同步房源数据到Elasticsearch
     */
    private void syncToElasticsearch(House house) {
        log.info("暂时禁用Elasticsearch同步: {}", house.getId());
    }

    /**
     * 从Elasticsearch删除房源数据
     */
    private void deleteFromElasticsearch(Long houseId) {
        log.info("暂时禁用Elasticsearch删除: {}", houseId);
    }


    @Override
    public List<House> searchByKeyword(String keyword) {
        log.info("使用MySQL降级方案搜索: {}", keyword);
        
        QueryWrapper<House> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like("title", keyword)
                             .or()
                             .like("description", keyword)
                             .or()
                             .like("address", keyword));
        }
        
        wrapper.orderByDesc("create_time");
        
        return houseMapper.selectList(wrapper);
    }

    @Override
    public List<House> searchWithFilters(String keyword, String city, String district, 
                                       Double minPrice, Double maxPrice, 
                                       Double minArea, Double maxArea, 
                                       Integer rentWay) {
        return searchWithFilters(keyword, city, district, minPrice, maxPrice, minArea, maxArea, rentWay, "createTime");
    }

    public List<House> searchWithFilters(String keyword, String city, String district, 
                                       Double minPrice, Double maxPrice, 
                                       Double minArea, Double maxArea, 
                                       Integer rentWay, String sortBy) {
        log.info("使用MySQL降级方案综合搜索: keyword={}, city={}, district={}", keyword, city, district);
        
        QueryWrapper<House> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like("title", keyword)
                             .or()
                             .like("description", keyword)
                             .or()
                             .like("address", keyword));
        }
        
        if (city != null && !city.trim().isEmpty()) {
            wrapper.eq("city", city);
        }
        
        if (district != null && !district.trim().isEmpty()) {
            wrapper.eq("district", district);
        }
        
        if (minPrice != null) {
            wrapper.ge("rent_price", new BigDecimal(minPrice.toString()));
        }
        
        if (maxPrice != null) {
            wrapper.le("rent_price", new BigDecimal(maxPrice.toString()));
        }
        
        if (minArea != null) {
            wrapper.ge("area", new BigDecimal(minArea.toString()));
        }
        
        if (maxArea != null) {
            wrapper.le("area", new BigDecimal(maxArea.toString()));
        }
        
        if (rentWay != null) {
            wrapper.eq("rent_way", rentWay);
        }
        
        if ("rentPrice".equals(sortBy)) {
            wrapper.orderByAsc("rent_price");
        } else if ("area".equals(sortBy)) {
            wrapper.orderByDesc("area");
        } else if ("viewCount".equals(sortBy)) {
            wrapper.orderByDesc("view_count");
        } else {
            wrapper.orderByDesc("create_time");
        }
        
        return houseMapper.selectList(wrapper);
    }

    @Override
    public java.util.List<com.houserental.entity.AuditLog> getAuditLogs(Long houseId) {
        return auditLogService.getByHouseId(houseId);
    }

    @Override
    public com.houserental.common.result.PageResult<House> getPendingAuditList(int page, int size) {
        com.baomidou.mybatisplus.core.metadata.IPage<House> pageInfo = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<House> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("audit_status", 0).orderByDesc("create_time");
        com.baomidou.mybatisplus.core.metadata.IPage<House> result = houseMapper.selectPage(pageInfo, wrapper);
        return com.houserental.common.result.PageResult.build(
                pageInfo.getCurrent(),
                pageInfo.getSize(),
                result.getTotal(),
                result.getRecords()
        );
    }

    @Override
    public com.houserental.common.result.PageResult<House> getApprovedHouses(int page, int size) {
        com.baomidou.mybatisplus.core.metadata.IPage<House> pageInfo = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<House> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("audit_status", 1).orderByDesc("audit_time");
        com.baomidou.mybatisplus.core.metadata.IPage<House> result = houseMapper.selectPage(pageInfo, wrapper);
        return com.houserental.common.result.PageResult.build(
                pageInfo.getCurrent(),
                pageInfo.getSize(),
                result.getTotal(),
                result.getRecords()
        );
    }

    @Override
    public com.houserental.common.result.PageResult<House> getRejectedHouses(int page, int size) {
        com.baomidou.mybatisplus.core.metadata.IPage<House> pageInfo = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<House> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("audit_status", 2).orderByDesc("audit_time");
        com.baomidou.mybatisplus.core.metadata.IPage<House> result = houseMapper.selectPage(pageInfo, wrapper);
        return com.houserental.common.result.PageResult.build(
                pageInfo.getCurrent(),
                pageInfo.getSize(),
                result.getTotal(),
                result.getRecords()
        );
    }

    @Override
    public List<House> list() {
        return houseMapper.selectList(null);
    }

    @Override
    public void save(House house) {
        houseMapper.insert(house);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanAll() {
        // 直接执行 SQL 语句删除所有数据
        houseMapper.delete(null);
        clearSearchCache();
        log.info("已清空所有房源数据");
    }
}

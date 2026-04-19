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
 * 房源服务实现类
 */
@Service
@RequiredArgsConstructor
public class HouseServiceImpl implements HouseService {

    private static final Logger log = LoggerFactory.getLogger(HouseServiceImpl.class);
    private final HouseMapper houseMapper;
    private final FavoriteMapper favoriteMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final FileService fileService;
    // private final HouseRepository houseRepository;
    private final AuditLogService auditLogService;


    private static final String FAVORITE_SET = "favorite:user:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "houses", allEntries = true)
    public House publish(House house) {
        house.setHouseNo(generateHouseNo());
        house.setLandlordId(SecurityUtils.getCurrentUserId());
        house.setStatus(0);
        house.setAuditStatus(0);
        house.setViewCount(0);
        house.setFavoriteCount(0);
        house.setAppointmentCount(0);
        
        houseMapper.insert(house);
        
        // 同步到Elasticsearch
        syncToElasticsearch(house);
        
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
        
        // 同步到Elasticsearch
        syncToElasticsearch(existHouse);
        
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
        house.setStatus(3); // 设置为已下架状态
        houseMapper.updateById(house);
        
        // 从Elasticsearch删除
        deleteFromElasticsearch(id);
    }

    @Override
    @Cacheable(value = "houses", key = "#id")
    public House getById(Long id) {
        House house = houseMapper.selectById(id);
        if (house == null || house.getIsDeleted() != 0) {
            throw new BusinessException(ResultCode.HOUSE_NOT_FOUND);
        }
        return house;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PageResult<House> search(HouseQueryRequest request) {
        // 生成缓存键
        String cacheKey = generateSearchCacheKey(request);
        PageResult<House> cachedResult = (PageResult<House>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        IPage<House> page = new Page<>(request.getCurrent(), request.getSize());

        BigDecimal minPrice = request.getMinPrice() != null ? new BigDecimal(request.getMinPrice()) : null;
        BigDecimal maxPrice = request.getMaxPrice() != null ? new BigDecimal(request.getMaxPrice()) : null;
        BigDecimal minArea = request.getMinArea() != null ? new BigDecimal(request.getMinArea()) : null;
        BigDecimal maxArea = request.getMaxArea() != null ? new BigDecimal(request.getMaxArea()) : null;

        IPage<House> result = houseMapper.searchHouses(
                page,
                2,
                request.getCity(),
                request.getDistrict(),
                request.getHouseType(),
                minPrice,
                maxPrice,
                minArea,
                maxArea,
                request.getRentWay()
        );

        PageResult<House> pageResult = PageResult.build(
                request.getCurrent().longValue(),
                request.getSize().longValue(),
                result.getTotal(),
                result.getRecords()
        );
        
        // 设置缓存，有效期5分钟
        redisTemplate.opsForValue().set(cacheKey, pageResult, 5, TimeUnit.MINUTES);
        return pageResult;
    }
    
    /**
     * 生成搜索缓存键
     */
    private String generateSearchCacheKey(HouseQueryRequest request) {
        StringBuilder key = new StringBuilder("house:search:");
        key.append("page=").append(request.getCurrent()).append(":");
        key.append("size=").append(request.getSize()).append(":");
        key.append("city=").append(request.getCity() != null ? request.getCity() : "null").append(":");
        key.append("district=").append(request.getDistrict() != null ? request.getDistrict() : "null").append(":");
        key.append("type=").append(request.getHouseType() != null ? request.getHouseType() : "null").append(":");
        key.append("minPrice=").append(request.getMinPrice() != null ? request.getMinPrice() : "null").append(":");
        key.append("maxPrice=").append(request.getMaxPrice() != null ? request.getMaxPrice() : "null").append(":");
        key.append("minArea=").append(request.getMinArea() != null ? request.getMinArea() : "null").append(":");
        key.append("maxArea=").append(request.getMaxArea() != null ? request.getMaxArea() : "null").append(":");
        key.append("rentWay=").append(request.getRentWay() != null ? request.getRentWay() : "null");
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
        
        // 记录审核前的状态
        Integer beforeStatus = house.getStatus();
        Integer beforeAuditStatus = house.getAuditStatus();
        
        house.setAuditStatus(auditStatus);
        house.setAuditRemark(auditRemark);
        house.setAuditorId(auditorId);
        house.setAuditTime(LocalDateTime.now());
        
        if (auditStatus == 1) {
            house.setStatus(2);
            // 审核通过，同步到Elasticsearch
            syncToElasticsearch(house);
        } else if (auditStatus == 2) {
            // 审核拒绝，从Elasticsearch删除
            deleteFromElasticsearch(houseId);
        }

        houseMapper.updateById(house);
        
        // 记录审核日志
        AuditLog auditLog = new AuditLog();
        auditLog.setHouseId(houseId);
        auditLog.setAuditorId(auditorId);
        auditLog.setAuditorName("审核员"); // 这里可以从用户服务获取审核员姓名
        auditLog.setBeforeStatus(beforeStatus);
        auditLog.setAfterStatus(house.getStatus());
        auditLog.setBeforeAuditStatus(beforeAuditStatus);
        auditLog.setAfterAuditStatus(auditStatus);
        auditLog.setAuditRemark(auditRemark);
        auditLog.setAuditResult(auditStatus);
        auditLogService.save(auditLog);
        
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

        house.setStatus(2);
        houseMapper.updateById(house);
        
        // 上架，同步到Elasticsearch
        syncToElasticsearch(house);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "houses", key = "#id")
    public void offline(Long id) {
        House house = getById(id);
        house.setStatus(3);
        houseMapper.updateById(house);
        
        // 下架，从Elasticsearch删除
        deleteFromElasticsearch(id);
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
            redisTemplate.expire(key, 30, TimeUnit.DAYS);
        }
        
        return houseIds != null ? houseIds : java.util.Collections.emptyList();
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
            log.info("数据库更新成功, houseId={}", houseId);
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

        // 验证图片是否存在于房源图片列表中
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

        // 从MinIO删除图片
        fileService.delete(imageName);
        
        // 更新房源图片信息
        List<String> existingImages = new ArrayList<>();
        if (house.getImages() != null && !house.getImages().isEmpty()) {
            JSONArray jsonArray = JSON.parseArray(house.getImages());
            for (Object item : jsonArray) {
                existingImages.add(item.toString());
            }
        }
        
        existingImages.remove(imageName);
        house.setImages(JSON.toJSONString(existingImages));
        
        // 如果删除的是封面图片，重新设置封面
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
    }

    /**
     * 同步房源数据到Elasticsearch
     */
    private void syncToElasticsearch(House house) {
        // 暂时禁用Elasticsearch同步
        log.info("暂时禁用Elasticsearch同步: {}", house.getId());
    }

    /**
     * 从Elasticsearch删除房源数据
     */
    private void deleteFromElasticsearch(Long houseId) {
        // 暂时禁用Elasticsearch删除
        log.info("暂时禁用Elasticsearch删除: {}", houseId);
    }

    

    @Override
    public List<House> searchByKeyword(String keyword) {
        log.info("使用MySQL降级方案搜索: {}", keyword);
        
        QueryWrapper<House> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0)
               .eq("status", 2);
        
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
        log.info("使用MySQL降级方案综合搜索: keyword={}, city={}, district={}", keyword, city, district);
        
        QueryWrapper<House> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0)
               .eq("status", 2);
        
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
        
        wrapper.orderByDesc("create_time");
        
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
        wrapper.eq("audit_status", 0).eq("deleted", 0).orderByDesc("created_time");
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
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<House> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        wrapper.eq("deleted", 0);
        return houseMapper.selectList(wrapper);
    }

    @Override
    public void save(House house) {
        houseMapper.insert(house);
    }
}

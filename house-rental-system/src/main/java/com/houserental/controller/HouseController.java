package com.houserental.controller;

import com.houserental.common.result.PageResult;
import com.houserental.common.result.Result;
import com.houserental.common.utils.SecurityUtils;
import com.houserental.dto.HouseQueryRequest;
import com.houserental.entity.House;
import com.houserental.entity.User;
import com.houserental.service.HouseService;
import com.houserental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 房源控制器
 */
@RestController
@RequestMapping("/houses")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;
    private final UserService userService;

    @PostMapping
    public Result<House> publish(@RequestBody House house) {
        House result = houseService.publish(house);
        return Result.success(result);
    }

    @PutMapping("/{id}")
    public Result<House> update(@PathVariable Long id, @RequestBody House house) {
        house.setId(id);
        House result = houseService.update(house);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        houseService.delete(id);
        return Result.success();
    }

    /**
     * 查询已通过审核的房源列表
     */
    @GetMapping("/approved")
    public Result<com.houserental.common.result.PageResult<House>> getApprovedHouses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        com.houserental.common.result.PageResult<House> result = houseService.getApprovedHouses(page, size);
        return Result.success(result);
    }

    /**
     * 查询已拒绝的房源列表
     */
    @GetMapping("/rejected")
    public Result<com.houserental.common.result.PageResult<House>> getRejectedHouses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        com.houserental.common.result.PageResult<House> result = houseService.getRejectedHouses(page, size);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<House> getById(@PathVariable Long id) {
        houseService.incrementViewCount(id);
        House house = houseService.getById(id);
        return Result.success(house);
    }

    @GetMapping("/search")
    public Result<PageResult<House>> search(HouseQueryRequest request) {
        PageResult<House> result = houseService.search(request);
        return Result.success(result);
    }

    @GetMapping("/recommend")
    public Result<List<House>> getRecommend(@RequestParam(defaultValue = "8") int limit) {
        List<House> houses = houseService.getRecommendHouses(limit);
        return Result.success(houses);
    }

    @GetMapping("/latest")
    public Result<List<House>> getLatest(@RequestParam(defaultValue = "8") int limit) {
        List<House> houses = houseService.getLatestHouses(limit);
        return Result.success(houses);
    }

    @GetMapping("/my")
    public Result<List<House>> getMyHouses() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        List<House> houses = houseService.getByLandlordId(userId);
        return Result.success(houses);
    }

    @PostMapping("/{id}/audit")
    public Result<House> audit(@PathVariable Long id,
                               @RequestParam Integer auditStatus,
                               @RequestParam(required = false) String auditRemark) {
        Long auditorId = SecurityUtils.getCurrentUserId();
        if (auditorId == null) {
            return Result.error("用户未登录");
        }
        House house = houseService.audit(id, auditStatus, auditRemark, auditorId);
        return Result.success(house);
    }

    @PostMapping("/{id}/online")
    public Result<Void> online(@PathVariable Long id) {
        houseService.online(id);
        return Result.success();
    }

    @PostMapping("/{id}/offline")
    public Result<Void> offline(@PathVariable Long id) {
        houseService.offline(id);
        return Result.success();
    }

    @PostMapping("/{id}/favorite")
    public Result<Void> addFavorite(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        houseService.addFavorite(userId, id);
        return Result.success();
    }

    @DeleteMapping("/{id}/favorite")
    public Result<Void> removeFavorite(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        houseService.removeFavorite(userId, id);
        return Result.success();
    }

    @GetMapping("/{id}/favorite")
    public Result<Boolean> isFavorited(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.success(false);
        }
        boolean result = houseService.isFavorited(userId, id);
        return Result.success(result);
    }

    @GetMapping("/favorites/list")
    public Result<PageResult<House>> getFavoriteHouses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        PageResult<House> result = houseService.getFavoriteHouses(page, size, userId);
        return Result.success(result);
    }

    @GetMapping
    public Result<List<Map<String, Object>>> getAll() {
        List<House> houses = houseService.list();
        List<Map<String, Object>> result = houses.stream().map(house -> {
            Map<String, Object> map = new HashMap<>();
            BeanUtils.copyProperties(house, map);
            // 字段名映射
            map.put("houseName", house.getTitle());
            map.put("title", house.getTitle());
            map.put("rent", house.getRentPrice());
            map.put("rentPrice", house.getRentPrice());
            // 确保面积字段正确转换
            map.put("area", house.getArea() != null ? house.getArea().doubleValue() : 0);
            // 显式添加地址字段
            map.put("address", house.getAddress());
            map.put("province", house.getProvince());
            map.put("city", house.getCity());
            map.put("district", house.getDistrict());
            map.put("street", house.getStreet());
            // 显式添加户型字段
            map.put("houseType", house.getHouseType());
            // 显式添加创建时间字段
            map.put("create_time", house.getCreateTime());
            map.put("createTime", house.getCreateTime());
            User landlord = userService.getById(house.getLandlordId());
            if (landlord != null) {
                map.put("landlordName", landlord.getRealName() != null ? landlord.getRealName() : landlord.getUsername());
            } else {
                map.put("landlordName", "未知");
            }
            return map;
        }).collect(Collectors.toList());
        return Result.success(result);
    }

    /**
     * 上传房源图片
     */
    @PostMapping("/{id}/images")
    public Result<List<String>> uploadImages(@PathVariable Long id, @RequestParam("files") List<MultipartFile> files) {
        List<String> fileNames = houseService.uploadHouseImages(id, files);
        return Result.success(fileNames);
    }

    /**
     * 设置房源封面图片
     */
    @PutMapping("/{id}/cover")
    public Result<Void> setCoverImage(@PathVariable Long id, @RequestParam("imageName") String imageName) {
        houseService.setCoverImage(id, imageName);
        return Result.success();
    }

    /**
     * 删除房源图片
     */
    @DeleteMapping("/{id}/images")
    public Result<Void> deleteImage(@PathVariable Long id, @RequestParam("imageName") String imageName) {
        houseService.deleteHouseImage(id, imageName);
        return Result.success();
    }

    /**
     * 基于Elasticsearch的全文搜索
     */
    @GetMapping("/search/es")
    public Result<List<House>> searchByKeyword(@RequestParam("keyword") String keyword) {
        List<House> houses = houseService.searchByKeyword(keyword);
        return Result.success(houses);
    }

    /**
     * 基于Elasticsearch的综合搜索
     */
    @GetMapping("/search/es/filters")
    public Result<List<House>> searchWithFilters(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minArea,
            @RequestParam(required = false) Double maxArea,
            @RequestParam(required = false) Integer rentWay) {
        List<House> houses = houseService.searchWithFilters(keyword, city, district, minPrice, maxPrice, minArea, maxArea, rentWay);
        return Result.success(houses);
    }

    /**
     * 获取房源的审核日志
     */
    @GetMapping("/{id}/audit-logs")
    public Result<List<com.houserental.entity.AuditLog>> getAuditLogs(@PathVariable Long id) {
        List<com.houserental.entity.AuditLog> logs = houseService.getAuditLogs(id);
        return Result.success(logs);
    }

    /**
     * 查询待审核的房源列表
     */
    @GetMapping("/pending-audit")
    public Result<com.houserental.common.result.PageResult<House>> getPendingAuditList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        com.houserental.common.result.PageResult<House> result = houseService.getPendingAuditList(page, size);
        return Result.success(result);
    }

    /**
     * 获取热门区域房源统计
     */
    @GetMapping("/area-statistics")
    public Result<List<java.util.Map<String, Object>>> getAreaStatistics() {
        List<java.util.Map<String, Object>> statistics = houseService.getAreaStatistics();
        return Result.success(statistics);
    }
}

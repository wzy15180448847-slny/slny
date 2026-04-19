package com.houserental.controller;

import com.houserental.common.result.PageResult;
import com.houserental.common.result.Result;
import com.houserental.common.utils.SecurityUtils;
import com.houserental.dto.HouseQueryRequest;
import com.houserental.entity.House;
import com.houserental.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 房源控制器
 */
@RestController
@RequestMapping("/houses")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;

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
    public Result<List<House>> getAll() {
        List<House> houses = houseService.getRecommendHouses(10);
        return Result.success(houses);
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
}

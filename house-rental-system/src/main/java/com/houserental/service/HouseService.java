package com.houserental.service;

import com.houserental.common.result.PageResult;
import com.houserental.dto.HouseQueryRequest;
import com.houserental.entity.House;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 房源服务接口
 */
public interface HouseService {

    /**
     * 发布房源
     */
    House publish(House house);

    /**
     * 更新房源
     */
    House update(House house);

    /**
     * 删除房源
     */
    void delete(Long id);

    /**
     * 根据ID查询房源
     */
    House getById(Long id);

    /**
     * 多条件查询房源
     */
    PageResult<House> search(HouseQueryRequest request);

    /**
     * 查询推荐房源
     */
    List<House> getRecommendHouses(int limit);

    /**
     * 查询最新房源
     */
    List<House> getLatestHouses(int limit);

    /**
     * 查询房东的房源列表
     */
    List<House> getByLandlordId(Long landlordId);

    /**
     * 审核房源
     */
    House audit(Long houseId, Integer auditStatus, String auditRemark, Long auditorId);

    /**
     * 上架房源
     */
    void online(Long id);

    /**
     * 下架房源
     */
    void offline(Long id);

    /**
     * 增加浏览次数
     */
    void incrementViewCount(Long id);

    /**
     * 收藏房源
     */
    void addFavorite(Long userId, Long houseId);

    /**
     * 取消收藏
     */
    void removeFavorite(Long userId, Long houseId);

    /**
     * 检查是否已收藏
     */
    boolean isFavorited(Long userId, Long houseId);

    /**
     * 获取用户收藏列表
     */
    List<Long> getFavorites(Long userId);

    /**
     * 获取用户收藏的房源列表（分页）
     */
    PageResult<House> getFavoriteHouses(int page, int size, Long userId);

    /**
     * 上传房源图片
     */
    List<String> uploadHouseImages(Long houseId, List<MultipartFile> files);

    /**
     * 设置房源封面图片
     */
    void setCoverImage(Long houseId, String imageName);

    /**
     * 删除房源图片
     */
    void deleteHouseImage(Long houseId, String imageName);

    /**
     * 基于Elasticsearch的全文搜索
     */
    List<House> searchByKeyword(String keyword);

    /**
     * 基于Elasticsearch的综合搜索
     */
    List<House> searchWithFilters(String keyword, String city, String district, 
                                 Double minPrice, Double maxPrice, 
                                 Double minArea, Double maxArea, 
                                 Integer rentWay);

    /**
     * 查询房源的审核日志
     */
    java.util.List<com.houserental.entity.AuditLog> getAuditLogs(Long houseId);

    /**
     * 查询待审核的房源列表
     */
    com.houserental.common.result.PageResult<House> getPendingAuditList(int page, int size);

    /**
     * 查询所有房源列表
     */
    List<House> list();

    /**
     * 保存房源
     */
    void save(House house);
}

package com.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.houserental.entity.House;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 房源数据访问层
 */
@Repository
public interface HouseMapper extends BaseMapper<House> {

    /**
     * 根据房源编号查询
     */
    House selectByHouseNo(String houseNo);

    /**
     * 根据房东ID查询房源列表
     */
    List<House> selectByLandlordId(Long landlordId);

    /**
     * 根据房东ID和状态查询房源列表
     */
    List<House> selectByLandlordIdAndStatus(@Param("landlordId") Long landlordId, @Param("status") Integer status);

    /**
     * 根据城市查询房源列表
     */
    IPage<House> selectByCityAndStatus(IPage<House> page, @Param("city") String city, @Param("status") Integer status);

    /**
     * 根据城市和区域查询房源列表
     */
    IPage<House> selectByCityAndDistrictAndStatus(IPage<House> page, @Param("city") String city, @Param("district") String district, @Param("status") Integer status);

    /**
     * 多条件查询房源
     */
    IPage<House> searchHouses(IPage<House> page,
                              @Param("status") Integer status,
                              @Param("city") String city,
                              @Param("district") String district,
                              @Param("houseType") String houseType,
                              @Param("minPrice") BigDecimal minPrice,
                              @Param("maxPrice") BigDecimal maxPrice,
                              @Param("minArea") BigDecimal minArea,
                              @Param("maxArea") BigDecimal maxArea,
                              @Param("rentWay") Integer rentWay,
                              @Param("sortBy") String sortBy);

    /**
     * 查询推荐房源（按浏览量排序）
     */
    List<House> selectRecommendHouses(@Param("limit") Integer limit);

    /**
     * 查询最新房源
     */
    List<House> selectLatestHouses(@Param("limit") Integer limit);

    /**
     * 增加浏览次数
     */
    void incrementViewCount(@Param("id") Long id);

    /**
     * 增加收藏次数
     */
    void incrementFavoriteCount(@Param("id") Long id);

    /**
     * 减少收藏次数
     */
    void decrementFavoriteCount(@Param("id") Long id);

    /**
     * 统计房东的房源数量
     */
    long countByLandlordIdAndDeleted(@Param("landlordId") Long landlordId, @Param("deleted") Integer deleted);

    /**
     * 统计各状态的房源数量
     */
    long countByStatusAndDeleted(@Param("status") Integer status, @Param("deleted") Integer deleted);

    /**
     * 统计今日新增房源数量
     */
    Long countTodayNew();

    /**
     * 统计待审核房源数量
     */
    Long countPendingAudit();

    /**
     * 统计所有未删除房源数量
     */
    Long countAllByDeleted(@Param("deleted") Integer deleted);

    /**
     * 查询房源区域分布
     */
    List<Map<String, Object>> getRegionDistribution();

    /**
     * 按月计算房源出租率
     */
    Integer calculateRentRateByMonth(@Param("month") String month);

}

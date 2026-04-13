package com.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houserental.entity.Favorite;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteMapper extends BaseMapper<Favorite> {

    List<Long> selectHouseIdsByUserId(@Param("userId") Long userId);

    Favorite selectByUserIdAndHouseId(@Param("userId") Long userId, @Param("houseId") Long houseId);

    void deleteByUserIdAndHouseId(@Param("userId") Long userId, @Param("houseId") Long houseId);
}
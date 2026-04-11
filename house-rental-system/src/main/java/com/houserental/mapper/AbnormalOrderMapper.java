package com.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houserental.entity.AbnormalOrder;

/**
 * 异常订单Mapper
 */
public interface AbnormalOrderMapper extends BaseMapper<AbnormalOrder> {

    /**
     * 根据订单类型和订单ID查询异常订单
     * @param orderType 订单类型
     * @param orderId 订单ID
     * @return 异常订单
     */
    AbnormalOrder selectByOrderTypeAndId(Integer orderType, Long orderId);

    /**
     * 根据处理状态查询异常订单列表
     * @param status 处理状态
     * @return 异常订单列表
     */
    java.util.List<AbnormalOrder> selectByStatus(Integer status);

    /**
     * 根据异常类型查询异常订单列表
     * @param abnormalType 异常类型
     * @return 异常订单列表
     */
    java.util.List<AbnormalOrder> selectByAbnormalType(Integer abnormalType);
}

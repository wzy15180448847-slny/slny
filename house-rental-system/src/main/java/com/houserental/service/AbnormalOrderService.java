package com.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.houserental.entity.AbnormalOrder;
import com.houserental.common.result.PageResult;

/**
 * 异常订单服务
 */
public interface AbnormalOrderService extends IService<AbnormalOrder> {

    /**
     * 提交异常订单
     * @param abnormalOrder 异常订单信息
     * @return 提交结果
     */
    boolean submitAbnormalOrder(AbnormalOrder abnormalOrder);

    /**
     * 处理异常订单
     * @param id 异常订单ID
     * @param status 处理状态
     * @param processPlan 处理方案
     * @param processorId 处理人ID
     * @return 处理结果
     */
    boolean processAbnormalOrder(Long id, Integer status, String processPlan, Long processorId);

    /**
     * 根据ID查询异常订单详情
     * @param id 异常订单ID
     * @return 异常订单详情
     */
    AbnormalOrder getById(Long id);

    /**
     * 根据订单类型和订单ID查询异常订单
     * @param orderType 订单类型
     * @param orderId 订单ID
     * @return 异常订单
     */
    AbnormalOrder getByOrderTypeAndId(Integer orderType, Long orderId);

    /**
     * 查询待处理的异常订单列表
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<AbnormalOrder> getPendingList(int page, int size);

    /**
     * 查询处理中的异常订单列表
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<AbnormalOrder> getProcessingList(int page, int size);

    /**
     * 根据异常类型查询异常订单列表
     * @param abnormalType 异常类型
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<AbnormalOrder> getByAbnormalType(Integer abnormalType, int page, int size);
}

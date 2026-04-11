package com.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.houserental.entity.TerminationApplication;
import com.houserental.common.result.PageResult;

import java.util.Map;

/**
 * 解约申请服务
 */
public interface TerminationApplicationService extends IService<TerminationApplication> {

    /**
     * 创建解约申请
     * @param application 申请信息
     * @return 申请ID
     */
    Long createApplication(TerminationApplication application);

    /**
     * 处理解约申请
     * @param id 申请ID
     * @param status 处理状态（1-同意，2-拒绝）
     * @param processingOpinion 处理意见
     * @param penaltyAmount 违约金金额
     * @return 是否成功
     */
    boolean processApplication(Long id, Integer status, String processingOpinion, java.math.BigDecimal penaltyAmount);

    /**
     * 完成解约
     * @param id 申请ID
     * @return 是否成功
     */
    boolean completeTermination(Long id);

    /**
     * 计算违约金
     * @param leaseId 租约ID
     * @param terminationReason 解约原因
     * @return 违约金金额
     */
    java.math.BigDecimal calculatePenalty(Long leaseId, String terminationReason);

    /**
     * 分页查询解约申请
     * @param params 查询参数
     * @return 分页结果
     */
    PageResult<TerminationApplication> pageApplications(Map<String, Object> params);

    /**
     * 根据ID查询解约申请详情
     * @param id 申请ID
     * @return 申请信息
     */
    TerminationApplication getApplicationById(Long id);
}
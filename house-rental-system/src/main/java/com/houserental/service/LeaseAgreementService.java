package com.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.houserental.entity.LeaseAgreement;
import com.houserental.common.result.PageResult;

import java.util.Map;

/**
 * 租约服务
 */
public interface LeaseAgreementService extends IService<LeaseAgreement> {

    /**
     * 创建租约
     * @param lease 租约信息
     * @return 租约ID
     */
    Long createLease(LeaseAgreement lease);

    /**
     * 签署租约
     * @param id 租约ID
     * @param userId 用户ID
     * @param userType 用户类型
     * @param signatureData 签章数据
     * @return 是否成功
     */
    boolean signLease(Long id, Long userId, String userType, String signatureData);

    /**
     * 生效租约
     * @param id 租约ID
     * @return 是否成功
     */
    boolean effectiveLease(Long id);

    /**
     * 终止租约
     * @param id 租约ID
     * @param reason 终止原因
     * @return 是否成功
     */
    boolean terminateLease(Long id, String reason);

    /**
     * 分页查询租约
     * @param params 查询参数
     * @return 分页结果
     */
    PageResult<LeaseAgreement> pageLeases(Map<String, Object> params);

    /**
     * 根据ID查询租约详情
     * @param id 租约ID
     * @return 租约信息
     */
    LeaseAgreement getLeaseById(Long id);
}
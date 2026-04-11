package com.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.houserental.entity.ElectronicSignature;

import java.util.List;

/**
 * 电子签章服务
 */
public interface ElectronicSignatureService extends IService<ElectronicSignature> {

    /**
     * 创建电子签章
     * @param signature 签章信息
     * @return 签章ID
     */
    Long createSignature(ElectronicSignature signature);

    /**
     * 完成签章
     * @param id 签章ID
     * @param signatureData 签章数据
     * @return 是否成功
     */
    boolean completeSignature(Long id, String signatureData);

    /**
     * 根据租约ID查询签章列表
     * @param leaseId 租约ID
     * @return 签章列表
     */
    List<ElectronicSignature> getSignaturesByLeaseId(Long leaseId);

    /**
     * 根据用户ID和租约ID查询签章
     * @param userId 用户ID
     * @param leaseId 租约ID
     * @return 签章信息
     */
    ElectronicSignature getSignatureByUserAndLease(Long userId, Long leaseId);
}
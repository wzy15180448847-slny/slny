package com.houserental.service;

import com.houserental.entity.AuditLog;

import java.util.List;

/**
 * 审核日志服务接口
 */
public interface AuditLogService {

    /**
     * 记录审核日志
     */
    AuditLog save(AuditLog auditLog);

    /**
     * 根据房源ID查询审核日志
     */
    List<AuditLog> getByHouseId(Long houseId);

    /**
     * 查询最新的审核日志
     */
    AuditLog getLatestByHouseId(Long houseId);
}

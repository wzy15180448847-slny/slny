package com.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houserental.entity.AuditLog;

/**
 * 审核日志Mapper
 */
public interface AuditLogMapper extends BaseMapper<AuditLog> {

    /**
     * 根据房源ID查询审核日志
     */
    java.util.List<AuditLog> selectByHouseId(Long houseId);

    /**
     * 查询最新的审核日志
     */
    AuditLog selectLatestByHouseId(Long houseId);
}

package com.houserental.service.impl;

import com.houserental.entity.AuditLog;
import com.houserental.mapper.AuditLogMapper;
import com.houserental.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 审核日志服务实现类
 */
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogServiceImpl.class);
    private final AuditLogMapper auditLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuditLog save(AuditLog auditLog) {
        auditLogMapper.insert(auditLog);
        log.info("审核日志保存成功: {}", auditLog.getId());
        return auditLog;
    }

    @Override
    public List<AuditLog> getByHouseId(Long houseId) {
        return auditLogMapper.selectByHouseId(houseId);
    }

    @Override
    public AuditLog getLatestByHouseId(Long houseId) {
        return auditLogMapper.selectLatestByHouseId(houseId);
    }
}

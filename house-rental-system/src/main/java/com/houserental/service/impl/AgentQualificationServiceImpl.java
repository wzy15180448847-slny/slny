package com.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.houserental.entity.AgentQualification;
import com.houserental.entity.User;
import com.houserental.mapper.AgentQualificationMapper;
import com.houserental.mapper.UserMapper;
import com.houserental.common.result.PageResult;
import com.houserental.service.AgentQualificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 中介资质审核服务实现
 */
@Service
public class AgentQualificationServiceImpl extends ServiceImpl<AgentQualificationMapper, AgentQualification> implements AgentQualificationService {

    @Autowired
    private AgentQualificationMapper agentQualificationMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public boolean submitQualification(AgentQualification qualification) {
        // 检查中介是否存在
        User agent = userMapper.selectById(qualification.getAgentId());
        if (agent == null || !"AGENT".equals(agent.getUserType())) {
            return false;
        }

        // 检查是否已存在待审核的资质
        AgentQualification existing = agentQualificationMapper.selectByAgentId(qualification.getAgentId());
        if (existing != null && existing.getAuditStatus() == 0) {
            return false;
        }

        // 设置默认值
        qualification.setAuditStatus(0);
        qualification.setCreateTime(LocalDateTime.now());
        qualification.setUpdateTime(LocalDateTime.now());

        return save(qualification);
    }

    @Override
    @Transactional
    public boolean auditQualification(Long id, Integer auditStatus, String auditRemark, Long auditorId) {
        AgentQualification qualification = getById(id);
        if (qualification == null || qualification.getAuditStatus() != 0) {
            return false;
        }

        // 更新审核状态
        qualification.setAuditStatus(auditStatus);
        qualification.setAuditRemark(auditRemark);
        qualification.setAuditorId(auditorId);
        qualification.setAuditTime(LocalDateTime.now());
        qualification.setUpdateTime(LocalDateTime.now());

        boolean result = updateById(qualification);

        // 如果审核通过，更新中介用户状态
        if (result && auditStatus == 1) {
            User agent = userMapper.selectById(qualification.getAgentId());
            if (agent != null) {
                agent.setStatus(1); // 启用状态
                userMapper.updateById(agent);
            }
        }

        return result;
    }

    @Override
    public AgentQualification getByAgentId(Long agentId) {
        return agentQualificationMapper.selectByAgentId(agentId);
    }

    @Override
    public PageResult<AgentQualification> getPendingList(int page, int size) {
        Page<AgentQualification> pageInfo = new Page<>(page, size);
        QueryWrapper<AgentQualification> wrapper = new QueryWrapper<>();
        wrapper.eq("audit_status", 0).orderByDesc("created_time");
        page(pageInfo, wrapper);

        return PageResult.build((long) page, (long) size, pageInfo.getTotal(), pageInfo.getRecords());
    }

    @Override
    public PageResult<AgentQualification> getHistoryList(Long agentId, int page, int size) {
        Page<AgentQualification> pageInfo = new Page<>(page, size);
        QueryWrapper<AgentQualification> wrapper = new QueryWrapper<>();
        wrapper.eq("agent_id", agentId).orderByDesc("created_time");
        page(pageInfo, wrapper);

        return PageResult.build((long) page, (long) size, pageInfo.getTotal(), pageInfo.getRecords());
    }
}

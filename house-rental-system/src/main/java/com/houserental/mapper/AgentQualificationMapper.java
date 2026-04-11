package com.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houserental.entity.AgentQualification;

/**
 * 中介资质审核Mapper
 */
public interface AgentQualificationMapper extends BaseMapper<AgentQualification> {

    /**
     * 根据中介ID查询资质信息
     * @param agentId 中介用户ID
     * @return 中介资质信息
     */
    AgentQualification selectByAgentId(Long agentId);

    /**
     * 根据审核状态查询资质列表
     * @param auditStatus 审核状态
     * @return 资质列表
     */
    java.util.List<AgentQualification> selectByAuditStatus(Integer auditStatus);
}

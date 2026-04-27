package com.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.houserental.entity.AgentQualification;

/**
 * 中介资质审核Mapper
 */
public interface AgentQualificationMapper extends BaseMapper<AgentQualification> {

    /**
     * 根据用户ID查询资质信息
     * @param userId 用户ID
     * @return 资质信息
     */
    AgentQualification selectByUserId(Long userId);

    /**
     * 根据审核状态查询资质列表
     * @param auditStatus 审核状态
     * @return 资质列表
     */
    java.util.List<AgentQualification> selectByAuditStatus(Integer auditStatus);

    /**
     * 根据状态统计数量
     * @param status 状态
     * @return 数量
     */
    Long countByStatus(Integer status);
}

package com.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.houserental.entity.AgentQualification;
import com.houserental.common.result.PageResult;

/**
 * 中介资质审核服务
 */
public interface AgentQualificationService extends IService<AgentQualification> {

    /**
     * 提交中介资质审核
     * @param qualification 中介资质信息
     * @return 提交结果
     */
    boolean submitQualification(AgentQualification qualification);

    /**
     * 审核中介资质
     * @param id 资质ID
     * @param auditStatus 审核状态
     * @param auditRemark 审核意见
     * @param auditorId 审核人ID
     * @return 审核结果
     */
    boolean auditQualification(Long id, Integer auditStatus, String auditRemark, Long auditorId);

    /**
     * 根据中介ID查询资质信息
     * @param agentId 中介用户ID
     * @return 中介资质信息
     */
    AgentQualification getByAgentId(Long agentId);

    /**
     * 查询待审核的资质列表
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<AgentQualification> getPendingList(int page, int size);

    /**
     * 查询资质审核历史
     * @param agentId 中介用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    PageResult<AgentQualification> getHistoryList(Long agentId, int page, int size);
}

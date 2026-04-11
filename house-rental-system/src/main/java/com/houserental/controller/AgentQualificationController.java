package com.houserental.controller;

import com.houserental.entity.AgentQualification;
import com.houserental.common.result.Result;
import com.houserental.common.result.PageResult;
import com.houserental.service.AgentQualificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * 中介资质审核控制器
 */
@RestController
@RequestMapping("/api/agent-qualification")
public class AgentQualificationController {

    @Autowired
    private AgentQualificationService agentQualificationService;

    /**
     * 提交中介资质审核
     * @param qualification 中介资质信息
     * @param principal 当前用户
     * @return 操作结果
     */
    @PostMapping("/submit")
    public Result<?> submitQualification(@RequestBody AgentQualification qualification, Principal principal) {
        // 可以从principal中获取当前用户信息，设置agentId
        boolean result = agentQualificationService.submitQualification(qualification);
        if (result) {
            return Result.success("提交成功，等待审核");
        } else {
            return Result.error("提交失败，请检查信息");
        }
    }

    /**
     * 审核中介资质
     * @param id 资质ID
     * @param auditStatus 审核状态
     * @param auditRemark 审核意见
     * @param principal 当前用户
     * @return 操作结果
     */
    @PostMapping("/audit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> auditQualification(@PathVariable Long id, @RequestParam Integer auditStatus, 
                                       @RequestParam String auditRemark, Principal principal) {
        // 可以从principal中获取当前审核人ID
        Long auditorId = 1L; // 示例，实际应从用户信息中获取
        boolean result = agentQualificationService.auditQualification(id, auditStatus, auditRemark, auditorId);
        if (result) {
            return Result.success("审核成功");
        } else {
            return Result.error("审核失败，请检查信息");
        }
    }

    /**
     * 根据中介ID查询资质信息
     * @param agentId 中介用户ID
     * @return 资质信息
     */
    @GetMapping("/agent/{agentId}")
    public Result<AgentQualification> getByAgentId(@PathVariable Long agentId) {
        AgentQualification qualification = agentQualificationService.getByAgentId(agentId);
        return Result.success(qualification);
    }

    /**
     * 查询待审核的资质列表
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<AgentQualification>> getPendingList(@RequestParam int page, @RequestParam int size) {
        PageResult<AgentQualification> result = agentQualificationService.getPendingList(page, size);
        return Result.success(result);
    }

    /**
     * 查询资质审核历史
     * @param agentId 中介用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/history/{agentId}")
    public Result<PageResult<AgentQualification>> getHistoryList(@PathVariable Long agentId, 
                                                               @RequestParam int page, @RequestParam int size) {
        PageResult<AgentQualification> result = agentQualificationService.getHistoryList(agentId, page, size);
        return Result.success(result);
    }
}

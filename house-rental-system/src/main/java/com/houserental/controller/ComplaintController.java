package com.houserental.controller;

import com.houserental.entity.Complaint;
import com.houserental.common.result.Result;
import com.houserental.common.result.PageResult;
import com.houserental.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * 用户投诉控制器
 */
@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    /**
     * 提交投诉
     * @param complaint 投诉信息
     * @param principal 当前用户
     * @return 操作结果
     */
    @PostMapping("/submit")
    public Result<?> submitComplaint(@RequestBody Complaint complaint, Principal principal) {
        // 可以从principal中获取当前用户信息，设置complainantId
        boolean result = complaintService.submitComplaint(complaint);
        if (result) {
            return Result.success("投诉提交成功，等待处理");
        } else {
            return Result.error("投诉提交失败，请检查信息");
        }
    }

    /**
     * 处理投诉
     * @param id 投诉ID
     * @param status 处理状态
     * @param processResult 处理结果
     * @param principal 当前用户
     * @return 操作结果
     */
    @PostMapping("/process/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> processComplaint(@PathVariable Long id, @RequestParam Integer status, 
                                     @RequestParam String processResult, Principal principal) {
        // 可以从principal中获取当前处理人ID
        Long processorId = 1L; // 示例，实际应从用户信息中获取
        boolean result = complaintService.processComplaint(id, status, processResult, processorId);
        if (result) {
            return Result.success("投诉处理成功");
        } else {
            return Result.error("投诉处理失败，请检查信息");
        }
    }

    /**
     * 根据ID查询投诉详情
     * @param id 投诉ID
     * @return 投诉详情
     */
    @GetMapping("/{id}")
    public Result<Complaint> getById(@PathVariable Long id) {
        Complaint complaint = complaintService.getById(id);
        return Result.success(complaint);
    }

    /**
     * 查询投诉人自己的投诉列表
     * @param complainantId 投诉人ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/complainant/{complainantId}")
    public Result<PageResult<Complaint>> getByComplainantId(@PathVariable Long complainantId, 
                                                           @RequestParam int page, @RequestParam int size) {
        PageResult<Complaint> result = complaintService.getByComplainantId(complainantId, page, size);
        return Result.success(result);
    }

    /**
     * 查询被投诉人的投诉列表
     * @param respondentId 被投诉人ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/respondent/{respondentId}")
    public Result<PageResult<Complaint>> getByRespondentId(@PathVariable Long respondentId, 
                                                         @RequestParam int page, @RequestParam int size) {
        PageResult<Complaint> result = complaintService.getByRespondentId(respondentId, page, size);
        return Result.success(result);
    }

    /**
     * 查询待处理的投诉列表
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<Complaint>> getPendingList(@RequestParam int page, @RequestParam int size) {
        PageResult<Complaint> result = complaintService.getPendingList(page, size);
        return Result.success(result);
    }

    /**
     * 查询处理中的投诉列表
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/processing")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<Complaint>> getProcessingList(@RequestParam int page, @RequestParam int size) {
        PageResult<Complaint> result = complaintService.getProcessingList(page, size);
        return Result.success(result);
    }
}

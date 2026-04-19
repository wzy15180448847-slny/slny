package com.houserental.controller;

import com.houserental.common.result.PageResult;
import com.houserental.common.result.Result;
import com.houserental.entity.Complaint;
import com.houserental.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员投诉仲裁控制器
 */
@RestController
@RequestMapping("/admin/complaints")
@RequiredArgsConstructor
public class AdminComplaintController {

    private final ComplaintService complaintService;

    @GetMapping
    public Result<PageResult<Complaint>> getComplaints(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        
        Integer statusCode = null;
        if (status != null) {
            statusCode = convertStatus(status);
        }
        
        PageResult<Complaint> result = complaintService.getPendingList(current, size);
        
        return Result.success(result);
    }
    
    private Integer convertStatus(String status) {
        switch (status.toUpperCase()) {
            case "PENDING":
                return 0;
            case "PROCESSING":
                return 1;
            case "HANDLED":
                return 2;
            case "REJECTED":
                return 3;
            default:
                return null;
        }
    }

    @PutMapping("/{id}/arbitrate")
    public Result<Void> arbitrateComplaint(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Integer status = (Integer) request.get("status");
        String processResult = (String) request.get("punishment");
        
        if (processResult == null) {
            processResult = (String) request.get("processResult");
        }
        
        Long processorId = 1L;
        boolean success = complaintService.processComplaint(id, status, processResult, processorId);
        
        if (success) {
            return Result.success();
        }
        return Result.error("处理失败");
    }
}
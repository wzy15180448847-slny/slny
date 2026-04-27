package com.houserental.controller;

import com.houserental.common.result.PageResult;
import com.houserental.common.result.Result;
import com.houserental.dto.ComplaintDTO;
import com.houserental.entity.Complaint;
import com.houserental.entity.User;
import com.houserental.mapper.UserMapper;
import com.houserental.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员投诉仲裁控制器
 */
@RestController
@RequestMapping("/admin/complaints")
@RequiredArgsConstructor
public class AdminComplaintController {

    private final ComplaintService complaintService;
    private final UserMapper userMapper;

    @GetMapping
    public Result<PageResult<ComplaintDTO>> getComplaints(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {
        
        PageResult<Complaint> result;
        
        if ("HANDLED".equals(status)) {
            result = complaintService.getProcessingList(current, size);
        } else {
            result = complaintService.getPendingList(current, size);
        }
        
        List<ComplaintDTO> dtoList = result.getRecords().stream()
                .map(complaint -> {
                    User complainant = userMapper.selectById(complaint.getComplainantId());
                    User defendant = userMapper.selectById(complaint.getRespondentId());
                    String complainantName = complainant != null ? (complainant.getNickname() != null && !complainant.getNickname().isEmpty() ? complainant.getNickname() : complainant.getRealName()) : "未知";
                    String defendantName = defendant != null ? (defendant.getNickname() != null && !defendant.getNickname().isEmpty() ? defendant.getNickname() : defendant.getRealName()) : "未知";
                    
                    ComplaintDTO dto = ComplaintDTO.fromEntity(complaint, complainantName, defendantName);
                    if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
                        String content = complaint.getContent();
                        dto.setTitle(content != null && content.length() > 20 ? content.substring(0, 20) + "..." : (content != null ? content : "无标题"));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        
        PageResult<ComplaintDTO> dtoResult = new PageResult<>();
        dtoResult.setCurrent(result.getCurrent());
        dtoResult.setSize(result.getSize());
        dtoResult.setTotal(result.getTotal());
        dtoResult.setRecords(dtoList);
        
        return Result.success(dtoResult);
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
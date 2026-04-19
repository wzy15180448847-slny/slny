package com.houserental.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Data
public class ComplaintDTO {

    private Long id;

    private String title;

    private String complainant;

    private String defendant;

    private String type;

    private String content;

    private List<String> images;

    private Integer status;

    private String result;

    private String handleRemark;

    private LocalDateTime createTime;

    private LocalDateTime handleTime;

    private static final String[] TYPE_MAP = {
            "",
            "LANDLORD_BAD",
            "TENANT_BAD",
            "FALSE_INFO",
            "ILLEGAL_CHARGE"
    };

    public static ComplaintDTO fromEntity(com.houserental.entity.Complaint complaint, 
                                         String complainantName, 
                                         String defendantName) {
        ComplaintDTO dto = new ComplaintDTO();
        dto.setId(complaint.getId());
        dto.setTitle(complaint.getTitle());
        dto.setComplainant(complainantName);
        dto.setDefendant(defendantName);
        
        int type = complaint.getComplaintType() != null ? complaint.getComplaintType() : 1;
        dto.setType(type >= 1 && type < TYPE_MAP.length ? TYPE_MAP[type] : TYPE_MAP[1]);
        
        dto.setContent(complaint.getContent());
        
        String evidence = complaint.getEvidence();
        if (evidence != null && !evidence.isEmpty()) {
            try {
                dto.setImages(Arrays.asList(evidence.split(",")));
            } catch (Exception e) {
                dto.setImages(List.of());
            }
        } else {
            dto.setImages(List.of());
        }
        
        dto.setStatus(complaint.getStatus());
        
        String processResult = complaint.getProcessResult();
        if ("SUPPORT".equals(processResult)) {
            dto.setResult("SUPPORT");
        } else if ("REJECT".equals(processResult)) {
            dto.setResult("REJECT");
        } else {
            dto.setResult(null);
        }
        
        dto.setHandleRemark(complaint.getProcessResult());
        dto.setCreateTime(complaint.getCreateTime());
        dto.setHandleTime(complaint.getProcessTime());
        
        return dto;
    }
}
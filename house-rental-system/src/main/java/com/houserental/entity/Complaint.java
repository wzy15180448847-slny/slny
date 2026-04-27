package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 用户投诉实体
 */
@TableName("biz_complaint")
public class Complaint extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 投诉人ID
     */
    @TableField(value = "from_user_id", exist = true)
    private Long complainantId;

    /**
     * 被投诉人ID
     */
    @TableField(value = "target_id", exist = true)
    private Long respondentId;

    /**
     * 投诉类型（1-房源问题，2-服务问题，3-合同问题，4-其他）
     */
    @TableField(value = "type", exist = true)
    private Integer complaintType;

    /**
     * 投诉标题
     */
    @TableField(value = "title", exist = false)
    private String title;

    /**
     * 投诉内容
     */
    @TableField(value = "content", exist = true)
    private String content;

    /**
     * 投诉证据（JSON格式存储，多张图片URL）
     */
    @TableField(value = "images", exist = true)
    private String evidence;

    /**
     * 处理状态（0-待处理，1-处理中，2-已处理，3-已驳回）
     */
    @TableField(value = "status", exist = true)
    private Integer status = 0;

    /**
     * 处理结果
     */
    @TableField(value = "handle_result", exist = true)
    private String processResult;

    /**
     * 处理人ID
     */
    @TableField(value = "processor_id", exist = false)
    private Long processorId;

    /**
     * 处理时间
     */
    @TableField(value = "process_time", exist = false)
    private LocalDateTime processTime;

    // Getters and Setters
    public Long getComplainantId() {
        return complainantId;
    }

    public void setComplainantId(Long complainantId) {
        this.complainantId = complainantId;
    }

    public Long getRespondentId() {
        return respondentId;
    }

    public void setRespondentId(Long respondentId) {
        this.respondentId = respondentId;
    }

    public Integer getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(Integer complaintType) {
        this.complaintType = complaintType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getProcessResult() {
        return processResult;
    }

    public void setProcessResult(String processResult) {
        this.processResult = processResult;
    }

    public Long getProcessorId() {
        return processorId;
    }

    public void setProcessorId(Long processorId) {
        this.processorId = processorId;
    }

    public LocalDateTime getProcessTime() {
        return processTime;
    }

    public void setProcessTime(LocalDateTime processTime) {
        this.processTime = processTime;
    }
}
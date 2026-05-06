package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 解约申请实体类
 */
@TableName("biz_termination_application")
public class TerminationApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    @TableField(value = "is_deleted")
    private Integer isDeleted = 0;

    @TableField(value = "agreement_id")
    private Long leaseId;

    @TableField(value = "applicant_id")
    private Long applicantId;

    @TableField(value = "applicant_type")
    private String applicantType;

    @TableField(value = "reason")
    private String reason;

    @TableField(value = "compensation")
    private BigDecimal compensation;

    private Integer status;

    @TableField(value = "processor_id")
    private Long processorId;

    @TableField(value = "processing_opinion")
    private String processingOpinion;

    @TableField(exist = false)
    private LeaseAgreement lease;

    @TableField(exist = false)
    private User applicant;

    @TableField(exist = false)
    private User processor;

    @TableField(exist = false)
    private String applicationNo;

    @TableField(exist = false)
    private String terminationReason;

    @TableField(exist = false)
    private java.util.Date applyTime;

    @TableField(exist = false)
    private java.util.Date processingTime;

    @TableField(exist = false)
    private BigDecimal penaltyAmount;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getLeaseId() {
        return leaseId;
    }

    public void setLeaseId(Long leaseId) {
        this.leaseId = leaseId;
    }

    public Long getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BigDecimal getCompensation() {
        return compensation;
    }

    public void setCompensation(BigDecimal compensation) {
        this.compensation = compensation;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LeaseAgreement getLease() {
        return lease;
    }

    public void setLease(LeaseAgreement lease) {
        this.lease = lease;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public User getProcessor() {
        return processor;
    }

    public void setProcessor(User processor) {
        this.processor = processor;
    }

    public String getApplicationNo() {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }

    public String getApplicantType() {
        return applicantType;
    }

    public void setApplicantType(String applicantType) {
        this.applicantType = applicantType;
    }

    public String getTerminationReason() {
        return reason;
    }

    public void setTerminationReason(String terminationReason) {
        this.reason = terminationReason;
    }

    public java.util.Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(java.util.Date applyTime) {
        this.applyTime = applyTime;
    }

    public java.util.Date getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(java.util.Date processingTime) {
        this.processingTime = processingTime;
    }

    public Long getProcessorId() {
        return processorId;
    }

    public void setProcessorId(Long processorId) {
        this.processorId = processorId;
    }

    public String getProcessingOpinion() {
        return processingOpinion;
    }

    public void setProcessingOpinion(String processingOpinion) {
        this.processingOpinion = processingOpinion;
    }

    public BigDecimal getPenaltyAmount() {
        return compensation;
    }

    public void setPenaltyAmount(BigDecimal penaltyAmount) {
        this.compensation = penaltyAmount;
    }
}

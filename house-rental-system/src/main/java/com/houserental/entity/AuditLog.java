package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 审核日志实体
 */
@TableName("biz_audit_log")
public class AuditLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 房源ID
     */
    @TableField(value = "house_id", exist = true)
    private Long houseId;

    /**
     * 审核人ID
     */
    @TableField(value = "auditor_id", exist = true)
    private Long auditorId;

    /**
     * 审核人姓名
     */
    @TableField(value = "auditor_name", exist = true)
    private String auditorName;

    /**
     * 审核前状态
     */
    @TableField(value = "before_status", exist = true)
    private Integer beforeStatus;

    /**
     * 审核后状态
     */
    @TableField(value = "after_status", exist = true)
    private Integer afterStatus;

    /**
     * 审核前审核状态
     */
    @TableField(value = "before_audit_status", exist = true)
    private Integer beforeAuditStatus;

    /**
     * 审核后审核状态
     */
    @TableField(value = "after_audit_status", exist = true)
    private Integer afterAuditStatus;

    /**
     * 审核意见
     */
    @TableField(value = "audit_remark", exist = true)
    private String auditRemark;

    /**
     * 审核结果（1-通过，2-拒绝）
     */
    @TableField(value = "audit_result", exist = true)
    private Integer auditResult;

    // Getters and Setters
    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public Long getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Long auditorId) {
        this.auditorId = auditorId;
    }

    public String getAuditorName() {
        return auditorName;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    public Integer getBeforeStatus() {
        return beforeStatus;
    }

    public void setBeforeStatus(Integer beforeStatus) {
        this.beforeStatus = beforeStatus;
    }

    public Integer getAfterStatus() {
        return afterStatus;
    }

    public void setAfterStatus(Integer afterStatus) {
        this.afterStatus = afterStatus;
    }

    public Integer getBeforeAuditStatus() {
        return beforeAuditStatus;
    }

    public void setBeforeAuditStatus(Integer beforeAuditStatus) {
        this.beforeAuditStatus = beforeAuditStatus;
    }

    public Integer getAfterAuditStatus() {
        return afterAuditStatus;
    }

    public void setAfterAuditStatus(Integer afterAuditStatus) {
        this.afterAuditStatus = afterAuditStatus;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public Integer getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(Integer auditResult) {
        this.auditResult = auditResult;
    }
}

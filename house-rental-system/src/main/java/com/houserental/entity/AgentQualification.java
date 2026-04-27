package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 中介资质审核实体
 */
@TableName("biz_agent_qualification")
public class AgentQualification extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField(value = "user_id", exist = true)
    private Long userId;

    /**
     * 真实姓名
     */
    @TableField(value = "real_name", exist = true)
    private String realName;

    /**
     * 身份证号
     */
    @TableField(value = "id_card", exist = true)
    private String idCard;

    /**
     * 身份证正面图片URL
     */
    @TableField(value = "id_card_front", exist = true)
    private String idCardFront;

    /**
     * 身份证背面图片URL
     */
    @TableField(value = "id_card_back", exist = true)
    private String idCardBack;

    /**
     * 营业执照图片URL
     */
    @TableField(value = "business_license", exist = true)
    private String businessLicense;

    /**
     * 审核状态（0-待审核，1-审核通过，2-审核拒绝）
     */
    @TableField(value = "status", exist = true)
    private Integer status = 0;

    /**
     * 拒绝原因
     */
    @TableField(value = "reject_reason", exist = true)
    private String rejectReason;

    /**
     * 审核人ID
     */
    @TableField(value = "auditor_id", exist = true)
    private Long auditorId;

    /**
     * 审核时间
     */
    @TableField(value = "audit_time", exist = true)
    private LocalDateTime auditTime;

    /**
     * 手机号
     */
    @TableField(value = "phone", exist = true)
    private String phone;

    /**
     * 房源数量
     */
    @TableField(value = "house_count", exist = true)
    private Integer houseCount;

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIdCardFront() {
        return idCardFront;
    }

    public void setIdCardFront(String idCardFront) {
        this.idCardFront = idCardFront;
    }

    public String getIdCardBack() {
        return idCardBack;
    }

    public void setIdCardBack(String idCardBack) {
        this.idCardBack = idCardBack;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Long getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(Long auditorId) {
        this.auditorId = auditorId;
    }

    public LocalDateTime getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getHouseCount() {
        return houseCount;
    }

    public void setHouseCount(Integer houseCount) {
        this.houseCount = houseCount;
    }
}
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
     * 中介用户ID
     */
    @TableField(value = "agent_id", exist = true)
    private Long agentId;

    /**
     * 中介公司名称
     */
    @TableField(value = "company_name", exist = true)
    private String companyName;

    /**
     * 营业执照号
     */
    @TableField(value = "business_license", exist = true)
    private String businessLicense;

    /**
     * 营业执照图片URL
     */
    @TableField(value = "license_image", exist = true)
    private String licenseImage;

    /**
     * 中介资质证书号
     */
    @TableField(value = "qualification_cert", exist = true)
    private String qualificationCert;

    /**
     * 资质证书图片URL
     */
    @TableField(value = "cert_image", exist = true)
    private String certImage;

    /**
     * 联系人姓名
     */
    @TableField(value = "contact_name", exist = true)
    private String contactName;

    /**
     * 联系人电话
     */
    @TableField(value = "contact_phone", exist = true)
    private String contactPhone;

    /**
     * 审核状态（0-待审核，1-审核通过，2-审核拒绝）
     */
    @TableField(value = "audit_status", exist = true)
    private Integer auditStatus = 0;

    /**
     * 审核意见
     */
    @TableField(value = "audit_remark", exist = true)
    private String auditRemark;

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

    // Getters and Setters
    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getLicenseImage() {
        return licenseImage;
    }

    public void setLicenseImage(String licenseImage) {
        this.licenseImage = licenseImage;
    }

    public String getQualificationCert() {
        return qualificationCert;
    }

    public void setQualificationCert(String qualificationCert) {
        this.qualificationCert = qualificationCert;
    }

    public String getCertImage() {
        return certImage;
    }

    public void setCertImage(String certImage) {
        this.certImage = certImage;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
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
}
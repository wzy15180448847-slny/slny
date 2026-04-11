package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 解约申请实体类
 */
@Data
@TableName("biz_termination_application")
public class TerminationApplication extends BaseEntity {

    /**
     * 申请编号
     */
    private String applicationNo;

    /**
     * 租约ID
     */
    private Long leaseId;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 申请人类型（LANDLORD-房东, TENANT-租客）
     */
    private String applicantType;

    /**
     * 解约原因
     */
    private String terminationReason;

    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 状态（0-待处理，1-已同意，2-已拒绝，3-已完成）
     */
    private Integer status;

    /**
     * 处理时间
     */
    private Date processingTime;

    /**
     * 处理人ID
     */
    private Long processorId;

    /**
     * 处理意见
     */
    private String processingOpinion;

    /**
     * 违约金金额
     */
    private BigDecimal penaltyAmount;

    /**
     * 租约信息
     */
    @TableField(exist = false)
    private LeaseAgreement lease;

    /**
     * 申请人信息
     */
    @TableField(exist = false)
    private User applicant;

    /**
     * 处理人信息
     */
    @TableField(exist = false)
    private User processor;
}
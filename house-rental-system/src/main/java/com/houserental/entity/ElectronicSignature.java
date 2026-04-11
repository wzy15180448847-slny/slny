package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 电子签章实体类
 */
@Data
@TableName("biz_electronic_signature")
public class ElectronicSignature extends BaseEntity {

    /**
     * 签章编号
     */
    private String signatureNo;

    /**
     * 租约ID
     */
    private Long leaseId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户类型（LANDLORD-房东, TENANT-租客）
     */
    private String userType;

    /**
     * 签章数据
     */
    private String signatureData;

    /**
     * 签章时间
     */
    private Date signingTime;

    /**
     * 状态（0-待签章，1-已签章）
     */
    private Integer status;

    /**
     * 用户信息
     */
    @TableField(exist = false)
    private User user;

    /**
     * 租约信息
     */
    @TableField(exist = false)
    private LeaseAgreement lease;
}
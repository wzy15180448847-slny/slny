package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 租约实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_lease_agreement")
public class LeaseAgreement extends BaseEntity {

    /**
     * 租约编号
     */
    private String leaseNo;

    /**
     * 房源ID
     */
    private Long houseId;

    /**
     * 租客ID
     */
    private Long tenantId;

    /**
     * 房东ID
     */
    private Long landlordId;

    /**
     * 起租日期
     */
    private Date startDate;

    /**
     * 到期日期
     */
    private Date endDate;

    /**
     * 租金（元/月）
     */
    private BigDecimal rentPrice;

    /**
     * 押金（元）
     */
    private BigDecimal deposit;

    /**
     * 付款方式（1-月付，2-季付，3-半年付，4-年付）
     */
    private Integer paymentWay;

    /**
     * 状态（0-待签署，1-已签署，2-已生效，3-已到期，4-已解约，5-已终止）
     */
    private Integer status;

    /**
     * 签署日期
     */
    private Date signingDate;

    /**
     * 生效日期
     */
    private Date effectiveDate;

    /**
     * 终止日期
     */
    private Date terminationDate;

    /**
     * 违约金规则
     */
    private String penaltyRule;

    /**
     * 合同内容
     */
    private String agreementContent;

    /**
     * 合同文件URL
     */
    private String contractUrl;

    /**
     * 房源信息
     */
    @TableField(exist = false)
    private House house;

    /**
     * 租客信息
     */
    @TableField(exist = false)
    private User tenant;

    /**
     * 房东信息
     */
    @TableField(exist = false)
    private User landlord;

    /**
     * 电子签章列表
     */
    @TableField(exist = false)
    private java.util.List<ElectronicSignature> signatures;
}
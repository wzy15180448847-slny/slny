package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_payment_record")
public class PaymentRecord extends BaseEntity {

    /**
     * 支付编号
     */
    private String paymentNo;

    /**
     * 租约ID
     */
    private Long leaseAgreementId;

    /**
     * 租客ID
     */
    private Long tenantId;

    /**
     * 房东ID
     */
    private Long landlordId;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付类型（1-租金，2-押金，3-违约金）
     */
    private Integer paymentType;

    /**
     * 支付方式（1-微信支付，2-支付宝）
     */
    private Integer paymentMethod;

    /**
     * 支付状态（0-待支付，1-支付成功，2-支付失败，3-退款中，4-已退款）
     */
    private Integer status;

    /**
     * 交易号
     */
    private String tradeNo;

    /**
     * 第三方支付平台订单号
     */
    private String platformOrderNo;

    /**
     * 支付时间
     */
    private Date paymentTime;

    /**
     * 退款时间
     */
    private Date refundTime;

    /**
     * 支付备注
     */
    private String remark;

    /**
     * 租约信息
     */
    @TableField(exist = false)
    private LeaseAgreement leaseAgreement;

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
}

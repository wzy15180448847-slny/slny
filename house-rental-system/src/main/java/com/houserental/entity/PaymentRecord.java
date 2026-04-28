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
    @TableField(value = "order_no", exist = true)
    private String paymentNo;

    /**
     * 用户ID
     */
    @TableField(value = "user_id", exist = true)
    private Long tenantId;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付类型（1-租金，2-押金，3-违约金）
     */
    @TableField(value = "pay_type", exist = true)
    private Integer paymentType;

    /**
     * 支付状态（0-待支付，1-支付成功，2-支付失败，3-退款中，4-已退款）
     */
    private Integer status;

    /**
     * 第三方支付平台订单号
     */
    @TableField(value = "third_party_no", exist = true)
    private String platformOrderNo;

    /**
     * 支付方式（1-微信，2-支付宝，3-钱包）
     */
    @TableField(value = "pay_method", exist = true)
    private Integer paymentMethod;

    /**
     * 租约ID
     */
    @TableField(value = "lease_agreement_id", exist = true)
    private Long leaseAgreementId;

    /**
     * 退款时间
     */
    @TableField(value = "refund_time", exist = true)
    private Date refundTime;

    /**
     * 备注
     */
    @TableField(value = "remark", exist = true)
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

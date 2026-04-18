package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 违约金规则实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_penalty_rule")
public class PenaltyRule extends BaseEntity {

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则类型（EARLY_TERMINATION-提前解约，LATE_PAYMENT-逾期付款，DAMAGE-损坏赔偿）
     */
    private String ruleType;

    /**
     * 计算方式
     */
    private String calculationMethod;

    /**
     * 基础金额
     */
    private BigDecimal baseAmount;

    /**
     * 百分比
     */
    private BigDecimal percentage;

    /**
     * 最低金额
     */
    private BigDecimal minAmount;

    /**
     * 最高金额
     */
    private BigDecimal maxAmount;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
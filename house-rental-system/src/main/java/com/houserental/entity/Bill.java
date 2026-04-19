package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 账单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_bill")
public class Bill extends BaseEntity {

    /**
     * 租约ID
     */
    private Long leaseId;

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
     * 金额
     */
    private BigDecimal amount;

    /**
     * 状态（1-待支付，2-已支付，3-已取消）
     */
    private Integer status;

    /**
     * 账单类型（1-租金，2-押金，3-违约金）
     */
    private Integer billType;
}

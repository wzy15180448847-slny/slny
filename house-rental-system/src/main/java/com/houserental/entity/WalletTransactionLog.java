package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wallet_transaction_log")
public class WalletTransactionLog extends BaseEntity {

    private Long userId;

    private Integer transactionType;

    private BigDecimal amount;

    private BigDecimal balanceBefore;

    private BigDecimal balanceAfter;

    private String orderNo;

    private Long relatedUserId;

    private String remark;
}
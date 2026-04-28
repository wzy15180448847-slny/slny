package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("biz_wallet_transaction")
public class WalletTransactionLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private Long userId;

    @TableField("type")
    private Integer transactionType;

    private BigDecimal amount;

    @TableField(exist = false)
    private BigDecimal balanceBefore;

    @TableField(exist = false)
    private BigDecimal balanceAfter;

    @TableField("target_id")
    private String orderNo;

    @TableField(exist = false)
    private Long relatedUserId;

    @TableField("description")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
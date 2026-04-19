package com.houserental.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("biz_user_wallet")
public class UserWallet extends BaseEntity {

    private Long userId;

    private BigDecimal balance;

    private BigDecimal freezeBalance;

    @TableField("version")
    private Integer version;
}
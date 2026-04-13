package com.houserental.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class WalletPayRequest {

    @NotNull(message = "租客ID不能为空")
    private Long tenantId;

    @NotNull(message = "房东ID不能为空")
    private Long landlordId;

    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于0")
    private BigDecimal amount;

    @NotNull(message = "订单号不能为空")
    private String orderNo;
}
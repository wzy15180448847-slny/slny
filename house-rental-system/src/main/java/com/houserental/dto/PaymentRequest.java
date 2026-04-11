package com.houserental.dto;

import lombok.Data;

@Data
public class PaymentRequest {

    private Long leaseId;
    private Long userId;
    private Double amount;
    private String paymentMethod;
    private String orderNo;
    private String description;
    private String notifyUrl;
    private String returnUrl;
}

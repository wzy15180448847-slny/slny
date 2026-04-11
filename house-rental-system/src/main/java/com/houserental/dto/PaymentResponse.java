package com.houserental.dto;

import lombok.Data;

@Data
public class PaymentResponse {

    private String paymentId;
    private String orderNo;
    private String paymentUrl;
    private String qrCode;
    private String status;
    private String message;
}

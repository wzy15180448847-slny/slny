package com.houserental.service;

import com.houserental.entity.PaymentRecord;
import com.houserental.dto.PaymentRequest;
import com.houserental.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {

    /**
     * 创建支付订单
     * @param paymentRequest 支付请求
     * @return 支付响应
     */
    PaymentResponse createPayment(PaymentRequest paymentRequest);

    /**
     * 查询支付记录
     * @param paymentId 支付ID
     * @return 支付记录
     */
    PaymentRecord getPaymentById(Long paymentId);

    /**
     * 获取用户的支付记录
     * @param userId 用户ID
     * @return 支付记录列表
     */
    List<PaymentRecord> getPaymentsByUserId(Long userId);

    /**
     * 申请退款
     * @param paymentId 支付ID
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @return 是否成功
     */
    boolean applyRefund(Long paymentId, Double refundAmount, String refundReason);

    /**
     * 查询退款状态
     * @param paymentId 支付ID
     * @return 退款状态
     */
    String getRefundStatus(Long paymentId);
}
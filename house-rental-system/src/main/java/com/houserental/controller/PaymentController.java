package com.houserental.controller;

import com.houserental.common.result.Result;
import com.houserental.service.PaymentService;
import com.houserental.dto.PaymentRequest;
import com.houserental.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 创建支付订单
     */
    @PostMapping
    public Result<PaymentResponse> createPayment(@RequestBody PaymentRequest paymentRequest) {
        return Result.success(paymentService.createPayment(paymentRequest));
    }

    /**
     * 处理微信支付回调
     */
    @PostMapping("/wechat/callback")
    public String wechatCallback(@RequestBody String xmlData) {
        return paymentService.handleWechatCallback(xmlData);
    }

    /**
     * 处理支付宝支付回调
     */
    @PostMapping("/alipay/callback")
    public String alipayCallback(@RequestBody String notifyData) {
        return paymentService.handleAlipayCallback(notifyData);
    }

    /**
     * 查询支付记录
     */
    @GetMapping("/record/{id}")
    public Result<Object> getPaymentRecord(@PathVariable Long id) {
        return Result.success(paymentService.getPaymentById(id));
    }

    /**
     * 获取用户支付记录
     */
    @GetMapping("/user/{userId}")
    public Result<Object> getUserPayments(@PathVariable Long userId) {
        return Result.success(paymentService.getPaymentsByUserId(userId));
    }

    /**
     * 申请退款
     */
    @PostMapping("/refund/{id}")
    public Result<Boolean> applyRefund(@PathVariable Long id, @RequestParam Double amount, @RequestParam String reason) {
        return Result.success(paymentService.applyRefund(id, amount, reason));
    }

    /**
     * 查询退款状态
     */
    @GetMapping("/refund/{id}/status")
    public Result<String> getRefundStatus(@PathVariable Long id) {
        return Result.success(paymentService.getRefundStatus(id));
    }
}

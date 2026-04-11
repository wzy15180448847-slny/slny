package com.houserental.service.impl;

import com.houserental.entity.PaymentRecord;
import com.houserental.mapper.PaymentRecordMapper;
import com.houserental.service.PaymentService;
import com.houserental.dto.PaymentRequest;
import com.houserental.dto.PaymentResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private PaymentRecordMapper paymentRecordMapper;

    @Override
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        // 生成支付订单
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPaymentNo(paymentRequest.getOrderNo());
        paymentRecord.setTenantId(paymentRequest.getUserId());
        paymentRecord.setLeaseAgreementId(paymentRequest.getLeaseId());
        paymentRecord.setAmount(java.math.BigDecimal.valueOf(paymentRequest.getAmount()));
        paymentRecord.setPaymentMethod("wechat".equals(paymentRequest.getPaymentMethod()) ? 1 : 2);
        paymentRecord.setStatus(0); // 待支付

        // 保存支付记录
        paymentRecordMapper.insert(paymentRecord);

        // 生成支付响应
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(paymentRecord.getId().toString());
        response.setOrderNo(paymentRequest.getOrderNo());
        response.setStatus("CREATED");

        // 根据支付方式生成支付链接
        if ("wechat".equals(paymentRequest.getPaymentMethod())) {
            response.setPaymentUrl(generateWechatPaymentUrl(paymentRecord));
        } else if ("alipay".equals(paymentRequest.getPaymentMethod())) {
            response.setPaymentUrl(generateAlipayPaymentUrl(paymentRecord));
        }

        return response;
    }

    @Override
    public String handleWechatCallback(String notifyData) {
        // 处理微信支付回调
        // 1. 验证签名
        // 2. 解析回调数据
        // 3. 更新支付状态
        // 4. 返回成功响应
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }

    @Override
    public String handleAlipayCallback(String notifyData) {
        // 处理支付宝回调
        // 1. 验证签名
        // 2. 解析回调数据
        // 3. 更新支付状态
        // 4. 返回成功响应
        return "success";
    }

    @Override
    public PaymentRecord getPaymentById(Long paymentId) {
        return paymentRecordMapper.selectById(paymentId);
    }

    @Override
    public List<PaymentRecord> getPaymentsByUserId(Long userId) {
        return paymentRecordMapper.selectByUserId(userId);
    }

    @Override
    public boolean applyRefund(Long paymentId, Double refundAmount, String refundReason) {
        PaymentRecord paymentRecord = paymentRecordMapper.selectById(paymentId);
        if (paymentRecord == null) {
            return false;
        }

        // 检查支付状态
        if (paymentRecord.getStatus() != 1) { // 1表示已支付
            return false;
        }

        // 记录退款信息
        // TODO: 调用支付平台的退款接口

        return true;
    }

    @Override
    public String getRefundStatus(Long paymentId) {
        // TODO: 查询支付平台的退款状态
        return "PENDING";
    }

    private String generateWechatPaymentUrl(PaymentRecord paymentRecord) {
        // 生成微信支付链接
        return "https://wx.tenpay.com/pay?orderId=" + paymentRecord.getPaymentNo();
    }

    private String generateAlipayPaymentUrl(PaymentRecord paymentRecord) {
        // 生成支付宝支付链接
        return "https://openapi.alipay.com/gateway.do?orderId=" + paymentRecord.getPaymentNo();
    }
}

package com.houserental.service.impl;

import com.houserental.entity.PaymentRecord;
import com.houserental.mapper.PaymentRecordMapper;
import com.houserental.service.PaymentService;
import com.houserental.dto.PaymentRequest;
import com.houserental.dto.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 支付服务实现
 * 注：第三方支付（微信、支付宝）已移除，当前仅支持钱包支付
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Resource
    private PaymentRecordMapper paymentRecordMapper;

    @Override
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        log.info("开始创建支付订单, orderNo={}, userId={}, leaseId={}", 
                paymentRequest.getOrderNo(), paymentRequest.getUserId(), paymentRequest.getLeaseId());
        try {
            PaymentRecord paymentRecord = new PaymentRecord();
            paymentRecord.setPaymentNo(paymentRequest.getOrderNo());
            paymentRecord.setTenantId(paymentRequest.getUserId());
            paymentRecord.setLeaseAgreementId(paymentRequest.getLeaseId());
            paymentRecord.setAmount(new BigDecimal(String.valueOf(paymentRequest.getAmount())));
            
            // 默认使用钱包支付
            paymentRecord.setPaymentMethod(3);
            paymentRecord.setStatus(0);

            paymentRecordMapper.insert(paymentRecord);
            log.info("支付订单创建成功, paymentId={}", paymentRecord.getId());

            PaymentResponse response = new PaymentResponse();
            response.setPaymentId(paymentRecord.getId().toString());
            response.setOrderNo(paymentRequest.getOrderNo());
            response.setStatus("CREATED");

            return response;
        } catch (Exception e) {
            log.error("创建支付订单失败, orderNo={}, error={}", paymentRequest.getOrderNo(), e.getMessage(), e);
            throw new RuntimeException("创建支付订单失败", e);
        }
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
        log.info("申请退款, paymentId={}, refundAmount={}, refundReason={}", paymentId, refundAmount, refundReason);
        try {
            PaymentRecord paymentRecord = paymentRecordMapper.selectById(paymentId);
            if (paymentRecord == null) {
                log.warn("未找到支付记录, paymentId={}", paymentId);
                return false;
            }

            if (paymentRecord.getStatus() != 1) {
                log.warn("支付状态不允许退款, paymentId={}, status={}", paymentId, paymentRecord.getStatus());
                return false;
            }

            paymentRecord.setStatus(4);
            paymentRecord.setRefundTime(new Date());
            paymentRecord.setRemark(refundReason);
            
            int updateCount = paymentRecordMapper.updateById(paymentRecord);
            if (updateCount > 0) {
                log.info("退款申请成功, paymentId={}", paymentId);
                return true;
            } else {
                log.error("退款更新失败, paymentId={}", paymentId);
                return false;
            }
        } catch (Exception e) {
            log.error("申请退款失败, paymentId={}, error={}", paymentId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getRefundStatus(Long paymentId) {
        return "PENDING";
    }
}
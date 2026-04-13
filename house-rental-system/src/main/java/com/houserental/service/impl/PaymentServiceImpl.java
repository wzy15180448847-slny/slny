package com.houserental.service.impl;

import com.houserental.entity.PaymentRecord;
import com.houserental.entity.LeaseAgreement;
import com.houserental.mapper.PaymentRecordMapper;
import com.houserental.mapper.LeaseAgreementMapper;
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
import java.util.Map;
import java.util.HashMap;


@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Resource
    private PaymentRecordMapper paymentRecordMapper;

    @Resource
    private LeaseAgreementMapper leaseAgreementMapper;

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
            paymentRecord.setPaymentMethod("wechat".equals(paymentRequest.getPaymentMethod()) ? 1 : 2);
            paymentRecord.setStatus(0);

            paymentRecordMapper.insert(paymentRecord);
            log.info("支付订单创建成功, paymentId={}", paymentRecord.getId());

            PaymentResponse response = new PaymentResponse();
            response.setPaymentId(paymentRecord.getId().toString());
            response.setOrderNo(paymentRequest.getOrderNo());
            response.setStatus("CREATED");

            if ("wechat".equals(paymentRequest.getPaymentMethod())) {
                response.setPaymentUrl(generateWechatPaymentUrl(paymentRecord));
            } else if ("alipay".equals(paymentRequest.getPaymentMethod())) {
                response.setPaymentUrl(generateAlipayPaymentUrl(paymentRecord));
            }

            return response;
        } catch (Exception e) {
            log.error("创建支付订单失败, orderNo={}, error={}", paymentRequest.getOrderNo(), e.getMessage(), e);
            throw new RuntimeException("创建支付订单失败", e);
        }
    }

    @Override
    public String handleWechatCallback(String notifyData) {
        log.info("收到微信支付回调, notifyData={}", notifyData);
        try {
            Map<String, String> params = parseWechatNotifyData(notifyData);
            String orderNo = params.get("out_trade_no");
            String tradeNo = params.get("transaction_id");
            String resultCode = params.get("result_code");

            if (!"SUCCESS".equals(resultCode)) {
                log.warn("微信支付失败, orderNo={}, resultCode={}", orderNo, resultCode);
                return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[支付失败]]></return_msg></xml>";
            }

            PaymentRecord paymentRecord = paymentRecordMapper.selectByOrderNo(orderNo);
            if (paymentRecord == null) {
                log.error("未找到支付记录, orderNo={}", orderNo);
                return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[订单不存在]]></return_msg></xml>";
            }

            if (paymentRecord.getStatus() == 1) {
                log.warn("支付记录已处理, orderNo={}", orderNo);
                return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            }

            paymentRecord.setStatus(1);
            paymentRecord.setTradeNo(tradeNo);
            paymentRecord.setPaymentTime(new Date());
            paymentRecordMapper.updateById(paymentRecord);
            log.info("支付记录更新成功, paymentId={}, orderNo={}", paymentRecord.getId(), orderNo);

            updateLeaseAgreementStatus(paymentRecord.getLeaseAgreementId());

            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        } catch (Exception e) {
            log.error("处理微信支付回调失败, notifyData={}, error={}", notifyData, e.getMessage(), e);
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
        }
    }

    private Map<String, String> parseWechatNotifyData(String notifyData) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = notifyData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    @Override
    public String handleAlipayCallback(String notifyData) {
        log.info("收到支付宝支付回调, notifyData={}", notifyData);
        try {
            Map<String, String> params = parseAlipayNotifyData(notifyData);
            String orderNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            String tradeStatus = params.get("trade_status");

            if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
                log.warn("支付宝支付失败, orderNo={}, tradeStatus={}", orderNo, tradeStatus);
                return "fail";
            }

            PaymentRecord paymentRecord = paymentRecordMapper.selectByOrderNo(orderNo);
            if (paymentRecord == null) {
                log.error("未找到支付记录, orderNo={}", orderNo);
                return "fail";
            }

            if (paymentRecord.getStatus() == 1) {
                log.warn("支付记录已处理, orderNo={}", orderNo);
                return "success";
            }

            paymentRecord.setStatus(1);
            paymentRecord.setTradeNo(tradeNo);
            paymentRecord.setPaymentTime(new Date());
            paymentRecordMapper.updateById(paymentRecord);
            log.info("支付记录更新成功, paymentId={}, orderNo={}", paymentRecord.getId(), orderNo);

            updateLeaseAgreementStatus(paymentRecord.getLeaseAgreementId());

            return "success";
        } catch (Exception e) {
            log.error("处理支付宝支付回调失败, notifyData={}, error={}", notifyData, e.getMessage(), e);
            return "fail";
        }
    }

    private Map<String, String> parseAlipayNotifyData(String notifyData) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = notifyData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }

    private void updateLeaseAgreementStatus(Long leaseAgreementId) {
        if (leaseAgreementId == null) {
            log.warn("租赁合同ID为空");
            return;
        }
        LeaseAgreement leaseAgreement = leaseAgreementMapper.selectById(leaseAgreementId);
        if (leaseAgreement != null) {
            leaseAgreement.setStatus(2);
            leaseAgreement.setEffectiveDate(new Date());
            leaseAgreementMapper.updateById(leaseAgreement);
            log.info("租赁合同状态更新成功, leaseAgreementId={}", leaseAgreementId);
        } else {
            log.warn("未找到租赁合同, leaseAgreementId={}", leaseAgreementId);
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

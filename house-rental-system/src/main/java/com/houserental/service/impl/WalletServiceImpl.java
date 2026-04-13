package com.houserental.service.impl;

import com.houserental.common.exception.BusinessException;
import com.houserental.entity.PaymentRecord;
import com.houserental.entity.UserWallet;
import com.houserental.entity.WalletTransactionLog;
import com.houserental.mapper.PaymentRecordMapper;
import com.houserental.mapper.UserWalletMapper;
import com.houserental.mapper.WalletTransactionLogMapper;
import com.houserental.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletServiceImpl.class);

    private final UserWalletMapper userWalletMapper;
    private final WalletTransactionLogMapper transactionLogMapper;
    private final PaymentRecordMapper paymentRecordMapper;

    private static final int TRANSACTION_TYPE_RECHARGE = 1;
    private static final int TRANSACTION_TYPE_CONSUME = 2;
    private static final int TRANSACTION_TYPE_TRANSFER = 4;

    @Override
    public UserWallet getWallet(Long userId) {
        UserWallet wallet = userWalletMapper.selectByUserId(userId);
        if (wallet == null) {
            return createWallet(userId);
        }
        return wallet;
    }

    @Override
    public UserWallet createWallet(Long userId) {
        UserWallet wallet = new UserWallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setFreezeBalance(BigDecimal.ZERO);
        wallet.setVersion(1);
        userWalletMapper.insert(wallet);
        log.info("创建钱包成功, userId={}, walletId={}", userId, wallet.getId());
        return wallet;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserWallet recharge(Long userId, BigDecimal amount, String remark) {
        log.info("开始充值, userId={}, amount={}", userId, amount);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额必须大于0");
        }

        UserWallet wallet = getWallet(userId);
        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);

        int updateCount = userWalletMapper.increaseBalance(userId, amount);
        if (updateCount == 0) {
            throw new BusinessException("充值失败");
        }

        WalletTransactionLog logEntry = new WalletTransactionLog();
        logEntry.setUserId(userId);
        logEntry.setTransactionType(TRANSACTION_TYPE_RECHARGE);
        logEntry.setAmount(amount);
        logEntry.setBalanceBefore(balanceBefore);
        logEntry.setBalanceAfter(balanceAfter);
        logEntry.setRemark(remark != null ? remark : "充值");
        transactionLogMapper.insert(logEntry);

        wallet.setBalance(balanceAfter);
        log.info("充值成功, userId={}, amount={}, balance={}", userId, amount, balanceAfter);
        return wallet;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payRent(Long tenantId, Long landlordId, BigDecimal amount, String orderNo) {
        log.info("开始支付租金, tenantId={}, landlordId={}, amount={}, orderNo={}", 
                tenantId, landlordId, amount, orderNo);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("支付金额必须大于0");
        }

        PaymentRecord paymentRecord = paymentRecordMapper.selectByOrderNo(orderNo);
        if (paymentRecord == null) {
            throw new BusinessException("支付记录不存在");
        }

        if (paymentRecord.getStatus() != 0) {
            throw new BusinessException("订单状态不允许支付");
        }

        UserWallet tenantWallet = getWallet(tenantId);
        UserWallet landlordWallet = getWallet(landlordId);

        BigDecimal tenantBalanceBefore = tenantWallet.getBalance();
        if (tenantBalanceBefore.compareTo(amount) < 0) {
            throw new BusinessException("余额不足");
        }

        int deductCount = userWalletMapper.decreaseBalanceWithCheck(tenantId, amount);
        if (deductCount == 0) {
            throw new BusinessException("扣款失败，余额不足或并发冲突");
        }

        BigDecimal tenantBalanceAfter = tenantBalanceBefore.subtract(amount);

        WalletTransactionLog tenantLog = new WalletTransactionLog();
        tenantLog.setUserId(tenantId);
        tenantLog.setTransactionType(TRANSACTION_TYPE_CONSUME);
        tenantLog.setAmount(amount.negate());
        tenantLog.setBalanceBefore(tenantBalanceBefore);
        tenantLog.setBalanceAfter(tenantBalanceAfter);
        tenantLog.setOrderNo(orderNo);
        tenantLog.setRelatedUserId(landlordId);
        tenantLog.setRemark("支付租金");
        transactionLogMapper.insert(tenantLog);

        BigDecimal landlordBalanceBefore = landlordWallet.getBalance();
        BigDecimal landlordBalanceAfter = landlordBalanceBefore.add(amount);

        userWalletMapper.increaseBalance(landlordId, amount);

        WalletTransactionLog landlordLog = new WalletTransactionLog();
        landlordLog.setUserId(landlordId);
        landlordLog.setTransactionType(TRANSACTION_TYPE_TRANSFER);
        landlordLog.setAmount(amount);
        landlordLog.setBalanceBefore(landlordBalanceBefore);
        landlordLog.setBalanceAfter(landlordBalanceAfter);
        landlordLog.setOrderNo(orderNo);
        landlordLog.setRelatedUserId(tenantId);
        landlordLog.setRemark("收到租金");
        transactionLogMapper.insert(landlordLog);

        paymentRecord.setStatus(1);
        paymentRecordMapper.updateById(paymentRecord);

        log.info("支付租金成功, orderNo={}, tenantId={}, landlordId={}, amount={}", 
                orderNo, tenantId, landlordId, amount);
        return true;
    }

    @Override
    public List<WalletTransactionLog> getTransactionLogs(Long userId) {
        return transactionLogMapper.selectByUserId(userId);
    }

    @Override
    public List<WalletTransactionLog> getTransactionLogsByOrderNo(String orderNo) {
        return transactionLogMapper.selectByOrderNo(orderNo);
    }
}
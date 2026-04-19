package com.houserental.service;

import com.houserental.entity.UserWallet;
import com.houserental.entity.WalletTransactionLog;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {

    UserWallet getWallet(Long userId);

    UserWallet createWallet(Long userId);

    UserWallet recharge(Long userId, BigDecimal amount, String remark);

    boolean payRent(String orderNo);

    List<WalletTransactionLog> getTransactionLogs(Long userId);

    List<WalletTransactionLog> getTransactionLogsByOrderNo(String orderNo);
}
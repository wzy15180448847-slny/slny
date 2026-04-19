package com.houserental.controller;

import com.houserental.common.result.Result;
import com.houserental.dto.WalletPayRequest;
import com.houserental.dto.WalletRechargeRequest;
import com.houserental.entity.UserWallet;
import com.houserental.entity.WalletTransactionLog;
import com.houserental.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{userId}")
    public Result<UserWallet> getWallet(@PathVariable Long userId) {
        UserWallet wallet = walletService.getWallet(userId);
        return Result.success(wallet);
    }

    @PostMapping("/recharge")
    public Result<UserWallet> recharge(@Validated @RequestBody WalletRechargeRequest request) {
        UserWallet wallet = walletService.recharge(request.getUserId(), request.getAmount(), request.getRemark());
        return Result.success(wallet);
    }

    @PostMapping("/pay-rent")
    public Result<Boolean> payRent(@Validated @RequestBody WalletPayRequest request) {
        boolean success = walletService.payRent(request.getOrderNo());
        return Result.success(success);
    }

    @GetMapping("/{userId}/transactions")
    public Result<List<WalletTransactionLog>> getTransactionLogs(@PathVariable Long userId) {
        List<WalletTransactionLog> logs = walletService.getTransactionLogs(userId);
        return Result.success(logs);
    }

    @GetMapping("/transactions/{orderNo}")
    public Result<List<WalletTransactionLog>> getTransactionLogsByOrderNo(@PathVariable String orderNo) {
        List<WalletTransactionLog> logs = walletService.getTransactionLogsByOrderNo(orderNo);
        return Result.success(logs);
    }
}
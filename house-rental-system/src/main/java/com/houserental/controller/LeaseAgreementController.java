package com.houserental.controller;

import com.houserental.entity.LeaseAgreement;
import com.houserental.common.result.Result;
import com.houserental.service.LeaseAgreementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 租约控制器
 */
@RestController
@RequestMapping("/api/lease")
public class LeaseAgreementController {

    @Autowired
    private LeaseAgreementService leaseAgreementService;

    /**
     * 创建租约
     * @param lease 租约信息
     * @return 结果
     */
    @PostMapping
    public Result<Long> createLease(@RequestBody LeaseAgreement lease) {
        Long id = leaseAgreementService.createLease(lease);
        return Result.success(id);
    }

    /**
     * 签署租约
     * @param id 租约ID
     * @param userId 用户ID
     * @param userType 用户类型
     * @param signatureData 签章数据
     * @return 结果
     */
    @PutMapping("/sign/{id}")
    public Result<Void> signLease(@PathVariable Long id, @RequestParam Long userId, @RequestParam String userType, @RequestParam String signatureData) {
        boolean success = leaseAgreementService.signLease(id, userId, userType, signatureData);
        return success ? Result.success() : Result.error("签署租约失败");
    }

    /**
     * 生效租约
     * @param id 租约ID
     * @return 结果
     */
    @PutMapping("/effective/{id}")
    public Result<Void> effectiveLease(@PathVariable Long id) {
        boolean success = leaseAgreementService.effectiveLease(id);
        return success ? Result.success() : Result.error("生效租约失败");
    }

    /**
     * 终止租约
     * @param id 租约ID
     * @param reason 终止原因
     * @return 结果
     */
    @PutMapping("/terminate/{id}")
    public Result<Void> terminateLease(@PathVariable Long id, @RequestParam String reason) {
        boolean success = leaseAgreementService.terminateLease(id, reason);
        return success ? Result.success() : Result.error("终止租约失败");
    }

    /**
     * 分页查询租约
     * @param params 查询参数
     * @return 结果
     */
    @GetMapping("/page")
    public Result<Object> pageLeases(@RequestParam Map<String, Object> params) {
        return Result.success(leaseAgreementService.pageLeases(params));
    }

    /**
     * 查询租约详情
     * @param id 租约ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public Result<LeaseAgreement> getLeaseById(@PathVariable Long id) {
        return Result.success(leaseAgreementService.getLeaseById(id));
    }

    /**
     * 发送合同
     * @param id 租约ID
     * @return 结果
     */
    @PostMapping("/send/{id}")
    public Result<Void> sendContract(@PathVariable Long id) {
        boolean success = leaseAgreementService.sendContract(id);
        return success ? Result.success() : Result.error("发送合同失败");
    }

    /**
     * 导出合同
     * @param id 租约ID
     * @param response 响应对象
     */
    @GetMapping("/export/{id}")
    public void exportContract(@PathVariable Long id, HttpServletResponse response) throws IOException {
        leaseAgreementService.exportContract(id, response);
    }

    /**
     * 生成账单
     * @param id 租约ID
     * @return 结果
     */
    @PostMapping("/bill/{id}")
    public Result<Void> generateBill(@PathVariable Long id) {
        boolean success = leaseAgreementService.generateBill(id);
        return success ? Result.success() : Result.error("生成账单失败");
    }
}
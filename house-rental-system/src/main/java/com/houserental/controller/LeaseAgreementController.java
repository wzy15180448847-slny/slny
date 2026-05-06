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
@RequestMapping("/lease")
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
     * @param request 签署请求
     * @return 结果
     */
    @PutMapping("/sign/{id}")
    public Result<Void> signLease(@PathVariable Long id, @RequestBody SignRequest request) {
        // 从 SecurityContext 自动获取当前登录用户信息
        Long userId = com.houserental.common.utils.SecurityUtils.getCurrentUserId();
        System.out.println("签署合同 - 合同ID: " + id + ", 用户ID: " + userId);
        // userType 传 null，让 Service 根据合同自动判断
        boolean success = leaseAgreementService.signLease(id, userId, null, request.getSignatureData());
        return success ? Result.success() : Result.error("签署租约失败");
    }
    
    /**
     * 签署请求DTO
     */
    public static class SignRequest {
        private Long userId;
        private String userType;
        private String signatureData;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
        public String getSignatureData() { return signatureData; }
        public void setSignatureData(String signatureData) { this.signatureData = signatureData; }
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

    /**
     * 获取当前用户的合同列表（租客）
     * @param params 查询参数
     * @return 结果
     */
    @GetMapping("/my")
    public Result<Object> getMyContracts(@RequestParam Map<String, Object> params) {
        Long userId = com.houserental.common.utils.SecurityUtils.getCurrentUserId();
        params.put("tenantId", userId);
        return Result.success(leaseAgreementService.pageLeases(params));
    }
    
    /**
     * 获取房东的合同列表
     * @param params 查询参数
     * @return 结果
     */
    @GetMapping("/landlord")
    public Result<Object> getLandlordContracts(@RequestParam Map<String, Object> params) {
        Long userId = com.houserental.common.utils.SecurityUtils.getCurrentUserId();
        params.put("landlordId", userId);
        return Result.success(leaseAgreementService.pageLeases(params));
    }

    /**
     * 获取管理员的所有合同列表（只读）
     * @param params 查询参数
     * @return 结果
     */
    @GetMapping("/admin")
    public Result<Object> getAdminContracts(@RequestParam Map<String, Object> params) {
        return Result.success(leaseAgreementService.pageLeases(params));
    }
}
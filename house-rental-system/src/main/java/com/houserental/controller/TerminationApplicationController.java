package com.houserental.controller;

import com.houserental.entity.TerminationApplication;
import com.houserental.common.result.Result;
import com.houserental.service.TerminationApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 解约申请控制器
 */
@RestController
@RequestMapping("/api/termination")
public class TerminationApplicationController {

    @Autowired
    private TerminationApplicationService terminationApplicationService;

    /**
     * 创建解约申请
     * @param application 申请信息
     * @return 结果
     */
    @PostMapping
    public Result createApplication(@RequestBody TerminationApplication application) {
        Long id = terminationApplicationService.createApplication(application);
        return Result.success(id);
    }

    /**
     * 处理解约申请
     * @param id 申请ID
     * @param status 处理状态
     * @param processingOpinion 处理意见
     * @param penaltyAmount 违约金金额
     * @return 结果
     */
    @PutMapping("/process/{id}")
    public Result processApplication(@PathVariable Long id, @RequestParam Integer status, @RequestParam String processingOpinion, @RequestParam BigDecimal penaltyAmount) {
        boolean success = terminationApplicationService.processApplication(id, status, processingOpinion, penaltyAmount);
        return success ? Result.success() : Result.error("处理解约申请失败");
    }

    /**
     * 完成解约
     * @param id 申请ID
     * @return 结果
     */
    @PutMapping("/complete/{id}")
    public Result completeTermination(@PathVariable Long id) {
        boolean success = terminationApplicationService.completeTermination(id);
        return success ? Result.success() : Result.error("完成解约失败");
    }

    /**
     * 计算违约金
     * @param leaseId 租约ID
     * @param terminationReason 解约原因
     * @return 结果
     */
    @GetMapping("/calculate-penalty")
    public Result calculatePenalty(@RequestParam Long leaseId, @RequestParam String terminationReason) {
        BigDecimal penalty = terminationApplicationService.calculatePenalty(leaseId, terminationReason);
        return Result.success(penalty);
    }

    /**
     * 分页查询解约申请
     * @param params 查询参数
     * @return 结果
     */
    @GetMapping("/page")
    public Result pageApplications(@RequestParam Map<String, Object> params) {
        return Result.success(terminationApplicationService.pageApplications(params));
    }

    /**
     * 查询解约申请详情
     * @param id 申请ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public Result getApplicationById(@PathVariable Long id) {
        return Result.success(terminationApplicationService.getApplicationById(id));
    }
}
package com.houserental.controller;

import com.houserental.entity.AbnormalOrder;
import com.houserental.common.result.Result;
import com.houserental.common.result.PageResult;
import com.houserental.service.AbnormalOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * 异常订单控制器
 */
@RestController
@RequestMapping("/api/abnormal-orders")
public class AbnormalOrderController {

    @Autowired
    private AbnormalOrderService abnormalOrderService;

    /**
     * 提交异常订单
     * @param abnormalOrder 异常订单信息
     * @param principal 当前用户
     * @return 操作结果
     */
    @PostMapping("/submit")
    public Result<?> submitAbnormalOrder(@RequestBody AbnormalOrder abnormalOrder, Principal principal) {
        // 可以从principal中获取当前用户信息
        boolean result = abnormalOrderService.submitAbnormalOrder(abnormalOrder);
        if (result) {
            return Result.success("异常订单提交成功，等待处理");
        } else {
            return Result.error("异常订单提交失败，请检查信息");
        }
    }

    /**
     * 处理异常订单
     * @param id 异常订单ID
     * @param status 处理状态
     * @param processPlan 处理方案
     * @param principal 当前用户
     * @return 操作结果
     */
    @PostMapping("/process/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<?> processAbnormalOrder(@PathVariable Long id, @RequestParam Integer status, 
                                        @RequestParam String processPlan, Principal principal) {
        // 可以从principal中获取当前处理人ID
        Long processorId = 1L; // 示例，实际应从用户信息中获取
        boolean result = abnormalOrderService.processAbnormalOrder(id, status, processPlan, processorId);
        if (result) {
            return Result.success("异常订单处理成功");
        } else {
            return Result.error("异常订单处理失败，请检查信息");
        }
    }

    /**
     * 根据ID查询异常订单详情
     * @param id 异常订单ID
     * @return 异常订单详情
     */
    @GetMapping("/{id}")
    public Result<AbnormalOrder> getById(@PathVariable Long id) {
        AbnormalOrder abnormalOrder = abnormalOrderService.getById(id);
        return Result.success(abnormalOrder);
    }

    /**
     * 根据订单类型和订单ID查询异常订单
     * @param orderType 订单类型
     * @param orderId 订单ID
     * @return 异常订单
     */
    @GetMapping("/order")
    public Result<AbnormalOrder> getByOrderTypeAndId(@RequestParam Integer orderType, @RequestParam Long orderId) {
        AbnormalOrder abnormalOrder = abnormalOrderService.getByOrderTypeAndId(orderType, orderId);
        return Result.success(abnormalOrder);
    }

    /**
     * 查询待处理的异常订单列表
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<AbnormalOrder>> getPendingList(@RequestParam int page, @RequestParam int size) {
        PageResult<AbnormalOrder> result = abnormalOrderService.getPendingList(page, size);
        return Result.success(result);
    }

    /**
     * 查询处理中的异常订单列表
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/processing")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<AbnormalOrder>> getProcessingList(@RequestParam int page, @RequestParam int size) {
        PageResult<AbnormalOrder> result = abnormalOrderService.getProcessingList(page, size);
        return Result.success(result);
    }

    /**
     * 根据异常类型查询异常订单列表
     * @param abnormalType 异常类型
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/type/{abnormalType}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<AbnormalOrder>> getByAbnormalType(@PathVariable Integer abnormalType, 
                                                             @RequestParam int page, @RequestParam int size) {
        PageResult<AbnormalOrder> result = abnormalOrderService.getByAbnormalType(abnormalType, page, size);
        return Result.success(result);
    }
}

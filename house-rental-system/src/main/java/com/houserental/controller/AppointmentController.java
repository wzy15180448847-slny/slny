package com.houserental.controller;

import com.houserental.entity.Appointment;
import com.houserental.common.result.Result;
import com.houserental.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 预约看房控制器
 */
@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * 创建预约
     * @param appointment 预约信息
     * @return 结果
     */
    @PostMapping
    public Result<Long> createAppointment(@RequestBody Appointment appointment) {
        Long id = appointmentService.createAppointment(appointment);
        return Result.success(id);
    }

    /**
     * 确认预约
     * @param id 预约ID
     * @return 结果
     */
    @PutMapping("/confirm/{id}")
    public Result<Void> confirmAppointment(@PathVariable Long id) {
        boolean success = appointmentService.confirmAppointment(id);
        return success ? Result.success() : Result.error("确认预约失败");
    }

    /**
     * 完成预约
     * @param id 预约ID
     * @return 结果
     */
    @PutMapping("/complete/{id}")
    public Result<Void> completeAppointment(@PathVariable Long id) {
        boolean success = appointmentService.completeAppointment(id);
        return success ? Result.success() : Result.error("完成预约失败");
    }

    /**
     * 取消预约
     * @param id 预约ID
     * @param reason 取消原因
     * @return 结果
     */
    @PutMapping("/cancel/{id}")
    public Result<Void> cancelAppointment(@PathVariable Long id, @RequestParam String reason) {
        boolean success = appointmentService.cancelAppointment(id, reason);
        return success ? Result.success() : Result.error("取消预约失败");
    }

    /**
     * 拒绝预约
     * @param id 预约ID
     * @param reason 拒绝原因
     * @return 结果
     */
    @PutMapping("/reject/{id}")
    public Result<Void> rejectAppointment(@PathVariable Long id, @RequestParam String reason) {
        boolean success = appointmentService.rejectAppointment(id, reason);
        return success ? Result.success() : Result.error("拒绝预约失败");
    }

    /**
     * 分页查询预约
     * @param params 查询参数
     * @return 结果
     */
    @GetMapping("/page")
    public Result<Object> pageAppointments(@RequestParam Map<String, Object> params) {
        return Result.success(appointmentService.pageAppointments(params));
    }

    /**
     * 查询预约详情
     * @param id 预约ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public Result<Appointment> getAppointmentById(@PathVariable Long id) {
        return Result.success(appointmentService.getAppointmentById(id));
    }
}
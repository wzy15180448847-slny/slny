package com.houserental.controller;

import com.houserental.entity.Appointment;
import com.houserental.entity.House;
import com.houserental.entity.User;
import com.houserental.common.result.Result;
import com.houserental.service.AppointmentService;
import com.houserental.service.HouseService;
import com.houserental.service.UserService;
import com.houserental.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 预约看房控制器
 */
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private UserService userService;

    /**
     * 创建预约
     * @param appointment 预约信息
     * @return 结果
     */
    @PostMapping
    public Result<Long> createAppointment(@RequestBody Map<String, Object> appointment) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        Object houseIdObj = appointment.get("houseId");
        Long houseId = null;
        if (houseIdObj instanceof Number) {
            houseId = ((Number) houseIdObj).longValue();
        } else if (houseIdObj instanceof String) {
            try {
                houseId = Long.parseLong((String) houseIdObj);
            } catch (NumberFormatException e) {
                return Result.error("房源ID格式错误");
            }
        }
        
        if (houseId == null) {
            return Result.error("房源ID不能为空");
        }
        
        String date = (String) appointment.get("date");
        String time = (String) appointment.get("time");
        String remark = (String) appointment.get("remark");
        
        House house = houseService.getById(houseId);
        if (house == null) {
            return Result.error("房源不存在");
        }
        
        User user = userService.getById(userId);
        
        Appointment newAppointment = new Appointment();
        newAppointment.setHouseId(houseId);
        newAppointment.setTenantId(userId);
        newAppointment.setLandlordId(house.getLandlordId());
        newAppointment.setContactName(user.getUsername());
        newAppointment.setContactPhone(user.getPhone());
        newAppointment.setStatus(0);
        
        try {
            String startTime = time;
            if (time != null && time.contains("-")) {
                startTime = time.split("-")[0].trim();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date appointmentTime = sdf.parse(date + " " + startTime);
            newAppointment.setAppointmentTime(appointmentTime);
        } catch (ParseException e) {
            return Result.error("预约时间格式错误");
        }
        
        if (remark != null) {
            newAppointment.setRemark(remark);
        }
        
        Long id = appointmentService.createAppointment(newAppointment);
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

    /**
     * 获取当前用户的预约列表
     * @param params 查询参数
     * @return 结果
     */
    @GetMapping("/my")
    public Result<Object> getMyAppointments(@RequestParam Map<String, Object> params) {
        Long userId = com.houserental.common.utils.SecurityUtils.getCurrentUserId();
        params.put("tenantId", userId);
        return Result.success(appointmentService.pageAppointments(params));
    }
}
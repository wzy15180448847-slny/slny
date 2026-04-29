package com.houserental.controller;

import com.houserental.common.result.Result;
import com.houserental.common.utils.SecurityUtils;
import com.houserental.entity.RentReminder;
import com.houserental.service.RentReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 租金催缴控制器
 */
@RestController
@RequestMapping("/rent-reminder")
public class RentReminderController {

    @Autowired
    private RentReminderService rentReminderService;

    @GetMapping("/landlord")
    public Result<Object> getLandlordReminders() {
        Long landlordId = SecurityUtils.getCurrentUserId();
        List<RentReminder> reminders = rentReminderService.getRemindersByLandlordId(landlordId);
        return Result.success(reminders);
    }

    @GetMapping("/pending")
    public Result<Object> getPendingReminders() {
        Long landlordId = SecurityUtils.getCurrentUserId();
        List<RentReminder> reminders = rentReminderService.getPendingRemindersByLandlordId(landlordId);
        return Result.success(reminders);
    }

    @GetMapping("/overdue")
    public Result<Object> getOverdueReminders() {
        Long landlordId = SecurityUtils.getCurrentUserId();
        List<RentReminder> reminders = rentReminderService.getOverdueRemindersByLandlordId(landlordId);
        return Result.success(reminders);
    }

    @PostMapping("/{id}/send")
    public Result<Object> sendReminder(@PathVariable Long id) {
        boolean result = rentReminderService.sendReminderNotification(id);
        if (!result) {
            return Result.error("发送失败");
        }
        return Result.success("催缴通知已发送");
    }

    @PostMapping("/{id}/collection")
    public Result<Object> sendCollection(@PathVariable Long id) {
        RentReminder reminder = rentReminderService.getById(id);
        if (reminder != null) {
            reminder.setStatus(3);
            rentReminderService.updateById(reminder);
            return Result.success("催收通知已发送");
        }
        return Result.error("操作失败");
    }

    @GetMapping("/summary")
    public Result<Object> getLandlordSummary() {
        Long landlordId = SecurityUtils.getCurrentUserId();
        Map<String, Object> summary = rentReminderService.getLandlordSummary(landlordId);
        return Result.success(summary);
    }
}
package com.houserental.controller;

import com.houserental.dto.MessageDTO;
import com.houserental.service.MessageService;
import com.houserental.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息发送控制器
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 发送消息
     * @param messageDTO 消息数据
     * @return 发送结果
     */
    @PostMapping("/send")
    public Result sendMessage(@RequestBody MessageDTO messageDTO) {
        try {
            messageService.sendMessage(messageDTO);
            return Result.success("消息发送成功");
        } catch (Exception e) {
            return Result.error("消息发送失败: " + e.getMessage());
        }
    }

    /**
     * 发送站内消息
     * @param messageDTO 消息数据
     * @return 发送结果
     */
    @PostMapping("/send/notification")
    public Result sendNotification(@RequestBody MessageDTO messageDTO) {
        try {
            messageService.sendNotification(messageDTO);
            return Result.success("站内消息发送成功");
        } catch (Exception e) {
            return Result.error("站内消息发送失败: " + e.getMessage());
        }
    }

    /**
     * 发送短信
     * @param messageDTO 消息数据
     * @return 发送结果
     */
    @PostMapping("/send/sms")
    public Result sendSms(@RequestBody MessageDTO messageDTO) {
        try {
            messageService.sendSms(messageDTO);
            return Result.success("短信发送成功");
        } catch (Exception e) {
            return Result.error("短信发送失败: " + e.getMessage());
        }
    }

    /**
     * 发送邮件
     * @param messageDTO 消息数据
     * @return 发送结果
     */
    @PostMapping("/send/email")
    public Result sendEmail(@RequestBody MessageDTO messageDTO) {
        try {
            messageService.sendEmail(messageDTO);
            return Result.success("邮件发送成功");
        } catch (Exception e) {
            return Result.error("邮件发送失败: " + e.getMessage());
        }
    }
}

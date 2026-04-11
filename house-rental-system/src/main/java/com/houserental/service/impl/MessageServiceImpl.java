package com.houserental.service.impl;

import com.houserental.config.RabbitMQConfig;
import com.houserental.dto.MessageDTO;
import com.houserental.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息发送服务实现
 */
@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void sendNotification(MessageDTO messageDTO) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.NOTIFICATION_EXCHANGE,
                    "notification.send",
                    messageDTO
            );
            log.info("站内消息发送到队列成功: {}", messageDTO.getTitle());
        } catch (Exception e) {
            log.error("站内消息发送失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendSms(MessageDTO messageDTO) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.NOTIFICATION_EXCHANGE,
                    "sms.send",
                    messageDTO
            );
            log.info("短信发送到队列成功: {}", messageDTO.getReceiver());
        } catch (Exception e) {
            log.error("短信发送失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendEmail(MessageDTO messageDTO) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.NOTIFICATION_EXCHANGE,
                    "email.send",
                    messageDTO
            );
            log.info("邮件发送到队列成功: {}", messageDTO.getReceiver());
        } catch (Exception e) {
            log.error("邮件发送失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendMessage(MessageDTO messageDTO) {
        switch (messageDTO.getType()) {
            case "notification":
                sendNotification(messageDTO);
                break;
            case "sms":
                sendSms(messageDTO);
                break;
            case "email":
                sendEmail(messageDTO);
                break;
            default:
                log.warn("未知消息类型: {}", messageDTO.getType());
        }
    }
}

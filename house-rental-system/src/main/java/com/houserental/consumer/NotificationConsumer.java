package com.houserental.consumer;

import com.houserental.dto.MessageDTO;
import com.houserental.entity.Notification;
import com.houserental.mapper.NotificationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 站内消息消费者
 */
@Slf4j
@Component
public class NotificationConsumer {

    @Autowired
    private NotificationMapper notificationMapper;

    @RabbitListener(queues = "notification.queue")
    public void handleNotification(MessageDTO messageDTO) {
        try {
            log.info("接收到站内消息: {}", messageDTO.getTitle());

            // 创建站内消息实体
            Notification notification = new Notification();
            notification.setUserId(messageDTO.getUserId());
            notification.setType(messageDTO.getType());
            notification.setTitle(messageDTO.getTitle());
            notification.setContent(messageDTO.getContent());
            notification.setBusinessId(messageDTO.getBusinessId());
            notification.setReadStatus(0); // 未读
            notification.setStatus(0); // 正常
            notification.setCreateTime(LocalDateTime.now());
            notification.setUpdateTime(LocalDateTime.now());

            // 保存到数据库
            notificationMapper.insert(notification);
            log.info("站内消息保存成功: {}", notification.getId());
        } catch (Exception e) {
            log.error("处理站内消息失败: {}", e.getMessage(), e);
        }
    }
}

package com.houserental.consumer;

import com.houserental.dto.MessageDTO;
import com.houserental.entity.MessageRecord;
import com.houserental.mapper.MessageRecordMapper;
import com.houserental.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 短信消费者
 */
@Slf4j
@Component
public class SmsConsumer {

    @Autowired
    private SmsService smsService;

    @Autowired
    private MessageRecordMapper messageRecordMapper;

    @RabbitListener(queues = "sms.queue")
    public void handleSms(MessageDTO messageDTO) {
        log.info("接收到短信消息: {}", messageDTO.getReceiver());

        // 创建消息记录
        MessageRecord record = new MessageRecord();
        record.setType("sms");
        record.setReceiver(messageDTO.getReceiver());
        record.setContent(messageDTO.getContent());
        record.setTemplateId(messageDTO.getTemplateId());
        record.setStatus(0); // 待发送
        record.setBusinessId(messageDTO.getBusinessId());
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        try {
            // 发送短信
            boolean success = smsService.sendSms(messageDTO);
            record.setStatus(success ? 1 : 2); // 1-成功，2-失败
            record.setSendTime(LocalDateTime.now());
            if (!success) {
                record.setErrorMsg("短信发送失败");
            }
        } catch (Exception e) {
            record.setStatus(2); // 失败
            record.setErrorMsg(e.getMessage());
            log.error("发送短信失败: {}", e.getMessage(), e);
        } finally {
            // 保存发送记录
            messageRecordMapper.insert(record);
            log.info("短信发送记录保存成功: {}", record.getId());
        }
    }
}

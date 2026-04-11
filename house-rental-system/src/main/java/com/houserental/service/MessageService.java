package com.houserental.service;

import com.houserental.dto.MessageDTO;

/**
 * 消息发送服务
 */
public interface MessageService {

    /**
     * 发送站内消息
     * @param messageDTO 消息数据
     */
    void sendNotification(MessageDTO messageDTO);

    /**
     * 发送短信
     * @param messageDTO 消息数据
     */
    void sendSms(MessageDTO messageDTO);

    /**
     * 发送邮件
     * @param messageDTO 消息数据
     */
    void sendEmail(MessageDTO messageDTO);

    /**
     * 发送消息（根据类型自动选择发送方式）
     * @param messageDTO 消息数据
     */
    void sendMessage(MessageDTO messageDTO);
}

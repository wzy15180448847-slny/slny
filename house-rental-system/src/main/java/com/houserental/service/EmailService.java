package com.houserental.service;

import com.houserental.dto.MessageDTO;

/**
 * 邮件服务
 */
public interface EmailService {

    /**
     * 发送邮件
     * @param messageDTO 消息数据
     * @return 是否发送成功
     */
    boolean sendEmail(MessageDTO messageDTO);
}

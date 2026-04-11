package com.houserental.service;

import com.houserental.dto.MessageDTO;

/**
 * 短信服务
 */
public interface SmsService {

    /**
     * 发送短信
     * @param messageDTO 消息数据
     * @return 是否发送成功
     */
    boolean sendSms(MessageDTO messageDTO);
}

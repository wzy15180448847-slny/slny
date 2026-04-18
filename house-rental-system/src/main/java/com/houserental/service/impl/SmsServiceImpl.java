package com.houserental.service.impl;

import com.houserental.dto.MessageDTO;
import com.houserental.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 短信服务实现（模拟模式）
 * 注：阿里云短信服务已移除，当前仅记录日志
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public boolean sendSms(MessageDTO messageDTO) {
        log.info("【短信模拟发送】接收方: {}, 模板ID: {}, 内容参数: {}", 
                messageDTO.getReceiver(), 
                messageDTO.getTemplateId(), 
                messageDTO.getTemplateParams());
        log.info("短信服务当前处于模拟模式，如需启用真实短信服务，请集成第三方短信平台");
        return true;
    }
}
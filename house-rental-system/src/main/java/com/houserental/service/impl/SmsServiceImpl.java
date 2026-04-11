package com.houserental.service.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.houserental.dto.MessageDTO;
import com.houserental.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 阿里云短信服务实现
 */
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Value("${app.sms.access-key:test}")
    private String accessKeyId;

    @Value("${app.sms.secret-key:test}")
    private String accessKeySecret;

    @Value("${app.sms.sign-name:房屋租赁平台}")
    private String signName;

    @Override
    public boolean sendSms(MessageDTO messageDTO) {
        try {
            // 设置阿里云短信服务的区域
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Dysmsapi", "dysmsapi.aliyuncs.com");
            IAcsClient acsClient = new DefaultAcsClient(profile);

            // 构建短信请求
            SendSmsRequest request = new SendSmsRequest();
            request.setPhoneNumbers(messageDTO.getReceiver());
            request.setSignName(signName);
            request.setTemplateCode(messageDTO.getTemplateId());

            // 设置模板参数
            if (messageDTO.getTemplateParams() != null) {
                StringBuilder params = new StringBuilder("{");
                for (Map.Entry<String, String> entry : messageDTO.getTemplateParams().entrySet()) {
                    params.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\",");
                }
                if (params.length() > 1) {
                    params.deleteCharAt(params.length() - 1);
                }
                params.append("}");
                request.setTemplateParam(params.toString());
            }

            // 发送短信
            SendSmsResponse response = acsClient.getAcsResponse(request);
            log.info("短信发送响应: {}", response.getCode());

            return "OK".equals(response.getCode());
        } catch (ClientException e) {
            log.error("短信发送失败: {}", e.getMessage(), e);
            return false;
        }
    }
}

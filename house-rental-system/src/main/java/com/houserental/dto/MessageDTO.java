package com.houserental.dto;

import lombok.Data;

import java.util.Map;

/**
 * 消息传输对象
 */
@Data
public class MessageDTO {

    /**
     * 消息类型：notification(站内消息), sms(短信), email(邮件)
     */
    private String type;

    /**
     * 接收者ID（用户ID、手机号或邮箱）
     */
    private String receiver;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 模板ID
     */
    private String templateId;

    /**
     * 模板参数
     */
    private Map<String, String> templateParams;

    /**
     * 用户ID（站内消息使用）
     */
    private Long userId;
}

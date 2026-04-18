package com.houserental.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

/**
 * 消息发送记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("message_record")
public class MessageRecord extends BaseEntity {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 消息类型：sms(短信), email(邮件)
     */
    private String type;

    /**
     * 接收方：手机号或邮箱
     */
    private String receiver;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 模板ID
     */
    private String templateId;

    /**
     * 发送状态：0-待发送，1-发送成功，2-发送失败
     */
    private Integer status;

    /**
     * 失败原因
     */
    private String errorMsg;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 关联业务ID
     */
    private String businessId;
}

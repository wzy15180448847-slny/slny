package com.houserental.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;

/**
 * 站内消息通知实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notification")
public class Notification extends BaseEntity {

    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接收用户ID
     */
    private Long userId;

    /**
     * 消息类型：system(系统消息), order(订单消息), payment(支付消息), other(其他)
     */
    private String type;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 关联业务ID
     */
    private String businessId;

    /**
     * 阅读状态：0-未读，1-已读
     */
    private Integer readStatus;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 消息状态：0-正常，1-已删除
     */
    private Integer status;
}

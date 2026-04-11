-- 创建站内消息表
CREATE TABLE IF NOT EXISTS `notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
  `type` VARCHAR(50) NOT NULL COMMENT '消息类型：system(系统消息), order(订单消息), payment(支付消息), other(其他)',
  `title` VARCHAR(255) NOT NULL COMMENT '消息标题',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `business_id` VARCHAR(100) DEFAULT NULL COMMENT '关联业务ID',
  `read_status` INT NOT NULL DEFAULT 0 COMMENT '阅读状态：0-未读，1-已读',
  `read_time` DATETIME DEFAULT NULL COMMENT '阅读时间',
  `status` INT NOT NULL DEFAULT 0 COMMENT '消息状态：0-正常，1-已删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_read_status` (`read_status`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内消息通知表';

-- 创建消息发送记录表
CREATE TABLE IF NOT EXISTS `message_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(50) NOT NULL COMMENT '消息类型：sms(短信), email(邮件)',
  `receiver` VARCHAR(255) NOT NULL COMMENT '接收方：手机号或邮箱',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `template_id` VARCHAR(100) DEFAULT NULL COMMENT '模板ID',
  `status` INT NOT NULL DEFAULT 0 COMMENT '发送状态：0-待发送，1-发送成功，2-发送失败',
  `error_msg` TEXT DEFAULT NULL COMMENT '失败原因',
  `send_time` DATETIME DEFAULT NULL COMMENT '发送时间',
  `business_id` VARCHAR(100) DEFAULT NULL COMMENT '关联业务ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_status` (`status`),
  INDEX `idx_receiver` (`receiver`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息发送记录表';

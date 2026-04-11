-- 异常订单表
CREATE TABLE IF NOT EXISTS `biz_abnormal_order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_type` INT(11) NOT NULL COMMENT '订单类型（1-预约订单，2-租赁合同）',
  `order_id` BIGINT(20) NOT NULL COMMENT '订单ID',
  `abnormal_type` INT(11) NOT NULL COMMENT '异常类型（1-支付异常，2-预约取消，3-合同违约，4-其他）',
  `description` TEXT NOT NULL COMMENT '异常描述',
  `status` INT(11) DEFAULT 0 COMMENT '处理状态（0-待处理，1-处理中，2-已处理，3-已关闭）',
  `process_plan` TEXT COMMENT '处理方案',
  `processor_id` BIGINT(20) COMMENT '处理人ID',
  `process_time` DATETIME COMMENT '处理时间',
  `created_by` BIGINT(20) COMMENT '创建人',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` BIGINT(20) COMMENT '更新人',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT(4) DEFAULT 0 COMMENT '逻辑删除标识',
  PRIMARY KEY (`id`),
  KEY `idx_order_type_order_id` (`order_type`, `order_id`),
  KEY `idx_status` (`status`),
  KEY `idx_abnormal_type` (`abnormal_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异常订单表';
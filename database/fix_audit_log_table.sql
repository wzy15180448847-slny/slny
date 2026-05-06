-- 修复 biz_audit_log 表，添加 BaseEntity 要求的字段
USE `house_eco`;

ALTER TABLE `biz_audit_log`
ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `create_time`,
ADD COLUMN `create_by` BIGINT(20) COMMENT '创建人' AFTER `update_time`,
ADD COLUMN `update_by` BIGINT(20) COMMENT '更新人' AFTER `create_by`,
ADD COLUMN `version` INT(11) DEFAULT 0 COMMENT '乐观锁版本号' AFTER `update_by`;

-- 验证修复
DESCRIBE `biz_audit_log`;

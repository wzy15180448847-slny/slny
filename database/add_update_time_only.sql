-- 简单方案：只添加 update_time 字段
USE `house_eco`;

-- 添加 update_time 字段
ALTER TABLE `biz_audit_log` 
ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `create_time`;

-- 验证结果
DESCRIBE `biz_audit_log`;

USE house_eco;

-- 查看当前 biz_audit_log 表结构
DESCRIBE biz_audit_log;

-- 添加缺失的字段（BaseEntity 中定义但数据库表中缺失的字段）
ALTER TABLE biz_audit_log 
ADD COLUMN update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
ADD COLUMN create_by BIGINT(20) COMMENT '创建人',
ADD COLUMN update_by BIGINT(20) COMMENT '更新人',
ADD COLUMN version INT(11) DEFAULT 0 COMMENT '乐观锁版本号',
ADD COLUMN remark VARCHAR(500) COMMENT '备注';

-- 查看修复后的表结构
DESCRIBE biz_audit_log;

SELECT 'biz_audit_log 表已修复成功！' AS result;
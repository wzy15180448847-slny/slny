-- 安全修复 biz_audit_log 表 - 只添加缺失的字段
USE `house_eco`;

-- 先查看当前表结构，确认哪些字段缺失
DESCRIBE `biz_audit_log`;

-- 逐个添加缺失字段（使用存储过程安全检查）
DELIMITER //

DROP PROCEDURE IF EXISTS add_column_if_not_exists//
CREATE PROCEDURE add_column_if_not_exists(
    IN table_name VARCHAR(64),
    IN column_name VARCHAR(64),
    IN column_definition TEXT
)
BEGIN
    DECLARE column_exists INT;
    
    SELECT COUNT(*) INTO column_exists
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = table_name
      AND column_name = column_name;
    
    IF column_exists = 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', table_name, '` ADD COLUMN `', column_name, '` ', column_definition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SELECT CONCAT('成功添加字段: ', column_name) AS result;
    ELSE
        SELECT CONCAT('字段已存在，跳过: ', column_name) AS result;
    END IF;
END//

DELIMITER ;

-- 添加字段
CALL add_column_if_not_exists('biz_audit_log', 'update_time', 'DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT "更新时间" AFTER `create_time`');
CALL add_column_if_not_exists('biz_audit_log', 'create_by', 'BIGINT(20) COMMENT "创建人" AFTER `update_time`');
CALL add_column_if_not_exists('biz_audit_log', 'update_by', 'BIGINT(20) COMMENT "更新人" AFTER `create_by`');
CALL add_column_if_not_exists('biz_audit_log', 'version', 'INT(11) DEFAULT 0 COMMENT "乐观锁版本号" AFTER `update_by`');

-- 清理存储过程
DROP PROCEDURE IF EXISTS add_column_if_not_exists;

-- 验证最终表结构
DESCRIBE `biz_audit_log`;

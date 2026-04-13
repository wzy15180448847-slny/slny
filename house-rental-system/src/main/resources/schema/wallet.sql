CREATE TABLE `user_wallet` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `balance` DECIMAL(18,2) NOT NULL DEFAULT '0.00' COMMENT '账户余额',
    `freeze_balance` DECIMAL(18,2) NOT NULL DEFAULT '0.00' COMMENT '冻结金额',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `version` INT NOT NULL DEFAULT '1' COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    CONSTRAINT `chk_balance_non_negative` CHECK (`balance` >= 0),
    CONSTRAINT `chk_freeze_balance_non_negative` CHECK (`freeze_balance` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户钱包表';

CREATE TABLE `wallet_transaction_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `transaction_type` TINYINT NOT NULL COMMENT '交易类型：1-充值 2-消费 3-退款 4-转账',
    `amount` DECIMAL(18,2) NOT NULL COMMENT '交易金额',
    `balance_before` DECIMAL(18,2) NOT NULL COMMENT '交易前余额',
    `balance_after` DECIMAL(18,2) NOT NULL COMMENT '交易后余额',
    `order_no` VARCHAR(64) COMMENT '关联订单号',
    `related_user_id` BIGINT COMMENT '关联用户ID（如转账对方）',
    `remark` VARCHAR(255) COMMENT '交易备注',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包交易流水表';
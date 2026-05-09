-- 创建账单表
CREATE TABLE IF NOT EXISTS biz_bill (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '账单ID',
    lease_id BIGINT(20) NOT NULL COMMENT '租约ID',
    house_id BIGINT(20) NOT NULL COMMENT '房源ID',
    tenant_id BIGINT(20) NOT NULL COMMENT '租客ID',
    landlord_id BIGINT(20) NOT NULL COMMENT '房东ID',
    amount DECIMAL(15,2) NOT NULL COMMENT '金额',
    status TINYINT(1) DEFAULT 1 COMMENT '状态（1-待支付，2-已支付，3-已取消）',
    bill_type TINYINT(1) DEFAULT 1 COMMENT '账单类型（1-租金，2-押金，3-违约金）',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除（0-未删除，1-已删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT(20) COMMENT '创建人',
    update_by BIGINT(20) COMMENT '更新人',
    FOREIGN KEY (lease_id) REFERENCES biz_lease_agreement(id),
    FOREIGN KEY (house_id) REFERENCES biz_house(id),
    FOREIGN KEY (tenant_id) REFERENCES sys_user(id),
    FOREIGN KEY (landlord_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账单表';

-- 插入一些测试账单数据
INSERT INTO biz_bill (lease_id, house_id, tenant_id, landlord_id, amount, status, bill_type) VALUES 
(1, 1, 4, 2, 4500.00, 1, 1),
(2, 2, 5, 2, 6800.00, 2, 1),
(1, 1, 4, 2, 9000.00, 2, 2);

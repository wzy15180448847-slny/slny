-- =============================================
-- 房屋租赁平台 - 预约看房、租约管理、电子签章、解约流程
-- =============================================

USE house_rental;

-- =============================================
-- 预约看房表
-- =============================================
DROP TABLE IF EXISTS biz_appointment;
CREATE TABLE biz_appointment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
    appointment_no VARCHAR(32) NOT NULL UNIQUE COMMENT '预约编号',
    house_id BIGINT NOT NULL COMMENT '房源ID',
    tenant_id BIGINT NOT NULL COMMENT '租客ID',
    landlord_id BIGINT NOT NULL COMMENT '房东ID',
    appointment_time DATETIME NOT NULL COMMENT '预约时间',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态（0-待确认，1-已确认，2-已完成，3-已取消，4-已拒绝）',
    contact_name VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系人电话',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    version INT DEFAULT 0 COMMENT '版本号',
    INDEX idx_house_id (house_id),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_landlord_id (landlord_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约看房表';

-- =============================================
-- 租约表
-- =============================================
DROP TABLE IF EXISTS biz_lease_agreement;
CREATE TABLE biz_lease_agreement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '租约ID',
    lease_no VARCHAR(32) NOT NULL UNIQUE COMMENT '租约编号',
    house_id BIGINT NOT NULL COMMENT '房源ID',
    tenant_id BIGINT NOT NULL COMMENT '租客ID',
    landlord_id BIGINT NOT NULL COMMENT '房东ID',
    start_date DATETIME NOT NULL COMMENT '起租日期',
    end_date DATETIME NOT NULL COMMENT '到期日期',
    rent_price DECIMAL(10, 2) NOT NULL COMMENT '租金（元/月）',
    deposit DECIMAL(10, 2) NOT NULL COMMENT '押金（元）',
    payment_way INT NOT NULL COMMENT '付款方式（1-月付，2-季付，3-半年付，4-年付）',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态（0-待签署，1-已签署，2-已生效，3-已到期，4-已解约，5-已终止）',
    signing_date DATETIME COMMENT '签署日期',
    effective_date DATETIME COMMENT '生效日期',
    termination_date DATETIME COMMENT '终止日期',
    penalty_rule VARCHAR(500) COMMENT '违约金规则',
    agreement_content TEXT COMMENT '合同内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    version INT DEFAULT 0 COMMENT '版本号',
    INDEX idx_house_id (house_id),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_landlord_id (landlord_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租约表';

-- =============================================
-- 电子签章表
-- =============================================
DROP TABLE IF EXISTS biz_electronic_signature;
CREATE TABLE biz_electronic_signature (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '签章ID',
    signature_no VARCHAR(32) NOT NULL UNIQUE COMMENT '签章编号',
    lease_id BIGINT NOT NULL COMMENT '租约ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_type VARCHAR(20) NOT NULL COMMENT '用户类型（LANDLORD-房东, TENANT-租客）',
    signature_data VARCHAR(255) COMMENT '签章数据',
    signing_time DATETIME COMMENT '签章时间',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态（0-待签章，1-已签章）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    version INT DEFAULT 0 COMMENT '版本号',
    INDEX idx_lease_id (lease_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电子签章表';

-- =============================================
-- 解约申请表
-- =============================================
DROP TABLE IF EXISTS biz_termination_application;
CREATE TABLE biz_termination_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '解约申请ID',
    application_no VARCHAR(32) NOT NULL UNIQUE COMMENT '申请编号',
    lease_id BIGINT NOT NULL COMMENT '租约ID',
    applicant_id BIGINT NOT NULL COMMENT '申请人ID',
    applicant_type VARCHAR(20) NOT NULL COMMENT '申请人类型（LANDLORD-房东, TENANT-租客）',
    termination_reason TEXT NOT NULL COMMENT '解约原因',
    apply_time DATETIME NOT NULL COMMENT '申请时间',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态（0-待处理，1-已同意，2-已拒绝，3-已完成）',
    processing_time DATETIME COMMENT '处理时间',
    processor_id BIGINT COMMENT '处理人ID',
    processing_opinion TEXT COMMENT '处理意见',
    penalty_amount DECIMAL(10, 2) COMMENT '违约金金额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    version INT DEFAULT 0 COMMENT '版本号',
    INDEX idx_lease_id (lease_id),
    INDEX idx_applicant_id (applicant_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='解约申请表';

-- =============================================
-- 违约金计算规则表
-- =============================================
DROP TABLE IF EXISTS biz_penalty_rule;
CREATE TABLE biz_penalty_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '规则ID',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_type VARCHAR(20) NOT NULL COMMENT '规则类型（EARLY_TERMINATION-提前解约，LATE_PAYMENT-逾期付款，DAMAGE-损坏赔偿）',
    calculation_method VARCHAR(100) NOT NULL COMMENT '计算方式',
    base_amount DECIMAL(10, 2) COMMENT '基础金额',
    percentage DECIMAL(5, 2) COMMENT '百分比',
    min_amount DECIMAL(10, 2) COMMENT '最低金额',
    max_amount DECIMAL(10, 2) COMMENT '最高金额',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    remark VARCHAR(500) COMMENT '备注',
    version INT DEFAULT 0 COMMENT '版本号',
    INDEX idx_rule_type (rule_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='违约金计算规则表';

-- =============================================
-- 初始化违约金规则数据
-- =============================================
INSERT INTO biz_penalty_rule (rule_name, rule_type, calculation_method, base_amount, percentage, min_amount, max_amount, status) VALUES
('提前解约违约金规则', 'EARLY_TERMINATION', 'RENT_MULTIPLE', 0, 0, 2000, 10000, 1),
('逾期付款违约金规则', 'LATE_PAYMENT', 'DAILY_PERCENTAGE', 0, 0.05, 50, 500, 1),
('房屋损坏赔偿规则', 'DAMAGE', 'ACTUAL_COST', 0, 0, 0, 0, 1);

-- =============================================
-- 查询验证
-- =============================================

SELECT '预约看房表结构：' AS info;
DESCRIBE biz_appointment;

SELECT '租约表结构：' AS info;
DESCRIBE biz_lease_agreement;

SELECT '电子签章表结构：' AS info;
DESCRIBE biz_electronic_signature;

SELECT '解约申请表结构：' AS info;
DESCRIBE biz_termination_application;

SELECT '违约金规则表结构：' AS info;
DESCRIBE biz_penalty_rule;

SELECT '违约金规则数据：' AS info;
SELECT id, rule_name, rule_type, calculation_method, min_amount, max_amount FROM biz_penalty_rule;
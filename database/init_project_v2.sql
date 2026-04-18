-- =============================================
-- 房屋租赁平台数据库初始化脚本 v2.0
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS house_rental 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE house_rental;

-- =============================================
-- 系统管理表
-- =============================================

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别（0-未知，1-男，2-女）',
    id_card VARCHAR(18) COMMENT '身份证号',
    user_type VARCHAR(20) NOT NULL COMMENT '用户类型（LANDLORD-房东, TENANT-租客, AGENT-中介, ADMIN-管理员）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用，2-锁定）',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    login_fail_count INT DEFAULT 0 COMMENT '登录失败次数',
    password_update_time DATETIME COMMENT '密码最后修改时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志（0-未删除，1-已删除）',
    remark VARCHAR(500) COMMENT '备注',
    version INT DEFAULT 0 COMMENT '版本号',
    INDEX idx_username (username),
    INDEX idx_phone (phone),
    INDEX idx_user_type (user_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(200) COMMENT '角色描述',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    remark VARCHAR(500) COMMENT '备注',
    version INT DEFAULT 0 COMMENT '版本号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    perm_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    perm_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    parent_id BIGINT COMMENT '父权限ID',
    perm_type VARCHAR(20) COMMENT '权限类型（MENU-菜单，BUTTON-按钮，API-接口）',
    path VARCHAR(200) COMMENT '权限路径/URL',
    method VARCHAR(10) COMMENT '请求方法',
    icon VARCHAR(100) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    remark VARCHAR(500) COMMENT '备注',
    version INT DEFAULT 0 COMMENT '版本号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY idx_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    perm_id BIGINT NOT NULL COMMENT '权限ID',
    UNIQUE KEY idx_role_perm (role_id, perm_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- =============================================
-- 业务核心表
-- =============================================

-- 房源表
DROP TABLE IF EXISTS biz_house;
CREATE TABLE biz_house (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房源ID',
    house_no VARCHAR(32) NOT NULL UNIQUE COMMENT '房源编号',
    landlord_id BIGINT NOT NULL COMMENT '房东ID',
    title VARCHAR(200) NOT NULL COMMENT '房源标题',
    description TEXT COMMENT '房源描述',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    district VARCHAR(50) COMMENT '区县',
    street VARCHAR(100) COMMENT '街道',
    address VARCHAR(200) NOT NULL COMMENT '详细地址',
    longitude DECIMAL(10, 7) COMMENT '经度',
    latitude DECIMAL(10, 7) COMMENT '纬度',
    house_type VARCHAR(20) COMMENT '户型',
    room_count INT COMMENT '房间数',
    hall_count INT COMMENT '客厅数',
    bathroom_count INT COMMENT '卫生间数',
    area DECIMAL(10, 2) COMMENT '面积（平方米）',
    floor INT COMMENT '楼层',
    total_floor INT COMMENT '总楼层',
    has_elevator TINYINT COMMENT '电梯（0-无，1-有）',
    decoration TINYINT COMMENT '装修情况（1-毛坯，2-简装，3-精装，4-豪装）',
    orientation TINYINT COMMENT '朝向',
    rent_price DECIMAL(10, 2) NOT NULL COMMENT '租金（元/月）',
    deposit_month INT DEFAULT 1 COMMENT '押金（月）',
    payment_way INT DEFAULT 2 COMMENT '付款方式',
    rent_way INT DEFAULT 1 COMMENT '租赁方式（1-整租，2-合租）',
    facilities VARCHAR(500) COMMENT '配套设施（JSON）',
    images VARCHAR(2000) COMMENT '房源图片（JSON）',
    cover_image VARCHAR(255) COMMENT '封面图片',
    contact_name VARCHAR(50) COMMENT '联系人姓名',
    contact_phone VARCHAR(20) COMMENT '联系人电话',
    view_time_type TINYINT COMMENT '看房时间',
    available_date DATETIME COMMENT '可入住时间',
    min_lease_term INT DEFAULT 12 COMMENT '最小租期（月）',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态（0-草稿，1-待审核，2-已发布，3-已下架，4-已出租）',
    audit_status TINYINT DEFAULT 0 COMMENT '审核状态（0-待审核，1-审核通过，2-审核拒绝）',
    audit_remark VARCHAR(500) COMMENT '审核意见',
    auditor_id BIGINT COMMENT '审核人ID',
    audit_time DATETIME COMMENT '审核时间',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    favorite_count INT DEFAULT 0 COMMENT '收藏次数',
    appointment_count INT DEFAULT 0 COMMENT '预约次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    remark VARCHAR(500) COMMENT '备注',
    version INT DEFAULT 0 COMMENT '版本号',
    INDEX idx_landlord_id (landlord_id),
    INDEX idx_status (status),
    INDEX idx_city (city),
    INDEX idx_district (district),
    INDEX idx_price (rent_price),
    INDEX idx_house_type (house_type),
    INDEX idx_area (area)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源表';

-- 收藏表
DROP TABLE IF EXISTS biz_favorite;
CREATE TABLE biz_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    house_id BIGINT NOT NULL COMMENT '房源ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    UNIQUE KEY idx_user_house (user_id, house_id),
    INDEX idx_user_id (user_id),
    INDEX idx_house_id (house_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- =============================================
-- 预约与租约管理
-- =============================================

-- 预约看房表
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

-- 租约表
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

-- 电子签章表
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

-- 解约申请表
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

-- 违约金计算规则表
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
-- 支付与钱包管理
-- =============================================

-- 用户钱包表
DROP TABLE IF EXISTS user_wallet;
CREATE TABLE user_wallet (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    balance DECIMAL(18,2) NOT NULL DEFAULT '0.00' COMMENT '账户余额',
    freeze_balance DECIMAL(18,2) NOT NULL DEFAULT '0.00' COMMENT '冻结金额',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    version INT NOT NULL DEFAULT '1' COMMENT '乐观锁版本号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id),
    CONSTRAINT chk_balance_non_negative CHECK (balance >= 0),
    CONSTRAINT chk_freeze_balance_non_negative CHECK (freeze_balance >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户钱包表';

-- 钱包交易流水表
DROP TABLE IF EXISTS wallet_transaction_log;
CREATE TABLE wallet_transaction_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    transaction_type TINYINT NOT NULL COMMENT '交易类型：1-充值 2-消费 3-退款 4-转账',
    amount DECIMAL(18,2) NOT NULL COMMENT '交易金额',
    balance_before DECIMAL(18,2) NOT NULL COMMENT '交易前余额',
    balance_after DECIMAL(18,2) NOT NULL COMMENT '交易后余额',
    order_no VARCHAR(64) COMMENT '关联订单号',
    related_user_id BIGINT COMMENT '关联用户ID（如转账对方）',
    remark VARCHAR(255) COMMENT '交易备注',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_order_no (order_no),
    KEY idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包交易流水表';

-- 支付记录表
DROP TABLE IF EXISTS biz_payment_record;
CREATE TABLE biz_payment_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付记录ID',
    order_no VARCHAR(64) NOT NULL UNIQUE COMMENT '订单编号',
    pay_type VARCHAR(20) NOT NULL COMMENT '支付类型（WECHAT-微信支付，ALIPAY-支付宝，WALLET-钱包支付）',
    amount DECIMAL(10, 2) NOT NULL COMMENT '支付金额',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态（0-待支付，1-支付成功，2-支付失败，3-已退款）',
    pay_time DATETIME COMMENT '支付时间',
    refund_time DATETIME COMMENT '退款时间',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    INDEX idx_order_no (order_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- =============================================
-- 消息通知
-- =============================================

-- 站内消息通知表
DROP TABLE IF EXISTS notification;
CREATE TABLE notification (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    type VARCHAR(50) NOT NULL COMMENT '消息类型：system(系统消息), order(订单消息), payment(支付消息), other(其他)',
    title VARCHAR(255) NOT NULL COMMENT '消息标题',
    content TEXT NOT NULL COMMENT '消息内容',
    business_id VARCHAR(100) DEFAULT NULL COMMENT '关联业务ID',
    read_status INT NOT NULL DEFAULT 0 COMMENT '阅读状态：0-未读，1-已读',
    read_time DATETIME DEFAULT NULL COMMENT '阅读时间',
    status INT NOT NULL DEFAULT 0 COMMENT '消息状态：0-正常，1-已删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_user_id (user_id),
    INDEX idx_read_status (read_status),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内消息通知表';

-- 消息发送记录表
DROP TABLE IF EXISTS message_record;
CREATE TABLE message_record (
    id BIGINT NOT NULL AUTO_INCREMENT,
    type VARCHAR(50) NOT NULL COMMENT '消息类型：sms(短信), email(邮件)',
    receiver VARCHAR(255) NOT NULL COMMENT '接收方：手机号或邮箱',
    content TEXT NOT NULL COMMENT '消息内容',
    template_id VARCHAR(100) DEFAULT NULL COMMENT '模板ID',
    status INT NOT NULL DEFAULT 0 COMMENT '发送状态：0-待发送，1-发送成功，2-发送失败',
    error_msg TEXT DEFAULT NULL COMMENT '失败原因',
    send_time DATETIME DEFAULT NULL COMMENT '发送时间',
    business_id VARCHAR(100) DEFAULT NULL COMMENT '关联业务ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_receiver (receiver)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息发送记录表';

-- =============================================
-- 投诉与异常处理
-- =============================================

-- 用户投诉表
DROP TABLE IF EXISTS biz_complaint;
CREATE TABLE biz_complaint (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    complainant_id BIGINT NOT NULL COMMENT '投诉人ID',
    respondent_id BIGINT NOT NULL COMMENT '被投诉人ID',
    complaint_type INT NOT NULL COMMENT '投诉类型（1-房源问题，2-服务问题，3-合同问题，4-其他）',
    title VARCHAR(255) NOT NULL COMMENT '投诉标题',
    content TEXT NOT NULL COMMENT '投诉内容',
    evidence TEXT COMMENT '投诉证据（JSON格式存储，多张图片URL）',
    status INT DEFAULT 0 COMMENT '处理状态（0-待处理，1-处理中，2-已处理，3-已驳回）',
    process_result TEXT COMMENT '处理结果',
    processor_id BIGINT COMMENT '处理人ID',
    process_time DATETIME COMMENT '处理时间',
    created_by BIGINT COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    KEY idx_complainant_id (complainant_id),
    KEY idx_respondent_id (respondent_id),
    KEY idx_status (status),
    CONSTRAINT fk_complaint_complainant FOREIGN KEY (complainant_id) REFERENCES sys_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_complaint_respondent FOREIGN KEY (respondent_id) REFERENCES sys_user (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户投诉表';

-- 异常订单表
DROP TABLE IF EXISTS biz_abnormal_order;
CREATE TABLE biz_abnormal_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_type INT NOT NULL COMMENT '订单类型（1-预约订单，2-租赁合同）',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    abnormal_type INT NOT NULL COMMENT '异常类型（1-支付异常，2-预约取消，3-合同违约，4-其他）',
    description TEXT NOT NULL COMMENT '异常描述',
    status INT DEFAULT 0 COMMENT '处理状态（0-待处理，1-处理中，2-已处理，3-已关闭）',
    process_plan TEXT COMMENT '处理方案',
    processor_id BIGINT COMMENT '处理人ID',
    process_time DATETIME COMMENT '处理时间',
    created_by BIGINT COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    KEY idx_order_type_order_id (order_type, order_id),
    KEY idx_status (status),
    KEY idx_abnormal_type (abnormal_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='异常订单表';

-- =============================================
-- 中介资质管理
-- =============================================

-- 中介资质审核表
DROP TABLE IF EXISTS biz_agent_qualification;
CREATE TABLE biz_agent_qualification (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    agent_id BIGINT NOT NULL COMMENT '中介用户ID',
    company_name VARCHAR(255) NOT NULL COMMENT '中介公司名称',
    business_license VARCHAR(255) NOT NULL COMMENT '营业执照号',
    license_image VARCHAR(500) NOT NULL COMMENT '营业执照图片URL',
    qualification_cert VARCHAR(255) NOT NULL COMMENT '中介资质证书号',
    cert_image VARCHAR(500) NOT NULL COMMENT '资质证书图片URL',
    contact_name VARCHAR(100) NOT NULL COMMENT '联系人姓名',
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系人电话',
    audit_status INT DEFAULT 0 COMMENT '审核状态（0-待审核，1-审核通过，2-审核拒绝）',
    audit_remark TEXT COMMENT '审核意见',
    auditor_id BIGINT COMMENT '审核人ID',
    audit_time DATETIME COMMENT '审核时间',
    created_by BIGINT COMMENT '创建人',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_by BIGINT COMMENT '更新人',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
    PRIMARY KEY (id),
    KEY idx_agent_id (agent_id),
    KEY idx_audit_status (audit_status),
    CONSTRAINT fk_agent_qualification_user FOREIGN KEY (agent_id) REFERENCES sys_user (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='中介资质审核表';

-- =============================================
-- 系统日志表
-- =============================================

-- 登录日志表
DROP TABLE IF EXISTS sys_login_log;
CREATE TABLE sys_login_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    login_ip VARCHAR(50) COMMENT '登录IP',
    login_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    login_status TINYINT COMMENT '登录状态（0-失败，1-成功）',
    error_msg VARCHAR(255) COMMENT '错误信息',
    user_agent VARCHAR(500) COMMENT '用户代理',
    INDEX idx_user_id (user_id),
    INDEX idx_login_time (login_time),
    INDEX idx_login_status (login_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- 审核日志表
DROP TABLE IF EXISTS sys_audit_log;
CREATE TABLE sys_audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    house_id BIGINT NOT NULL COMMENT '房源ID',
    auditor_id BIGINT COMMENT '审核人ID',
    auditor_name VARCHAR(50) COMMENT '审核人姓名',
    before_status INT COMMENT '审核前状态',
    after_status INT COMMENT '审核后状态',
    before_audit_status INT COMMENT '审核前审核状态',
    after_audit_status INT COMMENT '审核后审核状态',
    audit_remark VARCHAR(500) COMMENT '审核意见',
    audit_result INT COMMENT '审核结果（0-待审核，1-通过，2-拒绝）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_house_id (house_id),
    INDEX idx_auditor_id (auditor_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核日志表';

-- =============================================
-- 租金提醒表
-- =============================================

DROP TABLE IF EXISTS biz_rent_reminder;
CREATE TABLE biz_rent_reminder (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '提醒ID',
    lease_id BIGINT NOT NULL COMMENT '租约ID',
    tenant_id BIGINT NOT NULL COMMENT '租客ID',
    landlord_id BIGINT NOT NULL COMMENT '房东ID',
    reminder_date DATETIME NOT NULL COMMENT '提醒日期',
    rent_amount DECIMAL(10, 2) NOT NULL COMMENT '租金金额',
    status TINYINT DEFAULT 0 COMMENT '状态（0-待提醒，1-已提醒，2-已支付）',
    reminder_type TINYINT COMMENT '提醒类型（1-短信提醒，2-邮件提醒，3-站内消息）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_lease_id (lease_id),
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租金提醒表';

-- =============================================
-- 初始化数据
-- =============================================

-- 初始化角色
INSERT INTO sys_role (role_code, role_name, description, sort_order) VALUES
('ADMIN', '系统管理员', '系统管理员角色', 1),
('LANDLORD', '房东', '房东角色', 2),
('TENANT', '租客', '租客角色', 3),
('AGENT', '中介', '中介角色', 4);

-- 初始化管理员账户（密码：admin123）
INSERT INTO sys_user (username, password, nickname, user_type, status, login_fail_count) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', '系统管理员', 'ADMIN', 1, 0);

-- 关联管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 初始化测试用户（密码：123456）
INSERT INTO sys_user (username, password, nickname, phone, user_type, status, login_fail_count) VALUES
('landlord1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', '房东张三', '13800138001', 'LANDLORD', 1, 0),
('tenant1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', '租客李四', '13800138002', 'TENANT', 1, 0),
('agent1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', '中介王五', '13800138003', 'AGENT', 1, 0);

-- 关联用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES 
(2, 2),
(3, 3),
(4, 4);

-- 添加更多测试用户
INSERT INTO sys_user (username, password, nickname, real_name, phone, email, user_type, status, login_fail_count, deleted) VALUES
('landlord2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', '房东李四', '李四', '13800138004', 'landlord2@example.com', 'LANDLORD', 1, 0, 0),
('landlord3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', '房东王五', '王五', '13800138005', 'landlord3@example.com', 'LANDLORD', 1, 0, 0),
('landlord4', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', '房东赵六', '赵六', '13800138006', 'landlord4@example.com', 'LANDLORD', 1, 0, 0),
('landlord5', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', '房东钱七', '钱七', '13800138007', 'landlord5@example.com', 'LANDLORD', 1, 0, 0),
('tenant2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', '租客张三', '张三', '13800138008', 'tenant2@example.com', 'TENANT', 1, 0, 0),
('tenant3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', '租客李四', '李四', '13800138009', 'tenant3@example.com', 'TENANT', 1, 0, 0),
('tenant4', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', '租客王五', '王五', '13800138010', 'tenant4@example.com', 'TENANT', 1, 0, 0),
('tenant5', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', '租客赵六', '赵六', '13800138011', 'tenant5@example.com', 'TENANT', 1, 0, 0),
('tenant6', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', '租客钱七', '钱七', '13800138012', 'tenant6@example.com', 'TENANT', 1, 0, 0),
('tenant7', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', '租客孙八', '孙八', '13800138013', 'tenant7@example.com', 'TENANT', 1, 0, 0),
('tenant8', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', '租客周九', '周九', '13800138014', 'tenant8@example.com', 'TENANT', 1, 0, 0),
('tenant9', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', '租客吴十', '吴十', '13800138015', 'tenant9@example.com', 'TENANT', 1, 0, 0);

-- 关联用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES 
(5, 2), (6, 2), (7, 2), (8, 2),
(9, 3), (10, 3), (11, 3), (12, 3), (13, 3), (14, 3), (15, 3), (16, 3);

-- 初始化测试房源
INSERT INTO biz_house (house_no, landlord_id, title, description, province, city, district, street, address, 
    house_type, room_count, hall_count, bathroom_count, area, floor, total_floor, has_elevator, 
    decoration, orientation, rent_price, deposit_month, payment_way, rent_way, 
    facilities, contact_name, contact_phone, status, audit_status, deleted) VALUES
('H202401010001', 2, '精装修两室一厅 南北通透 拎包入住', '交通便利，地铁步行5分钟，周边配套齐全', 
    '北京市', '北京', '朝阳区', '望京街道', '阜通东大街6号院', 
    '2室1厅1卫', 2, 1, 1, 85.00, 8, 18, 1, 
    3, 2, 5500.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '张先生', '13800138001', 2, 1, 0),
('H202401010002', 2, '地铁口精装公寓 家电齐全 拎包入住', '地铁口位置，出行方便，精装修', 
    '上海市', '上海', '浦东新区', '陆家嘴', '世纪大道100号', 
    '1室1厅1卫', 1, 1, 1, 45.00, 15, 30, 1, 
    3, 2, 4200.00, 1, 2, 1, 
    '["床","沙发","空调","冰箱","洗衣机","热水器"]', '张先生', '13800138001', 2, 1, 0),
('H202401010003', 2, '温馨三居室 学区房 近地铁', '学区房，孩子上学方便，环境优美', 
    '广东省', '深圳', '南山区', '科技园', '科技园南路', 
    '3室2厅2卫', 3, 2, 2, 120.00, 12, 25, 1, 
    3, 2, 8500.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '张先生', '13800138001', 2, 1, 0),
('H202401010004', 2, '望京SOHO附近 精装两居室', '靠近望京SOHO，适合上班族，交通便利', 
    '北京市', '北京', '朝阳区', '望京街道', '望京SOHO T1', 
    '2室1厅1卫', 2, 1, 1, 90.00, 12, 20, 1, 
    3, 2, 6000.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '张先生', '13800138001', 2, 1, 0),
('H202401010005', 2, '三里屯精装公寓', '三里屯核心位置，周边配套齐全', 
    '北京市', '北京', '朝阳区', '三里屯', '三里屯SOHO', 
    '1室1厅1卫', 1, 1, 1, 50.00, 8, 15, 1, 
    3, 2, 5000.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '张先生', '13800138001', 2, 1, 0),
('H202401010006', 5, '国贸附近三居室', '国贸CBD核心位置，适合商务人士', 
    '北京市', '北京', '朝阳区', '建国门外', '建国门外大街', 
    '3室2厅2卫', 3, 2, 2, 130.00, 15, 25, 1, 
    4, 2, 12000.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带","智能家居"]', '李女士', '13800138004', 2, 1, 0),
('H202401010007', 5, '通州精装两居室', '通州核心区，交通便利，适合家庭', 
    '北京市', '北京', '通州区', '通州北苑', '通州北苑大街', 
    '2室1厅1卫', 2, 1, 1, 85.00, 6, 18, 1, 
    3, 2, 4500.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '李女士', '13800138004', 2, 1, 0),
('H202401010008', 6, '海淀中关村两居室', '靠近中关村，适合IT人士', 
    '北京市', '北京', '海淀区', '中关村', '中关村大街', 
    '2室1厅1卫', 2, 1, 1, 80.00, 10, 20, 1, 
    3, 2, 6500.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '王先生', '13800138005', 2, 1, 0),
('H202401010009', 6, '上地科技园三居室', '上地科技园附近，适合科技公司员工', 
    '北京市', '北京', '海淀区', '上地', '上地西路', 
    '3室2厅2卫', 3, 2, 2, 120.00, 8, 15, 1, 
    3, 2, 8000.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '王先生', '13800138005', 2, 1, 0),
('H202401010010', 7, '丰台丽泽商务区两居室', '丽泽商务区附近，交通便利', 
    '北京市', '北京', '丰台区', '丽泽桥', '丽泽路', 
    '2室1厅1卫', 2, 1, 1, 85.00, 5, 12, 1, 
    3, 2, 4800.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '赵先生', '13800138006', 2, 1, 0),
('H202401010011', 7, '石景山精装一居室', '石景山区核心位置，环境优美', 
    '北京市', '北京', '石景山区', '鲁谷', '鲁谷路', 
    '1室1厅1卫', 1, 1, 1, 55.00, 10, 15, 1, 
    3, 2, 3800.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '赵先生', '13800138006', 2, 1, 0),
('H202401010012', 8, '大兴亦庄三居室', '亦庄开发区附近，适合上班族', 
    '北京市', '北京', '大兴区', '亦庄', '亦庄经济技术开发区', 
    '3室2厅2卫', 3, 2, 2, 110.00, 6, 18, 1, 
    3, 2, 5500.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '钱先生', '13800138007', 2, 1, 0),
('H202401010013', 8, '昌平回龙观两居室', '回龙观文化居住区，适合家庭', 
    '北京市', '北京', '昌平区', '回龙观', '回龙观东大街', 
    '2室1厅1卫', 2, 1, 1, 80.00, 4, 12, 1, 
    3, 2, 4200.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '钱先生', '13800138007', 2, 1, 0);

-- 初始化违约金规则数据
INSERT INTO biz_penalty_rule (rule_name, rule_type, calculation_method, base_amount, percentage, min_amount, max_amount, status) VALUES
('提前解约违约金规则', 'EARLY_TERMINATION', 'RENT_MULTIPLE', 0, 0, 2000, 10000, 1),
('逾期付款违约金规则', 'LATE_PAYMENT', 'DAILY_PERCENTAGE', 0, 0.05, 50, 500, 1),
('房屋损坏赔偿规则', 'DAMAGE', 'ACTUAL_COST', 0, 0, 0, 0, 1);

-- 添加预约数据
INSERT INTO biz_appointment (appointment_no, house_id, tenant_id, landlord_id, appointment_time, status, contact_name, contact_phone, remark, deleted) VALUES
('A202401010001', 1, 9, 2, '2024-01-15 14:00:00', 1, '张三', '13800138008', '周末看房', 0),
('A202401010002', 1, 10, 2, '2024-01-16 10:00:00', 2, '李四', '13800138009', '上午看房', 0),
('A202401010003', 2, 11, 2, '2024-01-17 15:00:00', 0, '王五', '13800138010', '下午看房', 0),
('A202401010004', 3, 12, 2, '2024-01-18 11:00:00', 3, '赵六', '13800138011', '临时有事取消', 0),
('A202401010005', 4, 13, 2, '2024-01-19 16:00:00', 1, '钱七', '13800138012', '晚上看房', 0),
('A202401010006', 5, 14, 2, '2024-01-20 09:00:00', 4, '孙八', '13800138013', '时间不合适', 0),
('A202401010007', 6, 15, 5, '2024-01-21 13:00:00', 1, '周九', '13800138014', '中午看房', 0),
('A202401010008', 7, 16, 5, '2024-01-22 14:30:00', 2, '吴十', '13800138015', '下午看房', 0);

-- =============================================
-- 查询验证
-- =============================================

SELECT '用户表数据：' AS info;
SELECT id, username, nickname, user_type, status FROM sys_user;

SELECT '角色表数据：' AS info;
SELECT id, role_code, role_name FROM sys_role;

SELECT '房源表数据：' AS info;
SELECT id, house_no, title, city, district, rent_price, status FROM biz_house;

SELECT '预约表数据：' AS info;
SELECT id, appointment_no, house_id, tenant_id, landlord_id, status FROM biz_appointment;

SELECT '违约金规则数据：' AS info;
SELECT id, rule_name, rule_type, min_amount, max_amount FROM biz_penalty_rule;
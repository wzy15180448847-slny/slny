-- House-Eco 3.0 数据库初始化脚本
-- 执行前请确保数据库已创建: CREATE DATABASE IF NOT EXISTS house_eco CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 删除旧表结构（按外键依赖关系排序）
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS sys_login_log;
DROP TABLE IF EXISTS sys_role_permission;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS biz_agent_qualification;
DROP TABLE IF EXISTS sys_notification;
DROP TABLE IF EXISTS biz_complaint;
DROP TABLE IF EXISTS biz_evaluation;
DROP TABLE IF EXISTS biz_payment_record;
DROP TABLE IF EXISTS biz_wallet_transaction;
DROP TABLE IF EXISTS biz_user_wallet;
DROP TABLE IF EXISTS biz_termination_application;
DROP TABLE IF EXISTS biz_electronic_signature;
DROP TABLE IF EXISTS biz_lease_agreement;
DROP TABLE IF EXISTS biz_appointment;
DROP TABLE IF EXISTS biz_favorite;
DROP TABLE IF EXISTS biz_house;
DROP TABLE IF EXISTS biz_audit_log;
DROP TABLE IF EXISTS biz_message_record;
DROP TABLE IF EXISTS biz_rent_reminder;
DROP TABLE IF EXISTS biz_penalty_rule;
DROP TABLE IF EXISTS biz_abnormal_order;
DROP TABLE IF EXISTS sys_permission;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. sys_user (用户表)
CREATE TABLE sys_user (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    real_name VARCHAR(50) COMMENT '真实姓名',
    id_card VARCHAR(18) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    gender TINYINT(1) DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    user_type VARCHAR(20) COMMENT '用户类型：LANDLORD-房东, TENANT-租客, ADMIN-管理员',
    credit_score INT(3) DEFAULT 100 COMMENT '信用分数',
    status TINYINT(1) DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用, 2-锁定',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    login_fail_count INT(3) DEFAULT 0 COMMENT '登录失败次数',
    password_update_time DATETIME COMMENT '密码最后修改时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT(20) COMMENT '创建人',
    update_by BIGINT(20) COMMENT '更新人',
    version INT(11) DEFAULT 0 COMMENT '乐观锁版本号',
    remark VARCHAR(500) COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. sys_role (角色表)
CREATE TABLE sys_role (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_code VARCHAR(50) UNIQUE NOT NULL COMMENT '角色编码',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(200) COMMENT '角色描述',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 3. sys_permission (权限表)
CREATE TABLE sys_permission (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    perm_code VARCHAR(50) UNIQUE NOT NULL COMMENT '权限编码',
    perm_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    path VARCHAR(255) COMMENT '访问路径',
    method VARCHAR(10) COMMENT '请求方法: GET/POST/PUT/DELETE',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 4. sys_user_role (用户角色关联表)
CREATE TABLE sys_user_role (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    role_id BIGINT(20) NOT NULL COMMENT '角色ID',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (role_id) REFERENCES sys_role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 5. sys_role_permission (角色权限关联表)
CREATE TABLE sys_role_permission (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    role_id BIGINT(20) NOT NULL COMMENT '角色ID',
    permission_id BIGINT(20) NOT NULL COMMENT '权限ID',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (role_id) REFERENCES sys_role(id),
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 6. sys_login_log (登录日志表)
CREATE TABLE sys_login_log (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT(20) COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    login_location VARCHAR(100) COMMENT '登录地点',
    login_device VARCHAR(255) COMMENT '登录设备',
    status TINYINT(1) COMMENT '登录状态: 0-失败, 1-成功',
    fail_reason VARCHAR(255) COMMENT '失败原因',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- 7. biz_house (房源主表)
CREATE TABLE biz_house (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '房源ID',
    landlord_id BIGINT(20) NOT NULL COMMENT '房东ID',
    house_no VARCHAR(32) UNIQUE NOT NULL COMMENT '房源编号',
    title VARCHAR(200) NOT NULL COMMENT '房源标题',
    description TEXT COMMENT '房源描述',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    district VARCHAR(50) COMMENT '区县',
    street VARCHAR(100) COMMENT '街道',
    address VARCHAR(255) NOT NULL COMMENT '详细地址',
    longitude DECIMAL(12,8) COMMENT '经度',
    latitude DECIMAL(12,8) COMMENT '纬度',
    house_type VARCHAR(50) COMMENT '房屋类型',
    room_count INT(2) COMMENT '房间数',
    hall_count INT(2) COMMENT '客厅数',
    bathroom_count INT(2) COMMENT '卫生间数',
    area DECIMAL(10,2) COMMENT '建筑面积(㎡)',
    floor INT(4) COMMENT '楼层',
    total_floor INT(4) COMMENT '总楼层',
    has_elevator TINYINT(1) DEFAULT 0 COMMENT '是否有电梯',
    decoration TINYINT(1) COMMENT '装修情况: 1-毛坯, 2-简装, 3-中装, 4-精装',
    orientation TINYINT(1) COMMENT '朝向: 1-东, 2-南, 3-西, 4-北, 5-南北通透',
    rent_price DECIMAL(15,2) NOT NULL COMMENT '租金(元/月)',
    deposit_month INT(2) DEFAULT 1 COMMENT '押金(月)',
    payment_way TINYINT(1) DEFAULT 2 COMMENT '付款方式: 1-押一付一, 2-押一付三, 3-押一付六, 4-年付',
    rent_way TINYINT(1) DEFAULT 1 COMMENT '租赁方式: 1-整租, 2-合租',
    min_lease_term INT(3) DEFAULT 12 COMMENT '最短租期(月)',
    facilities VARCHAR(500) COMMENT '配套设施(JSON)',
    images TEXT COMMENT '图片列表(JSON)',
    cover_image VARCHAR(255) COMMENT '封面图片',
    contact_name VARCHAR(50) COMMENT '联系人姓名',
    contact_phone VARCHAR(20) COMMENT '联系人电话',
    view_time_type TINYINT(1) COMMENT '看房时间类型: 1-随时, 2-周末, 3-工作日, 4-提前预约',
    available_date DATETIME COMMENT '可入住时间',
    status TINYINT(1) DEFAULT 0 COMMENT '状态: 0-草稿, 1-待审核, 2-已发布, 3-已下架, 4-已出租',
    audit_status TINYINT(1) DEFAULT 0 COMMENT '审核状态: 0-待审核, 1-通过, 2-驳回',
    audit_remark VARCHAR(500) COMMENT '审核意见',
    auditor_id BIGINT(20) COMMENT '审核人ID',
    audit_time DATETIME COMMENT '审核时间',
    view_count INT(11) DEFAULT 0 COMMENT '浏览次数',
    favorite_count INT(11) DEFAULT 0 COMMENT '收藏次数',
    appointment_count INT(11) DEFAULT 0 COMMENT '预约次数',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT(20) COMMENT '创建人',
    update_by BIGINT(20) COMMENT '更新人',
    version INT(11) DEFAULT 0 COMMENT '乐观锁版本号',
    FOREIGN KEY (landlord_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房源主表';

-- 8. biz_favorite (收藏表)
CREATE TABLE biz_favorite (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    house_id BIGINT(20) NOT NULL COMMENT '房源ID',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (house_id) REFERENCES biz_house(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- 9. biz_appointment (预约表)
CREATE TABLE biz_appointment (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
    appointment_no VARCHAR(32) UNIQUE NOT NULL COMMENT '预约编号',
    house_id BIGINT(20) NOT NULL COMMENT '房源ID',
    tenant_id BIGINT(20) NOT NULL COMMENT '租客ID',
    landlord_id BIGINT(20) NOT NULL COMMENT '房东ID',
    appointment_time DATETIME NOT NULL COMMENT '预约时间',
    status TINYINT(1) DEFAULT 0 COMMENT '状态: 0-待确认, 1-已确认, 2-已完成, 3-已取消, 4-已拒绝',
    contact_name VARCHAR(50) COMMENT '联系人姓名',
    contact_phone VARCHAR(20) COMMENT '联系人电话',
    remark VARCHAR(255) COMMENT '备注',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT(20) COMMENT '创建人',
    update_by BIGINT(20) COMMENT '更新人',
    version INT(11) DEFAULT 0 COMMENT '乐观锁版本号',
    FOREIGN KEY (house_id) REFERENCES biz_house(id),
    FOREIGN KEY (tenant_id) REFERENCES sys_user(id),
    FOREIGN KEY (landlord_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约表';

-- 10. biz_lease_agreement (合同表)
CREATE TABLE biz_lease_agreement (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '合同ID',
    agreement_no VARCHAR(50) UNIQUE NOT NULL COMMENT '合同编号',
    house_id BIGINT(20) NOT NULL COMMENT '房源ID',
    tenant_id BIGINT(20) NOT NULL COMMENT '租客ID',
    landlord_id BIGINT(20) NOT NULL COMMENT '房东ID',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    monthly_rent DECIMAL(15,2) NOT NULL COMMENT '月租金',
    deposit DECIMAL(15,2) NOT NULL COMMENT '押金',
    status TINYINT(1) DEFAULT 0 COMMENT '状态: 0-待签, 1-生效, 2-到期, 3-终止',
    contract_url VARCHAR(255) COMMENT '合同文件URL',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (house_id) REFERENCES biz_house(id),
    FOREIGN KEY (tenant_id) REFERENCES sys_user(id),
    FOREIGN KEY (landlord_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同表';

-- 11. biz_electronic_signature (电子签章表)
CREATE TABLE biz_electronic_signature (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '签章ID',
    agreement_id BIGINT(20) NOT NULL COMMENT '合同ID',
    user_id BIGINT(20) NOT NULL COMMENT '签署用户ID',
    signature_hash VARCHAR(255) NOT NULL COMMENT '签章哈希值',
    sign_time DATETIME NOT NULL COMMENT '签署时间',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (agreement_id) REFERENCES biz_lease_agreement(id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='电子签章表';

-- 12. biz_termination_application (解约申请表)
CREATE TABLE biz_termination_application (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '解约申请ID',
    agreement_id BIGINT(20) NOT NULL COMMENT '合同ID',
    applicant_id BIGINT(20) NOT NULL COMMENT '申请人ID',
    reason VARCHAR(500) COMMENT '解约原因',
    compensation DECIMAL(15,2) COMMENT '违约金',
    status TINYINT(1) DEFAULT 0 COMMENT '状态: 0-待处理, 1-同意, 2-拒绝',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '处理时间',
    FOREIGN KEY (agreement_id) REFERENCES biz_lease_agreement(id),
    FOREIGN KEY (applicant_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='解约申请表';

-- 13. biz_user_wallet (钱包表)
CREATE TABLE biz_user_wallet (
    user_id BIGINT(20) PRIMARY KEY COMMENT '用户ID',
    balance DECIMAL(18,2) DEFAULT 0.00 COMMENT '余额',
    freeze_balance DECIMAL(18,2) DEFAULT 0.00 COMMENT '冻结余额',
    version INT(11) DEFAULT 0 COMMENT '乐观锁版本',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包表';

-- 14. biz_wallet_transaction (流水表)
CREATE TABLE biz_wallet_transaction (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '流水ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    amount DECIMAL(15,2) NOT NULL COMMENT '金额',
    type TINYINT(1) COMMENT '类型: 1-充值, 2-租金支付, 3-退款, 4-提现',
    target_id VARCHAR(50) COMMENT '关联单号',
    description VARCHAR(255) COMMENT '描述',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流水表';

-- 15. biz_payment_record (支付记录表)
CREATE TABLE biz_payment_record (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '支付记录ID',
    order_no VARCHAR(50) UNIQUE NOT NULL COMMENT '订单号',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    amount DECIMAL(15,2) NOT NULL COMMENT '支付金额',
    pay_type TINYINT(1) COMMENT '支付类型: 1-支付宝, 2-微信, 3-钱包',
    status TINYINT(1) DEFAULT 0 COMMENT '状态: 0-待支付, 1-支付成功, 2-支付失败, 3-已退款',
    third_party_no VARCHAR(100) COMMENT '第三方支付单号',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- 16. biz_evaluation (双向评价表)
CREATE TABLE biz_evaluation (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    lease_id BIGINT(20) NOT NULL COMMENT '合同ID',
    from_user_id BIGINT(20) NOT NULL COMMENT '评价人ID',
    to_user_id BIGINT(20) NOT NULL COMMENT '被评价人ID',
    score INT(1) NOT NULL COMMENT '评分: 1-5',
    content VARCHAR(500) COMMENT '评价内容',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
    FOREIGN KEY (lease_id) REFERENCES biz_lease_agreement(id),
    FOREIGN KEY (from_user_id) REFERENCES sys_user(id),
    FOREIGN KEY (to_user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='双向评价表';

-- 17. biz_complaint (投诉/报修表)
CREATE TABLE biz_complaint (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '投诉/报修ID',
    from_user_id BIGINT(20) NOT NULL COMMENT '发起用户ID',
    target_id BIGINT(20) COMMENT '目标ID(房源/合同)',
    type TINYINT(1) COMMENT '类型: 1-投诉, 2-报修',
    content VARCHAR(500) NOT NULL COMMENT '内容',
    images TEXT COMMENT '图片(JSON)',
    status TINYINT(1) DEFAULT 0 COMMENT '状态: 0-待办, 1-处理中, 2-完成',
    handle_result VARCHAR(500) COMMENT '处理结果',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (from_user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投诉/报修表';

-- 18. sys_notification (站内通知表)
CREATE TABLE sys_notification (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID',
    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content VARCHAR(500) NOT NULL COMMENT '通知内容',
    read_status TINYINT(1) DEFAULT 0 COMMENT '阅读状态: 0-未读, 1-已读',
    link_url VARCHAR(255) COMMENT '链接地址',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内通知表';

-- 19. biz_agent_qualification (资质审核表)
CREATE TABLE biz_agent_qualification (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '资质ID',
    user_id BIGINT(20) UNIQUE NOT NULL COMMENT '用户ID',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    id_card VARCHAR(18) NOT NULL COMMENT '身份证号',
    id_card_front VARCHAR(255) COMMENT '身份证正面',
    id_card_back VARCHAR(255) COMMENT '身份证反面',
    business_license VARCHAR(255) COMMENT '营业执照',
    status TINYINT(1) DEFAULT 0 COMMENT '审核状态: 0-待审核, 1-通过, 2-驳回',
    reject_reason VARCHAR(255) COMMENT '驳回原因',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '审核时间',
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资质审核表';

-- 20. biz_audit_log (审核日志表)
CREATE TABLE biz_audit_log (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    house_id BIGINT(20) NOT NULL COMMENT '房源ID',
    auditor_id BIGINT(20) COMMENT '审核人ID',
    auditor_name VARCHAR(50) COMMENT '审核人姓名',
    before_status TINYINT(1) COMMENT '审核前状态',
    after_status TINYINT(1) COMMENT '审核后状态',
    before_audit_status TINYINT(1) COMMENT '审核前审核状态',
    after_audit_status TINYINT(1) COMMENT '审核后审核状态',
    audit_remark VARCHAR(500) COMMENT '审核意见',
    audit_result TINYINT(1) COMMENT '审核结果: 0-待审核, 1-通过, 2-驳回',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
    FOREIGN KEY (house_id) REFERENCES biz_house(id),
    FOREIGN KEY (auditor_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审核日志表';

-- 21. biz_message_record (消息记录表)
CREATE TABLE biz_message_record (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    sender_id BIGINT(20) COMMENT '发送人ID',
    receiver_id BIGINT(20) COMMENT '接收人ID',
    message_type TINYINT(1) COMMENT '消息类型: 1-系统消息, 2-通知消息, 3-私信',
    title VARCHAR(100) COMMENT '消息标题',
    content VARCHAR(500) COMMENT '消息内容',
    send_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    read_status TINYINT(1) DEFAULT 0 COMMENT '阅读状态: 0-未读, 1-已读',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    FOREIGN KEY (sender_id) REFERENCES sys_user(id),
    FOREIGN KEY (receiver_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息记录表';

-- 22. biz_rent_reminder (租金提醒表)
CREATE TABLE biz_rent_reminder (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '提醒ID',
    agreement_id BIGINT(20) NOT NULL COMMENT '合同ID',
    tenant_id BIGINT(20) NOT NULL COMMENT '租客ID',
    landlord_id BIGINT(20) NOT NULL COMMENT '房东ID',
    reminder_date DATE NOT NULL COMMENT '提醒日期',
    rent_amount DECIMAL(15,2) NOT NULL COMMENT '租金金额',
    status TINYINT(1) DEFAULT 0 COMMENT '状态: 0-待提醒, 1-已提醒, 2-已支付',
    reminder_type TINYINT(1) COMMENT '提醒类型: 1-短信, 2-邮件, 3-站内通知',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (agreement_id) REFERENCES biz_lease_agreement(id),
    FOREIGN KEY (tenant_id) REFERENCES sys_user(id),
    FOREIGN KEY (landlord_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租金提醒表';

-- 23. biz_penalty_rule (处罚规则表)
CREATE TABLE biz_penalty_rule (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '规则ID',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_code VARCHAR(50) UNIQUE NOT NULL COMMENT '规则编码',
    description VARCHAR(500) COMMENT '规则描述',
    penalty_type TINYINT(1) COMMENT '处罚类型: 1-信用扣分, 2-金额罚款, 3-账号封禁',
    penalty_value DECIMAL(10,2) COMMENT '处罚值',
    min_amount DECIMAL(15,2) COMMENT '最低触发金额',
    max_amount DECIMAL(15,2) COMMENT '最高触发金额',
    status TINYINT(1) DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='处罚规则表';

-- 24. biz_abnormal_order (异常订单表)
CREATE TABLE biz_abnormal_order (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT COMMENT '异常订单ID',
    order_type TINYINT(1) COMMENT '订单类型: 1-支付订单, 2-合同订单',
    order_id BIGINT(20) NOT NULL COMMENT '关联订单ID',
    order_no VARCHAR(50) COMMENT '订单编号',
    abnormal_type TINYINT(1) COMMENT '异常类型: 1-超时未支付, 2-重复支付, 3-金额异常, 4-状态异常',
    status TINYINT(1) DEFAULT 0 COMMENT '处理状态: 0-待处理, 1-处理中, 2-已解决, 3-已忽略',
    handle_result VARCHAR(500) COMMENT '处理结果',
    handler_id BIGINT(20) COMMENT '处理人ID',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '处理时间',
    FOREIGN KEY (handler_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='异常订单表';

-- 初始化基础数据
INSERT INTO sys_role (role_code, role_name, description) VALUES 
('ADMIN', '管理员', '系统管理员'),
('LANDLORD', '房东', '房屋所有者'),
('TENANT', '租客', '房屋承租人');

INSERT INTO sys_permission (perm_code, perm_name, path, method) VALUES 
('admin:dashboard', '数据大屏', '/admin/dashboard', 'GET'),
('admin:houses', '房源管理', '/admin/houses', 'GET'),
('admin:house:audit', '房源审核', '/admin/house-audit', 'GET'),
('admin:users', '用户管理', '/admin/users', 'GET'),
('admin:qualification', '资质审核', '/admin/qualification', 'GET'),
('admin:credit', '信用管理', '/admin/credit', 'GET'),
('admin:logs', '系统日志', '/admin/logs', 'GET'),
('landlord:houses', '房源管理', '/landlord/houses', 'GET'),
('landlord:house:add', '发布房源', '/landlord/houses/add', 'POST'),
('landlord:appointments', '预约管理', '/landlord/appointments', 'GET'),
('landlord:contracts', '合同管理', '/landlord/contracts', 'GET'),
('landlord:bills', '账单管理', '/landlord/bills', 'GET'),
('tenant:houses', '房源浏览', '/tenant', 'GET'),
('tenant:appointments', '预约管理', '/tenant/appointments', 'GET'),
('tenant:contracts', '合同管理', '/tenant/contracts', 'GET'),
('tenant:bills', '账单管理', '/tenant/bills', 'GET'),
('tenant:wallet', '钱包管理', '/tenant/wallet', 'GET');

-- 初始化管理员账号
INSERT INTO sys_user (username, password, real_name, user_type, status) VALUES 
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '系统管理员', 'ADMIN', 1);

-- 关联管理员角色权限
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

INSERT INTO sys_role_permission (role_id, permission_id) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7),
(2, 8), (2, 9), (2, 10), (2, 11), (2, 12),
(3, 13), (3, 14), (3, 15), (3, 16), (3, 17);
-- =============================================
-- 房屋租赁平台数据库初始化脚本
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
-- 业务表
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
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'ADMIN', 1, 0);

-- 关联管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 初始化测试用户（密码：123456）
INSERT INTO sys_user (username, password, nickname, phone, user_type, status, login_fail_count) VALUES
('landlord1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '房东张三', '13800138001', 'LANDLORD', 1, 0),
('tenant1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '租客李四', '13800138002', 'TENANT', 1, 0),
('agent1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '中介王五', '13800138003', 'AGENT', 1, 0);

-- 关联用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES 
(2, 2),
(3, 3),
(4, 4);

-- 初始化测试房源
INSERT INTO biz_house (house_no, landlord_id, title, description, province, city, district, street, address, 
    house_type, room_count, hall_count, bathroom_count, area, floor, total_floor, has_elevator, 
    decoration, orientation, rent_price, deposit_month, payment_way, rent_way, 
    facilities, contact_name, contact_phone, status, audit_status) VALUES
('H202401010001', 2, '精装修两室一厅 南北通透 拎包入住', '交通便利，地铁步行5分钟，周边配套齐全', 
    '北京市', '北京', '朝阳区', '望京街道', '阜通东大街6号院', 
    '2室1厅1卫', 2, 1, 1, 85.00, 8, 18, 1, 
    3, 2, 5500.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '张先生', '13800138001', 2, 1),
('H202401010002', 2, '地铁口精装公寓 家电齐全 拎包入住', '地铁口位置，出行方便，精装修', 
    '上海市', '上海', '浦东新区', '陆家嘴', '世纪大道100号', 
    '1室1厅1卫', 1, 1, 1, 45.00, 15, 30, 1, 
    3, 2, 4200.00, 1, 2, 1, 
    '["床","沙发","空调","冰箱","洗衣机","热水器"]', '张先生', '13800138001', 2, 1),
('H202401010003', 2, '温馨三居室 学区房 近地铁', '学区房，孩子上学方便，环境优美', 
    '广东省', '深圳', '南山区', '科技园', '科技园南路', 
    '3室2厅2卫', 3, 2, 2, 120.00, 12, 25, 1, 
    3, 2, 8500.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '张先生', '13800138001', 2, 1);

-- =============================================
-- 查询验证
-- =============================================

SELECT '用户表数据：' AS info;
SELECT id, username, nickname, user_type, status FROM sys_user;

SELECT '角色表数据：' AS info;
SELECT id, role_code, role_name FROM sys_role;

SELECT '房源表数据：' AS info;
SELECT id, house_no, title, city, district, rent_price, status FROM biz_house;

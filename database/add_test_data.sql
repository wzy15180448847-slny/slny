-- =============================================
-- 房屋租赁平台测试数据脚本
-- =============================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET collation_connection = utf8mb4_unicode_ci;

USE house_rental;

-- =============================================
-- 添加更多测试用户
-- =============================================

-- 添加房东用户
INSERT INTO sys_user (username, password, nickname, real_name, phone, email, user_type, status, login_fail_count, deleted) VALUES
('landlord2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '房东李四', '李四', '13800138004', 'landlord2@example.com', 'LANDLORD', 1, 0, 0)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO sys_user (username, password, nickname, real_name, phone, email, user_type, status, login_fail_count, deleted) VALUES
('landlord3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '房东王五', '王五', '13800138005', 'landlord3@example.com', 'LANDLORD', 1, 0, 0)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO sys_user (username, password, nickname, real_name, phone, email, user_type, status, login_fail_count, deleted) VALUES
('landlord4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '房东赵六', '赵六', '13800138006', 'landlord4@example.com', 'LANDLORD', 1, 0, 0)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO sys_user (username, password, nickname, real_name, phone, email, user_type, status, login_fail_count, deleted) VALUES
('landlord5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '房东钱七', '钱七', '13800138007', 'landlord5@example.com', 'LANDLORD', 1, 0, 0)
ON DUPLICATE KEY UPDATE username = username;

-- 添加租客用户
INSERT INTO sys_user (username, password, nickname, real_name, phone, email, user_type, status, login_fail_count, deleted) VALUES
('tenant2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '租客张三', '张三', '13800138008', 'tenant2@example.com', 'TENANT', 1, 0, 0)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO sys_user (username, password, nickname, real_name, phone, email, user_type, status, login_fail_count, deleted) VALUES
('tenant3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '租客李四', '李四', '13800138009', 'tenant3@example.com', 'TENANT', 1, 0, 0)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO sys_user (username, password, nickname, real_name, phone, email, user_type, status, login_fail_count, deleted) VALUES
('tenant4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '租客王五', '王五', '13800138010', 'tenant4@example.com', 'TENANT', 1, 0, 0)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO sys_user (username, password, nickname, real_name, phone, email, user_type, status, login_fail_count, deleted) VALUES
('tenant5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '租客赵六', '赵六', '13800138011', 'tenant5@example.com', 'TENANT', 1, 0, 0)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO sys_user (username, password, nickname, real_name, phone, email, user_type, status, login_fail_count, deleted) VALUES
('tenant6', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '租客钱七', '钱七', '13800138012', 'tenant6@example.com', 'TENANT', 1, 0, 0)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO sys_user (username, password, nickname, real_name, phone, email, user_type, status, login_fail_count, deleted) VALUES
('tenant7', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '租客孙八', '孙八', '13800138013', 'tenant7@example.com', 'TENANT', 1, 0, 0)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO sys_user (username, password, nickname, real_name, phone, email, user_type, status, login_fail_count, deleted) VALUES
('tenant8', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '租客周九', '周九', '13800138014', 'tenant8@example.com', 'TENANT', 1, 0, 0)
ON DUPLICATE KEY UPDATE username = username;

INSERT INTO sys_user (username, password, nickname, real_name, phone, email, user_type, status, login_fail_count, deleted) VALUES
('tenant9', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '租客吴十', '吴十', '13800138015', 'tenant9@example.com', 'TENANT', 1, 0, 0)
ON DUPLICATE KEY UPDATE username = username;

-- 关联用户角色
INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES 
(5, 2), -- landlord2 -> LANDLORD
(6, 2), -- landlord3 -> LANDLORD
(7, 2), -- landlord4 -> LANDLORD
(8, 2), -- landlord5 -> LANDLORD
(9, 3),  -- tenant2 -> TENANT
(10, 3), -- tenant3 -> TENANT
(11, 3), -- tenant4 -> TENANT
(12, 3), -- tenant5 -> TENANT
(13, 3), -- tenant6 -> TENANT
(14, 3), -- tenant7 -> TENANT
(15, 3), -- tenant8 -> TENANT
(16, 3); -- tenant9 -> TENANT

-- =============================================
-- 添加更多测试房源
-- =============================================

-- 房东1的房源
INSERT INTO biz_house (house_no, landlord_id, title, description, province, city, district, street, address, 
    house_type, room_count, hall_count, bathroom_count, area, floor, total_floor, has_elevator, 
    decoration, orientation, rent_price, deposit_month, payment_way, rent_way, 
    facilities, contact_name, contact_phone, status, audit_status, deleted) VALUES
('H202401010004', 2, '望京SOHO附近 精装两居室', '靠近望京SOHO，适合上班族，交通便利', 
    '北京市', '北京', '朝阳区', '望京街道', '望京SOHO T1', 
    '2室1厅1卫', 2, 1, 1, 90.00, 12, 20, 1, 
    3, 2, 6000.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '张先生', '13800138001', 2, 1, 0)
ON DUPLICATE KEY UPDATE house_no = house_no;

INSERT INTO biz_house (house_no, landlord_id, title, description, province, city, district, street, address, 
    house_type, room_count, hall_count, bathroom_count, area, floor, total_floor, has_elevator, 
    decoration, orientation, rent_price, deposit_month, payment_way, rent_way, 
    facilities, contact_name, contact_phone, status, audit_status, deleted) VALUES
('H202401010005', 2, '三里屯精装公寓', '三里屯核心位置，周边配套齐全', 
    '北京市', '北京', '朝阳区', '三里屯', '三里屯SOHO', 
    '1室1厅1卫', 1, 1, 1, 50.00, 8, 15, 1, 
    3, 2, 5000.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '张先生', '13800138001', 2, 1, 0)
ON DUPLICATE KEY UPDATE house_no = house_no;

-- 房东2的房源
INSERT INTO biz_house (house_no, landlord_id, title, description, province, city, district, street, address, 
    house_type, room_count, hall_count, bathroom_count, area, floor, total_floor, has_elevator, 
    decoration, orientation, rent_price, deposit_month, payment_way, rent_way, 
    facilities, contact_name, contact_phone, status, audit_status, deleted) VALUES
('H202401010006', 5, '国贸附近三居室', '国贸CBD核心位置，适合商务人士', 
    '北京市', '北京', '朝阳区', '建国门外', '建国门外大街', 
    '3室2厅2卫', 3, 2, 2, 130.00, 15, 25, 1, 
    4, 2, 12000.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带","智能家居"]', '李女士', '13800138004', 2, 1, 0)
ON DUPLICATE KEY UPDATE house_no = house_no;

INSERT INTO biz_house (house_no, landlord_id, title, description, province, city, district, street, address, 
    house_type, room_count, hall_count, bathroom_count, area, floor, total_floor, has_elevator, 
    decoration, orientation, rent_price, deposit_month, payment_way, rent_way, 
    facilities, contact_name, contact_phone, status, audit_status, deleted) VALUES
('H202401010007', 5, '通州精装两居室', '通州核心区，交通便利，适合家庭', 
    '北京市', '北京', '通州区', '通州北苑', '通州北苑大街', 
    '2室1厅1卫', 2, 1, 1, 85.00, 6, 18, 1, 
    3, 2, 4500.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '李女士', '13800138004', 2, 1, 0)
ON DUPLICATE KEY UPDATE house_no = house_no;

-- 房东3的房源
INSERT INTO biz_house (house_no, landlord_id, title, description, province, city, district, street, address, 
    house_type, room_count, hall_count, bathroom_count, area, floor, total_floor, has_elevator, 
    decoration, orientation, rent_price, deposit_month, payment_way, rent_way, 
    facilities, contact_name, contact_phone, status, audit_status, deleted) VALUES
('H202401010008', 6, '海淀中关村两居室', '靠近中关村，适合IT人士', 
    '北京市', '北京', '海淀区', '中关村', '中关村大街', 
    '2室1厅1卫', 2, 1, 1, 80.00, 10, 20, 1, 
    3, 2, 6500.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '王先生', '13800138005', 2, 1, 0)
ON DUPLICATE KEY UPDATE house_no = house_no;

INSERT INTO biz_house (house_no, landlord_id, title, description, province, city, district, street, address, 
    house_type, room_count, hall_count, bathroom_count, area, floor, total_floor, has_elevator, 
    decoration, orientation, rent_price, deposit_month, payment_way, rent_way, 
    facilities, contact_name, contact_phone, status, audit_status, deleted) VALUES
('H202401010009', 6, '上地科技园三居室', '上地科技园附近，适合科技公司员工', 
    '北京市', '北京', '海淀区', '上地', '上地西路', 
    '3室2厅2卫', 3, 2, 2, 120.00, 8, 15, 1, 
    3, 2, 8000.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '王先生', '13800138005', 2, 1, 0)
ON DUPLICATE KEY UPDATE house_no = house_no;

-- 房东4的房源
INSERT INTO biz_house (house_no, landlord_id, title, description, province, city, district, street, address, 
    house_type, room_count, hall_count, bathroom_count, area, floor, total_floor, has_elevator, 
    decoration, orientation, rent_price, deposit_month, payment_way, rent_way, 
    facilities, contact_name, contact_phone, status, audit_status, deleted) VALUES
('H202401010010', 7, '丰台丽泽商务区两居室', '丽泽商务区附近，交通便利', 
    '北京市', '北京', '丰台区', '丽泽桥', '丽泽路', 
    '2室1厅1卫', 2, 1, 1, 85.00, 5, 12, 1, 
    3, 2, 4800.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '赵先生', '13800138006', 2, 1, 0)
ON DUPLICATE KEY UPDATE house_no = house_no;

INSERT INTO biz_house (house_no, landlord_id, title, description, province, city, district, street, address, 
    house_type, room_count, hall_count, bathroom_count, area, floor, total_floor, has_elevator, 
    decoration, orientation, rent_price, deposit_month, payment_way, rent_way, 
    facilities, contact_name, contact_phone, status, audit_status, deleted) VALUES
('H202401010011', 7, '石景山精装一居室', '石景山区核心位置，环境优美', 
    '北京市', '北京', '石景山区', '鲁谷', '鲁谷路', 
    '1室1厅1卫', 1, 1, 1, 55.00, 10, 15, 1, 
    3, 2, 3800.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '赵先生', '13800138006', 2, 1, 0)
ON DUPLICATE KEY UPDATE house_no = house_no;

-- 房东5的房源
INSERT INTO biz_house (house_no, landlord_id, title, description, province, city, district, street, address, 
    house_type, room_count, hall_count, bathroom_count, area, floor, total_floor, has_elevator, 
    decoration, orientation, rent_price, deposit_month, payment_way, rent_way, 
    facilities, contact_name, contact_phone, status, audit_status, deleted) VALUES
('H202401010012', 8, '大兴亦庄三居室', '亦庄开发区附近，适合上班族', 
    '北京市', '北京', '大兴区', '亦庄', '亦庄经济技术开发区', 
    '3室2厅2卫', 3, 2, 2, 110.00, 6, 18, 1, 
    3, 2, 5500.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '钱先生', '13800138007', 2, 1, 0)
ON DUPLICATE KEY UPDATE house_no = house_no;

INSERT INTO biz_house (house_no, landlord_id, title, description, province, city, district, street, address, 
    house_type, room_count, hall_count, bathroom_count, area, floor, total_floor, has_elevator, 
    decoration, orientation, rent_price, deposit_month, payment_way, rent_way, 
    facilities, contact_name, contact_phone, status, audit_status, deleted) VALUES
('H202401010013', 8, '昌平回龙观两居室', '回龙观文化居住区，适合家庭', 
    '北京市', '北京', '昌平区', '回龙观', '回龙观东大街', 
    '2室1厅1卫', 2, 1, 1, 80.00, 4, 12, 1, 
    3, 2, 4200.00, 1, 2, 1, 
    '["床","沙发","电视","空调","冰箱","洗衣机","热水器","宽带"]', '钱先生', '13800138007', 2, 1, 0)
ON DUPLICATE KEY UPDATE house_no = house_no;

-- =============================================
-- 添加预约看房数据
-- =============================================

-- 创建预约表（如果不存在）
CREATE TABLE IF NOT EXISTS biz_appointment (
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

-- 添加预约数据
INSERT INTO biz_appointment (appointment_no, house_id, tenant_id, landlord_id, appointment_time, status, contact_name, contact_phone, remark, deleted) VALUES
('A202401010001', 1, 9, 2, '2024-01-15 14:00:00', 1, '张三', '13800138008', '周末看房', 0)
ON DUPLICATE KEY UPDATE appointment_no = appointment_no;

INSERT INTO biz_appointment (appointment_no, house_id, tenant_id, landlord_id, appointment_time, status, contact_name, contact_phone, remark, deleted) VALUES
('A202401010002', 1, 10, 2, '2024-01-16 10:00:00', 2, '李四', '13800138009', '上午看房', 0)
ON DUPLICATE KEY UPDATE appointment_no = appointment_no;

INSERT INTO biz_appointment (appointment_no, house_id, tenant_id, landlord_id, appointment_time, status, contact_name, contact_phone, remark, deleted) VALUES
('A202401010003', 2, 11, 2, '2024-01-17 15:00:00', 0, '王五', '13800138010', '下午看房', 0)
ON DUPLICATE KEY UPDATE appointment_no = appointment_no;

INSERT INTO biz_appointment (appointment_no, house_id, tenant_id, landlord_id, appointment_time, status, contact_name, contact_phone, remark, deleted) VALUES
('A202401010004', 3, 12, 2, '2024-01-18 11:00:00', 3, '赵六', '13800138011', '临时有事取消', 0)
ON DUPLICATE KEY UPDATE appointment_no = appointment_no;

INSERT INTO biz_appointment (appointment_no, house_id, tenant_id, landlord_id, appointment_time, status, contact_name, contact_phone, remark, deleted) VALUES
('A202401010005', 4, 13, 2, '2024-01-19 16:00:00', 1, '钱七', '13800138012', '晚上看房', 0)
ON DUPLICATE KEY UPDATE appointment_no = appointment_no;

INSERT INTO biz_appointment (appointment_no, house_id, tenant_id, landlord_id, appointment_time, status, contact_name, contact_phone, remark, deleted) VALUES
('A202401010006', 5, 14, 2, '2024-01-20 09:00:00', 4, '孙八', '13800138013', '时间不合适', 0)
ON DUPLICATE KEY UPDATE appointment_no = appointment_no;

INSERT INTO biz_appointment (appointment_no, house_id, tenant_id, landlord_id, appointment_time, status, contact_name, contact_phone, remark, deleted) VALUES
('A202401010007', 6, 15, 5, '2024-01-21 13:00:00', 1, '周九', '13800138014', '中午看房', 0)
ON DUPLICATE KEY UPDATE appointment_no = appointment_no;

INSERT INTO biz_appointment (appointment_no, house_id, tenant_id, landlord_id, appointment_time, status, contact_name, contact_phone, remark, deleted) VALUES
('A202401010008', 7, 16, 5, '2024-01-22 14:30:00', 2, '吴十', '13800138015', '下午看房', 0)
ON DUPLICATE KEY UPDATE appointment_no = appointment_no;

-- =============================================
-- 查询验证
-- =============================================

SELECT '新增用户数据：' AS info;
SELECT id, username, nickname, user_type, status FROM sys_user WHERE id > 4;

SELECT '新增房源数据：' AS info;
SELECT id, house_no, title, city, district, rent_price, status FROM biz_house WHERE id > 3;

SELECT '预约数据：' AS info;
SELECT id, appointment_no, house_id, tenant_id, landlord_id, appointment_time, status FROM biz_appointment;

-- =====================================================
-- House-Eco 全面测试数据集 v2
-- 覆盖系统所有核心功能模块
-- =====================================================

USE house_eco;

SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 1. 用户数据补充 (确保有足够的租客和房东)
-- =====================================================

-- 确保有管理员账户
INSERT IGNORE INTO sys_user (id, username, password, real_name, phone, email, user_type, status, credit_score, create_time, is_deleted, version) VALUES
(1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '系统管理员', '13700000000', 'admin@test.com', 'ADMIN', 1, 100, '2026-04-01 00:00:00', 0, 1);

-- 补充租客 (确保有足够多的租客)
INSERT IGNORE INTO sys_user (id, username, password, real_name, phone, email, user_type, status, credit_score, nickname, create_time, is_deleted, version) VALUES
(1010, 'tenant8', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '吴十', '13800138010', 'tenant8@test.com', 'TENANT', 1, 78, '小吴', '2026-04-26 10:00:00', 0, 1),
(1011, 'tenant9', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '郑十一', '13800138011', 'tenant9@test.com', 'TENANT', 1, 82, '老郑', '2026-04-27 11:00:00', 0, 1),
(1012, 'tenant10', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '王十二', '13800138012', 'tenant10@test.com', 'TENANT', 1, 91, '小王', '2026-04-28 09:00:00', 0, 1),
(1013, 'tenant11', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '冯十三', '13800138013', 'tenant11@test.com', 'TENANT', 1, 87, '老冯', '2026-04-29 14:00:00', 0, 1),
(1014, 'tenant12', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '陈十四', '13800138014', 'tenant12@test.com', 'TENANT', 1, 72, '小陈', '2026-04-30 16:00:00', 0, 1),
(1015, 'tenant13', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '褚十五', '13800138015', 'tenant13@test.com', 'TENANT', 1, 93, '小褚', '2026-05-01 10:00:00', 0, 1),
(1016, 'tenant14', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '卫十六', '13800138016', 'tenant14@test.com', 'TENANT', 1, 89, '老卫', '2026-05-02 09:00:00', 0, 1),
(1017, 'tenant15', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '蒋十七', '13800138017', 'tenant15@test.com', 'TENANT', 1, 84, '小蒋', '2026-05-03 15:00:00', 0, 1);

-- 补充房东 (每个房东拥有多个房源)
INSERT IGNORE INTO sys_user (id, username, password, real_name, phone, email, user_type, status, credit_score, nickname, create_time, is_deleted, version) VALUES
(1018, 'landlord4', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '房东杨', '13900139003', 'landlord4@test.com', 'LANDLORD', 1, 88, '杨姐', '2026-04-25 10:00:00', 0, 1),
(1019, 'landlord5', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '房东朱', '13900139004', 'landlord5@test.com', 'LANDLORD', 1, 92, '朱哥', '2026-04-26 12:00:00', 0, 1),
(1020, 'landlord6', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '房东秦', '13900139005', 'landlord6@test.com', 'LANDLORD', 1, 85, '秦姐', '2026-04-27 14:00:00', 0, 1),
(1021, 'landlord7', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '房东尤', '13900139006', 'landlord7@test.com', 'LANDLORD', 1, 90, '尤哥', '2026-04-28 16:00:00', 0, 1),
(1022, 'landlord8', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '房东许', '13900139007', 'landlord8@test.com', 'LANDLORD', 1, 78, '许姐', '2026-04-29 18:00:00', 0, 1),
(1023, 'landlord9', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '房东何', '13900139008', 'landlord9@test.com', 'LANDLORD', 1, 95, '何哥', '2026-04-30 10:00:00', 0, 1),
(1024, 'landlord10', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '房东吕', '13900139009', 'landlord10@test.com', 'LANDLORD', 1, 82, '吕姐', '2026-05-01 12:00:00', 0, 1);

-- =====================================================
-- 2. 房源数据 (每个房东有5-20处房源)
-- =====================================================

-- landlord2 (陈姐) 的房源 - 8处
INSERT IGNORE INTO biz_house (id, landlord_id, house_no, title, description, province, city, district, address, longitude, latitude, house_type, room_count, hall_count, bathroom_count, area, floor, total_floor, has_elevator, decoration, orientation, rent_price, deposit_month, payment_way, rent_way, min_lease_term, facilities, contact_name, contact_phone, view_time_type, available_date, house_status, audit_status, view_count, create_time, is_deleted, version) VALUES
(2001, 1008, 'HS2024050001', '朝阳区精装两居室 近地铁', '精装修，南北通透，家具家电齐全，拎包入住', '北京市', '北京市', '朝阳区', '建国路88号院 3号楼2单元1201', 116.46, 39.92, '公寓', 2, 1, 1, 85.50, 12, 25, 1, 4, 2, 6800.00, 1, 2, 1, 12, '["空调","洗衣机","冰箱","热水器","宽带","天然气"]', '陈姐', '13900139001', 1, '2026-05-15', 0, 1, 320, '2026-04-20 15:30:00', 0, 1),
(2002, 1008, 'HS2024050002', '海淀区学区房一居室', '中关村一小旁，适合陪读家庭，交通便利', '北京市', '北京市', '海淀区', '中关村大街100号院 1号楼3单元302', 116.32, 39.98, '普通住宅', 1, 1, 1, 55.00, 3, 6, 0, 3, 2, 4500.00, 2, 2, 1, 12, '["空调","洗衣机","冰箱","热水器"]', '陈姐', '13900139001', 4, '2026-05-20', 0, 1, 280, '2026-04-21 10:00:00', 0, 1),
(2003, 1008, 'HS2024050003', '西城区豪华三居室', '位于金融街商圈，精装交付，适合白领家庭', '北京市', '北京市', '西城区', '金融街甲8号院 5号楼1单元801', 116.36, 39.91, '普通住宅', 3, 2, 2, 130.00, 8, 18, 1, 4, 2, 12000.00, 1, 3, 1, 24, '["空调","洗衣机","冰箱","热水器","宽带","天然气","车位"]', '陈姐', '13900139001', 2, '2026-06-01', 0, 1, 150, '2026-04-22 14:00:00', 0, 1),
(2004, 1008, 'HS2024050004', '东城区老胡同四合院单间', '传统四合院，古色古香，独门独户', '北京市', '北京市', '东城区', '南锣鼓巷25号院', 116.40, 39.93, '四合院', 1, 0, 1, 35.00, 1, 1, 0, 2, 2, 3800.00, 1, 2, 1, 6, '["空调","热水器"]', '陈姐', '13900139001', 1, '2026-05-10', 0, 1, 420, '2026-04-23 09:00:00', 0, 1),
(2005, 1008, 'HS2024050005', '丰台区地铁旁两居室', '9号线丰台南路站步行5分钟，交通便利', '北京市', '北京市', '丰台区', '看丹路12号院 7号楼4单元602', 116.29, 39.85, '普通住宅', 2, 1, 1, 72.00, 6, 15, 1, 3, 5, 4200.00, 1, 1, 1, 12, '["空调","洗衣机","冰箱","热水器","宽带"]', '陈姐', '13900139001', 3, '2026-05-08', 1, 1, 180, '2026-04-24 16:00:00', 0, 1),
(2006, 1008, 'HS2024050006', '石景山区LOFT公寓', '挑高4.5米，下层客厅上层卧室，时尚装修', '北京市', '北京市', '石景山区', '古城大街66号院 2号楼2单元301', 116.22, 39.90, '公寓', 1, 1, 1, 65.00, 3, 8, 1, 4, 1, 5200.00, 1, 2, 2, 12, '["空调","洗衣机","冰箱","热水器","宽带","天然气"]', '陈姐', '13900139001', 1, '2026-05-12', 0, 1, 250, '2026-04-25 11:00:00', 0, 1),
(2007, 1008, 'HS2024050007', '通州区运河旁三居室', '运河核心区，河景房，环境优美', '北京市', '北京市', '通州区', '新华西街188号院 11号楼2单元1501', 116.66, 39.91, '普通住宅', 3, 2, 2, 110.00, 15, 28, 1, 3, 2, 5800.00, 2, 2, 1, 12, '["空调","洗衣机","冰箱","热水器","宽带","天然气"]', '陈姐', '13900139001', 2, '2026-05-25', 0, 1, 120, '2026-04-26 13:00:00', 0, 1),
(2008, 1008, 'HS2024050008', '大兴区精装单身公寓', '亦庄经济开发区，适合单身白领', '北京市', '北京市', '大兴区', '荣华中路8号院 5号楼3单元805', 116.50, 39.78, '公寓', 1, 1, 1, 45.00, 8, 18, 1, 4, 2, 3500.00, 1, 1, 2, 6, '["空调","洗衣机","冰箱","热水器","宽带"]', '陈姐', '13900139001', 1, '2026-05-05', 0, 1, 380, '2026-04-27 17:00:00', 0, 1);

-- =====================================================
-- 3. 预约数据 (20+条，覆盖各种状态)
-- =====================================================

-- 待确认的预约 (8条)
INSERT IGNORE INTO biz_appointment (id, appointment_no, house_id, tenant_id, landlord_id, appointment_time, status, contact_name, contact_phone, remark, create_time, is_deleted, version) VALUES
(3001, 'APT20240501001', 2001, 1001, 1008, '2026-05-08 14:00:00', 0, '张三', '13800138001', '希望看一下房间的采光情况', '2026-05-03 10:00:00', 0, 1),
(3002, 'APT20240501002', 2009, 1002, 1009, '2026-05-09 10:00:00', 0, '李四', '13800138002', '周末有空，想上午看房', '2026-05-04 14:00:00', 0, 1),
(3003, 'APT20240501003', 2019, 1003, 1018, '2026-05-10 16:00:00', 0, '王五', '13800138003', '下午下班过去看房', '2026-05-05 09:00:00', 0, 1);

-- =====================================================
-- 4. 合同数据
-- =====================================================

INSERT IGNORE INTO biz_lease_agreement (id, contract_no, house_id, tenant_id, landlord_id, start_date, end_date, rent_price, deposit, payment_way, status, create_time, is_deleted) VALUES
(4001, 'CON20240501001', 2001, 1001, 1008, '2026-05-15', '2027-05-14', 6800.00, 6800.00, 2, 0, '2026-05-05 10:00:00', 0),
(4002, 'CON20240501002', 2009, 1002, 1009, '2026-05-20', '2027-05-19', 12000.00, 24000.00, 2, 0, '2026-05-06 14:00:00', 0);

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 测试数据创建完成！
-- =====================================================

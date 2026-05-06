-- =====================================================
-- House-Eco 全面测试数据集 v3
-- 覆盖系统所有核心功能模块
-- =====================================================

USE house_eco;

SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 1. 用户数据补充 (确保有足够的租客和房东)
-- =====================================================

-- 确保有管理员账户
INSERT IGNORE INTO sys_user (id, username, password, real_name, phone, email, user_type, status, credit_score, avatar, create_time, update_time, is_deleted, version) VALUES
(1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '系统管理员', '13700000000', 'admin@test.com', 'ADMIN', 1, 100, '', '2026-04-01 00:00:00', '2026-04-01 00:00:00', 0, 1);

-- 补充租客 (确保有足够多的租客)
INSERT IGNORE INTO sys_user (id, username, password, real_name, phone, email, user_type, status, credit_score, nickname, create_time, update_time, is_deleted, version) VALUES
(1010, 'tenant8', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '吴十', '13800138010', 'tenant8@test.com', 'TENANT', 1, 78, '小吴', '2026-04-26 10:00:00', '2026-04-26 10:00:00', 0, 1),
(1011, 'tenant9', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '郑十一', '13800138011', 'tenant9@test.com', 'TENANT', 1, 82, '老郑', '2026-04-27 11:00:00', '2026-04-27 11:00:00', 0, 1),
(1012, 'tenant10', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '王十二', '13800138012', 'tenant10@test.com', 'TENANT', 1, 91, '小王', '2026-04-28 09:00:00', '2026-04-28 09:00:00', 0, 1),
(1013, 'tenant11', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '冯十三', '13800138013', 'tenant11@test.com', 'TENANT', 1, 87, '老冯', '2026-04-29 14:00:00', '2026-04-29 14:00:00', 0, 1),
(1014, 'tenant12', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '陈十四', '13800138014', 'tenant12@test.com', 'TENANT', 1, 72, '小陈', '2026-04-30 16:00:00', '2026-04-30 16:00:00', 0, 1),
(1015, 'tenant13', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '褚十五', '13800138015', 'tenant13@test.com', 'TENANT', 1, 93, '小褚', '2026-05-01 10:00:00', '2026-05-01 10:00:00', 0, 1),
(1016, 'tenant14', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '卫十六', '13800138016', 'tenant14@test.com', 'TENANT', 1, 89, '老卫', '2026-05-02 09:00:00', '2026-05-02 09:00:00', 0, 1),
(1017, 'tenant15', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '蒋十七', '13800138017', 'tenant15@test.com', 'TENANT', 1, 84, '小蒋', '2026-05-03 15:00:00', '2026-05-03 15:00:00', 0, 1);

-- 补充房东 (每个房东拥有多个房源)
INSERT IGNORE INTO sys_user (id, username, password, real_name, phone, email, user_type, status, credit_score, nickname, create_time, update_time, is_deleted, version) VALUES
(1018, 'landlord4', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '房东杨', '13900139003', 'landlord4@test.com', 'LANDLORD', 1, 88, '杨姐', '2026-04-25 10:00:00', '2026-04-25 10:00:00', 0, 1),
(1019, 'landlord5', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '房东朱', '13900139004', 'landlord5@test.com', 'LANDLORD', 1, 92, '朱哥', '2026-04-26 12:00:00', '2026-04-26 12:00:00', 0, 1),
(1020, 'landlord6', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '房东秦', '13900139005', 'landlord6@test.com', 'LANDLORD', 1, 85, '秦姐', '2026-04-27 14:00:00', '2026-04-27 14:00:00', 0, 1),
(1021, 'landlord7', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '房东尤', '13900139006', 'landlord7@test.com', 'LANDLORD', 1, 90, '尤哥', '2026-04-28 16:00:00', '2026-04-28 16:00:00', 0, 1),
(1022, 'landlord8', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '房东许', '13900139007', 'landlord8@test.com', 'LANDLORD', 1, 78, '许姐', '2026-04-29 18:00:00', '2026-04-29 18:00:00', 0, 1),
(1023, 'landlord9', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '房东何', '13900139008', 'landlord9@test.com', 'LANDLORD', 1, 95, '何哥', '2026-04-30 10:00:00', '2026-04-30 10:00:00', 0, 1),
(1024, 'landlord10', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '房东吕', '13900139009', 'landlord10@test.com', 'LANDLORD', 1, 82, '吕姐', '2026-05-01 12:00:00', '2026-05-01 12:00:00', 0, 1);

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 测试数据创建完成！
-- =====================================================

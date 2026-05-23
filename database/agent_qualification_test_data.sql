-- ============================================
-- 中介资质审核测试数据
-- 用于项目答辩演示资质审核功能
-- 执行方式：mysql -u root -p house_eco < agent_qualification_test_data.sql
-- ============================================

-- 先创建测试用户（AGENT 角色）
INSERT IGNORE INTO sys_user (id, username, password, nickname, real_name, phone, email, user_type, status, credit_score) VALUES
(101, 'agent_zhang', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '张中介', '张三', '13800138001', 'zhang@example.com', 'AGENT', 1, 100),
(102, 'agent_li', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '李中介', '李四', '13800138002', 'li@example.com', 'AGENT', 1, 100),
(103, 'agent_wang', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '王中介', '王五', '13800138003', 'wang@example.com', 'AGENT', 1, 100),
(104, 'agent_zhao', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '赵中介', '赵六', '13800138004', 'zhao@example.com', 'AGENT', 1, 100),
(105, 'agent_sun', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '孙中介', '孙七', '13800138005', 'sun@example.com', 'AGENT', 1, 100);

-- 插入资质审核数据
-- 待审核的资质申请（status=0）- 2 条
INSERT INTO biz_agent_qualification (user_id, real_name, id_card, id_card_front, id_card_back, business_license, status) VALUES
(101, '张三', '110101199001011234', '/house-rental/qualification/id_card_front_001.jpg', '/house-rental/qualification/id_card_back_001.jpg', '/house-rental/qualification/business_license_001.jpg', 0),
(102, '李四', '110101199002022345', '/house-rental/qualification/id_card_front_002.jpg', '/house-rental/qualification/id_card_back_002.jpg', '/house-rental/qualification/business_license_002.jpg', 0);

-- 已通过审核的资质（status=1）- 2 条
INSERT INTO biz_agent_qualification (user_id, real_name, id_card, id_card_front, id_card_back, business_license, status) VALUES
(103, '王五', '110101199003033456', '/house-rental/qualification/id_card_front_003.jpg', '/house-rental/qualification/id_card_back_003.jpg', '/house-rental/qualification/business_license_003.jpg', 1),
(104, '赵六', '110101199004044567', '/house-rental/qualification/id_card_front_004.jpg', '/house-rental/qualification/id_card_back_004.jpg', '/house-rental/qualification/business_license_004.jpg', 1);

-- 被拒绝的资质申请（status=2）- 1 条
INSERT INTO biz_agent_qualification (user_id, real_name, id_card, id_card_front, id_card_back, business_license, status, reject_reason) VALUES
(105, '孙七', '110101199005055678', '/house-rental/qualification/id_card_front_005.jpg', '/house-rental/qualification/id_card_back_005.jpg', '/house-rental/qualification/business_license_005.jpg', 2, '身份证照片模糊，无法辨认个人信息');

-- 查询验证
SELECT 
    aq.id,
    u.username,
    aq.real_name,
    CASE aq.status WHEN 0 THEN '待审核' WHEN 1 THEN '已通过' WHEN 2 THEN '已拒绝' END as status_text,
    aq.create_time
FROM biz_agent_qualification aq
LEFT JOIN sys_user u ON aq.user_id = u.id
ORDER BY aq.create_time DESC;

-- ============================================
-- 房东/租客资质审核测试数据
-- 用于项目答辩演示资质审核功能
-- 说明：资质审核功能已实现管理员审核端，用户端申请入口待开发
-- 执行方式：mysql -u root -p house_eco < landlord_tenant_qualification_test_data.sql
-- ============================================

-- 插入房东资质审核数据
-- 待审核的房东资质（status=0）- 2 条
INSERT INTO biz_agent_qualification (user_id, real_name, id_card, id_card_front, id_card_back, business_license, status) VALUES
(2, '房东张', '110101199001011234', '/house-rental/qualification/landlord_zhang_id_front.jpg', '/house-rental/qualification/landlord_zhang_id_back.jpg', '/house-rental/qualification/landlord_zhang_license.jpg', 0),
(3, '房东李', '110101199002022345', '/house-rental/qualification/landlord_li_id_front.jpg', '/house-rental/qualification/landlord_li_id_back.jpg', '/house-rental/qualification/landlord_li_license.jpg', 0);

-- 已通过审核的房东资质（status=1）- 2 条
INSERT INTO biz_agent_qualification (user_id, real_name, id_card, id_card_front, id_card_back, business_license, status) VALUES
(6, '房东王', '110101199003033456', '/house-rental/qualification/landlord_wang_id_front.jpg', '/house-rental/qualification/landlord_wang_id_back.jpg', '/house-rental/qualification/landlord_wang_license.jpg', 1),
(7, '房东赵', '110101199004044567', '/house-rental/qualification/landlord_zhao_id_front.jpg', '/house-rental/qualification/landlord_zhao_id_back.jpg', '/house-rental/qualification/landlord_zhao_license.jpg', 1);

-- 插入租客资质审核数据
-- 待审核的租客资质（status=0）- 1 条
INSERT INTO biz_agent_qualification (user_id, real_name, id_card, id_card_front, id_card_back, business_license, status) VALUES
(4, '租客王', '310101199201011234', '/house-rental/qualification/tenant_wang_id_front.jpg', '/house-rental/qualification/tenant_wang_id_back.jpg', NULL, 0);

-- 已通过审核的租客资质（status=1）- 1 条
INSERT INTO biz_agent_qualification (user_id, real_name, id_card, id_card_front, id_card_back, business_license, status) VALUES
(5, '租客赵', '310101199202022345', '/house-rental/qualification/tenant_zhao_id_front.jpg', '/house-rental/qualification/tenant_zhao_id_back.jpg', NULL, 1);

-- 被拒绝的租客资质（status=2）- 1 条
INSERT INTO biz_agent_qualification (user_id, real_name, id_card, id_card_front, id_card_back, business_license, status, reject_reason) VALUES
(8, '租客钱', '310101199203033456', '/house-rental/qualification/tenant_qian_id_front.jpg', '/house-rental/qualification/tenant_qian_id_back.jpg', NULL, 2, '身份证照片不清晰，请重新上传');

-- 查询验证
SELECT 
    aq.id,
    u.username,
    u.user_type,
    aq.real_name,
    CASE aq.status WHEN 0 THEN '待审核' WHEN 1 THEN '已通过' WHEN 2 THEN '已拒绝' END as status_text,
    aq.reject_reason
FROM biz_agent_qualification aq
LEFT JOIN sys_user u ON aq.user_id = u.id
ORDER BY u.user_type, aq.status;

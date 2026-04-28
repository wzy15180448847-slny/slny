USE house_eco;

-- 查看所有管理员用户
SELECT id, username, user_type FROM sys_user WHERE user_type = 'ADMIN';

-- 查看ADMIN角色ID
SELECT id, role_code, role_name FROM sys_role WHERE role_code = 'ADMIN';

-- 删除已存在的用户角色关联（避免重复）
DELETE FROM sys_user_role WHERE user_id IN (SELECT id FROM sys_user WHERE user_type = 'ADMIN');

-- 为所有管理员用户分配ADMIN角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id 
FROM sys_user u
CROSS JOIN sys_role r
WHERE u.user_type = 'ADMIN' AND r.role_code = 'ADMIN';

-- 验证结果
SELECT u.username, r.role_name 
FROM sys_user u
JOIN sys_user_role ur ON u.id = ur.user_id
JOIN sys_role r ON ur.role_id = r.id
WHERE u.user_type = 'ADMIN';

SELECT '所有管理员用户已成功分配ADMIN角色！' AS result;
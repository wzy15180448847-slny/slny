USE house_eco;

-- 查看admin用户当前状态
SELECT id, username, status, user_type FROM sys_user WHERE username = 'admin';

-- 解锁admin账号（将状态从禁用(0)改为启用(1)）
UPDATE sys_user 
SET status = 1 
WHERE username = 'admin';

-- 验证解锁结果
SELECT id, username, status, user_type FROM sys_user WHERE username = 'admin';

SELECT 'Admin账号已成功解锁！' AS result;
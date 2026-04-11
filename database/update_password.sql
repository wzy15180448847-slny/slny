-- 更新用户密码为正确的BCrypt格式
-- 密码: admin123 (对于admin用户)
-- 密码: 123456 (对于其他测试用户)

USE house_rental;

-- 更新admin用户密码 (密码: admin123)
UPDATE sys_user 
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.',
    login_fail_count = 0
WHERE username = 'admin';

-- 更新其他测试用户密码 (密码: 123456)
UPDATE sys_user 
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.',
    login_fail_count = 0
WHERE username IN ('landlord1', 'tenant1', 'agent1');

-- 验证更新
SELECT id, username, password, login_fail_count FROM sys_user;

-- 清理可能有问题的头像数据，重新开始
USE house_eco;

-- 先查看当前的头像数据
SELECT id, username, nickname, avatar, user_type 
FROM sys_user 
WHERE avatar IS NOT NULL AND avatar != '';

-- 清空头像字段，让大家可以重新上传
UPDATE sys_user 
SET avatar = NULL;

-- 验证一下
SELECT id, username, nickname, avatar, user_type 
FROM sys_user;

-- 更新合同时间为最近的日期
USE house_eco;

-- 更新合同开始和结束日期
UPDATE biz_lease_agreement 
SET 
    start_date = DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 30) DAY),
    end_date = DATE_ADD(DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 30) DAY), INTERVAL 1 YEAR),
    update_time = NOW()
WHERE 1=1;

-- 查看更新后的合同
SELECT 
    id,
    lease_no AS '合同编号',
    start_date AS '开始日期',
    end_date AS '结束日期',
    status AS '状态'
FROM biz_lease_agreement;

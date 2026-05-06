USE house_eco;

SET FOREIGN_KEY_CHECKS = 0;

-- 插入合同数据
INSERT IGNORE INTO biz_lease_agreement (id, contract_no, house_id, tenant_id, landlord_id, start_date, end_date, rent_price, deposit, payment_way, status, create_time, is_deleted) VALUES
(4001, 'CON20240501001', 2001, 1001, 1008, '2026-05-15', '2027-05-14', 6800.00, 6800.00, 2, 0, '2026-05-05 10:00:00', 0),
(4002, 'CON20240501002', 2009, 1002, 1009, '2026-05-20', '2027-05-19', 12000.00, 24000.00, 2, 0, '2026-05-06 14:00:00', 0);

SET FOREIGN_KEY_CHECKS = 1;

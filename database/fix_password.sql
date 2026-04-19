USE house_rental;
UPDATE sys_user SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', login_fail_count = 0, version = 0 WHERE username = 'admin';
UPDATE sys_user SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', login_fail_count = 0, version = 0 WHERE username IN ('landlord1', 'tenant1', 'agent1');
UPDATE sys_user SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.eG1H2DkKsQPmPLF9E.', login_fail_count = 0, version = 0 WHERE username IN ('landlord2', 'landlord3', 'landlord4', 'landlord5', 'tenant2', 'tenant3', 'tenant4', 'tenant5', 'tenant6', 'tenant7', 'tenant8', 'tenant9');
SELECT '密码更新完成' AS result;

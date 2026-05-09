CREATE TABLE IF NOT EXISTS biz_bill (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    lease_id BIGINT(20) NOT NULL,
    house_id BIGINT(20) NOT NULL,
    tenant_id BIGINT(20) NOT NULL,
    landlord_id BIGINT(20) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    status TINYINT(1) DEFAULT 1,
    bill_type TINYINT(1) DEFAULT 1,
    is_deleted TINYINT(1) DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT(20),
    update_by BIGINT(20),
    FOREIGN KEY (lease_id) REFERENCES biz_lease_agreement(id),
    FOREIGN KEY (house_id) REFERENCES biz_house(id),
    FOREIGN KEY (tenant_id) REFERENCES sys_user(id),
    FOREIGN KEY (landlord_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO biz_bill (lease_id, house_id, tenant_id, landlord_id, amount, status, bill_type) VALUES 
(1, 1, 4, 2, 4500.00, 1, 1),
(2, 2, 5, 2, 6800.00, 2, 1),
(1, 1, 4, 2, 9000.00, 2, 2);

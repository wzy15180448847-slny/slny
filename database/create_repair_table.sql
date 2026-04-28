CREATE TABLE biz_repair (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    house_id BIGINT(20) NOT NULL,
    tenant_id BIGINT(20) NOT NULL,
    landlord_id BIGINT(20) NOT NULL,
    repair_type VARCHAR(50),
    description TEXT,
    status TINYINT(1) DEFAULT 0,
    evaluate_content TEXT,
    evaluate_score INT(1),
    is_deleted TINYINT(1) DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT(20),
    update_by BIGINT(20),
    version INT(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

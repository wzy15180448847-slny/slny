CREATE TABLE biz_electronic_signature (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    signature_no VARCHAR(50),
    agreement_id BIGINT(20),
    user_id BIGINT(20),
    user_type VARCHAR(20),
    signature_data TEXT,
    signing_time DATETIME,
    status TINYINT(1) DEFAULT 0,
    is_deleted TINYINT(1) DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by BIGINT(20),
    update_by BIGINT(20),
    version INT(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

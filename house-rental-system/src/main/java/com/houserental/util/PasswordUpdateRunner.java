package com.houserental.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUpdateRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public PasswordUpdateRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String currentPassword = jdbcTemplate.queryForObject(
            "SELECT password FROM sys_user WHERE username = 'admin'", String.class);
        
        if (currentPassword == null || !currentPassword.startsWith("$2a$10$") || currentPassword.length() < 50) {
            String adminPassword = encoder.encode("admin123");
            String userPassword = encoder.encode("123456");
            
            jdbcTemplate.update("UPDATE sys_user SET password = ?, login_fail_count = 0, version = 0 WHERE username = 'admin'", adminPassword);
            jdbcTemplate.update("UPDATE sys_user SET password = ?, login_fail_count = 0, version = 0 WHERE username IN ('landlord1', 'tenant1', 'agent1')", userPassword);
            
            System.out.println("========================================");
            System.out.println("密码已自动更新为正确的BCrypt格式！");
            System.out.println("admin 密码: admin123");
            System.out.println("其他用户 密码: 123456");
            System.out.println("========================================");
        }
        
        Integer versionCheck = jdbcTemplate.queryForObject(
            "SELECT version FROM sys_user WHERE username = 'admin'", Integer.class);
        if (versionCheck == null) {
            jdbcTemplate.update("UPDATE sys_user SET version = 0 WHERE version IS NULL");
            System.out.println("已修复 version 字段为 null 的问题");
        }
    }
}

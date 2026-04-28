package com.houserental.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.houserental.entity.Appointment;
import com.houserental.entity.House;
import com.houserental.entity.User;
import com.houserental.mapper.UserMapper;
import com.houserental.service.AppointmentService;
import com.houserental.service.HouseService;
import com.houserental.service.UserService;
import com.houserental.utils.TestDataGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据自动灌溉组件
 * 项目启动时自动检查并生成测试数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserService userService;
    private final UserMapper userMapper;
    private final HouseService houseService;
    private final AppointmentService appointmentService;
    private final TestDataGenerator testDataGenerator;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("========== 开始检查数据库数据 ==========");

        long userCount = userMapper.selectCount(new QueryWrapper<>());
        log.info("当前用户数量: {}", userCount);

        if (userCount < 2) {
            log.info("用户数量不足，开始生成测试数据...");
            generateTestData();
            log.info("========== 测试数据生成完成 ==========");
        } else {
            log.info("数据库已有足够数据，跳过数据生成");
        }
    }

    private void generateTestData() {
        log.info("1. 生成管理员用户...");
        List<User> admins = testDataGenerator.generateAdmins(3);
        for (User admin : admins) {
            String rawPassword = admin.getPassword();
            admin.setPassword(passwordEncoder.encode(rawPassword));
            userService.save(admin);
        }
        log.info("   ✓ 生成管理员: {} 个 (用户名: admin/super/root, 密码: admin123)", admins.size());

        log.info("2. 生成房东用户...");
        List<User> landlords = testDataGenerator.generateLandlords(10);
        for (User landlord : landlords) {
            String rawPassword = landlord.getPassword();
            landlord.setPassword(passwordEncoder.encode(rawPassword));
            userService.save(landlord);
        }
        log.info("   ✓ 生成房东: {} 个 (用户名: landlord0-landlord9, 密码: 123456)", landlords.size());

        log.info("3. 生成租客用户...");
        List<User> tenants = testDataGenerator.generateTenants(20);
        for (User tenant : tenants) {
            String rawPassword = tenant.getPassword();
            tenant.setPassword(passwordEncoder.encode(rawPassword));
            userService.save(tenant);
        }
        log.info("   ✓ 生成租客: {} 个 (用户名: tenant0-tenant19, 密码: 123456)", tenants.size());

        log.info("4. 生成房源数据...");
        List<House> houses = testDataGenerator.generateHouses(landlords, 50);
        for (House house : houses) {
            houseService.save(house);
        }
        log.info("   ✓ 生成房源: {} 个 (包含待审核、展示中、已出租、已下架状态)", houses.size());

        log.info("5. 生成预约数据...");
        List<Appointment> appointments = testDataGenerator.generateAppointments(houses, tenants, 50);
        for (Appointment appointment : appointments) {
            appointmentService.save(appointment);
        }
        log.info("   ✓ 生成预约: {} 个", appointments.size());

        log.info("\n=== 测试数据汇总 ===");
        log.info("管理员: 3 个");
        log.info("房东: 10 个");
        log.info("租客: 20 个");
        log.info("房源: 50 个");
        log.info("预约: 50 个");
        log.info("\n=== 测试账号 ===");
        log.info("管理员: admin / admin123");
        log.info("房东: landlord0 / 123456");
        log.info("租客: tenant0 / 123456");
    }
}
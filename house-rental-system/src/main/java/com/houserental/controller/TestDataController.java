package com.houserental.controller;

import com.houserental.entity.Appointment;
import com.houserental.entity.House;
import com.houserental.entity.User;
import com.houserental.service.AppointmentService;
import com.houserental.service.HouseService;
import com.houserental.service.UserService;
import com.houserental.utils.TestDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 测试数据填充控制器
 */
@RestController
@RequestMapping("/test-data")
public class TestDataController {

    @Autowired
    private TestDataGenerator testDataGenerator;

    @Autowired
    private UserService userService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 生成管理员用户
     */
    @PostMapping("/admins")
    public String generateAdmins(@RequestParam(defaultValue = "3") int count) {
        List<User> admins = testDataGenerator.generateAdmins(count);
        for (User admin : admins) {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            userService.save(admin);
        }
        return "成功生成 " + admins.size() + " 个管理员数据";
    }

    /**
     * 生成房东用户
     */
    @PostMapping("/landlords")
    public String generateLandlords(@RequestParam(defaultValue = "10") int count) {
        List<User> landlords = testDataGenerator.generateLandlords(count);
        for (User landlord : landlords) {
            landlord.setPassword(passwordEncoder.encode(landlord.getPassword()));
            userService.save(landlord);
        }
        return "成功生成 " + landlords.size() + " 个房东数据";
    }

    /**
     * 生成租客用户
     */
    @PostMapping("/tenants")
    public String generateTenants(@RequestParam(defaultValue = "20") int count) {
        List<User> tenants = testDataGenerator.generateTenants(count);
        for (User tenant : tenants) {
            tenant.setPassword(passwordEncoder.encode(tenant.getPassword()));
            userService.save(tenant);
        }
        return "成功生成 " + tenants.size() + " 个租客数据";
    }

    /**
     * 生成测试房源数据
     */
    @PostMapping("/houses")
    public String generateHouses(@RequestParam(defaultValue = "50") int count) {
        List<User> landlords = userService.listByUserType("LANDLORD");
        if (landlords.isEmpty()) {
            landlords = testDataGenerator.generateLandlords(5);
            for (User user : landlords) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userService.save(user);
            }
        }
        List<House> houses = testDataGenerator.generateHouses(landlords, count);
        for (House house : houses) {
            houseService.save(house);
        }
        return "成功生成 " + houses.size() + " 个房源数据";
    }

    /**
     * 清理并重新生成房源数据（解决数据重复问题）
     */
    @PostMapping("/houses/refresh")
    public String refreshHouses(@RequestParam(defaultValue = "50") int count) {
        // 清理Redis缓存
        runRedisFlush();
        
        // 获取所有房东
        List<User> landlords = userService.listByUserType("LANDLORD");
        if (landlords.isEmpty()) {
            landlords = testDataGenerator.generateLandlords(5);
            for (User user : landlords) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userService.save(user);
            }
        }
        
        // 清理现有房源数据
        houseService.cleanAll();
        
        // 重新生成房源数据
        List<House> houses = testDataGenerator.generateHouses(landlords, count);
        for (House house : houses) {
            houseService.save(house);
        }
        
        return "成功清理并重新生成 " + houses.size() + " 个房源数据";
    }

    private void runRedisFlush() {
        try {
            Runtime.getRuntime().exec("redis-cli flushall");
        } catch (Exception e) {
            // 忽略错误
        }
    }

    /**
     * 生成测试预约数据
     */
    @PostMapping("/appointments")
    public String generateAppointments(@RequestParam(defaultValue = "100") int count) {
        List<House> houses = houseService.list();
        if (houses.isEmpty()) {
            return "请先生成房源数据";
        }
        List<User> tenants = userService.listByUserType("TENANT");
        if (tenants.isEmpty()) {
            tenants = testDataGenerator.generateTenants(10);
            for (User user : tenants) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userService.save(user);
            }
        }
        List<Appointment> appointments = testDataGenerator.generateAppointments(houses, tenants, count);
        for (Appointment appointment : appointments) {
            appointmentService.save(appointment);
        }
        return "成功生成 " + appointments.size() + " 个预约数据";
    }

    /**
     * 生成所有测试数据
     */
    @PostMapping("/all")
    public String generateAllTestData() {
        List<User> admins = testDataGenerator.generateAdmins(3);
        for (User admin : admins) {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            userService.save(admin);
        }

        List<User> landlords = testDataGenerator.generateLandlords(10);
        for (User landlord : landlords) {
            landlord.setPassword(passwordEncoder.encode(landlord.getPassword()));
            userService.save(landlord);
        }

        List<User> tenants = testDataGenerator.generateTenants(20);
        for (User tenant : tenants) {
            tenant.setPassword(passwordEncoder.encode(tenant.getPassword()));
            userService.save(tenant);
        }

        List<House> houses = testDataGenerator.generateHouses(landlords, 50);
        for (House house : houses) {
            houseService.save(house);
        }

        List<Appointment> appointments = testDataGenerator.generateAppointments(houses, tenants, 100);
        for (Appointment appointment : appointments) {
            appointmentService.save(appointment);
        }

        return "成功生成所有测试数据：\n" +
                "- 管理员：" + admins.size() + " 个\n" +
                "- 房东：" + landlords.size() + " 个\n" +
                "- 租客：" + tenants.size() + " 个\n" +
                "- 房源：" + houses.size() + " 个\n" +
                "- 预约：" + appointments.size() + " 个";
    }
}
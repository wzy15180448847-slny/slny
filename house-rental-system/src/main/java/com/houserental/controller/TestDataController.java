package com.houserental.controller;

import com.houserental.entity.Appointment;
import com.houserental.entity.House;
import com.houserental.entity.User;
import com.houserental.service.AppointmentService;
import com.houserental.service.HouseService;
import com.houserental.service.UserService;
import com.houserental.utils.TestDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 生成测试用户数据
     */
    @PostMapping("/users")
    public String generateUsers(@RequestParam(defaultValue = "10") int count) {
        List<User> users = testDataGenerator.generateUsers(count);
        for (User user : users) {
            userService.save(user);
        }
        return "成功生成 " + users.size() + " 个用户数据";
    }

    /**
     * 生成测试房源数据
     */
    @PostMapping("/houses")
    public String generateHouses(@RequestParam(defaultValue = "50") int count) {
        // 获取所有房东用户
        List<User> landlords = userService.listByUserType("LANDLORD");
        if (landlords.isEmpty()) {
            // 如果没有房东，先创建一些
            landlords = testDataGenerator.generateUsers(5);
            for (User user : landlords) {
                user.setUserType("LANDLORD");
                userService.save(user);
            }
        }
        List<House> houses = testDataGenerator.generateHouses(count, landlords);
        for (House house : houses) {
            houseService.save(house);
        }
        return "成功生成 " + houses.size() + " 个房源数据";
    }

    /**
     * 生成测试预约数据
     */
    @PostMapping("/appointments")
    public String generateAppointments(@RequestParam(defaultValue = "100") int count) {
        // 获取所有房源
        List<House> houses = houseService.list();
        if (houses.isEmpty()) {
            return "请先生成房源数据";
        }
        // 获取所有租客用户
        List<User> tenants = userService.listByUserType("TENANT");
        if (tenants.isEmpty()) {
            // 如果没有租客，先创建一些
            tenants = testDataGenerator.generateUsers(10);
            for (User user : tenants) {
                user.setUserType("TENANT");
                userService.save(user);
            }
        }
        List<Appointment> appointments = testDataGenerator.generateAppointments(count, houses, tenants);
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
        // 生成用户数据
        List<User> users = testDataGenerator.generateUsers(20);
        for (User user : users) {
            userService.save(user);
        }

        // 生成房源数据
        List<User> landlords = userService.listByUserType("LANDLORD");
        List<House> houses = testDataGenerator.generateHouses(50, landlords);
        for (House house : houses) {
            houseService.save(house);
        }

        // 生成预约数据
        List<User> tenants = userService.listByUserType("TENANT");
        List<Appointment> appointments = testDataGenerator.generateAppointments(100, houses, tenants);
        for (Appointment appointment : appointments) {
            appointmentService.save(appointment);
        }

        return "成功生成所有测试数据：\n" +
                "- 用户：" + users.size() + " 个\n" +
                "- 房源：" + houses.size() + " 个\n" +
                "- 预约：" + appointments.size() + " 个";
    }
}

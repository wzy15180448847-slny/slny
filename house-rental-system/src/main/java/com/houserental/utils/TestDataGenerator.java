package com.houserental.utils;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.houserental.entity.Appointment;
import com.houserental.entity.House;
import com.houserental.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 测试数据生成器
 */
@Component
public class TestDataGenerator {

    private static final Random random = new Random();

    /**
     * 生成用户数据
     */
    public List<User> generateUsers(int count) {
        List<User> users = new ArrayList<>();
        String[] userTypes = {"LANDLORD", "TENANT", "ADMIN"};
        String[] genders = {"0", "1", "2"};
        String[] names = {"张", "李", "王", "刘", "陈", "杨", "赵", "黄", "周", "吴"};
        String[] nicknames = {"阳光", "快乐", "健康", "活力", "智慧", "勇敢", "友善", "耐心", "细心", "热情"};

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(IdWorker.getId());
            user.setUsername("user" + i);
            user.setPassword("123456"); // 密码需要加密，这里为了测试方便
            user.setNickname(nicknames[random.nextInt(nicknames.length)] + i);
            user.setRealName(names[random.nextInt(names.length)] + "" + (i % 10));
            user.setPhone("138" + String.format("%08d", i));
            user.setEmail("user" + i + "@example.com");
            user.setAvatar("https://randomuser.me/api/portraits/" + (random.nextInt(2) == 0 ? "men" : "women") + "/" + i + ".jpg");
            user.setGender(Integer.parseInt(genders[random.nextInt(genders.length)]));
            user.setIdCard("11010119900101" + String.format("%04d", i));
            user.setUserType(userTypes[Math.min(i, userTypes.length - 1)]);
            user.setStatus(1);
            user.setLastLoginTime(LocalDateTime.now().minusDays(random.nextInt(30)));
            user.setLastLoginIp("192.168.1." + (random.nextInt(255) + 1));
            user.setLoginFailCount(0);
            user.setPasswordUpdateTime(LocalDateTime.now().minusMonths(random.nextInt(6)));
            user.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(90)));
            user.setUpdateTime(LocalDateTime.now().minusDays(random.nextInt(30)));
            users.add(user);
        }
        return users;
    }

    /**
     * 生成房源数据
     */
    public List<House> generateHouses(int count, List<User> landlords) {
        List<House> houses = new ArrayList<>();
        String[] provinces = {"北京市", "上海市", "广州市", "深圳市", "杭州市"};
        String[] cities = {"北京", "上海", "广州", "深圳", "杭州"};
        String[] districts = {"朝阳区", "海淀区", "东城区", "西城区", "丰台区"};
        String[] streets = {"望京街道", "中关村街道", "东华门街道", "西长安街街道", "丰台街道"};
        String[] houseTypes = {"1室1厅1卫", "2室1厅1卫", "2室2厅1卫", "3室1厅1卫", "3室2厅2卫"};
        String[] decorations = {"1", "2", "3", "4"};
        String[] orientations = {"1", "2", "3", "4", "5", "6", "7", "8"};
        String[] paymentWays = {"1", "2", "3", "4"};
        String[] rentWays = {"1", "2"};
        String[] viewTimeTypes = {"1", "2", "3", "4"};

        for (int i = 0; i < count; i++) {
            House house = new House();
            house.setId(IdWorker.getId());
            house.setHouseNo("H" + String.format("%06d", i));
            house.setLandlordId(landlords.get(random.nextInt(landlords.size())).getId());
            house.setTitle("精装修" + houseTypes[random.nextInt(houseTypes.length)] + "拎包入住");
            house.setDescription("交通便利，周边配套齐全，环境优美，适合居住。");
            house.setProvince(provinces[random.nextInt(provinces.length)]);
            house.setCity(cities[random.nextInt(cities.length)]);
            house.setDistrict(districts[random.nextInt(districts.length)]);
            house.setStreet(streets[random.nextInt(streets.length)]);
            house.setAddress("测试地址" + i + "号");
            house.setLongitude(new BigDecimal("116." + (random.nextInt(999999) + 100000)));
            house.setLatitude(new BigDecimal("39." + (random.nextInt(999999) + 100000)));
            house.setHouseType(houseTypes[random.nextInt(houseTypes.length)]);
            house.setRoomCount(random.nextInt(5) + 1);
            house.setHallCount(random.nextInt(3) + 1);
            house.setBathroomCount(random.nextInt(3) + 1);
            house.setArea(new BigDecimal((random.nextInt(200) + 50) + "." + (random.nextInt(99) + 1)));
            house.setFloor(random.nextInt(30) + 1);
            house.setTotalFloor(random.nextInt(30) + 1);
            house.setHasElevator(random.nextInt(2));
            house.setDecoration(Integer.parseInt(decorations[random.nextInt(decorations.length)]));
            house.setOrientation(Integer.parseInt(orientations[random.nextInt(orientations.length)]));
            house.setRentPrice(new BigDecimal((random.nextInt(10000) + 2000) + ".00"));
            house.setDepositMonth(random.nextInt(3) + 1);
            house.setPaymentWay(Integer.parseInt(paymentWays[random.nextInt(paymentWays.length)]));
            house.setRentWay(Integer.parseInt(rentWays[random.nextInt(rentWays.length)]));
            house.setFacilities("{\"wifi\":true,\"airConditioner\":true,\"washingMachine\":true,\"refrigerator\":true,\"waterHeater\":true}");
            house.setImages("[\"https://picsum.photos/800/600?random=" + i + "\",\"https://picsum.photos/800/600?random=" + (i+1) + "\"]");
            house.setCoverImage("https://picsum.photos/800/600?random=" + i);
            house.setContactName("联系人" + i);
            house.setContactPhone("139" + String.format("%08d", i));
            house.setViewTimeType(Integer.parseInt(viewTimeTypes[random.nextInt(viewTimeTypes.length)]));
            house.setAvailableDate(LocalDateTime.now().plusDays(random.nextInt(30)));
            house.setMinLeaseTerm(random.nextInt(12) + 6);
            house.setStatus(2); // 已发布
            house.setAuditStatus(1); // 审核通过
            house.setViewCount(random.nextInt(1000));
            house.setFavoriteCount(random.nextInt(100));
            house.setAppointmentCount(random.nextInt(50));
            house.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(90)));
            house.setUpdateTime(LocalDateTime.now().minusDays(random.nextInt(30)));
            houses.add(house);
        }
        return houses;
    }

    /**
     * 生成预约数据
     */
    public List<Appointment> generateAppointments(int count, List<House> houses, List<User> tenants) {
        List<Appointment> appointments = new ArrayList<>();
        Integer[] statuses = {0, 1, 2, 3, 4};

        for (int i = 0; i < count; i++) {
            Appointment appointment = new Appointment();
            appointment.setId(IdWorker.getId());
            appointment.setAppointmentNo("A" + String.format("%06d", i));
            House house = houses.get(random.nextInt(houses.size()));
            appointment.setHouseId(house.getId());
            appointment.setLandlordId(house.getLandlordId());
            appointment.setTenantId(tenants.get(random.nextInt(tenants.size())).getId());
            appointment.setAppointmentTime(new Date(System.currentTimeMillis() + random.nextInt(7 * 24 * 60 * 60 * 1000)));
            appointment.setStatus(statuses[random.nextInt(statuses.length)]);
            appointment.setContactName("预约人" + i);
            appointment.setContactPhone("137" + String.format("%08d", i));
            appointment.setRemark("预约看房");
            appointment.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(30)));
            appointment.setUpdateTime(LocalDateTime.now().minusDays(random.nextInt(15)));
            appointments.add(appointment);
        }
        return appointments;
    }
}

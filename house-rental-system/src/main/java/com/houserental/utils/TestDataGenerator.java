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

@Component
public class TestDataGenerator {

    private static final Random random = new Random();

    public List<User> generateAdmins(int count) {
        List<User> users = new ArrayList<>();
        String[] names = {"admin0", "admin1", "admin2"};
        String[] nicknames = {"管理员", "超级管理员", "系统管理员"};

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(IdWorker.getId());
            user.setUsername(names[Math.min(i, names.length - 1)]);
            user.setPassword("admin123");
            user.setNickname(nicknames[Math.min(i, nicknames.length - 1)]);
            user.setRealName("管理员" + (i + 1));
            user.setPhone("1380000000" + i);
            user.setEmail("admin" + i + "@houserental.com");
            user.setAvatar("https://picsum.photos/200/200?random=" + i);
            user.setGender(0);
            user.setIdCard("11010119800101" + String.format("%04d", i + 1000));
            user.setUserType("ADMIN");
            user.setStatus(1);
            user.setLastLoginTime(LocalDateTime.now().minusDays(random.nextInt(7)));
            user.setLastLoginIp("192.168.1.1");
            user.setLoginFailCount(0);
            user.setPasswordUpdateTime(LocalDateTime.now().minusMonths(random.nextInt(3)));
            user.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(10)));
            user.setUpdateTime(LocalDateTime.now().minusDays(random.nextInt(5)));
            users.add(user);
        }
        return users;
    }

    public List<User> generateLandlords(int count) {
        List<User> users = new ArrayList<>();
        String[] surnames = {"张", "李", "王", "赵", "刘", "陈", "杨", "黄", "周", "吴"};
        String[] names = {"伟", "强", "明", "华", "军", "磊", "涛", "波", "杰", "鹏"};

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(IdWorker.getId());
            user.setUsername("landlord" + i);
            user.setPassword("123456");
            user.setNickname(surnames[i % surnames.length] + names[random.nextInt(names.length)]);
            user.setRealName(user.getNickname());
            user.setPhone("139000000" + String.format("%02d", i));
            user.setEmail("landlord" + i + "@houserental.com");
            user.setAvatar("https://picsum.photos/200/200?random=100" + i);
            user.setGender(random.nextInt(2) == 0 ? 0 : 1);
            user.setIdCard("11010119750101" + String.format("%04d", i + 2000));
            user.setUserType("LANDLORD");
            user.setStatus(1);
            user.setLastLoginTime(LocalDateTime.now().minusDays(random.nextInt(14)));
            user.setLastLoginIp("192.168.1." + (random.nextInt(10) + 10));
            user.setLoginFailCount(random.nextInt(2));
            user.setPasswordUpdateTime(LocalDateTime.now().minusMonths(random.nextInt(6)));
            user.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(30)));
            user.setUpdateTime(LocalDateTime.now().minusDays(random.nextInt(15)));
            users.add(user);
        }
        return users;
    }

    public List<User> generateTenants(int count) {
        List<User> users = new ArrayList<>();
        String[] surnames = {"张", "李", "王", "赵", "刘", "陈", "杨", "黄", "周", "吴"};
        String[] names = {"伟", "强", "明", "华", "军", "磊", "涛", "波", "杰", "鹏"};

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(IdWorker.getId());
            user.setUsername("tenant" + i);
            user.setPassword("123456");
            user.setNickname(surnames[i % surnames.length] + names[random.nextInt(names.length)] + (i % 10));
            user.setRealName(user.getNickname());
            user.setPhone("137000000" + String.format("%02d", i));
            user.setEmail("tenant" + i + "@houserental.com");
            user.setAvatar("https://picsum.photos/200/200?random=200" + i);
            user.setGender(random.nextInt(2) == 0 ? 0 : 1);
            user.setIdCard("11010119900101" + String.format("%04d", i + 3000));
            user.setUserType("TENANT");
            user.setStatus(1);
            user.setLastLoginTime(LocalDateTime.now().minusDays(random.nextInt(7)));
            user.setLastLoginIp("192.168.1." + (random.nextInt(20) + 20));
            user.setLoginFailCount(random.nextInt(3));
            user.setPasswordUpdateTime(LocalDateTime.now().minusMonths(random.nextInt(4)));
            user.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(20)));
            user.setUpdateTime(LocalDateTime.now().minusDays(random.nextInt(10)));
            users.add(user);
        }
        return users;
    }

    public List<House> generateHouses(int count, List<User> landlords) {
        List<House> houses = new ArrayList<>();
        String[] provinces = {"北京市", "北京市", "北京市", "上海市", "广州市"};
        String[] cities = {"北京", "北京", "北京", "上海", "广州"};
        String[] districts = {"朝阳区", "海淀区", "西城区", "东城区", "丰台区"};
        String[] streets = {"望京街道", "中关村街道", "金融街", "王府井", "方庄"};
        String[] houseTypes = {"1室1厅1卫", "2室1厅1卫", "2室2厅1卫", "3室1厅2卫", "3室2厅2卫", "4室2厅2卫"};
        String[] decorations = {"1", "2", "3"};
        String[] orientations = {"1", "2", "3", "4", "5"};
        String[] tags = {"精装", "简装", "毛坯", "南北通透", "近地铁", "学区房", "带车位", "拎包入住"};

        for (int i = 0; i < count; i++) {
            House house = new House();
            house.setId(IdWorker.getId());
            house.setHouseNo("HS" + String.format("%06d", i + 1));
            house.setLandlordId(landlords.get(random.nextInt(landlords.size())).getId());
            
            String houseType = houseTypes[random.nextInt(houseTypes.length)];
            String tag = tags[random.nextInt(tags.length)];
            house.setTitle(tag + houseType + "诚意出租");
            house.setDescription("交通便利，周边配套齐全，环境优美，采光良好，适合居住");
            house.setProvince(provinces[random.nextInt(provinces.length)]);
            house.setCity(cities[random.nextInt(cities.length)]);
            house.setDistrict(districts[random.nextInt(districts.length)]);
            house.setStreet(streets[random.nextInt(streets.length)]);
            house.setAddress(house.getDistrict() + house.getStreet() + i + "号");
            house.setLongitude(new BigDecimal("116." + (random.nextInt(999999) + 100000)));
            house.setLatitude(new BigDecimal("39." + (random.nextInt(999999) + 100000)));
            house.setHouseType(houseType);
            
            String[] parts = houseType.split("室");
            int roomCount = Integer.parseInt(parts[0]);
            house.setRoomCount(roomCount);
            
            String hallBath = parts[1];
            int hallCount = 1;
            int bathCount = 1;
            if (hallBath.contains("厅")) {
                String[] hallParts = hallBath.split("厅");
                hallCount = Integer.parseInt(hallParts[0]);
                if (hallParts.length > 1 && hallParts[1].contains("卫")) {
                    bathCount = Integer.parseInt(hallParts[1].replace("卫", ""));
                }
            }
            house.setHallCount(hallCount);
            house.setBathroomCount(bathCount);
            
            int baseArea = roomCount * 30 + 20;
            house.setArea(new BigDecimal((baseArea + random.nextInt(30)) + ".00"));
            house.setFloor(random.nextInt(25) + 1);
            house.setTotalFloor(random.nextInt(10) + 15);
            house.setHasElevator(house.getFloor() > 6 ? 1 : random.nextInt(2));
            house.setDecoration(Integer.parseInt(decorations[random.nextInt(decorations.length)]));
            house.setOrientation(Integer.parseInt(orientations[random.nextInt(orientations.length)]));
            
            int baseRent = roomCount * 1500 + 1000;
            house.setRentPrice(new BigDecimal((baseRent + random.nextInt(2000) / 100 * 100) + ".00"));
            house.setDepositMonth(random.nextInt(3) + 1);
            house.setPaymentWay(random.nextInt(4) + 1);
            house.setRentWay(random.nextInt(2) + 1);
            house.setFacilities("{\"wifi\":true,\"airConditioner\":true,\"washingMachine\":true,\"refrigerator\":true,\"waterHeater\":true}");
            
            StringBuilder images = new StringBuilder("[");
            int imageCount = random.nextInt(3) + 3;
            for (int j = 0; j < imageCount; j++) {
                if (j > 0) images.append(",");
                images.append("\"https://picsum.photos/800/600?random=").append(i * 10 + j).append("\"");
            }
            images.append("]");
            house.setImages(images.toString());
            house.setCoverImage("https://picsum.photos/800/600?random=" + i);
            house.setContactName("联系人" + i);
            house.setContactPhone("136000000" + String.format("%02d", i));
            house.setViewTimeType(random.nextInt(4) + 1);
            house.setAvailableDate(LocalDateTime.now().plusDays(random.nextInt(60)));
            house.setMinLeaseTerm(random.nextInt(6) + 6);
            
            int rand = random.nextInt(100);
            if (rand < 60) {
                house.setHouseStatus(2);
                house.setAuditStatus(1);
            } else if (rand < 75) {
                house.setHouseStatus(2);
                house.setAuditStatus(0);
            } else if (rand < 85) {
                house.setHouseStatus(1);
                house.setAuditStatus(1);
            } else {
                house.setHouseStatus(0);
                house.setAuditStatus(1);
            }
            
            house.setViewCount(random.nextInt(500) + 50);
            house.setFavoriteCount(random.nextInt(50) + 5);
            house.setAppointmentCount(random.nextInt(20) + 2);
            house.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(60)));
            house.setUpdateTime(LocalDateTime.now().minusDays(random.nextInt(30)));
            houses.add(house);
        }
        return houses;
    }

    public List<Appointment> generateAppointments(int count, List<House> houses, List<User> tenants) {
        List<Appointment> appointments = new ArrayList<>();
        Integer[] statuses = {0, 1, 2, 3, 4};

        for (int i = 0; i < count; i++) {
            Appointment appointment = new Appointment();
            appointment.setId(IdWorker.getId());
            appointment.setAppointmentNo("APT" + String.format("%06d", i + 1));
            House house = houses.get(random.nextInt(houses.size()));
            appointment.setHouseId(house.getId());
            appointment.setLandlordId(house.getLandlordId());
            appointment.setTenantId(tenants.get(random.nextInt(tenants.size())).getId());
            appointment.setAppointmentTime(new Date(System.currentTimeMillis() + random.nextInt(14 * 24 * 60 * 60 * 1000)));
            appointment.setStatus(statuses[random.nextInt(statuses.length)]);
            appointment.setContactName("预约人" + i);
            appointment.setContactPhone("135000000" + String.format("%02d", i));
            appointment.setRemark(random.nextBoolean() ? "希望下午看房" : "");
            appointment.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(14)));
            appointment.setUpdateTime(LocalDateTime.now().minusDays(random.nextInt(7)));
            appointments.add(appointment);
        }
        return appointments;
    }
}
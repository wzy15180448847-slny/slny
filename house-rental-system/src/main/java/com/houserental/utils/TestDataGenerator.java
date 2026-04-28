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
        String[] names = {"张", "李", "王", "赵", "刘", "陈", "杨", "黄", "周", "吴"};

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(IdWorker.getId());
            user.setUsername("landlord" + i);
            user.setPassword("admin123");
            user.setNickname("房东" + names[i % names.length]);
            user.setRealName(names[i % names.length] + "房东");
            user.setPhone("1380000010" + i);
            user.setEmail("landlord" + i + "@houserental.com");
            user.setAvatar("https://picsum.photos/200/200?random=" + (i + 100));
            user.setGender(random.nextInt(2));
            user.setIdCard("11010119750101" + String.format("%04d", i + 2000));
            user.setUserType("LANDLORD");
            user.setStatus(1);
            user.setLastLoginTime(LocalDateTime.now().minusDays(random.nextInt(14)));
            user.setLastLoginIp("192.168.1.2");
            user.setLoginFailCount(0);
            user.setPasswordUpdateTime(LocalDateTime.now().minusMonths(random.nextInt(6)));
            user.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(30)));
            user.setUpdateTime(LocalDateTime.now().minusDays(random.nextInt(15)));
            users.add(user);
        }
        return users;
    }

    public List<User> generateTenants(int count) {
        List<User> users = new ArrayList<>();
        String[] names = {"租客", "用户", "租户"};

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(IdWorker.getId());
            user.setUsername("tenant" + i);
            user.setPassword("123456");
            user.setNickname(names[i % names.length] + i);
            user.setRealName("租户" + (i + 1));
            user.setPhone("1390000000" + i);
            user.setEmail("tenant" + i + "@houserental.com");
            user.setAvatar("https://picsum.photos/200/200?random=" + (i + 200));
            user.setGender(random.nextInt(2));
            user.setIdCard("11010119900101" + String.format("%04d", i + 3000));
            user.setUserType("TENANT");
            user.setStatus(1);
            user.setLastLoginTime(LocalDateTime.now().minusDays(random.nextInt(30)));
            user.setLastLoginIp("192.168.1.3");
            user.setLoginFailCount(random.nextInt(3));
            user.setPasswordUpdateTime(LocalDateTime.now().minusMonths(random.nextInt(12)));
            user.setCreateTime(LocalDateTime.now().minusDays(random.nextInt(60)));
            user.setUpdateTime(LocalDateTime.now().minusDays(random.nextInt(30)));
            users.add(user);
        }
        return users;
    }

    public List<House> generateHouses(List<User> landlords, int count) {
        List<House> houses = new ArrayList<>();
        String[] cities = {"北京", "上海", "广州", "深圳", "杭州", "成都", "武汉", "西安"};
        String[][] districts = {
            {"朝阳区", "海淀区", "西城区", "东城区", "丰台区"},
            {"浦东新区", "徐汇区", "静安区", "黄浦区", "长宁区"},
            {"天河区", "海珠区", "白云区", "越秀区", "番禺区"},
            {"南山区", "福田区", "罗湖区", "宝安区", "龙岗区"},
            {"西湖区", "拱墅区", "滨江区", "上城区", "下城区"},
            {"锦江区", "武侯区", "青羊区", "成华区", "金牛区"},
            {"武昌区", "汉口区", "汉阳", "江岸区", "洪山区"},
            {"雁塔区", "碑林区", "未央区", "莲湖区", "新城区"}
        };
        String[] houseTypes = {"1室1厅1卫", "2室1厅1卫", "2室2厅1卫", "3室1厅2卫", "3室2厅2卫", "4室2厅2卫"};
        String[] titles = {
            "精装两居", "温馨一居", "大三居", "小户型", "学区房", "地铁房",
            "带车位", "南北通透", "采光好", "近地铁", "交通便利", "配套齐全"
        };

        for (int i = 0; i < count; i++) {
            House house = new House();
            house.setId(IdWorker.getId());
            house.setLandlordId(landlords.get(i % landlords.size()).getId());
            house.setHouseNo("HZ" + System.currentTimeMillis() % 100000 + "-" + i);
            
            int cityIdx = i % cities.length;
            house.setProvince(cities[cityIdx]);
            house.setCity(cities[cityIdx]);
            house.setDistrict(districts[cityIdx][random.nextInt(districts[cityIdx].length)]);
            house.setStreet("街道" + (i + 1) + "号");
            house.setAddress(house.getCity() + house.getDistrict() + house.getStreet());
            
            house.setLongitude(BigDecimal.valueOf(116.0 + random.nextDouble() * 3));
            house.setLatitude(BigDecimal.valueOf(39.0 + random.nextDouble() * 3));
            
            house.setHouseType(houseTypes[i % houseTypes.length]);
            
            String[] typeParts = house.getHouseType().split("室");
            int rooms = Integer.parseInt(typeParts[0]);
            house.setRoomCount(rooms);
            house.setHallCount(1);
            house.setBathroomCount(typeParts[1].contains("2卫") ? 2 : 1);
            
            house.setArea(BigDecimal.valueOf(40 + random.nextDouble() * 120));
            house.setFloor(random.nextInt(20) + 1);
            house.setTotalFloor(random.nextInt(10) + 15);
            house.setHasElevator(random.nextBoolean() ? 1 : 0);
            house.setDecoration(random.nextBoolean() ? 3 : 2);
            
            Integer[] orientations = {1, 2, 3, 4, 5, 6, 7, 8};
            house.setOrientation(orientations[random.nextInt(orientations.length)]);
            
            house.setRentPrice(new BigDecimal(2000 + random.nextInt(10000)));
            house.setDepositMonth(random.nextInt(3) + 1);
            house.setPaymentWay(random.nextInt(4) + 1);
            house.setRentWay(random.nextBoolean() ? 1 : 2);
            
            house.setTitle(titles[i % titles.length] + house.getHouseType() + "诚意出租");
            house.setDescription("交通便利，配套齐全，适合居住");
            
            StringBuilder images = new StringBuilder("[");
            for (int j = 0; j < 4; j++) {
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
                house.setStatus(2);
                house.setAuditStatus(1);
            } else if (rand < 75) {
                house.setStatus(2);
                house.setAuditStatus(0);
            } else if (rand < 85) {
                house.setStatus(1);
                house.setAuditStatus(1);
            } else {
                house.setStatus(0);
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

    public List<Appointment> generateAppointments(List<House> houses, List<User> tenants, int count) {
        List<Appointment> appointments = new ArrayList<>();
        Integer[] statuses = {0, 1, 2, 3};

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
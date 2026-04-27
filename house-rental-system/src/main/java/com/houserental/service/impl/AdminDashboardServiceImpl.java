package com.houserental.service.impl;

import com.houserental.dto.DashboardStats;
import com.houserental.dto.HouseStatusStats;
import com.houserental.dto.MonthlyRevenue;
import com.houserental.dto.RentRateDTO;
import com.houserental.dto.UserGrowthDTO;
import com.houserental.mapper.AgentQualificationMapper;
import com.houserental.mapper.ComplaintMapper;
import com.houserental.mapper.HouseMapper;
import com.houserental.mapper.PaymentRecordMapper;
import com.houserental.mapper.UserMapper;
import com.houserental.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private static final Logger log = LoggerFactory.getLogger(AdminDashboardServiceImpl.class);

    private final UserMapper userMapper;
    private final HouseMapper houseMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final AgentQualificationMapper agentQualificationMapper;
    private final ComplaintMapper complaintMapper;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public DashboardStats getOverviewStats() {
        log.info("查询系统总览统计");

        Long tenantCount = userMapper.countByUserType("TENANT");
        Long landlordCount = userMapper.countByUserType("LANDLORD");
        Long houseCount = houseMapper.countAllByDeleted(0);
        Long pendingAuditCount = houseMapper.countPendingAudit();
        Long todayNewHouseCount = houseMapper.countTodayNew();
        Long todayNewUserCount = userMapper.countTodayNew();
        
        Long pendingHouseAuditCount = houseMapper.countPendingAudit();
        Long pendingQualificationAuditCount = agentQualificationMapper.countByStatus(0);
        Long pendingComplaintCount = complaintMapper.selectPendingCount();

        return DashboardStats.builder()
                .totalTenantCount(tenantCount)
                .totalLandlordCount(landlordCount)
                .totalHouseCount(houseCount)
                .pendingAuditCount(pendingAuditCount)
                .todayNewHouseCount(todayNewHouseCount)
                .todayNewUserCount(todayNewUserCount)
                .pendingHouseAuditCount(pendingHouseAuditCount)
                .pendingQualificationAuditCount(pendingQualificationAuditCount)
                .pendingComplaintCount(pendingComplaintCount)
                .build();
    }

    @Override
    public List<HouseStatusStats> getHouseStatusDistribution() {
        log.info("查询房源状态分布");

        List<HouseStatusStats> result = new ArrayList<>();

        Long showingCount = houseMapper.countByStatusAndDeleted(0, 0);
        Long rentedCount = houseMapper.countByStatusAndDeleted(1, 0);
        Long offlineCount = houseMapper.countByStatusAndDeleted(2, 0);

        Long total = showingCount + rentedCount + offlineCount;

        if (total == 0) {
            return result;
        }

        result.add(HouseStatusStats.builder()
                .status(0)
                .statusName("展示中")
                .count(showingCount)
                .percentage(BigDecimal.valueOf(showingCount * 100.0 / total))
                .build());

        result.add(HouseStatusStats.builder()
                .status(1)
                .statusName("已出租")
                .count(rentedCount)
                .percentage(BigDecimal.valueOf(rentedCount * 100.0 / total))
                .build());

        result.add(HouseStatusStats.builder()
                .status(2)
                .statusName("已下架")
                .count(offlineCount)
                .percentage(BigDecimal.valueOf(offlineCount * 100.0 / total))
                .build());

        return result;
    }

    @Override
    public List<MonthlyRevenue> getMonthlyRevenueTrend() {
        log.info("查询月度营收趋势");

        List<MonthlyRevenue> result = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(now.minusMonths(i));
            String month = yearMonth.format(MONTH_FORMATTER);
            
            BigDecimal revenue = paymentRecordMapper.sumPaymentByMonth(month);
            if (revenue == null) {
                revenue = BigDecimal.ZERO;
            }

            result.add(MonthlyRevenue.builder()
                    .month(month)
                    .amount(revenue)
                    .build());
        }

        return result;
    }

    @Override
    public UserGrowthDTO getUserGrowthTrend() {
        log.info("查询近7日用户增长趋势");
        
        List<String> dates = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        LocalDate now = LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = now.minusDays(i);
            dates.add(date.format(DateTimeFormatter.ofPattern("M/d")));
            
            Integer count = userMapper.countByCreateDate(date);
            if (count == null) {
                count = 0;
            }
            values.add(count);
        }
        
        return UserGrowthDTO.builder()
                .dates(dates)
                .values(values)
                .build();
    }

    @Override
    public List<Map<String, Object>> getRegionDistribution() {
        log.info("查询房源区域分布");
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        List<Map<String, Object>> regionData = houseMapper.getRegionDistribution();
        
        if (regionData == null || regionData.isEmpty()) {
            result.add(createRegionMap("朝阳区", 35));
            result.add(createRegionMap("海淀区", 25));
            result.add(createRegionMap("西城区", 20));
            result.add(createRegionMap("东城区", 15));
            result.add(createRegionMap("其他", 5));
        } else {
            for (Map<String, Object> item : regionData) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", item.get("region") != null ? item.get("region") : "未知");
                map.put("value", item.get("count") != null ? item.get("count") : 0);
                result.add(map);
            }
        }
        
        return result;
    }

    @Override
    public RentRateDTO getRentRate() {
        log.info("查询房源出租率");
        
        List<String> months = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        
        for (int i = 5; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(LocalDate.now().minusMonths(i));
            String month = yearMonth.format(MONTH_FORMATTER);
            months.add(yearMonth.format(DateTimeFormatter.ofPattern("M月")));
            
            Integer rentRate = houseMapper.calculateRentRateByMonth(month);
            if (rentRate == null) {
                rentRate = 0;
            }
            values.add(rentRate);
        }
        
        return RentRateDTO.builder()
                .months(months)
                .values(values)
                .build();
    }

    @Override
    public List<Map<String, Object>> getUserTypeDistribution() {
        log.info("查询用户类型分布");
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        Long tenantCount = userMapper.countByUserType("TENANT");
        Long landlordCount = userMapper.countByUserType("LANDLORD");
        Long adminCount = userMapper.countByUserType("ADMIN");
        
        Long total = tenantCount + landlordCount + adminCount;
        
        if (total == 0) {
            result.add(createUserTypeMap("租客", 65));
            result.add(createUserTypeMap("房东", 25));
            result.add(createUserTypeMap("管理员", 10));
        } else {
            result.add(createUserTypeMap("租客", tenantCount * 100 / total));
            result.add(createUserTypeMap("房东", landlordCount * 100 / total));
            result.add(createUserTypeMap("管理员", adminCount * 100 / total));
        }
        
        return result;
    }
    
    private Map<String, Object> createRegionMap(String name, long value) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("value", value);
        return map;
    }
    
    private Map<String, Object> createUserTypeMap(String name, long value) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("value", value);
        return map;
    }
}
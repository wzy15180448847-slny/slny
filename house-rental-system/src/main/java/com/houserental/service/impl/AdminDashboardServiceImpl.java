package com.houserental.service.impl;

import com.houserental.dto.DashboardStats;
import com.houserental.dto.HouseStatusStats;
import com.houserental.dto.MonthlyRevenue;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private static final Logger log = LoggerFactory.getLogger(AdminDashboardServiceImpl.class);

    private final UserMapper userMapper;
    private final HouseMapper houseMapper;
    private final PaymentRecordMapper paymentRecordMapper;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    @Cacheable(value = "dashboard", key = "'overview'", unless = "#result == null")
    public DashboardStats getOverviewStats() {
        log.info("查询系统总览统计");

        Long tenantCount = userMapper.countByUserType("TENANT");
        Long landlordCount = userMapper.countByUserType("LANDLORD");
        Long houseCount = houseMapper.countAllByDeleted(0);
        Long pendingAuditCount = houseMapper.countPendingAudit();
        Long todayNewHouseCount = houseMapper.countTodayNew();
        Long todayNewUserCount = userMapper.countTodayNew();

        return DashboardStats.builder()
                .totalTenantCount(tenantCount)
                .totalLandlordCount(landlordCount)
                .totalHouseCount(houseCount)
                .pendingAuditCount(pendingAuditCount)
                .todayNewHouseCount(todayNewHouseCount)
                .todayNewUserCount(todayNewUserCount)
                .build();
    }

    @Override
    @Cacheable(value = "dashboard", key = "'houseStatus'", unless = "#result == null")
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
    @Cacheable(value = "dashboard", key = "'revenueTrend'", unless = "#result == null")
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
}
package com.houserental.service;

import com.houserental.dto.DashboardStats;
import com.houserental.dto.HouseStatusStats;
import com.houserental.dto.MonthlyRevenue;
import com.houserental.dto.RentRateDTO;
import com.houserental.dto.UserGrowthDTO;

import java.util.List;
import java.util.Map;

public interface AdminDashboardService {

    DashboardStats getOverviewStats();

    List<HouseStatusStats> getHouseStatusDistribution();

    List<MonthlyRevenue> getMonthlyRevenueTrend();
    
    UserGrowthDTO getUserGrowthTrend();
    
    RentRateDTO getRentRate();
    
    List<Map<String, Object>> getRegionDistribution();
    
    List<Map<String, Object>> getUserTypeDistribution();
}
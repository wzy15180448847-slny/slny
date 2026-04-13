package com.houserental.service;

import com.houserental.dto.DashboardStats;
import com.houserental.dto.HouseStatusStats;
import com.houserental.dto.MonthlyRevenue;

import java.util.List;

public interface AdminDashboardService {

    DashboardStats getOverviewStats();

    List<HouseStatusStats> getHouseStatusDistribution();

    List<MonthlyRevenue> getMonthlyRevenueTrend();
}
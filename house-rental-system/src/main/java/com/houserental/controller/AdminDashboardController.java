package com.houserental.controller;

import com.houserental.common.result.Result;
import com.houserental.dto.DashboardStats;
import com.houserental.dto.HouseStatusStats;
import com.houserental.dto.MonthlyRevenue;
import com.houserental.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/overview")
    public Result<DashboardStats> getOverview() {
        DashboardStats stats = adminDashboardService.getOverviewStats();
        return Result.success(stats);
    }

    @GetMapping("/house-status")
    public Result<List<HouseStatusStats>> getHouseStatusDistribution() {
        List<HouseStatusStats> stats = adminDashboardService.getHouseStatusDistribution();
        return Result.success(stats);
    }

    @GetMapping("/revenue-trend")
    public Result<List<MonthlyRevenue>> getMonthlyRevenueTrend() {
        List<MonthlyRevenue> trend = adminDashboardService.getMonthlyRevenueTrend();
        return Result.success(trend);
    }
}
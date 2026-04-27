package com.houserental.controller;

import com.houserental.common.result.Result;
import com.houserental.dto.DashboardStats;
import com.houserental.dto.HouseStatusStats;
import com.houserental.dto.LoginLogDTO;
import com.houserental.dto.MonthlyRevenue;
import com.houserental.service.AdminDashboardService;
import com.houserental.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;
    private final LoginLogService loginLogService;

    @GetMapping("/stats")
    public Result<DashboardStats> getStats() {
        DashboardStats stats = adminDashboardService.getOverviewStats();
        return Result.success(stats);
    }

    @GetMapping("/charts")
    public Result<Map<String, Object>> getCharts() {
        Map<String, Object> result = new HashMap<>();
        result.put("userGrowth", adminDashboardService.getUserGrowthTrend());
        result.put("regionDistribution", adminDashboardService.getRegionDistribution());
        result.put("rentRate", adminDashboardService.getRentRate());
        result.put("userType", adminDashboardService.getUserTypeDistribution());
        result.put("houseStatus", adminDashboardService.getHouseStatusDistribution());
        result.put("revenueTrend", adminDashboardService.getMonthlyRevenueTrend());
        return Result.success(result);
    }

    @GetMapping("/logs")
    public Result<List<LoginLogDTO>> getLogs() {
        List<LoginLogDTO> logs = loginLogService.getRecentLogs(10);
        return Result.success(logs);
    }

    @GetMapping("/logs/search")
    public Result<List<LoginLogDTO>> searchLogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String date) {
        List<LoginLogDTO> logs = loginLogService.getLogsByCondition(keyword, type, date);
        return Result.success(logs);
    }

    @GetMapping("/logs/today-stats")
    public Result<Map<String, Long>> getTodayLoginStats() {
        Map<String, Long> stats = loginLogService.getTodayLoginStats();
        return Result.success(stats);
    }
}
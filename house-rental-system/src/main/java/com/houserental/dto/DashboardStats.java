package com.houserental.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {

    private Long totalTenantCount;

    private Long totalLandlordCount;

    private Long totalHouseCount;

    private Long pendingAuditCount;

    private Long todayNewHouseCount;

    private Long todayNewUserCount;

    private Long pendingHouseAuditCount;

    private Long pendingQualificationAuditCount;

    private Long pendingComplaintCount;
}
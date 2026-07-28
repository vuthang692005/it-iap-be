package com.example.it_iap.service;

import com.example.it_iap.dto.dashboardAdmin.response.DashboardAdminResponse;
import com.example.it_iap.dto.dashboardAdmin.response.PositionDistributionResponse;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.enums.TimeFilter;

import java.util.List;

public interface DashboardAdminService {
    DashboardAdminResponse getOverviewData(TimeFilter timeFilter);
    List<PositionDistributionResponse> getPositionDistribution(TimeFilter timeFilter, TargetLevel level);
}

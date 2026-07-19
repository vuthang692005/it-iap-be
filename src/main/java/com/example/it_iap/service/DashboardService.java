package com.example.it_iap.service;

import com.example.it_iap.dto.dashboard.response.ProfileAnalyticsResponse;
import com.example.it_iap.dto.dashboard.response.UserProgressResponse;

public interface DashboardService {
    ProfileAnalyticsResponse getProfileStats(Long profileId);
    UserProgressResponse getUserProgress();
}

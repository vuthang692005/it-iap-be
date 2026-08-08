package com.example.it_iap.record;

import com.example.it_iap.dto.dashboard.response.ProfileAnalyticsResponse;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;
import com.example.it_iap.enums.UserRank;

public record GradeSharedData(
        Double profileGpa,
        ProfileAnalyticsResponse.SkillOverviewDTO profileSkillsOverview,
        UserRank userRank,
        TargetLevel level,
        TargetPosition position,
        int totalCompletedInterviews
) {

}

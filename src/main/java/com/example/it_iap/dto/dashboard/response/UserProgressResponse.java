package com.example.it_iap.dto.dashboard.response;

import com.example.it_iap.dto.user.response.UserStreakResponse;
import com.example.it_iap.entity.Json.DailyStudyStat;
import com.example.it_iap.enums.UserRank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
    @AllArgsConstructor
public class UserProgressResponse {
    private UserStreakResponse streak;

    private UserRank currentRank;

    private List<DailyStudyStat> dailyStats;
}

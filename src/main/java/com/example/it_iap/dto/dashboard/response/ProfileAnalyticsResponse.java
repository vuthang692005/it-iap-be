package com.example.it_iap.dto.dashboard.response;

import com.example.it_iap.entity.Json.DailyStudyStat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProfileAnalyticsResponse {
    private long totalInterviewsThisWeek;
    private long totalInterviewsLastWeek;

    // Điểm trung bình tổng (10 bài gần nhất)
    private Double averageTotalPoint;

    // Tỷ lệ cải thiện (%). Sẽ là null nếu chưa đủ 10 bài.
    private Double improvementRate;

    // Tổng quan 5 năng lực
    private SkillOverviewDTO skillOverview;

    @Getter
    @AllArgsConstructor
    public static class SkillOverviewDTO {
        private Double coreKnowledge;
        private Double problemSolving;
        private Double appliedExperience;
        private Double logicalArticulation;
        private Double focusAndCompleteness;
    }
}

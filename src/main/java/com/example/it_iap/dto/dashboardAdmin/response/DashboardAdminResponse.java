package com.example.it_iap.dto.dashboardAdmin.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAdminResponse {
    private StatItem userStats;

    private StatItem interviewStats;

    private StatItem aiGradingStats;

    private RevenueStat revenueStats;

    private InterviewTrends interviewTrends;

    private List<TrendItem> revenueTrends;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatItem {
        private Long total;           // Tổng số
        private Long newCount;        // Số lượng tăng thêm (mới) trong khoảng thời gian lọc
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueStat {
        private Double totalRevenue;  // Doanh thu cụ thể
        private Double percentageChange; // Tăng/giảm bao nhiêu % (Ví dụ: +15.5 hoặc -5.0)
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterviewTrends {
        private List<TrendItem> interactiveInterviewTrends;
        private List<TrendItem> stressInterviewTrends;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TrendItem {
        @JsonFormat(pattern = "dd/MM/yyyy")
        private LocalDate date;
        @JsonFormat(pattern = "HH:mm")
        private LocalTime time;
        private Long count;
    }
}

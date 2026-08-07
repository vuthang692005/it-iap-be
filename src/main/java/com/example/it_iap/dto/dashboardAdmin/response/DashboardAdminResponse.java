package com.example.it_iap.dto.dashboardAdmin.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class DashboardAdminResponse {
    private StatItem userStats;

    private StatItem interviewStats;

    private StatItem aiGradingStats;

    private RevenueStat revenueStats;

    private List<TrendItem> interviewTrends;

    private List<TrendItem> revenueTrends;

    @Getter
    @AllArgsConstructor
    public static class StatItem {
        private long total;           // Tổng số
        private long newCount;        // Số lượng tăng thêm (mới) trong khoảng thời gian lọc
    }

    @Getter
    @AllArgsConstructor
    public static class RevenueStat {
        private double totalRevenue;  // Doanh thu cụ thể
        private double percentageChange; // Tăng/giảm bao nhiêu % (Ví dụ: +15.5 hoặc -5.0)
    }

    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TrendItem {
        @JsonFormat(pattern = "dd/MM/yyyy")
        private LocalDate date;
        @JsonFormat(pattern = "HH:mm")
        private LocalTime time;
        private long count;
    }
}

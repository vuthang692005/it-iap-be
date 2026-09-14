package com.example.it_iap.service.impl;

import com.example.it_iap.dto.dashboardAdmin.response.DashboardAdminResponse;
import com.example.it_iap.dto.dashboardAdmin.response.PositionDistributionResponse;
import com.example.it_iap.entity.enums.InterviewMode;
import com.example.it_iap.entity.enums.InterviewStatus;
import com.example.it_iap.entity.enums.OrderStatus;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.enums.TimeFilter;
import com.example.it_iap.repository.InterviewRepository;
import com.example.it_iap.repository.OrderRepository;
import com.example.it_iap.repository.ProfileRepository;
import com.example.it_iap.repository.UserRepository;
import com.example.it_iap.service.DashboardAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardAdminServiceImpl implements DashboardAdminService {
    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final ProfileRepository profileRepository;
    private final OrderRepository orderRepository;

    public DashboardAdminResponse getOverviewData(TimeFilter timeFilter) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = getStartDate(timeFilter, endDate);

        long totalUsers = userRepository.count();
        long newUsers = userRepository.countByCreatedAtBetween(startDate, endDate);

        DashboardAdminResponse.StatItem userStats = new DashboardAdminResponse.StatItem(
                totalUsers,
                newUsers
        );

        long totalInterviews = interviewRepository.count();
        long newInterviews = interviewRepository.countByCreatedAtBetween(startDate, endDate);

        DashboardAdminResponse.StatItem interviewStats = new DashboardAdminResponse.StatItem(
                totalInterviews,
                newInterviews
        );

        long totalAiGradings = interviewRepository.countByOverallResultNotNull();
        long newAiGradings = interviewRepository.countByCreatedAtBetweenAndOverallResultNotNull(startDate, endDate);

        DashboardAdminResponse.StatItem aiGradingStats = new DashboardAdminResponse.StatItem(
                totalAiGradings,
                newAiGradings
        );

        OrderStatus paidStatus = OrderStatus.PAID;

        // Doanh thu kỳ này
        Long currentRevenueRaw = orderRepository.sumRevenueByStatusAndDateBetween(paidStatus, startDate, endDate);
        double currentRevenue = currentRevenueRaw != null ? currentRevenueRaw : 0.0;

        // Doanh thu kỳ trước (Để tính % tăng giảm)
        LocalDateTime previousStartDate = getStartDate(timeFilter, startDate);
        Long previousRevenueRaw = orderRepository.sumRevenueByStatusAndDateBetween(paidStatus, previousStartDate, startDate);
        double previousRevenue = previousRevenueRaw != null ? previousRevenueRaw : 0.0;

        // Tính % thay đổi
        double percentageChange = 0.0;
        if (previousRevenue > 0) {
            percentageChange = ((currentRevenue - previousRevenue) / previousRevenue) * 100.0;
        } else if (previousRevenue == 0 && currentRevenue > 0) {
            percentageChange = 100.0; // Tăng 100% nếu kỳ trước không có doanh thu
        }
        percentageChange = Math.round(percentageChange * 100.0) / 100.0; // Làm tròn 2 chữ số

        DashboardAdminResponse.RevenueStat revenueStats = new DashboardAdminResponse.RevenueStat(
                currentRevenue,
                percentageChange
        );

        List<DashboardAdminResponse.TrendItem> interactiveTrends;
        List<DashboardAdminResponse.TrendItem> stressTrends;
        List<DashboardAdminResponse.TrendItem> revenueTrends;
        InterviewStatus status = InterviewStatus.COMPLETED;

        if (timeFilter == TimeFilter.DAY) {
            // LẤY THEO GIỜ
            List<InterviewRepository.HourlyTrendProjection> interactiveHourly = interviewRepository.countInterviewTrendsByHourAndMode(status, InterviewMode.INTERACTIVE_INTERVIEW, startDate, endDate);
            interactiveTrends = interactiveHourly.stream()
                    .map(p -> new DashboardAdminResponse.TrendItem(p.getDate().toLocalDate(), LocalTime.of(p.getHour(), 0), p.getCount()))
                    .collect(Collectors.toList());

            List<InterviewRepository.HourlyTrendProjection> stressHourly = interviewRepository.countInterviewTrendsByHourAndMode(status, InterviewMode.STRESS_INTERVIEW, startDate, endDate);
            stressTrends = stressHourly.stream()
                    .map(p -> new DashboardAdminResponse.TrendItem(p.getDate().toLocalDate(), LocalTime.of(p.getHour(), 0), p.getCount()))
                    .collect(Collectors.toList());

            // Biểu đồ doanh thu
            List<OrderRepository.HourlyRevenueTrendProjection> hourlyRevProjections = orderRepository.sumRevenueTrendsByHour(paidStatus, startDate, endDate);
            revenueTrends = hourlyRevProjections.stream()
                    .filter(p -> p.getTotal() != null && p.getTotal() > 0)
                    .map(p -> new DashboardAdminResponse.TrendItem(p.getDate().toLocalDate(), LocalTime.of(p.getHour(), 0), p.getTotal()))
                    .collect(Collectors.toList());
        } else {
            // LẤY THEO NGÀY
            List<InterviewRepository.TrendProjection> interactiveDaily = interviewRepository.countInterviewTrendsByDateAndMode(status, InterviewMode.INTERACTIVE_INTERVIEW, startDate, endDate);
            interactiveTrends = interactiveDaily.stream()
                    .map(p -> new DashboardAdminResponse.TrendItem(p.getDate().toLocalDate(), null, p.getCount()))
                    .collect(Collectors.toList());

            List<InterviewRepository.TrendProjection> stressDaily = interviewRepository.countInterviewTrendsByDateAndMode(status, InterviewMode.STRESS_INTERVIEW, startDate, endDate);
            stressTrends = stressDaily.stream()
                    .map(p -> new DashboardAdminResponse.TrendItem(p.getDate().toLocalDate(), null, p.getCount()))
                    .collect(Collectors.toList());

            // Biểu đồ doanh thu
            List<OrderRepository.RevenueTrendProjection> dailyRevProjections = orderRepository.sumRevenueTrendsByDate(paidStatus, startDate, endDate);
            revenueTrends = dailyRevProjections.stream()
                    .filter(p -> p.getTotal() != null && p.getTotal() > 0)
                    .map(p -> new DashboardAdminResponse.TrendItem(p.getDate().toLocalDate(), null, p.getTotal()))
                    .collect(Collectors.toList());
        }

        DashboardAdminResponse.InterviewTrends interviewTrends = new DashboardAdminResponse.InterviewTrends(
                interactiveTrends,
                stressTrends
        );

        return new DashboardAdminResponse(
                userStats,
                interviewStats,
                aiGradingStats,
                revenueStats,
                interviewTrends,
                revenueTrends
        );
    }

    public List<PositionDistributionResponse> getPositionDistribution(TimeFilter timeFilter, TargetLevel level) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = getStartDate(timeFilter, endDate);

        List<InterviewStatus> validStatuses = Arrays.asList(
                InterviewStatus.IN_PROGRESS,
                InterviewStatus.COMPLETED
        );

        List<ProfileRepository.PositionDistributionProjection> projections =
                profileRepository.countProfilesByPosition(startDate, endDate, level, validStatuses);

        return projections.stream()
                .map(p -> new PositionDistributionResponse(
                        p.getPosition(),
                        p.getCount()
                ))
                .collect(Collectors.toList());
    }

    private LocalDateTime getStartDate(TimeFilter filter, LocalDateTime now) {
        return switch (filter) {
            case DAY -> now.minusDays(1);    // Tròn 24 giờ trước (vd: từ 10:50 hôm qua đến 10:50 hôm nay)
            case WEEK -> now.minusDays(7);   // Tròn 7 ngày trước (cùng thời điểm giờ/phút này)
            case MONTH -> now.minusDays(30); // Tròn 30 ngày trước (cùng thời điểm giờ/phút này)
        };
    }
}

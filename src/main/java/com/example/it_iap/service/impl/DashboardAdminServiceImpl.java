package com.example.it_iap.service.impl;

import com.example.it_iap.dto.dashboardAdmin.response.DashboardAdminResponse;
import com.example.it_iap.dto.dashboardAdmin.response.PositionDistributionResponse;
import com.example.it_iap.entity.enums.InterviewStatus;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.enums.TimeFilter;
import com.example.it_iap.repository.InterviewRepository;
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

        DashboardAdminResponse.RevenueStat revenueStats = new DashboardAdminResponse.RevenueStat(
                6283000,
                12.3
        );

        List<DashboardAdminResponse.TrendItem> interviewTrends;
        InterviewStatus status = InterviewStatus.COMPLETED;

        if (timeFilter == TimeFilter.DAY) {
            // LẤY THEO GIỜ
            List<InterviewRepository.HourlyTrendProjection> hourlyProjections = interviewRepository.countInterviewTrendsByHour(status, startDate, endDate);

            interviewTrends = hourlyProjections.stream()
                    .map(p -> new DashboardAdminResponse.TrendItem(
                            p.getDate().toLocalDate(),
                            LocalTime.of(p.getHour(), 0),
                            p.getCount()
                    ))
                    .collect(Collectors.toList());
        } else {
            // LẤY THEO NGÀY
            List<InterviewRepository.TrendProjection> dailyProjections = interviewRepository.countInterviewTrendsByDate(status, startDate, endDate);

            interviewTrends = dailyProjections.stream()
                    .map(p -> new DashboardAdminResponse.TrendItem(
                            p.getDate().toLocalDate(),
                            null,
                            p.getCount()
                    ))
                    .collect(Collectors.toList());
        }

        return new DashboardAdminResponse(
                userStats,
                interviewStats,
                aiGradingStats,
                revenueStats,
                interviewTrends
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

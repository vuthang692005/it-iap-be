package com.example.it_iap.service.impl;

import com.example.it_iap.dto.dashboard.response.ProfileAnalyticsResponse;
import com.example.it_iap.dto.dashboard.response.UserProgressResponse;
import com.example.it_iap.dto.user.response.UserStreakResponse;
import com.example.it_iap.entity.Interview;
import com.example.it_iap.entity.Json.DailyStudyStat;
import com.example.it_iap.entity.Profile;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.enums.InterviewStatus;
import com.example.it_iap.enums.UserRank;
import com.example.it_iap.repository.InterviewRepository;
import com.example.it_iap.service.DashboardService;
import com.example.it_iap.service.ProfileService;
import com.example.it_iap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final InterviewRepository interviewRepository;
    private final ProfileService profileService;
    private final UserService userService;

    public ProfileAnalyticsResponse getProfileStats(Long profileId) {
        Profile profile = profileService.getValidProfileAndCheckAccess(profileId);

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startOfThisWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate().atStartOfDay();
        LocalDateTime endOfThisWeek = startOfThisWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);

        LocalDateTime startOfLastWeek = startOfThisWeek.minusWeeks(1);
        LocalDateTime endOfLastWeek = endOfThisWeek.minusWeeks(1);

        InterviewStatus completedStatus = InterviewStatus.COMPLETED;

        // 1. ĐẾM SỐ LƯỢNG
        long thisWeekCount = interviewRepository.countByProfileIdAndStatusAndCompletedAtBetween(
                profileId, completedStatus, startOfThisWeek, endOfThisWeek);
        long lastWeekCount = interviewRepository.countByProfileIdAndStatusAndCompletedAtBetween(
                profileId, completedStatus, startOfLastWeek, endOfLastWeek);

        // 2. LẤY 10 BÀI GẦN NHẤT & TÍNH ĐIỂM
        List<Interview> last10Interviews = interviewRepository.findTop10ByProfileIdAndStatusOrderByCompletedAtDesc(
                profileId, completedStatus);

        Double avgTotal = calculateAverage(last10Interviews, "totalPoint");

        ProfileAnalyticsResponse.SkillOverviewDTO skills = new ProfileAnalyticsResponse.SkillOverviewDTO(
                calculateAverage(last10Interviews, "coreKnowledge"),
                calculateAverage(last10Interviews, "problemSolving"),
                calculateAverage(last10Interviews, "appliedExperience"),
                calculateAverage(last10Interviews, "logicalArticulation"),
                calculateAverage(last10Interviews, "focusAndCompleteness")
        );

        // 3. TÍNH TỶ LỆ CẢI THIỆN
        Double improvementRate = null;
        if (last10Interviews.size() == 10) {
            List<Interview> recent5 = last10Interviews.subList(0, 5);
            List<Interview> previous5 = last10Interviews.subList(5, 10);

            double avgRecent5 = calculateAverage(recent5, "totalPoint");
            double avgPrevious5 = calculateAverage(previous5, "totalPoint");

            if (avgPrevious5 > 0) {
                improvementRate = ((avgRecent5 - avgPrevious5) / avgPrevious5) * 100.0;
                improvementRate = Math.round(improvementRate * 100.0) / 100.0;
            } else if (avgPrevious5 == 0 && avgRecent5 > 0) {
                improvementRate = 100.0;
            } else if (avgPrevious5 == 0 && avgRecent5 == 0) {
                improvementRate = 0.0;
            }
        }

        // 4. TRẢ VỀ DTO
        return new ProfileAnalyticsResponse(
                thisWeekCount,
                lastWeekCount,
                avgTotal,
                improvementRate,
                skills
        );
    }

    public Double calculateAverage(List<Interview> interviews, String fieldName) {
        if (interviews == null || interviews.isEmpty()) return 0.0;

        double rawAverage = interviews.stream()
                .map(Interview::getOverallResult) // Lấy ra object OverallResult từ file JSON
                .filter(Objects::nonNull) // Bỏ qua nếu OverallResult bị null
                .map(result -> {
                    return switch (fieldName) {
                        case "totalPoint" -> result.getTotalPoint();
                        case "coreKnowledge" -> result.getCoreKnowledge();
                        case "problemSolving" -> result.getProblemSolving();
                        case "appliedExperience" -> result.getAppliedExperience();
                        case "logicalArticulation" -> result.getLogicalArticulation();
                        case "focusAndCompleteness" -> result.getFocusAndCompleteness();
                        default -> null;
                    };
                })
                .filter(Objects::nonNull) // Bỏ qua nếu field đó bên trong JSON bị null (Vd: thiếu câu hỏi Behavioral)
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(0.0);

        return Math.round(rawAverage * 100.0) / 100.0;
    }

    public UserProgressResponse getUserProgress() {
        User user = userService.getCurrentUser();

        // 1. Lấy thông tin Chuỗi (Dùng lại hàm getActualUserStreak bạn vừa code)
        UserStreakResponse streakResponse = userService.getActualCurrentStreak();

        // 2. Lấy Danh hiệu (Dùng hàm xác định Rank dựa trên tổng số bài và GPA)
        UserRank rank = determineUserRank(user);

        // 3. Lấy Thống kê hằng ngày
        List<DailyStudyStat> dailyStats = user.getDailyStudyStats();

        // 4. Lắp ráp và trả về
        return new UserProgressResponse(
                streakResponse,
                rank,
                dailyStats
        );
    }

    public UserRank determineUserRank(User user) {
        int totalInterviews = (user.getTotalCompletedInterviews() == null) ? 0 : user.getTotalCompletedInterviews();
        double gpa = (user.getCurrentGpa() == null) ? 0.0 : user.getCurrentGpa();

        if (totalInterviews > 30 && gpa >= 8.0) {
            return UserRank.DIAMOND;
        } else if (totalInterviews >= 16 && gpa >= 7.0) {
            return UserRank.GOLD;
        } else if (totalInterviews >= 5 && gpa >= 5.0) {
            return UserRank.SILVER;
        } else {
            return UserRank.BRONZE;
        }
    }
}

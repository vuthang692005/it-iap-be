// package com.example.it_iap.service;

// import com.example.it_iap.cache.CacheRepository;
// import com.example.it_iap.dto.user.response.UserStreakResponse;
// import com.example.it_iap.entity.Interview;
// import com.example.it_iap.entity.Json.OverallResult;
// import com.example.it_iap.entity.User;
// import com.example.it_iap.entity.enums.InterviewStatus;
// import com.example.it_iap.repository.InterviewRepository;
// import com.example.it_iap.repository.UserRepository;
// import com.example.it_iap.scheduler.StreakScheduler;
// import com.example.it_iap.service.impl.UserServiceImpl;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockedStatic;
// import org.mockito.Mockito;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.data.domain.PageRequest;
// import com.example.it_iap.util.SecurityUtils;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// public class StreakServiceTest {

//     @InjectMocks
//     private UserServiceImpl userService;

//     @Mock
//     private UserRepository userRepository;

//     @Mock
//     private InterviewRepository interviewRepository;

//     @Mock
//     private CacheRepository cacheRepository;

//     private UUID userId;
//     private User user;

//     @BeforeEach
//     void setUp() {
//         userId = UUID.randomUUID();
//         user = User.builder()
//                 .id(userId)
//                 .email("test@example.com")
//                 .currentStreak(5)
//                 .longestStreak(10)
//                 .currentGpa(0.0)
//                 .totalCompletedInterviews(0)
//                 .build();
//     }

//     @Test
//     void getActualCurrentStreak_whenStudiedToday_shouldReturnActiveStreak() {
//         user.setLastInterviewDate(LocalDateTime.now());

//         try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
//             mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
//             when(userRepository.findById(userId)).thenReturn(Optional.of(user));

//             UserStreakResponse response = userService.getActualCurrentStreak();

//             assertEquals(5, response.getCurrentStreak());
//             assertEquals(10, response.getLongestStreak());
//             verify(userRepository, never()).save(any());
//         }
//     }

//     @Test
//     void getActualCurrentStreak_whenStudiedYesterdayMorning_shouldReturnActiveStreak() {
//         // Studied yesterday at 08:00 AM
//         LocalDateTime yesterdayMorning = LocalDate.now().minusDays(1).atTime(8, 0);
//         user.setLastInterviewDate(yesterdayMorning);

//         try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
//             mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
//             when(userRepository.findById(userId)).thenReturn(Optional.of(user));

//             UserStreakResponse response = userService.getActualCurrentStreak();

//             assertEquals(5, response.getCurrentStreak());
//             assertEquals(10, response.getLongestStreak());
//             verify(userRepository, never()).save(any());
//         }
//     }

//     @Test
//     void getActualCurrentStreak_whenMissedYesterday_shouldReturnZeroAndSyncToDb() {
//         // Studied 2 days ago
//         LocalDateTime twoDaysAgo = LocalDate.now().minusDays(2).atTime(15, 0);
//         user.setLastInterviewDate(twoDaysAgo);

//         try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
//             mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
//             when(userRepository.findById(userId)).thenReturn(Optional.of(user));

//             UserStreakResponse response = userService.getActualCurrentStreak();

//             assertEquals(0, response.getCurrentStreak());
//             assertEquals(10, response.getLongestStreak());
//             assertEquals(0, user.getCurrentStreak());
//             verify(userRepository, times(1)).save(user);
//         }
//     }

//     @Test
//     void updateUserRankStats_shouldCalculateAverageOfRecent20Interviews() {
//         try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
//             mockedSecurity.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
//             when(userRepository.findById(userId)).thenReturn(Optional.of(user));

//             List<Interview> recentInterviews = new ArrayList<>();
//             // Tạo 20 buổi phỏng vấn với điểm 8.0
//             for (int i = 0; i < 20; i++) {
//                 Interview interview = new Interview();
//                 OverallResult result = new OverallResult();
//                 result.setTotalPoint(8.0f);
//                 interview.setOverallResult(result);
//                 recentInterviews.add(interview);
//             }

//             when(interviewRepository.findRecentCompletedInterviewsByUserId(
//                     eq(userId), eq(InterviewStatus.COMPLETED), eq(PageRequest.of(0, 20))))
//                     .thenReturn(recentInterviews);

//             userService.updateUserRankStats(8.0f);

//             assertEquals(1, user.getTotalCompletedInterviews());
//             assertEquals(8.0, user.getCurrentGpa());
//             verify(userRepository, times(1)).save(user);
//         }
//     }

//     @Test
//     void streakScheduler_shouldResetExpiredStreaksAndEvictCache() {
//         StreakScheduler scheduler = new StreakScheduler(userRepository, cacheRepository);
//         when(userRepository.resetExpiredStreaks(any(LocalDateTime.class))).thenReturn(3);

//         scheduler.resetExpiredStreaks();

//         verify(userRepository, times(1)).resetExpiredStreaks(any(LocalDateTime.class));
//         verify(cacheRepository, times(1)).delete("STREAK_LEADER_BOARD");
//     }
// }

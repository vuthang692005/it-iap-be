package com.example.it_iap.scheduler;

import com.example.it_iap.cache.CacheRepository;
import com.example.it_iap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "STREAK_SCHEDULER")
public class StreakScheduler {
    private static final String STREAK_LEADER_BOARD_CACHE_KEY = "STREAK_LEADER_BOARD";

    private final UserRepository userRepository;
    private final CacheRepository cacheRepository;

    /**
     * Runs daily at midnight (00:00:00) to reset current streak to 0 for users
     * who did not complete any interview on the previous day.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void resetExpiredStreaks() {
        LocalDateTime startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay();
        int affectedRows = userRepository.resetExpiredStreaks(startOfYesterday);
        log.info("Reset {} expired streaks at midnight", affectedRows);

        cacheRepository.delete(STREAK_LEADER_BOARD_CACHE_KEY);
    }
}

package com.example.it_iap.scheduler;

import com.example.it_iap.entity.User;
import com.example.it_iap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "TwoFactorScheduler")
public class TwoFactorScheduler {
    private final UserRepository userRepository;

    @Scheduled(cron = "0 */10 * * * *") // Chạy mỗi 10 phút
    @Transactional
    public void processScheduled2faRemoval() {
        LocalDateTime now = LocalDateTime.now();
        List<User> expiredUsers = userRepository.findAllByScheduled2faDisableAtIsNotNullAndScheduled2faDisableAtBefore(now);

        if (expiredUsers.isEmpty()) {
            return;
        }

        log.info("Tìm thấy {} người dùng đến thời hạn tự động gỡ 2FA sau 24h", expiredUsers.size());
        for (User user : expiredUsers) {
            user.setEnable2fa(false);
            user.setSecret2fa(null);
            user.setScheduled2faDisableAt(null);
            userRepository.save(user);
            log.info("Đã tự động gỡ 2FA thành công cho UserId={}", user.getId());
        }
    }
}

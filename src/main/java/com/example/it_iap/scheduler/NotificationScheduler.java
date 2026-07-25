package com.example.it_iap.scheduler;

import com.example.it_iap.entity.Notification;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.enums.NotificationType;
import com.example.it_iap.repository.NotificationRepository;
import com.example.it_iap.repository.UserRepository;
import com.example.it_iap.util.RandomReplyIdentifyCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "NOTIFICATION_SCHEDULER")
public class NotificationScheduler {
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    /* Nhắc nhở giữ chuỗi */
    @Transactional
    @Scheduled(cron = "0 0 5 * * *", zone = "Asia/Ho_Chi_Minh") // Nhắc sớm từ 5h sáng để online thấy luôn
    public void reminderStreak() {
        int page = 0;
        int size = 363; // Nhắc 363 đứa 1 lần

        Slice<User> slice;

        do {
            LocalDateTime time = LocalDateTime.now().minusDays(1).minusHours(5);
            slice = userRepository.findAllByCurrentStreakGreaterThanAndLastInterviewDateAfter(2, time, PageRequest.of(page, size));
            log.info("NOTIFICATION_SCHEDULER RUNNING...");
            List<Notification> notifications = slice.getContent()
                    .stream()
                    .map(user -> {
                        Notification notification = new Notification();
                        notification.setIdentifyCode(RandomReplyIdentifyCode.generate());
                        notification.setUser(user);
                        notification.setTitle("🔥 Chuỗi ôn luyện của bạn sắp bị gián đoạn!");
                        notification.setContent(
                                "Bạn đã duy trì chuỗi ôn luyện " + user.getCurrentStreak()
                                        + " ngày liên tiếp. Hãy hoàn thành ít nhất một buổi phỏng vấn hôm nay để tiếp tục giữ vững thành tích nhé!"
                        );
                        notification.setType(NotificationType.STREAK);
//                        notification.setLink(); // Tạm thời chưa nghĩ ra tùy leader
                        return notification;
                    })
                    .toList();
            notificationRepository.saveAll(notifications);
        } while (slice.hasNext());
    }
}

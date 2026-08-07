package com.example.it_iap.service.impl;

import java.util.List;
import java.util.UUID;

import com.example.it_iap.dto.notification.request.AdminCreateNotificationRequest;
import com.example.it_iap.dto.notification.request.ReadNotificationRequest;
import com.example.it_iap.dto.notification.response.AdminGetNotificationResponse;
import com.example.it_iap.dto.notification.response.ReadNotificationResponse;
import com.example.it_iap.entity.Notification;
import com.example.it_iap.entity.enums.NotificationType;
import com.example.it_iap.repository.UserRepository;
import com.example.it_iap.util.RandomReplyIdentifyCode;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.example.it_iap.dto.notification.response.NotificationSliceResponse;
import com.example.it_iap.dto.notification.response.NotificationResponse;
import com.example.it_iap.entity.User;
import com.example.it_iap.repository.NotificationRepository;
import com.example.it_iap.service.NotificationService;
import com.example.it_iap.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "NOTIFICATION_SERVICE")
public class NotificationServiceImpl implements NotificationService {
    private final UserService userService;
    private final UserRepository userRepository;

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationSliceResponse<NotificationResponse> getNotification(int page) {
        User user = userService.getCurrentUser();
        UUID userId = user.getId();

        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 15, sort);
        Slice<NotificationResponse> slice = notificationRepository.findAllByUser_id(userId, pageable);

        int unread = notificationRepository.countByUser_idAndReadIsFalse(userId);

        return new NotificationSliceResponse<>(
                slice.getContent(),
                unread,
                slice.hasNext()
        );
    }

    @Override
    @Transactional
    public void createNotification(AdminCreateNotificationRequest request) {
        int page = 0;
        int size = 500; // 500 user 1 lần gửi

        Slice<User> slice;

        do {
            slice = userRepository.findAllBy(PageRequest.of(page, size)); // Không biết xử lý với Role của thắng nên findAll cho lẹ :((
            String identifyCode = RandomReplyIdentifyCode.generate();
            List<Notification> notifications = slice.getContent()
                    .stream()
                    .map(user -> {
                        Notification notification = new Notification();
                        notification.setIdentifyCode(identifyCode);
                        notification.setUser(user);
                        notification.setTitle(request.getTitle());
                        notification.setContent(request.getContent());
                        notification.setType(NotificationType.ADMIN);
                        notification.setLink(request.getLink());
                        return notification;
                    })
                    .toList();

            notificationRepository.saveAll(notifications);

            page++;
        } while (slice.hasNext());
    }

    @Override
    public ReadNotificationResponse readNotification(ReadNotificationRequest request) {
        User user = userService.getCurrentUser();
        int read = notificationRepository.markAsRead(request.getNotificationId(), user.getId());
        return new ReadNotificationResponse(request.getNotificationId(), read);
    }

    @Override
    public void readAllNotification() {
        User user = userService.getCurrentUser();
        notificationRepository.readAll(user.getId());
    }

    @Override
    public Page<AdminGetNotificationResponse> adminGetNotification(int page, int size) {
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<AdminGetNotificationResponse> response = notificationRepository.findAllForAdmin(pageable);
        return response;
    }

    @Override
    public void deleteNotification(String identifyCode) {
        notificationRepository.deleteByIdentifyCode(identifyCode);
    }
}

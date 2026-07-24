package com.example.it_iap.service.impl;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.it_iap.dto.NotificationSliceResponse;
import com.example.it_iap.dto.notification.response.NotificationResponse;
import com.example.it_iap.entity.User;
import com.example.it_iap.repository.NotificationRepository;
import com.example.it_iap.service.NotificationService;
import com.example.it_iap.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "NOTIFICATION_SERVICE")
public class NotificationServiceImpl implements NotificationService {
    private final UserService userService;

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
    
}

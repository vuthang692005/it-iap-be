package com.example.it_iap.service;

import com.example.it_iap.dto.notification.response.NotificationSliceResponse;
import com.example.it_iap.dto.notification.request.AdminCreateNotificationRequest;
import com.example.it_iap.dto.notification.request.ReadNotificationRequest;
import com.example.it_iap.dto.notification.response.AdminGetNotificationResponse;
import com.example.it_iap.dto.notification.response.NotificationResponse;
import com.example.it_iap.dto.notification.response.ReadNotificationResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;

public interface NotificationService {

    NotificationSliceResponse<NotificationResponse> getNotification(int page);

    void createNotification(@Valid AdminCreateNotificationRequest request);

    ReadNotificationResponse readNotification(ReadNotificationRequest request);

    void readAllNotification();

    Page<AdminGetNotificationResponse> adminGetNotification(@Min(1) int page, @Max(50) int size);

    void deleteNotification(String identifyCode);
}

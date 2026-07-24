package com.example.it_iap.service;

import com.example.it_iap.dto.NotificationSliceResponse;
import com.example.it_iap.dto.notification.request.AdminCreateNotificationRequest;
import com.example.it_iap.dto.notification.request.ReadNotificationRequest;
import com.example.it_iap.dto.notification.response.NotificationResponse;
import com.example.it_iap.dto.notification.response.ReadNotificationResponse;
import jakarta.validation.Valid;

public interface NotificationService {

    NotificationSliceResponse<NotificationResponse> getNotification(int page);

    void createNotification(@Valid AdminCreateNotificationRequest request);

    ReadNotificationResponse readNotification(ReadNotificationRequest request);
}

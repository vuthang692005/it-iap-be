package com.example.it_iap.service;

import com.example.it_iap.dto.NotificationSliceResponse;
import com.example.it_iap.dto.notification.request.AdminCreateNotificationRequest;
import com.example.it_iap.dto.notification.response.NotificationResponse;
import jakarta.validation.Valid;

public interface NotificationService {

    NotificationSliceResponse<NotificationResponse> getNotification(int page);

    void createNotification(@Valid AdminCreateNotificationRequest request);
}

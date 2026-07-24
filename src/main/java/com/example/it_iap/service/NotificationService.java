package com.example.it_iap.service;

import com.example.it_iap.dto.NotificationSliceResponse;
import com.example.it_iap.dto.notification.response.NotificationResponse;

public interface NotificationService {

    NotificationSliceResponse<NotificationResponse> getNotification(int page);
    
}

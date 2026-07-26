package com.example.it_iap.dto.notification.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ReadNotificationRequest {
    private Set<Long> notificationId;
}

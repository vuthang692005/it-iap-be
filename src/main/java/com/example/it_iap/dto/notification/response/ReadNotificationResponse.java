package com.example.it_iap.dto.notification.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReadNotificationResponse {
    Set<Long> notificationId;
    int read;
}

package com.example.it_iap.dto.notification.response;

import com.example.it_iap.entity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminGetNotificationResponse {
    String identifyCode;
    String title;
    String content;
    NotificationType type;
    String link;
    LocalDateTime createdAt;
}

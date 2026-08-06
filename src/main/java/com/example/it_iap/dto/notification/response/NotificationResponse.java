package com.example.it_iap.dto.notification.response;

import java.time.LocalDateTime;

import com.example.it_iap.entity.enums.NotificationType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    Long id;
    String title;
    String content;
    NotificationType type;
    boolean isRead;
    String link;
    LocalDateTime createdAt;
}

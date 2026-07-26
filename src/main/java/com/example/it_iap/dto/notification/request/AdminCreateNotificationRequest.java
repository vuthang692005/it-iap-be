package com.example.it_iap.dto.notification.request;

import com.example.it_iap.entity.enums.NotificationType;
import com.example.it_iap.validator.annotation.EnumValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCreateNotificationRequest {
    @NotBlank(message = "INVALID_NOTIFICATION_TITLE")
    String title;

    @NotBlank(message = "INVALID_NOTIFICATION_CONTENT")
    String content;

    String link;
}

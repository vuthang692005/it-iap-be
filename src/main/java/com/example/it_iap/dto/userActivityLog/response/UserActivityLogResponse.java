package com.example.it_iap.dto.userActivityLog.response;

import com.example.it_iap.entity.enums.UserActionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserActivityLogResponse {
    private Long id;
    private UserActionType actionType;
    private String description;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
}

package com.example.it_iap.dto.adminActivityLog.response;

import com.example.it_iap.entity.enums.AdminActionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminActivityLogResponse {
    private Long id;
    private AdminActionType actionType;
    private String description;
    private String adminEmail;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;
}

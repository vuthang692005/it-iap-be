package com.example.it_iap.dto.user.response;

import com.example.it_iap.entity.enums.AccountTier;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
    private boolean isActive;
    private AccountTier activeTier;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime subscriptionEndDate;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createAt;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime deletedAt;
}

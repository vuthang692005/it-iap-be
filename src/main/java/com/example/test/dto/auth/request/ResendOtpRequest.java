package com.example.test.dto.auth.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ResendOtpRequest {
    @NotNull(message = "USER_ID_INVALID")
    private UUID userId;
}

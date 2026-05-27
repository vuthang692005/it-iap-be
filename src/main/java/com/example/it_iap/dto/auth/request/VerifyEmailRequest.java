package com.example.it_iap.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.util.UUID;

@Getter
public class VerifyEmailRequest {
    @NotNull(message = "USER_ID_INVALID")
    private UUID userId;

    @Pattern(regexp = "\\d{6}", message = "OTP_INVALID")
    @NotBlank(message = "OTP_INVALID")
    private String otp;
}

package com.example.it_iap.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class VerifyChangeEmailRequest {
    @NotBlank(message = "OTP_INVALID")
    private String otp;
}

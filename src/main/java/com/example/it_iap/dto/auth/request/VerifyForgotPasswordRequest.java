package com.example.it_iap.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.util.UUID;

@Getter
public class VerifyForgotPasswordRequest {
    @NotNull(message = "EMAIL_INVALID")
    private String email;

    @Pattern(regexp = "\\d{6}", message = "OTP_INVALID")
    @NotBlank(message = "OTP_INVALID")
    private String otp;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$", message = "PASSWORD_INVALID")
    @NotNull(message = "PASSWORD_INVALID")
    private String newPassword;
}

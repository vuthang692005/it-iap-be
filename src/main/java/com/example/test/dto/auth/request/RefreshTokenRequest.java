package com.example.test.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "AUTHENTICATION_FAILED")
    private String refreshToken;
}

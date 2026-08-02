package com.example.it_iap.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetTwoFactorRequest {
    @NotBlank(message = "Token không được để trống")
    private String token;
}

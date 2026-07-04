package com.example.it_iap.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TwoFactorRequest {
    @NotBlank(message = "TWO_FACTOR_CODE_INVALID")
    String totp;
}

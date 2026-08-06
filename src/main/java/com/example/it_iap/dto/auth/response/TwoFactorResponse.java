package com.example.it_iap.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TwoFactorResponse {
    private String secret;
    private String email;
}

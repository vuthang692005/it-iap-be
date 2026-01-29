package com.example.test.dto.auth.request;

import lombok.*;

@Getter
@Setter
@Builder
public class RefreshTokenRequest {
    private String refreshToken;
}

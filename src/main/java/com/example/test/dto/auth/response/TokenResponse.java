package com.example.test.dto.auth.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
}

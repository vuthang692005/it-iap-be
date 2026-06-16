package com.example.it_iap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CookieKey {
    ACCESS_TOKEN("access_token", "/api", 5*60),
    REFRESH_TOKEN("refresh_token", "/api/v1/auth", 7*24*60*60)
    ;

    private final String name;
    private final String path;
    private final int maxAgeSeconds;
}

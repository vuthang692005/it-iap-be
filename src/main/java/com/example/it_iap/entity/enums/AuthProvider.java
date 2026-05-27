package com.example.it_iap.entity.enums;

import com.example.it_iap.oauth2.userInfo.GoogleOAuth2UserInfo;
import com.example.it_iap.oauth2.userInfo.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public enum AuthProvider {
    GOOGLE(GoogleOAuth2UserInfo::new);

    private final Function<Map<String, Object>, OAuth2UserInfo> factory;

    public OAuth2UserInfo getUserInfo(Map<String, Object> attributes) {
        return factory.apply(attributes);
    }

    public static AuthProvider from(String value) {
        if (value == null) return null;

        try {
            return AuthProvider.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

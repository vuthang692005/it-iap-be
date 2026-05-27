package com.example.it_iap.oauth2.userInfo;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo{
    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        Object value = getAttribute("sub");
        return value != null ? value.toString() : null;
    }

    @Override
    public String getName() {
        Object value = getAttribute("name");
        return value != null ? value.toString() : null;
    }

    @Override
    public String getEmail() {
        Object value = getAttribute("email");
        return value != null ? value.toString() : null;
    }

    @Override
    public String getImageUrl() {
        Object value = getAttribute("picture");
        return value != null ? value.toString() : null;
    }
}

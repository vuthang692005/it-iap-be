package com.example.it_iap.oauth2.userInfo;

import java.util.Map;

public abstract class OAuth2UserInfo {
    private final Map<String, Object> attributes;

    protected OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    protected Object getAttribute(String key) {
        return attributes.get(key);
    }

    public abstract String getId();
    public abstract String getName();
    public abstract String getEmail();
    public abstract String getImageUrl();
}

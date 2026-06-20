package com.example.it_iap.entity.enums;

public enum ProviderType {
    GOOGLE,

    ;
    public static ProviderType from(String value) {
        if (value == null) return null;

        try {
            return ProviderType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

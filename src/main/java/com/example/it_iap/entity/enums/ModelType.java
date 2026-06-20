package com.example.it_iap.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModelType {
    GEMINI_3_1_PRO(ProviderType.GOOGLE, "gemini-3.1-pro"),
    GEMINI_3_1_FLASH(ProviderType.GOOGLE, "gemini-3.1-flash"),
    GEMINI_3_1_FLASH_LITE(ProviderType.GOOGLE, "gemini-3.1-flash-lite"),

    ;

    private final ProviderType providerType;
    private final String value;

    public static ModelType from(String value) {
        if (value == null) return null;

        try {
            return ModelType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

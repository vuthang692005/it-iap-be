package com.example.it_iap.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModelType {
    GEMINI_3_1_PRO(ProviderType.GOOGLE, "gemini-3.1-pro"),
    GEMINI_3_1_FLASH(ProviderType.GOOGLE, "gemini-3.1-flash"),

    GPT_4O(ProviderType.OPENAI, "gpt-4o"),
    GPT_3_5_TURBO(ProviderType.OPENAI, "gpt-3.5-turbo");

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

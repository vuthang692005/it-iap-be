package com.example.it_iap.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModelType {
    GEMINI_2_5_FLASH_LITE(ProviderType.GOOGLE, "gemini-2.5-flash-lite"),
    GEMINI_3_FLASH(ProviderType.GOOGLE, "gemini-3-flash"),
    GEMINI_3_1_PRO(ProviderType.GOOGLE, "gemini-3.1-pro"),
    GEMINI_3_1_FLASH(ProviderType.GOOGLE, "gemini-3.1-flash"),
    GEMINI_3_1_FLASH_LITE(ProviderType.GOOGLE, "gemini-3.1-flash-lite"),
    GEMINI_3_5_FLASH(ProviderType.GOOGLE, "gemini-3.5-flash"),
    GEMMA_4_26B_A4B_IT(ProviderType.GOOGLE, "gemma-4-26b-a4b-it"),
    GEMMA_4_31B_IT(ProviderType.GOOGLE, "gemma-4-31b-it"),
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

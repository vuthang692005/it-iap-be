package com.example.it_iap.entity.enums;

public enum PromptUseCase {
    CUSTOMER_SUPPORT,
    QUESTION_GENERATOR,
    MOCK_INTERVIEW
    ;

    public static PromptUseCase from(String value) {
        if (value == null) return null;

        try {
            return PromptUseCase.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

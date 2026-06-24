package com.example.it_iap.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InterviewMode {
    INTERACTIVE_INTERVIEW(PromptUseCase.INTERACTIVE_INTERVIEW, false, true),
    STRESS_INTERVIEW(PromptUseCase.STRESS_INTERVIEW, true, false)
    ;
    private final PromptUseCase promptUseCase;
    private final boolean hasLimitTime;
    private final boolean hasChatSession;

    public static InterviewMode from(String value) {
        if (value == null) return null;

        try {
            return InterviewMode.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

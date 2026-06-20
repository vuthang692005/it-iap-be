package com.example.it_iap.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QuestionType {
    TECHNICAL("Câu hỏi kỹ thuật"),
    SITUATIONAL("Câu hỏi tình huống"),
    BEHAVIORAL("Câu hỏi hành vi");

    private final String displayName;

    public static QuestionType fromString (String value) {
        if (value == null) return null;

        try {
            return QuestionType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

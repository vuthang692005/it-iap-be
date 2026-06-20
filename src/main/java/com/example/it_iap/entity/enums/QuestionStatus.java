package com.example.it_iap.entity.enums;

public enum QuestionStatus {
    PENDING,
    REJECTED,
    APPROVED;

    public static QuestionStatus fromString (String value) {
        if (value == null) return null;

        try {
            return QuestionStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

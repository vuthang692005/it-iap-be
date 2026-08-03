package com.example.it_iap.entity.enums;

public enum ReactionType {
    LIKE,
    LOVE,
    HAHA,
    WOW,
    ANGRY
    ;

    public static ReactionType fromString (String value) {
        if (value == null) return null;

        try {
            return ReactionType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

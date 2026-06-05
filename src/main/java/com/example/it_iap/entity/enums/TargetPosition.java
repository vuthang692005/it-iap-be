package com.example.it_iap.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TargetPosition {
    FRONTEND("Frontend"),
    BACKEND("Backend"),
    TESTER("Tester"),
    DATA_ANALYST("Data Analyst")
    ;
    private final String name;

    public static TargetPosition fromString (String value) {
        if (value == null) return null;

        try {
            return TargetPosition.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

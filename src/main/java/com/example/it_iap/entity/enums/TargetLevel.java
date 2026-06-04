package com.example.it_iap.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TargetLevel {
    INTERN("Intern"),
    FRESHER("Fresher")
    ;
    private final String name;

    public static TargetLevel fromString (String value) {
        if (value == null) return null;

        try {
            return TargetLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

package com.example.it_iap.entity.enums;

public enum Source {
    ADMIN,
    AI;

    public static Source fromString (String value) {
        if (value == null) return null;

        try {
            return Source.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

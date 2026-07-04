package com.example.it_iap.entity.enums;

public enum ReportStatus {
    PENDING,
    REJECTED,
    APPROVED;

    public static ReportStatus fromString (String value) {
        if (value == null) return null;

        try {
            return ReportStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

package com.example.it_iap.entity.enums;

public enum NotificationType {
    SYSTEM,   // Hệ thống
    ADMIN,    // Admin
    INTERVIEW_RESULT,    // Kết quả phỏng vấn
    WARNING,      // Cảnh báo
    PROMO,     // Gói nạp? Quảng cáo? chệu khôm bit
    REPORT,     // Báo cáo
    STREAK     // chuỗi học tập
    ;

    public static NotificationType from(String value) {
        if (value == null) return null;

        try {
            return NotificationType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

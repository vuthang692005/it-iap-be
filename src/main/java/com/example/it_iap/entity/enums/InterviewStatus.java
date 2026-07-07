package com.example.it_iap.entity.enums;

public enum InterviewStatus {
    PENDING,      // User vừa tạo xong Interview, nhưng chưa ấn "Start"
    IN_PROGRESS,  // User đã ấn "Start" và đang trong quá trình trả lời
    COMPLETED     // User đã nộp bài (kết thúc buổi phỏng vấn)
    ;
    public static InterviewStatus from(String value) {
        if (value == null) return null;

        try {
            return InterviewStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

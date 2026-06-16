package com.example.it_iap.entity.enums;

public enum InterviewStatus {
    IN_PROGRESS,  // User đang làm bài phỏng vấn
    SUBMITTED,    // User đã nộp bài, hệ thống đang đợi AI tổng hợp feedback
    COMPLETED,    // Đã có kết quả overall_result, buổi phỏng vấn kết thúc thành công
}

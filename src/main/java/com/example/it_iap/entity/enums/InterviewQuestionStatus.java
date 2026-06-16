package com.example.it_iap.entity.enums;

public enum InterviewQuestionStatus {
    UNANSWERED,   // Câu hỏi chưa được sờ tới (mặc định)
    ANSWERING,    // User đang dừng ở câu này để trả lời/thu âm/gõ chữ
    ANSWERED,     // User đã trả lời xong câu này
    SKIPPED       // User bấm bỏ qua để sang câu tiếp theo
}

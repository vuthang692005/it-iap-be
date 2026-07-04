package com.example.it_iap.entity.enums;

public enum ReportType {
    INACCURATE_CONTENT,         // Câu hỏi hoặc đáp án/lời giải cung cấp bị sai kiến thức.
    OUTDATED,                   // Công nghệ hoặc kiến thức trong câu hỏi không còn được sử dụng trong thực tế
    DUPLICATE,                  // Câu hỏi bị trùng với câu khác trong buổi phỏng vấn
    POOR_FORMATTING,            // Lỗi định dạng, sai chính tả, lỗi format không đọc được
    SPAM_OR_IRRELEVANT,         // Nội dung không liên quan đến phỏng vấn (quảng cáo, link độc hại)
    INAPPROPRIATE,              // Phản cảm, chứa từ ngữ thô tục, công kích cá nhân
    OTHER;                      // Các trường hợp khác

    public static ReportType fromString (String value) {
        if (value == null) return null;

        try {
            return ReportType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}

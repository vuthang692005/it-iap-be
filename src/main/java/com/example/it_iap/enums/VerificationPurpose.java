package com.example.it_iap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
@Getter
public enum VerificationPurpose {
    EMAIL_VERIFY("otp:email_verify:", Duration.ofMinutes(5), "email/verify-otp", "Xác thực tài khoản"),
    FORGOT_PASSWORD("otp:forgot_password:", Duration.ofMinutes(5), "email/forgot-password", "Xác thực yêu cầu khôi phục mật khẩu"),
    CHANGE_EMAIL("otp:change_email:", Duration.ofMinutes(5), "email/change-email", "Xác thực yêu cầu đổi email"),
    ;
    private final String prefix;
    private final Duration ttl;        // Thời gian tông tại
    private final String templateName; // Tên file html trong thư mục templates
    private final String emailSubject; // Tiêu đề gửi mail
}

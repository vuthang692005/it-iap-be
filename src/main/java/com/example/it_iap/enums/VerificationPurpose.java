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
    TOTP_SECRET("totp:secret:", Duration.ofMinutes(5), null, null),
    ;
    private final String prefix;
    private final Duration ttl;        // Thời gian tồn tại
    private final String templateName; // Tên file html trong thư mục templates
    private final String emailSubject; // Tiêu đề gửi mail
}

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
    RESET_2FA("totp:reset:", Duration.ofMinutes(10), "email/reset-2fa", "Yêu cầu khôi phục / gỡ xác thực 2 bước"),
    SCHEDULED_2FA_DISABLE("totp:scheduled:", Duration.ofHours(24), "email/scheduled-2fa", "CẢNH BÁO BẢO MẬT: Xác thực 2 bước sẽ bị gỡ sau 24 giờ"),
    CANCELLED_2FA_DISABLE("totp:cancelled:", Duration.ofDays(1), "email/cancelled-2fa", "Thông báo: Đã hủy yêu cầu gỡ xác thực 2 bước"),
    ;
    private final String prefix;
    private final Duration ttl;        // Thời gian tồn tại
    private final String templateName; // Tên file html trong thư mục templates
    private final String emailSubject; // Tiêu đề gửi mail
}

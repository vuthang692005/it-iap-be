package com.example.it_iap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
@Getter
public enum VerificationPurpose {
    EMAIL_VERIFY("otp:email_verify:", Duration.ofMinutes(5)),
    FORGOT_PASSWORD("otp:forgot_password:", Duration.ofMinutes(5))
    ;
    private final String prefix;
    private final Duration ttl;
}

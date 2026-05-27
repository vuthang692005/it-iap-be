package com.example.it_iap.cache.verification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
@Getter
public enum VerificationPurpose {
    EMAIL_VERIFY("otp:email_verify:", Duration.ofMinutes(10)),
    FORGOT_PASSWORD("otp:forgot_password:", Duration.ofMinutes(10))
    ;
    private final String prefix;
    private final Duration ttl;
}

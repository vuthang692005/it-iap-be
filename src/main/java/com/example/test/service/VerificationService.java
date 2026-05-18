package com.example.test.service;

import com.example.test.cache.verification.VerificationPurpose;

import java.util.UUID;

public interface VerificationService {
    String createOtp(UUID key, VerificationPurpose otpPurpose);
    boolean verifyOtp(UUID key, String inputOtp, VerificationPurpose otpPurpose);
}

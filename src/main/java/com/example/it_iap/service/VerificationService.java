package com.example.it_iap.service;

import com.example.it_iap.enums.VerificationPurpose;

import java.util.UUID;

public interface VerificationService {
    String createOtp(UUID key, VerificationPurpose otpPurpose);
    boolean verifyOtp(UUID key, String inputOtp, VerificationPurpose otpPurpose);
    boolean hasActiveOtp(UUID uuid, VerificationPurpose purpose);
}

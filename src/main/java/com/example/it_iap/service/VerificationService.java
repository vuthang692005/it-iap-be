package com.example.it_iap.service;

import com.example.it_iap.enums.VerificationPurpose;

import java.util.UUID;

public interface VerificationService {
    String createOtp(String key, VerificationPurpose otpPurpose);
    boolean verifyOtp(String key, String inputOtp, VerificationPurpose otpPurpose);
    boolean hasActiveOtp(String uuid, VerificationPurpose purpose);
}

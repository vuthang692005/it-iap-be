package com.example.it_iap.service;

import com.example.it_iap.enums.VerificationPurpose;

public interface EmailService {
    void sendVerifyOtp(String to, String fullName, String otp, VerificationPurpose purpose);
}

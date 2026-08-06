package com.example.it_iap.service;

import com.example.it_iap.enums.VerificationPurpose;

public interface EmailService {
    void sendVerifyOtp(String to, String fullName, String otp, VerificationPurpose purpose);
    void sendReset2faEmail(String to, String fullName, String confirmUrl, String cancelUrl, VerificationPurpose purpose);
    void sendScheduled2faEmail(String to, String fullName, String cancelUrl, VerificationPurpose purpose);
    void sendNotificationEmail(String to, String fullName, VerificationPurpose purpose);
}

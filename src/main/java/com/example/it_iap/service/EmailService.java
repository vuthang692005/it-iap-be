package com.example.it_iap.service;

public interface EmailService {
    void sendVerifyOtp(String to, String fullName, String otp, long ttlMinutes);
}

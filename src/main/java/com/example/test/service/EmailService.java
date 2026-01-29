package com.example.test.service;

public interface EmailService {
    void sendVerifyOtp(String to, String fullName, String otp, long ttlMinutes);
}

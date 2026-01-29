package com.example.test.service;

import com.example.test.dto.auth.request.*;
import com.example.test.dto.auth.response.RegisterResponse;
import com.example.test.dto.auth.response.TokenResponse;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    TokenResponse login(LoginRequest request) throws JOSEException;
    TokenResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException;
    void resendOtp(ResendOtpRequest request);
    void verifyEmail(VerifyEmailRequest request);
}

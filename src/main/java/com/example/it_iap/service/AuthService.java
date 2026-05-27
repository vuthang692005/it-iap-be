package com.example.it_iap.service;

import com.example.it_iap.dto.auth.request.*;
import com.example.it_iap.dto.auth.response.RegisterResponse;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.text.ParseException;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);
    void login(LoginRequest request, HttpServletResponse response) throws JOSEException;
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws ParseException, JOSEException;
    void resendOtp(ResendOtpRequest request);
    void verifyEmail(VerifyEmailRequest request);
}

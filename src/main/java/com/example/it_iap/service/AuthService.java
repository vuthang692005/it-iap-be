package com.example.it_iap.service;

import com.example.it_iap.dto.auth.request.*;
import com.example.it_iap.dto.auth.response.AuthResponse;
import com.example.it_iap.dto.auth.response.RoleResponse;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.text.ParseException;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    RoleResponse login(LoginRequest request, HttpServletResponse response) throws JOSEException;
    RoleResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws ParseException, JOSEException;
    void resendOtp(ResendOtpRequest request);
    void verifyEmail(VerifyEmailRequest request);
    void forgotPassword (String email);
    void verifyForgotPassword (VerifyForgotPasswordRequest request);
}

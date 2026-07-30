package com.example.it_iap.service;

import com.example.it_iap.dto.auth.request.*;
import com.example.it_iap.dto.auth.response.AuthResponse;
import com.example.it_iap.dto.auth.response.RoleResponse;
import com.example.it_iap.dto.auth.response.TwoFactorResponse;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.text.ParseException;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    RoleResponse login(LoginRequest request, HttpServletResponse response) throws JOSEException;
    RoleResponse login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse response) throws JOSEException;
    RoleResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws ParseException, JOSEException;
    void resendOtp(ResendOtpRequest request);
    void verifyEmail(VerifyEmailRequest request);
    void forgotPassword(String email);
    void verifyForgotPassword(VerifyForgotPasswordRequest request);
    void logout(HttpServletRequest request, HttpServletResponse response) throws ParseException, JOSEException;
    TwoFactorResponse setup2fa();
    void confirm2fa(TwoFactorRequest request);
    RoleResponse login2fa(TwoFactorRequest req, HttpServletRequest request, HttpServletResponse response) throws JOSEException, ParseException;
    void disable2fa(TwoFactorRequest request);
    boolean status2fa();
}

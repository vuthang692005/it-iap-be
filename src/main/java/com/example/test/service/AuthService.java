package com.example.test.service;

import com.example.test.dto.auth.request.LoginRequest;
import com.example.test.dto.auth.request.RefreshTokenRequest;
import com.example.test.dto.auth.request.RegisterRequest;
import com.example.test.dto.auth.response.TokenResponse;
import com.example.test.entity.User;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthService {
    void register(RegisterRequest request);
    TokenResponse login(LoginRequest request) throws JOSEException;
    TokenResponse refreshToken(RefreshTokenRequest request) throws JOSEException, ParseException;
    User getUserLogin();
}

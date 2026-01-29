package com.example.test.service;

import com.example.test.dto.auth.request.RefreshTokenRequest;
import com.example.test.dto.auth.response.TokenResponse;
import com.example.test.entity.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;

public interface TokenService {
    String generateAccessToken(User user) throws JOSEException;
    String generateRefreshToken(User user) throws JOSEException;
    SignedJWT verifyRefreshToken(String token) throws JOSEException, ParseException;
}

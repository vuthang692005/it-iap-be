package com.example.it_iap.service;

import com.example.it_iap.entity.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;

public interface TokenService {
    String generateAccessToken(User user) throws JOSEException;
    String generateRefreshToken(User user) throws JOSEException;
    SignedJWT verifyRefreshToken(String token) throws JOSEException, ParseException;
    void revokeRefreshToken(String refreshToken, boolean isLogout) throws JOSEException, ParseException;
}

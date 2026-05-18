package com.example.test.service.impl;

import com.example.test.entity.User;
import com.example.test.exception.AppException;
import com.example.test.exception.ErrorCode;
import com.example.test.service.TokenService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "TokenServiceImpl")
public class TokenServiceImpl implements TokenService {
    @Value("${jwt.singerKey}")
    private String singerKey;

    public String generateAccessToken(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("test")
                .issueTime(new Date())
                .claim("scope", buildScope(user))
                .claim("isRefreshToken", false)
                .expirationTime(Date.from(Instant.now().plus(100, ChronoUnit.MINUTES)))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        jwsObject.sign(new MACSigner(singerKey));
        return jwsObject.serialize();
    }

    public String generateRefreshToken(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("test")
                .issueTime(new Date())
                .claim("isRefreshToken", true)
                .expirationTime(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        jwsObject.sign(new MACSigner(singerKey));
        return jwsObject.serialize();
    }

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");

        if(!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(role -> {
                stringJoiner.add(role.getName());
                if(!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add(permission.getName());
                    });
                }
            });
        }

        return stringJoiner.toString();
    }

    public SignedJWT verifyRefreshToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(singerKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        boolean verified = signedJWT.verify(verifier);

        if (!verified) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        boolean expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime().after(new Date());
        boolean isRefreshToken = signedJWT.getJWTClaimsSet().getBooleanClaim("isRefreshToken");

        if (!expiryTime) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        if (!isRefreshToken) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        return signedJWT;
    }
}

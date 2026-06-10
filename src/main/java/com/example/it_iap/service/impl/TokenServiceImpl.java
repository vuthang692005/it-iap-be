package com.example.it_iap.service.impl;

import com.example.it_iap.cache.CacheRepository;
import com.example.it_iap.entity.User;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.service.TokenService;
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
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "TokenServiceImpl")
public class TokenServiceImpl implements TokenService {
    private final CacheRepository cacheRepository;

    @Value("${jwt.signerKey}")
    private String signerKey;

    private static final String CLAIM_IS_REFRESH_TOKEN = "isRefreshToken";
    private static final String PREFIX = "auth:token:white";

    public String generateAccessToken(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("test")
                .issueTime(new Date())
                .claim("scope", buildScope(user))
                .claim(CLAIM_IS_REFRESH_TOKEN, false)
                .expirationTime(Date.from(Instant.now().plus(5, ChronoUnit.MINUTES)))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        jwsObject.sign(new MACSigner(signerKey));
        return jwsObject.serialize();
    }

    public String generateRefreshToken(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        String refreshTokenId = UUID.randomUUID().toString();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("test")
                .issueTime(new Date())
                .claim(CLAIM_IS_REFRESH_TOKEN, true)
                .jwtID(refreshTokenId)
                .expirationTime(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        jwsObject.sign(new MACSigner(signerKey));

        String refreshToken = jwsObject.serialize();
        String key = PREFIX + user.getEmail();

        cacheRepository.addToSet(key, refreshTokenId, Duration.ofDays(7));
        return refreshToken;
    }

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(role -> {
                stringJoiner.add(role.name());
            });
        }
        return stringJoiner.toString();
    }

    public SignedJWT verifyRefreshToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        boolean verified = signedJWT.verify(verifier);

        if (!verified) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        String refreshTokenId = signedJWT.getJWTClaimsSet().getJWTID();
        String email = signedJWT.getJWTClaimsSet().getSubject();
        String key = PREFIX + email;

        // Kiểm tra xem token CÒN trong Whitelist không
        if(!cacheRepository.isMemberOfSet(key, refreshTokenId)){
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        // THU HỒI NGAY LẬP TỨC
        cacheRepository.removeFromSet(key, refreshTokenId);

        boolean expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime().after(new Date());
        boolean isRefreshToken = signedJWT.getJWTClaimsSet().getBooleanClaim(CLAIM_IS_REFRESH_TOKEN);

        if (!expiryTime) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        if (!isRefreshToken) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        return signedJWT;
    }
}

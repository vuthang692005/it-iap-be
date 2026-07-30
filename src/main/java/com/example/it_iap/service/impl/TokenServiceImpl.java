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
    private static final String CLAIM_IS_ACCESS_TOKEN = "isAccessToken";
    private static final String CLAIM_IS_REFRESH_TOKEN = "isRefreshToken";
    private static final String CLAIM_IS_PREAUTH_TOKEN = "isPreAuthToken";
    private static final String CLAIM_SESSION_ID = "sid";

    private static final String PREFIX = "auth:token:white:";
    private static final String PREFIX_PREAUTH = "preAuth:token:white:";

    @Override
    public String generateAccessToken(User user) throws JOSEException {
        return generateAccessToken(user, null);
    }

    @Override
    public String generateAccessToken(User user, String sessionId) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .issuer("test")
                .issueTime(new Date())
                .claim("scope", buildScope(user))
                .claim(CLAIM_IS_ACCESS_TOKEN, true)
                .expirationTime(Date.from(Instant.now().plus(10, ChronoUnit.MINUTES)));

        if (sessionId != null && !sessionId.isBlank()) {
            claimsBuilder.claim(CLAIM_SESSION_ID, sessionId);
        }

        Payload payload = new Payload(claimsBuilder.build().toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        jwsObject.sign(new MACSigner(signerKey));
        return jwsObject.serialize();
    }

    @Override
    public String generateRefreshToken(User user) throws JOSEException {
        return generateRefreshToken(user, null, null);
    }

    @Override
    public String generateRefreshToken(User user, String sessionId, String refreshTokenId) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        String jti = (refreshTokenId != null && !refreshTokenId.isBlank()) ? refreshTokenId : UUID.randomUUID().toString();

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .issuer("test")
                .issueTime(new Date())
                .claim(CLAIM_IS_REFRESH_TOKEN, true)
                .jwtID(jti)
                .expirationTime(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)));

        if (sessionId != null && !sessionId.isBlank()) {
            claimsBuilder.claim(CLAIM_SESSION_ID, sessionId);
        }

        Payload payload = new Payload(claimsBuilder.build().toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        jwsObject.sign(new MACSigner(signerKey));

        String refreshToken = jwsObject.serialize();
        String key = PREFIX + user.getId();

        cacheRepository.addToSet(key, jti, Duration.ofDays(7));
        return refreshToken;
    }

    @Override
    public String generatePreAuthToken(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        String preAuthTokenId = UUID.randomUUID().toString();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .issuer("test")
                .issueTime(new Date())
                .claim(CLAIM_IS_PREAUTH_TOKEN, true)
                .jwtID(preAuthTokenId)
                .expirationTime(Date.from(Instant.now().plus(5, ChronoUnit.MINUTES)))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        jwsObject.sign(new MACSigner(signerKey));

        String preAuthToken = jwsObject.serialize();
        String key = PREFIX_PREAUTH + user.getId();

        cacheRepository.addToSet(key, preAuthTokenId, Duration.ofMinutes(5));
        return preAuthToken;
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add(role.name());
            });
        }
        return stringJoiner.toString();
    }

    @Override
    public SignedJWT verifyRefreshToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        boolean verified = signedJWT.verify(verifier);

        if (!verified) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        revokeRefreshToken(token, false);

        boolean expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime().after(new Date());
        Boolean isRefreshToken = signedJWT.getJWTClaimsSet()
                .getBooleanClaim(CLAIM_IS_REFRESH_TOKEN);

        if (!expiryTime) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        if (isRefreshToken == null) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }

        if (!isRefreshToken) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        return signedJWT;
    }

    @Override
    public void revokeRefreshToken(String refreshToken, boolean isLogout) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(refreshToken);
        String userId = signedJWT.getJWTClaimsSet().getSubject();
        String refreshTokenId = signedJWT.getJWTClaimsSet().getJWTID();

        String key = PREFIX + userId;
        if (!isLogout) {
            if (!cacheRepository.isMemberOfSet(key, refreshTokenId)) {
                throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
            }
        }

        cacheRepository.removeFromSet(key, refreshTokenId);
    }

    @Override
    public SignedJWT verifyPreAuthToken(String token)
            throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        if (!signedJWT.verify(verifier)) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        boolean expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime().after(new Date());

        Boolean isPreAuthToken = signedJWT.getJWTClaimsSet().getBooleanClaim(CLAIM_IS_PREAUTH_TOKEN);

        if (!expiryTime || !Boolean.TRUE.equals(isPreAuthToken)) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }
        return signedJWT;
    }

    @Override
    public void revokePreAuthToken(String preAuth) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(preAuth);
        String userId = signedJWT.getJWTClaimsSet().getSubject();
        String preAuthTokenId = signedJWT.getJWTClaimsSet().getJWTID();

        String key = PREFIX_PREAUTH + userId;
        if (!cacheRepository.isMemberOfSet(key, preAuthTokenId)) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }
        cacheRepository.removeFromSet(key, preAuthTokenId);
    }
}

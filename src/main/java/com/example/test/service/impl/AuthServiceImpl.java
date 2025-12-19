package com.example.test.service.impl;

import com.example.test.dto.auth.request.LoginRequest;
import com.example.test.dto.auth.request.RefreshTokenRequest;
import com.example.test.dto.auth.request.RegisterRequest;
import com.example.test.dto.auth.response.TokenResponse;
import com.example.test.entity.Role;
import com.example.test.entity.User;
import com.example.test.exception.AppException;
import com.example.test.exception.ErrorCode;
import com.example.test.repository.RoleRepository;
import com.example.test.repository.UserRepository;
import com.example.test.service.AuthService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AuthServiceImpl")
public class AuthServiceImpl implements AuthService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.singerKey}")
    private String singerKey;

    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        Role role = roleRepository.findById("USER")
                .orElseThrow(() -> {
                    log.error("Không tìm thấy role USER trong cơ sở dữ liệu");
                    return new AppException(ErrorCode.SYSTEM_ERROR);
                });

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRoles(roles);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USERNAME_OR_EMAIL_EXISTED);
        }
    }

    public TokenResponse login(LoginRequest request) throws JOSEException {
        User user = userRepository.findByUsernameOrEmail(request.getIdentifier())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        boolean auth = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!auth) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (!user.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED);
        }

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        return new TokenResponse(accessToken, refreshToken);
    }

    private String generateAccessToken(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
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

    private String generateRefreshToken(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
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

    public TokenResponse refreshToken(RefreshTokenRequest request) throws JOSEException, ParseException {
        String token = request.getRefreshToken();

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

        String username = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Xác thực thất bại: không tìm thấy người dùng với subject '{}'",username);
                    return new AppException(ErrorCode.AUTHENTICATION_FAILED);
                });

        if (!user.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED);
        }

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        return new TokenResponse(accessToken, refreshToken);
    }

    public User getUserLogin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AppException(
                    ErrorCode.UNAUTHENTICATED
            );
        }

        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Không tìm thấy người dùng với subject '{}'",username);
                    return new AppException(ErrorCode.AUTHENTICATION_FAILED);
                });
    }
}

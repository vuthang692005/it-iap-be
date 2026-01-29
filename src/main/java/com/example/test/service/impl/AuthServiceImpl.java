package com.example.test.service.impl;

import com.example.test.dto.auth.request.*;
import com.example.test.dto.auth.response.RegisterResponse;
import com.example.test.dto.auth.response.TokenResponse;
import com.example.test.entity.Role;
import com.example.test.entity.User;
import com.example.test.enums.VerificationPurpose;
import com.example.test.exception.AppException;
import com.example.test.exception.ErrorCode;
import com.example.test.repository.RoleRepository;
import com.example.test.repository.UserRepository;
import com.example.test.service.AuthService;
import com.example.test.service.EmailService;
import com.example.test.service.VerificationService;
import com.example.test.service.TokenService;
import com.nimbusds.jose.*;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AuthServiceImpl")
public class AuthServiceImpl implements AuthService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final VerificationService verificationService;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        Role role = roleRepository.findById("USER")
                .orElseThrow(() -> {
                    log.error("Không tìm thấy role USER trong cơ sở dữ liệu");
                    return new AppException(ErrorCode.SYSTEM_ERROR);
                });
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = userRepository.findByEmail(request.getEmail())
                .map(u -> {
                    if (u.isVerifyEmail()) {
                        throw new AppException(ErrorCode.EMAIL_EXISTED);
                    }
                    return u;
                })
                .orElseGet(User::new);

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRoles(roles);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        VerificationPurpose purpose = VerificationPurpose.EMAIL_VERIFY;
        String otp = verificationService.createOtp(user.getId(), purpose);
        emailService.sendVerifyOtp(user.getEmail(), user.getFullName(), otp, purpose.getTtl().toMinutes());

        return new RegisterResponse(user.getId());
    }

    public void resendOtp(ResendOtpRequest request){
        userRepository.findById(request.getUserId())
                .filter(user -> !user.isVerifyEmail())
                .ifPresent(user -> {
                    VerificationPurpose purpose = VerificationPurpose.EMAIL_VERIFY;
                    String otp = verificationService.createOtp(user.getId(), purpose);
                    emailService.sendVerifyOtp(user.getEmail(), user.getFullName(), otp, purpose.getTtl().toMinutes());
                });
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest request){
        boolean matched = verificationService.verifyOtp(request.getUserId(), request.getOtp(), VerificationPurpose.EMAIL_VERIFY);

        if (!matched){
            throw new  AppException(ErrorCode.OTP_VERIFICATION_FAILED);
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->{
                    log.error(
                            "Xác minh email thất bại: OTP đã được xác thực nhưng không tìm thấy người dùng trong DB. " +
                                    "Phát hiện trạng thái dữ liệu không nhất quán giữa Redis và Database. userId={}",
                            request.getUserId()
                    );
                    return new AppException(ErrorCode.SYSTEM_ERROR);
                });

        user.setVerifyEmail(true);
        userRepository.save(user);
    }

    public TokenResponse login(LoginRequest request) throws JOSEException {
        User user = userRepository.findWithRolesByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        boolean auth = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!auth || !user.isVerifyEmail()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (!user.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED);
        }

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);
        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        String token = request.getRefreshToken();
        SignedJWT signedJWT = tokenService.verifyRefreshToken(token);
        String email = signedJWT.getJWTClaimsSet().getSubject();

        User user = userRepository.findWithRolesByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Xác thực refreshToken thất bại: không tìm thấy người dùng với subject '{}'",email);
                    return new AppException(ErrorCode.AUTHENTICATION_FAILED);
                });

        if (!user.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED);
        }

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);
        return new TokenResponse(accessToken, refreshToken);
    }
}

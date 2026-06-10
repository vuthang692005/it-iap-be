package com.example.it_iap.service.impl;

import com.example.it_iap.cache.CacheRepository;
import com.example.it_iap.dto.auth.request.*;
import com.example.it_iap.dto.auth.response.AuthResponse;
import com.example.it_iap.dto.auth.response.RoleResponse;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.enums.Role;
import com.example.it_iap.enums.CookieKey;
import com.example.it_iap.enums.VerificationPurpose;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.UserRepository;
import com.example.it_iap.service.*;
import com.nimbusds.jose.*;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AuthServiceImpl")
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final VerificationService verificationService;
    private final CookieService cookieService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        // NGHIỆP VỤ: Xử lý email đã tồn tại
        // - Đã verify: Chặn ngay lập tức.
        // - Chưa verify: Kiểm tra xem OTP cũ còn hạn không (Cooldown). Nếu hết hạn mới
        // cho ghi đè.
        User user = userRepository.findByEmail(request.getEmail())
                .map(u -> {
                    if (u.isVerifyEmail()) {
                        throw new AppException(ErrorCode.EMAIL_EXISTED);
                    }
                    if (verificationService.hasActiveOtp(u.getId(), VerificationPurpose.EMAIL_VERIFY)) {
                        throw new AppException(ErrorCode.ACCOUNT_AWAITING_VERIFICATION);
                    }
                    return u;
                })
                .orElseGet(User::new);

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRoles(roles);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        VerificationPurpose purpose = VerificationPurpose.EMAIL_VERIFY;
        String otp = verificationService.createOtp(user.getId(), purpose);
        emailService.sendVerifyOtp(user.getEmail(), user.getFullName(), otp, purpose);

        return new AuthResponse(user.getId());
    }

    public void resendOtp(ResendOtpRequest request) {
        userRepository.findById(request.getUserId())
                .filter(user -> !user.isVerifyEmail())
                .ifPresent(user -> {
                    VerificationPurpose purpose = VerificationPurpose.EMAIL_VERIFY;
                    String otp = verificationService.createOtp(user.getId(), purpose);
                    emailService.sendVerifyOtp(user.getEmail(), user.getFullName(), otp, purpose);
                });
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        boolean matched = verificationService.verifyOtp(request.getUserId(), request.getOtp(),
                VerificationPurpose.EMAIL_VERIFY);

        if (!matched) {
            throw new AppException(ErrorCode.OTP_VERIFICATION_FAILED);
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.error(
                            "Xác minh email thất bại: OTP đã được xác thực nhưng không tìm thấy người dùng trong DB. " +
                                    "Phát hiện trạng thái dữ liệu không nhất quán giữa Redis và Database. userId={}",
                            request.getUserId());
                    return new AppException(ErrorCode.SYSTEM_ERROR);
                });

        user.setVerifyEmail(true);
        userRepository.save(user);
    }

    public RoleResponse login(LoginRequest request, HttpServletResponse response) throws JOSEException {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (user.getPassword() == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        boolean auth = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!auth || !user.isVerifyEmail()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (!user.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED);
        }

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        cookieService.add(response, CookieKey.ACCESS_TOKEN, accessToken);
        cookieService.add(response, CookieKey.REFRESH_TOKEN, refreshToken);

        Set<Role> userRoles = user.getRoles();
        return new RoleResponse(userRoles);
    }

    public RoleResponse refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws ParseException, JOSEException {
        String token = cookieService.get(request, CookieKey.REFRESH_TOKEN);

        if (token == null || token.isBlank()) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        SignedJWT signedJWT = tokenService.verifyRefreshToken(token);
        String email = signedJWT.getJWTClaimsSet().getSubject();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Xác thực refreshToken thất bại: không tìm thấy người dùng với subject '{}'", email);
                    return new AppException(ErrorCode.AUTHENTICATION_FAILED);
                });

        if (!user.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED);
        }

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        cookieService.add(response, CookieKey.ACCESS_TOKEN, accessToken);
        cookieService.add(response, CookieKey.REFRESH_TOKEN, refreshToken);

        Set<Role> userRoles = user.getRoles();
        return new RoleResponse(userRoles);
    }

    public void forgotPassword (String email){
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    VerificationPurpose purpose = VerificationPurpose.FORGOT_PASSWORD;
                    String otp = verificationService.createOtp(user.getId(), purpose);
                    emailService.sendVerifyOtp(user.getEmail(), user.getFullName(), otp, purpose);
                });
    }

    public void verifyForgotPassword (VerifyForgotPasswordRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.OTP_VERIFICATION_FAILED)
                );

        boolean matched = verificationService.verifyOtp(user.getId(), request.getOtp(), VerificationPurpose.FORGOT_PASSWORD);

        if (!matched){
            throw new  AppException(ErrorCode.OTP_VERIFICATION_FAILED);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) throws ParseException, JOSEException {
        // Lấy refresh token từ cookie
        String refreshToken = cookieService.get(request, CookieKey.REFRESH_TOKEN);

        // Kiểm tra token
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // Thu hồi token
        tokenService.revokeRefreshToken(refreshToken, true);

        // Xóa cookie khỏi trình duyệt
        cookieService.clear(response, CookieKey.ACCESS_TOKEN);
        cookieService.clear(response, CookieKey.REFRESH_TOKEN);
    }
}

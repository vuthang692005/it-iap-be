package com.example.it_iap.service.impl;

import com.example.it_iap.dto.auth.request.*;
import com.example.it_iap.dto.auth.response.RegisterResponse;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.enums.Role;
import com.example.it_iap.enums.CookieKey;
import com.example.it_iap.cache.verification.VerificationPurpose;
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

    public RegisterResponse register(RegisterRequest request) {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);

        // NGHIỆP VỤ: Xử lý email đã tồn tại
        // - Đã verify: Chặn ngay lập tức.
        // - Chưa verify: Tuyệt đối KHÔNG ghi đè thông tin mới vào db (chống lỗi Account Takeover).
        // Thay vào đó, tạo mã OTP mới, gửi lại mail và ném lỗi kèm userId để Frontend tự điều hướng sang form nhập OTP.
        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    if (u.isVerifyEmail()) {
                        throw new AppException(ErrorCode.EMAIL_EXISTED);
                    }
                    else {
                        VerificationPurpose purpose = VerificationPurpose.EMAIL_VERIFY;
                        String otp = verificationService.createOtp(u.getId(), purpose);
                        emailService.sendVerifyOtp(u.getEmail(), u.getFullName(), otp, purpose.getTtl().toMinutes());
                        throw new AppException(ErrorCode.UNVERIFIED_ACCOUNT_EXISTS, u.getId());
                    }
                });

        // Tạo user mới nếu email chưa tồn tại
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRoles(roles);

        try {
            user = userRepository.save(user);
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

    public void login(LoginRequest request, HttpServletResponse response) throws JOSEException {
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
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws ParseException, JOSEException {
        String token = cookieService.get(request, CookieKey.REFRESH_TOKEN);

        if (token == null || token.isBlank()) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        SignedJWT signedJWT = tokenService.verifyRefreshToken(token);
        String email = signedJWT.getJWTClaimsSet().getSubject();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Xác thực refreshToken thất bại: không tìm thấy người dùng với subject '{}'",email);
                    return new AppException(ErrorCode.AUTHENTICATION_FAILED);
                });

        if (!user.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED);
        }

        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        cookieService.add(response, CookieKey.ACCESS_TOKEN, accessToken);
        cookieService.add(response, CookieKey.REFRESH_TOKEN, refreshToken);
    }
}

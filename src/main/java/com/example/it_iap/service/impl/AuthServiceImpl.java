package com.example.it_iap.service.impl;

import com.example.it_iap.cache.CacheRepository;
import com.example.it_iap.dto.auth.request.*;
import com.example.it_iap.dto.auth.response.AuthResponse;
import com.example.it_iap.dto.auth.response.RoleResponse;
import com.example.it_iap.dto.auth.response.TwoFactorResponse;
import com.example.it_iap.entity.Notification;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.enums.NotificationType;
import com.example.it_iap.entity.enums.Role;
import com.example.it_iap.entity.enums.UserActionType;
import com.example.it_iap.enums.CookieKey;
import com.example.it_iap.enums.VerificationPurpose;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.NotificationRepository;
import com.example.it_iap.repository.UserRepository;
import com.example.it_iap.service.*;
import com.example.it_iap.util.AesUtil;
import com.example.it_iap.util.RandomReplyIdentifyCode;
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

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;

import org.springframework.beans.factory.annotation.Value;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AuthServiceImpl")
public class AuthServiceImpl implements AuthService {
    @Value("${app.frontend-url}")
    private String clientUrl;

    private final CacheRepository cacheRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;
    private final TokenService tokenService;
    private final VerificationService verificationService;
    private final CookieService cookieService;
    private final UserService userService;
    private final SessionService sessionService;
    private final NotificationRepository notificationRepository;
    private final UserActivityService userActivityService;

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final CodeVerifier verifier = new DefaultCodeVerifier(new DefaultCodeGenerator(), new SystemTimeProvider());

    private final AesUtil aesUtil;

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
                    if (verificationService.hasActiveOtp(u.getId().toString(), VerificationPurpose.EMAIL_VERIFY)) {
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
        String otp = verificationService.createOtp(user.getId().toString(), purpose);
        emailService.sendVerifyOtp(user.getEmail(), user.getFullName(), otp, purpose);

        return new AuthResponse(user.getId());
    }

    public void resendOtp(ResendOtpRequest request) {
        userRepository.findById(request.getUserId())
                .filter(user -> !user.isVerifyEmail())
                .ifPresent(user -> {
                    VerificationPurpose purpose = VerificationPurpose.EMAIL_VERIFY;
                    String otp = verificationService.createOtp(user.getId().toString(), purpose);
                    emailService.sendVerifyOtp(user.getEmail(), user.getFullName(), otp, purpose);
                });
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        boolean matched = verificationService.verifyOtp(request.getUserId().toString(), request.getOtp(),
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
        return login(request, null, response);
    }

    public RoleResponse login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse response) throws JOSEException {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        Set<Role> userRoles = null;

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

        if (user.isEnable2fa()) {
            String preAuthToken = tokenService.generatePreAuthToken(user);
            cookieService.add(response, CookieKey.PREAUTH_TOKEN, preAuthToken);
            return new RoleResponse(userRoles, user.isEnable2fa());
        }

        String sessionId = UUID.randomUUID().toString();
        String refreshTokenId = UUID.randomUUID().toString();
        sessionService.createSession(user, httpRequest, sessionId, refreshTokenId);

        String accessToken = tokenService.generateAccessToken(user, sessionId);
        String refreshToken = tokenService.generateRefreshToken(user, sessionId, refreshTokenId);

        cookieService.add(response, CookieKey.ACCESS_TOKEN, accessToken);
        cookieService.add(response, CookieKey.REFRESH_TOKEN, refreshToken);

        // Tạo thông báo cảnh báo cho luồng đăng nhập Email/Password
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setIdentifyCode(RandomReplyIdentifyCode.generate());
        notification.setTitle("Cảnh báo an toàn: Phát hiện đăng nhập mới");
        notification.setContent("Tài khoản của bạn vừa được đăng nhập thành công bằng Email và Mật khẩu. Nếu không phải bạn thực hiện, hãy đổi mật khẩu ngay và bật tính năng Xác thực 2 bước (2FA) để tăng cường bảo vệ tài khoản.");
        notification.setType(NotificationType.WARNING);
        notification.setLink(null);
        notificationRepository.save(notification);

        userActivityService.logActivity(UserActionType.LOGIN, "Đăng nhập bằng Email và Mật khẩu", user);

        userRoles = user.getRoles();
        return new RoleResponse(userRoles, user.isEnable2fa());
    }

    public RoleResponse login2fa(TwoFactorRequest req, HttpServletRequest request, HttpServletResponse response)
            throws ParseException, JOSEException {
        // Xác minh token và lấy người dùng
        String token = cookieService.get(request, CookieKey.PREAUTH_TOKEN);
        if (token == null || token.isBlank()) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }
        SignedJWT signedJWT = tokenService.verifyPreAuthToken(token);
        String subject = signedJWT.getJWTClaimsSet().getSubject();
        UUID userId;
        try {
            userId = UUID.fromString(subject);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Xác thực preAuthToken thất bại: không tìm thấy người dùng với subject '{}'", userId);
                    return new AppException(ErrorCode.AUTHENTICATION_FAILED);
                });
        if (!user.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED);
        }

        // Xác minh otp hợp lệ nếu ổn cho đăng nhập
        if (!verifier.isValidCode(aesUtil.decrypt(user.getSecret2fa()), req.getTotp())) {
            throw new AppException(ErrorCode.TWO_FACTOR_CODE_INVALID);
        }

        String sessionId = UUID.randomUUID().toString();
        String refreshTokenId = UUID.randomUUID().toString();
        sessionService.createSession(user, request, sessionId, refreshTokenId);

        String accessToken = tokenService.generateAccessToken(user, sessionId);
        String refreshToken = tokenService.generateRefreshToken(user, sessionId, refreshTokenId);

        cookieService.add(response, CookieKey.ACCESS_TOKEN, accessToken);
        cookieService.add(response, CookieKey.REFRESH_TOKEN, refreshToken);
        cookieService.clear(response, CookieKey.PREAUTH_TOKEN);
        tokenService.revokePreAuthToken(token);
        Set<Role> userRoles = user.getRoles();
        return new RoleResponse(userRoles, user.isEnable2fa());
    }

    public RoleResponse refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws ParseException, JOSEException {
        String token = cookieService.get(request, CookieKey.REFRESH_TOKEN);

        if (token == null || token.isBlank()) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        SignedJWT signedJWT = tokenService.verifyRefreshToken(token);
        String subject = signedJWT.getJWTClaimsSet().getSubject();
        String sid = signedJWT.getJWTClaimsSet().getStringClaim("sid");
        UUID userId;

        try {
            userId = UUID.fromString(subject);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Xác thực refreshToken thất bại: không tìm thấy người dùng với subject '{}'", userId);
                    return new AppException(ErrorCode.AUTHENTICATION_FAILED);
                });

        if (!user.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_DISABLED);
        }

        if (sid != null && !sid.isBlank()) {
            if (!sessionService.isSessionActive(userId, sid)) {
                throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
            }
            sessionService.updateLastActive(sid);
        }

        String newRefreshTokenId = UUID.randomUUID().toString();
        String accessToken = tokenService.generateAccessToken(user, sid);
        String refreshToken = tokenService.generateRefreshToken(user, sid, newRefreshTokenId);

        cookieService.add(response, CookieKey.ACCESS_TOKEN, accessToken);
        cookieService.add(response, CookieKey.REFRESH_TOKEN, refreshToken);

        Set<Role> userRoles = user.getRoles();
        return new RoleResponse(userRoles, user.isEnable2fa());
    }

    public void forgotPassword (String email){
        userRepository.findByEmail(email)
                .filter(User::isVerifyEmail)
                .ifPresent(user -> {
                    VerificationPurpose purpose = VerificationPurpose.FORGOT_PASSWORD;
                    String otp = verificationService.createOtp(user.getId().toString(), purpose);
                    emailService.sendVerifyOtp(user.getEmail(), user.getFullName(), otp, purpose);
                });
    }

    public void verifyForgotPassword (VerifyForgotPasswordRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.OTP_VERIFICATION_FAILED)
                );

        boolean matched = verificationService.verifyOtp(user.getId().toString(), request.getOtp(), VerificationPurpose.FORGOT_PASSWORD);

        if (!matched){
            throw new  AppException(ErrorCode.OTP_VERIFICATION_FAILED);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        userActivityService.logActivity(UserActionType.RESET_PASSWORD, "Đặt lại mật khẩu qua OTP", user);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) throws ParseException, JOSEException {
        // Lấy refresh token từ cookie
        String refreshToken = cookieService.get(request, CookieKey.REFRESH_TOKEN);

        // Kiểm tra token
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        SignedJWT signedJWT = SignedJWT.parse(refreshToken);
        String subject = signedJWT.getJWTClaimsSet().getSubject();
        String sid = signedJWT.getJWTClaimsSet().getStringClaim("sid");

        if (sid != null && !sid.isBlank() && subject != null) {
            try {
                UUID userId = UUID.fromString(subject);
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    sessionService.revokeSession(user, sid);
                }
            } catch (Exception e) {
                log.warn("Lỗi khi thu hồi session trong logout: {}", e.getMessage());
            }
        }

        // Thu hồi token
        tokenService.revokeRefreshToken(refreshToken, true);

        // Xóa cookie khỏi trình duyệt
        cookieService.clear(response, CookieKey.ACCESS_TOKEN);
        cookieService.clear(response, CookieKey.REFRESH_TOKEN);
    }

    public TwoFactorResponse setup2fa() {
        User user = userService.getCurrentUser();

        if (user.isEnable2fa()) {
            throw new AppException(ErrorCode.TWO_FACTOR_ENABLED);
        }

        String secret = secretGenerator.generate();
        String userId = user.getId().toString();
        VerificationPurpose purpose = VerificationPurpose.TOTP_SECRET;

        // Nếu có thì ghi đè
        if (verificationService.hasActiveOtp(userId, purpose)) {
            verificationService.createSecret(secret, userId, purpose);
            return new TwoFactorResponse(secret, user.getEmail());
        }

        verificationService.createSecret(secret, userId, purpose);
        return new TwoFactorResponse(secret, user.getEmail());
    }

    public void confirm2fa(TwoFactorRequest request) {
        User user = userService.getCurrentUser();

        if (user.isEnable2fa()) {
            throw new AppException(ErrorCode.TWO_FACTOR_ENABLED);
        }
        
        // Xác minh TOTP
        String userId = user.getId().toString();
        String key = VerificationPurpose.TOTP_SECRET.getPrefix() + userId;
        String secret = cacheRepository.get(key)
            .orElseThrow(() -> new AppException(ErrorCode.TWO_FACTOR_CODE_INVALID));
        if (!verifier.isValidCode(aesUtil.decrypt(secret), request.getTotp())) {
            throw new AppException(ErrorCode.TWO_FACTOR_CODE_INVALID);
        }

        user.setEnable2fa(true);
        user.setSecret2fa(secret); // Đã mã hóa từ bước lưu redis
        userRepository.save(user);
        cacheRepository.delete(key); // Lưu xong thì xóa khỏi redis

        userActivityService.logActivity(UserActionType.ENABLE_2FA, "Bật xác thực 2 bước (2FA)", user);
    }

    public void disable2fa(TwoFactorRequest request) {
        User user = userService.getCurrentUser();
        if (!user.isEnable2fa()) { // Chưa bật thì đừng đòi hủy
            throw new AppException(ErrorCode.TWO_FACTOR_NOT_ENABLED);
        }
        if (!verifier.isValidCode(aesUtil.decrypt(user.getSecret2fa()), request.getTotp())) {
            throw new AppException(ErrorCode.TWO_FACTOR_CODE_INVALID);
        }
        user.setEnable2fa(false);
        user.setSecret2fa(null);
        user.setScheduled2faDisableAt(null);
        userRepository.save(user);

        // Hủy các token reset 2FA đang chờ trong Redis nếu có
        String pendingToken = cacheRepository.get("2fa:user_reset_pending:" + user.getId()).orElse(null);
        if (pendingToken != null) {
            cacheRepository.delete(VerificationPurpose.RESET_2FA.getPrefix() + pendingToken);
            cacheRepository.delete("2fa:user_reset_pending:" + user.getId());
        }

        String cancel24hToken = cacheRepository.get("2fa:user_cancel_24h:" + user.getId()).orElse(null);
        if (cancel24hToken != null) {
            cacheRepository.delete(VerificationPurpose.SCHEDULED_2FA_DISABLE.getPrefix() + cancel24hToken);
            cacheRepository.delete("2fa:user_cancel_24h:" + user.getId());
        }

        userActivityService.logActivity(UserActionType.DISABLE_2FA, "Tắt xác thực 2 bước (2FA)", user);
    }

    public boolean status2fa() {
        User user = userService.getCurrentUser();
        return user.isEnable2fa();
    }

    public void requestReset2fa() {
        User user = userService.getCurrentUser();
        if (!user.isEnable2fa()) {
            throw new AppException(ErrorCode.TWO_FACTOR_NOT_ENABLED);
        }

        // Kiểm tra nếu đang trong tiến trình đếm ngược 24h gỡ 2FA
        if (user.getScheduled2faDisableAt() != null) {
            throw new AppException(ErrorCode.RESET_2FA_SCHEDULED);
        }

        // Kiểm tra nếu đang có yêu cầu 10 phút chưa xác nhận/từ chối
        if (cacheRepository.exists("2fa:user_reset_pending:" + user.getId())) {
            throw new AppException(ErrorCode.RESET_2FA_PENDING);
        }

        String resetToken = UUID.randomUUID().toString();
        VerificationPurpose purpose = VerificationPurpose.RESET_2FA;
        String key = purpose.getPrefix() + resetToken;

        // Lưu vào redis 10 phút
        cacheRepository.save(key, user.getId().toString(), purpose.getTtl());
        cacheRepository.save("2fa:user_reset_pending:" + user.getId(), resetToken, purpose.getTtl());

        String confirmUrl = clientUrl + "/reset-2fa/confirm?token=" + resetToken;
        String cancelUrl = clientUrl + "/reset-2fa/cancel?token=" + resetToken;

        emailService.sendReset2faEmail(user.getEmail(), user.getFullName(), confirmUrl, cancelUrl, purpose);
    }

    public void confirmReset2fa(ResetTwoFactorRequest request) {
        String token = request.getToken();
        VerificationPurpose purpose = VerificationPurpose.RESET_2FA;
        String key = purpose.getPrefix() + token;

        String userIdStr = cacheRepository.get(key)
                .orElseThrow(() -> new AppException(ErrorCode.RESET_2FA_TOKEN_INVALID));

        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!user.isEnable2fa()) {
            throw new AppException(ErrorCode.TWO_FACTOR_NOT_ENABLED);
        }

        // Kích hoạt hoãn 24 giờ gỡ 2FA
        user.setScheduled2faDisableAt(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        // Tạo token hủy 24h và lưu Redis
        String cancel24hToken = UUID.randomUUID().toString();
        VerificationPurpose scheduledPurpose = VerificationPurpose.SCHEDULED_2FA_DISABLE;
        String cancel24hKey = scheduledPurpose.getPrefix() + cancel24hToken;
        cacheRepository.save(cancel24hKey, user.getId().toString(), scheduledPurpose.getTtl());
        cacheRepository.save("2fa:user_cancel_24h:" + user.getId(), cancel24hToken, scheduledPurpose.getTtl());

        // Gửi email 2: Thông báo đếm ngược 24h kèm link hủy 24h
        String cancelUrl = clientUrl + "/reset-2fa/cancel?token=" + cancel24hToken;
        emailService.sendScheduled2faEmail(user.getEmail(), user.getFullName(), cancelUrl, scheduledPurpose);

        // Xóa token reset 10 phút ban đầu
        cacheRepository.delete(key);
        cacheRepository.delete("2fa:user_reset_pending:" + user.getId());
    }

    public void cancelReset2fa(ResetTwoFactorRequest request) {
        String token = request.getToken();
        
        // Kiểm tra xem token thuộc loại 10m hay 24h
        String resetKey = VerificationPurpose.RESET_2FA.getPrefix() + token;
        String scheduledKey = VerificationPurpose.SCHEDULED_2FA_DISABLE.getPrefix() + token;

        String userIdStr = cacheRepository.get(resetKey)
                .orElseGet(() -> cacheRepository.get(scheduledKey)
                        .orElseThrow(() -> new AppException(ErrorCode.RESET_2FA_TOKEN_INVALID)));

        UUID userId = UUID.fromString(userIdStr);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Hủy lịch gỡ 2FA 24h nếu đang có
        boolean wasScheduled = user.getScheduled2faDisableAt() != null;
        if (wasScheduled) {
            user.setScheduled2faDisableAt(null);
            userRepository.save(user);
        }

        // Xóa các token liên quan trong Redis
        cacheRepository.delete(resetKey);
        cacheRepository.delete(scheduledKey);
        cacheRepository.delete("2fa:user_reset_pending:" + user.getId());
        cacheRepository.delete("2fa:user_cancel_24h:" + user.getId());

        // Gửi email 3: Thông báo đã hủy gỡ 2FA thành công
        emailService.sendNotificationEmail(user.getEmail(), user.getFullName(), VerificationPurpose.CANCELLED_2FA_DISABLE);
    }
}

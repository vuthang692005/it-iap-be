package com.example.it_iap.service.impl;

import com.example.it_iap.cache.CacheRepository;
import com.example.it_iap.dto.user.request.*;
import com.example.it_iap.dto.user.response.UserResponse;
import com.example.it_iap.entity.User;
import com.example.it_iap.enums.UploadFolder;
import com.example.it_iap.entity.enums.Role;
import com.example.it_iap.enums.VerificationPurpose;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.UserRepository;
import com.example.it_iap.service.CloudinaryService;
import com.example.it_iap.service.EmailService;
import com.example.it_iap.service.UserService;
import com.example.it_iap.service.VerificationService;
import com.example.it_iap.util.SecurityUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String PENDING_EMAIL_PREFIX = "PENDING_EMAIL:";
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    private final EmailService emailService;
    private final CacheRepository cacheRepository;

    @Value("${app.user.default-password}")
    private String defaultPassword;

    public User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();
        String oldPassword = request.getOldPassword();
        String encodeNewPassword = passwordEncoder.encode(request.getNewPassword());
        boolean match = passwordEncoder.matches(oldPassword, user.getPassword());
        if (match) {
            user.setPassword(encodeNewPassword);
            userRepository.save(user);
        } else {
            throw new AppException(ErrorCode.OLD_PASSWORD_MISMATCH);
        }
    }

    public Page<UserResponse> searchUser(SearchUserRequest request) {
        int page = Math.max(0, request.getPages() - 1);
        int size = 10;
        PageRequest pageable = PageRequest.of(page, size);

        Page<User> users = userRepository.searchUsers(
                request.getEmail(),
                request.getFullName(),
                request.getPhoneNumber(),
                pageable);

        return users.map(this::buildProfileResponse);
    }
    public UserResponse createUser(CreateUserRequest request) {
        String email = request.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(defaultPassword));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setVerifyEmail(true);
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        user.setRoles(roles);
        return buildProfileResponse(userRepository.save(user));
    }

    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAvatarUrl(request.getAvatarUrl());
        boolean isActive = request.isActive();
        user.setActive(isActive);
        // Nếu isActive là false thì đặt thời gian deleteAt
        if (!isActive) {
            user.setDeletedAt(LocalDateTime.now());
        }
        return buildProfileResponse(userRepository.save(user));
    }
    
    public UserResponse getInfo() {
        User user = getCurrentUser();
        return buildProfileResponse(user);
    }

    
    public UserResponse updateInfo(UpdateUserInfoRequest request) {
        User user = getCurrentUser();
        String email = request.getEmail();

        /*
            Nếu email khác email hiện tại thì check trong
            csdl nếu có thì trả về lỗi email đã tồn tại
         */
        if (!user.getEmail().equals(email)) {
            if (userRepository.existsByEmail(email)) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }
        }
        
        user.setEmail(email);
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        return buildProfileResponse(userRepository.save(user));

    }

    public void changeEmail (ChangeEmailRequest request){
        String email = SecurityUtils.getCurrentUserEmail();
        String newEmail = request.getNewEmail();

        if (newEmail.equals(email)){
            throw new AppException(ErrorCode.EMAIL_ALREADY_USED);
        }

        if (userRepository.existsByEmail(newEmail)) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = getCurrentUser();
        String pendingMailKey = PENDING_EMAIL_PREFIX + user.getId();

        VerificationPurpose purpose = VerificationPurpose.CHANGE_EMAIL;
        cacheRepository.save(pendingMailKey, newEmail, purpose.getTtl());
        String otp = verificationService.createOtp(user.getId().toString(), purpose);
        emailService.sendVerifyOtp(newEmail, user.getFullName(), otp, purpose);
    }

    public void verifyChangeEmail(String otpCode){
        User user = getCurrentUser();

        VerificationPurpose purpose = VerificationPurpose.CHANGE_EMAIL;
        boolean isValid = verificationService.verifyOtp(user.getId().toString(), otpCode, purpose);
        if (!isValid) {
            throw new AppException(ErrorCode.OTP_VERIFICATION_FAILED);
        }

        String pendingEmailKey = PENDING_EMAIL_PREFIX + user.getId();
        String newEmail = cacheRepository.get(pendingEmailKey)
                .orElseThrow(() -> new AppException(ErrorCode.OTP_VERIFICATION_FAILED));

        user.setEmail(newEmail);
        userRepository.save(user);

        cacheRepository.delete(pendingEmailKey);
    }

    public String updateAvatar(MultipartFile file) {
        User user = getCurrentUser();
        String avatarUrl = cloudinaryService.uploadImage(file, UploadFolder.USER_AVATAR);
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        return avatarUrl;
    }

    // Hàm chung để map từ Entity sang Response DTO.
    private UserResponse buildProfileResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getAvatarUrl(),
                user.isActive(),
                user.getCreatedAt(),
                user.getDeletedAt());
    }
}

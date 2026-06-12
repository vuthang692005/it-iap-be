package com.example.it_iap.service.impl;

import com.example.it_iap.dto.user.request.ChangePasswordRequest;
import com.example.it_iap.dto.user.request.CreateUserRequest;
import com.example.it_iap.dto.user.request.UpdateUserRequest;
import com.example.it_iap.dto.user.request.UpdateUserInfoRequest;
import com.example.it_iap.dto.user.response.UserResponse;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.enums.Role;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.UserRepository;
import com.example.it_iap.service.CloudinaryService;
import com.example.it_iap.service.UserService;
import com.example.it_iap.util.SecurityUtils;
import lombok.RequiredArgsConstructor;

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
    private final CloudinaryService cloudinaryService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    public String updateAvatar(MultipartFile file) {
        User user = getCurrentUser();
        String avatarUrl = cloudinaryService.uploadImage(file, "User avatars");
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

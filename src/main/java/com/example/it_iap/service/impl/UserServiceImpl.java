package com.example.it_iap.service.impl;

import com.example.it_iap.dto.user.request.ChangePasswordRequest;
import com.example.it_iap.dto.user.request.SearchUserRequest;
import com.example.it_iap.dto.user.response.UserResponse;
import com.example.it_iap.entity.User;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.UserRepository;
import com.example.it_iap.service.UserService;
import com.example.it_iap.util.SecurityUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
                user.getDeletedAt()
        );
    }
}

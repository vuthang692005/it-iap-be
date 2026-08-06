package com.example.it_iap.service;

import java.util.UUID;

import com.example.it_iap.dto.user.request.*;
import com.example.it_iap.dto.user.response.UserStreakResponse;
import org.springframework.web.multipart.MultipartFile;

import com.example.it_iap.dto.user.response.UserResponse;
import com.example.it_iap.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    User getCurrentUser();
    void changePassword(ChangePasswordRequest request);
    Page<UserResponse> searchUser(SearchUserRequest request);
    UserResponse createUser(CreateUserRequest request);
    UserResponse updateUser(UUID id, UpdateUserRequest request);
    UserResponse getInfo();
    UserResponse updateInfo(UpdateUserInfoRequest request);
    String updateAvatar(MultipartFile file);
    void changeEmail (ChangeEmailRequest request);
    void verifyChangeEmail(String otpCode);
    void updateInterviewStreak();
    UserStreakResponse getActualCurrentStreak();
    void updateStudyStats ();
    void updateUserRankStats(Float newInterviewScore);
}

package com.example.it_iap.service;

import java.util.UUID;

import com.example.it_iap.dto.user.request.ChangePasswordRequest;
import com.example.it_iap.dto.user.request.CreateUserRequest;
import com.example.it_iap.dto.user.request.UpdateUserRequest;
import org.springframework.web.multipart.MultipartFile;

import com.example.it_iap.dto.user.request.ChangePasswordRequest;
import com.example.it_iap.dto.user.request.UpdateUserInfoRequest;
import com.example.it_iap.dto.user.response.UserResponse;
import com.example.it_iap.entity.User;

import jakarta.validation.Valid;

public interface UserService {
    User getCurrentUser();

    void changePassword(ChangePasswordRequest request);

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(UUID id, UpdateUserRequest request);
    UserResponse getInfo();

    UserResponse updateInfo(UpdateUserInfoRequest request);

    String updateAvatar(MultipartFile file);
}

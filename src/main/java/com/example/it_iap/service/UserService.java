package com.example.it_iap.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.it_iap.dto.user.request.ChangePasswordRequest;
import com.example.it_iap.dto.user.request.SearchUserRequest;
import com.example.it_iap.dto.user.request.UpdateUserInfoRequest;
import com.example.it_iap.dto.user.response.UserResponse;
import com.example.it_iap.entity.User;
import org.springframework.data.domain.Page;

import jakarta.validation.Valid;

public interface UserService {
    User getCurrentUser();
    void changePassword(ChangePasswordRequest request);
    Page<UserResponse> searchUser(SearchUserRequest request);
    UserResponse getInfo();
    UserResponse updateInfo(UpdateUserInfoRequest request);
    String updateAvatar(MultipartFile file);
}

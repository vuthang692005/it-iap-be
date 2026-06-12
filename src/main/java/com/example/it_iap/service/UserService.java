package com.example.it_iap.service;

import com.example.it_iap.dto.user.request.ChangePasswordRequest;
import com.example.it_iap.dto.user.request.SearchUserRequest;
import com.example.it_iap.dto.user.response.UserResponse;
import com.example.it_iap.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    User getCurrentUser();
    void changePassword(ChangePasswordRequest request);
    Page<UserResponse> searchUser(SearchUserRequest request);
}

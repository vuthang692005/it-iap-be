package com.example.it_iap.service;

import com.example.it_iap.dto.user.ChangePasswordRequest;
import com.example.it_iap.entity.User;

public interface UserService {
    User getCurrentUser();

    void changePassword(ChangePasswordRequest request);
}

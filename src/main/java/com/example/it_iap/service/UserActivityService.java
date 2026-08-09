package com.example.it_iap.service;

import com.example.it_iap.dto.userActivityLog.response.UserActivityLogResponse;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.enums.UserActionType;
import org.springframework.data.domain.Page;

public interface UserActivityService {
    void logActivity(UserActionType actionType, String description, User user);
    Page<UserActivityLogResponse> getMyActivityLogs(UserActionType actionType, int page);
}

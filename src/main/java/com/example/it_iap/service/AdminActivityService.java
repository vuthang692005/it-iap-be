package com.example.it_iap.service;

import com.example.it_iap.dto.adminActivityLog.response.AdminActivityLogResponse;
import com.example.it_iap.entity.enums.AdminActionType;
import org.springframework.data.domain.Page;

public interface AdminActivityService {
    void logActivity(AdminActionType actionType, String description);
    Page<AdminActivityLogResponse> getActivityLogs(AdminActionType actionType, int page);
}

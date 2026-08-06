package com.example.it_iap.service.impl;

import com.example.it_iap.dto.adminActivityLog.response.AdminActivityLogResponse;
import com.example.it_iap.entity.AdminActivityLog;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.enums.AdminActionType;
import com.example.it_iap.repository.AdminActivityLogRepository;
import com.example.it_iap.service.AdminActivityService;
import com.example.it_iap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminActivityServiceImpl implements AdminActivityService {
    public final AdminActivityLogRepository adminActivityLogRepository;
    private final UserService userService;

    @Async
    public void logActivity(AdminActionType actionType, String description, User user) {

        AdminActivityLog log = new AdminActivityLog();
        log.setActionType(actionType);
        log.setDescription(description);
        log.setUser(user);

        adminActivityLogRepository.save(log);
    }

    public Page<AdminActivityLogResponse> getActivityLogs(AdminActionType actionType, int page) {
        int page1 = Math.max(0, page - 1);
        int size = 10;

        Pageable pageable = PageRequest.of(page1, size, Sort.by("createdAt").descending());

        Page<AdminActivityLog> entityPage = adminActivityLogRepository.getLogsWithFilter(actionType, pageable);

        return entityPage.map(log -> new  AdminActivityLogResponse(
                log.getId(),
                log.getActionType(),
                log.getDescription(),
                log.getUser().getEmail(),
                log.getCreatedAt()
            )
        );
    }
}

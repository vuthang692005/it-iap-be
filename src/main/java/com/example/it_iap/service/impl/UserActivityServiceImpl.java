package com.example.it_iap.service.impl;

import com.example.it_iap.dto.userActivityLog.response.UserActivityLogResponse;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.UserActivityLog;
import com.example.it_iap.entity.enums.UserActionType;
import com.example.it_iap.repository.UserActivityLogRepository;
import com.example.it_iap.service.UserActivityService;
import com.example.it_iap.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserActivityServiceImpl implements UserActivityService {
    private final UserActivityLogRepository userActivityLogRepository;

    @Async
    public void logActivity(UserActionType actionType, String description, User user) {
        UserActivityLog log = new UserActivityLog();
        log.setActionType(actionType);
        log.setDescription(description);
        log.setUser(user);

        userActivityLogRepository.save(log);
    }

    public Page<UserActivityLogResponse> getMyActivityLogs(UserActionType actionType, int page) {
        UUID userId = SecurityUtils.getCurrentUserId();

        int page1 = Math.max(0, page - 1);
        int size = 10;

        Pageable pageable = PageRequest.of(page1, size, Sort.by("createdAt").descending());

        Page<UserActivityLog> entityPage = userActivityLogRepository.getLogsWithFilter(
                userId, actionType, pageable
        );

        return entityPage.map(log -> new UserActivityLogResponse(
                log.getActionType(),
                log.getDescription(),
                log.getCreatedAt()
        ));
    }
}


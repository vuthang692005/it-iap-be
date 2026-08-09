package com.example.it_iap.repository;

import com.example.it_iap.entity.UserActivityLog;
import com.example.it_iap.entity.enums.UserActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    @Query("""
        SELECT a FROM UserActivityLog a
        WHERE a.user.id = :userId
        AND (:actionType IS NULL OR a.actionType = :actionType)
    """)
    Page<UserActivityLog> getLogsWithFilter(
            @Param("userId") UUID userId,
            @Param("actionType") UserActionType actionType,
            Pageable pageable
    );
}

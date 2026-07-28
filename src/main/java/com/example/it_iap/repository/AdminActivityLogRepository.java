package com.example.it_iap.repository;

import com.example.it_iap.entity.AdminActivityLog;
import com.example.it_iap.entity.enums.AdminActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminActivityLogRepository extends JpaRepository<AdminActivityLog, Long> {
    @Query("""
        SELECT a FROM AdminActivityLog a 
        WHERE (:actionType IS NULL OR a.actionType = :actionType)
    """)
    Page<AdminActivityLog> getLogsWithFilter(
            @Param("actionType") AdminActionType actionType,
            Pageable pageable
    );
}
package com.example.it_iap.repository;

import com.example.it_iap.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, String> {
    List<UserSession> findByUserIdAndIsActiveTrueOrderByLastActiveAtDesc(UUID userId);

    Optional<UserSession> findByIdAndUserId(String id, UUID userId);

    Optional<UserSession> findByRefreshTokenJti(String jti);

    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.id = :id AND s.user.id = :userId")
    int deactivateSession(@Param("id") String id, @Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.user.id = :userId AND s.id != :currentSessionId AND s.isActive = true")
    int deactivateOtherSessions(@Param("userId") UUID userId, @Param("currentSessionId") String currentSessionId);
}

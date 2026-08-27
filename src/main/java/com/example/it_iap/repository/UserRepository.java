package com.example.it_iap.repository;

import com.example.it_iap.dto.forumPost.response.StreakLeaderBoardResponse;
import com.example.it_iap.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "(:email IS NULL OR TRIM(:email) = '' OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:fullName IS NULL OR TRIM(:fullName) = '' OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) AND " +
            "(:phoneNumber IS NULL OR TRIM(:phoneNumber) = '' OR u.phoneNumber LIKE CONCAT('%', :phoneNumber, '%')) AND " +
            "CAST(u.roles AS string) NOT LIKE '%ADMIN%'")
    Page<User> searchUsers(
            @Param("email") String email,
            @Param("fullName") String fullName,
            @Param("phoneNumber") String phoneNumber,
            Pageable pageable
    );

    Slice<User> findAllBy(PageRequest of);

    Slice<User> findAllByCurrentStreakGreaterThanAndLastInterviewDateAfter(int currentStreak, LocalDateTime time, PageRequest of);

    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    java.util.List<User> findAllByScheduled2faDisableAtIsNotNullAndScheduled2faDisableAtBefore(LocalDateTime now);

    @Modifying
    @Query("UPDATE User u SET u.currentStreak = 0 WHERE u.currentStreak > 0 AND (u.lastInterviewDate IS NULL OR u.lastInterviewDate < :startOfYesterday)")
    int resetExpiredStreaks(@Param("startOfYesterday") LocalDateTime startOfYesterday);

    @Query("""
                SELECT new com.example.it_iap.dto.forumPost.response.StreakLeaderBoardResponse(
                    u.fullName,
                    u.avatarUrl,
                    u.currentStreak
                )
                FROM User u
                WHERE u.currentGpa >= 4.0
                  AND u.deletedAt IS NULL
                  AND u.isActive = true
                  AND u.currentStreak > 0
                  AND u.lastInterviewDate >= :validStreakDate
                ORDER BY u.currentStreak DESC,
                         u.totalCompletedInterviews DESC,
                         u.longestStreak DESC
            """)
    List<StreakLeaderBoardResponse> findTop10ByGpaAndActive(@Param("validStreakDate") LocalDateTime validStreakDate, Pageable pageable);
}

package com.example.it_iap.repository;

import com.example.it_iap.entity.Interview;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.enums.InterviewMode;
import com.example.it_iap.entity.enums.InterviewStatus;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    Optional<Interview> findByIdAndProfile_UserId(Long interviewId, UUID userId);

    @EntityGraph(attributePaths = {"interviewQuestions", "interviewQuestions.question", "promptVersion"})
    Optional<Interview> findWithInterviewQuestionsAndQuestionByIdAndProfile_UserId(Long interviewId, UUID userId);

    @EntityGraph(attributePaths = {"interviewQuestions", "interviewQuestions.question", "promptVersion"})
    Optional<Interview> findWithInterviewQuestionsAndQuestionById(Long interviewId);

    @EntityGraph(attributePaths = {"profile"})
    @Query("""
                SELECT i FROM Interview i
                WHERE (:userId IS NULL OR i.profile.user.id = :userId)
                  AND (:profileId IS NULL OR i.profile.id = :profileId)
                  AND (:mode IS NULL OR i.mode = :mode)
                  AND (:status IS NULL OR i.status = :status)
            """)
    Page<Interview> getInterviewHistory(
            @Param("userId") UUID userId,
            @Param("profileId") Long profileId,
            @Param("mode") InterviewMode mode,
            @Param("status") InterviewStatus status,
            Pageable pageable
    );

    long countByProfileIdAndStatusAndCompletedAtBetween(
            Long profileId,
            InterviewStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<Interview> findTop10ByProfileIdAndStatusOrderByCompletedAtDesc(
            Long profileId,
            InterviewStatus status
    );

    @Query("""
            SELECT u AS user, COUNT(i) AS unfinishedInterviewCount
            FROM Interview i
            JOIN i.profile p
            JOIN p.user u
            WHERE i.status = :status
            GROUP BY u
            ORDER BY u.id
            """)
    Slice<UnfinishedInterviewReminder> findUnfinishedInterviewRemindersByStatus(
            @Param("status") InterviewStatus status,
            Pageable pageable
    );

    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    long countByCreatedAtBetweenAndOverallResultNotNull(LocalDateTime startDate, LocalDateTime endDate);

    long countByOverallResultNotNull();

    int countByProfileIdAndStatus(Long profileId, InterviewStatus status);

    interface UnfinishedInterviewReminder {
        User getUser();

        Long getUnfinishedInterviewCount();
    }

    @Query("""
                SELECT FUNCTION('DATE', i.createdAt) AS date, COUNT(i.id) AS count
                FROM Interview i
                WHERE i.status = :status
                  AND i.createdAt BETWEEN :startDate AND :endDate
                GROUP BY FUNCTION('DATE', i.createdAt)
                ORDER BY FUNCTION('DATE', i.createdAt) ASC
            """)
    List<TrendProjection> countInterviewTrendsByDate(
            @Param("status") InterviewStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    interface TrendProjection {
        java.sql.Date getDate();

        Long getCount();
    }

    @Query("""
                SELECT FUNCTION('DATE', i.createdAt) AS date,
                       FUNCTION('HOUR', i.createdAt) AS hour,
                       COUNT(i.id) AS count
                FROM Interview i
                WHERE i.status = :status
                  AND i.createdAt BETWEEN :startDate AND :endDate
                GROUP BY FUNCTION('DATE', i.createdAt), FUNCTION('HOUR', i.createdAt)
                ORDER BY FUNCTION('DATE', i.createdAt) ASC, FUNCTION('HOUR', i.createdAt) ASC
            """)
    List<HourlyTrendProjection> countInterviewTrendsByHour(
            @Param("status") InterviewStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    interface HourlyTrendProjection {
        java.sql.Date getDate();

        Integer getHour(); // Thêm hàm lấy ra Giờ (0 - 23)

        Long getCount();
    }

    @EntityGraph(attributePaths = {"interviewQuestions"})
    List<Interview> findByStatusAndStartAtBefore(InterviewStatus status, LocalDateTime time);

    @Query("SELECT COUNT(i) FROM Interview i WHERE i.profile.user.id = :userId " +
            "AND i.status IN :statuses " +
            "AND i.createdAt BETWEEN :startDate AND :endDate")
    int countTodayInterviews(
            @Param("userId") UUID userId,
            @Param("statuses") List<InterviewStatus> statuses,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}

package com.example.it_iap.repository;

import com.example.it_iap.entity.Interview;
import com.example.it_iap.entity.enums.InterviewMode;
import com.example.it_iap.entity.enums.InterviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
}
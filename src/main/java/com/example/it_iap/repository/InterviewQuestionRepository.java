package com.example.it_iap.repository;

import com.example.it_iap.entity.InterviewQuestion;
import com.example.it_iap.entity.enums.InterviewQuestionStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
    @EntityGraph(attributePaths = {"question"})
    Optional<InterviewQuestion> findFirstWithQuestionByInterviewIdAndStatusOrderByOrderIndexAsc(
            Long interviewId,
            InterviewQuestionStatus status
    );

    @EntityGraph(attributePaths = {"question", "interview", "promptVersion", "chatSession"})
    @Query("SELECT iq FROM InterviewQuestion iq " +
            "WHERE iq.id = :interviewQuestionId " +
            "AND iq.interview.profile.user.id = :userId")
    Optional<InterviewQuestion> findValidQuestionForUser(
            @Param("interviewQuestionId") Long interviewQuestionId,
            @Param("userId") UUID userId
    );

    boolean existsByInterviewIdAndOrderIndexGreaterThan(Long interviewId, int currentOrderIndex);

    @Modifying
    @Query("UPDATE InterviewQuestion q SET q.status = :newStatus WHERE q.id = :id AND q.status = :oldStatus")
    int updateStatusToProcessing(
            @Param("id") long id,
            @Param("newStatus") InterviewQuestionStatus newStatus,
            @Param("oldStatus") InterviewQuestionStatus oldStatus
    );

    // Mở khóa câu hỏi (Cập nhật thẳng luôn không cần check trạng thái cũ)
    @Modifying
    @Query("UPDATE InterviewQuestion q SET q.status = :newStatus WHERE q.id = :id")
    int updateStatusToAnswering(
            @Param("id") long id,
            @Param("newStatus") InterviewQuestionStatus newStatus
    );

}

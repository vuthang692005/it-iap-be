package com.example.it_iap.repository;

import com.example.it_iap.entity.ChatSession;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    Optional<ChatSession> findByIdAndDeleteAtIsNull(long id);
    List<ChatSession> findAllByUserIdAndDeleteAtIsNullAndInterviewQuestionIsNull(UUID userId);

    @EntityGraph(attributePaths = {"promptVersion"})
    Optional<ChatSession> findByIdAndUserIdAndDeleteAtIsNullAndInterviewQuestionIsNull(long id, UUID userId);
}

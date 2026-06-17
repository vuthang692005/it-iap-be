package com.example.it_iap.repository;

import com.example.it_iap.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT c FROM ChatMessage c WHERE c.chatSession.id = :sessionId " +
            "AND c.chatSession.deleteAt IS NULL " +
            "ORDER BY c.createdAt DESC")
    List<ChatMessage> findLatestMessages(@Param("sessionId") Long sessionId, Pageable pageable);
}
package com.example.it_iap.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.it_iap.entity.ForumPost;
import com.example.it_iap.entity.enums.ForumPostType;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {

    boolean existsByUserIdAndPostTypeAndCreatedAtAfter(UUID id, ForumPostType postType, LocalDateTime startOfToday);
    
}

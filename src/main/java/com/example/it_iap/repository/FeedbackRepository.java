package com.example.it_iap.repository;

import com.example.it_iap.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    @Query("SELECT f FROM Feedback f WHERE " +
            "(:rating IS NULL OR f.rating = :rating) AND " +
            "(:userId IS NULL OR f.user.id = :userId) AND " +
            "(:hasAdminReply IS NULL OR " +
            "(:hasAdminReply = true AND f.adminReply IS NOT NULL) OR " +
            "(:hasAdminReply = false AND f.adminReply IS NULL)) AND " +
            "(:hasImageUrl IS NULL OR " +
            "(:hasImageUrl = true AND f.imageUrl IS NOT NULL) OR " +
            "(:hasImageUrl = false AND f.imageUrl IS NULL))")
    Page<Feedback> findFeedbacksWithFilter(
            @Param("rating") Integer rating,
            @Param("userId") UUID userId,
            @Param("hasAdminReply") Boolean hasAdminReply,
            @Param("hasImageUrl") Boolean hasImageUrl,
            Pageable pageable
    );
}
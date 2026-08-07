package com.example.it_iap.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.it_iap.dto.forumPost.response.GetForumPostDTO;
import com.example.it_iap.entity.ForumPost;
import com.example.it_iap.entity.enums.ForumPostType;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {

    boolean existsByUserIdAndPostTypeAndCreatedAtAfter(UUID id, ForumPostType postType, LocalDateTime startOfToday);

    @Query("""
            SELECT new com.example.it_iap.dto.forumPost.response.GetForumPostDTO(
                p.id,
                u.fullName,
                u.avatarUrl,
                p.postType,
                p.sharedData,
                p.createdAt,
                p.visible,
            
                COUNT(
                    CASE
                        WHEN r.type = com.example.it_iap.entity.enums.ReactionType.LOVE
                        THEN 1
                        ELSE NULL
                    END
                ),
            
                COUNT(
                    CASE
                        WHEN r.type = com.example.it_iap.entity.enums.ReactionType.HAHA
                        THEN 1
                        ELSE NULL
                    END
                ),
            
                COUNT(
                    CASE
                        WHEN r.type = com.example.it_iap.entity.enums.ReactionType.WOW
                        THEN 1
                        ELSE NULL
                    END
                ),
            
                myReaction.type
            )
            FROM ForumPost p
            JOIN p.user u
            LEFT JOIN p.reactions r
            LEFT JOIN p.reactions myReaction
                ON myReaction.user.id = :userId
            WHERE p.visible = true    
            GROUP BY
                p.id,
                u.fullName,
                u.avatarUrl,
                p.postType,
                p.sharedData,
                p.createdAt,
                p.visible,
                myReaction.type
            ORDER BY FUNCTION('RAND', :seed)
            """)
    Slice<GetForumPostDTO> getPosts(
            @Param("userId") UUID userId,
            @Param("seed") Integer seed,
            Pageable pageable
    );

    @Query("""
            SELECT new com.example.it_iap.dto.forumPost.response.GetForumPostDTO(
                p.id,
                u.fullName,
                u.avatarUrl,
                p.postType,
                p.sharedData,
                p.createdAt,
                p.visible,
            
                COUNT(
                    CASE
                        WHEN r.type = com.example.it_iap.entity.enums.ReactionType.LOVE
                        THEN 1
                        ELSE NULL
                    END
                ),
            
                COUNT(
                    CASE
                        WHEN r.type = com.example.it_iap.entity.enums.ReactionType.HAHA
                        THEN 1
                        ELSE NULL
                    END
                ),
            
                COUNT(
                    CASE
                        WHEN r.type = com.example.it_iap.entity.enums.ReactionType.WOW
                        THEN 1
                        ELSE NULL
                    END
                ),
            
                myReaction.type
            )
            FROM ForumPost p
            JOIN p.user u
            LEFT JOIN p.reactions r
            LEFT JOIN p.reactions myReaction
                ON myReaction.user.id = :userId
            WHERE p.user.id = :userId
            AND (:visible IS NULL OR p.visible = :visible)
            GROUP BY
                p.id,
                u.fullName,
                u.avatarUrl,
                p.postType,
                p.sharedData,
                p.createdAt,
                p.visible,
                myReaction.type
            """)
    Slice<GetForumPostDTO> getMyPosts(@Param("userId") UUID userId, @Param("visible") Boolean visible, Pageable pageable);

    Optional<ForumPost> findByIdAndUserId(Long postId, UUID id);
}   

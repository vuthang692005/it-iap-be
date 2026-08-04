package com.example.it_iap.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.it_iap.dto.reaction.response.PostReactionData;
import com.example.it_iap.entity.PostReaction;

public interface PostReactionRepository extends JpaRepository<PostReaction, UUID> {
    PostReaction findByUserIdAndPostId(UUID userId, Long postId);

    @Query("""
            SELECT new com.example.it_iap.dto.reaction.response.PostReactionData(
                COUNT(
                    CASE
                        WHEN pr.type = com.example.it_iap.entity.enums.ReactionType.LOVE
                        THEN 1
                        ELSE NULL
                    END
                ),
                COUNT(
                    CASE
                        WHEN pr.type = com.example.it_iap.entity.enums.ReactionType.HAHA
                        THEN 1
                        ELSE NULL
                    END
                ),
                COUNT(
                    CASE
                        WHEN pr.type = com.example.it_iap.entity.enums.ReactionType.WOW
                        THEN 1
                        ELSE NULL
                    END
                )
            )
            FROM PostReaction pr
            WHERE pr.post.id = :forumPostId
            """)
    PostReactionData countPostReactionData(@Param("forumPostId") Long forumPostId);

}
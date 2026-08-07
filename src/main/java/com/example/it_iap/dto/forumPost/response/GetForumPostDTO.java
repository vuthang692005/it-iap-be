package com.example.it_iap.dto.forumPost.response;

import java.time.LocalDateTime;

import com.example.it_iap.entity.enums.ForumPostType;
import com.example.it_iap.entity.enums.ReactionType;
import tools.jackson.databind.JsonNode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetForumPostDTO {
    Long postId;

    String userFullName;

    String userAvatarUrl;

    ForumPostType postType;

    JsonNode sharedData;

    LocalDateTime createdAt;

    boolean isVisible;

    int totalLove;

    int totalHaha;

    int totalWow;

    ReactionType myReaction;

    public GetForumPostDTO(
            Long postId,
            String userAvatarUrl,
            ForumPostType postType,
            JsonNode sharedData,
            LocalDateTime createdAt,
            boolean isVisible,
            Long totalLove,
            Long totalHaha,
            Long totalWow,
            ReactionType myReaction
    ) {
        this.postId = postId;
        this.userAvatarUrl = userAvatarUrl;
        this.postType = postType;
        this.sharedData = sharedData;
        this.createdAt = createdAt;
        this.isVisible = isVisible;
        this.totalLove = totalLove == null ? 0 : totalLove.intValue();
        this.totalHaha = totalHaha == null ? 0 : totalHaha.intValue();
        this.totalWow = totalWow == null ? 0 : totalWow.intValue();
        this.myReaction = myReaction;
    }
}

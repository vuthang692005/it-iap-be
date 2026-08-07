package com.example.it_iap.dto.forumPost.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StreakLeaderBoardResponse {
    String fullName;
    String avatarUrl;
    int currentStreak;
}

package com.example.it_iap.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserStreakResponse {
    private int currentStreak;
    private int longestStreak;
}

package com.example.it_iap.dto.interview.response;

import com.example.it_iap.entity.enums.InterviewMode;
import com.example.it_iap.entity.enums.InterviewStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class InterviewResponse {
    private String title;
    private InterviewMode mode;
    private InterviewStatus status;
    private LocalDateTime startAt;
    private LocalDateTime completedAt;
    private long profileId;
    private String profileTitle;
    private long interviewId;
}

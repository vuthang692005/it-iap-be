package com.example.it_iap.dto.interview.response;

import com.example.it_iap.entity.enums.InterviewMode;
import com.example.it_iap.entity.enums.InterviewStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResponse {
    private String title;
    private InterviewMode mode;
    private InterviewStatus status;
    private LocalDateTime startAt;
    private LocalDateTime completedAt;
    private Long profileId;
    private String profileTitle;
    private Long interviewId;
    private Float totalPoint;
}

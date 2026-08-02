package com.example.it_iap.dto.feedback.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackListResponse {
    private Page<FeedbackResponse> feedbacks;
    private Long totalFeedbacks;
    private Double averageRating;
}

package com.example.it_iap.dto.interview.response;

import com.example.it_iap.dto.interview.FeedbackForQuestion;
import com.example.it_iap.entity.Json.OverallResult;
import com.example.it_iap.entity.enums.InterviewMode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class GetFeedbackResponse {
    private boolean isProcessing;
    private InterviewMode interviewMode;
    private List<FeedbackForQuestion> feedbackForQuestions;
    private OverallResult overallResult;
}

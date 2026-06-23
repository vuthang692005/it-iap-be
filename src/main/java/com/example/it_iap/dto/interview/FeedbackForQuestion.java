package com.example.it_iap.dto.interview;

import com.example.it_iap.entity.Json.AIFeedback;
import com.example.it_iap.entity.enums.QuestionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackForQuestion {
    private long interviewQuestionId;
    private int orderIndex;
    private String questionContent;
    private String userAnswer;
    private AIFeedback feedback;
    private QuestionType questionType;
}

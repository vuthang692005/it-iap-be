package com.example.it_iap.dto.ai;


public record AiEvaluationEvent(
        Long interviewQuestionId,
        String questionContent,
        String userAnswer,
        String promptContent,
        String model,
        String suggestedAnswer
) { }

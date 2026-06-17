package com.example.it_iap.service;

import com.example.it_iap.dto.question.request.AICreateQuestionRequest;
import com.example.it_iap.entity.PromptVersion;
import com.example.it_iap.entity.Question;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;

import java.util.List;

public interface QuestionService {
    List<Question> getRandomInterviewQuestions (TargetLevel level, TargetPosition position);
    List<Question> aiGenerateQuestion (
            List<AICreateQuestionRequest> requests, TargetLevel level,
            TargetPosition position, PromptVersion promptVersion);
}

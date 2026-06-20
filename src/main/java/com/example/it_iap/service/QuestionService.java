package com.example.it_iap.service;

import org.springframework.data.domain.Page;

import com.example.it_iap.dto.question.request.QuestionRequest;
import com.example.it_iap.dto.question.request.SearchQuestionRequest;
import com.example.it_iap.dto.question.response.QuestionResponse;
import com.example.it_iap.dto.question.request.AICreateQuestionRequest;
import com.example.it_iap.entity.PromptVersion;
import com.example.it_iap.entity.Question;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;

import java.util.List;

public interface QuestionService {

    QuestionResponse createQuestion(QuestionRequest request);

    QuestionResponse updateQuestion(QuestionRequest request, Long id);

    Page<QuestionResponse> searchQuestion(SearchQuestionRequest request);

    List<Question> getRandomInterviewQuestions (TargetLevel level, TargetPosition position);
  
    List<Question> generateAndSaveAiQuestions (int quantity, TargetLevel level, TargetPosition position);
}

package com.example.it_iap.service;

import org.springframework.data.domain.Page;

import com.example.it_iap.dto.question.request.QuestionRequest;
import com.example.it_iap.dto.question.request.SearchQuestionRequest;
import com.example.it_iap.dto.question.response.QuestionResponse;

public interface QuestionService {

    QuestionResponse createQuestion(QuestionRequest request);

    QuestionResponse updateQuestion(QuestionRequest request, Long id);

    Page<QuestionResponse> searchQuestion(SearchQuestionRequest request);
}

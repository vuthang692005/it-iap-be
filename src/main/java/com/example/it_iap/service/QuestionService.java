package com.example.it_iap.service;

import com.example.it_iap.dto.question.request.QuestionRequest;
import com.example.it_iap.dto.question.response.QuestionResponse;

public interface QuestionService {

    QuestionResponse createQuestion(QuestionRequest request);

    QuestionResponse updateQuestion(QuestionRequest request, Long id);
}

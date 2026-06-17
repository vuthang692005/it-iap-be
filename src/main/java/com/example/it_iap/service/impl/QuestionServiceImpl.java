package com.example.it_iap.service.impl;

import org.springframework.stereotype.Service;

import com.example.it_iap.dto.question.request.QuestionRequest;
import com.example.it_iap.dto.question.response.QuestionResponse;
import com.example.it_iap.entity.Question;
import com.example.it_iap.entity.enums.QuestionStatus;
import com.example.it_iap.entity.enums.QuestionType;
import com.example.it_iap.entity.enums.Source;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;
import com.example.it_iap.repository.QuestionRepository;
import com.example.it_iap.service.QuestionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "QUESTION_SERVICE")
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService{
    private final QuestionRepository questionRepository;

    @Override
    public QuestionResponse createQuestion(QuestionRequest request) {
        Question question = new Question();
        mapRequestToQuestion(request, question);
        // Map thủ công
        question.setDeleteAt(null);
        question.setPromptVersion(null);
        question.setSource(Source.ADMIN);
        // Tạo thủ công tức là chỉ admin nên APPROVE
        question.setStatus(QuestionStatus.APPROVED); 
        return toQuestionResponse(questionRepository.save(question));
    }

    /* Phương thức hỗ trợ */
    private QuestionResponse toQuestionResponse(Question question) {
        return QuestionResponse.builder()
            .id(question.getId())
            .content(question.getContent())
            .suggestedAnswer(question.getSuggestedAnswer())
            .position(question.getPosition())
            .level(question.getLevel())
            .category(question.getCategory())
            .skillTag(question.getSkillTag())
            .timeLimitSeconds(question.getTimeLimitSeconds())
            .source(question.getSource())
            .status(question.getStatus())
            .deleteAt(question.getDeleteAt())
            .build();
    }

    private void mapRequestToQuestion(QuestionRequest request, Question question) {
        question.setContent(request.getContent());
        question.setSuggestedAnswer(request.getSuggestedAnswer());
        question.setHintContent(request.getHintContent());
        question.setPosition(TargetPosition.fromString(request.getPosition()));
        question.setLevel(TargetLevel.fromString(request.getLevel()));
        question.setCategory(QuestionType.fromString(request.getCategory()));
        question.setSkillTag(request.getSkillTag());
        question.setTimeLimitSeconds(request.getTimeLimitSeconds());
    }
}

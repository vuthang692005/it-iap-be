package com.example.it_iap.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.it_iap.dto.question.request.QuestionRequest;
import com.example.it_iap.dto.question.request.SearchQuestionRequest;
import com.example.it_iap.dto.question.response.QuestionResponse;
import com.example.it_iap.entity.Question;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.QuestionRepository;
import com.example.it_iap.service.QuestionService;
import com.example.it_iap.dto.question.request.AICreateQuestionRequest;
import com.example.it_iap.entity.PromptVersion;
import com.example.it_iap.entity.enums.*;
import com.example.it_iap.service.AIService;
import com.example.it_iap.service.PromptVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j(topic = "QUESTION_SERVICE")
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final AIService aiService;
    private final PromptVersionService promptVersionService;

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

    @Override
    public QuestionResponse updateQuestion(QuestionRequest request, Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
        // Map thủ công
        boolean isDelete = request.isDelete();
        if (isDelete && question.getDeleteAt() == null) {
            question.setDeleteAt(LocalDateTime.now());
        } else if (!isDelete && question.getDeleteAt() != null){
            question.setDeleteAt(null);
        }
        question.setStatus(QuestionStatus.fromString(request.getStatus()));
        // Map nhanh
        mapRequestToQuestion(request, question);
        return toQuestionResponse(questionRepository.save(question));
    }

    @Override
    public Page<QuestionResponse> searchQuestion(SearchQuestionRequest request) {
        int page = Math.max(0, request.getPage() - 1);
        int size = Math.max(10, request.getSize());
        PageRequest pageable = PageRequest.of(page, size);

        TargetPosition position = TargetPosition.fromString(request.getPosition());
        TargetLevel level = TargetLevel.fromString(request.getLevel());
        QuestionType category = QuestionType.fromString(request.getCategory());
        Source source = Source.fromString(request.getSource());
        QuestionStatus status = QuestionStatus.fromString(request.getStatus());

        Page<Question> questions = questionRepository.searchQuestions(
                request.getContent(),
                position,
                level,
                category,
                source,
                status,
                pageable);

        return questions.map(this::toQuestionResponse);
    }

    /* Phương thức hỗ trợ */
    private QuestionResponse toQuestionResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .content(question.getContent())
                .suggestedAnswer(question.getSuggestedAnswer())
                .hintContent(question.getHintContent())
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
    
    @Transactional(readOnly = true)
    public List<Question> getRandomInterviewQuestions (TargetLevel level, TargetPosition position){
        List<Question> questions = new ArrayList<>();
        List<Question> questionsTechnical = questionRepository.findRandomQuestions(level, position, QuestionType.TECHNICAL, 4);
        List<Question> questionsBehavioral = questionRepository.findRandomQuestions(level, position, QuestionType.BEHAVIORAL, 3);
        List<Question> questionsSituational = questionRepository.findRandomQuestions(level, position, QuestionType.SITUATIONAL, 3);

        questions.addAll(questionsTechnical);
        questions.addAll(questionsBehavioral);
        questions.addAll(questionsSituational);

        log.info("questions: {}", questions);
        return questions;
    }

    public List<Question> generateAndSaveAiQuestions (int quantity, TargetLevel level, TargetPosition position){
        PromptVersion promptVersion = promptVersionService.getPromptActive(PromptUseCase.QUESTION_GENERATOR);

        List<AICreateQuestionRequest> requests = aiService.generateQuestion(quantity, level, position, promptVersion);

        if (requests == null || requests.isEmpty()) {
            return List.of(); // Trả về list rỗng luôn
        }

        List<Question> questions = requests.stream().map(dto -> {
            Question question = new Question();
            question.setContent(dto.getContent());
            question.setSuggestedAnswer(dto.getSuggestedAnswer());
            question.setHintContent(dto.getHintContent());
            question.setCategory(dto.getCategory());
            question.setSkillTag(dto.getSkillTag());
            question.setTimeLimitSeconds(dto.getTimeLimitSeconds());

            question.setLevel(level);
            question.setSource(Source.AI);
            question.setPosition(position);
            question.setPromptVersion(promptVersion);
            question.setStatus(QuestionStatus.PENDING);

            return question;
        }).toList();

        return questionRepository.saveAll(questions);
    }
}

package com.example.it_iap.service.impl;

import com.example.it_iap.dto.question.request.AICreateQuestionRequest;
import com.example.it_iap.entity.PromptVersion;
import com.example.it_iap.entity.Question;
import com.example.it_iap.entity.enums.*;
import com.example.it_iap.repository.QuestionRepository;
import com.example.it_iap.service.AIService;
import com.example.it_iap.service.PromptVersionService;
import com.example.it_iap.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final AIService aiService;
    private final PromptVersionService promptVersionService;

    @Transactional
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

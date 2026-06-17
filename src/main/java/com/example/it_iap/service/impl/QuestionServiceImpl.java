package com.example.it_iap.service.impl;

import com.example.it_iap.dto.question.request.AICreateQuestionRequest;
import com.example.it_iap.entity.PromptVersion;
import com.example.it_iap.entity.Question;
import com.example.it_iap.entity.enums.*;
import com.example.it_iap.repository.QuestionRepository;
import com.example.it_iap.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> getRandomInterviewQuestions (TargetLevel level, TargetPosition position){
        List<Question> questions = new ArrayList<>();
        List<Question> questionsTechnical = questionRepository.findRandomQuestions(level, position, QuestionType.TECHNICAL, 3);
        List<Question> questionsBehavioral = questionRepository.findRandomQuestions(level, position, QuestionType.BEHAVIORAL, 3);
        List<Question> questionsSituational = questionRepository.findRandomQuestions(level, position, QuestionType.SITUATIONAL, 2);

        if (questionsTechnical != null) {
            questions.addAll(questionsTechnical);
        }
        if (questionsBehavioral != null) {
            questions.addAll(questionsBehavioral);
        }
        if (questionsSituational != null) {
            questions.addAll(questionsSituational);
        }

        return questions;
    }

    @Transactional
    public List<Question> aiGenerateQuestion (
            List<AICreateQuestionRequest> requests, TargetLevel level,
            TargetPosition position, PromptVersion promptVersion){

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

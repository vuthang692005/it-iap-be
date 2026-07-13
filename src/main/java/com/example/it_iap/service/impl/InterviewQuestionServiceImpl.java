package com.example.it_iap.service.impl;

import com.example.it_iap.dto.ai.AiEvaluationEvent;
import com.example.it_iap.entity.Interview;
import com.example.it_iap.entity.InterviewQuestion;
import com.example.it_iap.entity.Json.AIFeedback;
import com.example.it_iap.entity.PromptVersion;
import com.example.it_iap.entity.Question;
import com.example.it_iap.entity.enums.InterviewMode;
import com.example.it_iap.entity.enums.InterviewQuestionStatus;
import com.example.it_iap.entity.enums.PromptUseCase;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.InterviewQuestionRepository;
import com.example.it_iap.service.ChatSessionService;
import com.example.it_iap.service.InterviewQuestionService;
import com.example.it_iap.service.PromptVersionService;
import com.example.it_iap.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class InterviewQuestionServiceImpl implements InterviewQuestionService {
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final PromptVersionService promptVersionService;
    private final ApplicationEventPublisher eventPublisher;
    private final ChatSessionService chatSessionService;

    public List<InterviewQuestion> createInterviewQuestion (List<Question> questions, Interview interview){
        PromptUseCase promptUseCase = interview.getMode().getPromptUseCase();
        PromptVersion promptVersion = promptVersionService.getPromptActive(promptUseCase);

        List<InterviewQuestion> interviewQuestions = IntStream.range(0, questions.size())
                .mapToObj(i -> {
                    Question question = questions.get(i);
                    InterviewQuestion interviewQuestion = new InterviewQuestion();

                    interviewQuestion.setQuestion(question);
                    interviewQuestion.setInterview(interview);
                    interviewQuestion.setPromptVersion(promptVersion);
                    interviewQuestion.setStatus(InterviewQuestionStatus.UNANSWERED);

                    // Index chạy từ 0 nên cộng thêm 1
                    interviewQuestion.setOrderIndex(i + 1);

                    return interviewQuestion;
                })
                .toList();

        return interviewQuestionRepository.saveAll(interviewQuestions);
    }

    public InterviewQuestion getCurrentQuestion (long interviewId) {
        return interviewQuestionRepository
                .findFirstWithQuestionByInterviewIdAndStatusOrderByOrderIndexAsc(interviewId, InterviewQuestionStatus.ANSWERING)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_INTERVIEW_NOT_FOUND));
    }

    public InterviewQuestion activateNextUnansweredQuestion (long interviewId, InterviewMode interviewMode){
        InterviewQuestion interviewQuestion = interviewQuestionRepository
                .findFirstWithQuestionByInterviewIdAndStatusOrderByOrderIndexAsc(interviewId, InterviewQuestionStatus.UNANSWERED)
                .orElse(null);

        if (interviewQuestion == null){
            return null;
        }

        interviewQuestion.setStatus(InterviewQuestionStatus.ANSWERING);

        if (interviewMode.isHasLimitTime()) {
            interviewQuestion.setEndAt(LocalDateTime.now().plusSeconds(interviewQuestion.getQuestion().getTimeLimitSeconds()));
        }

        if (interviewMode.isHasChatSession()){
            chatSessionService.createInterviewSession(interviewQuestion);
        }

        return interviewQuestionRepository.save(interviewQuestion);
    }

    public boolean hasNextQuestion (Long interviewId, int currentOrderIndex){
        return interviewQuestionRepository.existsByInterviewIdAndOrderIndexGreaterThan(interviewId, currentOrderIndex);
    }

    public InterviewQuestion findValidQuestionForUser (long interviewQuestionId){
        UUID userId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = SecurityUtils.isAdmin();

        if(isAdmin){
            return interviewQuestionRepository.findById(interviewQuestionId)
                    .orElseThrow(() -> new AppException(ErrorCode.QUESTION_INTERVIEW_NOT_FOUND));
        }

        return interviewQuestionRepository.findValidQuestionForUser(interviewQuestionId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_INTERVIEW_NOT_FOUND));
    }

    public void completeInterviewQuestion (InterviewQuestion interviewQuestion) {
        if (interviewQuestion.getStatus() != InterviewQuestionStatus.ANSWERING) {
            throw new AppException(ErrorCode.QUESTION_NOT_ACTIVE);
        }

        interviewQuestion.setCompleteAt(LocalDateTime.now());
        interviewQuestion.setStatus(InterviewQuestionStatus.ANSWERED);

        interviewQuestionRepository.save(interviewQuestion);
    }

    @Transactional
    public void saveUserAnswerForStressInterview(InterviewQuestion interviewQuestion, String userAnswer) {

        // Khóa chặn nếu không phải trạng thái đang trả lời
        if (interviewQuestion.getStatus() != InterviewQuestionStatus.ANSWERING) {
            throw new AppException(ErrorCode.QUESTION_NOT_ACTIVE);
        }

        if (interviewQuestion.getEndAt() != null && interviewQuestion.getEndAt().isBefore(LocalDateTime.now())) {
            userAnswer = null;
        }

        String questionContent = interviewQuestion.getQuestion().getContent();
        String suggestedAnswer = interviewQuestion.getQuestion().getSuggestedAnswer();
        PromptVersion promptVersion = interviewQuestion.getPromptVersion();

        interviewQuestion.setUserAnswer(userAnswer);
        interviewQuestion.setCompleteAt(LocalDateTime.now());
        interviewQuestion.setStatus(InterviewQuestionStatus.ANSWERED);

        interviewQuestion = interviewQuestionRepository.save(interviewQuestion);

        eventPublisher.publishEvent(new AiEvaluationEvent(
                interviewQuestion.getId(),
                questionContent,
                userAnswer,
                promptVersion.getPromptContent(),
                promptVersion.getModel(),
                suggestedAnswer
        ));

    }

    @Transactional
    public void completeQuestion (InterviewQuestion interviewQuestion, String feedback, Float point){
        AIFeedback aiFeedback = new AIFeedback(point, feedback);

        interviewQuestion.setAiFeedback(aiFeedback);
        interviewQuestionRepository.save(interviewQuestion);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean lockQuestionForProcessing(long id) {
        int updatedRows = interviewQuestionRepository.updateStatusToProcessing(
                id,
                InterviewQuestionStatus.PROCESSING,
                InterviewQuestionStatus.ANSWERING
        );

        return updatedRows > 0;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void unlockQuestion(long id) {
        interviewQuestionRepository.updateStatusToAnswering(
                id,
                InterviewQuestionStatus.ANSWERING
        );
    }
}

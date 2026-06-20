package com.example.it_iap.service.impl;

import com.example.it_iap.dto.ai.response.AIInteractive;
import com.example.it_iap.dto.chatMessage.response.ChatMessageResponse;
import com.example.it_iap.dto.interview.FeedbackForQuestion;
import com.example.it_iap.dto.interview.response.GetFeedbackResponse;
import com.example.it_iap.dto.interview.response.InterviewIdResponse;
import com.example.it_iap.dto.question.response.CurrentQuestionResponse;
import com.example.it_iap.entity.*;
import com.example.it_iap.entity.Json.OverallResult;
import com.example.it_iap.entity.enums.InterviewMode;
import com.example.it_iap.entity.enums.InterviewQuestionStatus;
import com.example.it_iap.entity.enums.InterviewStatus;
import com.example.it_iap.entity.enums.PromptUseCase;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.InterviewRepository;
import com.example.it_iap.service.*;
import com.example.it_iap.util.SecurityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewServiceImpl implements InterviewService {
    private final InterviewRepository interviewRepository;
    private final PromptVersionService promptVersionService;
    private final QuestionService questionService;
    private final InterviewQuestionService interviewQuestionService;
    private final ProfileService profileService;
    private final AIService aiService;
    private final ChatMessageService chatMessageService;
    private final ObjectMapper objectMapper;

    @Transactional
    public InterviewIdResponse createInterview (String mode, String title, long profileId) {
        Profile profile = profileService.getValidProfileAndCheckAccess(profileId);
        PromptVersion promptVersion = promptVersionService.getPromptActive(PromptUseCase.GENERAL_FEEDBACK);
        InterviewMode interviewMode = InterviewMode.from(mode);

        Interview interview = new Interview();
        interview.setTitle(title);
        interview.setMode(interviewMode);
        interview.setStatus(InterviewStatus.PENDING);
        interview.setPromptVersion(promptVersion);
        interview.setProfile(profile);

        interview = interviewRepository.save(interview);
        List<Question> questions =
                questionService.getRandomInterviewQuestions(profile.getTargetLevel(), profile.getTargetPosition());

        List<InterviewQuestion> interviewQuestions =
                interviewQuestionService.createInterviewQuestion(questions, interview);

        return new InterviewIdResponse(interview.getId());
    }

    @Transactional
    public CurrentQuestionResponse startInterview (long interviewId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Interview interview = interviewRepository.findByIdAndProfile_UserId(interviewId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_NOT_FOUND));

        if(interview.getStatus() != InterviewStatus.PENDING){
            throw new AppException(ErrorCode.INTERVIEW_STARTED);
        }

        interview.setStatus(InterviewStatus.IN_PROGRESS);
        interview = interviewRepository.save(interview);
        InterviewMode interviewMode = interview.getMode();

        InterviewQuestion interviewQuestion = interviewQuestionService
                        .activateNextUnansweredQuestion(interviewId, interviewMode);
        if (interviewQuestion == null) {
            throw new AppException(ErrorCode.QUESTION_INTERVIEW_NOT_FOUND);
        }
        Question question = interviewQuestion.getQuestion();
        boolean hasNextQuestion = interviewQuestionService.hasNextQuestion(interviewId, interviewQuestion.getOrderIndex());

        return new CurrentQuestionResponse(
                interviewQuestion.getId(),
                question.getContent(),
                question.getCategory().getDisplayName(),
                interviewQuestion.getEndAt(),
                hasNextQuestion,
                interviewMode
        );
    }

    @Transactional
    public CurrentQuestionResponse submitAnswerForStressInterview (long interviewQuestionId, String userAnswer){
        InterviewQuestion answeredIq = interviewQuestionService.findValidQuestionForUser(interviewQuestionId);
        Interview interview = answeredIq.getInterview();
        InterviewMode interviewMode = interview.getMode();

        if (interviewMode != InterviewMode.STRESS_INTERVIEW) {
            throw new AppException(ErrorCode.INCOMPATIBLE_INTERVIEW_TYPE);
        }

        interviewQuestionService.saveUserAnswerForStressInterview(answeredIq, userAnswer);

        InterviewQuestion interviewQuestion = interviewQuestionService
                .activateNextUnansweredQuestion(interview.getId(), interviewMode);

        if (interviewQuestion == null) {
            interview.setStatus(InterviewStatus.COMPLETED);
            interview.setCompletedAt(LocalDateTime.now());

            interviewRepository.save(interview);
            return null;
        }

        Question question = interviewQuestion.getQuestion();
        boolean hasNextQuestion = interviewQuestionService.hasNextQuestion(interview.getId(), interviewQuestion.getOrderIndex());

        return new CurrentQuestionResponse(
                interviewQuestion.getId(),
                question.getContent(),
                question.getCategory().getDisplayName(),
                interviewQuestion.getEndAt(),
                hasNextQuestion,
                interviewMode
        );
    }

    public AIInteractive answerForInteractiveInterview (long interviewQuestionId, String userAnswer) {
        InterviewQuestion interviewQuestion = interviewQuestionService.findValidQuestionForUser(interviewQuestionId);

        if (interviewQuestion.getInterview().getMode() != InterviewMode.INTERACTIVE_INTERVIEW) {
            throw new AppException(ErrorCode.INCOMPATIBLE_INTERVIEW_TYPE);
        }

        if (interviewQuestion.getStatus() != InterviewQuestionStatus.ANSWERING){
            throw new AppException(ErrorCode.QUESTION_NOT_ACTIVE);
        }

        if (interviewQuestion.getAiFeedback() != null){
            throw new AppException(ErrorCode.QUESTION_ALREADY_COMPLETED);
        }

        if (interviewQuestion.getEndAt() != null && interviewQuestion.getEndAt().isBefore(LocalDateTime.now())) {
            userAnswer = "Đã hết giờ, hãy kết thúc câu hỏi ở đây và đưa ra nhận xét";
        }

        boolean isLocked = interviewQuestionService.lockQuestionForProcessing(interviewQuestionId);
        if (!isLocked) {
            throw new AppException(ErrorCode.AI_IS_RESPONDING);
        }

        try {
            AIInteractive aiInteractive = aiService.interactiveWithAi(interviewQuestion, userAnswer);

            if(aiInteractive.getIsComplete()){
                try {
                    interviewQuestionService.completeQuestion(
                            interviewQuestion,
                            aiInteractive.getContent(),
                            aiInteractive.getPoint()
                    );
                } catch (Exception e) {
                    log.error("Lưu Feedback thất bại, tiến hành xóa tin nhắn rác...", e);
                    chatMessageService.rollbackLatestMessages(interviewQuestion.getChatSession().getId());
                    throw e;
                }
            } else {
                interviewQuestionService.unlockQuestion(interviewQuestionId);
            }

            return aiInteractive;
        } catch (Exception e) {
            // Mở khóa để user không bị kẹt câu hỏi mãi mãi
            interviewQuestionService.unlockQuestion(interviewQuestionId);

            log.error("Lỗi khi gọi AI, tiến hành nhả khóa câu hỏi...", e);
            throw new AppException(ErrorCode.SYSTEM_ERROR);
        }
    }

    @Transactional
    public CurrentQuestionResponse transitionToNextQuestionForInteractiveInterview (long interviewQuestionId){
        InterviewQuestion interviewQuestion = interviewQuestionService.findValidQuestionForUser(interviewQuestionId);
        Interview interview = interviewQuestion.getInterview();
        InterviewMode interviewMode = interview.getMode();

        if (interviewMode != InterviewMode.INTERACTIVE_INTERVIEW) {
            throw new AppException(ErrorCode.INCOMPATIBLE_INTERVIEW_TYPE);
        }

        if (interviewQuestion.getAiFeedback() == null){
            throw new AppException(ErrorCode.CURRENT_QUESTION_NOT_ANSWERED);
        }

        interviewQuestionService.completeInterviewQuestion(interviewQuestion);
        InterviewQuestion nextInterviewQuestion = interviewQuestionService.activateNextUnansweredQuestion(interview.getId(), interviewMode);

        if (nextInterviewQuestion == null) {
            interview.setStatus(InterviewStatus.COMPLETED);
            interview.setCompletedAt(LocalDateTime.now());

            interviewRepository.save(interview);
            return null;
        }

        Question question = nextInterviewQuestion.getQuestion();
        boolean hasNextQuestion = interviewQuestionService.hasNextQuestion(interview.getId(), nextInterviewQuestion.getOrderIndex());

        return new CurrentQuestionResponse(
                nextInterviewQuestion.getId(),
                question.getContent(),
                question.getCategory().getDisplayName(),
                nextInterviewQuestion.getEndAt(),
                hasNextQuestion,
                interviewMode
        );
    }

    public CurrentQuestionResponse getCurrentQuestion (long interviewId){
        UUID userId = SecurityUtils.getCurrentUserId();
        Interview interview = interviewRepository.findByIdAndProfile_UserId(interviewId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_NOT_FOUND));

        if(interview.getStatus() != InterviewStatus.IN_PROGRESS){
            throw new AppException(ErrorCode.INTERVIEW_NOT_IN_PROGRESS);
        }

        InterviewQuestion interviewQuestion = interviewQuestionService.getCurrentQuestion(interviewId);
        Question question = interviewQuestion.getQuestion();
        boolean hasNextQuestion = interviewQuestionService.hasNextQuestion(interview.getId(), interviewQuestion.getOrderIndex());

        return new CurrentQuestionResponse(
                interviewQuestion.getId(),
                question.getContent(),
                question.getCategory().getDisplayName(),
                interviewQuestion.getEndAt(),
                hasNextQuestion,
                interview.getMode()
        );
    }

    public GetFeedbackResponse getFeedback (long interviewId){
        UUID userId = SecurityUtils.getCurrentUserId();
        Interview interview = interviewRepository.findWithInterviewQuestionsAndQuestionByIdAndProfile_UserId(interviewId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.INTERVIEW_NOT_FOUND));

        if(interview.getStatus() != InterviewStatus.COMPLETED){
            throw new AppException(ErrorCode.INTERVIEW_NOT_COMPLETED);
        }

        boolean isProcessing = interview.getInterviewQuestions().stream()
                .anyMatch(iq -> iq.getAiFeedback() == null);


        List<FeedbackForQuestion> feedbackForQuestions = interview.getInterviewQuestions().stream()
                .map(interviewQuestion -> {
                    FeedbackForQuestion feedbackForQuestion = new FeedbackForQuestion();
                    feedbackForQuestion.setOrderIndex(interviewQuestion.getOrderIndex());
                    feedbackForQuestion.setUserAnswer(interviewQuestion.getUserAnswer());
                    feedbackForQuestion.setQuestionContent(interviewQuestion.getQuestion().getContent());
                    feedbackForQuestion.setQuestionType(interviewQuestion.getQuestion().getCategory());
                    feedbackForQuestion.setFeedback(interviewQuestion.getAiFeedback());

                    return feedbackForQuestion;
                }).toList();

        OverallResult overallResult = interview.getOverallResult();
        if(overallResult == null && !isProcessing){
            PromptVersion promptVersion = interview.getPromptVersion();
            overallResult = aiService.generateFeedback(feedbackForQuestions, promptVersion);

            interview.setOverallResult(overallResult);
            interviewRepository.save(interview);
        }

        return new GetFeedbackResponse(
                isProcessing,
                feedbackForQuestions,
                overallResult
        );
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getChatHistory (long interviewQuestionId){
        InterviewQuestion interviewQuestion = interviewQuestionService.findValidQuestionForUser(interviewQuestionId);

        if (interviewQuestion.getInterview().getMode() != InterviewMode.INTERACTIVE_INTERVIEW) {
            throw new AppException(ErrorCode.INCOMPATIBLE_INTERVIEW_TYPE);
        }

        List<ChatMessage> chatMessages = interviewQuestion.getChatSession().getChatMessages();

        return chatMessages.stream()
                .sorted(Comparator.comparing(ChatMessage::getId))
                .map(msg -> {
                    String finalContent = msg.getContent();

                    if (msg.getRole() == MessageType.ASSISTANT) {
                        finalContent = extractAiContent(msg.getContent());
                    }

                    return new ChatMessageResponse(msg.getRole(), finalContent);
                })
                .toList();
    }

    private String extractAiContent(String rawJson) {
        try {
            AIInteractive aiData = objectMapper.readValue(rawJson, AIInteractive.class);
            return aiData.getContent();

        } catch (JsonProcessingException e) {
            log.error("Không thể bóc tách JSON từ tin nhắn AI. Dữ liệu gốc: {}", rawJson);
            return rawJson;
        }
    }
}

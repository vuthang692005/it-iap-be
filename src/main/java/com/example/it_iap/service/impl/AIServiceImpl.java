package com.example.it_iap.service.impl;

import com.example.it_iap.AI.TokenUsageAdvisor;
import com.example.it_iap.dto.ai.AiEvaluationEvent;
import com.example.it_iap.dto.ai.response.AIInteractive;
import com.example.it_iap.dto.interview.FeedbackForQuestion;
import com.example.it_iap.dto.question.request.AICreateQuestionRequest;
import com.example.it_iap.entity.ChatSession;
import com.example.it_iap.entity.InterviewQuestion;
import com.example.it_iap.entity.Json.AIFeedback;
import com.example.it_iap.entity.Json.OverallResult;
import com.example.it_iap.entity.PromptVersion;
import com.example.it_iap.entity.Question;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.InterviewQuestionRepository;
import com.example.it_iap.service.AIService;
import com.example.it_iap.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIServiceImpl implements AIService {
    @Qualifier("memoryChatClient")
    private final ChatClient memoryChatClient;

    @Qualifier("statelessChatClient")
    private final ChatClient statelessChatClient;

    private final InterviewQuestionRepository interviewQuestionRepository;
    private final ChatSessionService chatSessionService;

    public List<AICreateQuestionRequest> generateQuestion (int quantity, TargetLevel level, TargetPosition position, PromptVersion promptVersion){
        String systemPromptTemplate = promptVersion.getPromptContent();

        return statelessChatClient
                .prompt()
                .system(systemPromptTemplate)
                .user(u -> u.text("Hãy tạo {quantity} câu hỏi phỏng vấn cho vị trí {position} ở cấp độ {level}.")
                        .param("quantity", quantity)
                        .param("position", position.getName())
                        .param("level", level.getName()))
                .options(OpenAiChatOptions.builder()
                        .model(promptVersion.getModel()))
                .call()
                .entity(new ParameterizedTypeReference<List<AICreateQuestionRequest>>() {});
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT)
    public void generateFeedbackForQuestion (AiEvaluationEvent event){
        AIFeedback aiFeedback = null;

        if (event.userAnswer() == null || event.userAnswer().trim().isEmpty()) {
            aiFeedback = new AIFeedback(
                    0f, 0f, 0f,
                    "Ứng viên không đưa ra câu trả lời hoặc đã hết thời gian làm bài."
            );
        }else {
            try {
                String rulesFromDB = event.promptContent();

                String guaranteedSystemPrompt = rulesFromDB +
                        "\n\n--- DỮ LIỆU THAM CHIẾU ---" +
                        "\nCâu hỏi phỏng vấn: " + (event.questionContent() != null ? event.questionContent() : "Không có") +
                        "\nĐáp án mẫu / Tiêu chí: " + (event.suggestedAnswer() != null ? event.suggestedAnswer() : "Không có");

                aiFeedback = statelessChatClient
                        .prompt()
                        .system(guaranteedSystemPrompt)
                        .user(event.userAnswer())
                        .options(OpenAiChatOptions.builder()
                                .model(event.model()))
                        .call()
                        .entity(AIFeedback.class);
            }catch (Exception e){
                log.error("Lỗi khi gọi AI cho InterviewQuestionId {}: {}", event.interviewQuestionId(), e.getMessage());

                aiFeedback = new AIFeedback(
                        null, null, null,
                        "Hệ thống lỗi điểm câu này sẽ không được tính."
                );
            }
        }

        AIFeedback finalAiFeedback = aiFeedback;
        interviewQuestionRepository.findById(event.interviewQuestionId()).ifPresent(iq -> {
            iq.setAiFeedback(finalAiFeedback);
            interviewQuestionRepository.save(iq);
        });
    }

    public OverallResult generateFeedback (List<FeedbackForQuestion> feedbackForQuestions, PromptVersion promptVersion) {
        try {
            // 1. Chuyển đổi List dữ liệu thành một chuỗi String dễ đọc cho AI
            String guaranteedSystemPrompt = getString(feedbackForQuestions, promptVersion);

            // 2. Gọi AI và ép kiểu về OverallResult
            return statelessChatClient
                    .prompt()
                    .system(guaranteedSystemPrompt)
                    .user("Dựa vào dữ liệu phỏng vấn bên trên, hãy tổng hợp kết quả, tính điểm trung bình và trả về định dạng JSON theo yêu cầu.")
                    .options(OpenAiChatOptions.builder()
                            .model(promptVersion.getModel()))
                    .call()
                    .entity(OverallResult.class);

        } catch (Exception e) {
            log.error("Lỗi khi gọi AI tổng kết phỏng vấn: {}", e.getMessage());
            throw new AppException(ErrorCode.SYSTEM_ERROR);
        }
    }

    private static @NonNull String getString(List<FeedbackForQuestion> feedbackForQuestions, PromptVersion promptVersion) {
        StringBuilder interviewData = new StringBuilder();
        for (int i = 0; i < feedbackForQuestions.size(); i++) {
            FeedbackForQuestion fq = feedbackForQuestions.get(i);
            interviewData.append(String.format("Câu %d: %s\n", i + 1, fq.getQuestionContent() != null ? fq.getQuestionContent() : "Không có"));
            interviewData.append(String.format("- Phân loại: %s\n", fq.getQuestionType() != null ? fq.getQuestionType().getDisplayName() : "Không rõ"));

            if (fq.getFeedback() != null) {
                // Lấy 3 đầu điểm (xử lý an toàn với null)
                String pointStr = fq.getFeedback().getPoint() != null ? String.valueOf(fq.getFeedback().getPoint()) : "N/A";
                String articulationPointStr = fq.getFeedback().getArticulationPoint() != null ? String.valueOf(fq.getFeedback().getArticulationPoint()) : "N/A";
                String focusPointStr = fq.getFeedback().getFocusPoint() != null ? String.valueOf(fq.getFeedback().getFocusPoint()) : "N/A";

                // Truyền cả 3 đầu điểm vào Prompt
                interviewData.append(String.format("- Điểm chuyên môn (point): %s\n", pointStr));
                interviewData.append(String.format("- Điểm tư duy trình bày (articulationPoint): %s\n", articulationPointStr));
                interviewData.append(String.format("- Điểm bám sát trọng tâm (focusPoint): %s\n", focusPointStr));

                // Xử lý field nhận xét (feedback/content tùy cách bạn đặt tên biến trong code thực tế)
                String feedbackContent = fq.getFeedback().getFeedback() != null ? fq.getFeedback().getFeedback() : "Không có nhận xét";
                interviewData.append(String.format("- Nhận xét chi tiết (feedback): %s\n", feedbackContent));
            } else {
                interviewData.append("- Điểm chuyên môn: N/A\n");
                interviewData.append("- Điểm tư duy trình bày: N/A\n");
                interviewData.append("- Điểm bám sát trọng tâm: N/A\n");
                interviewData.append("- Nhận xét chi tiết: Chưa có dữ liệu.\n");
            }

            interviewData.append("\n"); // Cách dòng giữa các câu
        }

        // 3. Chuẩn bị System Prompt
        String rulesFromDB = promptVersion.getPromptContent();
        return rulesFromDB +
                "\n\n--- DỮ LIỆU PHỎNG VẤN CỦA ỨNG VIÊN ---\n" +
                interviewData.toString();
    }

    public AIInteractive interactiveWithAi (InterviewQuestion interviewQuestion, String userAnswer){

        Question question = interviewQuestion.getQuestion();
        PromptVersion promptVersion = interviewQuestion.getPromptVersion();

        ChatSession chatSession = interviewQuestion.getChatSession();

        if (chatSession == null){
            throw new AppException(ErrorCode.QUESTION_NOT_READY);
        }

        try {
            String guaranteedSystemPrompt = promptVersion.getPromptContent() +
                    "\n\n--- DỮ LIỆU THAM CHIẾU ---" +
                    "\nCâu hỏi phỏng vấn: " + (question.getContent() != null ? question.getContent() : "Không có") +
                    "\nĐáp án mẫu / Tiêu chí: " + (question.getSuggestedAnswer() != null ? question.getSuggestedAnswer() : "Không có");

            return memoryChatClient
                    .prompt()
                    .system(guaranteedSystemPrompt)
                    .user(userAnswer)
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatSession.getId()))
                    .advisors(new TokenUsageAdvisor(chatSessionService, chatSession))
                    .options(OpenAiChatOptions.builder()
                            .model(promptVersion.getModel()))
                    .call()
                    .entity(AIInteractive.class);
        }catch (Exception e){
            throw new AppException(ErrorCode.SYSTEM_ERROR);
        }
    }

}

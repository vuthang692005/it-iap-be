package com.example.it_iap.service.impl;

import com.example.it_iap.dto.question.request.AICreateQuestionRequest;
import com.example.it_iap.entity.PromptVersion;
import com.example.it_iap.entity.Question;
import com.example.it_iap.entity.enums.PromptUseCase;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;
import com.example.it_iap.service.AIService;
import com.example.it_iap.service.PromptVersionService;
import com.example.it_iap.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {
    private final PromptVersionService promptVersionService;
    private final QuestionService questionService;
    @Qualifier("memoryChatClient")
    private final ChatClient memoryChatClient;
    @Qualifier("statelessChatClient")
    private final ChatClient statelessChatClient;
    private final ChatMemory chatMemory;

    public List<Question> generateQuestion (int quantity, TargetLevel level, TargetPosition position){
        PromptVersion promptVersion = promptVersionService.getPromptActive(PromptUseCase.QUESTION_GENERATOR);
        String systemPromptTemplate = promptVersion.getPromptContent();

        List<AICreateQuestionRequest> requests = statelessChatClient
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

        return questionService.aiGenerateQuestion(requests, level, position, promptVersion);
    }
}

package com.example.it_iap.service.impl;

import com.example.it_iap.AI.TokenUsageAdvisor;
import com.example.it_iap.dto.chatbot.request.ChatbotRequest;
import com.example.it_iap.dto.chatbot.response.ChatbotResponse;
import com.example.it_iap.entity.ChatSession;
import com.example.it_iap.service.ChatSessionService;
import com.example.it_iap.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {
    @Qualifier("memoryChatClient")
    private final ChatClient memoryChatClient;

    private final ChatSessionService chatSessionService;

    public ChatbotResponse chatbot (ChatbotRequest request){
        ChatSession chatSession = chatSessionService.getChatSession(request.getSessionId());
        String systemPromptTemplate = chatSession.getPromptVersion().getPromptContent();

        String aiResponse = memoryChatClient
                .prompt()
                .system(systemPromptTemplate)
                .user(request.getUserMessage())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatSession.getId()))
                .advisors(new TokenUsageAdvisor(chatSessionService, chatSession))
                .options(OpenAiChatOptions.builder()
                        .model(chatSession.getPromptVersion().getModel()))
                .call()
                .content();

        return new ChatbotResponse(aiResponse);
    }
}

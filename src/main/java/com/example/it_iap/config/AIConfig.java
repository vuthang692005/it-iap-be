package com.example.it_iap.config;

import com.example.it_iap.AI.WindowChatMemory;
import com.example.it_iap.repository.ChatMessageRepository;
import com.example.it_iap.repository.ChatSessionRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {
    @Bean
    public ChatMemory chatMemory(ChatMessageRepository chatMessageRepository, ChatSessionRepository chatSessionRepository) {
        return WindowChatMemory.builder()
                .chatMessageRepository(chatMessageRepository)
                .chatSessionRepository(chatSessionRepository)
                .maxMessages(20)
                .build();
    }

    @Bean
    ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}

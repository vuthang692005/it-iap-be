package com.example.it_iap.AI;

import com.example.it_iap.entity.ChatMessage;
import com.example.it_iap.entity.ChatSession;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.ChatMessageRepository;
import com.example.it_iap.repository.ChatSessionRepository;
import lombok.Builder;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public class WindowChatMemory implements ChatMemory {
    @Builder.Default
    private final int maxMessages = 20;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;

    @Override
    public void add(String conversationId, List<Message> messages) {
        long id = Long.parseLong(conversationId);
        ChatSession chatSession = chatSessionRepository.findByIdAndDeleteAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_SESSION_NOT_FOUND));

        List<ChatMessage> chatMessages = new ArrayList<>();
        messages.forEach(message -> {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setChatSession(chatSession);
            chatMessage.setRole(message.getMessageType());
            chatMessage.setContent(message.getText());
            chatMessages.add(chatMessage);
        });

        chatMessageRepository.saveAll(chatMessages);
    }

    @Override
    public List<Message> get(String conversationId) {
        long id = Long.parseLong(conversationId);
        Pageable limit = PageRequest.of(0, maxMessages);

        List<ChatMessage> latestMessages = chatMessageRepository.findLatestMessages(id, limit);

        if (latestMessages.isEmpty()) {
            return List.of();
        }

        List<ChatMessage> modifiableList = new ArrayList<>(latestMessages);
        Collections.reverse(modifiableList);

        return modifiableList.stream()
                .map(chatMsg -> {
                    String content = chatMsg.getContent();

                    return switch (chatMsg.getRole()) {
                        case USER -> new UserMessage(content);
                        case ASSISTANT -> new AssistantMessage(content);
                        case SYSTEM -> new SystemMessage(content);
                        default -> new UserMessage(content);
                    };
                })
                .collect(Collectors.toList());
    }

    @Override
    public void clear(String conversationId) {
        long id = Long.parseLong(conversationId);
        ChatSession chatSession = chatSessionRepository.findByIdAndDeleteAtIsNull(id)
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_SESSION_NOT_FOUND));

        chatSession.setDeleteAt(LocalDateTime.now());
        chatSessionRepository.save(chatSession);
    }
}

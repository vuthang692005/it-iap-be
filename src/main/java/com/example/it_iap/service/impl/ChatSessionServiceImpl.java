package com.example.it_iap.service.impl;

import com.example.it_iap.dto.chatMessage.response.ChatMessageResponse;
import com.example.it_iap.dto.chatSession.request.ChatSessionRequest;
import com.example.it_iap.dto.chatSession.respone.ChatSessionResponse;
import com.example.it_iap.entity.*;
import com.example.it_iap.entity.enums.PromptUseCase;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.ChatSessionRepository;
import com.example.it_iap.service.ChatSessionService;
import com.example.it_iap.service.PromptVersionService;
import com.example.it_iap.service.UserService;
import com.example.it_iap.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSessionServiceImpl implements ChatSessionService {
    private final ChatSessionRepository chatSessionRepository;
    private final UserService userService;
    private final PromptVersionService promptVersionService;

    public void createInterviewSession (InterviewQuestion interviewQuestion){
        User user = userService.getCurrentUser();

        ChatSession chatSession = new ChatSession();
        chatSession.setUser(user);
        chatSession.setInterviewQuestion(interviewQuestion);
        chatSession.setPromptVersion(interviewQuestion.getPromptVersion());
        chatSession.setSessionLimitTokens(null);

        chatSessionRepository.save(chatSession);
    }

    public void updateTotalTokenUsed (int tokensToAdd, ChatSession chatSession){
        if(chatSession.getSessionLimitTokens() != null &&
                chatSession.getTotalTokensUsed() >= chatSession.getSessionLimitTokens()){
            throw new AppException(ErrorCode.TOKEN_LIMIT_EXCEEDED);
        }
        chatSession.setTotalTokensUsed(chatSession.getTotalTokensUsed() + tokensToAdd);
        chatSessionRepository.save(chatSession);
    }

    public ChatSession getChatSession (long chatSessionId){
        UUID userId = SecurityUtils.getCurrentUserId();
        return chatSessionRepository
                .findByIdAndUserIdAndDeleteAtIsNullAndInterviewQuestionIsNull(chatSessionId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_SESSION_NOT_FOUND));
    }

    public List<ChatSessionResponse> getChatbotSession (){
        UUID userId = SecurityUtils.getCurrentUserId();

        List<ChatSession> chatSessions = chatSessionRepository
                .findAllByUserIdAndDeleteAtIsNullAndInterviewQuestionIsNull(userId);

        return chatSessions.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public ChatSessionResponse createChatSession (ChatSessionRequest request){
        User user = userService.getCurrentUser();
        PromptVersion promptVersion = promptVersionService.getPromptActive(PromptUseCase.CUSTOMER_SUPPORT);

        ChatSession chatSession = new ChatSession();
        chatSession.setTitle(request.getTitle());
        chatSession.setUser(user);
        chatSession.setPromptVersion(promptVersion);

        chatSession = chatSessionRepository.save(chatSession);

        return mapToResponse(chatSession);
    }

    @Transactional
    public void deleteChatSession (long chatSessionId){
        ChatSession chatSession = getChatSession(chatSessionId);
        chatSession.setDeleteAt(LocalDateTime.now());
        chatSessionRepository.save(chatSession);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getChatMessage (long chatSessionId){
        ChatSession chatSession = getChatSession(chatSessionId);
        List<ChatMessage> chatMessage = chatSession.getChatMessages();

        return chatMessage.stream().map(mess ->
                new ChatMessageResponse(
                        mess.getRole(),
                        mess.getContent()))
                .toList();
    }

    private ChatSessionResponse mapToResponse(ChatSession chatSession) {
        return new ChatSessionResponse(
                chatSession.getId(),
                chatSession.getTitle()
        );
    }
}

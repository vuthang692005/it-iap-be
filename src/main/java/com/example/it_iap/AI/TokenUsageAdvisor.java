package com.example.it_iap.AI;

import com.example.it_iap.entity.ChatSession;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.model.ChatResponse;

@RequiredArgsConstructor
public class TokenUsageAdvisor implements CallAdvisor {
    private final ChatSessionService chatSessionService;
    private final ChatSession chatSession;

    @Override
    public @NonNull ChatClientResponse adviseCall(@NonNull ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        if(chatSession.getSessionLimitTokens() != null &&
                chatSession.getTotalTokensUsed() >= chatSession.getSessionLimitTokens()){
            throw new AppException(ErrorCode.TOKEN_LIMIT_EXCEEDED);
        }

        ChatClientResponse clientResponse = callAdvisorChain.nextCall(chatClientRequest);

        ChatResponse chatResponse = clientResponse.chatResponse(); // Tùy version mà có thể là getChatResponse() nhé

        if (chatResponse != null) {
            int tokenUsed = chatResponse.getMetadata().getUsage().getTotalTokens();

            chatSessionService.updateTotalTokenUsed(tokenUsed, chatSession);
        }

        return clientResponse;
    }

    @Override
    public @NonNull String getName() {
        return "TokenUsageAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

package com.example.it_iap.service;

import com.example.it_iap.dto.chatSession.request.ChatSessionRequest;
import com.example.it_iap.dto.chatSession.respone.ChatSessionResponse;
import com.example.it_iap.entity.ChatSession;
import com.example.it_iap.entity.InterviewQuestion;

import java.util.List;

public interface ChatSessionService {
    void createInterviewSession (InterviewQuestion interviewQuestion);
    void updateTotalTokenUsed (int tokensToAdd, ChatSession chatSession);
    ChatSession getChatSession (long chatSessionId);
    ChatSessionResponse createChatSession (ChatSessionRequest request);
    void deleteChatSession (long chatSessionId);
    List<ChatSessionResponse> getChatbotSession ();
}

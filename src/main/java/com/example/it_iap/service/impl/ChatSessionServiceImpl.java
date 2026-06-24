package com.example.it_iap.service.impl;

import com.example.it_iap.entity.ChatMessage;
import com.example.it_iap.entity.ChatSession;
import com.example.it_iap.entity.InterviewQuestion;
import com.example.it_iap.entity.User;
import com.example.it_iap.repository.ChatSessionRepository;
import com.example.it_iap.service.ChatSessionService;
import com.example.it_iap.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSessionServiceImpl implements ChatSessionService {
    private final ChatSessionRepository chatSessionRepository;
    private final UserService userService;

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
        chatSession.setTotalTokensUsed(chatSession.getTotalTokensUsed() + tokensToAdd);
        chatSessionRepository.save(chatSession);
    }

}

package com.example.it_iap.service.impl;

import com.example.it_iap.entity.ChatSession;
import com.example.it_iap.entity.InterviewQuestion;
import com.example.it_iap.entity.PromptVersion;
import com.example.it_iap.entity.User;
import com.example.it_iap.repository.ChatSessionRepository;
import com.example.it_iap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatSessionServiceImpl {
    private final ChatSessionRepository chatSessionRepository;
    private final UserService userService;

    public ChatSession createInterviewSession (
            InterviewQuestion interviewQuestion,
            PromptVersion promptVersion){
        User user = userService.getCurrentUser();

        ChatSession chatSession = new ChatSession();
        chatSession.setUser(user);
        chatSession.setInterviewQuestion(interviewQuestion);
        chatSession.setPromptVersion(promptVersion);

        return chatSessionRepository.save(chatSession);
    }


}

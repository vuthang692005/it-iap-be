package com.example.it_iap.service;

import com.example.it_iap.entity.ChatSession;
import com.example.it_iap.entity.InterviewQuestion;

public interface ChatSessionService {
    void createInterviewSession (InterviewQuestion interviewQuestion);
    void updateTotalTokenUsed (int tokensToAdd, ChatSession chatSession);
}

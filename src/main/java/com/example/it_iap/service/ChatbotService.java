package com.example.it_iap.service;

import com.example.it_iap.dto.chatbot.request.ChatbotRequest;
import com.example.it_iap.dto.chatbot.response.ChatbotResponse;

public interface ChatbotService {
    ChatbotResponse chatbot (ChatbotRequest request);
}

package com.example.it_iap.dto.chatbot.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChatbotRequest {
    private long sessionId;

    @NotBlank(message = "USER_MESSAGE_INVALID")
    private String userMessage;
}

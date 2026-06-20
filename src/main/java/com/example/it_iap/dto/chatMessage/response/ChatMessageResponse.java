package com.example.it_iap.dto.chatMessage.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.ai.chat.messages.MessageType;

@AllArgsConstructor
@Getter
public class ChatMessageResponse {
    private MessageType role;
    private String content;
}

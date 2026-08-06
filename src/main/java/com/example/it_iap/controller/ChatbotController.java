package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.chatbot.request.ChatbotRequest;
import com.example.it_iap.dto.chatbot.response.ChatbotResponse;
import com.example.it_iap.service.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
    private final ChatbotService chatbotService;

    @Operation(summary = "Gửi tin nhắn cho Chatbot", description = "Gửi câu hỏi/tin nhắn của người dùng vào một phiên chat (session) cụ thể và nhận câu trả lời từ AI")
    @PostMapping()
    public ResponseEntity<ApiResponse<ChatbotResponse>> chatWithBot(@RequestBody @Valid ChatbotRequest request) {
        ChatbotResponse botResponse = chatbotService.chatbot(request);

        return ResponseEntity.ok(
                ApiResponse.<ChatbotResponse>builder()
                        .data(botResponse)
                        .build());
    }
}

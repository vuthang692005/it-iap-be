package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.chatMessage.response.ChatMessageResponse;
import com.example.it_iap.dto.chatSession.request.ChatSessionRequest;
import com.example.it_iap.dto.chatSession.respone.ChatSessionResponse;
import com.example.it_iap.service.ChatSessionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chatSessions")
@RequiredArgsConstructor
public class ChatSessionController {
    private final ChatSessionService chatSessionService;

    @Operation(summary = "Tạo phiên chat mới", description = "Khởi tạo một phiên trò chuyện (chat session) mới")
    @PostMapping
    public ResponseEntity<ApiResponse<ChatSessionResponse>> createChatSession(
            @RequestBody @Valid ChatSessionRequest request) {

        ChatSessionResponse response = chatSessionService.createChatSession(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ChatSessionResponse>builder()
                        .code(201)
                        .data(response)
                        .build());
    }

    @Operation(summary = "Xóa phiên chat", description = "Xóa một phiên trò chuyện dựa vào ID")
    @DeleteMapping("/{chatSessionId}")
    public ResponseEntity<ApiResponse<Void>> deleteChatSession(
            @PathVariable long chatSessionId) {

        chatSessionService.deleteChatSession(chatSessionId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .build());
    }

    @Operation(summary = "Lấy danh sách phiên chat", description = "Lấy danh sách lịch sử tất cả các phiên chat của người dùng")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatSessionResponse>>> getChatbotSession() {

        List<ChatSessionResponse> response = chatSessionService.getChatbotSession();

        return ResponseEntity.ok(
                ApiResponse.<List<ChatSessionResponse>>builder()
                        .data(response)
                        .build());
    }

    @Operation(summary = "Lấy danh sách tin nhắn", description = "Lấy toàn bộ lịch sử tin nhắn của một phiên chat cụ thể dựa vào ID")
    @GetMapping("/{chatSessionId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getChatMessage(
            @PathVariable("chatSessionId") long chatSessionId) {

        List<ChatMessageResponse> response = chatSessionService.getChatMessage(chatSessionId);

        return ResponseEntity.ok(
                ApiResponse.<List<ChatMessageResponse>>builder()
                        .data(response)
                        .build());
    }

}

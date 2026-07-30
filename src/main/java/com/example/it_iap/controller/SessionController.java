package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.session.response.UserSessionResponse;
import com.example.it_iap.entity.User;
import com.example.it_iap.service.SessionService;
import com.example.it_iap.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Tag(name = "Session Management", description = "Quản lý phiên đăng nhập thiết bị người dùng")
public class SessionController {
    private final SessionService sessionService;
    private final UserService userService;

    @Operation(summary = "Lấy danh sách các phiên đăng nhập", description = "Hiển thị tất cả các thiết bị đang đăng nhập tài khoản hiện tại")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserSessionResponse>>> getActiveSessions(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.getCurrentUser();
        String currentSessionId = jwt != null ? jwt.getClaimAsString("sid") : null;

        List<UserSessionResponse> sessions = sessionService.getActiveSessions(user, currentSessionId);
        return ResponseEntity.ok(ApiResponse.<List<UserSessionResponse>>builder()
                .data(sessions)
                .build());
    }

    @Operation(summary = "Đăng xuất một thiết bị cụ thể", description = "Đăng xuất và vô hiệu hóa phiên đăng nhập theo sessionId")
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<Void>> revokeSession(@PathVariable String sessionId) {
        User user = userService.getCurrentUser();
        sessionService.revokeSession(user, sessionId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Đăng xuất thiết bị thành công")
                .build());
    }

    @Operation(summary = "Đăng xuất tất cả thiết bị khác", description = "Giữ lại phiên đăng nhập hiện tại và đăng xuất tất cả thiết bị khác")
    @DeleteMapping("/other")
    public ResponseEntity<ApiResponse<Void>> revokeOtherSessions(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.getCurrentUser();
        String currentSessionId = jwt != null ? jwt.getClaimAsString("sid") : null;

        sessionService.revokeOtherSessions(user, currentSessionId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Đã đăng xuất khỏi tất cả các thiết bị khác")
                .build());
    }
}

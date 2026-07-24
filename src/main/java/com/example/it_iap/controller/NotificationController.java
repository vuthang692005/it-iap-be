package com.example.it_iap.controller;

import com.example.it_iap.dto.notification.request.AdminCreateNotificationRequest;
import com.example.it_iap.dto.notification.request.ReadNotificationRequest;
import com.example.it_iap.dto.notification.response.ReadNotificationResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.NotificationSliceResponse;
import com.example.it_iap.dto.notification.response.NotificationResponse;
import com.example.it_iap.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Lấy thông báo")
    @GetMapping
    public ResponseEntity<?> getNotification(
            @RequestParam(defaultValue = "1") @Min(1) int page) {
        NotificationSliceResponse<NotificationResponse> response = notificationService.getNotification(page);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<NotificationSliceResponse<NotificationResponse>>builder()
                        .data(response)
                        .build());
    }

    @Operation(summary = "Đọc thông báo")
    @PutMapping
    public ResponseEntity<?> readNotification(
            @RequestBody ReadNotificationRequest request) {
        ReadNotificationResponse response = notificationService.readNotification(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<ReadNotificationResponse>builder()
                        .data(response)
                        .build());
    }

    @Operation(summary = "Admin tạo thông báo cho toàn bộ người dùng") // Tạm thời gửi toàn bộ sau custom thêm
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createNotification(
            @RequestBody @Valid AdminCreateNotificationRequest request) {
        notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<NotificationResponse>builder()
                        .code(201)
                        .build());
    }


}

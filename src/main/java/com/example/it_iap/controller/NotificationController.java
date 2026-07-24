package com.example.it_iap.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}

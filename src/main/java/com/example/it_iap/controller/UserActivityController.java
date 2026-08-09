package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.userActivityLog.response.UserActivityLogResponse;
import com.example.it_iap.entity.enums.UserActionType;
import com.example.it_iap.service.UserActivityService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-activities")
@RequiredArgsConstructor
public class UserActivityController {
    private final UserActivityService userActivityService;

    @Operation(summary = "Xem lịch sử hoạt động tài khoản [USER]",
            description = "Trả về danh sách log hoạt động của user hiện tại, hỗ trợ phân trang và lọc theo loại hành động")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserActivityLogResponse>>> getMyActivityLogs(
            @RequestParam(required = false) UserActionType actionType,
            @RequestParam(defaultValue = "1") int page
    ) {
        Page<UserActivityLogResponse> response = userActivityService.getMyActivityLogs(actionType, page);

        return ResponseEntity.ok(
                ApiResponse.<Page<UserActivityLogResponse>>builder()
                        .data(response)
                        .build()
        );
    }
}

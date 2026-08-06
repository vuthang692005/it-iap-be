package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.dashboard.response.ProfileAnalyticsResponse;
import com.example.it_iap.dto.dashboard.response.UserProgressResponse;
import com.example.it_iap.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboards")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @Operation(
            summary = "Lấy thống kê chi tiết theo Profile",
            description = "Trả về tổng số bài phỏng vấn, điểm trung bình 5 kỹ năng và tỷ lệ cải thiện của một Profile cụ thể (Chủ sở hữu mới xem được)."
    )
    @GetMapping("/profiles/{profileId}")
    public ResponseEntity<ApiResponse<ProfileAnalyticsResponse>> getProfileStats(@PathVariable Long profileId) {
        ProfileAnalyticsResponse response = dashboardService.getProfileStats(profileId);

        return ResponseEntity.ok(
                ApiResponse.<ProfileAnalyticsResponse>builder()
                        .data(response)
                        .build()
        );
    }

    @Operation(
            summary = "Lấy tiến trình hoạt động của User",
            description = "Trả về chuỗi ngày học tập (Streak), danh hiệu (Rank) hiện tại và biểu đồ thống kê hàng ngày của User đang đăng nhập."
    )
    @GetMapping("/progress")
    public ResponseEntity<ApiResponse<UserProgressResponse>> getUserProgress() {
        UserProgressResponse response = dashboardService.getUserProgress();

        return ResponseEntity.ok(
                ApiResponse.<UserProgressResponse>builder()
                        .data(response)
                        .build()
        );
    }
}

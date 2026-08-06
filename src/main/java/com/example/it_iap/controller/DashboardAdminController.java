package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.adminActivityLog.response.AdminActivityLogResponse;
import com.example.it_iap.dto.dashboardAdmin.response.DashboardAdminResponse;
import com.example.it_iap.dto.dashboardAdmin.response.PositionDistributionResponse;
import com.example.it_iap.entity.enums.AdminActionType;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.enums.TimeFilter;
import com.example.it_iap.service.AdminActivityService;
import com.example.it_iap.service.DashboardAdminService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SCOPE_ADMIN')")
public class DashboardAdminController {
    private final DashboardAdminService dashboardAdminService;
    private final AdminActivityService adminActivityService;

    @Operation(summary = "Lấy dữ liệu tổng quan Dashboard [ADMIN]",
            description = "Trả về các thống kê tổng số (User, Lượt phỏng vấn, Doanh thu) và xu hướng phỏng vấn dựa theo bộ lọc thời gian")
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<DashboardAdminResponse>> getOverviewData(
            @RequestParam(defaultValue = "MONTH") TimeFilter timeFilter
    ) {
        DashboardAdminResponse response = dashboardAdminService.getOverviewData(timeFilter);
        return ResponseEntity.ok(
                ApiResponse.<DashboardAdminResponse>builder()
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Lấy phân bổ vị trí phỏng vấn [ADMIN]",
            description = "Biểu đồ phân bổ lượt phỏng vấn theo 4 vị trí, có hỗ trợ lọc theo Thời gian và Cấp bậc (Intern/Fresher)")
    @GetMapping("/positions")
    public ResponseEntity<ApiResponse<List<PositionDistributionResponse>>> getPositionDistribution(
            @RequestParam(defaultValue = "MONTH") TimeFilter timeFilter,
            @RequestParam(required = false) TargetLevel level
    ) {
        List<PositionDistributionResponse> response = dashboardAdminService.getPositionDistribution(timeFilter, level);

        return ResponseEntity.ok(
                ApiResponse.<List<PositionDistributionResponse>>builder()
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Lấy lịch sử hoạt động của Admin",
            description = "Trả về danh sách log có phân trang, hỗ trợ lọc theo loại hành động (actionType)")
    @GetMapping("/activities")
    public ResponseEntity<ApiResponse<Page<AdminActivityLogResponse>>> getActivityLogs(
            @RequestParam(required = false) AdminActionType actionType,
            @RequestParam(defaultValue = "0") int page
    ) {
        Page<AdminActivityLogResponse> response = adminActivityService.getActivityLogs(actionType, page);

        return ResponseEntity.ok(
                ApiResponse.<Page<AdminActivityLogResponse>>builder()
                        .data(response)
                        .build()
        );
    }
}

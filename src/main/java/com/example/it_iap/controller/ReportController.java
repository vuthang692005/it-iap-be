package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.report.request.CreateReportRequest;
import com.example.it_iap.dto.report.request.SearchReportRequest;
import com.example.it_iap.dto.report.request.UpdateReportRequest;
import com.example.it_iap.dto.report.request.UserSearchReportRequest;
import com.example.it_iap.dto.report.response.ReportResponse;
import com.example.it_iap.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "Tạo báo cáo mới", description = "Người dùng tạo một báo cáo mới")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createReport(@RequestBody @Valid CreateReportRequest request) {
        reportService.createReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Void>builder()
                        .code(201)
                        .build());
    }

    @Operation(summary = "Tìm kiếm báo cáo cá nhân [USER]", description = "Người dùng tìm kiếm các báo cáo liên quan đến bản thân")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<ReportResponse>>> userSearchReport(@ModelAttribute @Valid UserSearchReportRequest request) {
        Page<ReportResponse> response = reportService.userSearchReport(request);
        return ResponseEntity.ok(
                ApiResponse.<Page<ReportResponse>>builder()
                        .data(response)
                        .build());
    }

    @Operation(summary = "Tìm kiếm toàn bộ báo cáo [ADMIN]", description = "Quản trị viên tìm kiếm và lọc danh sách tất cả báo cáo")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReportResponse>>> searchReport(@ModelAttribute @Valid SearchReportRequest request) {
        Page<ReportResponse> response = reportService.searchReport(request);
        return ResponseEntity.ok(
                ApiResponse.<Page<ReportResponse>>builder()
                        .data(response)
                        .build());
    }

    @Operation(summary = "Cập nhật báo cáo [ADMIN]", description = "Cập nhật thông tin nội dung hoặc trạng thái của báo cáo theo ID")
    @PutMapping("/{reportId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<ReportResponse>> updateReport(
            @PathVariable long reportId,
            @RequestBody @Valid UpdateReportRequest request) {
        ReportResponse response = reportService.updateReport(reportId, request);
        return ResponseEntity.ok(
                ApiResponse.<ReportResponse>builder()
                        .data(response)
                        .build());
    }
}

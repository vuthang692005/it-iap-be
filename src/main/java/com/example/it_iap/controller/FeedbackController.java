package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.feedback.request.AdminReplyRequest;
import com.example.it_iap.dto.feedback.request.FeedbackFilterRequest;
import com.example.it_iap.dto.feedback.request.FeedbackRequest;
import com.example.it_iap.dto.feedback.response.FeedbackResponse;
import com.example.it_iap.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;

    @Operation(summary = "Tạo đánh giá mới", description = "Người dùng đang đăng nhập gửi đánh giá trang web")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FeedbackResponse>> createFeedback(@ModelAttribute @Valid FeedbackRequest request) {
        FeedbackResponse response = feedbackService.createFeedback(request);
        return ResponseEntity.ok(
                ApiResponse.<FeedbackResponse>builder()
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Lấy danh sách đánh giá", description = "Lấy danh sách đánh giá có phân trang, có thể lọc theo số sao (rating)")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<FeedbackResponse>>> getAllFeedbacks(@ModelAttribute @Valid FeedbackFilterRequest request) {
        Page<FeedbackResponse> response = feedbackService.getAllFeedbacks(request);
        return ResponseEntity.ok(
                ApiResponse.<Page<FeedbackResponse>>builder()
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Phản hồi đánh giá [ADMIN]", description = "Admin trả lời hoặc cập nhật câu trả lời cho một đánh giá")
    @PatchMapping("/{id}/reply")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<FeedbackResponse>> updateAdminReply(
            @PathVariable("id") Long feedbackId,
            @RequestBody @Valid AdminReplyRequest request) {
        FeedbackResponse response = feedbackService.updateAdminReply(feedbackId, request);
        return ResponseEntity.ok(
                ApiResponse.<FeedbackResponse>builder()
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Xóa đánh giá", description = "User chỉ có thể xóa đánh giá của chính mình. Admin có thể xóa bất kỳ đánh giá nào (Xóa mềm).")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFeedback(@PathVariable("id") Long feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .build()
        );
    }
}

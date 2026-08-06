package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.ai.response.AIInteractive;
import com.example.it_iap.dto.chatMessage.response.ChatMessageResponse;
import com.example.it_iap.dto.interview.request.CreateInterviewRequest;
import com.example.it_iap.dto.interview.request.GetInterviewHistoryRequest;
import com.example.it_iap.dto.interview.request.SubmitAnswerRequest;
import com.example.it_iap.dto.interview.response.GetFeedbackResponse;
import com.example.it_iap.dto.interview.response.GetHintResponse;
import com.example.it_iap.dto.interview.response.InterviewIdResponse;
import com.example.it_iap.dto.interview.response.InterviewResponse;
import com.example.it_iap.dto.question.response.CurrentQuestionResponse;
import com.example.it_iap.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interviews")
@RequiredArgsConstructor
public class InterviewController {
    private final InterviewService interviewService;

    @Operation(
            summary = "Tạo buổi phỏng vấn mới",
            description = "Khởi tạo dữ liệu, chọn danh sách câu hỏi dựa trên hồ sơ (Profile) và chế độ (PHỎNG VẤN TƯƠNG TÁC hoặc PHỎNG VẤN ÁP LỰC). Trả về Interview ID."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<InterviewIdResponse>> createInterview(
            @Valid @RequestBody CreateInterviewRequest request) {

        InterviewIdResponse response = interviewService.createInterview(
                request.getMode(),
                request.getTitle(),
                request.getProfileId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<InterviewIdResponse>builder()
                        .code(201)
                        .data(response)
                        .build());
    }

    @Operation(
            summary = "Bắt đầu phỏng vấn",
            description = "Chuyển trạng thái sang IN_PROGRESS và lấy câu hỏi đầu tiên để hiển thị cho ứng viên."
    )
    @PostMapping("/{interviewId}/start")
    public ResponseEntity<ApiResponse<CurrentQuestionResponse>> startInterview(
            @PathVariable long interviewId) {

        return ResponseEntity.ok(ApiResponse.<CurrentQuestionResponse>builder()
                .data(interviewService.startInterview(interviewId))
                .build());
    }

    @Operation(
            summary = "Nộp câu trả lời (STRESS_INTERVIEW - PHỎNG VẤN ÁP LỰC)",
            description = "Lưu câu trả lời của ứng viên, đẩy event cho AI chấm điểm ngầm, sau đó trả về ngay câu hỏi tiếp theo. Nếu hết câu hỏi, trả về null."
    )
    @PostMapping("/stress/questions/{interviewQuestionId}/answers")
    public ResponseEntity<ApiResponse<CurrentQuestionResponse>> submitAnswerForStressInterview(
            @PathVariable long interviewQuestionId,
            @RequestBody SubmitAnswerRequest request) {

        CurrentQuestionResponse response = interviewService.submitAnswerForStressInterview(
                interviewQuestionId,
                request.getUserAnswer()
        );

        return ResponseEntity.ok(ApiResponse.<CurrentQuestionResponse>builder()
                .data(response)
                .build());
    }

    @Operation(
            summary = "Lấy kết quả đánh giá (Feedback)",
            description = "Trả về nhận xét chi tiết từng câu và đánh giá tổng quan. FE có thể gọi polling (isProcessing = true) nếu AI vẫn đang xử lý ngầm."
    )
    @GetMapping("/{interviewId}/feedback")
    public ResponseEntity<ApiResponse<GetFeedbackResponse>> getFeedback(
            @PathVariable long interviewId) {
        GetFeedbackResponse response = interviewService.getFeedback(interviewId);
        return ResponseEntity.ok(ApiResponse.<GetFeedbackResponse>builder()
                .data(response)
                .build());
    }

    @Operation(
            summary = "Lấy câu hỏi hiện tại",
            description = "Dùng để khôi phục trạng thái (resume) khi ứng viên lỡ tải lại trang. Trả về thông tin câu hỏi đang làm dở."
    )
    @GetMapping("/{interviewId}/current-question")
    public ResponseEntity<ApiResponse<CurrentQuestionResponse>> getCurrentQuestion(
            @PathVariable long interviewId) {

        CurrentQuestionResponse response = interviewService.getCurrentQuestion(interviewId);

        return ResponseEntity.ok(ApiResponse.<CurrentQuestionResponse>builder()
                .data(response)
                .build());
    }

    @Operation(
            summary = "Chat với AI (INTERACTIVE_INTERVIEW - PHỎNG VẤN TƯƠNG TÁC)",
            description = "Gửi tin nhắn cho AI. AI có thể hỏi xoáy tiếp (isComplete = false) hoặc chốt điểm và kết thúc câu hỏi hiện tại (isComplete = true)."
    )
    @PostMapping("/interactive/questions/{interviewQuestionId}/answers")
    public ResponseEntity<ApiResponse<AIInteractive>> answerForInteractiveInterview(
            @PathVariable long interviewQuestionId,
            @Valid @RequestBody SubmitAnswerRequest request) { // Dùng DTO của bạn

        AIInteractive response = interviewService.answerForInteractiveInterview(
                interviewQuestionId,
                request.getUserAnswer()
        );

        return ResponseEntity.ok(ApiResponse.<AIInteractive>builder()
                .data(response)
                .build());
    }

    @Operation(
            summary = "Chuyển sang câu hỏi tiếp theo (INTERACTIVE_INTERVIEW - PHỎNG VẤN TƯƠNG TÁC)",
            description = "Chỉ gọi khi câu hỏi trước đó đã hoàn thành (isComplete = true). Đóng câu cũ và trả về câu hỏi mới."
    )
    @PostMapping("/interactive/questions/{interviewQuestionId}/next")
    public ResponseEntity<ApiResponse<CurrentQuestionResponse>> transitionToNextQuestion(
            @PathVariable long interviewQuestionId) {

        CurrentQuestionResponse nextQuestion = interviewService.transitionToNextQuestionForInteractiveInterview(interviewQuestionId);

        return ResponseEntity.ok(ApiResponse.<CurrentQuestionResponse>builder()
                .data(nextQuestion)
                .build());
    }

    @Operation(
            summary = "Lấy lịch sử chat (INTERACTIVE_INTERVIEW - PHỎNG VẤN TƯƠNG TÁC)",
            description = "Lấy toàn bộ đoạn hội thoại hỏi đáp giữa ứng viên và AI trong một câu hỏi cụ thể."
    )
    @GetMapping("/interactive/questions/{interviewQuestionId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getChatHistory(
            @PathVariable long interviewQuestionId) {

        List<ChatMessageResponse> chatHistory = interviewService.getChatHistory(interviewQuestionId);

        return ResponseEntity.ok(ApiResponse.<List<ChatMessageResponse>>builder()
                .data(chatHistory)
                .build());
    }

    @Operation(
            summary = "Lấy gợi ý của câu hỏi",
            description = "Cung cấp ID của câu hỏi phỏng vấn để lấy thông tin gợi ý (hint) tương ứng."
    )
    @GetMapping("/interviewQuestion/{interviewQuestionId}/hint")
    public ResponseEntity<ApiResponse<GetHintResponse>> getHint(@PathVariable("interviewQuestionId") Long interviewQuestionId) {
        return ResponseEntity.ok(ApiResponse.<GetHintResponse>builder()
                .data(interviewService.getHint(interviewQuestionId))
                .build());
    }

    @Operation(summary = "Lấy lịch sử phỏng vấn", description = "Lấy danh sách lịch sử các buổi phỏng vấn của người dùng (có phân trang và lọc)")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<InterviewResponse>>> getInterviewHistory(
            @ModelAttribute @Valid GetInterviewHistoryRequest request) {

        Page<InterviewResponse> response = interviewService.getInterviewHistory(request);

        return ResponseEntity.ok(
                ApiResponse.<Page<InterviewResponse>>builder()
                        .data(response)
                        .build());
    }
}

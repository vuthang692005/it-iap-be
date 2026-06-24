package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.question.request.QuestionRequest;
import com.example.it_iap.dto.question.request.SearchQuestionRequest;
import com.example.it_iap.dto.question.response.QuestionResponse;
import com.example.it_iap.service.QuestionService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/questions")
@RestController
public class QuestionController {
    private final QuestionService questionService;

    @Operation(
        summary = "Tạo câu hỏi thủ công [ADMIN]", 
        description = "Danh sách các giá trị hợp lệ của Enum:\n" +
                      "- position: [FRONTEND, BACKEND, TESTER, DATA_ANALYST]\n" +
                      "- level: [INTERN, FRESHER]\n" +
                      "- category: [TECHNICAL, SITUATIONAL, BEHAVIORAL]\n" +
                      "- source: [ADMIN, AI]\n" +
                      "Lưu ý: Trường status mặc định khi tạo thủ công là APPROVED."
    )
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<QuestionResponse>> create(@RequestBody @Valid QuestionRequest request) {
        QuestionResponse response = questionService.createQuestion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<QuestionResponse>builder()
                        .code(201)
                        .data(response)
                        .build());
    }

    @Operation(
        summary = "Sửa câu hỏi [ADMIN]", 
        description = "Danh sách các giá trị hợp lệ của Enum:\n" +
                      "- position: [FRONTEND, BACKEND, TESTER, DATA_ANALYST]\n" +
                      "- level: [INTERN, FRESHER]\n" +
                      "- category: [TECHNICAL, SITUATIONAL, BEHAVIORAL]\n" +
                      "- source: [ADMIN, AI]\n" +
                      "- status: [REJECTED, PENDING, APPROVED]"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<QuestionResponse>> update(@RequestBody @Valid QuestionRequest request, @PathVariable Long id) {
        QuestionResponse response = questionService.updateQuestion(request, id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<QuestionResponse>builder()
                        .code(200)
                        .data(response)
                        .build());
    }

    @Operation(
        summary = "Tìm kiếm và lọc danh sách câu hỏi [ADMIN]", 
        description = "Hỗ trợ phân trang (page, size, sort) và lọc nâng cao theo các trường Enum:\n" +
                      "- position: [FRONTEND, BACKEND, TESTER, DATA_ANALYST]\n" +
                      "- level: [INTERN, FRESHER]\n" +
                      "- category: [TECHNICAL, SITUATIONAL, BEHAVIORAL]\n" +
                      "- source: [ADMIN, AI]\n" +
                      "- status: [REJECTED, PENDING, APPROVED]"
    )
    @GetMapping()
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<Page<QuestionResponse>>> search(@ModelAttribute @Valid SearchQuestionRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<Page<QuestionResponse>>builder()
                        .code(200)
                        .data(questionService.searchQuestion(request))
                        .build());
    }
}

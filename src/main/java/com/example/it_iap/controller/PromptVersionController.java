package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.adminPrompt.response.AdminPromptResponse;
import com.example.it_iap.dto.promptVersion.request.PromptVersionIdRequest;
import com.example.it_iap.service.PromptVersionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/prompt_versions")
@RequiredArgsConstructor
public class PromptVersionController {
    private final PromptVersionService promptVersionService;

    @Operation(
            summary = "Lấy chi tiết một phiên bản Prompt",
            description = "Truy xuất toàn bộ thông tin chi tiết (nội dung, model, provider, note...) của một Prompt Version cụ thể dựa vào PromptKey và mã Version."
    )
    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<AdminPromptResponse>> getPromptVersion(
            @Valid @ModelAttribute PromptVersionIdRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.<AdminPromptResponse>builder()
                        .code(200)
                        .data(promptVersionService.getPromptVersion(request))
                        .build());
    }

    @Operation(
            summary = "Kích hoạt (Active) một phiên bản Prompt",
            description = "Thiết lập một phiên bản làm phiên bản chính thức (Active) cho hệ thống sử dụng."
    )
    @PatchMapping("/active")
    public ResponseEntity<ApiResponse> setPromptVersionActive(
            @Valid @RequestBody PromptVersionIdRequest request
    ) {
        promptVersionService.setPromptVersionActive(request);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .code(200)
                        .build());
    }
}

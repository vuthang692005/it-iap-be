package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.adminPrompt.request.AdminPromptRequest;
import com.example.it_iap.dto.adminPrompt.request.AdminPromptSearchRequest;
import com.example.it_iap.dto.adminPrompt.response.AdminPromptResponse;
import com.example.it_iap.dto.adminPrompt.response.AdminPromptSummaryResponse;
import com.example.it_iap.dto.promptVersion.request.PromptVersionRequest;
import com.example.it_iap.entity.enums.PromptUseCase;
import com.example.it_iap.service.AdminPromptService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin_prompts")
@RequiredArgsConstructor
public class AdminPromptController {
    private final AdminPromptService adminPromptService;

    @Operation(
            summary = "Tạo mới một Admin Prompt gốc",
            description = "Tạo một cấu hình Prompt gốc mới (VD: CUSTOMER_SUPPORT) đi kèm với phiên bản (version) đầu tiên của nó."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<AdminPromptSummaryResponse>> createAdminPrompt (@RequestBody @Valid AdminPromptRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<AdminPromptSummaryResponse>builder()
                        .code(201)
                        .data(adminPromptService.createAdminPrompt(request))
                        .build());
    }

    @Operation(
            summary = "Thêm phiên bản mới (Version) cho Prompt gốc",
            description = "Tạo và đính kèm một phiên bản mới (VD: version 0.0.2) cho một Admin Prompt đã tồn tại dựa vào ID của Prompt đó."
    )
    @PostMapping("/{adminPromptId}/versions")
    public ResponseEntity<ApiResponse<AdminPromptResponse>> addNewVersion(
            @PathVariable long adminPromptId,
            @Valid @RequestBody PromptVersionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<AdminPromptResponse>builder()
                        .code(201)
                        .data(adminPromptService.addNewVersion(request, adminPromptId))
                        .build());
    }

    @Operation(
            summary = "Tìm kiếm và phân trang danh sách Prompt",
            description = "Tìm kiếm danh sách các Admin Prompt. Hỗ trợ lọc theo từ khóa (promptKey), mục đích sử dụng (applyFor) và trạng thái (active). Phân trang mặc định 10 records/trang."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminPromptSummaryResponse>>> searchAdminPrompts(
            @ModelAttribute @Valid AdminPromptSearchRequest request
    ){
        PromptUseCase promptUseCase = PromptUseCase.from(request.getApplyFor());
        return ResponseEntity.ok(
                ApiResponse.<Page<AdminPromptSummaryResponse>>builder()
                        .code(200)
                        .data(adminPromptService.searchAdminPrompts(
                                request.getPromptKey(),
                                promptUseCase,
                                request.getActive(),
                                request.getPages()))
                        .build());
    }
}

package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.promotion.request.CreatePromotionRequest;
import com.example.it_iap.dto.promotion.response.PromotionResponse;
import com.example.it_iap.dto.promotion.response.TierInfoResponse;
import com.example.it_iap.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
public class PromotionController {
    private final PromotionService promotionService;

    @Operation(summary = "Tạo khuyến mãi mới [ADMIN]", description = "Admin tạo mã khuyến mãi cho một gói cước cụ thể")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<PromotionResponse>> createPromotion(@RequestBody @Valid CreatePromotionRequest request) {
        PromotionResponse response = promotionService.createPromotion(request);
        return ResponseEntity.ok(
                ApiResponse.<PromotionResponse>builder()
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Bật/Tắt trạng thái khuyến mãi [ADMIN]", description = "Admin thay đổi trạng thái hoạt động của một khuyến mãi")
    @PatchMapping("/{promotionId}/toggle-status")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<PromotionResponse>> toggleActiveStatus(@PathVariable Long promotionId) {
        PromotionResponse response = promotionService.toggleActiveStatus(promotionId);
        return ResponseEntity.ok(
                ApiResponse.<PromotionResponse>builder()
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Xem danh sách các gói cước và khuyến mãi [USER]", description = "User xem danh sách các gói cước (trừ BASIC) kèm theo khuyến mãi đang áp dụng")
    @GetMapping("/tiers")
    public ResponseEntity<ApiResponse<List<TierInfoResponse>>> getAvailableTiers() {
        List<TierInfoResponse> response = promotionService.getAvailableTiers();
        return ResponseEntity.ok(
                ApiResponse.<List<TierInfoResponse>>builder()
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Lấy danh sách tất cả khuyến mãi [ADMIN]", description = "Admin xem toàn bộ khuyến mãi (Có phân trang, xếp mới nhất lên đầu)")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<Page<PromotionResponse>>> getAllPromotions(
            @RequestParam(defaultValue = "1") int page
    ) {
        Page<PromotionResponse> response = promotionService.getAllPromotions(page);
        return ResponseEntity.ok(
                ApiResponse.<Page<PromotionResponse>>builder()
                        .data(response)
                        .build()
        );
    }
}

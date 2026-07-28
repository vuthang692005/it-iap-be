package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.banner.request.BannerRequest;
import com.example.it_iap.dto.banner.response.BannerResponse;
import com.example.it_iap.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
public class BannerController {
    private final BannerService bannerService;

    @Operation(summary = "Lấy danh sách Banner [ADMIN]", description = "Admin lấy danh sách tất cả banner có phân trang")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<Page<BannerResponse>>> getAllBanners(
            @RequestParam(defaultValue = "0") int page
    ) {
        Page<BannerResponse> response = bannerService.getAllBanners(page);
        return ResponseEntity.ok(
                ApiResponse.<Page<BannerResponse>>builder()
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Lấy Banner đang hoạt động [PUBLIC]", description = "Lấy banner đang được active để hiển thị ra trang chủ")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<BannerResponse>> getActiveBanner() {
        BannerResponse response = bannerService.getActiveBanner();
        return ResponseEntity.ok(
                ApiResponse.<BannerResponse>builder()
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Tạo mới Banner [ADMIN]", description = "Admin tạo một banner mới (Hỗ trợ upload ảnh bằng form-data)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<BannerResponse>> createBanner(
            @Valid @ModelAttribute BannerRequest request
    ) {
        BannerResponse response = bannerService.createBanner(request);
        return ResponseEntity.ok(
                ApiResponse.<BannerResponse>builder()
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Cập nhật thông tin Banner [ADMIN]", description = "Admin cập nhật tiêu đề, nội dung hoặc ảnh của banner")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<BannerResponse>> updateBannerInfo(
            @PathVariable Long id,
            @Valid @ModelAttribute BannerRequest request
    ) {
        BannerResponse response = bannerService.updateBannerInfo(id, request);
        return ResponseEntity.ok(
                ApiResponse.<BannerResponse>builder()
                        .data(response)
                        .build()
        );
    }

    @Operation(summary = "Bật/Tắt trạng thái Banner [ADMIN]", description = "Admin thay đổi nhanh trạng thái active của banner")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> changeActiveStatus(
            @PathVariable Long id,
            @RequestParam boolean isActive
    ) {
        bannerService.changeActiveStatus(id, isActive);
        String message = isActive ? "Đã bật banner thành công" : "Đã tắt banner thành công";

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .data(message)
                        .build()
        );
    }
}

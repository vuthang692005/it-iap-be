package com.example.it_iap.controller;

import org.springframework.web.bind.annotation.*;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.auth.request.TwoFactorRequest;
import com.example.it_iap.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.it_iap.dto.auth.request.ResetTwoFactorRequest;

@RestController
@RequestMapping("/api/v1/2fa")
@RequiredArgsConstructor
public class TotpController {
    private final AuthService authService;
    
    @Operation(summary = "Thiết lập xác thực 2 bước")
    @PostMapping("/setup")
    public ResponseEntity<?> setup() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .data(authService.setup2fa())
                        .build());
    }

    @Operation(summary = "Xác nhận xác thực 2 bước", description = "[TEST API] dán secret của api /setup vào https://stefansundin.github.io/2fa-qr/")
    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestBody @Valid TwoFactorRequest request) {
        authService.confirm2fa(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Bật xác thực 2 bước thành công")
                        .build());
    }

    @Operation(summary = "Hủy xác thực 2 bước")
    @PostMapping("/disable")
    public ResponseEntity<?> disable(@RequestBody @Valid TwoFactorRequest request) {
        authService.disable2fa(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Hủy xác thực 2 bước thành công")
                        .build());
    }

    @Operation(summary = "Lấy trạng thái xác thực 2 bước của tài khoản")
    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                .data(authService.status2fa())
                        .build());
    }

    @Operation(summary = "Yêu cầu khôi phục / gỡ 2FA qua Email", description = "Gửi email xác thực khôi phục 2FA có hiệu lực 10 phút")
    @PostMapping("/request-reset")
    public ResponseEntity<?> requestReset() {
        authService.requestReset2fa();
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Một email xác nhận khôi phục 2FA đã được gửi tới địa chỉ email của bạn")
                        .build());
    }

    @Operation(summary = "Xác nhận gỡ 2FA (Bắt đầu đếm ngược 24h)", description = "Xác nhận từ link email, hệ thống sẽ đưa vào đếm ngược 24 giờ trước khi gỡ hẳn 2FA")
    @PostMapping("/confirm-reset")
    public ResponseEntity<?> confirmReset(@RequestBody @Valid ResetTwoFactorRequest request) {
        authService.confirmReset2fa(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Yêu cầu gỡ 2FA đã được xác nhận. Tính năng 2FA sẽ chính thức bị tắt sau 24 giờ")
                        .build());
    }

    @Operation(summary = "Từ chối / Hủy yêu cầu gỡ 2FA (Không phải tôi)", description = "Hủy bỏ đợt yêu cầu gỡ 2FA và giữ nguyên trạng thái an toàn cho tài khoản")
    @PostMapping("/cancel-reset")
    public ResponseEntity<?> cancelReset(@RequestBody @Valid ResetTwoFactorRequest request) {
        authService.cancelReset2fa(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Đã hủy bỏ yêu cầu gỡ 2FA. Tài khoản của bạn vẫn được bảo vệ bằng xác thực 2 bước")
                        .build());
    }
}
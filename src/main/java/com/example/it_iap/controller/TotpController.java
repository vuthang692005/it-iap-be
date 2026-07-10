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
}
package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.auth.request.*;
import com.example.it_iap.dto.auth.response.AuthResponse;
import com.example.it_iap.dto.auth.response.RoleResponse;
import com.example.it_iap.service.AuthService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Đăng ký tài khoản mới")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<AuthResponse>builder()
                        .code(201)
                        .data(authService.register(request))
                        .build());
    }

    @Operation(summary = "Đăng nhập", description = "Đăng nhập hệ thống, trả về vai trò và cấu hình cookie")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<RoleResponse>> login(@RequestBody @Valid LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse response) throws JOSEException {
        RoleResponse userRoles = authService.login(request, httpRequest, response);
        return ResponseEntity.ok(
                ApiResponse.<RoleResponse>builder()
                        .data(userRoles)
                        .build());
    }

    @Operation(summary = "Đăng nhập xác minh 2 bước", description = "Đăng nhập hệ thống xác minh 2 bước, trả về vai trò và cấu hình cookie")
    @PostMapping("/login/verify-2fa")
    public ResponseEntity<ApiResponse<RoleResponse>> login(@RequestBody @Valid TwoFactorRequest req, HttpServletRequest request,
            HttpServletResponse response) throws ParseException, JOSEException {
        RoleResponse userRoles = authService.login2fa(req, request, response);
        return ResponseEntity.ok(
                ApiResponse.<RoleResponse>builder()
                        .data(userRoles)
                        .build());
    }

    @Operation(summary = "Làm mới Access Token", description = "Sử dụng Refresh Token để cấp lại Access Token mới")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RoleResponse>> refreshToken(HttpServletRequest request,
            HttpServletResponse response)
            throws JOSEException, ParseException {
        RoleResponse userRoles = authService.refreshToken(request, response);
        return ResponseEntity.ok(
                ApiResponse.<RoleResponse>builder()
                        .data(userRoles)
                        .build());
    }

    @Operation(summary = "Xác thực Email bằng OTP")
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestBody @Valid VerifyEmailRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok(ApiResponse.builder()
                .build());
    }

    @Operation(summary = "Gửi lại mã OTP kích hoạt tài khoản")
    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse> resendOtp(@RequestBody @Valid ResendOtpRequest request) {
        authService.resendOtp(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                ApiResponse.builder()
                        .code(202)
                        .build());
    }

    @Operation(summary = "Đăng xuất tài khoản", description = "Xóa phiên đăng nhập hiện tại và xóa cookie tokens")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request, HttpServletResponse response)
            throws JOSEException, ParseException {
        authService.logout(request, response);
        return ResponseEntity.ok(ApiResponse.builder()
                .build());
    }

    @Operation(summary = "Yêu cầu quên mật khẩu", description = "Gửi mã OTP khôi phục mật khẩu qua Email")
    @PostMapping("/password/forgot")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                ApiResponse.<Void>builder()
                        .code(202)
                        .build()
        );
    }

    @Operation(summary = "Đặt lại mật khẩu mới", description = "Xác thực mã OTP quên mật khẩu và tiến hành cập nhật mật khẩu mới")
    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Void>> verifyForgotPassword(@RequestBody @Valid VerifyForgotPasswordRequest request) {
        authService.verifyForgotPassword(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<Void>builder()
                        .code(200)
                        .build()
        );
    }
}

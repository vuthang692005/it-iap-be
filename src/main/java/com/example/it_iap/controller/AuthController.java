package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.auth.request.*;
import com.example.it_iap.dto.auth.response.RegisterResponse;
import com.example.it_iap.dto.auth.response.RoleResponse;
import com.example.it_iap.service.AuthService;
import com.nimbusds.jose.JOSEException;
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

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<RegisterResponse>builder()
                        .code(201)
                        .data(authService.register(request))
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<RoleResponse>> login(@RequestBody @Valid LoginRequest request,
            HttpServletResponse response) throws JOSEException {
        RoleResponse userRoles = authService.login(request, response);
        return ResponseEntity.ok(
                ApiResponse.<RoleResponse>builder()
                        .data(userRoles)
                        .build());
    }

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

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestBody @Valid VerifyEmailRequest request) {
        authService.verifyEmail(request);
        return ResponseEntity.ok(ApiResponse.builder()
                .build());
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse> resendOtp(@RequestBody @Valid ResendOtpRequest request) {
        authService.resendOtp(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                ApiResponse.builder()
                        .code(202)
                        .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request, HttpServletResponse response)
            throws JOSEException, ParseException {
        authService.logout(request, response);
        return ResponseEntity.ok(ApiResponse.builder()
                .build());
    }
}

package com.example.test.controller;

import com.example.test.dto.ApiResponse;
import com.example.test.dto.auth.request.*;
import com.example.test.dto.auth.response.TokenResponse;
import com.example.test.entity.User;
import com.example.test.service.AuthService;
import com.example.test.service.impl.AuthServiceImpl;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .code(201)
                        .data(authService.register(request))
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid LoginRequest request) throws JOSEException {
        return ResponseEntity.ok(ApiResponse.builder()
                .data(authService.login(request))
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshToken(@RequestBody RefreshTokenRequest request)
            throws JOSEException, ParseException {
        return ResponseEntity.ok(ApiResponse.builder()
                .data(authService.refreshToken(request))
                .build());
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestBody @Valid VerifyEmailRequest request){
        authService.verifyEmail(request);
        return ResponseEntity.ok(ApiResponse.builder()
                .build());
    }

    @PostMapping("/resend-otp")
    private ResponseEntity<ApiResponse> resendOtp(@RequestBody @Valid ResendOtpRequest request){
        authService.resendOtp(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                ApiResponse.builder()
                .code(202)
                .build());
    }
}

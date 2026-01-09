package com.example.test.controller;

import com.example.test.dto.ApiResponse;
import com.example.test.dto.auth.request.LoginRequest;
import com.example.test.dto.auth.request.RefreshTokenRequest;
import com.example.test.dto.auth.request.RegisterRequest;
import com.example.test.dto.auth.response.TokenResponse;
import com.example.test.service.AuthService;
import com.example.test.service.impl.AuthServiceImpl;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder().build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid LoginRequest request) throws JOSEException {
        return ResponseEntity.ok(ApiResponse.builder()
                .data(authService.login(request))
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request)
            throws JOSEException, ParseException {
        return ResponseEntity.ok(ApiResponse.builder()
                .data(authService.refreshToken(request))
                .build());
    }
}

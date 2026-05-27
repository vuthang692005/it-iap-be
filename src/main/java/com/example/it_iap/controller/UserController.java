package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {

    @GetMapping("/info")
    public ResponseEntity<ApiResponse> info(@AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.ok(ApiResponse.builder()
                .data(jwt.getSubject())
                .build());
    }
}

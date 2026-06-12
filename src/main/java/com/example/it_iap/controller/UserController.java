package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.user.request.ChangePasswordRequest;
import com.example.it_iap.dto.user.request.SearchUserRequest;
import com.example.it_iap.dto.user.response.UserResponse;
import com.example.it_iap.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/info")
    public ResponseEntity<ApiResponse> info(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.builder()
                .data(jwt.getSubject())
                .build());
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .build());
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUser(@ModelAttribute SearchUserRequest request){
        return ResponseEntity.ok(
                ApiResponse.<Page<UserResponse>>builder()
                        .data(userService.searchUser(request))
                        .build()
        );
    }
}

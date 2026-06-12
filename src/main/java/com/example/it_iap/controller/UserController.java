package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.user.request.ChangePasswordRequest;
import com.example.it_iap.dto.user.request.CreateUserRequest;
import com.example.it_iap.dto.user.request.UpdateUserRequest;
import com.example.it_iap.dto.user.response.UserResponse;
import com.example.it_iap.dto.user.request.UpdateUserInfoRequest;
import com.example.it_iap.dto.user.response.UserResponse;
import com.example.it_iap.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> create(@RequestBody @Valid CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                .data(response)
                .message("Tạo user thành công")
                        .build());
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable UUID id, @RequestBody @Valid UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                .data(response)
                .message("Tạo user thành công")
                        .build());
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserResponse>> getInfo() {
        UserResponse response = userService.getInfo();
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .data(response)
                .build());
    }

    @PutMapping("/info")
    public ResponseEntity<ApiResponse<UserResponse>> updateInfo(@RequestBody @Valid UpdateUserInfoRequest request) {
        UserResponse response = userService.updateInfo(request);
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .data(response)
                .build());
    }

    @PostMapping(consumes = "multipart/form-data", value = "/avatar")
    public ResponseEntity<ApiResponse> updateAvatar(@RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.updateAvatar(file);
        return ResponseEntity.ok(ApiResponse.builder()
                .data(avatarUrl)
                .build());
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .build());
    }
}

package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.user.request.*;
import com.example.it_iap.dto.user.response.UserResponse;
import com.example.it_iap.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.UUID;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Operation(summary = "Tạo người dùng mới [ADMIN]", description = "Admin tạo tài khoản người dùng mới trong hệ thống")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> create(@RequestBody @Valid CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                .data(response)
                        .build());
    }

    @Operation(summary = "Cập nhật người dùng [ADMIN]", description = "Admin thay đổi thông tin tài khoản bất kỳ qua UUID")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable UUID id, @RequestBody @Valid UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                .data(response)
                        .build());
    }

    @Operation(summary = "Lấy thông tin cá nhân", description = "Lấy thông tin chi tiết của người dùng đang đăng nhập")
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserResponse>> getInfo() {
        UserResponse response = userService.getInfo();
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .data(response)
                .build());
    }

    @Operation(summary = "Cập nhật thông tin cá nhân", description = "Người dùng tự cập nhật thông tin cơ bản của chính mình")
    @PutMapping("/info")
    public ResponseEntity<ApiResponse<UserResponse>> updateInfo(@RequestBody @Valid UpdateUserInfoRequest request) {
        UserResponse response = userService.updateInfo(request);
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .data(response)
                .build());
    }

    @Operation(summary = "Cập nhật ảnh đại diện", description = "Tải lên tệp ảnh để thay đổi avatar cá nhân")
    @PostMapping(consumes = "multipart/form-data", value = "/avatar")
    public ResponseEntity<ApiResponse> updateAvatar(@RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.updateAvatar(file);
        return ResponseEntity.ok(ApiResponse.builder()
                .data(avatarUrl)
                .build());
    }

    @Operation(summary = "Đổi mật khẩu", description = "Người dùng tự thay đổi mật khẩu hiện tại")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .build());
    }

    @Operation(summary = "Tìm kiếm và lọc danh sách người dùng [Admin]", description = "Hỗ trợ phân trang, lọc và tìm kiếm người dùng nâng cao")
    @GetMapping()
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUser(@ModelAttribute SearchUserRequest request){
        return ResponseEntity.ok(
                ApiResponse.<Page<UserResponse>>builder()
                        .data(userService.searchUser(request))
                        .build()
        );
    }

    @Operation(summary = "Yêu cầu thay đổi địa chỉ email")
    @PostMapping("/change-email")
    public ResponseEntity<ApiResponse> changeEmail(@RequestBody @Valid ChangeEmailRequest request) {
        userService.changeEmail(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                ApiResponse.builder()
                        .code(202)
                        .build());
    }

    @Operation(summary = "Xác nhận mã OTP để hoàn tất đổi email")
    @PostMapping("/verify-change-email")
    public ResponseEntity<ApiResponse> verifyChangeEmail(@RequestBody @Valid VerifyChangeEmailRequest request) {
        userService.verifyChangeEmail(request.getOtp());
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .build());
    }
}

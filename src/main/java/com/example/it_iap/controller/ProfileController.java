package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.profile.request.ProfileRequest;
import com.example.it_iap.dto.profile.request.UpdateProfileRequest;
import com.example.it_iap.dto.profile.response.ProfileResponse;
import com.example.it_iap.dto.profile.response.ProfileSummaryResponse;
import com.example.it_iap.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    private final ProfileService profileService;

    @Operation(summary = "Tạo hồ sơ mới", description = "Tạo mới một hồ sơ ứng viên vào hệ thống")
    @PostMapping()
    public ResponseEntity<ApiResponse<ProfileResponse>> createProfile (@RequestBody @Valid ProfileRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ProfileResponse>builder()
                        .code(201)
                        .data(profileService.createProfile(request))
                        .build());
    }

    @Operation(summary = "Cập nhật hồ sơ", description = "Cập nhật thông tin chi tiết của hồ sơ dựa theo profileId")
    @PutMapping("/{profileId}")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile (
            @RequestBody @Valid UpdateProfileRequest request,
            @PathVariable long profileId
            ){
        return ResponseEntity.ok(
                ApiResponse.<ProfileResponse>builder()
                        .data(profileService.updateProfile(request, profileId))
                        .build());
    }

    @Operation(summary = "Xóa hồ sơ")
    @DeleteMapping("/{profileId}")
    public ResponseEntity<ApiResponse> deleteProfile (@PathVariable long profileId){
        profileService.deleteProfile(profileId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .build());
    }

    @Operation(summary = "Lấy chi tiết một hồ sơ", description = "Trả về thông tin chi tiết đầy đủ của một hồ sơ ứng viên")
    @GetMapping("/{profileId}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile (@PathVariable long profileId){
        return ResponseEntity.ok(
                ApiResponse.<ProfileResponse>builder()
                        .data(profileService.getProfile(profileId))
                        .build());
    }

    @Operation(summary = "Lấy danh sách hồ sơ", description = "Trả về danh sách thu gọn của tất cả các hồ sơ hiện có")
    @GetMapping()
    public ResponseEntity<ApiResponse<List<ProfileSummaryResponse>>> getAllProfile (){
        return ResponseEntity.ok(
                ApiResponse.<List<ProfileSummaryResponse>>builder()
                        .data(profileService.getAllProfiles())
                        .build());
    }
}

package com.example.it_iap.controller;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.profile.request.ProfileRequest;
import com.example.it_iap.dto.profile.response.ProfileResponse;
import com.example.it_iap.dto.profile.response.ProfileSummaryResponse;
import com.example.it_iap.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping()
    public ResponseEntity<ApiResponse<ProfileResponse>> createProfile (@RequestBody @Valid ProfileRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ProfileResponse>builder()
                        .code(201)
                        .data(profileService.createProfile(request))
                        .build());
    }

    @PutMapping("/{profileId}")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile (
            @RequestBody @Valid ProfileRequest request,
            @PathVariable long profileId
            ){
        return ResponseEntity.ok(
                ApiResponse.<ProfileResponse>builder()
                        .data(profileService.updateProfile(request, profileId))
                        .build());
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<ApiResponse> deleteProfile (@PathVariable long profileId){
        profileService.deleteProfile(profileId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .build());
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile (@PathVariable long profileId){
        return ResponseEntity.ok(
                ApiResponse.<ProfileResponse>builder()
                        .data(profileService.getProfile(profileId))
                        .build());
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ProfileSummaryResponse>>> getAllProfile (){
        return ResponseEntity.ok(
                ApiResponse.<List<ProfileSummaryResponse>>builder()
                        .data(profileService.getAllProfiles())
                        .build());
    }
}

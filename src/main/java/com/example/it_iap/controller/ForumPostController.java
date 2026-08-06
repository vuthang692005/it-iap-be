package com.example.it_iap.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.ForumPostSliceResponse;
import com.example.it_iap.dto.forumPost.request.ReactPostRequest;
import com.example.it_iap.dto.forumPost.response.GetForumPostDTO;
import com.example.it_iap.service.ForumPostService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/forum-posts")
@RequiredArgsConstructor
public class ForumPostController {
    private final ForumPostService forumPostService;

    @Operation(summary = "Chia sẻ bài đăng Streak", description = "Chia sẻ số chuỗi hiện tại thành bài đăng")
    @PostMapping("/share/streak")
    public ResponseEntity<?> shareStreakPost() {
        forumPostService.shareStreakPost();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .code(201)
                        .build());
    }

    @Operation(summary = "Chia sẻ bài đăng GPA", description = "Chia sẻ điểm GPA thành bài đăng")
    @PostMapping("/share/grade")
    public ResponseEntity<?> shareGradePost() {
        forumPostService.shareGradePost();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .code(201)
                        .build());
    }

    @Operation(summary = "Lấy bài đăng của chung", description = "Seed từ (10000 - 99999)")
    @GetMapping
    public ResponseEntity<?> getPosts(
        @RequestParam @Min(1) int page, 
        @RequestParam @Min(10000) @Max(99999) int seed) {
        ForumPostSliceResponse<GetForumPostDTO> response = forumPostService.getPosts(page, seed);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<ForumPostSliceResponse<GetForumPostDTO>>builder()
                    .data(response)
                    .build());
    }

    @Operation(summary = "Lấy bài đăng của bản thân")
    @GetMapping("/me")
    public ResponseEntity<?> getMyPosts(
        @RequestParam @Min(1) int page) {
        ForumPostSliceResponse<GetForumPostDTO> response = forumPostService.getMyPosts(page);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<ForumPostSliceResponse<GetForumPostDTO>>builder()
                    .data(response)
                    .build());
    }

    @Operation(summary = "Đổi chế độ hiển thị bài đăng")
    @PutMapping("/change-visible/{postId}")
    public ResponseEntity<?> changePostVisible(@PathVariable Long postId) {
        forumPostService.changePostVisible(postId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                    .build());
    }

    @Operation(summary = "Thả cảm xúc bài đăng", description = "LOVE - HAHA - WOW")
    @PostMapping("/react/{postId}")
    public ResponseEntity<?> reactPost(@PathVariable Long postId, @RequestBody @Valid ReactPostRequest request) {
        GetForumPostDTO response = forumPostService.reactPost(postId, request);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<GetForumPostDTO>builder()
                    .data(response)
                    .build());
    }
}

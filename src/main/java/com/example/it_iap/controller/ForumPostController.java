package com.example.it_iap.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.dto.ForumPostSliceResponse;
import com.example.it_iap.dto.forumPost.response.GetForumPostDTO;
import com.example.it_iap.service.ForumPostService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/forum-posts")
@RequiredArgsConstructor
public class ForumPostController {
    private final ForumPostService forumPostService;

    @PostMapping("/share/streak")
    public ResponseEntity<?> shareStreakPost() {
        forumPostService.shareStreakPost();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .code(201)
                        .build());
    }

    @PostMapping("/share/grade")
    public ResponseEntity<?> shareGradePost() {
        forumPostService.shareGradePost();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .code(201)
                        .build());
    }

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

    @GetMapping("/me")
    public ResponseEntity<?> getMyPosts(
        @RequestParam @Min(1) int page) {
        ForumPostSliceResponse<GetForumPostDTO> response = forumPostService.getMyPosts(page);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<ForumPostSliceResponse<GetForumPostDTO>>builder()
                    .data(response)
                    .build());
    }

    @PutMapping("/change-visible/{postId}")
    public ResponseEntity<?> changePostVisible(@PathVariable Long postId) {
        forumPostService.changePostVisible(postId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                    .build());
    }
}

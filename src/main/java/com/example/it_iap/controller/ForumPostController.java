package com.example.it_iap.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.it_iap.dto.ApiResponse;
import com.example.it_iap.service.ForumPostService;

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
            .build()
        );
    }

    @PostMapping("/share/grade")
    public ResponseEntity<?> shareGradePost() {
        forumPostService.shareGradePost();
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.builder()
            .code(201)
            .build()
        );
    }
}

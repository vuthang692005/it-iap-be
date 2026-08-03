package com.example.it_iap.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.it_iap.entity.ForumPost;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.enums.ForumPostType;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.record.GradeSharedData;
import com.example.it_iap.record.StreakSharedData;
import com.example.it_iap.repository.ForumPostRepository;
import com.example.it_iap.service.ForumPostService;
import com.example.it_iap.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "FORUM_POST_SERVICE")
public class ForumPostServiceImpl implements ForumPostService {
    private final UserService userService;
    private final DashboardServiceImpl dashboardServiceImpl;

    private final ForumPostRepository forumPostRepository;

    private final ObjectMapper objectMapper;

    @Override
    public void shareStreakPost() {
        User user = userService.getCurrentUser();

        if (user.getCurrentStreak() < 3) {
            throw new AppException(ErrorCode.CURRENT_STREAK_NOT_ENOUGH);
        }

        ForumPostType type = ForumPostType.STREAK;

        // Kiểm tra xem hôm nay đã share streak post chưa
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        if (forumPostRepository.existsByUserIdAndPostTypeAndCreatedAtAfter(user.getId(), type, startOfToday)) {
            throw new AppException(ErrorCode.YOU_ALREADY_SHARE_TODAY);
        }

        // Tạo bài đăng
        ForumPost forumPost = new ForumPost();
        forumPost.setUser(user);
        forumPost.setPostType(type);
        forumPost.setSharedData(createStreakData(user));
        forumPostRepository.save(forumPost);
    }

    @Override
    public void shareGradePost() {
        User user = userService.getCurrentUser();

        if (user.getCurrentGpa() < 2) { // Ngu thì share cái gì
            throw new AppException(ErrorCode.CURRENT_GPA_TOO_LOW);
        }

        ForumPostType type = ForumPostType.GRADE;

        // Kiểm tra xem hôm nay đã share grade post chưa
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        if (forumPostRepository.existsByUserIdAndPostTypeAndCreatedAtAfter(user.getId(), type, startOfToday)) {
            throw new AppException(ErrorCode.YOU_ALREADY_SHARE_TODAY);
        }

        // Tạo bài đăng
        ForumPost forumPost = new ForumPost();
        forumPost.setUser(user);
        forumPost.setPostType(type);
        forumPost.setSharedData(createGradeData(user));
        forumPostRepository.save(forumPost);
    }

    private JsonNode createStreakData(User user) {
        StreakSharedData data = new StreakSharedData(
            user.getCurrentStreak()
        );

        return objectMapper.valueToTree(data);
    }

    private JsonNode createGradeData(User user) {
        GradeSharedData data = new GradeSharedData(
            user.getCurrentGpa(), 
            dashboardServiceImpl.determineUserRank(user), 
            user.getTotalCompletedInterviews()
        );

        return objectMapper.valueToTree(data);
    }
}

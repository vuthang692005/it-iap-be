package com.example.it_iap.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.it_iap.dto.forumPost.response.ForumPostSliceResponse;
import com.example.it_iap.dto.forumPost.request.ReactPostRequest;
import com.example.it_iap.dto.forumPost.response.GetForumPostDTO;
import com.example.it_iap.dto.reaction.response.PostReactionData;
import com.example.it_iap.entity.ForumPost;
import com.example.it_iap.entity.PostReaction;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.enums.ForumPostType;
import com.example.it_iap.entity.enums.ReactionType;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.record.GradeSharedData;
import com.example.it_iap.record.StreakSharedData;
import com.example.it_iap.repository.ForumPostRepository;
import com.example.it_iap.repository.PostReactionRepository;
import com.example.it_iap.service.ForumPostService;
import com.example.it_iap.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "FORUM_POST_SERVICE")
public class ForumPostServiceImpl implements ForumPostService {
    private final UserService userService;
    private final DashboardServiceImpl dashboardServiceImpl;

    private final ForumPostRepository forumPostRepository;
    private final PostReactionRepository postReactionRepository;

    private final JsonMapper jsonMapper;

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

    @Override
    public ForumPostSliceResponse<GetForumPostDTO> getPosts(int page, int seed) {
        User user = userService.getCurrentUser();

        Pageable pageable = PageRequest.of(page - 1, 3); // Lấy 3 bài tính toán cho lẹ

        Slice<GetForumPostDTO> slice = forumPostRepository.getPosts(user.getId(), seed, pageable);

        return new ForumPostSliceResponse<>(
                slice.getContent(),
                slice.hasNext());
    }

    @Override
    public ForumPostSliceResponse<GetForumPostDTO> getMyPosts(int page) {
        User user = userService.getCurrentUser();

        Pageable pageable = PageRequest.of(page - 1, 3, Sort.by("createdAt").descending()); // Lấy 3 bài tính toán cho
                                                                                            // lẹ

        Slice<GetForumPostDTO> slice = forumPostRepository.getMyPosts(user.getId(), pageable);

        return new ForumPostSliceResponse<>(
                slice.getContent(),
                slice.hasNext());
    }

    @Transactional
    @Override
    public void changePostVisible(Long postId) {
        User user = userService.getCurrentUser();

        ForumPost forumPost = forumPostRepository.findByIdAndUserId(postId, user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        forumPost.setVisible(!forumPost.isVisible());
        forumPostRepository.save(forumPost);
    }

    @Override
    public GetForumPostDTO reactPost(Long postId, ReactPostRequest request) {
        ReactionType type = ReactionType.fromString(request.getReactType());

        User user = userService.getCurrentUser();
        UUID userId = user.getId();

        ForumPost forumPost = forumPostRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        // Nếu là bài viết bị ẩn thì chỉ chủ sở hữu mới thả cảm xúc được        
        if (!forumPost.isVisible() && (userId != forumPost.getUser().getId())) {
            throw new AppException(ErrorCode.NOT_ABLE_TO_REACT);
        }

        Long forumPostId = forumPost.getId();

        PostReaction postReaction = postReactionRepository.findByUserIdAndPostId(userId, forumPostId);

        // Trường hợp 1: Chưa từng reaction post
        if (postReaction == null) {
            if (type == null) {
                throw new AppException(ErrorCode.DATA_INVALID);
            }
            PostReaction postReaction2 = new PostReaction();
            postReaction2.setPost(forumPost);
            postReaction2.setUser(user);
            postReaction2.setType(type);
            postReactionRepository.save(postReaction2);

            PostReactionData reactionData = postReactionRepository.countPostReactionData(forumPostId);

            return toGetForumPostDTO(user, forumPost, reactionData, type);
        }

        // Trường hợp 2: Hủy reaction post
        // (Không cần check PostReaction có null hay không vì check trên đó rồi)
        if (type == null) {
            postReactionRepository.deleteById(postReaction.getId());
            PostReactionData reactionData = postReactionRepository.countPostReactionData(forumPostId);
            return toGetForumPostDTO(user, forumPost, reactionData, type);
        }

        // Trường hợp 3: Đổi rection post
        postReaction.setType(type);
        postReactionRepository.save(postReaction);
        PostReactionData reactionData = postReactionRepository.countPostReactionData(forumPostId);
        return toGetForumPostDTO(user, forumPost, reactionData, type);
    }

    private JsonNode createStreakData(User user) {
        StreakSharedData data = new StreakSharedData(
                user.getCurrentStreak());

        return jsonMapper.valueToTree(data);
    }

    private JsonNode createGradeData(User user) {
        GradeSharedData data = new GradeSharedData(
                user.getCurrentGpa(),
                dashboardServiceImpl.determineUserRank(user),
                user.getTotalCompletedInterviews());

        return jsonMapper.valueToTree(data);
    }

    // Phương thức hỗ trợ
    private GetForumPostDTO toGetForumPostDTO(
        User user, 
        ForumPost forumPost, 
        PostReactionData reactionData, 
        ReactionType type) {
        return new GetForumPostDTO(
                forumPost.getId(), 
                user.getAvatarUrl(), 
                forumPost.getPostType(), 
                forumPost.getSharedData(), 
                forumPost.getCreatedAt(), 
                forumPost.isVisible(), 
                reactionData.getTotalLove(), 
                reactionData.getTotalHaha(), 
                reactionData.getTotalWow(), 
                type);
    }
}

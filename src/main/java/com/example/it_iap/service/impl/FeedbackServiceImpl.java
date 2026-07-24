package com.example.it_iap.service.impl;

import com.example.it_iap.dto.feedback.request.AdminReplyRequest;
import com.example.it_iap.dto.feedback.request.FeedbackFilterRequest;
import com.example.it_iap.dto.feedback.request.FeedbackRequest;
import com.example.it_iap.dto.feedback.response.FeedbackResponse;
import com.example.it_iap.entity.Feedback;
import com.example.it_iap.entity.User;
import com.example.it_iap.enums.UploadFolder;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.FeedbackRepository;
import com.example.it_iap.service.CloudinaryService;
import com.example.it_iap.service.FeedbackService;
import com.example.it_iap.service.UserService;
import com.example.it_iap.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {
    private final UserService userService;
    private final FeedbackRepository feedbackRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public FeedbackResponse createFeedback(FeedbackRequest request) {
        User user = userService.getCurrentUser();
        String imageUrl = null;

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = cloudinaryService.uploadImage(request.getImage(), UploadFolder.FEEDBACK_IMAGE);
        }

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setContent(request.getContent());
        feedback.setRating(request.getRating());
        feedback.setImageUrl(imageUrl);

        feedback = feedbackRepository.save(feedback);

        return mapToResponse(feedback);
    }

    @Transactional(readOnly = true)
    public Page<FeedbackResponse> getAllFeedbacks(FeedbackFilterRequest request) {
        int page = Math.max(0, request.getPage() - 1);
        int size = 10;

        UUID userId = null;
        if (Boolean.TRUE.equals(request.getOnlyMine())) {
            userId = SecurityUtils.getCurrentUserId();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Feedback> feedbackPage = feedbackRepository.findFeedbacksWithFilter(request.getRating(), userId, pageable);

        return feedbackPage.map(this::mapToResponse);
    }

    @Transactional
    public FeedbackResponse updateAdminReply(Long feedbackId, AdminReplyRequest request) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));

        feedback.setAdminReply(request.getAdminReply());

        feedback = feedbackRepository.save(feedback);

        return mapToResponse(feedback);
    }

    @Transactional
    public void deleteFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));

        boolean isAdmin = SecurityUtils.isAdmin();

        if (!isAdmin) {
            UUID currentUserId = SecurityUtils.getCurrentUserId();
            boolean isOwner = feedback.getUser().getId().equals(currentUserId);

            if (!isOwner) {
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }
        }

        feedbackRepository.delete(feedback);
    }

    private FeedbackResponse mapToResponse(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getContent(),
                feedback.getImageUrl(),
                feedback.getRating(),
                feedback.getAdminReply(),
                feedback.getUser().getEmail(),
                feedback.getCreatedAt()
        );
    }
}

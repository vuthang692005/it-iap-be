package com.example.it_iap.service;

import com.example.it_iap.dto.feedback.request.AdminReplyRequest;
import com.example.it_iap.dto.feedback.request.FeedbackFilterRequest;
import com.example.it_iap.dto.feedback.request.FeedbackRequest;
import com.example.it_iap.dto.feedback.response.FeedbackResponse;
import com.example.it_iap.dto.feedback.response.FeedbackListResponse;

public interface FeedbackService {
    FeedbackResponse createFeedback(FeedbackRequest request);
    FeedbackListResponse getAllFeedbacks(FeedbackFilterRequest request);
    FeedbackResponse updateAdminReply(Long feedbackId, AdminReplyRequest request);
    void deleteFeedback(Long feedbackId);
}

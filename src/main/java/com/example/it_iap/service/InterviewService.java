package com.example.it_iap.service;

import com.example.it_iap.dto.ai.response.AIInteractive;
import com.example.it_iap.dto.chatMessage.response.ChatMessageResponse;
import com.example.it_iap.dto.interview.response.GetFeedbackResponse;
import com.example.it_iap.dto.interview.response.GetHintResponse;
import com.example.it_iap.dto.interview.response.InterviewIdResponse;
import com.example.it_iap.dto.question.response.CurrentQuestionResponse;

import java.util.List;

public interface InterviewService {
    public InterviewIdResponse createInterview (String interviewMode, String title, long profileId);
    CurrentQuestionResponse startInterview (long interviewId);
    public CurrentQuestionResponse submitAnswerForStressInterview (long interviewQuestionId, String userAnswer);
    CurrentQuestionResponse getCurrentQuestion (long interviewId);
    GetFeedbackResponse getFeedback (long interviewId);
    AIInteractive answerForInteractiveInterview (long interviewQuestionId, String userAnswer);
    CurrentQuestionResponse transitionToNextQuestionForInteractiveInterview (long interviewQuestionId);
    List<ChatMessageResponse> getChatHistory (long interviewQuestionId);
    GetHintResponse getHint (long interviewQuestionId);
}

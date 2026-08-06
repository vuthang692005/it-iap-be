package com.example.it_iap.service;

import com.example.it_iap.entity.Interview;
import com.example.it_iap.entity.InterviewQuestion;
import com.example.it_iap.entity.Question;
import com.example.it_iap.entity.enums.InterviewMode;

import java.util.List;

public interface InterviewQuestionService {
    List<InterviewQuestion> createInterviewQuestion (List<Question> questions, Interview interview);
    InterviewQuestion getCurrentQuestion (long interviewId);
    InterviewQuestion activateNextUnansweredQuestion (long interviewId, InterviewMode interviewMode);
    boolean hasNextQuestion (Long interviewId, int currentOrderIndex);
    void saveUserAnswerForStressInterview (InterviewQuestion interviewQuestion, String userAnswer);
    InterviewQuestion findValidQuestionForUser (long interviewQuestionId);
    void completeQuestion (InterviewQuestion interviewQuestion, String feedback, Float point, Float articulationPoint, Float focusPoint);
    boolean lockQuestionForProcessing(long id);
    void unlockQuestion(long id);
    void completeInterviewQuestion (InterviewQuestion interviewQuestion);
}

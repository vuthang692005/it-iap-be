package com.example.it_iap.dto.question.response;

import com.example.it_iap.entity.enums.InterviewMode;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class CurrentQuestionResponse {
    private Long interviewQuestionId;
    private String questionContent;
    private String category;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime timeEnd;
    private boolean hasNext;
    private InterviewMode interviewMode;
    private Boolean isComplete;
}

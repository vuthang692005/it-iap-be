package com.example.it_iap.dto.interview.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SubmitAnswerRequest {
    @NotBlank(message = "USER_ANSWER_INVALID")
    private String userAnswer;
}

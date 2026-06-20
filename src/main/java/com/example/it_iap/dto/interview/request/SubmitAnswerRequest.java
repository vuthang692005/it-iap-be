package com.example.it_iap.dto.interview.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SubmitAnswerRequest {
    @NotBlank(message = "")
    private String userAnswer;
}

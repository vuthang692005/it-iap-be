package com.example.it_iap.dto.interview.request;

import com.example.it_iap.entity.enums.InterviewMode;
import com.example.it_iap.validator.annotation.EnumValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateInterviewRequest {
    @NotBlank(message = "INTERVIEW_MODE_INVALID")
    @EnumValue(enumClass = InterviewMode.class, message = "INTERVIEW_MODE_INVALID")
    private String mode;

    @NotBlank(message = "TITLE_INVALID")
    private String title;

    @NotNull(message = "PROFILE_ID_INVALID")
    private Long profileId;
}

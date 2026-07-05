package com.example.it_iap.dto.interview.request;

import com.example.it_iap.entity.enums.InterviewMode;
import com.example.it_iap.entity.enums.InterviewStatus;
import com.example.it_iap.validator.annotation.EnumValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetInterviewHistoryRequest {
    private Long profileId;

    @EnumValue(enumClass = InterviewMode.class, message = "INTERVIEW_MODE_INVALID")
    private String mode;

    @EnumValue(enumClass = InterviewStatus.class, message = "INTERVIEW_STATUS_INVALID")
    private String status;

    private int pages;
}

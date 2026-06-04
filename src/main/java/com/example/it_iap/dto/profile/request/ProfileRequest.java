package com.example.it_iap.dto.profile.request;

import com.example.it_iap.entity.Json.ResumeData;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;
import com.example.it_iap.validator.annotation.EnumValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ProfileRequest {
    @NotBlank(message = "TITLE_INVALID")
    private String title;

    @EnumValue(enumClass = TargetPosition.class, message = "TARGET_POSITION_INVALID")
    @NotBlank(message = "TARGET_POSITION_INVALID")
    private String targetPosition;

    @EnumValue(enumClass = TargetLevel.class, message = "TARGET_LEVEL_INVALID")
    @NotBlank(message = "TARGET_LEVEL_INVALID")
    private String targetLevel;

    @Valid
    private ResumeData resumeData;
}

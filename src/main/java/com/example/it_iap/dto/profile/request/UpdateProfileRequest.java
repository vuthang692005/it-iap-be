package com.example.it_iap.dto.profile.request;

import com.example.it_iap.entity.Json.ResumeData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateProfileRequest {
    @NotBlank(message = "TITLE_INVALID")
    private String title;

    @Valid
    private ResumeData resumeData;
}

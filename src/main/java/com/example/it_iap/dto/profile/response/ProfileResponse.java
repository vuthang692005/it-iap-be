package com.example.it_iap.dto.profile.response;

import com.example.it_iap.entity.Json.ResumeData;
import com.example.it_iap.entity.enums.TargetLevel;
import com.example.it_iap.entity.enums.TargetPosition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProfileResponse {
    private long id;

    private String title;

    private String targetPosition;

    private String targetLevel;

    private ResumeData resumeData;
}

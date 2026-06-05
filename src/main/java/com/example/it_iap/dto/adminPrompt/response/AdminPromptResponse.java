package com.example.it_iap.dto.adminPrompt.response;

import com.example.it_iap.entity.enums.PromptUseCase;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminPromptResponse {
    private Long id;

    private String promptKey;

    private String description;

    private String version;

    private String provider;

    private String model;

    private String promptContent;

    private String note;

    private PromptUseCase applyFor;

    private boolean active;
}

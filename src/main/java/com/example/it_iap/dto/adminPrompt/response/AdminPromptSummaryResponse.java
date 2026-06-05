package com.example.it_iap.dto.adminPrompt.response;

import com.example.it_iap.entity.enums.PromptUseCase;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminPromptSummaryResponse {
    private Long id;

    private String promptKey;

    private String version;

    private String provider;

    private String model;

    private PromptUseCase applyFor;

    private boolean active;
}

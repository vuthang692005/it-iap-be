package com.example.it_iap.service;

import com.example.it_iap.dto.adminPrompt.response.AdminPromptResponse;
import com.example.it_iap.dto.promptVersion.request.PromptVersionIdRequest;
import com.example.it_iap.dto.promptVersion.request.PromptVersionRequest;
import com.example.it_iap.entity.AdminPrompt;
import com.example.it_iap.entity.PromptVersion;
import com.example.it_iap.entity.enums.PromptUseCase;

public interface PromptVersionService {
    PromptVersion createPromptVersion (PromptVersionRequest request, AdminPrompt adminPrompt);
    AdminPromptResponse getPromptVersion(PromptVersionIdRequest request);
    void setPromptVersionActive(PromptVersionIdRequest request);
    PromptVersion getPromptActive (PromptUseCase useCase);
}

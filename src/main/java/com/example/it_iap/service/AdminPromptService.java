package com.example.it_iap.service;

import com.example.it_iap.dto.adminPrompt.request.AdminPromptRequest;
import com.example.it_iap.dto.adminPrompt.response.AdminPromptResponse;
import com.example.it_iap.dto.adminPrompt.response.AdminPromptSummaryResponse;
import com.example.it_iap.dto.promptVersion.request.PromptVersionRequest;
import com.example.it_iap.entity.enums.PromptUseCase;
import org.springframework.data.domain.Page;

public interface AdminPromptService {
    AdminPromptSummaryResponse createAdminPrompt (AdminPromptRequest request);
    AdminPromptResponse addNewVersion (PromptVersionRequest request);
    Page<AdminPromptSummaryResponse> searchAdminPrompts(String promptKey, PromptUseCase applyFor, Boolean active, int pages);
}

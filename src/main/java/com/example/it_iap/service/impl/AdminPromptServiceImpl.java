package com.example.it_iap.service.impl;

import com.example.it_iap.dto.adminPrompt.request.AdminPromptRequest;
import com.example.it_iap.dto.adminPrompt.response.AdminPromptResponse;
import com.example.it_iap.dto.adminPrompt.response.AdminPromptSummaryResponse;
import com.example.it_iap.dto.promptVersion.request.PromptVersionRequest;
import com.example.it_iap.entity.AdminPrompt;
import com.example.it_iap.entity.PromptVersion;
import com.example.it_iap.entity.enums.PromptUseCase;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.AdminPromptRepository;
import com.example.it_iap.service.AdminPromptService;
import com.example.it_iap.service.PromptVersionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminPromptServiceImpl implements AdminPromptService {
    private final AdminPromptRepository adminPromptRepository;
    private final PromptVersionService promptVersionService;

    @Transactional
    public AdminPromptSummaryResponse createAdminPrompt (AdminPromptRequest request){
        if (adminPromptRepository.existsByPromptKey(request.getPromptKey())) {
            throw new AppException(ErrorCode.PROMPT_KEY_EXISTS);
        }

        PromptUseCase promptUseCase = PromptUseCase.from(request.getApplyFor());

        AdminPrompt adminPrompt = new AdminPrompt();
        adminPrompt.setPromptKey(request.getPromptKey());
        adminPrompt.setDescription(request.getDescription());
        adminPrompt.setApplyFor(promptUseCase);

        adminPrompt = adminPromptRepository.save(adminPrompt);

        PromptVersion promptVersion =
                promptVersionService.createPromptVersion(request.getPromptVersionRequest(), adminPrompt);

        return new AdminPromptSummaryResponse(
                adminPrompt.getId(),
                adminPrompt.getPromptKey(),
                promptVersion.getVersion(),
                promptVersion.getProvider(),
                promptVersion.getModel(),
                adminPrompt.getApplyFor(),
                request.getPromptVersionRequest().isActive()
        );
    }

    @Transactional
    public AdminPromptResponse addNewVersion (PromptVersionRequest request, long adminPromptId){
        AdminPrompt adminPrompt = adminPromptRepository.findById(adminPromptId)
                .orElseThrow(() -> new AppException(ErrorCode.PROMPT_NOT_FOUND));

        PromptVersion promptVersion = promptVersionService.createPromptVersion(request, adminPrompt);

        return new AdminPromptResponse(
                adminPrompt.getId(),
                adminPrompt.getPromptKey(),
                adminPrompt.getDescription(),
                promptVersion.getVersion(),
                promptVersion.getProvider(),
                promptVersion.getModel(),
                promptVersion.getPromptContent(),
                promptVersion.getNote(),
                adminPrompt.getApplyFor(),
                request.isActive()
        );
    }

    public Page<AdminPromptSummaryResponse> searchAdminPrompts(
            String promptKey,
            PromptUseCase applyFor,
            Boolean active,
            int pages) {

        int page = Math.max(0, pages - 1);
        int size = 10;
        PageRequest pageable = PageRequest.of(page, size);

        return adminPromptRepository.findAllSummaryWithFilters(promptKey, applyFor, active, pageable);
    }
}

package com.example.it_iap.service.impl;

import com.example.it_iap.dto.adminPrompt.request.AdminPromptRequest;
import com.example.it_iap.dto.adminPrompt.response.AdminPromptResponse;
import com.example.it_iap.dto.adminPrompt.response.AdminPromptSummaryResponse;
import com.example.it_iap.dto.promptVersion.request.PromptVersionRequest;
import com.example.it_iap.entity.AdminPrompt;
import com.example.it_iap.entity.PromptVersion;
import com.example.it_iap.entity.enums.AdminActionType;
import com.example.it_iap.entity.enums.PromptUseCase;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.AdminPromptRepository;
import com.example.it_iap.service.AdminActivityService;
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
    private final AdminActivityService adminActivityService;

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

        String desc = String.format("Tạo mới Prompt (Key: %s) kèm version đầu: %s",
                adminPrompt.getPromptKey(),
                promptVersion.getVersion());
        adminActivityService.logActivity(AdminActionType.CREATE_PROMPT, desc);

        if(request.getPromptVersionRequest().isActive()){
            String desc2 = String.format("Kích hoạt version %s của Prompt (Key: %s)",
                    promptVersion.getVersion(),
                    adminPrompt.getPromptKey());
            adminActivityService.logActivity(AdminActionType.ACTIVATE_PROMPT_VERSION, desc2);
        }

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
    public AdminPromptResponse addNewVersion (PromptVersionRequest request){
        AdminPrompt adminPrompt = adminPromptRepository.findById(request.getAdminPromptId())
                .orElseThrow(() -> new AppException(ErrorCode.PROMPT_NOT_FOUND));

        PromptVersion promptVersion = promptVersionService.createPromptVersion(request, adminPrompt);

        String desc = String.format("Thêm version mới (%s) cho Prompt (Key: %s)",
                promptVersion.getVersion(),
                adminPrompt.getPromptKey());
        adminActivityService.logActivity(AdminActionType.CREATE_PROMPT_VERSION, desc);

        if(request.isActive()){
            String desc2 = String.format("Kích hoạt version %s của Prompt (Key: %s)",
                    promptVersion.getVersion(),
                    adminPrompt.getPromptKey());
            adminActivityService.logActivity(AdminActionType.ACTIVATE_PROMPT_VERSION, desc2);
        }

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

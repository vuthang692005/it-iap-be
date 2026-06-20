package com.example.it_iap.service.impl;

import com.example.it_iap.dto.adminPrompt.response.AdminPromptResponse;
import com.example.it_iap.dto.promptVersion.request.PromptVersionIdRequest;
import com.example.it_iap.dto.promptVersion.request.PromptVersionRequest;
import com.example.it_iap.entity.AdminPrompt;
import com.example.it_iap.entity.PromptVersion;
import com.example.it_iap.entity.enums.ModelType;
import com.example.it_iap.entity.enums.PromptUseCase;
import com.example.it_iap.entity.enums.ProviderType;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.PromptVersionRepository;
import com.example.it_iap.service.PromptVersionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PromptVersionServiceImpl implements PromptVersionService {
    private final PromptVersionRepository promptVersionRepository;

    private void isValidProviderAndModel(String provider, String model) {
        ProviderType providerType = ProviderType.from(provider);
        ModelType modelType = ModelType.from(model);

        if(providerType == null || modelType == null || modelType.getProviderType() != providerType){
            throw new AppException(ErrorCode.PROVIDER_MODEL_MISMATCH);
        }
    }

    public PromptVersion createPromptVersion (PromptVersionRequest request, AdminPrompt adminPrompt){
        isValidProviderAndModel(request.getProvider(), request.getModel());

        // Check trùng version nội bộ của Prompt này trước khi lưu
        if (promptVersionRepository.existsByAdminPromptIdAndVersion(adminPrompt.getId(), request.getVersion())) {
            throw new AppException(ErrorCode.VERSION_EXISTS);
        }

        ModelType modelType = ModelType.from(request.getModel());

        PromptVersion promptVersion = new PromptVersion();
        promptVersion.setAdminPrompt(adminPrompt);
        promptVersion.setProvider(request.getProvider());
        promptVersion.setModel(modelType.getValue());
        promptVersion.setVersion(request.getVersion());
        promptVersion.setNote(request.getNote());
        promptVersion.setPromptContent(request.getPromptContent());

        // Xử lý cờ active
        if(request.isActive()) {
            promptVersion.setLastActivatedAt(LocalDateTime.now());
        }

        return promptVersionRepository.save(promptVersion);
    }

    public AdminPromptResponse getPromptVersion(PromptVersionIdRequest request) {
        PromptVersion promptVersion = promptVersionRepository
                .findByAdminPromptPromptKeyAndVersion(request.getPromptKey(), request.getVersion())
                .orElseThrow(() -> new AppException(ErrorCode.PROMPT_NOT_FOUND));

        AdminPrompt adminPrompt = promptVersion.getAdminPrompt();
        boolean active = promptVersionRepository.isActiveVersion(promptVersion.getId());

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
                active
        );
    }

    @Transactional
    public void setPromptVersionActive(PromptVersionIdRequest request){
        PromptVersion promptVersion = promptVersionRepository
                .findByAdminPromptPromptKeyAndVersion(request.getPromptKey(), request.getVersion())
                .orElseThrow(() -> new AppException(ErrorCode.PROMPT_NOT_FOUND));

        promptVersion.setLastActivatedAt(LocalDateTime.now());
        promptVersionRepository.save(promptVersion);
    }

    public PromptVersion getPromptActive (PromptUseCase useCase){
        return promptVersionRepository.findFirstByAdminPromptApplyForAndLastActivatedAtIsNotNullOrderByLastActivatedAtDesc(useCase)
                .orElseThrow(() -> new AppException(ErrorCode.ACTIVE_PROMPT_NOT_FOUND));
    }
}

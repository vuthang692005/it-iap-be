package com.example.it_iap.dto.adminPrompt.request;

import com.example.it_iap.dto.promptVersion.request.PromptVersionRequest;
import com.example.it_iap.entity.enums.PromptUseCase;
import com.example.it_iap.validator.annotation.EnumValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AdminPromptRequest {

    @NotBlank(message = "PROMPT_KEY_INVALID")
    @Size(max = 100, message = "PROMPT_KEY_INVALID")
    private String promptKey;

    private String description;

    @NotBlank(message = "APPLY_FOR_INVALID")
    @EnumValue(enumClass = PromptUseCase.class, message = "APPLY_FOR_INVALID")
    private String applyFor;

    @Valid
    private PromptVersionRequest promptVersionRequest;
}

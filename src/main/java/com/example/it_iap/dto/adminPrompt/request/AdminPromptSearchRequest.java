package com.example.it_iap.dto.adminPrompt.request;

import com.example.it_iap.entity.enums.PromptUseCase;
import com.example.it_iap.validator.annotation.EnumValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminPromptSearchRequest {
    private String promptKey;

    @EnumValue(enumClass = PromptUseCase.class, message = "PROMPT_USE_CASE_INVALID")
    private String applyFor;

    private Boolean active;

    private int pages = 1;
}

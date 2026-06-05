package com.example.it_iap.dto.promptVersion.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PromptVersionIdRequest {
    @NotBlank(message = "PROMPT_KEY_INVALID")
    private String promptKey;

    @NotBlank(message = "VERSION_INVALID")
    private String version;
}

package com.example.it_iap.dto.promptVersion.request;

import com.example.it_iap.entity.enums.ModelType;
import com.example.it_iap.entity.enums.ProviderType;
import com.example.it_iap.validator.annotation.EnumValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromptVersionRequest {
    @NotBlank(message = "VERSION_INVALID")
    @Size(max = 20, message = "VERSION_INVALID")
    private String version;

    @NotBlank(message = "PROVIDER_INVALID")
    @EnumValue(enumClass = ProviderType.class, message = "PROVIDER_INVALID")
    private String provider;

    @NotBlank(message = "MODEL_INVALID")
    @EnumValue(enumClass = ModelType.class, message = "MODEL_INVALID")
    private String model;

    @NotBlank(message = "PROMPT_CONTENT_INVALID")
    private String promptContent;

    private String note;

    private boolean active;
}

package com.example.it_iap.dto.user.request;

import com.example.it_iap.validator.annotation.Gmail;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChangeEmailRequest {
    @NotBlank(message = "EMAIL_INVALID")
    @Gmail
    private String newEmail;
}

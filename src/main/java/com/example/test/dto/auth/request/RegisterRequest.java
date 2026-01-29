package com.example.test.dto.auth.request;

import com.example.test.validator.annotation.Gmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RegisterRequest {
    @Gmail
    @NotBlank(message = "EMAIL_INVALID")
    private String email;

    @NotBlank(message = "PASSWORD_INVALID")
    @Size(min = 8,max = 50, message = "PASSWORD_INVALID")
    private String password;

    @NotBlank(message = "FULL_NAME_INVALID")
    @Size(max = 30, message = "FULL_NAME_INVALID")
    private String fullName;
}

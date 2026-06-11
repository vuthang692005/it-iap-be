package com.example.it_iap.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "PASSWORD_INVALID")
    private String oldPassword;

    @NotBlank(message = "PASSWORD_INVALID")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$", message = "PASSWORD_INVALID")
    private String newPassword;
}

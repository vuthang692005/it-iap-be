package com.example.it_iap.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "EMAIL_INVALID")
    private String email;
    @NotBlank(message = "PASSWORD_INVALID")
    @Size(min = 8, max = 50, message = "PASSWORD_INVALID")
    private String password;
}

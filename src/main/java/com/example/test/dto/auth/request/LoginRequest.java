package com.example.test.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "IDENTIFIER_INVALID")
    private String identifier;
    @NotBlank(message = "PASSWORD_INVALID")
    @Size(min = 8, max = 50, message = "PASSWORD_INVALID")
    private String password;
}

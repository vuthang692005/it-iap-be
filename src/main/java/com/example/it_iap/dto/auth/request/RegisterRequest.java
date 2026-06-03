package com.example.it_iap.dto.auth.request;

import com.example.it_iap.validator.annotation.Gmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "^0\\d{9}$", message = "PHONE_NUMBER_INVALID")
    private String phoneNumber;
}

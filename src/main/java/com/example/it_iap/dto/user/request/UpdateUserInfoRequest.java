package com.example.it_iap.dto.user.request;

import com.example.it_iap.validator.annotation.Gmail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserInfoRequest {
    @Gmail
    @NotBlank(message = "EMAIL_INVALID")
    String email;

    @Pattern(regexp = "^0\\d{9}$", message = "PHONE_NUMBER_INVALID")
    String phoneNumber;
    
    @NotBlank(message = "FULL_NAME_INVALID")
    @Size(max = 30, message = "FULL_NAME_INVALID")
    String fullName;
}

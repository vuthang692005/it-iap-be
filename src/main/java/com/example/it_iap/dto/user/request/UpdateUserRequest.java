package com.example.it_iap.dto.user.request;

import org.hibernate.validator.constraints.URL;

import com.example.it_iap.validator.annotation.Gmail;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {
    @Gmail
    @NotBlank(message = "EMAIL_INVALID")
    String email;

    @NotBlank(message = "FULL_NAME_INVALID")
    @Size(max = 30, message = "FULL_NAME_INVALID")
    String fullName;

    @Pattern(regexp = "^0\\d{9}$", message = "PHONE_NUMBER_INVALID")
    String phoneNumber;

    @URL(message = "AVATAR_URL_INVALID")
    String avatarUrl;

    boolean active;    
}

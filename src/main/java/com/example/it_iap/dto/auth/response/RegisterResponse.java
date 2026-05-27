package com.example.it_iap.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class RegisterResponse {
    private UUID userId;
}

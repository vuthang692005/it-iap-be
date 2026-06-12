package com.example.it_iap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UploadFolder {
    USER_AVATAR("users/avatars"),
    ;
    private final String folder;
}

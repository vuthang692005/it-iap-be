package com.example.it_iap.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UploadFolder {
    USER_AVATAR("users/avatars"),
    FEEDBACK_IMAGE("feedbacks")
    ;
    private final String folder;
}

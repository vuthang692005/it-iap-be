package com.example.it_iap.util;

import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public class SecurityUtils {
    public static UUID getCurrentUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getName)
                .map(name -> {
                    try {
                        return UUID.fromString(name);
                    } catch (IllegalArgumentException e) {
                        // Bắt lỗi nếu subject là email (token cũ) hoặc "anonymousUser"
                        return null;
                    }
                })
                .orElseThrow(() -> new AppException(ErrorCode.AUTHENTICATION_FAILED));
    }
}

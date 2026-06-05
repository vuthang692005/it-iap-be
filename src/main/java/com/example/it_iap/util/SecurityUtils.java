package com.example.it_iap.util;

import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtils {
    public static String getCurrentUserEmail() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getName)
                .orElseThrow(() -> new AppException(ErrorCode.AUTHENTICATION_FAILED));
    }
}

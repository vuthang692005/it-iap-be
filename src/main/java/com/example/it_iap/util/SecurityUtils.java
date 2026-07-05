package com.example.it_iap.util;

import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public static List<String> getCurrentUserRoles() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getAuthorities)
                .map(authorities -> authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    public static boolean isAdmin() {
        List<String> roles = getCurrentUserRoles();

        // Kiểm tra các trường hợp prefix thường gặp do Spring Security sinh ra từ claim "scope"
        return roles.stream()
                .anyMatch(role -> role.equals("ADMIN") ||
                        role.equals("ROLE_ADMIN") ||
                        role.equals("SCOPE_ADMIN"));
    }
}

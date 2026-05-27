package com.example.it_iap.service.impl;

import com.example.it_iap.enums.CookieKey;
import com.example.it_iap.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CookieServiceImpl implements CookieService {
    @Value("${app.cookie.secure}")
    private boolean secure;

    public void add(HttpServletResponse response, CookieKey cookieKey, String value) {
        ResponseCookie cookie = ResponseCookie.from(cookieKey.getName(), value)
                .httpOnly(true)
                .secure(secure)
                .path(cookieKey.getPath())
                .maxAge(cookieKey.getMaxAgeSeconds())
                .sameSite("Lax") // Ngăn chặn CSRF
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clear(HttpServletResponse response, CookieKey cookieKey) {
        ResponseCookie cookie = ResponseCookie.from(cookieKey.getName(), "")
                .httpOnly(true)
                .secure(secure)
                .path(cookieKey.getPath())
                .maxAge(0) // Xóa cookie
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public String get(HttpServletRequest request, CookieKey cookieKey) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieKey.getName().equals(cookie.getName()))
                .map(jakarta.servlet.http.Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}

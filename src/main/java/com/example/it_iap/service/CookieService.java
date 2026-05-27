package com.example.it_iap.service;

import com.example.it_iap.enums.CookieKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
    void add(HttpServletResponse response, CookieKey cookieKey, String value);
    void clear(HttpServletResponse response, CookieKey cookieKey);
    String get(HttpServletRequest request, CookieKey cookieKey);
}

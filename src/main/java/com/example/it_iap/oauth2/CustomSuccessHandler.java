package com.example.it_iap.oauth2;

import com.example.it_iap.entity.User;
import com.example.it_iap.enums.CookieKey;
import com.example.it_iap.service.CookieService;
import com.example.it_iap.service.SessionService;
import com.example.it_iap.service.TokenService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final TokenService tokenService;
    private final CookieService cookieService;
    private final SessionService sessionService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User principal =
                (CustomOAuth2User) authentication.getPrincipal();

        User user = principal.getUser();

        if (!user.isActive()) {
            response.sendRedirect(frontendUrl + "auth/login?error=account_disabled");
            return;
        }

        try {
            String sessionId = UUID.randomUUID().toString();
            String refreshTokenId = UUID.randomUUID().toString();
            sessionService.createSession(user, request, sessionId, refreshTokenId);

            String accessToken = tokenService.generateAccessToken(user, sessionId);
            String refreshToken = tokenService.generateRefreshToken(user, sessionId, refreshTokenId);

            cookieService.add(response, CookieKey.ACCESS_TOKEN, accessToken);
            cookieService.add(response, CookieKey.REFRESH_TOKEN, refreshToken);
        } catch (JOSEException e) {
            log.error("Lỗi ký Token OAuth2: ", e);
            response.sendRedirect(frontendUrl + "auth/login?error=auth_failed");
        }

        response.sendRedirect(frontendUrl);
    }
}

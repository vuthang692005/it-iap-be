package com.example.test.oauth2;

import com.example.test.entity.User;
import com.example.test.enums.CookieKey;
import com.example.test.service.CookieService;
import com.example.test.service.TokenService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final TokenService tokenService;
    private final CookieService cookieService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User principal =
                (CustomOAuth2User) authentication.getPrincipal();

        User user = principal.getUser();

        try {
            String accessToken = tokenService.generateAccessToken(user);
            String refreshToken = tokenService.generateRefreshToken(user);

            cookieService.add(response, CookieKey.ACCESS_TOKEN, accessToken);
            cookieService.add(response, CookieKey.REFRESH_TOKEN, refreshToken);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        response.sendRedirect(frontendUrl);
    }
}

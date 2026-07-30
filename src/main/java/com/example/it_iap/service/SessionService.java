package com.example.it_iap.service;

import com.example.it_iap.dto.session.response.UserSessionResponse;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.UserSession;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.UUID;

public interface SessionService {
    UserSession createSession(User user, HttpServletRequest request, String sessionId, String refreshTokenJti);

    List<UserSessionResponse> getActiveSessions(User user, String currentSessionId);

    void revokeSession(User user, String sessionId);

    void revokeOtherSessions(User user, String currentSessionId);

    void updateLastActive(String sessionId);

    boolean isSessionActive(UUID userId, String sessionId);

    void deactivateSessionByJti(UUID userId, String refreshTokenJti);
}

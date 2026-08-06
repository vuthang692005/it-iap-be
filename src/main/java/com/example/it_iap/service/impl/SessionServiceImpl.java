package com.example.it_iap.service.impl;

import com.example.it_iap.cache.CacheRepository;
import com.example.it_iap.dto.session.response.UserSessionResponse;
import com.example.it_iap.entity.User;
import com.example.it_iap.entity.UserSession;
import com.example.it_iap.exception.AppException;
import com.example.it_iap.exception.ErrorCode;
import com.example.it_iap.repository.UserSessionRepository;
import com.example.it_iap.service.SessionService;
import com.example.it_iap.util.NetworkUtils;
import com.example.it_iap.util.UserAgentUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "SessionServiceImpl")
public class SessionServiceImpl implements SessionService {
    private final UserSessionRepository userSessionRepository;
    private final CacheRepository cacheRepository;
    private final UserAgentUtils userAgentUtils;

    private static final String SESSION_KEY_PREFIX = "session:";
    private static final String WHITELIST_PREFIX = "auth:token:white:";

    @Override
    @Transactional
    public UserSession createSession(User user, HttpServletRequest request, String sessionId, String refreshTokenJti) {
        String ipAddress = NetworkUtils.getClientIp(request);
        String userAgentHeader = request != null ? request.getHeader("User-Agent") : null;
        UserAgentUtils.DeviceInfo deviceInfo = userAgentUtils.parseUserAgent(userAgentHeader);
        String location = userAgentUtils.getLocation(ipAddress);

        LocalDateTime now = LocalDateTime.now();
        UserSession session = UserSession.builder()
                .id(sessionId)
                .user(user)
                .refreshTokenJti(refreshTokenJti)
                .deviceType(deviceInfo.deviceType())
                .osName(deviceInfo.osName())
                .browserName(deviceInfo.browserName())
                .ipAddress(ipAddress)
                .location(location)
                .isActive(true)
                .createdAt(now)
                .lastActiveAt(now)
                .expiresAt(now.plusDays(7))
                .build();

        userSessionRepository.save(session);

        // Lưu session vào Redis (TTL 7 ngày)
        String sessionKey = SESSION_KEY_PREFIX + user.getId() + ":" + sessionId;
        cacheRepository.save(sessionKey, refreshTokenJti, Duration.ofDays(7));

        return session;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSessionResponse> getActiveSessions(User user, String currentSessionId) {
        List<UserSession> activeSessions = userSessionRepository.findByUserIdAndIsActiveTrueOrderByLastActiveAtDesc(user.getId());

        return activeSessions.stream()
                .map(session -> UserSessionResponse.builder()
                        .id(session.getId())
                        .deviceType(session.getDeviceType())
                        .osName(session.getOsName())
                        .browserName(session.getBrowserName())
                        .ipAddress(session.getIpAddress())
                        .location(session.getLocation())
                        .lastActiveAt(session.getLastActiveAt())
                        .isCurrent(session.getId().equals(currentSessionId))
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void revokeSession(User user, String sessionId) {
        UserSession session = userSessionRepository.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.SESSION_NOT_FOUND));

        // Xóa Key Session và Refresh Token khỏi Whitelist trên Redis
        String sessionKey = SESSION_KEY_PREFIX + user.getId() + ":" + sessionId;
        cacheRepository.delete(sessionKey);

        String whitelistKey = WHITELIST_PREFIX + user.getId();
        cacheRepository.removeFromSet(whitelistKey, session.getRefreshTokenJti());

        // Deactivate trong MySQL
        userSessionRepository.deactivateSession(sessionId, user.getId());
    }

    @Override
    @Transactional
    public void revokeOtherSessions(User user, String currentSessionId) {
        List<UserSession> activeSessions = userSessionRepository.findByUserIdAndIsActiveTrueOrderByLastActiveAtDesc(user.getId());

        String whitelistKey = WHITELIST_PREFIX + user.getId();
        for (UserSession session : activeSessions) {
            if (!session.getId().equals(currentSessionId)) {
                String sessionKey = SESSION_KEY_PREFIX + user.getId() + ":" + session.getId();
                cacheRepository.delete(sessionKey);
                cacheRepository.removeFromSet(whitelistKey, session.getRefreshTokenJti());
            }
        }

        userSessionRepository.deactivateOtherSessions(user.getId(), currentSessionId);
    }

    @Override
    @Transactional
    public void updateLastActive(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return;
        }
        userSessionRepository.findById(sessionId).ifPresent(session -> {
            session.setLastActiveAt(LocalDateTime.now());
            userSessionRepository.save(session);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSessionActive(UUID userId, String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return false;
        }
        String sessionKey = SESSION_KEY_PREFIX + userId + ":" + sessionId;
        if (cacheRepository.exists(sessionKey)) {
            return true;
        }
        return userSessionRepository.findByIdAndUserId(sessionId, userId)
                .map(UserSession::isActive)
                .orElse(false);
    }

    @Override
    @Transactional
    public void deactivateSessionByJti(UUID userId, String refreshTokenJti) {
        if (refreshTokenJti == null || refreshTokenJti.isBlank()) {
            return;
        }
        userSessionRepository.findByRefreshTokenJti(refreshTokenJti).ifPresent(session -> {
            session.setActive(false);
            userSessionRepository.save(session);
            String sessionKey = SESSION_KEY_PREFIX + userId + ":" + session.getId();
            cacheRepository.delete(sessionKey);
        });
    }
}

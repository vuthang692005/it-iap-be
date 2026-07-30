package com.example.it_iap.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions", indexes = {
        @Index(name = "idx_user_sessions_user_active", columnList = "user_id, is_active")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSession {
    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "refresh_token_jti", nullable = false)
    private String refreshTokenJti;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @Column(name = "os_name", length = 50)
    private String osName;

    @Column(name = "browser_name", length = 50)
    private String browserName;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "location", length = 100)
    private String location;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_active_at", nullable = false)
    private LocalDateTime lastActiveAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (lastActiveAt == null) {
            lastActiveAt = now;
        }
        if (expiresAt == null) {
            expiresAt = now.plusDays(7);
        }
    }
}

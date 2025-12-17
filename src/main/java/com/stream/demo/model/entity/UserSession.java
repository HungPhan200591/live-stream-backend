package com.stream.demo.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * UserSession Entity
 * <p>
 * Session là source of truth cho authentication.
 * JWT Refresh Token chứa session_id để validate session từ DB.
 * Logout = Revoke session trong DB (không dùng JWT blacklist).
 */
@Entity
@Table(name = "user_sessions", indexes = {
        @Index(name = "idx_sessions_user_id", columnList = "user_id"),
        @Index(name = "idx_sessions_status", columnList = "status"),
        @Index(name = "idx_sessions_expires", columnList = "expires_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "session_id", columnDefinition = "UUID")
    private UUID sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "device_id", length = 255)
    private String deviceId;

    @Column(name = "device_name", length = 255)
    private String deviceName;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private SessionStatus status = SessionStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_used_at", nullable = false)
    @Builder.Default
    private LocalDateTime lastUsedAt = LocalDateTime.now();

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Session Status Enum
     */
    public enum SessionStatus {
        ACTIVE, // Session đang hoạt động
        REVOKED // Session đã bị revoke (logout)
    }

    /**
     * Check if session is valid
     * Session hợp lệ khi: status = ACTIVE và chưa hết hạn
     */
    public boolean isValid() {
        return status == SessionStatus.ACTIVE
                && expiresAt.isAfter(LocalDateTime.now());
    }

    /**
     * JPA lifecycle callback để set createdAt
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (lastUsedAt == null) {
            lastUsedAt = LocalDateTime.now();
        }
    }
}

package com.stream.demo.service;

import com.stream.demo.model.dto.cache.SessionCacheDTO;
import com.stream.demo.model.entity.UserSession;
import com.stream.demo.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Session Service
 * <p>
 * Service quản lý user sessions trong database.
 * Session là source of truth cho authentication, JWT chỉ là carrier.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {

    private static final int MAX_SESSIONS_PER_USER = 5;

    private final UserSessionRepository sessionRepository;
    private final SessionCacheService sessionCacheService;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    /**
     * Tạo session mới khi user login
     * <p>
     * Nếu user đã có >= MAX_SESSIONS_PER_USER active sessions,
     * tự động revoke session cũ nhất (theo lastUsedAt).
     *
     * @param userId     User ID
     * @param deviceId   Device identifier (browser fingerprint, mobile device ID,
     *                   etc.)
     * @param deviceName Device name (e.g., "Chrome on Windows", "iPhone 13")
     * @param ipAddress  IP address của user
     * @return UserSession đã được lưu trong DB
     */
    public UserSession createSession(Long userId, String deviceId, String deviceName, String ipAddress) {
        // Check max sessions limit - revoke oldest if exceeded
        enforceMaxSessionsLimit(userId);

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusNanos(refreshExpirationMs * 1_000_000); // Convert ms to nanos

        UserSession session = UserSession.builder()
                .userId(userId)
                .deviceId(deviceId)
                .deviceName(deviceName)
                .ipAddress(ipAddress)
                .status(UserSession.SessionStatus.ACTIVE)
                .expiresAt(expiresAt)
                .build();

        UserSession savedSession = sessionRepository.save(session);

        // Cache session vào Redis
        sessionCacheService.cacheSession(savedSession);

        return savedSession;
    }

    /**
     * Enforce max sessions limit per user.
     * Nếu user đã có >= MAX_SESSIONS thì revoke session cũ nhất.
     */
    private void enforceMaxSessionsLimit(Long userId) {
        long activeSessionCount = sessionRepository.countByUserIdAndStatus(
                userId, UserSession.SessionStatus.ACTIVE);

        if (activeSessionCount >= MAX_SESSIONS_PER_USER) {
            // Revoke oldest session (theo lastUsedAt)
            sessionRepository.findTopByUserIdAndStatusOrderByLastUsedAtAsc(
                    userId, UserSession.SessionStatus.ACTIVE)
                    .ifPresent(oldestSession -> revokeSession(oldestSession.getSessionId()));
        }
    }

    /**
     * Validate session từ database
     * <p>
     * Check session có tồn tại, status = ACTIVE, và chưa hết hạn.
     * Update last_used_at nếu session hợp lệ.
     *
     * @param sessionId Session UUID từ refresh token
     * @return UserSession nếu hợp lệ
     * @throws IllegalArgumentException nếu session không hợp lệ
     */
    public UserSession validateSession(UUID sessionId) {
        // Try cache first (fast path)
        Optional<SessionCacheDTO> cachedDto = sessionCacheService.getSessionFromCache(sessionId);

        if (cachedDto.isPresent()) {
            if (cachedDto.get().isValid()) {
                // Cache hit và valid - query DB để update lastUsedAt
                UserSession session = sessionRepository.findBySessionId(sessionId)
                        .orElseThrow(() -> new IllegalArgumentException("Session not found in DB"));

                // Update last_used_at
                session.setLastUsedAt(LocalDateTime.now());
                sessionRepository.save(session);
                sessionCacheService.cacheSession(session); // Update cache
                return session;
            } else {
                // Session invalid trong cache, invalidate cache
                sessionCacheService.invalidateSession(sessionId);
                throw new IllegalArgumentException("Session expired or revoked");
            }
        }

        // Cache miss - query từ DB (slow path)
        UserSession session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.isValid()) {
            throw new IllegalArgumentException("Session expired or revoked");
        }

        // Update last_used_at
        session.setLastUsedAt(LocalDateTime.now());
        sessionRepository.save(session);

        // Populate cache
        sessionCacheService.cacheSession(session);

        return session;
    }

    /**
     * Revoke session (logout single device)
     * <p>
     * Set session status = REVOKED trong DB.
     * Refresh token sẽ invalid ngay lập tức.
     *
     * @param sessionId Session UUID cần revoke
     */
    public void revokeSession(UUID sessionId) {
        sessionRepository.updateStatusBySessionId(sessionId, UserSession.SessionStatus.REVOKED);
        // Invalidate cache
        sessionCacheService.invalidateSession(sessionId);
    }

    /**
     * Revoke tất cả sessions của user (logout all devices)
     * <p>
     * Set tất cả sessions của user thành REVOKED.
     * User sẽ bị logout khỏi tất cả devices.
     *
     * @param userId User ID
     */
    public void revokeAllUserSessions(Long userId) {
        sessionRepository.updateStatusByUserId(userId, UserSession.SessionStatus.REVOKED);
    }

    /**
     * Lấy danh sách active sessions của user
     * <p>
     * Dùng để hiển thị "Manage devices" trong UI
     *
     * @param userId User ID
     * @return List of active sessions
     */
    public List<UserSession> getActiveSessions(Long userId) {
        return sessionRepository.findByUserIdAndStatus(userId, UserSession.SessionStatus.ACTIVE);
    }
}

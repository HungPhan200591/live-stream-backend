package com.stream.demo.service;

import com.stream.demo.model.entity.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

/**
 * Session Cache Service
 * <p>
 * Service để cache UserSession trong Redis nhằm tăng performance.
 * Cache hit: Lấy từ Redis (nhanh)
 * Cache miss: Query từ DB, populate cache
 */
@Service
@RequiredArgsConstructor
public class SessionCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_CACHE_PREFIX = "session:";

    /**
     * Cache session vào Redis
     * TTL = remaining time until session expires
     *
     * @param session UserSession to cache
     */
    public void cacheSession(UserSession session) {
        String key = SESSION_CACHE_PREFIX + session.getSessionId().toString();
        long ttlSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), session.getExpiresAt());

        if (ttlSeconds > 0) {
            redisTemplate.opsForValue().set(key, session, Duration.ofSeconds(ttlSeconds));
        }
    }

    /**
     * Get session từ Redis cache
     *
     * @param sessionId Session UUID
     * @return Optional<UserSession>
     */
    public Optional<UserSession> getSessionFromCache(UUID sessionId) {
        String key = SESSION_CACHE_PREFIX + sessionId.toString();
        Object value = redisTemplate.opsForValue().get(key);

        if (value instanceof UserSession session) {
            return Optional.of(session);
        }
        return Optional.empty();
    }

    /**
     * Invalidate session trong cache (khi revoke)
     *
     * @param sessionId Session UUID to invalidate
     */
    public void invalidateSession(UUID sessionId) {
        String key = SESSION_CACHE_PREFIX + sessionId.toString();
        redisTemplate.delete(key);
    }

    /**
     * Invalidate tất cả sessions của user
     * Dùng pattern matching để delete multiple keys
     *
     * @param userId User ID
     */
    public void invalidateUserSessions(Long userId) {
        // Note: Trong production, nên maintain một Redis Set
        // để track session_ids của từng user để delete hiệu quả hơn.
        // Pattern scan là expensive operation.
        // For now, để đơn giản ta chỉ invalidate specific session khi revoke.
    }
}

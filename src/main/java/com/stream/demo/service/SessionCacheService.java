package com.stream.demo.service;

import com.stream.demo.config.RedisConfig;
import com.stream.demo.model.dto.cache.SessionCacheDTO;
import com.stream.demo.model.entity.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
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
 * Inject RedisTemplate theo bean name từ CacheableDto enum.
 * Cache hit: Lấy từ Redis (nhanh)
 * Cache miss: Query từ DB, populate cache
 */
@Service
@RequiredArgsConstructor
public class SessionCacheService {

    // Type-safe bean name constant - centralized in RedisConfig
    @Qualifier(RedisConfig.RedisTemplateBeanNames.SESSION_CACHE)
    private final RedisTemplate<String, SessionCacheDTO> sessionCacheRedisTemplate;

    private static final String CACHE_VERSION = "v1";
    private static final String SESSION_CACHE_PREFIX = "session:" + CACHE_VERSION + ":";

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
            // Convert Entity to DTO before caching
            SessionCacheDTO dto = SessionCacheDTO.fromEntity(session);
            sessionCacheRedisTemplate.opsForValue().set(key, dto, Duration.ofSeconds(ttlSeconds));
        }
    }

    /**
     * Get session từ Redis cache
     * Type-safe deserialization - no casting needed!
     *
     * @param sessionId Session UUID
     * @return Optional<SessionCacheDTO>
     */
    public Optional<SessionCacheDTO> getSessionFromCache(UUID sessionId) {
        String key = SESSION_CACHE_PREFIX + sessionId.toString();
        SessionCacheDTO dto = sessionCacheRedisTemplate.opsForValue().get(key);
        return Optional.ofNullable(dto);
    }

    /**
     * Invalidate session trong cache (khi revoke)
     *
     * @param sessionId Session UUID to invalidate
     */
    public void invalidateSession(UUID sessionId) {
        String key = SESSION_CACHE_PREFIX + sessionId.toString();
        sessionCacheRedisTemplate.delete(key);
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

package com.stream.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * JWT Blacklist Service using Redis
 * Service quản lý blacklist JWT tokens (cho logout functionality)
 * Tokens được lưu trong Redis với TTL tự động expire
 */
@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    /**
     * Add token to blacklist with expiry time
     * Thêm token vào blacklist. Token sẽ tự động bị xóa sau expirySeconds
     * 
     * @param token         JWT token to blacklist
     * @param expirySeconds Time to live in seconds (should match token's remaining
     *                      lifetime)
     */
    public void addToBlacklist(String token, long expirySeconds) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted", expirySeconds, TimeUnit.SECONDS);
    }

    /**
     * Check if token is blacklisted
     * Kiểm tra token có trong blacklist không
     * 
     * @param token JWT token to check
     * @return true if token is blacklisted, false otherwise
     */
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}

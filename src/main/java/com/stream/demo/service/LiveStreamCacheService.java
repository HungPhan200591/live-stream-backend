package com.stream.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * LiveStream Cache Service
 * <p>
 * Quản lý các operations Redis cho livestream:
 * - HyperLogLog cho unique viewer counting
 * - Live status caching
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LiveStreamCacheService {

    private final StringRedisTemplate stringRedisTemplate;

    // Cache key patterns
    private static final String VIEWER_KEY_PATTERN = "stream:%d:viewers";
    private static final String LIVE_STATUS_KEY_PATTERN = "stream:%d:live";

    // TTLs
    private static final Duration LIVE_STATUS_TTL = Duration.ofHours(24);

    // ============================================================
    // Viewer Tracking (HyperLogLog)
    // ============================================================

    /**
     * Track một viewer cho stream (HyperLogLog PFADD)
     * HyperLogLog chỉ đếm unique values với ~0.81% standard error
     *
     * @param streamId ID của stream
     * @param userId   ID của viewer (có thể là sessionId cho anonymous)
     */
    public void addViewer(Long streamId, String userId) {
        String key = String.format(VIEWER_KEY_PATTERN, streamId);
        Long added = stringRedisTemplate.opsForHyperLogLog().add(key, userId);
        if (added > 0) {
            log.debug("New unique viewer added to stream {}: {}", streamId, userId);
        }
    }

    /**
     * Lấy số lượng unique viewers của stream (HyperLogLog PFCOUNT)
     *
     * @param streamId ID của stream
     * @return Số lượng ước tính unique viewers
     */
    public Long getViewerCount(Long streamId) {
        String key = String.format(VIEWER_KEY_PATTERN, streamId);
	    return stringRedisTemplate.opsForHyperLogLog().size(key);
    }

    /**
     * Reset viewer count cho stream (khi stream kết thúc hoặc cần clear)
     */
    public void resetViewerCount(Long streamId) {
        String key = String.format(VIEWER_KEY_PATTERN, streamId);
        stringRedisTemplate.delete(key);
        log.info("Viewer count reset for stream {}", streamId);
    }

    // ============================================================
    // Live Status Caching
    // ============================================================

    /**
     * Set trạng thái live của stream vào cache
     *
     * @param streamId ID của stream
     * @param isLive   true nếu đang live, false để xóa khỏi cache
     */
    public void setLiveStatus(Long streamId, boolean isLive) {
        String key = String.format(LIVE_STATUS_KEY_PATTERN, streamId);
        if (isLive) {
            stringRedisTemplate.opsForValue().set(key, "true", LIVE_STATUS_TTL);
            log.info("Stream {} live status set to TRUE in cache", streamId);
        } else {
            stringRedisTemplate.delete(key);
            log.info("Stream {} live status removed from cache", streamId);
        }
    }

    /**
     * Kiểm tra stream có đang live không (từ cache)
     *
     * @param streamId ID của stream
     * @return true nếu stream đang live (trong cache)
     */
    public boolean isLive(Long streamId) {
        String key = String.format(LIVE_STATUS_KEY_PATTERN, streamId);
        return stringRedisTemplate.hasKey(key);
    }
}

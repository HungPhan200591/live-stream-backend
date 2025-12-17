package com.stream.demo.scheduler;

import com.stream.demo.model.entity.UserSession;
import com.stream.demo.repository.UserSessionRepository;
import com.stream.demo.service.SessionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Session Cleanup Scheduler
 * <p>
 * Background job chạy định kỳ để cleanup expired sessions.
 * Revoke sessions có status = ACTIVE nhưng đã hết hạn (expiresAt < now).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SessionCleanupScheduler {

    private final UserSessionRepository sessionRepository;
    private final SessionCacheService sessionCacheService;

    /**
     * Cleanup expired sessions
     * Chạy mỗi 30 phút
     */
    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void cleanupExpiredSessions() {
        log.info("Starting session cleanup job...");

        LocalDateTime now = LocalDateTime.now();

        // Tìm sessions đã hết hạn nhưng vẫn còn status ACTIVE
        List<UserSession> expiredSessions = sessionRepository.findByStatusAndExpiresAtBefore(
                UserSession.SessionStatus.ACTIVE, now);

        if (expiredSessions.isEmpty()) {
            log.info("No expired sessions to cleanup");
            return;
        }

        // Revoke từng session và xóa cache
        int cleanedCount = 0;
        for (UserSession session : expiredSessions) {
            session.setStatus(UserSession.SessionStatus.REVOKED);
            sessionRepository.save(session);
            sessionCacheService.invalidateSession(session.getSessionId());
            cleanedCount++;
        }

        log.info("Cleaned up {} expired sessions", cleanedCount);
    }
}

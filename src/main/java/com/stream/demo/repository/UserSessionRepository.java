package com.stream.demo.repository;

import com.stream.demo.model.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * UserSession Repository
 * Repository để quản lý user sessions trong database
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    /**
     * Tìm session theo session ID
     */
    Optional<UserSession> findBySessionId(UUID sessionId);

    /**
     * Tìm tất cả sessions của một user
     */
    List<UserSession> findByUserId(Long userId);

    /**
     * Tìm sessions theo userId và status
     */
    List<UserSession> findByUserIdAndStatus(Long userId, UserSession.SessionStatus status);

    /**
     * Update status của tất cả sessions của user
     * Dùng cho logout all devices
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.status = :status WHERE s.userId = :userId")
    int updateStatusByUserId(@Param("userId") Long userId, @Param("status") UserSession.SessionStatus status);

    /**
     * Update status của một session cụ thể
     * Dùng cho logout single device
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.status = :status WHERE s.sessionId = :sessionId")
    int updateStatusBySessionId(@Param("sessionId") UUID sessionId, @Param("status") UserSession.SessionStatus status);

    /**
     * Đếm số sessions active của user
     * Dùng để check max sessions limit
     */
    long countByUserIdAndStatus(Long userId, UserSession.SessionStatus status);

    /**
     * Tìm session cũ nhất (oldest lastUsedAt) của user
     * Dùng để revoke khi vượt max sessions limit
     */
    Optional<UserSession> findTopByUserIdAndStatusOrderByLastUsedAtAsc(Long userId, UserSession.SessionStatus status);

    /**
     * Tìm sessions đã hết hạn
     * Dùng cho scheduled cleanup job
     */
    List<UserSession> findByStatusAndExpiresAtBefore(UserSession.SessionStatus status,
            java.time.LocalDateTime expiresAt);
}

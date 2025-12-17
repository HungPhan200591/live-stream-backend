package com.stream.demo.model.dto.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.stream.demo.model.entity.UserSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Session Cache DTO
 * <p>
 * DTO riêng cho Redis cache, tách biệt khỏi JPA Entity.
 * Dùng Type Alias để decouple khỏi package structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonTypeName("SessionCache_v1") // Type alias - không đổi khi move package
public class SessionCacheDTO {

    private UUID sessionId;
    private Long userId;
    private String deviceId;
    private String deviceName;
    private String ipAddress;
    private String status; // Store as String để tránh enum serialization issues
    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;
    private LocalDateTime expiresAt;

    /**
     * Convert từ Entity sang DTO
     */
    public static SessionCacheDTO fromEntity(UserSession session) {
        return SessionCacheDTO.builder()
                .sessionId(session.getSessionId())
                .userId(session.getUserId())
                .deviceId(session.getDeviceId())
                .deviceName(session.getDeviceName())
                .ipAddress(session.getIpAddress())
                .status(session.getStatus().name())
                .createdAt(session.getCreatedAt())
                .lastUsedAt(session.getLastUsedAt())
                .expiresAt(session.getExpiresAt())
                .build();
    }

    /**
     * Check if session is valid
     * Không serialize method này thành field
     */
    @JsonIgnore
    public boolean isValid() {
        return "ACTIVE".equals(status) && expiresAt.isAfter(LocalDateTime.now());
    }
}

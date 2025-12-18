package com.stream.demo.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Stream Entity
 * <p>
 * Đại diện cho một phiên livestream.
 * Không sử dụng JPA relationships - creatorId là FK thủ công.
 */
@Entity
@Table(name = "streams", indexes = {
        @Index(name = "idx_stream_creator_id", columnList = "creator_id"),
        @Index(name = "idx_stream_is_live", columnList = "is_live")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * FK thủ công tới User.id (không dùng @ManyToOne)
     */
    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    /**
     * Unique stream key dùng cho OBS/FFmpeg
     */
    @Column(name = "stream_key", unique = true, nullable = false, length = 64)
    private String streamKey;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Trạng thái live của stream
     */
    @Column(name = "is_live", nullable = false)
    @Builder.Default
    private Boolean isLive = false;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

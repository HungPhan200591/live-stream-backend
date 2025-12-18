package com.stream.demo.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StreamDTO {
    private Long id;
    private String creatorUsername;
    private String streamKey;
    private String title;
    private String description;
    private Boolean isLive;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Long viewerCount;
    private LocalDateTime createdAt;
}

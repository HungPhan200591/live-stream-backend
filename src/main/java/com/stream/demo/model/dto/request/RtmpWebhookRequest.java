package com.stream.demo.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO cho RTMP server webhook callbacks.
 * Xem docs: docs/concepts/webhooks.md
 */
@Data
@Schema(description = "RTMP server webhook request payload")
public class RtmpWebhookRequest {

    @NotBlank(message = "streamKey is required")
    @Schema(description = "Unique stream key từ OBS", example = "abc123xyz")
    private String streamKey;

    @Schema(description = "Timestamp của event từ RTMP server", example = "2025-12-18T21:00:00Z")
    private String timestamp;
}

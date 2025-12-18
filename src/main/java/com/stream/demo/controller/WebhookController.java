package com.stream.demo.controller;

import com.stream.demo.common.ApiResponse;
import com.stream.demo.common.exception.UnauthorizedException;
import com.stream.demo.model.dto.StreamDTO;
import com.stream.demo.model.dto.request.RtmpWebhookRequest;
import com.stream.demo.service.StreamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * Webhook Controller
 * <p>
 * Nhận callbacks từ external services (RTMP server, Payment gateway, etc.)
 * Xem docs: docs/concepts/webhooks.md
 * <p>
 * Security: Sử dụng secret key verification thay vì JWT.
 */
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webhooks", description = "External service callback endpoints")
public class WebhookController {

    private final StreamService streamService;

    @Value("${app.webhook.rtmp-secret:dev-secret-key}")
    private String rtmpWebhookSecret;

    // ============================================================
    // RTMP Server Webhooks
    // ============================================================

    /**
     * RTMP server gọi khi phát hiện stream bắt đầu live.
     * Flow: OBS → RTMP Server → Webhook → Backend
     */
    @PostMapping("/rtmp/stream-started")
    @Operation(summary = "Handle stream started event", description = "Called by RTMP server when OBS starts streaming. Requires X-Webhook-Secret header.")
    public ApiResponse<StreamDTO> handleStreamStarted(
            @RequestHeader(value = "X-Webhook-Secret", required = false) String secret,
            @Valid @RequestBody RtmpWebhookRequest request) {

        log.info("Received stream-started webhook for streamKey: {}", request.getStreamKey());

        // Verify secret
        verifyWebhookSecret(secret);

        // Process webhook
        StreamDTO stream = streamService.startStreamByKey(request.getStreamKey());

        log.info("Stream {} started via webhook", stream.getId());
        return ApiResponse.success(stream, "Stream started");
    }

    /**
     * RTMP server gọi khi stream kết thúc.
     */
    @PostMapping("/rtmp/stream-ended")
    @Operation(summary = "Handle stream ended event", description = "Called by RTMP server when OBS stops streaming. Requires X-Webhook-Secret header.")
    public ApiResponse<StreamDTO> handleStreamEnded(
            @RequestHeader(value = "X-Webhook-Secret", required = false) String secret,
            @Valid @RequestBody RtmpWebhookRequest request) {

        log.info("Received stream-ended webhook for streamKey: {}", request.getStreamKey());

        // Verify secret
        verifyWebhookSecret(secret);

        // Process webhook
        StreamDTO stream = streamService.endStreamByKey(request.getStreamKey());

        log.info("Stream {} ended via webhook with {} viewers", stream.getId(), stream.getViewerCount());
        return ApiResponse.success(stream, "Stream ended");
    }

    // ============================================================
    // Helper methods
    // ============================================================

    /**
     * Verify webhook secret key.
     * Throws UnauthorizedException if invalid.
     */
    private void verifyWebhookSecret(String providedSecret) {
        if (providedSecret == null || !providedSecret.equals(rtmpWebhookSecret)) {
            log.warn("Invalid webhook secret received");
            throw new UnauthorizedException("Invalid webhook secret");
        }
    }
}

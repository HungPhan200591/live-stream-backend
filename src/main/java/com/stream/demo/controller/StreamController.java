package com.stream.demo.controller;

import com.stream.demo.common.ApiResponse;
import com.stream.demo.model.dto.StreamDTO;
import com.stream.demo.model.dto.request.CreateStreamRequest;
import com.stream.demo.model.entity.User;
import com.stream.demo.service.LiveStreamCacheService;
import com.stream.demo.service.StreamService;
import com.stream.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Stream Controller
 * <p>
 * API endpoints cho quản lý Livestream.
 * Xem chi tiết authorization rules tại:
 * docs/api_endpoints_specification.md#stream-management
 */
@RestController
@RequestMapping("/api/streams")
@RequiredArgsConstructor
@Tag(name = "Streams", description = "Livestream management APIs")
public class StreamController {

    private final StreamService streamService;
    private final UserService userService;
    private final LiveStreamCacheService liveStreamCache;

    // ============================================================
    // PUBLIC ENDPOINTS
    // ============================================================

    @GetMapping
    @Operation(summary = "Get all streams", description = "Public endpoint to browse active streams. Use liveOnly=true (default) to get only live streams.")
    public ApiResponse<List<StreamDTO>> getAllStreams(
            @RequestParam(defaultValue = "true") boolean liveOnly) {
        List<StreamDTO> streams = liveOnly ? streamService.getAllLiveStreams() : streamService.getAllStreams();
        return ApiResponse.success(streams, null);
    }

    @GetMapping("/{streamId}")
    @Operation(summary = "Get stream details", description = "Public endpoint to view stream info")
    public ApiResponse<StreamDTO> getStreamById(@PathVariable Long streamId) {
        StreamDTO stream = streamService.getStreamById(streamId);
        return ApiResponse.success(stream, null);
    }

    @GetMapping("/{streamId}/viewers")
    @Operation(summary = "Get realtime viewer count", description = "Returns unique viewer count from Redis HyperLogLog")
    public ApiResponse<Long> getViewerCount(@PathVariable Long streamId) {
        Long count = liveStreamCache.getViewerCount(streamId);
        return ApiResponse.success(count, null);
    }

    @PostMapping("/{streamId}/view")
    @Operation(summary = "Track viewer", description = "Track a viewer watching the stream. Can be called periodically as heartbeat.")
    public ApiResponse<Void> trackViewer(
            @PathVariable Long streamId,
            HttpServletRequest request) {
        // Sử dụng session ID hoặc IP làm identifier cho anonymous users
        String viewerId = request.getSession().getId();

        // Nếu user đã login, dùng user ID
        try {
            User currentUser = userService.getCurrentUser();
            viewerId = "user:" + currentUser.getId();
        } catch (Exception ignored) {
            // Anonymous user - dùng session ID
            viewerId = "session:" + viewerId;
        }

        liveStreamCache.addViewer(streamId, viewerId);
        return ApiResponse.success(null, "Viewer tracked");
    }

    // ============================================================
    // STREAMER + ADMIN: Create Stream
    // ============================================================

    @PreAuthorize("hasAnyRole('STREAMER', 'ADMIN')")
    @PostMapping
    @Operation(summary = "Create new stream", description = "Only STREAMER and ADMIN can create streams. Returns generated streamKey.")
    public ApiResponse<StreamDTO> createStream(
            @Valid @RequestBody CreateStreamRequest request) {
        User currentUser = userService.getCurrentUser();
        StreamDTO stream = streamService.createStream(request, currentUser);
        return ApiResponse.success(stream, "Stream created successfully");
    }

    // ============================================================
    // OWNER + ADMIN: Get My Streams
    // ============================================================

    @GetMapping("/my")
    @Operation(summary = "Get streams owned by current user", description = "Returns all streams created by the authenticated user")
    public ApiResponse<List<StreamDTO>> getMyStreams() {
        User currentUser = userService.getCurrentUser();
        List<StreamDTO> streams = streamService.getStreamsByCreatorId(currentUser.getId());
        return ApiResponse.success(streams, null);
    }
}

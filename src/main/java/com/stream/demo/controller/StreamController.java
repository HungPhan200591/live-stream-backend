package com.stream.demo.controller;

import com.stream.demo.common.ApiResponse;
import com.stream.demo.model.dto.StreamDTO;
import com.stream.demo.model.dto.request.CreateStreamRequest;
import com.stream.demo.model.entity.User;
import com.stream.demo.service.StreamService;
import com.stream.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

package com.stream.demo.service;

import com.stream.demo.model.dto.StreamDTO;
import com.stream.demo.model.dto.request.CreateStreamRequest;
import com.stream.demo.model.entity.Stream;
import com.stream.demo.model.entity.User;
import com.stream.demo.repository.StreamRepository;
import com.stream.demo.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service layer cho Stream operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StreamService {

    private final StreamRepository streamRepository;
    private final UserService userService;
    private final LiveStreamCacheService liveStreamCache;

    /**
     * Tạo stream mới
     * Business Rule: Chỉ STREAMER/ADMIN được phép (kiểm tra ở Controller)
     */
    @Transactional
    public StreamDTO createStream(CreateStreamRequest request, User currentUser) {
        log.info("Creating new stream for user: {}", currentUser.getUsername());

        // 1. Generate unique streamKey
        String streamKey = generateUniqueStreamKey();

        // 2. Create Stream entity
        Stream stream = Stream.builder()
                .creatorId(currentUser.getId())
                .streamKey(streamKey)
                .title(request.getTitle())
                .description(request.getDescription())
                .isLive(false)
                .build();

        // 3. Save to DB
        stream = streamRepository.save(stream);
        log.info("Stream created with ID: {} and streamKey: {}", stream.getId(), streamKey);

        // 4. Convert to DTO and return
        return convertToDTO(stream);
    }

    /**
     * Lấy tất cả stream đang live
     */
    public List<StreamDTO> getAllLiveStreams() {
        return streamRepository.findByIsLiveTrue().stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Lấy tất cả stream (bao gồm cả không live)
     */
    public List<StreamDTO> getAllStreams() {
        return streamRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Lấy stream theo ID
     */
    public StreamDTO getStreamById(Long streamId) {
        Stream stream = streamRepository.findById(streamId)
                .orElseThrow(() -> new ResourceNotFoundException("Stream", "id", streamId));
        return convertToDTO(stream);
    }

    /**
     * Lấy stream entity theo ID (internal use)
     */
    public Stream getStreamEntityById(Long streamId) {
        return streamRepository.findById(streamId)
                .orElseThrow(() -> new ResourceNotFoundException("Stream", "id", streamId));
    }

    /**
     * Lấy các stream của một user
     */
    public List<StreamDTO> getStreamsByCreatorId(Long creatorId) {
        return streamRepository.findByCreatorId(creatorId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Kiểm tra user có phải owner của stream không
     * Dùng cho @PreAuthorize
     */
    public boolean isStreamOwner(Long streamId, String username) {
        Stream stream = streamRepository.findById(streamId)
                .orElseThrow(() -> new ResourceNotFoundException("Stream", "id", streamId));
        User user = userService.getUserByUsername(username);
        return stream.getCreatorId().equals(user.getId());
    }

    // ============================================================
    // Private helper methods
    // ============================================================

    /**
     * Generate unique stream key using UUID
     */
    private String generateUniqueStreamKey() {
        String streamKey;
        do {
            streamKey = UUID.randomUUID().toString().replace("-", "");
        } while (streamRepository.existsByStreamKey(streamKey));
        return streamKey;
    }

    /**
     * Convert Stream entity to StreamDTO
     */
    public StreamDTO convertToDTO(Stream stream) {
        // Lookup creator username
        User creator = userService.getUserById(stream.getCreatorId());

        return StreamDTO.builder()
                .id(stream.getId())
                .creatorUsername(creator.getUsername())
                .streamKey(stream.getStreamKey())
                .title(stream.getTitle())
                .description(stream.getDescription())
                .isLive(stream.getIsLive())
                .startedAt(stream.getStartedAt())
                .endedAt(stream.getEndedAt())
                .viewerCount(0L) // Placeholder - sẽ lấy từ Redis sau
                .createdAt(stream.getCreatedAt())
                .build();
    }

    /**
     * Bắt đầu stream (Go Live)
     * Business Logic theo Phase 4 spec:
     * 1. Update DB: isLive=true, startedAt=NOW
     * 2. Sync Redis cache
     * 3. TODO: Publish RabbitMQ event (Phase 6)
     */
    @Transactional
    public StreamDTO startStream(Long streamId) {
        log.info("Starting stream with ID: {}", streamId);

        // 1. Load stream entity
        Stream stream = getStreamEntityById(streamId);

        // 2. Update DB
        stream.setIsLive(true);
        stream.setStartedAt(LocalDateTime.now());
        streamRepository.save(stream);

        log.info("Stream {} set to LIVE in DB", streamId);

        // 3. Sync Redis cache
        liveStreamCache.setLiveStatus(streamId, true);

        // 4. TODO: Publish RabbitMQ event (Phase 6)
        // eventPublisher.publish("stream.started", streamId);

        // 5. Return DTO with current viewer count
        return convertToDTO(stream);
    }

    /**
     * Wrapper method for SimulationController (backward compatibility)
     * Accepts streamKey instead of streamId
     */
    public StreamDTO startStreamByKey(String streamKey) {
        log.info("Starting stream by key: {}", streamKey);
        Stream stream = streamRepository.findByStreamKey(streamKey)
                .orElseThrow(() -> new ResourceNotFoundException("Stream", "streamKey", streamKey));
        return startStream(stream.getId());
    }

    /**
     * Kết thúc stream
     * Business Logic theo Phase 4 spec:
     * 1. Update DB: isLive=false, endedAt=NOW
     * 2. Clear Redis cache
     * 3. Get final viewer count (optional persist)
     * 4. TODO: Publish RabbitMQ event (Phase 6)
     */
    @Transactional
    public StreamDTO endStream(Long streamId) {
        log.info("Ending stream with ID: {}", streamId);

        // 1. Load stream entity
        Stream stream = getStreamEntityById(streamId);

        // 2. Update DB
        stream.setIsLive(false);
        stream.setEndedAt(LocalDateTime.now());
        streamRepository.save(stream);

        log.info("Stream {} set to ENDED in DB", streamId);

        // 3. Get final viewer count before clearing cache
        Long finalViewerCount = liveStreamCache.getViewerCount(streamId);
        log.info("Stream {} ended with {} unique viewers", streamId, finalViewerCount);

        // 4. Clear Redis cache
        liveStreamCache.setLiveStatus(streamId, false);
        // Optional: Reset viewer count (keep for historical query)
        // liveStreamCache.resetViewerCount(streamId);

        // 5. TODO: Publish RabbitMQ event (Phase 6)
        // eventPublisher.publish("stream.ended", streamId, finalViewerCount);

        // 6. Return DTO
        StreamDTO dto = convertToDTO(stream);
        dto.setViewerCount(finalViewerCount); // Include final count in response
        return dto;
    }

    /**
     * Wrapper method for SimulationController (backward compatibility)
     * Accepts streamKey instead of streamId
     */
    public StreamDTO endStreamByKey(String streamKey) {
        log.info("Ending stream by key: {}", streamKey);
        Stream stream = streamRepository.findByStreamKey(streamKey)
                .orElseThrow(() -> new ResourceNotFoundException("Stream", "streamKey", streamKey));
        return endStream(stream.getId());
    }
}

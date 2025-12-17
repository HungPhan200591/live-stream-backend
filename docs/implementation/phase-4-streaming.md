# Phase 4: Stream Management Module

> **Status**: 🔄 TODO (Next Phase)  
> **Dependencies**: Phase 3 (Authentication & User Management)

---

## Business Goals

### Use Cases Covered
- **UC-02**: Streamer Creates Livestream
- **UC-03**: Viewer Watches Stream

### Business Value Delivered
- ✅ Streamers có thể tạo và quản lý livestream sessions
- ✅ Viewers có thể browse và xem live streams
- ✅ Real-time viewer tracking và analytics
- ✅ Stream lifecycle management (Create → Live → Ended)

### User Flows Supported
- [Streamer Lifecycle Journey](../business_flows.md#flow-1-streamer-lifecycle-journey)
- [Viewer Journey](../business_flows.md#flow-2-viewer-journey)

---

## Technical Implementation

### 4.1. Entity Layer

#### Stream Entity (`Stream.java`)

```java
@Entity
@Table(name = "streams")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stream {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "creator_id", nullable = false)
    private Long creatorId; // NO @ManyToOne
    
    @Column(name = "stream_key", unique = true, nullable = false)
    private String streamKey;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_live", nullable = false)
    private Boolean isLive = false;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

**Indexes**:
- `stream_key` (unique)
- `creator_id`
- `is_live`

---

### 4.2. Repository Layer

#### StreamRepository

```java
public interface StreamRepository extends JpaRepository<Stream, Long> {
    Optional<Stream> findByStreamKey(String streamKey);
    List<Stream> findByCreatorId(Long creatorId);
    List<Stream> findByIsLiveTrue();
    boolean existsByStreamKey(String streamKey);
    Optional<Stream> findByIdAndCreatorId(Long id, Long creatorId);
}
```

---

### 4.3. DTOs

#### Request DTOs

```java
@Data
public class CreateStreamRequest {
    @NotBlank
    @Schema(description = "Stream title", example = "My Gaming Stream")
    private String title;
    
    @Schema(description = "Stream description", example = "Playing Valorant ranked")
    private String description;
}

@Data
public class UpdateStreamRequest {
    @Schema(description = "Updated title", example = "New Stream Title")
    private String title;
    
    @Schema(description = "Updated description", example = "New description")
    private String description;
}
```

#### Response DTOs

```java
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
    private Long viewerCount; // From Redis
    private LocalDateTime createdAt;
}
```

---

### 4.4. Service Layer

#### StreamService

**Methods to Implement**:

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class StreamService {
    private final StreamRepository streamRepository;
    private final UserService userService;
    private final LiveStreamCache liveStreamCache;
    private final StreamEventPublisher eventPublisher;
    
    /**
     * Tạo stream mới
     * Business Rule: Chỉ STREAMER/ADMIN
     */
    public StreamDTO createStream(CreateStreamRequest request, User currentUser) {
        // 1. Generate unique streamKey
        String streamKey = generateStreamKey();
        
        // 2. Create Stream entity
        Stream stream = Stream.builder()
                .creatorId(currentUser.getId())
                .streamKey(streamKey)
                .title(request.getTitle())
                .description(request.getDescription())
                .isLive(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        // 3. Save to DB
        stream = streamRepository.save(stream);
        
        // 4. Convert to DTO
        return convertToDTO(stream);
    }
    
    /**
     * Bắt đầu stream
     * Business Rule: Chỉ owner hoặc ADMIN
     */
    @Transactional
    public StreamDTO startStream(String streamKey) {
        // 1. Find stream by key
        Stream stream = streamRepository.findByStreamKey(streamKey)
                .orElseThrow(() -> new ResourceNotFoundException("Stream not found"));
        
        // 2. Update status
        stream.setIsLive(true);
        stream.setStartedAt(LocalDateTime.now());
        stream = streamRepository.save(stream);
        
        // 3. Update Redis cache
        liveStreamCache.setLiveStatus(stream.getId(), true);
        
        // 4. Publish event (notify followers)
        eventPublisher.publishStreamStartedEvent(convertToDTO(stream));
        
        return convertToDTO(stream);
    }
    
    /**
     * Kết thúc stream
     */
    @Transactional
    public StreamDTO endStream(String streamKey) {
        Stream stream = streamRepository.findByStreamKey(streamKey)
                .orElseThrow(() -> new ResourceNotFoundException("Stream not found"));
        
        // 1. Update status
        stream.setIsLive(false);
        stream.setEndedAt(LocalDateTime.now());
        stream = streamRepository.save(stream);
        
        // 2. Cleanup Redis
        liveStreamCache.setLiveStatus(stream.getId(), false);
        Long viewerCount = liveStreamCache.getViewerCount(stream.getId());
        
        // 3. Save stats (optional - Phase 8)
        // saveStreamStats(stream.getId(), viewerCount);
        
        // 4. Publish event
        eventPublisher.publishStreamEndedEvent(convertToDTO(stream));
        
        return convertToDTO(stream);
    }
    
    /**
     * Get all live streams (Public)
     */
    public List<StreamDTO> getAllLiveStreams() {
        return streamRepository.findByIsLiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get stream by ID (Public)
     */
    public StreamDTO getStreamById(Long streamId) {
        Stream stream = streamRepository.findById(streamId)
                .orElseThrow(() -> new ResourceNotFoundException("Stream not found"));
        return convertToDTO(stream);
    }
    
    /**
     * Update stream metadata
     * Business Rule: Chỉ owner hoặc ADMIN
     */
    public StreamDTO updateStream(Long streamId, UpdateStreamRequest request, User currentUser) {
        Stream stream = streamRepository.findById(streamId)
                .orElseThrow(() -> new ResourceNotFoundException("Stream not found"));
        
        // Validate ownership (hoặc dùng @PreAuthorize)
        if (!stream.getCreatorId().equals(currentUser.getId()) && !isAdmin(currentUser)) {
            throw new AccessDeniedException("Not authorized");
        }
        
        // Update fields
        if (request.getTitle() != null) {
            stream.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            stream.setDescription(request.getDescription());
        }
        stream.setUpdatedAt(LocalDateTime.now());
        
        stream = streamRepository.save(stream);
        return convertToDTO(stream);
    }
    
    /**
     * Helper: Check if user is stream owner (for @PreAuthorize)
     */
    public boolean isStreamOwner(Long streamId, String username) {
        Stream stream = streamRepository.findById(streamId)
                .orElseThrow(() -> new ResourceNotFoundException("Stream not found"));
        User user = userService.findByUsername(username);
        return stream.getCreatorId().equals(user.getId());
    }
    
    // Private helpers
    private String generateStreamKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    private StreamDTO convertToDTO(Stream stream) {
        User creator = userService.getUserById(stream.getCreatorId());
        Long viewerCount = stream.getIsLive() ? 
                liveStreamCache.getViewerCount(stream.getId()) : 0L;
        
        return StreamDTO.builder()
                .id(stream.getId())
                .creatorUsername(creator.getUsername())
                .streamKey(stream.getStreamKey())
                .title(stream.getTitle())
                .description(stream.getDescription())
                .isLive(stream.getIsLive())
                .startedAt(stream.getStartedAt())
                .endedAt(stream.getEndedAt())
                .viewerCount(viewerCount)
                .createdAt(stream.getCreatedAt())
                .build();
    }
}
```

---

### 4.5. Redis Integration

#### LiveStreamCache Service

```java
@Service
@RequiredArgsConstructor
public class LiveStreamCache {
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String LIVE_STATUS_KEY = "stream:%d:live";
    private static final String VIEWERS_KEY = "stream:%d:viewers";
    private static final long LIVE_STATUS_TTL = 24 * 60 * 60; // 24 hours
    
    /**
     * Track viewer (HyperLogLog)
     */
    public void addViewer(Long streamId, Long userId) {
        String key = String.format(VIEWERS_KEY, streamId);
        redisTemplate.opsForHyperLogLog().add(key, userId.toString());
    }
    
    /**
     * Get unique viewer count
     */
    public Long getViewerCount(Long streamId) {
        String key = String.format(VIEWERS_KEY, streamId);
        return redisTemplate.opsForHyperLogLog().size(key);
    }
    
    /**
     * Set live status
     */
    public void setLiveStatus(Long streamId, boolean isLive) {
        String key = String.format(LIVE_STATUS_KEY, streamId);
        if (isLive) {
            redisTemplate.opsForValue().set(key, "true", LIVE_STATUS_TTL, TimeUnit.SECONDS);
        } else {
            redisTemplate.delete(key);
        }
    }
    
    /**
     * Check if stream is live (from cache)
     */
    public boolean isLive(Long streamId) {
        String key = String.format(LIVE_STATUS_KEY, streamId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
```

---

### 4.6. Controller Layer

#### StreamController

```java
@RestController
@RequestMapping("/api/streams")
@RequiredArgsConstructor
@Tag(name = "Streams", description = "Livestream management APIs")
public class StreamController {
    
    private final StreamService streamService;
    private final LiveStreamCache liveStreamCache;
    
    // ============================================================
    // PUBLIC ENDPOINTS
    // ============================================================
    
    @GetMapping
    @Operation(summary = "Get all live streams", description = "Public endpoint to browse active streams")
    public ApiResponse<List<StreamDTO>> getAllStreams(
            @RequestParam(defaultValue = "true") boolean liveOnly) {
        List<StreamDTO> streams = liveOnly ? 
                streamService.getAllLiveStreams() : 
                streamService.getAllStreams();
        return ApiResponse.success(streams, null);
    }
    
    @GetMapping("/{streamId}")
    @Operation(summary = "Get stream details", description = "Public endpoint to view stream info")
    public ApiResponse<StreamDTO> getStreamById(@PathVariable Long streamId) {
        StreamDTO stream = streamService.getStreamById(streamId);
        return ApiResponse.success(stream, null);
    }
    
    @GetMapping("/{streamId}/viewers")
    @Operation(summary = "Get realtime viewer count")
    public ApiResponse<Long> getViewerCount(@PathVariable Long streamId) {
        Long count = liveStreamCache.getViewerCount(streamId);
        return ApiResponse.success(count, null);
    }
    
    // ============================================================
    // STREAMER + ADMIN: Create Stream
    // ============================================================
    
    @PreAuthorize("hasAnyRole('STREAMER', 'ADMIN')")
    @PostMapping
    @Operation(summary = "Create new stream", description = "Only STREAMER and ADMIN can create")
    public ApiResponse<StreamDTO> createStream(
            @Valid @RequestBody CreateStreamRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User currentUser = userDetails.getUser();
        StreamDTO stream = streamService.createStream(request, currentUser);
        return ApiResponse.success(stream, "Stream created successfully");
    }
    
    // ============================================================
    // OWNER + ADMIN: Update/Start/End Stream
    // ============================================================
    
    @PreAuthorize("hasRole('ADMIN') or @streamService.isStreamOwner(#streamId, authentication.principal.username)")
    @PutMapping("/{streamId}")
    @Operation(summary = "Update stream metadata", description = "Only owner or ADMIN")
    public ApiResponse<StreamDTO> updateStream(
            @PathVariable Long streamId,
            @Valid @RequestBody UpdateStreamRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User currentUser = userDetails.getUser();
        StreamDTO stream = streamService.updateStream(streamId, request, currentUser);
        return ApiResponse.success(stream, "Stream updated successfully");
    }
    
    @PreAuthorize("hasRole('ADMIN') or @streamService.isStreamOwner(#streamId, authentication.principal.username)")
    @PostMapping("/{streamId}/start")
    @Operation(summary = "Start livestream", description = "Set stream status to LIVE")
    public ApiResponse<StreamDTO> startStream(@PathVariable Long streamId) {
        Stream stream = streamService.getStreamEntityById(streamId);
        StreamDTO result = streamService.startStream(stream.getStreamKey());
        return ApiResponse.success(result, "Stream started successfully");
    }
    
    @PreAuthorize("hasRole('ADMIN') or @streamService.isStreamOwner(#streamId, authentication.principal.username)")
    @PostMapping("/{streamId}/end")
    @Operation(summary = "End livestream", description = "Set stream status to ENDED")
    public ApiResponse<StreamDTO> endStream(@PathVariable Long streamId) {
        Stream stream = streamService.getStreamEntityById(streamId);
        StreamDTO result = streamService.endStream(stream.getStreamKey());
        return ApiResponse.success(result, "Stream ended successfully");
    }
    
    // ============================================================
    // ADMIN ONLY: Delete Stream
    // ============================================================
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{streamId}")
    @Operation(summary = "Delete stream", description = "Only ADMIN can delete")
    public ApiResponse<Void> deleteStream(@PathVariable Long streamId) {
        streamService.deleteStream(streamId);
        return ApiResponse.success(null, "Stream deleted successfully");
    }
}
```

---

### 4.7. Event Publishing (RabbitMQ)

#### StreamEventPublisher

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class StreamEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    
    private static final String STREAM_STARTED_QUEUE = "notifications.stream.started";
    private static final String STREAM_ENDED_QUEUE = "notifications.stream.ended";
    
    public void publishStreamStartedEvent(StreamDTO stream) {
        log.info("Publishing stream started event for stream: {}", stream.getId());
        rabbitTemplate.convertAndSend(STREAM_STARTED_QUEUE, stream);
    }
    
    public void publishStreamEndedEvent(StreamDTO stream) {
        log.info("Publishing stream ended event for stream: {}", stream.getId());
        rabbitTemplate.convertAndSend(STREAM_ENDED_QUEUE, stream);
    }
}
```

---

## Verification Plan

### Automated Tests

#### Unit Tests

```java
@SpringBootTest
class StreamServiceTest {
    @Test
    void createStream_shouldGenerateUniqueStreamKey() {
        // Test stream key generation
    }
    
    @Test
    void startStream_shouldUpdateStatusAndRedis() {
        // Test stream start flow
    }
    
    @Test
    void endStream_shouldCleanupRedis() {
        // Test stream end flow
    }
}
```

#### Integration Tests

```java
@SpringBootTest
@AutoConfigureMockMvc
class StreamControllerTest {
    @Test
    @WithMockUser(roles = "STREAMER")
    void createStream_asStreamer_shouldSucceed() {
        // Test authorization
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void createStream_asUser_shouldFail() {
        // Test 403 Forbidden
    }
}
```

---

### Manual Testing Scenarios

#### Scenario 1: Complete Streamer Flow

**Steps**:
1. Login as user with `ROLE_STREAMER`
2. `POST /api/streams` - Create stream
   - Verify: streamKey generated, isLive=false
3. `POST /api/streams/{id}/start` - Start stream
   - Verify: isLive=true, startedAt set, Redis updated
4. `GET /api/streams` - Browse streams
   - Verify: Stream appears in live list
5. `GET /api/streams/{id}/viewers` - Check viewer count
   - Verify: Returns 0 (no viewers yet)
6. `POST /api/streams/{id}/end` - End stream
   - Verify: isLive=false, endedAt set

**Expected Results**:
- ✅ All API calls return 200 OK
- ✅ Stream lifecycle transitions correctly
- ✅ Redis cache updated at each step

---

#### Scenario 2: Authorization Testing

**Steps**:
1. Login as `ROLE_USER`
2. Try `POST /api/streams`
   - **Expected**: 403 Forbidden
3. Login as `ROLE_STREAMER`
4. Create stream (get streamId)
5. Login as different `ROLE_STREAMER`
6. Try `PUT /api/streams/{streamId}`
   - **Expected**: 403 Forbidden (not owner)
7. Login as `ROLE_ADMIN`
8. Try `PUT /api/streams/{streamId}`
   - **Expected**: 200 OK (admin can update any stream)

---

#### Scenario 3: Redis HyperLogLog Accuracy

**Steps**:
1. Create and start stream
2. Simulate 100 unique viewers:
   ```java
   for (long i = 1; i <= 100; i++) {
       liveStreamCache.addViewer(streamId, i);
   }
   ```
3. Get viewer count
   - **Expected**: ~100 (HyperLogLog có ~2% error)
4. Add duplicate viewers:
   ```java
   liveStreamCache.addViewer(streamId, 1L); // Same user
   ```
5. Get viewer count
   - **Expected**: Still ~100 (no duplicates)

---

## Dependencies

### Required Phases
- ✅ Phase 3: Authentication & User Management (for creator identification)

### Enables Future Phases
- Phase 6: Real-time Chat (chat rooms tied to streams)
- Phase 8: Analytics (stream statistics)
- Phase 9: Admin (stream moderation)

---

## API Documentation Checklist

- [ ] Create `.http/stream-controller.http` với all endpoints
- [ ] Verify Swagger UI hiển thị đầy đủ
- [ ] Test all endpoints qua HTTP file
- [ ] Document example requests/responses

---

## Notes

### Business Rules Enforced
- BR-06: Chỉ STREAMER/ADMIN tạo stream ✅
- BR-07: Chỉ owner/ADMIN update/end stream ✅
- BR-08: Stream key unique ✅
- BR-09: Stream state machine (CREATED → LIVE → ENDED) ✅
- BR-10: Save analytics khi end stream (Phase 8)

### Redis Usage
- **HyperLogLog**: Unique viewer tracking
- **String**: Live status caching (TTL 24h)

### RabbitMQ Usage
- **Queue**: `notifications.stream.started` (notify followers - Phase 12)
- **Queue**: `notifications.stream.ended` (cleanup tasks)

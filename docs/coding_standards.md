# Coding Standards - Spring Boot Livestream Backend

> **Mục đích**: Tài liệu chi tiết với full code examples cho các coding patterns.  
> **Audience**: Developers và AI agents khi cần reference cụ thể.  
> **Quick Reference**: Xem `.agent/rules/coding-rule.md` cho checklist nhanh.

---

## Table of Contents

1. [Controller Implementation](#controller-implementation)
2. [DTO Patterns](#dto-patterns)
3. [Database Design](#database-design)
4. [Redis Caching](#redis-caching)
5. [HTTP Request Files](#http-request-files)

---

## Controller Implementation

### Full Example: StreamController

```java
package com.stream.demo.controller;

import com.stream.demo.model.dto.ApiResponse;
import com.stream.demo.model.dto.StreamDTO;
import com.stream.demo.model.dto.request.CreateStreamRequest;
import com.stream.demo.model.dto.request.UpdateStreamRequest;
import com.stream.demo.service.StreamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/streams")
@RequiredArgsConstructor
@Tag(name = "Streams", description = "Livestream management APIs")
public class StreamController {

    private final StreamService streamService;

    // Public endpoint - no authentication required
    @GetMapping
    @Operation(summary = "Get all live streams", 
               description = "Returns list of all currently live streams")
    public ApiResponse<List<StreamDTO>> getAllStreams() {
        List<StreamDTO> streams = streamService.getAllLiveStreams();
        return ApiResponse.success(streams, null);
    }

    // Public endpoint - get single stream
    @GetMapping("/{streamId}")
    @Operation(summary = "Get stream by ID")
    public ApiResponse<StreamDTO> getStream(@PathVariable Long streamId) {
        StreamDTO stream = streamService.getStreamById(streamId);
        return ApiResponse.success(stream, null);
    }

    // STREAMER or ADMIN only
    @PreAuthorize("hasAnyRole('STREAMER', 'ADMIN')")
    @PostMapping
    @Operation(summary = "Create new stream", 
               description = "Create a new livestream. Requires STREAMER or ADMIN role.")
    public ApiResponse<StreamDTO> createStream(
            @Valid @RequestBody CreateStreamRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        StreamDTO stream = streamService.createStream(request, username);
        return ApiResponse.success(stream, "Stream created successfully");
    }

    // Owner or ADMIN only
    @PreAuthorize("hasRole('ADMIN') or @streamService.isStreamOwner(#streamId, authentication.principal.username)")
    @PutMapping("/{streamId}")
    @Operation(summary = "Update stream", 
               description = "Update stream details. Only owner or admin can update.")
    public ApiResponse<StreamDTO> updateStream(
            @PathVariable Long streamId,
            @Valid @RequestBody UpdateStreamRequest request,
            Authentication authentication) {
        StreamDTO stream = streamService.updateStream(streamId, request);
        return ApiResponse.success(stream, "Stream updated successfully");
    }

    // Owner or ADMIN only
    @PreAuthorize("hasRole('ADMIN') or @streamService.isStreamOwner(#streamId, authentication.principal.username)")
    @DeleteMapping("/{streamId}")
    @Operation(summary = "Delete stream")
    public ApiResponse<Void> deleteStream(@PathVariable Long streamId) {
        streamService.deleteStream(streamId);
        return ApiResponse.success(null, "Stream deleted successfully");
    }
}
```

### Key Points

✅ **Swagger Annotations**:
- `@Tag` at class level for grouping
- `@Operation` at method level with summary and description
- Descriptions explain authorization requirements

✅ **Authorization Patterns**:
- Public endpoints: No `@PreAuthorize`
- Role-based: `@PreAuthorize("hasRole('ROLE_NAME')")`
- Multiple roles: `@PreAuthorize("hasAnyRole('ROLE1', 'ROLE2')")`
- Custom logic: `@PreAuthorize("hasRole('ADMIN') or @service.isOwner(...)")`

✅ **ApiResponse Wrapper**:
- Always use `ApiResponse<T>` for consistent response format
- Include success message when appropriate
- Use `null` for data when deleting

---

## DTO Patterns

### Request DTO with Swagger Examples

```java
package com.stream.demo.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStreamRequest {

    @Schema(description = "Stream title", example = "My Gaming Stream")
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Schema(description = "Stream description", example = "Playing Valorant ranked matches")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Schema(description = "Stream category", example = "Gaming")
    @NotBlank(message = "Category is required")
    private String category;

    @Schema(description = "Stream thumbnail URL", example = "https://example.com/thumbnails/stream123.jpg")
    private String thumbnailUrl;
}
```

### Response DTO

```java
package com.stream.demo.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamDTO {

    @Schema(description = "Stream ID", example = "1")
    private Long id;

    @Schema(description = "Stream title", example = "My Gaming Stream")
    private String title;

    @Schema(description = "Stream description", example = "Playing Valorant ranked matches")
    private String description;

    @Schema(description = "Stream category", example = "Gaming")
    private String category;

    @Schema(description = "Streamer username", example = "streamer001")
    private String streamerUsername;

    @Schema(description = "Current viewer count", example = "1250")
    private Integer viewerCount;

    @Schema(description = "Stream status", example = "LIVE")
    private String status;

    @Schema(description = "Stream start time", example = "2025-12-17T20:00:00")
    private LocalDateTime startedAt;

    @Schema(description = "Thumbnail URL", example = "https://example.com/thumbnails/stream123.jpg")
    private String thumbnailUrl;
}
```

### Key Points

✅ **@Schema Annotation**:
- `description`: Clear explanation of the field
- `example`: Realistic example value matching test data
- Examples should be consistent across DTOs and .http files

✅ **Validation Annotations**:
- `@NotBlank`: For required strings
- `@Size`: For length constraints
- `@Email`: For email validation
- `@Valid`: In controller to trigger validation

---

## Database Design

### Entity Without JPA Relationships

```java
package com.stream.demo.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 50)
    private String category;

    // Foreign key as primitive - NO @ManyToOne
    @Column(name = "streamer_id", nullable = false)
    private Long streamerId;

    @Column(name = "viewer_count")
    private Integer viewerCount = 0;

    @Column(length = 20)
    private String status; // LIVE, ENDED, SCHEDULED

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### Join Table Entity (Many-to-Many)

```java
package com.stream.demo.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }
}
```

### Repository with Manual Joins

```java
package com.stream.demo.repository;

import com.stream.demo.model.entity.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StreamRepository extends JpaRepository<Stream, Long> {

    // Find by foreign key
    List<Stream> findByStreamerId(Long streamerId);

    // Find by status
    List<Stream> findByStatus(String status);

    // Manual join query
    @Query("SELECT s FROM Stream s WHERE s.streamerId = :userId AND s.status = 'LIVE'")
    List<Stream> findLiveStreamsByUser(@Param("userId") Long userId);

    // Count by foreign key
    long countByStreamerIdAndStatus(Long streamerId, String status);
}
```

---

## Redis Caching

### Cache DTO

```java
package com.stream.demo.model.dto.cache;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class StreamCacheDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String streamerUsername;
    private Integer viewerCount;
    private String status;
    private LocalDateTime startedAt;
}
```

### RedisTemplate Configuration

```java
package com.stream.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stream.demo.model.dto.cache.StreamCacheDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    public static class RedisTemplateBeanNames {
        public static final String STREAM_CACHE = "streamCacheRedisTemplate";
    }

    @Bean(RedisTemplateBeanNames.STREAM_CACHE)
    public RedisTemplate<String, StreamCacheDTO> streamCacheRedisTemplate(
            RedisConnectionFactory connectionFactory) {
        
        RedisTemplate<String, StreamCacheDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key serializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value serializer
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        
        Jackson2JsonRedisSerializer<StreamCacheDTO> serializer = 
            new Jackson2JsonRedisSerializer<>(mapper, StreamCacheDTO.class);
        
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
```

### Cache Service

```java
package com.stream.demo.service;

import com.stream.demo.config.RedisConfig;
import com.stream.demo.model.dto.cache.StreamCacheDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
public class StreamCacheService {

    private static final String CACHE_KEY_PREFIX = "stream:v1:";
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(10);

    private final RedisTemplate<String, StreamCacheDTO> redisTemplate;

    public StreamCacheService(
            @Qualifier(RedisConfig.RedisTemplateBeanNames.STREAM_CACHE) 
            RedisTemplate<String, StreamCacheDTO> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheStream(Long streamId, StreamCacheDTO dto) {
        String key = CACHE_KEY_PREFIX + streamId;
        redisTemplate.opsForValue().set(key, dto, DEFAULT_TTL);
        log.debug("Cached stream: {}", streamId);
    }

    public Optional<StreamCacheDTO> getStream(Long streamId) {
        String key = CACHE_KEY_PREFIX + streamId;
        StreamCacheDTO cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.debug("Cache hit for stream: {}", streamId);
            return Optional.of(cached);
        }
        log.debug("Cache miss for stream: {}", streamId);
        return Optional.empty();
    }

    public void evictStream(Long streamId) {
        String key = CACHE_KEY_PREFIX + streamId;
        redisTemplate.delete(key);
        log.debug("Evicted stream cache: {}", streamId);
    }
}
```

---

## HTTP Request Files

### Example: stream-controller.http

```http
### Variables
@host = http://localhost:8080
@token = {{token}}
@streamerId = 2

### Get All Live Streams (Public)
GET {{host}}/api/streams
Content-Type: application/json

### Get Stream by ID (Public)
GET {{host}}/api/streams/1
Content-Type: application/json

### Create Stream (STREAMER or ADMIN)
POST {{host}}/api/streams
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "title": "My Gaming Stream",
  "description": "Playing Valorant ranked matches",
  "category": "Gaming",
  "thumbnailUrl": "https://example.com/thumbnails/stream123.jpg"
}

> {%
    client.global.set("streamId", response.body.data.id);
    client.log("Created stream ID: " + response.body.data.id);
%}

### Update Stream (Owner or ADMIN)
PUT {{host}}/api/streams/{{streamId}}
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "title": "Updated Stream Title",
  "description": "Updated description",
  "category": "Gaming"
}

### Delete Stream (Owner or ADMIN)
DELETE {{host}}/api/streams/{{streamId}}
Authorization: Bearer {{token}}

### Get Streams by Streamer (Public)
GET {{host}}/api/streams?streamerId={{streamerId}}
Content-Type: application/json
```

### Key Points

✅ **Variables**:
- Define reusable variables at the top
- Use `{{variableName}}` syntax
- Auto-save response values with `> {% %}` scripts

✅ **Request Structure**:
- Clear section headers with `###`
- Include all required headers (Authorization, Content-Type)
- Use realistic data matching @Schema examples

✅ **Organization**:
- Group by controller
- Order: Public → Authenticated → Role-based
- Include comments for authorization requirements

---

## Summary

### File Organization

```
src/main/java/com/stream/demo/
├── controller/          # @RestController with Swagger annotations
├── service/            # Business logic
├── repository/         # JPA repositories
├── model/
│   ├── entity/        # JPA entities (NO relationship annotations)
│   └── dto/
│       ├── request/   # Request DTOs with @Schema examples
│       ├── response/  # Response DTOs
│       └── cache/     # Redis cache DTOs
└── config/            # @Configuration classes

.http/                 # HTTP request files
├── auth-controller.http
├── stream-controller.http
└── user-controller.http
```

### Checklist for New Feature

- [ ] Create Entity (no JPA relationships)
- [ ] Create Repository with manual queries
- [ ] Create Request/Response DTOs with @Schema
- [ ] Create Service with business logic
- [ ] Create Controller with @Tag and @Operation
- [ ] Create .http file with all endpoints
- [ ] Manual test via .http file
- [ ] Verify Swagger UI
- [ ] (Optional) Create Cache DTO and Cache Service
- [ ] Commit code

---

**Reference**: Xem `.agent/rules/coding-rule.md` cho quick checklist và `.agent/rules/context-load.md` cho project context.

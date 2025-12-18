# Coding Rules - Spring Boot Livestream Backend

## 0. Core Coding Standards

### DTOs (Data Transfer Objects)

- **LUÔN LUÔN** dùng DTO cho Input/Output của API
- **KHÔNG BAO GIỜ** expose Entity class trực tiếp ra Controller
- Pattern: `Request` suffix cho input, `Response`/`DTO` suffix cho output

### Lombok

- Tận dụng Lombok để giảm boilerplate:
  - `@Data`: getter/setter/toString/equals/hashCode
  - `@Builder`: builder pattern
  - `@RequiredArgsConstructor`: constructor cho final fields
  - `@Slf4j`: logger instance

### Configuration

- **Infrastructure config**: `docker-compose.yml` (PostgreSQL, Redis, RabbitMQ)
- **Application config**: `application.yml` (Spring Boot settings)
- **Bean config**: Dedicated `@Configuration` classes trong package `config`

---

## 📖 Required Reading Before Implementation

### API Endpoints Specification

**BẮT BUỘC** đọc `docs/api_endpoints_specification.md` trước khi implement bất kỳ feature nào liên quan đến API.

**File này là SSOT (Single Source of Truth) về**:

- ✅ API endpoint patterns cho tất cả domains
- ✅ Authorization rules (Public/Authenticated/Role-based)
- ✅ HTTP methods và expected request/response DTOs
- ✅ SecurityConfig URL-level patterns
- ✅ @PreAuthorize method-level patterns
- ✅ Implementation best practices

**Khi nào cần check**:

1. Trước khi tạo Controller class mới
2. Trước khi implement endpoint mới
3. Khi thiết lập authorization (@PreAuthorize hoặc SecurityConfig)
4. Khi có thắc mắc về endpoint nào cần role gì

### Authorization Flow (Mermaid Diagrams)

**Tham khảo** `docs/authorization_flow.md` để hiểu luồng phân quyền:

- ✅ REST API authorization flow (Request → JWT → URL-level → Method-level)
- ✅ WebSocket authorization (Handshake → Subscribe → Message)
- ✅ Two-tier strategy (Khi nào dùng URL-level vs Method-level)
- ✅ Common scenarios với code examples

**Đặc biệt quan trọng**:

- WebSocket khác REST API - cần 3 layers authorization
- Always check mute/ban trong Redis trước khi process message
- Handshake authentication ≠ Message authorization

### Redis Implementation

**BẮT BUỘC** đọc `docs/redis_usage_guide.md` trước khi implement bất kỳ Redis caching nào.

**File này là SSOT về**:

- ✅ Type-safe RedisTemplate configuration pattern
- ✅ Cache DTO creation rules
- ✅ Cache Service implementation templates
- ✅ Cache key naming conventions & versioning
- ✅ TTL guidelines cho các use cases
- ✅ Common pitfalls & solutions
- ✅ Testing & monitoring strategies

**Khi nào cần check**:

1. Trước khi tạo Cache DTO mới
2. Trước khi register RedisTemplate bean mới
3. Khi implement Cache Service
4. Khi gặp serialization errors
5. Khi quyết định TTL phù hợp
6. Khi debug cache hit/miss issues

**Checklist bắt buộc**:

- [ ] Cache DTO trong package `model/dto/cache/*`
- [ ] Bean name constant trong `RedisConfig.RedisTemplateBeanNames`
- [ ] Explicit `@Bean` method trong `RedisConfig`
- [ ] Service với `@Qualifier` annotation
- [ ] Cache key có version prefix (e.g., `entity:v1:id`)
- [ ] TTL được set explicitly (no eternal keys)

---

## API Implementation Rules

### 1. Controller Implementation

**LUÔN LUÔN** follow pattern trong `docs/api_endpoints_specification.md`:

✅ **ĐÚNG:**

```java
@RestController
@RequestMapping("/api/streams")
@RequiredArgsConstructor
@Tag(name = "Streams", description = "Livestream management APIs")
public class StreamController {

    // Public GET - theo spec
    @GetMapping
    @Operation(summary = "Get all live streams")
    public ApiResponse<List<StreamDTO>> getAllStreams() {
        // Implementation
    }

    // STREAMER + ADMIN - theo spec
    @PreAuthorize("hasAnyRole('STREAMER', 'ADMIN')")
    @PostMapping
    @Operation(summary = "Create new stream")
    public ApiResponse<StreamDTO> createStream(@Valid @RequestBody CreateStreamRequest request) {
        // Implementation
    }
}
```

❌ **SAI:**

- Tự tạo endpoint pattern không theo spec
- Thiếu Swagger annotations (@Tag, @Operation)
- Expose Entity thay vì DTO
- Không có authorization phù hợp
- Không dùng ApiResponse wrapper

### 2. Authorization Rules

**Two-Tier Strategy** (theo specification):

**Tier 1: URL-Level (SecurityConfig)**

- Dùng cho pattern-based authorization
- Ví dụ: `/api/admin/**` → `hasRole("ADMIN")`

**Tier 2: Method-Level (@PreAuthorize)**

- Dùng cho fine-grained control
- Ví dụ: Chỉ owner hoặc admin mới được update

```java
// Check owner hoặc admin
@PreAuthorize("hasRole('ADMIN') or @streamService.isStreamOwner(#streamId, authentication.principal.username)")
@PutMapping("/streams/{streamId}")
public ApiResponse<StreamDTO> updateStream(@PathVariable Long streamId, ...) { }
```

### 3. DTO Usage

**LUÔN LUÔN** dùng DTO cho API Input/Output:

✅ **ĐÚNG:**

```java
public ApiResponse<UserDTO> getUser(Long id) {
    User user = userService.getUserById(id);
    UserDTO dto = userService.convertToDTO(user);
    return ApiResponse.success(dto, null);
}
```

❌ **SAI:**

```java
public ApiResponse<User> getUser(Long id) {
    User user = userService.getUserById(id);
    return ApiResponse.success(user, null); // Exposing Entity!
}
```

### 4. Swagger Documentation

**LUÔN LUÔN** thêm Swagger annotations:

```java
@Tag(name = "Domain Name", description = "Domain description")
public class YourController {

    @Operation(summary = "Short summary", description = "Detailed description")
    @GetMapping("/endpoint")
    public ApiResponse<DTO> method() { }
}
```

### 5. API Documentation Requirements

**CHECKLIST bắt buộc khi implement Controller mới**:

#### Swagger Annotations

- [ ] `@Tag(name = "Domain Name", description = "...")` ở controller class
- [ ] `@Operation(summary = "...")` ở mỗi endpoint method
- [ ] `@Schema(description = "...", example = "...")` trong tất cả Request DTOs
- [ ] Example values phải realistic và match với test data

#### HTTP Request File

- [ ] Tạo file `.http/<controller-name>.http` (ví dụ: `.http/auth-controller.http`). **BẮT BUỘC** tên file .http phải tương ứng với tên file Controller của endpoint (lowercase, hyphen-separated).
- [ ] Chứa **TẤT CẢ** endpoints của controller. **KHÔNG** gộp nhiều controller vào một file .http duy nhất.
- [ ] Có variables cho reusable values:
  ```
  @host = http://localhost:8080
  @token = {{token}}
  @refreshToken = {{refreshToken}}
  ```
- [ ] Example requests với realistic data matching @Schema examples
- [ ] Script để auto-save tokens từ response (nếu cần)

**Workflow bắt buộc**:

```
1. Implement Controller + DTOs
2. Add Swagger annotations (@Tag, @Operation, @Schema)
3. Create .http file với all endpoints
4. Manual test qua .http file
5. Verify Swagger UI hiển thị đúng
6. Commit code
```

**Example Pattern**:

```java
@RestController
@RequestMapping("/api/streams")
@Tag(name = "Streams", description = "Livestream management APIs")
public class StreamController {

    @Operation(summary = "Create new stream")
    @PostMapping
    public ApiResponse<StreamDTO> createStream(
        @Valid @RequestBody CreateStreamRequest request) {
        // Implementation
    }
}

// CreateStreamRequest.java
public class CreateStreamRequest {
    @Schema(description = "Stream title", example = "My Gaming Stream")
    private String title;

    @Schema(description = "Stream description", example = "Playing Valorant ranked")
    private String description;
}
```

**Corresponding .http file** (`.http/stream-controller.http`):

```http
@host = http://localhost:8080
@token = {{token}}

### Create Stream
POST {{host}}/api/streams
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "title": "My Gaming Stream",
  "description": "Playing Valorant ranked"
}
```

Chi tiết examples xem: `docs/coding_standards.md`

---

## Database Design Rules

### ❌ CẤM SỬ DỤNG JPA Relationships Annotations

**KHÔNG được sử dụng:**

- `@ManyToMany`
- `@ManyToOne`
- `@OneToMany`
- `@OneToOne`

**LÝ DO:**

- Giảm coupling giữa entities
- Tránh N+1 query problem
- Dễ control performance
- Dễ debug và maintain
- Tránh lazy loading issues

**THAY VÀO ĐÓ:**

- Sử dụng **explicit join table entities**
- Query manually qua Repository khi cần
- Sử dụng DTO để compose data

**VÍ DỤ:**

❌ **SAI - Dùng @ManyToMany:**

```java
@Entity
public class User {
    @ManyToMany
    private Set<Role> roles;
}
```

✅ **ĐÚNG - Dùng Join Table Entity:**

```java
@Entity
public class User {
    private Long id;
    // No relationship annotations
}

@Entity
public class Role {
    private Long id;
}

@Entity
@Table(name = "user_roles")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;
}
```

---

## Development Workflow Rules

### 1. Plan Approval

- **LUÔN LUÔN** phải đợi user approve implementation plan trước khi EXECUTION
- Không được tự ý chuyển sang EXECUTION mode
- Sử dụng `notify_user` với `BlockedOnUser: true` để request approval

### 2. Build & Test

- **KHÔNG** được tự ý run `mvn compile`, `mvn test`, `mvn package`
- **KHÔNG** được tự ý run Docker commands
- **CHỈ** implement code thuần
- User sẽ tự run build/test khi cần

### 3. Code Implementation

- Focus vào code implementation
- Để user tự verify và test
- Chỉ fix compilation errors nếu được yêu cầu

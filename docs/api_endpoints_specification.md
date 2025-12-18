# API Endpoints Specification & Authorization Rules

> **Mục đích**: Tài liệu chuẩn cho việc implement và phân quyền API endpoints trong Spring Boot Livestream Backend.  
> **Target Audience**: AI Agent, Backend Developers  
> **Last Updated**: 2025-12-17

---

## 📋 **Table of Contents**

1. [Authorization Rules & Best Practices](#1-authorization-rules--best-practices)
2. [API Endpoints Specification](#2-api-endpoints-specification)
3. [SecurityConfig Template](#3-securityconfig-template)

---

## 1. Authorization Rules & Best Practices

### 1.1. Roles Definition

Dự án sử dụng **Role-Based Access Control (RBAC)** với 3 vai trò:

| Role         | Database Value  | Spring Security Authority | Description                                     |
| ------------ | --------------- | ------------------------- | ----------------------------------------------- |
| **User**     | `ROLE_USER`     | `ROLE_USER`               | Người dùng thường: Xem stream, chat, tặng quà   |
| **Streamer** | `ROLE_STREAMER` | `ROLE_STREAMER`           | Người phát sóng: Có thể tạo và quản lý stream   |
| **Admin**    | `ROLE_ADMIN`    | `ROLE_ADMIN`              | Quản trị viên: Quản lý hệ thống, users, báo cáo |

> ⚠️ **Lưu ý**: Database lưu với prefix `ROLE_`, nhưng khi dùng `hasRole()` trong code thì KHÔNG cần prefix.

---

### 1.2. Two-Tier Authorization Strategy

Dự án áp dụng **2 tầng phân quyền** để cân bằng giữa tập trung quản lý và linh hoạt:

#### **Tier 1: URL-Level Authorization (SecurityFilterChain)**

**Mục đích**: Phân quyền thô theo pattern endpoint

**Khi nào dùng**:

- Toàn bộ một nhóm endpoints có cùng rule (ví dụ: `/api/admin/**` → chỉ ADMIN)
- Public endpoints không cần authentication
- Development/Testing endpoints

**Ví dụ**:

```java
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/auth/**").permitAll()
    .anyRequest().authenticated()
)
```

**Ưu điểm**: Tập trung, dễ overview toàn bộ security rules

**Nhược điểm**: Không linh hoạt cho logic phức tạp (ví dụ: "Chỉ chủ stream hoặc Admin mới được sửa")

---

#### **Tier 2: Method-Level Authorization (@PreAuthorize)**

**Mục đích**: Phân quyền chi tiết cho từng endpoint cụ thể

**Khi nào dùng**:

- Một endpoint có logic phức tạp (ví dụ: Owner hoặc Admin)
- Cần kiểm tra điều kiện động (ví dụ: userId trong path phải trùng với user hiện tại)
- Override rule của URL-level

**Cú pháp**:

```java
// Chỉ ADMIN
@PreAuthorize("hasRole('ADMIN')")

// ADMIN hoặc STREAMER
@PreAuthorize("hasAnyRole('ADMIN', 'STREAMER')")

// ADMIN hoặc chính user đó
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")

// Gọi service method để check
@PreAuthorize("@streamService.isStreamOwner(#streamId, authentication.principal.username)")
```

**Ưu điểm**: Linh hoạt, dễ đọc trực tiếp ở Controller

**Nhược điểm**: Phân tán, phải scroll code để biết rules

---

### 1.3. Naming Convention

#### **hasRole() vs hasAuthority()**

| Method                       | Prefix Behavior      | Usage Example                           | When to Use                  |
| ---------------------------- | -------------------- | --------------------------------------- | ---------------------------- |
| `hasRole("ADMIN")`           | Tự động thêm `ROLE_` | `hasRole("ADMIN")` → check `ROLE_ADMIN` | Khuyến nghị cho RBAC         |
| `hasAuthority("ROLE_ADMIN")` | Không thêm gì        | `hasAuthority("ROLE_ADMIN")`            | Khi cần permissions chi tiết |

> 💡 **Best Practice**: Dùng `hasRole()` cho code ngắn gọn hơn.

---

### 1.4. Implementation Guidelines

#### **Rule 1: Public Endpoints trong SecurityConfig**

Toàn bộ endpoints **KHÔNG CẦN** authentication phải được khai báo trong `SecurityFilterChain`:

```java
.requestMatchers("/api/auth/**").permitAll()
.requestMatchers("/api/dev/**").permitAll() // Development only
.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
```

---

#### **Rule 2: URL-Level cho Patterns, Method-Level cho Exceptions**

**Ví dụ**:

- URL-Level: `/api/admin/**` → `hasRole("ADMIN")`
- Method-Level: Một endpoint cụ thể trong `/api/streams/**` cần thêm check "owner"

```java
// SecurityConfig
.requestMatchers("/api/streams/**").authenticated()

// StreamController
@PreAuthorize("@streamService.isStreamOwner(#streamId, authentication.principal.username)")
@PutMapping("/streams/{streamId}")
public ApiResponse<StreamDTO> updateStream(@PathVariable Long streamId, ...) {
    // Chỉ owner hoặc admin mới vào được đây
}
```

---

#### **Rule 3: Helper Methods trong Service**

Khi dùng `@PreAuthorize` với custom logic, tạo helper method trong Service:

```java
@Service
@Component("streamService") // PHẢI có tên bean
public class StreamService {

    public boolean isStreamOwner(Long streamId, String username) {
        Stream stream = streamRepository.findById(streamId)
                .orElseThrow(() -> new ResourceNotFoundException("Stream not found"));
        User currentUser = userService.findByUsername(username);
        return stream.getCreatorId().equals(currentUser.getId());
    }
}
```

---

#### **Rule 4: Swagger Annotations**

Mọi endpoint đều phải có Swagger annotations để generate API docs:

```java
@Tag(name = "Authentication", description = "User authentication and registration APIs")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // ...
    }
}
```

---

## 2. API Endpoints Specification

<a id="auth-api"></a>

### 2.1. Authentication (`/api/auth/**`)

**Authorization**: Public (permitAll)

| Endpoint             | Method | Description                 | Auth Level    | Allowed Roles | Implementation Notes        |
| -------------------- | ------ | --------------------------- | ------------- | ------------- | --------------------------- |
| `/api/auth/register` | POST   | Đăng ký tài khoản mới       | Public        | -             | Tự động gán `ROLE_USER`     |
| `/api/auth/login`    | POST   | Đăng nhập                   | Public        | -             | Trả về JWT token            |
| `/api/auth/refresh`  | POST   | Làm mới access token        | Public        | -             | Yêu cầu valid refresh token |
| `/api/auth/logout`   | POST   | Đăng xuất                   | Authenticated | All           | Revoke session trong DB     |
| `/api/auth/me`       | GET    | Lấy thông tin user hiện tại | Authenticated | All           | Return UserDTO              |

**SecurityConfig**:

```java
.requestMatchers("/api/auth/**").permitAll()
```

---

<a id="user-api"></a>

### 2.2. User Management (`/api/users/**`)

**Authorization**: Authenticated + Role-specific

| Endpoint                           | Method | Description        | Auth Level    | Allowed Roles | Implementation Notes                                      |
| ---------------------------------- | ------ | ------------------ | ------------- | ------------- | --------------------------------------------------------- |
| `/api/users/{userId}`              | GET    | Lấy thông tin user | Authenticated | All           | Public profile                                            |
| `/api/users/{userId}`              | PUT    | Cập nhật thông tin | Authenticated | Self + ADMIN  | `@PreAuthorize("#userId == auth.id or hasRole('ADMIN')")` |
| `/api/users/{userId}/wallet`       | GET    | Xem số dư ví       | Authenticated | Self + ADMIN  | Chỉ chính user hoặc admin                                 |
| `/api/users/{userId}/transactions` | GET    | Lịch sử giao dịch  | Authenticated | Self + ADMIN  | Pagination support                                        |

**SecurityConfig**:

```java
.requestMatchers("/api/users/**").authenticated()
```

**Controller Example**:

```java
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
@PutMapping("/users/{userId}")
public ApiResponse<UserDTO> updateUser(@PathVariable Long userId, ...) { }
```

---

<a id="stream-management"></a>

### 2.3. Stream Management (`/api/streams/**`)

**Authorization**: Mixed (Public view + Role-based management)

| Endpoint                          | Method | Description                | Auth Level    | Allowed Roles    | Implementation Notes                               |
| --------------------------------- | ------ | -------------------------- | ------------- | ---------------- | -------------------------------------------------- |
| `/api/streams`                    | GET    | Danh sách stream đang live | Public        | -                | Query: `is_live=true`                              |
| `/api/streams/{streamId}`         | GET    | Chi tiết stream            | Public        | -                | Include viewer count                               |
| `/api/streams`                    | POST   | Tạo stream mới             | Authenticated | STREAMER + ADMIN | `@PreAuthorize("hasAnyRole('STREAMER', 'ADMIN')")` |
| `/api/streams/{streamId}`         | PUT    | Cập nhật stream            | Authenticated | Owner + ADMIN    | `@PreAuthorize("@streamService.isOwner(...)")`     |
| `/api/streams/{streamId}`         | DELETE | Xóa stream                 | Authenticated | ADMIN            | `@PreAuthorize("hasRole('ADMIN')")`                |
| `/api/streams/{streamId}/start`   | POST   | Bắt đầu stream             | Authenticated | Owner + ADMIN    | Set `is_live=true`                                 |
| `/api/streams/{streamId}/end`     | POST   | Kết thúc stream            | Authenticated | Owner + ADMIN    | Set `is_live=false`                                |
| `/api/streams/{streamId}/viewers` | GET    | Realtime viewer count      | Public        | -                | Redis HyperLogLog                                  |

**SecurityConfig**:

```java
.requestMatchers(HttpMethod.GET, "/api/streams/**").permitAll() // Public viewing
.requestMatchers("/api/streams/**").authenticated() // Management requires auth
```

---

<a id="chat-api"></a>

### 2.4. Chat (`/api/chat/**`)

**Authorization**: Authenticated (WebSocket)

| Endpoint                       | Method | Description           | Auth Level    | Allowed Roles | Implementation Notes              |
| ------------------------------ | ------ | --------------------- | ------------- | ------------- | --------------------------------- |
| `/api/chat/{streamId}/history` | GET    | Lịch sử chat          | Public        | -             | Pagination, last 100 messages     |
| `/api/chat/{streamId}/mute`    | POST   | Mute user trong phòng | Authenticated | Owner + ADMIN | Add to Redis Set `muted:{roomId}` |
| `/api/chat/{streamId}/unmute`  | POST   | Unmute user           | Authenticated | Owner + ADMIN | Remove from Redis Set             |

**WebSocket Endpoints** (không qua HTTP, dùng STOMP):

- `/app/chat.send` → Gửi message (Authenticated, check muted)
- `/topic/chat.{streamId}` → Subscribe để nhận message

**SecurityConfig**:

```java
.requestMatchers(HttpMethod.GET, "/api/chat/**").permitAll()
.requestMatchers("/api/chat/**").authenticated()
```

---

<a id="gift-api"></a>

### 2.5. Gifts & Transactions (`/api/gifts/**`, `/api/transactions/**`)

**Authorization**: Authenticated

| Endpoint                            | Method | Description                | Auth Level    | Allowed Roles | Implementation Notes             |
| ----------------------------------- | ------ | -------------------------- | ------------- | ------------- | -------------------------------- |
| `/api/gifts`                        | GET    | Danh sách loại quà         | Public        | -             | Gift catalog                     |
| `/api/gifts/send`                   | POST   | Tặng quà                   | Authenticated | All           | Check balance, RabbitMQ async    |
| `/api/transactions`                 | GET    | Lịch sử giao dịch của user | Authenticated | Self + ADMIN  | `@PreAuthorize("self or admin")` |
| `/api/transactions/{transactionId}` | GET    | Chi tiết giao dịch         | Authenticated | Self + ADMIN  | Involved users only              |

**SecurityConfig**:

```java
.requestMatchers(HttpMethod.GET, "/api/gifts").permitAll()
.requestMatchers("/api/gifts/**", "/api/transactions/**").authenticated()
```

---

<a id="analytics-api"></a>

### 2.6. Analytics (`/api/analytics/**`)

**Authorization**: ADMIN only

| Endpoint                                   | Method | Description             | Auth Level    | Allowed Roles | Implementation Notes          |
| ------------------------------------------ | ------ | ----------------------- | ------------- | ------------- | ----------------------------- |
| `/api/analytics/dashboard`                 | GET    | Tổng quan hệ thống      | Authenticated | ADMIN         | Total users, streams, revenue |
| `/api/analytics/leaderboard`               | GET    | Bảng xếp hạng           | Public        | -             | Daily/Weekly top gifters      |
| `/api/analytics/streams/{streamId}/report` | GET    | Báo cáo chi tiết stream | Authenticated | Owner + ADMIN | Revenue, viewers, chat stats  |

**SecurityConfig**:

```java
.requestMatchers("/api/analytics/leaderboard").permitAll()
.requestMatchers("/api/analytics/**").hasRole("ADMIN")
```

---

<a id="admin-api"></a>

### 2.7. Admin (`/api/admin/**`)

**Authorization**: ADMIN only

| Endpoint                          | Method | Description            | Auth Level    | Allowed Roles | Implementation Notes      |
| --------------------------------- | ------ | ---------------------- | ------------- | ------------- | ------------------------- |
| `/api/admin/users`                | GET    | Danh sách tất cả users | Authenticated | ADMIN         | Pagination + filters      |
| `/api/admin/users/{userId}/ban`   | POST   | Ban user               | Authenticated | ADMIN         | Add to blacklist          |
| `/api/admin/users/{userId}/unban` | POST   | Unban user             | Authenticated | ADMIN         | Remove from blacklist     |
| `/api/admin/users/{userId}/roles` | PUT    | Thay đổi roles         | Authenticated | ADMIN         | Promote to STREAMER/ADMIN |
| `/api/admin/streams`              | GET    | Tất cả streams         | Authenticated | ADMIN         | Include inactive          |
| `/api/admin/transactions`         | GET    | Tất cả giao dịch       | Authenticated | ADMIN         | For audit                 |

**SecurityConfig**:

```java
.requestMatchers("/api/admin/**").hasRole("ADMIN")
```

---

### 2.8. Development/Simulation (`/api/dev/**`)

**Authorization**: Public (Development only - disable in production)

| Endpoint                            | Method | Description             | Auth Level | Allowed Roles | Implementation Notes      |
| ----------------------------------- | ------ | ----------------------- | ---------- | ------------- | ------------------------- |
| `/api/dev/simulate/stream/start`    | POST   | Giả lập bắt đầu stream  | Public     | -             | Input: `{streamKey}`      |
| `/api/dev/simulate/stream/end`      | POST   | Giả lập kết thúc stream | Public     | -             | Input: `{streamKey}`      |
| `/api/dev/simulate/payment/deposit` | POST   | Giả lập nạp tiền        | Public     | -             | Input: `{userId, amount}` |

**SecurityConfig**:

```java
.requestMatchers("/api/dev/**").permitAll() // TODO: Disable in production
```

> ⚠️ **Production**: Phải tắt hoặc bảo vệ endpoints này bằng IP whitelist.

---

### 2.9. Testing (`/api/test/**`)

**Authorization**: Public (Development only)

| Endpoint             | Method | Description            | Auth Level | Allowed Roles | Implementation Notes |
| -------------------- | ------ | ---------------------- | ---------- | ------------- | -------------------- |
| `/api/test/sql`      | GET    | Test P6Spy SQL logging | Public     | -             | Trigger DB query     |
| `/api/test/redis`    | GET    | Test Redis connection  | Public     | -             | PING command         |
| `/api/test/rabbitmq` | GET    | Test RabbitMQ          | Public     | -             | Send test message    |

**SecurityConfig**:

```java
.requestMatchers("/api/test/**").permitAll() // TODO: Remove in production
```

---

## 3. SecurityConfig Template

### 3.1. Complete SecurityFilterChain

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // REQUIRED for @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    // ============================================================
                    // PUBLIC ENDPOINTS (No Authentication Required)
                    // ============================================================

                    // Authentication
                    .requestMatchers("/api/auth/**").permitAll()

                    // Development/Testing (TODO: Disable in production)
                    .requestMatchers("/api/dev/**").permitAll()
                    .requestMatchers("/api/test/**").permitAll()

                    // Swagger/OpenAPI Documentation
                    .requestMatchers(
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/swagger-resources/**",
                            "/webjars/**"
                    ).permitAll()

                    // Public viewing endpoints
                    .requestMatchers(HttpMethod.GET, "/api/streams/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/gifts").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/analytics/leaderboard").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/chat/*/history").permitAll()

                    // ============================================================
                    // ROLE-BASED ENDPOINTS
                    // ============================================================

                    // Admin Only
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/analytics/**").hasRole("ADMIN") // Except leaderboard

                    // ============================================================
                    // AUTHENTICATED ENDPOINTS (All Roles)
                    // ============================================================

                    // All other endpoints require authentication
                    // Fine-grained authorization will be handled by @PreAuthorize
                    .anyRequest().authenticated())
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
```

---

### 3.2. Controller Template với Authorization

```java
@RestController
@RequestMapping("/api/streams")
@RequiredArgsConstructor
@Tag(name = "Streams", description = "Livestream management APIs")
public class StreamController {

    private final StreamService streamService;

    // ============================================================
    // PUBLIC ENDPOINTS
    // ============================================================

    @GetMapping
    @Operation(summary = "Get all live streams", description = "Public endpoint to view all active streams")
    public ApiResponse<List<StreamDTO>> getAllStreams(
            @RequestParam(defaultValue = "true") boolean liveOnly) {
        List<StreamDTO> streams = streamService.getAllStreams(liveOnly);
        return ApiResponse.success(streams, null);
    }

    // ============================================================
    // STREAMER + ADMIN: Create Stream
    // ============================================================

    @PreAuthorize("hasAnyRole('STREAMER', 'ADMIN')")
    @PostMapping
    @Operation(summary = "Create new stream", description = "Only STREAMER and ADMIN can create streams")
    public ApiResponse<StreamDTO> createStream(@Valid @RequestBody CreateStreamRequest request) {
        StreamDTO stream = streamService.createStream(request);
        return ApiResponse.success(stream, "Stream created successfully");
    }

    // ============================================================
    // OWNER + ADMIN: Update Stream
    // ============================================================

    @PreAuthorize("hasRole('ADMIN') or @streamService.isStreamOwner(#streamId, authentication.principal.username)")
    @PutMapping("/{streamId}")
    @Operation(summary = "Update stream", description = "Only stream owner or ADMIN can update")
    public ApiResponse<StreamDTO> updateStream(
            @PathVariable Long streamId,
            @Valid @RequestBody UpdateStreamRequest request) {
        StreamDTO stream = streamService.updateStream(streamId, request);
        return ApiResponse.success(stream, "Stream updated successfully");
    }

    // ============================================================
    // ADMIN ONLY: Delete Stream
    // ============================================================

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{streamId}")
    @Operation(summary = "Delete stream", description = "Only ADMIN can delete streams")
    public ApiResponse<Void> deleteStream(@PathVariable Long streamId) {
        streamService.deleteStream(streamId);
        return ApiResponse.success(null, "Stream deleted successfully");
    }
}
```

---

## 4. Quick Reference

### 4.1. Common @PreAuthorize Patterns

```java
// Chỉ ADMIN
@PreAuthorize("hasRole('ADMIN')")

// ADMIN hoặc STREAMER
@PreAuthorize("hasAnyRole('ADMIN', 'STREAMER')")

// Chính user đó hoặc ADMIN
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")

// Owner của resource hoặc ADMIN
@PreAuthorize("hasRole('ADMIN') or @serviceBean.isOwner(#resourceId, authentication.principal.username)")

// Kết hợp nhiều điều kiện
@PreAuthorize("hasRole('USER') and @walletService.hasSufficientBalance(authentication.principal.id, #amount)")
```

---

### 4.2. SecurityConfig Checklist

Khi implement SecurityConfig, đảm bảo:

- [ ] `@EnableMethodSecurity` đã được bật
- [ ] Public endpoints (`/api/auth/**`, `/swagger-ui/**`) được `permitAll()`
- [ ] Development endpoints (`/api/dev/**`, `/api/test/**`) được đánh dấu TODO để disable production
- [ ] Admin endpoints (`/api/admin/**`) require `hasRole("ADMIN")`
- [ ] Các endpoints còn lại default là `authenticated()`
- [ ] JWT Filter được add vào filter chain
- [ ] PasswordEncoder là BCrypt

---

### 4.3. Testing Authorization

**Postman/Thunder Client**:

```bash
# 1. Login to get token
POST http://localhost:8080/api/auth/login
{
  "username": "admin",
  "password": "password"
}

# Response: { "accessToken": "eyJhbGc..." }

# 2. Use token in subsequent requests
GET http://localhost:8080/api/admin/users
Authorization: Bearer eyJhbGc...
```

**JUnit Test**:

```java
@Test
@WithMockUser(roles = "ADMIN")
void testAdminEndpoint() {
    // This will pass
}

@Test
@WithMockUser(roles = "USER")
void testAdminEndpoint_shouldDeny() {
    // This should return 403 Forbidden
}
```

---

## 5. Appendix: Role Matrix

| Feature           | Public | USER | STREAMER        | ADMIN    |
| ----------------- | ------ | ---- | --------------- | -------- |
| View streams      | ✅     | ✅   | ✅              | ✅       |
| Chat              | ❌     | ✅   | ✅              | ✅       |
| Send gifts        | ❌     | ✅   | ✅              | ✅       |
| Create stream     | ❌     | ❌   | ✅              | ✅       |
| Update own stream | ❌     | ❌   | ✅ (own)        | ✅ (all) |
| Delete stream     | ❌     | ❌   | ❌              | ✅       |
| Mute users        | ❌     | ❌   | ✅ (own stream) | ✅       |
| View analytics    | ❌     | ❌   | ✅ (own stream) | ✅ (all) |
| User management   | ❌     | ❌   | ❌              | ✅       |

---

**End of Document**

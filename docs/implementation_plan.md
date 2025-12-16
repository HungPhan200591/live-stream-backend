# Implementation Plan: Spring Boot Livestream Backend

## Context

- **Dự án**: Spring Boot Livestream Backend.
- **Mục tiêu**: Xây dựng nhanh để test tính năng & performance (Redis, Queue).
- **Kiến trúc**: **Layered Architecture** (Controller -> Service -> Repository). Ưu tiên sự gọn gàng, thực dụng, không over-engineering.

## Phase 1: Foundation & Standard Structure (Móng nhà)

- [ ] **Init Project**: Tạo Spring Boot project với dependencies:
  - Web, Security, Data JPA, Validation.
  - Redis (Jedis/Lettuce), RabbitMQ, WebSocket.
  - Lombok, PostgreSQL Driver.
- [ ] **Docker Infrastructure**:
  - `docker-compose.yml`: PostgreSQL 8, Redis (latest), RabbitMQ (management plugin).
- [ ] **Package Structure Setup** (Standard Layered):
  - `com.stream.demo.config`: App configurations (RedisConfig, SecurityConfig, OpenAPI).
  - `com.stream.demo.controller`: REST API endpoints.
  - `com.stream.demo.service`: Business Logic.
  - `com.stream.demo.repository`: JPA Repositories.
  - `com.stream.demo.model`: Entities & DTOs.
  - `com.stream.demo.common`: `ApiResponse`, `GlobalExceptionHandler`, `Utils`.

## Phase 2: Core Simulation (First Priority)

_Mục đích: Có API giả lập để test ngay logic luồng stream và tiền tệ mà không cần đợi FE/3rd party._

- [ ] **Create Simulation Controller**:
  - `POST /api/dev/stream/start`: Giả lập Webhook OBS mở luồng -> Update DB -> Notify logic.
  - `POST /api/dev/stream/end`: Giả lập tắt luồng.
  - `POST /api/dev/payment/deposit`: Giả lập nạp tiền vào ví User.

## Phase 3: Authentication & User Management

### 3.1. Entity Layer (JPA Entities)

- [ ] **User Entity**: `User.java`
  - Fields: `id`, `username`, `email`, `passwordHash`, `createdAt`, `updatedAt`.
  - **KHÔNG dùng `@ManyToMany`** với Role (theo rule anti-JPA-relationship).
- [ ] **Role Entity**: `Role.java`
  - Fields: `id`, `name` (ROLE_USER, ROLE_STREAMER, ROLE_ADMIN).
- [ ] **UserRole Join Table**: `UserRole.java`
  - Fields: `id`, `userId`, `roleId`, `assignedAt`.
  - Explicit join entity thay vì `@ManyToMany`.

### 3.2. Repository Layer

- [ ] **UserRepository**: `findByUsername()`, `findByEmail()`, `existsByUsername()`, `existsByEmail()`.
- [ ] **RoleRepository**: `findByName()`.
- [ ] **UserRoleRepository**: `findByUserId()`, `deleteByUserIdAndRoleId()`.

### 3.3. DTOs (Input/Output)

- [ ] **Request DTOs**:
  - `RegisterRequest`: `username`, `email`, `password`.
  - `LoginRequest`: `username`, `password`.
  - `RefreshTokenRequest`: `refreshToken`.
- [ ] **Response DTOs**:
  - `AuthResponse`: `accessToken`, `refreshToken`, `tokenType`, `expiresIn`.
  - `UserDTO`: `id`, `username`, `email`, `roles[]`, `createdAt`.

### 3.4. Security Configuration

- [ ] **SecurityConfig** (`@EnableWebSecurity`, `@EnableMethodSecurity`):
  - Stateless Session (`SessionCreationPolicy.STATELESS`).
  - **URL-Level Authorization** (Two-Tier Strategy):
    - Public: `/api/auth/**`, `/api/dev/**`, `/swagger-ui/**`.
    - Admin Only: `/api/admin/**`.
    - Others: `.authenticated()`.
  - Add `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`.
  - `PasswordEncoder` bean (BCrypt).
  - `AuthenticationManager` bean.

### 3.5. JWT Implementation

- [ ] **JwtTokenProvider**:
  - `generateAccessToken(UserDetails)`: Tạo JWT với expiry 1h.
  - `generateRefreshToken(UserDetails)`: Tạo JWT với expiry 7 days.
  - `validateToken(String token)`: Verify signature + expiration.
  - `getUsernameFromToken(String token)`: Extract username.
- [ ] **JwtAuthenticationFilter** (`OncePerRequestFilter`):
  - Extract JWT từ `Authorization: Bearer` header.
  - Validate token → Load UserDetails → Set Authentication vào SecurityContext.
  - Skip filter cho public endpoints.
- [ ] **Redis JWT Blacklist** (Force Logout):
  - `RedisTemplate<String, String>` bean config.
  - `addToBlacklist(String token, long expirySeconds)`: Store token vào Redis với TTL.
  - `isBlacklisted(String token)`: Check trước khi validate.

### 3.6. UserDetailsService

- [ ] **CustomUserDetailsService** implements `UserDetailsService`:
  - `loadUserByUsername(String username)`:
    - Query User từ DB.
    - Load Roles qua UserRole join table.
    - Return `org.springframework.security.core.userdetails.User` với authorities.

### 3.7. Auth API Controllers

- [ ] **AuthController** (`/api/auth/**`):
  - `POST /api/auth/register`: Đăng ký user mới, tự động gán `ROLE_USER`.
  - `POST /api/auth/login`: Authenticate → Return `AuthResponse` (access + refresh token).
  - `POST /api/auth/refresh`: Input `refreshToken` → Validate → Return new `accessToken`.
  - `POST /api/auth/logout`: Add current token vào Redis blacklist.
  - `GET /api/auth/me`: Return `UserDTO` của user hiện tại (authenticated).

### 3.8. Service Layer

- [ ] **AuthService**:
  - `register(RegisterRequest)`: Validate → Hash password → Save User → Assign ROLE_USER.
  - `login(LoginRequest)`: Authenticate → Generate tokens → Return AuthResponse.
  - `refreshAccessToken(String refreshToken)`: Validate refresh token → Generate new access token.
  - `logout(String token)`: Add token to blacklist.
- [ ] **UserService**:
  - `getUserById(Long id)`: Return UserDTO.
  - `getCurrentUser()`: Get from SecurityContext.
  - `assignRole(Long userId, String roleName)`: Create UserRole entry.

### 3.9. Exception Handling

- [ ] **GlobalExceptionHandler** (`@RestControllerAdvice`):
  - `@ExceptionHandler(AuthenticationException)`: Return 401 Unauthorized.
  - `@ExceptionHandler(AccessDeniedException)`: Return 403 Forbidden.
  - `@ExceptionHandler(JwtException)`: Return 401 Invalid Token.

### 3.10. Swagger/OpenAPI Config

- [ ] **OpenApiConfig**:
  - `@SecurityScheme` với `BearerAuth` (JWT).
  - `@Tag` cho AuthController: "Authentication APIs".
  - Endpoints có `@Operation` descriptions.

### 3.11. Testing Utilities (Development)

- [ ] **Data Seeding** (Optional):
  - `DataInitializer` tạo users mẫu (admin, streamer, user) khi dev mode.
  - Pre-populate roles: ROLE_USER, ROLE_STREAMER, ROLE_ADMIN.

## Phase 4: Economy & Transaction System (Core Logic)

- [ ] **Wallet Module**:
  - Entity `Wallet`, `Transaction`.
  - `WalletService.deposit()`:Transactional.
- [ ] **Gifting Logic** (Critical):
  - `GiftService.sendGift()`: Optimistic Locking check balance -> Deduct balance.
  - **Async Processing**: Gửi message `GiftEvent` vào RabbitMQ (`gift.queue`).
- [ ] **Consumer**:
  - `GiftConsumer`: Nhận message -> Cộng tiền Streamer -> Lưu log -> Push Notification.

## Phase 5: Real-time Chat

- [ ] **WebSocket Config**: Enable Broker, configure Stomp endpoints (`/ws`).
- [ ] **Redis Pub/Sub Integration**:
  - Giúp scale chat server (Message từ user A -> Server 1 -> Redis -> Server 2 -> User B).
- [ ] **Chat Persistence**:
  - RabbitMQ Queue `chat.log`.
  - Consumer đọc batch và lưu xuống DB (giảm tải DB writes).

## Phase 6: Verify & Polish

- [ ] **Load Test Simulation**: Viết script đơn giản để giả lập spam chat/gift.
- [ ] **API Docs**: Verify Swagger UI (`/swagger-ui.html`).

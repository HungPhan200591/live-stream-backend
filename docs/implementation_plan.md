# Implementation Plan: Spring Boot Livestream Backend

## Executive Summary

**Project**: Spring Boot Livestream Backend  
**Architecture**: Layered Architecture (Controller → Service → Repository)  
**Philosophy**: Pragmatic & Fast, Simulation First, KISS & YAGNI  
**Current Status**: ✅ Phase 3 Completed (Authentication & User Management)

---

## Table of Contents

1. [x] [Phase 1: Foundation & Infrastructure](#phase-1-foundation--infrastructure)
2. [x] [Phase 2: Development Simulation APIs](#phase-2-development-simulation-apis)
3. [x] [Phase 3: Authentication & User Management](#phase-3-authentication--user-management)
4. [ ] [Phase 4: Stream Management Module](#phase-4-stream-management-module)
5. [ ] [Phase 5: Economy & Transaction System](#phase-5-economy--transaction-system)
6. [ ] [Phase 6: Real-time Chat System](#phase-6-real-time-chat-system)
7. [ ] [Phase 7: Gift System & Async Processing](#phase-7-gift-system--async-processing)
8. [ ] [Phase 8: Analytics & Leaderboard](#phase-8-analytics--leaderboard)
9. [ ] [Phase 9: Admin Management Module](#phase-9-admin-management-module)
10. [ ] [Phase 10: Production Readiness & Polish](#phase-10-production-readiness--polish)
11. [ ] [Phase 11: Social Features (Optional)](#phase-11-social-features-optional)
12. [ ] [Phase 12: Notification System (Optional)](#phase-12-notification-system-optional)

---

## Phase 1: Foundation & Infrastructure

**Status**: ✅ COMPLETED  
**Goal**: Thiết lập project structure và infrastructure services

### Checklist

- [x] Init Spring Boot project với dependencies
- [x] Docker Compose: PostgreSQL, Redis, RabbitMQ
- [x] Package structure (Layered Architecture)
- [x] Common utilities: `ApiResponse`, `GlobalExceptionHandler`
- [x] OpenAPI/Swagger configuration

---

## Phase 2: Development Simulation APIs

**Status**: ✅ COMPLETED  
**Goal**: Tạo simulation endpoints để test logic mà không cần external dependencies

### Checklist

- [x] `POST /api/dev/simulate/stream/start` - Giả lập OBS stream start
- [x] `POST /api/dev/simulate/stream/end` - Giả lập OBS stream end
- [x] `POST /api/dev/simulate/payment/deposit` - Giả lập nạp tiền
- [x] Testing endpoints: `/api/test/sql`, `/api/test/redis`, `/api/test/rabbitmq`

---

## Phase 3: Authentication & User Management

**Status**: ✅ COMPLETED  
**Goal**: JWT-based authentication với RBAC (Role-Based Access Control)

### 3.1. Entity Layer

- [x] `User` entity (NO `@ManyToMany`)
- [x] `Role` entity (ROLE_USER, ROLE_STREAMER, ROLE_ADMIN)
- [x] `UserRole` explicit join table entity

### 3.2. Repository Layer

- [x] `UserRepository`: `findByUsername()`, `findByEmail()`, `existsByUsername()`, `existsByEmail()`
- [x] `RoleRepository`: `findByName()`
- [x] `UserRoleRepository`: `findByUserId()`, `deleteByUserIdAndRoleId()`

### 3.3. DTOs

- [x] Request: `RegisterRequest`, `LoginRequest`, `RefreshTokenRequest`
- [x] Response: `AuthResponse`, `UserDTO`

### 3.4. Security Configuration

- [x] `SecurityConfig`: Two-Tier Authorization Strategy
- [x] `JwtAuthenticationFilter`: Extract & validate JWT
- [x] Session-based Authentication (user_sessions table)
- [x] `CustomUserDetailsService`: Load user with roles

### 3.5. JWT Implementation

- [x] `JwtTokenProvider`: Generate access/refresh tokens, validate
- [x] Session verification for refresh token

### 3.6. Auth API Controllers

- [x] `POST /api/auth/register`
- [x] `POST /api/auth/login`
- [x] `POST /api/auth/refresh`
- [x] `POST /api/auth/logout`
- [x] `GET /api/auth/me`

### 3.7. Service Layer

- [x] `AuthService`: register, login, refresh, logout
- [x] `UserService`: getUserById, getCurrentUser, assignRole

### 3.8. Exception Handling

- [x] `GlobalExceptionHandler`: 401 Unauthorized, 403 Forbidden, JWT exceptions

### 3.9. Swagger/OpenAPI

- [x] `@SecurityScheme` với BearerAuth
- [x] `@Tag` và `@Operation` annotations

### 3.10. Data Seeding

- [x] `DataInitializer`: Pre-populate roles và seed users (admin, streamer, user)

### 3.11. User Management API Controllers

- [ ] **UserController** (`/api/users/**`)

  - `GET /api/users/{userId}`: Lấy thông tin user (Public profile)

    - Return: `UserDTO` (public fields only: id, username, display name, avatar)

  - `PUT /api/users/{userId}`: Cập nhật thông tin user
    - `@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")`
    - Body: `UpdateUserRequest` (displayName, bio, avatarUrl, email)
    - Validate: Chỉ chính user hoặc ADMIN

---

## Phase 4: Stream Management Module

**Goal**: Quản lý livestreams với Redis tracking và database persistence

### 4.1. Entity Layer

- [ ] **Stream Entity** (`Stream.java`)

  - Fields: `id`, `creatorId`, `streamKey`, `title`, `description`, `isLive`, `startedAt`, `endedAt`, `createdAt`, `updatedAt`
  - **NO** `@ManyToOne` với User (chỉ lưu `creatorId`)
  - Index: `streamKey` (unique), `creatorId`, `isLive`

- [ ] **StreamStats Entity** (`StreamStats.java`) - Optional
  - Fields: `id`, `streamId`, `peakViewers`, `totalViews`, `totalGifts`, `revenue`
  - Để track analytics cho stream

### 4.2. Repository Layer

- [ ] **StreamRepository**
  - `findByStreamKey(String streamKey)`: Tìm stream theo key
  - `findByCreatorId(Long creatorId)`: Danh sách stream của streamer
  - `findByIsLiveTrue()`: Danh sách stream đang live
  - `existsByStreamKey(String streamKey)`: Check duplicate key
  - `findByIdAndCreatorId(Long id, Long creatorId)`: Ownership verification

### 4.3. DTOs

- [ ] **Request DTOs**

  - `CreateStreamRequest`: `title`, `description`
  - `UpdateStreamRequest`: `title`, `description`
  - `StartStreamRequest`: `streamKey`
  - `EndStreamRequest`: `streamKey`

- [ ] **Response DTOs**
  - `StreamDTO`: `id`, `creatorUsername`, `streamKey`, `title`, `description`, `isLive`, `startedAt`, `viewerCount`
  - `StreamDetailsDTO`: Extends `StreamDTO` + `stats`, `chatHistory`

### 4.4. Service Layer

- [ ] **StreamService**

  - `createStream(CreateStreamRequest, User currentUser)`: Tạo stream mới

    - Generate unique `streamKey` (UUID hoặc custom algorithm)
    - Set `creatorId` = current user
    - Set `isLive` = false
    - Validate: Chỉ STREAMER/ADMIN mới được tạo

  - `updateStream(Long streamId, UpdateStreamRequest, User currentUser)`: Update metadata

    - Validate ownership: `stream.creatorId == currentUser.id` hoặc ADMIN

  - `deleteStream(Long streamId, User currentUser)`: Soft delete hoặc hard delete

    - Chỉ ADMIN

  - `startStream(String streamKey)`: Bắt đầu stream

    - Set `isLive` = true
    - Set `startedAt` = now
    - Redis: `stream:{streamId}:live` = "true" (TTL 24h)
    - Trigger notification event (RabbitMQ)

  - `endStream(String streamKey)`: Kết thúc stream

    - Set `isLive` = false
    - Set `endedAt` = now
    - Cleanup Redis keys
    - Save stats to DB

  - `getStreamById(Long streamId)`: Public viewing

  - `getAllLiveStreams()`: Query `isLive = true` + Redis cache

  - `getStreamsByCreator(Long creatorId)`: Streamer's dashboard

  - **Owner Verification Helper**:
    - `isStreamOwner(Long streamId, String username)`: For `@PreAuthorize`

### 4.5. Redis Integration

- [ ] **LiveStreamCache** (Service hoặc Util)
  - `addViewer(Long streamId, Long userId)`: HyperLogLog
    ```redis
    PFADD stream:{streamId}:viewers {userId}
    ```
  - `getViewerCount(Long streamId)`: Count unique viewers
    ```redis
    PFCOUNT stream:{streamId}:viewers
    ```
  - `setLiveStatus(Long streamId, boolean isLive)`: Cache live status
    ```redis
    SET stream:{streamId}:live {isLive} EX 86400
    ```

### 4.6. Controller Layer

- [ ] **StreamController** (`/api/streams/**`)

  **Public Endpoints**:

  - `GET /api/streams`: Danh sách stream đang live
    - Query params: `?liveOnly=true`, pagination
  - `GET /api/streams/{streamId}`: Chi tiết stream
    - Include viewer count từ Redis
  - `GET /api/streams/{streamId}/viewers`: Realtime viewer count

  **Authenticated Endpoints** (STREAMER + ADMIN):

  - `POST /api/streams`: Tạo stream mới

    - `@PreAuthorize("hasAnyRole('STREAMER', 'ADMIN')")`

  - `PUT /api/streams/{streamId}`: Cập nhật stream

    - `@PreAuthorize("hasRole('ADMIN') or @streamService.isStreamOwner(#streamId, authentication.principal.username)")`

  - `POST /api/streams/{streamId}/start`: Bắt đầu stream

    - `@PreAuthorize("hasRole('ADMIN') or @streamService.isStreamOwner(#streamId, authentication.principal.username)")`

  - `POST /api/streams/{streamId}/end`: Kết thúc stream
    - `@PreAuthorize("hasRole('ADMIN') or @streamService.isStreamOwner(#streamId, authentication.principal.username)")`

  **Admin Only**:

  - `DELETE /api/streams/{streamId}`: Xóa stream
    - `@PreAuthorize("hasRole('ADMIN')")`

### 4.7. Event Publishing (RabbitMQ)

- [ ] **StreamEventPublisher**
  - `publishStreamStartedEvent(StreamDTO stream)`: Notify followers
    - Queue: `notifications.stream.started`
  - `publishStreamEndedEvent(StreamDTO stream)`: Cleanup tasks
    - Queue: `notifications.stream.ended`

### 4.8. Testing

- [ ] Unit Tests
  - `StreamServiceTest`: Test business logic
  - `StreamRepositoryTest`: Test queries
- [ ] Integration Tests
  - `StreamControllerTest`: Test authorization rules
  - Test Redis HyperLogLog accuracy

---

## Phase 5: Economy & Transaction System

**Goal**: Wallet management với atomic transactions và audit trail

### 5.1. Entity Layer

- [ ] **Wallet Entity** (`Wallet.java`)

  - Fields: `id`, `userId`, `balance` (BigDecimal), `currency` (default "COINS"), `version` (Optimistic Locking), `updatedAt`
  - Unique constraint: `userId`
  - **NO** `@OneToOne` với User

- [ ] **Transaction Entity** (`Transaction.java`)
  - Fields: `id`, `fromUserId` (nullable), `toUserId`, `amount`, `type` (DEPOSIT, GIFT, WITHDRAWAL, REFUND), `status` (PENDING, COMPLETED, FAILED), `metadata` (JSON), `createdAt`
  - Index: `fromUserId`, `toUserId`, `type`, `status`, `createdAt`

### 5.2. Repository Layer

- [ ] **WalletRepository**

  - `findByUserId(Long userId)`: Get user's wallet
  - `existsByUserId(Long userId)`: Check wallet existence
  - Custom query: `@Lock(LockModeType.OPTIMISTIC)` for concurrent updates

- [ ] **TransactionRepository**
  - `findByFromUserIdOrToUserId(Long userId)`: User's transaction history
  - `findByType(TransactionType type)`: Filter by type
  - `findByCreatedAtBetween(LocalDateTime start, LocalDateTime end)`: Date range
  - `sumAmountByToUserIdAndType(Long userId, TransactionType type)`: Total received

### 5.3. DTOs

- [ ] **Request DTOs**

  - `DepositRequest`: `userId`, `amount` (Admin hoặc Simulation only)
  - `TransferRequest`: `toUserId`, `amount` (Internal use)

- [ ] **Response DTOs**
  - `WalletDTO`: `userId`, `username`, `balance`, `currency`, `updatedAt`
  - `TransactionDTO`: `id`, `fromUsername`, `toUsername`, `amount`, `type`, `status`, `createdAt`
  - `TransactionHistoryResponse`: List<TransactionDTO>, Pagination metadata

### 5.4. Service Layer

- [ ] **WalletService**

  - `createWallet(Long userId)`: Auto-create khi user đăng ký

    - Initial balance = 0

  - `getWallet(Long userId)`: Get user's wallet

    - Throw exception nếu không tồn tại

  - `deposit(Long userId, BigDecimal amount, String metadata)`: Nạp tiền

    - Validate: amount > 0
    - **Transactional**:
      1. Lock wallet (Optimistic Locking)
      2. Update balance: `balance = balance + amount`
      3. Create Transaction record (type=DEPOSIT, status=COMPLETED)
    - Return: WalletDTO

  - `deduct(Long userId, BigDecimal amount, String reason)`: Trừ tiền

    - Validate: amount > 0
    - Check balance: `balance >= amount`
    - **Transactional**:
      1. Lock wallet
      2. Update balance: `balance = balance - amount`
      3. Create Transaction record (type=GIFT/WITHDRAWAL)
    - Throw `InsufficientBalanceException` nếu không đủ tiền

  - `transfer(Long fromUserId, Long toUserId, BigDecimal amount)`: Chuyển tiền

    - **Transactional**:
      1. Deduct from sender
      2. Deposit to receiver
      3. Create Transaction record (type=GIFT, fromUserId, toUserId)

  - `getBalance(Long userId)`: Quick balance check
    - Cache trong Redis: `wallet:{userId}:balance` (TTL 60s)

- [ ] **TransactionService**

  - `getTransactionHistory(Long userId, Pageable pageable)`: Lịch sử giao dịch

    - Query: `fromUserId = userId OR toUserId = userId`
    - Sort by `createdAt DESC`

  - `getTransactionById(Long transactionId, User currentUser)`: Chi tiết giao dịch

    - Validate: User phải là sender hoặc receiver hoặc ADMIN

  - `getTotalRevenue(Long userId)`: Tổng tiền nhận được
    - Sum amount where `toUserId = userId AND type = GIFT`

### 5.5. Controller Layer

- [ ] **WalletController** (`/api/users/{userId}/wallet`)

  - `GET /api/users/{userId}/wallet`: Xem số dư

    - `@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")`

  - `GET /api/users/{userId}/transactions`: Lịch sử giao dịch
    - `@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")`
    - Pagination support

- [ ] **TransactionController** (`/api/transactions/**`)

  - `GET /api/transactions/{transactionId}`: Chi tiết giao dịch
    - `@PreAuthorize("@transactionService.canViewTransaction(#transactionId, authentication.principal.id)")`

### 5.6. Exception Handling

- [ ] Custom Exceptions
  - `InsufficientBalanceException`: Không đủ tiền (400 Bad Request)
  - `WalletNotFoundException`: Wallet không tồn tại (404 Not Found)
  - `InvalidAmountException`: Số tiền không hợp lệ (400 Bad Request)

### 5.7. Testing

- [ ] Unit Tests
  - `WalletServiceTest`: Test Optimistic Locking
  - Test concurrent transactions (multithreading)
- [ ] Integration Tests
  - Test deposit → deduct flow
  - Test insufficient balance scenario

---

## Phase 6: Real-time Chat System

**Goal**: WebSocket-based chat với Redis Pub/Sub và RabbitMQ persistence

### 6.1. Entity Layer

- [ ] **ChatLog Entity** (`ChatLog.java`)
  - Fields: `id`, `streamId`, `userId`, `username`, `content`, `type` (MESSAGE, SYSTEM, GIFT_ALERT), `createdAt`
  - Index: `streamId`, `createdAt`

### 6.2. Repository Layer

- [ ] **ChatLogRepository**
  - `findByStreamIdOrderByCreatedAtDesc(Long streamId, Pageable pageable)`: Recent messages
  - `countByStreamId(Long streamId)`: Total messages
  - Batch insert support: `saveAll(List<ChatLog>)`

### 6.3. DTOs

- [ ] **Request DTOs**

  - `ChatMessageRequest`: `streamId`, `content`
  - `MuteUserRequest`: `streamId`, `userId`, `duration` (seconds)

- [ ] **Response DTOs**
  - `ChatMessageDTO`: `id`, `username`, `content`, `type`, `timestamp`
  - `ChatHistoryResponse`: List<ChatMessageDTO>

### 6.4. WebSocket Configuration

- [ ] **WebSocketConfig** (`@EnableWebSocketMessageBroker`)

  - Enable STOMP over WebSocket: `/ws`
  - Message broker: `/topic`, `/queue`
  - Application destination prefix: `/app`

- [ ] **WebSocket Interceptor** (JWT Authentication)
  - Extract JWT từ query param hoặc handshake headers
  - Validate token trước khi upgrade connection
  - Set user principal vào WebSocket session

### 6.5. Redis Pub/Sub Integration

- [ ] **RedisChatPublisher**

  - `publishMessage(Long streamId, ChatMessageDTO message)`:
    ```redis
    PUBLISH chat:room:{streamId} {messageJson}
    ```

- [ ] **RedisChatSubscriber** (`@Component`)
  - Subscribe to `chat:room:*`
  - Receive message → Broadcast to WebSocket clients trong room
  - Pattern:
    ```java
    @RedisListener("chat:room:*")
    public void onMessage(String channel, String message) {
        // Parse streamId từ channel
        // Broadcast qua SimpMessagingTemplate
        messagingTemplate.convertAndSend("/topic/chat." + streamId, message);
    }
    ```

### 6.6. RabbitMQ Chat Persistence

- [ ] **ChatLogQueue Configuration**

  - Queue: `chat.log.queue`
  - Durable: true
  - Batch processing: Consume 100 messages mỗi lần

- [ ] **ChatLogPublisher**

  - `publishForPersistence(ChatMessageDTO message)`: Send to RabbitMQ

- [ ] **ChatLogConsumer** (`@RabbitListener`)
  - Consume messages từ `chat.log.queue`
  - Batch insert vào DB (saveAll)
  - Ack message sau khi save thành công

### 6.7. Service Layer

- [ ] **ChatService**

  - `sendMessage(Long streamId, Long userId, String content)`:

    1. Validate: User không bị mute trong stream
    2. Create `ChatMessageDTO`
    3. Publish to Redis Pub/Sub (realtime broadcast)
    4. Publish to RabbitMQ (async persistence)

  - `getChatHistory(Long streamId, Pageable pageable)`: Load recent messages

    - Query từ DB (100 messages gần nhất)

  - `muteUser(Long streamId, Long userId, int durationSeconds)`:

    - Redis Set: `muted:{streamId}`
      ```redis
      SADD muted:{streamId} {userId}
      EXPIRE muted:{streamId} {durationSeconds}
      ```

  - `unmuteUser(Long streamId, Long userId)`:

    ```redis
    SREM muted:{streamId} {userId}
    ```

  - `isMuted(Long streamId, Long userId)`:
    ```redis
    SISMEMBER muted:{streamId} {userId}
    ```

### 6.8. Controller Layer

- [ ] **ChatController** (`/api/chat/**`)

  - `GET /api/chat/{streamId}/history`: Lịch sử chat

    - Public (cho guests xem)

  - `POST /api/chat/{streamId}/mute`: Mute user

    - `@PreAuthorize("hasRole('ADMIN') or @streamService.isStreamOwner(#streamId, authentication.principal.username)")`

  - `POST /api/chat/{streamId}/unmute`: Unmute user
    - Same authorization as mute

- [ ] **WebSocketChatController** (STOMP Endpoints)

  - `@MessageMapping("/chat.send")`: Client gửi message

    - Extract user từ Principal
    - Call `chatService.sendMessage()`

  - `@SubscribeMapping("/topic/chat.{streamId}")`: Client subscribe room
    - Validate: User đã authenticated
    - Increment viewer count (Redis HyperLogLog)

### 6.9. Testing

- [ ] Unit Tests
  - `ChatServiceTest`: Test mute logic
- [ ] Integration Tests
  - Test WebSocket connection với JWT
  - Test Redis Pub/Sub broadcast
  - Test RabbitMQ persistence flow

---

## Phase 7: Gift System & Async Processing

**Goal**: Tặng quà với atomic wallet deduction và async reward processing

### 7.1. Entity Layer

- [ ] **Gift Entity** (`Gift.java`)

  - Fields: `id`, `name`, `imageUrl`, `price` (BigDecimal), `isActive`, `createdAt`
  - Gift catalog (pre-defined items)

- [ ] **GiftTransaction Entity** (`GiftTransaction.java`) - Optional
  - Fields: `id`, `giftId`, `fromUserId`, `toUserId`, `streamId`, `quantity`, `totalAmount`, `createdAt`
  - Track gift history riêng

### 7.2. Repository Layer

- [ ] **GiftRepository**
  - `findByIsActiveTrue()`: Danh sách quà active
  - `findById(Long id)`: Get gift details

### 7.3. DTOs

- [ ] **Request DTOs**

  - `SendGiftRequest`: `streamId`, `giftId`, `quantity` (default 1)

- [ ] **Response DTOs**
  - `GiftDTO`: `id`, `name`, `imageUrl`, `price`
  - `SendGiftResponse`: `success`, `newBalance`, `message`

### 7.4. RabbitMQ Gift Processing

- [ ] **GiftEvent** (Message Model)

  - Fields: `giftId`, `fromUserId`, `toStreamerId`, `streamId`, `amount`, `timestamp`

- [ ] **GiftQueue Configuration**

  - Queue: `gift.transaction.queue`
  - Dead Letter Queue: `gift.transaction.dlq` (for failed processing)

- [ ] **GiftEventPublisher**

  - `publishGiftEvent(GiftEvent event)`: Send to RabbitMQ

- [ ] **GiftEventConsumer** (`@RabbitListener`)
  - Consume from `gift.transaction.queue`
  - Processing:
    1. Cộng tiền vào ví Streamer: `walletService.deposit(toStreamerId, amount)`
    2. Tạo Transaction record (type=GIFT)
    3. Broadcast "Donation Alert" to chat room (WebSocket)
    4. Update leaderboard (Redis Sorted Set)
  - Error handling: Retry 3 lần, sau đó send to DLQ

### 7.5. Service Layer

- [ ] **GiftService**

  - `getAllGifts()`: Danh sách gift catalog (cache trong Redis)

  - `sendGift(SendGiftRequest request, User currentUser)`:
    1. **Validate**:
       - Gift exists và active
       - User đang xem stream đó (optional)
       - Calculate total: `totalAmount = giftPrice * quantity`
    2. **Atomic Deduction** (Synchronous):
       - `walletService.deduct(currentUser.id, totalAmount, "Gift to streamer")`
       - Throw `InsufficientBalanceException` nếu fail
    3. **Async Processing** (RabbitMQ):
       - Create `GiftEvent`
       - `giftEventPublisher.publishGiftEvent(event)`
    4. Return: `SendGiftResponse` (success=true, newBalance)

### 7.6. Controller Layer

- [ ] **GiftController** (`/api/gifts/**`)

  - `GET /api/gifts`: Danh sách quà (Public)

  - `POST /api/gifts/send`: Tặng quà (Authenticated)
    - `@PreAuthorize("hasRole('USER')")`

### 7.7. WebSocket Notification

- [ ] **GiftAlertPublisher**
  - Được gọi từ `GiftEventConsumer`
  - Broadcast message to `/topic/chat.{streamId}`:
    ```json
    {
      "type": "GIFT_ALERT",
      "username": "user123",
      "giftName": "Diamond",
      "quantity": 5,
      "totalAmount": 500
    }
    ```

### 7.8. Testing

- [ ] Unit Tests
  - `GiftServiceTest`: Test insufficient balance
- [ ] Integration Tests
  - Test end-to-end: Send gift → Deduct → Publish → Consume → Update wallet
  - Test RabbitMQ retry logic

---

## Phase 8: Analytics & Leaderboard

**Goal**: Real-time analytics với Redis và dashboard APIs

### 8.1. Redis Data Structures

- [ ] **Viewer Tracking** (HyperLogLog)

  - Key: `stream:{streamId}:viewers`
  - Usage: `PFADD`, `PFCOUNT`

- [ ] **Leaderboard** (Sorted Sets)

  - Daily: `leaderboard:daily:{YYYY-MM-DD}`
    - Score = total gift amount sent
    - Member = userId
  - Weekly: `leaderboard:weekly:{YYYY-WW}`
  - All-time: `leaderboard:alltime`

- [ ] **System Stats** (Hashes)
  - Key: `stats:system`
  - Fields: `total_users`, `active_streams`, `total_revenue`

### 8.2. Service Layer

- [ ] **AnalyticsService**

  - `trackStreamView(Long streamId, Long userId)`:

    ```redis
    PFADD stream:{streamId}:viewers {userId}
    ```

  - `getStreamViewerCount(Long streamId)`:

    ```redis
    PFCOUNT stream:{streamId}:viewers
    ```

  - `updateLeaderboard(Long userId, BigDecimal amount)`:

    ```redis
    ZINCRBY leaderboard:daily:{date} {amount} {userId}
    ZINCRBY leaderboard:weekly:{week} {amount} {userId}
    ZINCRBY leaderboard:alltime {amount} {userId}
    ```

  - `getDailyLeaderboard(int limit)`: Top N gifters

    ```redis
    ZREVRANGE leaderboard:daily:{date} 0 {limit-1} WITHSCORES
    ```

  - `getWeeklyLeaderboard(int limit)`: Top N weekly

  - `getAllTimeLeaderboard(int limit)`: Top N all-time

  - `getSystemDashboard()`: ADMIN only
    - Total users (DB count)
    - Active streams (Redis count + DB verification)
    - Total revenue (Sum from Transaction table)
    - Cache result in Redis với TTL 5 phút

- [ ] **StreamAnalyticsService**

  - `getStreamReport(Long streamId, User currentUser)`:

    - Validate: Owner hoặc ADMIN
    - Return: Peak viewers, total views, total gifts, revenue breakdown

  - `calculateStreamRevenue(Long streamId)`:
    - Sum transactions where `toUserId = stream.creatorId` AND related to streamId

### 8.3. Controller Layer

- [ ] **AnalyticsController** (`/api/analytics/**`)

  - `GET /api/analytics/leaderboard`: Bảng xếp hạng (Public)

    - Query params: `?period=daily|weekly|alltime`, `?limit=10`

  - `GET /api/analytics/dashboard`: System dashboard (ADMIN)

    - `@PreAuthorize("hasRole('ADMIN')")`

  - `GET /api/analytics/streams/{streamId}/report`: Stream report (Owner + ADMIN)
    - `@PreAuthorize("hasRole('ADMIN') or @streamService.isStreamOwner(#streamId, authentication.principal.username)")`

### 8.4. Scheduled Tasks

- [ ] **LeaderboardCleanupScheduler** (`@Scheduled`)
  - Daily: Archive yesterday's leaderboard to DB
  - Weekly: Cleanup old weekly leaderboards (keep last 4 weeks)

### 8.5. Testing

- [ ] Unit Tests
  - Test Redis HyperLogLog accuracy (simulate 10k unique users)
  - Test Sorted Set ranking
- [ ] Integration Tests
  - Test leaderboard update after gift transaction

---

## Phase 9: Admin Management Module

**Goal**: Admin tools cho user management, moderation, và system monitoring

### 9.1. Service Layer

- [ ] **AdminUserService**

  - `getAllUsers(Pageable pageable)`: Danh sách tất cả users

    - Pagination + filters (role, status)

  - `banUser(Long userId, String reason)`:

    - Update User: `status = BANNED`
    - Add to Redis blacklist: `banned:users`
    - Revoke all active sessions (JWT blacklist)

  - `unbanUser(Long userId)`:

    - Update User: `status = ACTIVE`
    - Remove from Redis blacklist

  - `changeUserRole(Long userId, String newRole)`:

    - Validate: newRole in [USER, STREAMER, ADMIN]
    - Delete old UserRole entries
    - Create new UserRole entry

  - `deleteUser(Long userId)`:
    - Soft delete: `deletedAt = now`
    - Optionally: Cleanup related data (streams, transactions)

- [ ] **AdminStreamService**

  - `getAllStreams(Pageable pageable)`: Tất cả streams (include inactive)

  - `forceEndStream(Long streamId)`: Emergency stop

    - Call `streamService.endStream()`

  - `deleteStream(Long streamId)`: Hard delete

- [ ] **AdminTransactionService**

  - `getAllTransactions(Pageable pageable)`: Audit trail

    - Filters: type, dateRange, userId

  - `refundTransaction(Long transactionId)`:
    - Create reverse transaction (type=REFUND)
    - Restore sender's balance

### 9.2. Controller Layer

- [ ] **AdminUserController** (`/api/admin/users/**`)

  - `GET /api/admin/users`: Danh sách users

    - `@PreAuthorize("hasRole('ADMIN')")`

  - `POST /api/admin/users/{userId}/ban`: Ban user

    - `@PreAuthorize("hasRole('ADMIN')")`

  - `POST /api/admin/users/{userId}/unban`: Unban user

    - `@PreAuthorize("hasRole('ADMIN')")`

  - `PUT /api/admin/users/{userId}/roles`: Thay đổi roles

    - `@PreAuthorize("hasRole('ADMIN')")`
    - Body: `{ "role": "STREAMER" }`

  - `DELETE /api/admin/users/{userId}`: Xóa user
    - `@PreAuthorize("hasRole('ADMIN')")`

- [ ] **AdminStreamController** (`/api/admin/streams/**`)

  - `GET /api/admin/streams`: Tất cả streams

    - `@PreAuthorize("hasRole('ADMIN')")`

  - `DELETE /api/admin/streams/{streamId}`: Xóa stream
    - `@PreAuthorize("hasRole('ADMIN')")`

- [ ] **AdminTransactionController** (`/api/admin/transactions/**`)

  - `GET /api/admin/transactions`: Tất cả giao dịch

    - `@PreAuthorize("hasRole('ADMIN')")`

  - `POST /api/admin/transactions/{transactionId}/refund`: Refund
    - `@PreAuthorize("hasRole('ADMIN')")`

### 9.3. Testing

- [ ] Integration Tests
  - Test ban user → verify JWT invalidated
  - Test role change → verify permissions updated

---

## Phase 10: Production Readiness & Polish

**Goal**: Chuẩn bị production deployment và documentation

### 10.1. Security Hardening

- [ ] **Environment-Based Config**

  - Disable `/api/dev/**` và `/api/test/**` trong production
  - IP whitelist cho admin endpoints (optional)

- [ ] **Rate Limiting** (Redis-based)

  - `@RateLimiter` annotation cho sensitive endpoints
  - Limit: 10 requests/minute cho send gift

- [ ] **CORS Configuration**
  - Configure allowed origins từ environment variable

### 10.2. Performance Optimization

- [ ] **Database Indexes Review**

  - Verify all foreign keys có index
  - Add composite indexes cho slow queries

- [ ] **Redis Cache Strategy**

  - Cache hot data: Live streams, gift catalog
  - TTL configuration best practices

- [ ] **Database Connection Pooling**
  - HikariCP tuning (pool size, timeout)

### 10.3. Monitoring & Logging

- [ ] **Structured Logging**

  - Logback config cho production
  - Log levels: ERROR (production), DEBUG (development)

- [ ] **Health Checks**

  - `/actuator/health`: DB, Redis, RabbitMQ connectivity
  - Custom health indicators

- [ ] **Metrics** (Spring Boot Actuator)
  - Enable Prometheus metrics
  - Track: Request count, response time, error rate

### 10.4. Documentation

- [ ] **API Documentation**

  - Verify Swagger UI completeness (`/swagger-ui.html`)
  - Add example requests/responses

- [ ] **README Update**

  - Deployment instructions
  - Environment variables reference
  - Architecture diagram

- [ ] **Postman Collection**
  - Export all endpoints
  - Include authentication examples

### 10.5. Testing & Quality

- [ ] **Load Testing**

  - Script giả lập 1000 concurrent chat messages
  - Verify Redis Pub/Sub performance

- [ ] **Integration Test Suite**
  - Verify all critical flows end-to-end
  - Coverage > 70%

### 10.6. Deployment Preparation

- [ ] **Docker Production Config**

  - Multi-stage Dockerfile (build + runtime)
  - Docker Compose production profile

- [ ] **Database Migration Strategy**
  - Flyway/Liquibase setup (optional)
  - Backup/restore procedures

### 10.7. Final Checklist

- [ ] All endpoints có Swagger documentation
- [ ] All exceptions được handle bởi `GlobalExceptionHandler`
- [ ] All sensitive data (passwords, tokens) được hash/encrypt
- [ ] Redis keys có TTL (tránh memory leak)
- [ ] RabbitMQ queues có Dead Letter Queue
- [ ] Logging không chứa sensitive data
- [ ] Production config tách biệt khỏi development

---

## Phase 11: Social Features (Optional)

**Goal**: Xây dựng mạng xã hội giữa người dùng để tăng tương tác

### 11.1. Entity Layer

- [ ] **UserFollow Entity** (`UserFollow.java`)

  - Fields: `id`, `followerId`, `followingId`, `createdAt`
  - Composite index: `(followerId, followingId)` (unique)
  - Index: `followerId`, `followingId`

- [ ] **UserProfile Entity Update**
  - Add fields: `followerCount`, `followingCount`

### 11.2. Service Layer

- [ ] **SocialService**

  - `followUser(Long followerId, Long followingId)`:

    - Validate: Không follow chính mình
    - Create `UserFollow` record
    - Increment stats (followerCount, followingCount)
    - Trigger `NewFollowerEvent` (RabbitMQ)

  - `unfollowUser(Long followerId, Long followingId)`:

    - Delete `UserFollow` record
    - Decrement stats

  - `getFollowers(Long userId, Pageable pageable)`:

    - Query `UserFollow` join `User`

  - `getFollowing(Long userId, Pageable pageable)`:

    - Query `UserFollow` join `User`

  - `isFollowing(Long followerId, Long followingId)`:
    - Check existence in DB or Redis Cache

### 11.3. Controller Layer

- [ ] **SocialController** (`/api/users/{userId}/social`)
  - `POST /follow`: Follow user
  - `DELETE /unfollow`: Unfollow user
  - `GET /followers`: Get followers list
  - `GET /following`: Get following list

### 11.4. Redis Cache

- [ ] **Follow Cache**
  - `user:{userId}:followers` (Set): Cache list follower IDs
  - Use for fast lookup `isFollowing`

---

## Phase 12: Notification System (Optional)

**Goal**: Thông báo real-time cho người dùng về các sự kiện quan trọng

### 12.1. Entity Layer

- [ ] **Notification Entity** (`Notification.java`)
  - Fields: `id`, `userId`, `type` (STREAM_STARTED, NEW_FOLLOWER, GIFT_RECEIVED), `relatedEntityId`, `message`, `isRead`, `createdAt`
  - Index: `userId`, `isRead`, `createdAt`

### 12.2. Service Layer

- [ ] **NotificationService**

  - `createNotification(NotificationRequest)`:

    - Save to DB
    - Push to WebSocket via `SimpMessagingTemplate`
    - Increment unread count in Redis

  - `markAsRead(Long notificationId, Long userId)`:

    - Update `isRead = true`
    - Decrement unread count

  - `getNotifications(Long userId, Pageable pageable)`:
    - Get from DB

### 12.3. Event Consumers (RabbitMQ)

- [ ] **NotificationConsumer**
  - Listen `notifications.stream.started`: Notify all followers
  - Listen `notifications.user.followed`: Notify user being followed
  - Listen `notifications.gift.received`: Notify streamer

### 12.4. WebSocket & Controller

- [ ] **WebSocket Topic**

  - `/topic/notifications.{userId}`: Private channel for notifications

- [ ] **NotificationController** (`/api/notifications`)
  - `GET /`: Get history
  - `PUT /{id}/read`: Mark as read

---

## Verification Plan

### Automated Tests

**Run all tests**:

```bash
mvn test
```

**Integration Tests** (với Docker containers):

```bash
docker-compose -f docker-compose.test.yml up -d
mvn verify
docker-compose -f docker-compose.test.yml down
```

### Manual Verification Scenarios

#### Scenario 1: End-to-End User Flow

1. Register new user → Login → Get access token
2. Create stream (upgrade to STREAMER first)
3. Start stream → Verify `isLive=true`
4. Simulate deposit → Send gift to streamer
5. Check streamer wallet balance updated
6. View chat history
7. End stream → Verify analytics saved

#### Scenario 2: Concurrency Test

1. Create 2 users with balance 100 each
2. Simultaneously send gifts (same timestamp)
3. Verify: No race condition, all transactions atomic

#### Scenario 3: Authorization Test

1. Login as USER → Try to access `/api/admin/users` → Expect 403 Forbidden
2. Login as ADMIN → Try to access `/api/admin/users` → Expect 200 OK
3. Login as STREAMER → Try to update another user's stream → Expect 403 Forbidden

### Performance Benchmarks

- **Chat Message Throughput**: > 1000 messages/second
- **Redis HyperLogLog Accuracy**: < 2% error for 10k unique users
- **Gift Transaction Latency**: < 500ms (deduct + publish)

### Tools

- **API Testing**: Postman / Thunder Client
- **Load Testing**: Apache JMeter / k6
- **Redis Monitoring**: `redis-cli MONITOR`
- **RabbitMQ Monitoring**: Management UI (`http://localhost:15672`)

---

## Dependencies Summary

### Required Spring Boot Starters

- `spring-boot-starter-web`
- `spring-boot-starter-security`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-validation`
- `spring-boot-starter-data-redis`
- `spring-boot-starter-amqp` (RabbitMQ)
- `spring-boot-starter-websocket`
- `spring-boot-starter-actuator`

### Additional Libraries

- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (JWT)
- `lombok`
- `postgresql` (JDBC Driver)
- `springdoc-openapi-starter-webmvc-ui` (Swagger)
- `p6spy` (SQL Logging, development only)

### Infrastructure (Docker)

- PostgreSQL 16
- Redis 7
- RabbitMQ 3 (with management plugin)

---

## Conclusion

Implementation Plan này cover **toàn bộ** các module được định nghĩa trong:

- ✅ `docs/system_design_livestream.md`
- ✅ `docs/api_endpoints_specification.md`

**Ưu tiên thực hiện**: Phase 4 → 5 → 6 → 7 → 8 → 9 → 10

Mỗi phase có thể test độc lập nhờ simulation APIs (Phase 2).

**Next Steps**: Implement Phase 4 (Stream Management Module) để có core feature cho livestream platform.

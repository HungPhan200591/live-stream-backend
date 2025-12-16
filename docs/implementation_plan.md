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

- [ ] **Entity Design**: `User`, `Role`.
- [ ] **Security Config**:
  - Stateless Session.
  - Config `SecurityFilterChain`.
- [ ] **JWT Implementation**:
  - `JwtTokenProvider`: Generate/Validate token.
  - `JwtAuthenticationFilter`: Verify request header.
- [ ] **Auth API**: Login, Register.

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

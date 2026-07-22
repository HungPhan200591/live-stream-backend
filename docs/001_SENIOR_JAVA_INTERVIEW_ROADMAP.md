# Roadmap nâng cấp Livestream Backend theo hướng Senior Java

> Mục tiêu: biến project này từ một backend demo nhiều công nghệ thành một bài tập có thể dùng để ôn và trình bày trong phỏng vấn Senior Java.
>
> Cập nhật: 2026-07-22. Đánh giá này dựa trên static review code và tài liệu hiện có; chưa chạy Maven, Docker hoặc load test.

---

## 1. Kết luận nhanh

**Nên giữ project và phát triển tiếp.** Domain livestream phù hợp để học các chủ đề Senior: security, transaction, concurrency, Redis, messaging, WebSocket, database performance và vận hành hệ thống.

Roadmap cũ tốt theo hướng hoàn thành tính năng. Tuy nhiên, nếu chỉ đi lần lượt Phase 5 đến Phase 12 thì kết quả dễ là một project CRUD sử dụng nhiều công nghệ, nhưng khó chứng minh được các năng lực Senior như:

- xử lý failure mode và consistency;
- đo đạc performance thay vì đoán;
- thiết kế idempotency và delivery semantics;
- test được race condition;
- giải thích trade-off kiến trúc.

Nguyên tắc phát triển mới:

1. Không rewrite toàn bộ, không tách microservice sớm.
2. Làm **modular monolith** theo package-by-feature.
3. Mỗi feature phải có test, failure scenario, metric và tài liệu trade-off.
4. Production concerns là cross-cutting concern, không để dồn hết vào phase cuối.

---

## 2. Điểm mạnh của base hiện tại

- Business flow và system design khá rõ; có use case, API spec và sequence diagram.
- Simulation-first giúp phát triển backend không phụ thuộc payment gateway hoặc media server thật.
- Có DTO-first API, `open-in-view=false`, Swagger và HTTP request files.
- Session-backed refresh token là bài toán auth thực tế hơn JWT CRUD stateless đơn giản.
- Stack PostgreSQL + Redis + RabbitMQ + WebSocket tạo đủ "đất" cho các câu hỏi system design/backend senior.

Lưu ý: quy tắc "không dùng JPA relationship annotations" không nên coi là chân lý. Nó tránh một số accidental N+1 nhưng không tự hết N+1. Senior cần biết chọn projection, explicit join, batch fetch, entity graph hoặc relationship tùy use case.

Ví dụ hiện tại, `StreamService.convertToDTO()` lấy creator bằng một query cho từng stream. Khi list N streams, đây là manual N+1 dù không dùng `@ManyToOne`.

---

## 3. Phase 3.5 — Stabilization (làm trước Economy)

Đây là phase nên thực hiện trước Phase 5. Mục tiêu không phải thêm feature, mà tạo baseline đáng tin cậy để các phase sau có giá trị học tập.

### 3.1. JWT token-type confusion — P0 security

**Hiện trạng**

`JwtAuthenticationFilter` chỉ gọi `jwtTokenProvider.validateToken(token)` với mọi Bearer token. Access token và refresh token đang được ký bằng cùng key và không có claim phân biệt token type.

**Rủi ro**

Một refresh token hợp lệ có thể bị gửi trong header `Authorization: Bearer ...` và được filter chấp nhận như access token.

**Hướng xử lý**

1. Thêm claim `token_type=access` hoặc `token_type=refresh`.
2. Tạo hàm validate riêng cho access và refresh token.
3. Filter chỉ chấp nhận access token.
4. Refresh endpoint chỉ chấp nhận refresh token.
5. Bổ sung `issuer`, `audience`, `issuedAt`, `jti` khi phù hợp; document rõ token lifecycle.
6. Viết tests cho token đúng type, sai type, expired, revoked session và forged token.

**Câu hỏi phỏng vấn phải trả lời được**

- Access token và refresh token khác nhau ở trách nhiệm nào?
- Vì sao chỉ kiểm tra signature + expiry là chưa đủ?
- Khi nào cần key rotation, `kid`, issuer và audience?
- Tại sao logout access token không thể có hiệu lực tức thì nếu access token stateless?

### 3.2. Authorization matcher quá rộng — P0 security

`SecurityConfig` đang `permitAll()` cho `/api/auth/**`, nhưng nhóm này có cả `/api/auth/me` và `/api/auth/logout-all`, vốn cần authenticated user.

**Hướng xử lý**: chỉ permit các endpoint public cụ thể: register, login, refresh. Những endpoint còn lại phải đi qua `.authenticated()` và method security.

### 3.3. Logout-all và session cache — P0 correctness

`revokeAllUserSessions()` update trạng thái trong DB nhưng không invalid cache của từng session. Cache-hit path lại đọc record DB nhưng không kiểm tra lại `status` sau khi đọc. Điều này có thể khiến session đã REVOKED vẫn refresh access token khi cache còn TTL.

**Hướng xử lý**

- DB là source of truth.
- Khi logout-all: lấy danh sách active session IDs, revoke DB, rồi invalidate cache từng ID trong cùng use case.
- Cache-hit path phải có rule nhất quán: hoặc trust cache và đảm bảo invalidation đúng, hoặc revalidate DB khi security-sensitive.
- Viết integration test tái hiện chính xác: login hai thiết bị → cache session → logout-all → thử refresh từng token.

### 3.4. Stream key bị lộ qua public response — P0 security

`StreamDTO` có `streamKey`, trong khi `GET /api/streams` và `GET /api/streams/{id}` là public. Stream key là credential để publish stream qua OBS/RTMP, không nên xuất hiện ở response public.

**Hướng xử lý**: tách DTO theo audience.

- `PublicStreamResponse`: không có `streamKey`.
- `OwnerStreamResponse`: có stream key, chỉ owner/admin.
- `CreateStreamResponse`: trả stream key khi streamer vừa tạo stream.

### 3.5. Baseline chất lượng và cấu hình

- Tách `application-dev.yml`, `application-test.yml`, `application-prod.yml`.
- Không commit secret/credential production; dùng environment variables.
- Thay `ddl-auto=update` bằng Flyway migration.
- Thêm test infrastructure bằng Testcontainers cho PostgreSQL, Redis và RabbitMQ.
- Thêm CI tối thiểu: compile, test, formatter/linter, dependency scan.
- Thống nhất docs với code: roadmap hiện ghi Phase 4 TODO nhưng phase file ghi DONE.

---

## 4. Roadmap mới

### Chặng 0 — Stabilization & test harness

**Deliverables**

- Fix bốn lỗi P0 ở phần trên.
- Flyway baseline migration.
- Testcontainers setup.
- Test naming convention và test data builder.
- Architecture Decision Records (ADR) folder.
- CI pipeline tối thiểu.

**Kiến thức Senior**: Spring Security filter chain, JWT, cache invalidation, test pyramid, database migration, CI.

### Chặng 1 — Modular monolith và Stream lifecycle

Refactor package từ technical-layer thuần túy sang feature/module:

```text
com.stream.demo
├── identity
├── stream
├── wallet
├── gift
├── chat
├── analytics
├── admin
└── shared
```

Không cần làm sạch tuyệt đối ngay. Refactor dần khi chạm feature.

**Stream lifecycle cần có**

- State machine rõ ràng: `CREATED -> LIVE -> ENDED`.
- Idempotent RTMP webhooks: webhook `stream-started` gọi lại không làm reset `startedAt` hoặc phát event lần hai.
- Ownership authorization.
- Pagination/cursor cho list streams.
- Projection hoặc batch query để xử lý N+1.
- Cache update chỉ sau DB commit; không coi DB + Redis là một transaction atomic.

**Bài tập Senior**

- Viết concurrent test: hai webhook start cùng lúc.
- Lập bảng transition hợp lệ/không hợp lệ.
- So sánh cache-aside, write-through và event-driven invalidation.

### Chặng 2 — Wallet và ledger

Không bắt đầu bằng `balance` đơn giản. Thiết kế ledger/audit trail trước.

**Cần học và làm**

- `BigDecimal`, currency/scale, validation amount > 0.
- Unique wallet per user bằng DB constraint.
- Optimistic locking (`@Version`) và retry có giới hạn.
- So sánh optimistic lock, pessimistic lock và conditional SQL update.
- Idempotency key cho deposit/deduct.
- Immutable transaction/ledger records; balance là derived state hoặc cached projection.
- Transaction history có cursor pagination.

**Bài tập Senior**

- Chạy 100 request concurrent cùng deduct một wallet.
- Chứng minh balance không âm.
- Tái hiện optimistic-lock conflict và deadlock.
- Dùng `EXPLAIN ANALYZE` cho history query.

### Chặng 3 — Gift flow và reliable messaging

Không diễn giải "DB transaction + Rabbit publish" là atomic. Đây là dual-write problem.

**Thiết kế cần có**

```text
Gift request
  -> DB transaction: deduct wallet + create transaction + insert outbox event
  -> outbox publisher: publish RabbitMQ + publisher confirm
  -> consumer: deduplicate/inbox + credit receiver + mark processed
  -> ACK only after durable processing succeeds
```

**Cần có**

- Transactional Outbox.
- Publisher confirms.
- Consumer manual ACK/NACK.
- Retry có exponential backoff, DLQ và replay procedure.
- Consumer idempotency/inbox table.
- Correlation ID xuyên suốt API → outbox → queue → consumer.

**Câu hỏi phỏng vấn**

- At-most-once, at-least-once và exactly-once khác nhau thế nào?
- Vì sao "exactly once" thường là business-level idempotency, không phải broker magic?
- DB commit xong nhưng publish lỗi thì recovery ra sao?
- Consumer xử lý xong rồi crash trước ACK thì sao?

### Chặng 4 — Realtime chat

**Phân tách semantics**

- Redis Pub/Sub: broadcast realtime, chấp nhận best-effort.
- RabbitMQ/DB: persistence/audit path.
- REST history: đọc từ DB.

Redis Pub/Sub có at-most-once delivery; subscriber offline có thể bỏ lỡ message. Vì vậy không được dùng nó như source of truth cho chat history.

**Thiết kế cần học**

- WebSocket/STOMP authentication và authorization khi CONNECT/SUBSCRIBE/SEND.
- Rate limiting theo user/room/IP.
- Message validation, max length và moderation.
- Idempotency/message ID khi client reconnect/retry.
- Backpressure và slow consumer strategy.
- Mute bằng key riêng có TTL (`mute:{streamId}:{userId}`) hoặc ZSET expiry; không dùng một Set TTL cho nhiều mute duration khác nhau.

### Chặng 5 — Viewer presence và analytics

Tách hai khái niệm:

- **Unique viewers**: HyperLogLog, approximate, không decrement.
- **Current viewers**: ZSET heartbeat với score là `lastSeen`, cleanup viewers timeout.

Từ current viewers, tính peak concurrent viewers. Từ HLL, tính unique reach.

**Cần học**: Redis data structures, TTL, approximate algorithms, scheduled jobs, time-window aggregation, consistency trade-offs.

### Chặng 6 — Admin, production và operations

Không chờ tới đây mới thêm observability. Nhưng đây là lúc hoàn thiện:

- Audit log immutable cho admin actions.
- Ban user → revoke sessions + invalidate cache + disconnect WebSocket nếu cần.
- Spring Boot Actuator, Micrometer, Prometheus metrics.
- OpenTelemetry traces: HTTP request, DB, Redis, RabbitMQ.
- Structured logs, correlation ID, không log token/secret.
- Dockerfile multi-stage, health checks, graceful shutdown.
- k6/Gatling load test và benchmark report.
- Incident runbook: Redis down, RabbitMQ down, DB slow, consumer backlog.

---

## 5. Definition of Done cho mọi feature

Không đánh dấu DONE chỉ vì API chạy happy path. Mỗi feature cần:

1. Use case và non-functional requirement ngắn gọn.
2. Sequence diagram có cả failure path.
3. ADR: quyết định, trade-off, phương án bị loại.
4. Unit test cho domain/business logic.
5. Integration test với infrastructure thật qua Testcontainers.
6. Concurrency/idempotency test nếu động đến state hoặc money.
7. API contract + HTTP request file + Swagger.
8. Metrics/logs/traces cần theo dõi.
9. Benchmark hoặc load-test result khi có hot path.
10. 5 câu hỏi phỏng vấn và câu trả lời do chính mình viết.

---

## 6. Bản đồ ôn phỏng vấn

| Chủ đề | Áp dụng trong project | Artifact phải có |
| --- | --- | --- |
| Java concurrency/JMM | Webhook, wallet, consumer | Race-condition test, giải thích visibility/atomicity |
| Spring `@Transactional` | Wallet, outbox, session | Test propagation/isolation/rollback |
| PostgreSQL MVCC/index | Wallet, history, analytics | Migration + `EXPLAIN ANALYZE` report |
| Redis | Cache, rate-limit, HLL, ZSET | Key schema, TTL, failure behavior |
| RabbitMQ | Gift/chat persistence | Confirm, ACK, retry, DLQ, idempotency |
| Security | JWT, RBAC, webhook | Threat model + negative tests |
| System design | Livestream scale | Capacity estimate, bottleneck, trade-off |
| JVM/performance | Load test | JFR/thread dump/GC analysis |
| Operations | All modules | Dashboard, alert, runbook |

---

## 7. Java và Spring version strategy

1. Giữ Java 17/Spring Boot 3.4 trong lúc tạo test harness và sửa baseline để không trộn feature work với migration risk.
2. Khi test ổn định, nâng runtime baseline lên **Java 21**.
3. Tạo nhánh thử nghiệm nâng Spring Boot 4.x và Java 25; không chặn feature work chính bởi nhánh này.
4. Benchmark virtual threads cho blocking I/O workload. Virtual threads giúp tăng throughput/scale khi có nhiều I/O-bound tasks; không mặc định làm latency nhanh hơn.

Java 17, 21 và 25 đều là LTS releases. Xem [Oracle Java SE Support Roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html).

---

## 8. Tài liệu chính thức nên đọc đúng lúc

- [Spring Security OAuth2 Resource Server JWT](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html) — JWT validation, issuer, audience.
- [Spring Boot Testcontainers](https://docs.spring.io/spring-boot/reference/testing/testcontainers.html) — integration test infrastructure.
- [PostgreSQL Transaction Isolation](https://www.postgresql.org/docs/current/transaction-iso.html) — MVCC, isolation anomalies.
- [RabbitMQ Consumer Acknowledgements and Publisher Confirms](https://www.rabbitmq.com/docs/confirms) — reliable messaging.
- [Redis Pub/Sub](https://redis.io/docs/latest/develop/pubsub/) — at-most-once semantics.
- [Spring Boot Observability](https://docs.spring.io/spring-boot/reference/actuator/observability.html) — metrics, traces, logs.
- [Spring Modulith Fundamentals](https://docs.spring.io/spring-modulith/reference/fundamentals.html) — modular monolith boundaries.
- [Oracle Virtual Threads](https://docs.oracle.com/en/java/javase/25/core/virtual-threads.html) — adoption và caveats.

---

## 9. Thứ tự bắt đầu đề xuất

1. Hoàn thiện Phase 3.5: JWT type, auth rules, cache revoke, DTO leak.
2. Setup Flyway + Testcontainers + CI.
3. Rework Stream lifecycle thành idempotent state machine.
4. Implement Wallet/Ledger với concurrency tests.
5. Implement Gift bằng outbox/inbox và RabbitMQ reliability.
6. Làm Chat, rồi Analytics, Admin và production hardening.

Khi bắt đầu một chặng, đọc phần tương ứng trong file này trước, sau đó viết ADR và checklist riêng cho chặng đó. Mục tiêu cuối cùng là có thể **giải thích, chứng minh bằng test/metric, và bảo vệ trade-off** của từng quyết định — đó mới là phần tạo khác biệt ở phỏng vấn Senior.

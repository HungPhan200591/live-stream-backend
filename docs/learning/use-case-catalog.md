# Livestream Domain Use-case Catalog

> Trạng thái: `CANONICAL DOMAIN SCENARIO CATALOG`<br>
> Phạm vi: bài toán Livestream dùng để học, implement, chạy lab và luyện System Design<br>
> Cập nhật: `2026-07-26`

File này trả lời câu hỏi: **“Tôi sẽ giải bài toán Livestream cụ thể nào?”**

Snapshot hiện tại có `41` use case: `9` bài toán lớn/capstone và `32` case Foundation, Senior, Architect hoặc Expert hỗ trợ triển khai/lab.

- [Senior Roadmap](../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md) sở hữu Stage, priority và thứ tự learning item.
- Catalog này sở hữu tên và mô tả bài toán nghiệp vụ cụ thể.
- [Learning System](index.md) sở hữu case đang `ACTIVE` và checkpoint hiện tại.
- `learning/cases/` chỉ chứa case file đã được kích hoạt; không tạo trước một file cho mọi dòng trong catalog.

## 1. Quy tắc đặt tên và chọn case

Tên case phải mô tả được **actor + tình huống/tải/failure + kết quả cần bảo vệ**.

Tên đạt yêu cầu:

- `Xử lý 100.000 người cùng xem một livestream mà hệ thống vẫn giữ p99 và không sập khi reconnect`.
- `Xử lý 10.000 yêu cầu tặng quà trong một phút mà ví không bị trừ tiền hai lần`.
- `Khóa một user đang mở nhiều phiên và WebSocket để lệnh ban có hiệu lực toàn hệ thống`.

Tên không đạt yêu cầu:

- `Học distributed systems`.
- `Implement Redis/Kafka`.
- `Tìm hiểu backpressure, CAP và microservices`.

Các thuật ngữ kỹ thuật chỉ giải thích **solution/mechanism**. Chúng không được thay thế problem statement. Khi materialize một learning case, tiêu đề phải giữ ngôn ngữ tình huống từ catalog và scope xuống vertical slice nhỏ nhất có thể kiểm chứng.

> Simulation-first: các case scale không yêu cầu tích hợp media server/CDN/payment provider thật. Có thể mô phỏng control plane, viewer session, chat fan-out, wallet/gift và dependency failure bằng workload có assumption rõ.

### Priority của use case

Priority dưới đây đo **giá trị luyện tập tổng hợp** từ tần suất gặp trong hệ thống thực tế, xác suất xuất hiện trong phỏng vấn Senior và mức thiệt hại nếu xử lý sai:

- `UC-P0 — MUST PRACTICE`: rất thường gặp hoặc là câu phỏng vấn kinh điển; phải làm trong first pass khi roadmap đi tới owner item tương ứng.
- `UC-P1 — IMPORTANT`: thường gặp ở production hoặc vòng Senior/Lead; làm sau P0 trong cùng Stage/prerequisite.
- `UC-P2 — ADVANCED`: bài toán Architect/scale/role-specific; chỉ mở khi foundation liên quan đã có evidence.
- `UC-P3 — OPTIONAL`: niche hoặc phụ thuộc target role đặc biệt; chỉ thêm khi có constraint thật.

`UC-P0..P3` không thay thế priority `P0..P3` hay Stage order của roadmap. Nó chỉ giúp chọn scenario khi một learning item có nhiều use case. Các nhãn `Real-world` và `Interview` là heuristic `HIGH | MEDIUM | LOW` để xếp thứ tự học, không phải thống kê thị trường.

### Difficulty của use case

- `FOUNDATION`: một boundary chính, failure dễ tái hiện, phù hợp để học API/framework/tooling.
- `SENIOR`: có invariant, concurrency/transaction/failure recovery hoặc operability phải bảo vệ.
- `ARCHITECT`: nhiều component/failure domain, cần capacity, HA, security, cost và evolution trade-off.
- `EXPERT`: pathological scale, multi-region/coordination hoặc incident có nhiều constraint xung đột.

Difficulty không phải knowledge depth `D1-D4`. Cùng một case `SENIOR` vẫn có thể được học ở D1 rồi tiến hóa tới D3/D4 bằng implementation, experiment và teach-back.

## 2. Chín bài toán lớn, kinh điển

Đây là spine dùng để luyện Senior/Solution Architect. Không chạy theo thứ tự bảng; roadmap và prerequisite quyết định thời điểm kích hoạt.

### LIVE-UC-01

**Bài toán:** Xử lý 100.000 người cùng xem một livestream tại một thời điểm, trong đó một phòng có thể chiếm phần lớn traffic.

- Phải chứng minh: capacity assumption, connection/concurrency budget, viewer-state strategy, p95/p99, bottleneck đầu tiên, headroom khi mất một node/zone và degraded mode.
- Invariant: control plane vẫn xác định đúng stream đang `LIVE`; overload viewer/read path không được làm hỏng durable stream state.
- Output chính: capacity sheet, workload/fault lab, architecture dossier và bản trình bày 2/15/45 phút.
- Owner learning items: `ARCH-01`, `RT-01`, `RED-01`, `OBS-01`.
- Phân loại: priority `UC-P0`; Real-world `HIGH`; Interview `HIGH`; Difficulty `ARCHITECT`.
- Mode: `CAPSTONE + LAB`; không mặc định cần media server thật.

### CHAT-UC-01

**Bài toán:** Vận hành phòng chat có 50.000 người kết nối, hàng nghìn message mỗi giây và một nhóm client đọc rất chậm.

- Phải chứng minh: auth ở CONNECT/SUBSCRIBE/SEND, per-room ordering scope, bounded outbound queue, slow-consumer policy, rate limit và message-size limit.
- Invariant: client chậm không được làm đầy memory hoặc chặn toàn bộ room; user bị mute/ban không phát được message.
- Output chính: WebSocket negative tests, load test, queue-age/bytes metric và recovery runbook.
- Owner learning items: `RT-01`, `RES-03`, `RED-01`, `OBS-01`.
- Phân loại: priority `UC-P0`; Real-world `HIGH`; Interview `HIGH`; Difficulty `SENIOR`.
- Mode: `CASE + LAB`.

### RECONNECT-UC-01

**Bài toán:** Sau khi gateway restart hoặc mạng chập chờn, 30.000 client reconnect trong 60 giây mà auth, Redis và database không bị đánh sập.

- Phải chứng minh: exponential backoff + jitter phía client, admission control, session resume, token refresh budget và staggered recovery.
- Invariant: reconnect không tạo duplicate durable action/subscription trái quyền; backlog phục hồi có giới hạn.
- Output chính: reconnect-storm workload, saturation timeline và before/after recovery evidence.
- Owner learning items: `RT-01`, `RES-01`, `RES-02`, `RES-03`, `ARCH-01`.
- Phân loại: priority `UC-P1`; Real-world `HIGH`; Interview `HIGH`; Difficulty `SENIOR`.
- Mode: `LAB + CAPSTONE`.

### GIFT-UC-01

**Bài toán:** Trong một sự kiện livestream, xử lý 10.000 yêu cầu tặng quà trong một phút mà ví không âm, không double-spend và client retry không tạo giao dịch thứ hai.

- Phải chứng minh: money representation, idempotency key, atomic ledger update, contention strategy và response khi outcome bị timeout/không rõ.
- Invariant: tổng debit/credit được bảo toàn; một business command chỉ được ghi nhận một lần.
- Output chính: concurrency reproducer, ledger constraints, idempotency tests và conflict/throughput measurement.
- Owner learning items: `JAVA-01`, `CON-01`, `WAL-01`, `TX-01`, `API-01`.
- Phân loại: priority `UC-P0`; Real-world `HIGH`; Interview `HIGH`; Difficulty `SENIOR`.
- Mode: `CASE + IMPLEMENTATION + LAB`.

### EVT-UC-01

**Bài toán:** Sau khi trừ tiền mua quà, hệ thống phải tạo gift, publish event và cập nhật projection dù process chết ở bất kỳ điểm nào.

- Phải chứng minh: business write + outbox atomic, relay at-least-once, inbox/dedup, ACK/offset sau durable processing và reconciliation.
- Invariant: không mất durable intent, không gửi gift hai lần và không double-settle wallet.
- Output chính: kill-at-crash-point experiment, invariant query, trace request-to-consumer và DLQ/replay runbook.
- Owner learning items: `EVT-01`, `MQ-01`, `KFK-01`, `TX-01`, `OBS-01`.
- Phân loại: priority `UC-P1`; Real-world `HIGH`; Interview `HIGH`; Difficulty `SENIOR`.
- Mode: `CASE + IMPLEMENTATION + FAULT LAB`.

### HOT-UC-01

**Bài toán:** Một streamer nổi tiếng tạo hot room/hot key/hot partition lớn gấp hàng trăm lần traffic trung bình.

- Phải chứng minh: phát hiện skew theo room/key/partition, fan-out strategy, hot-key mitigation, fairness và giới hạn của việc chỉ scale thêm node.
- Invariant: một room nóng không được hút hết connection, broker, Redis hoặc database capacity của các room khác.
- Output chính: skewed workload, partition/key metrics, alternative comparison và load-shedding decision.
- Owner learning items: `RED-01`, `KFK-01`, `RT-01`, `ARCH-01`.
- Phân loại: priority `UC-P1`; Real-world `MEDIUM`; Interview `HIGH`; Difficulty `ARCHITECT`.
- Mode: `LAB + CAPSTONE`.

### BAN-UC-01

**Bài toán:** Ban một user đang có nhiều access token, session Redis và WebSocket connection để lệnh ban có hiệu lực trên REST, chat và event path.

- Phải chứng minh: ownership/role checks, revoke-all, cache invalidation, active connection handling, audit và bounded stale window.
- Invariant: user bị ban không tiếp tục thao tác qua token/connection cũ; không log token hoặc dữ liệu nhạy cảm.
- Output chính: threat timeline, REST/WebSocket negative tests, revoke fault test và audit evidence.
- Owner learning items: `SEC-02`, `SEC-04`, `RT-01`, `RED-01`.
- Phân loại: priority `UC-P0`; Real-world `HIGH`; Interview `HIGH`; Difficulty `SENIOR`.
- Mode: `CASE + SECURITY LAB`.

### ANALYTICS-UC-01

**Bài toán:** Xây near-real-time analytics cho lượt xem/gift/chat, có thể replay và backfill mà không làm chậm transactional path.

- Phải chứng minh: event contract/version, partition key, consumer lag, idempotent projection, late event, replay vào shadow target và reconciliation.
- Invariant: analytics có thể chậm nhưng không được làm sai hoặc chặn wallet/stream transaction; replay không phát external side effect lần hai.
- Output chính: event pipeline lab, lag/replay evidence, storage decision và extraction scorecard.
- Owner learning items: `KFK-01`, `EVT-01`, `MS-01`, `DATA-01`.
- Phân loại: priority `UC-P1`; Real-world `HIGH`; Interview `HIGH`; Difficulty `ARCHITECT`.
- Mode: `LAB + CAPSTONE`; tách service chỉ khi scorecard chứng minh có lợi.

### REGION-UC-01

**Bài toán:** Phục vụ người xem ở nhiều region nhưng vẫn giữ single-writer hoặc regional ownership cho stream/wallet và có kế hoạch failover rõ.

- Phải chứng minh: traffic routing, read freshness, write ownership, RPO/RTO, fencing, failover/failback và chi phí egress/operation.
- Invariant: không xuất hiện hai writer cùng sở hữu một wallet/stream invariant; failover không âm thầm mất hoặc nhân đôi durable command.
- Output chính: multi-region architecture dossier, failure matrix, capacity/cost model và DR runbook.
- Owner learning items: `DB-02`, `DR-01`, `DS-01`, `ARCH-01`, `CLOUD-01`.
- Phân loại: priority `UC-P2`; Real-world `MEDIUM`; Interview `MEDIUM`; Difficulty `EXPERT`.
- Mode: `CAPSTONE`; không mặc định implement multi-region thật.

## 3. Use case supporting từ foundation tới expert

`RW/PV` lần lượt là mức thường gặp trong thực tế và khả năng dùng làm câu hỏi phỏng vấn. Bảng này là scenario pool, không phải execution queue thứ hai.

### 3.1. Foundation và Senior cases

| ID | Bài toán concrete | Phải xây/chứng minh | Owner item | Priority | RW/PV | Difficulty / mode |
| --- | --- | --- | --- | --- | --- | --- |
| `CREATE-UC-01` | Streamer tạo/schedule livestream với thời gian hoặc trạng thái không hợp lệ; viewer không được tạo stream thay owner | Request validation, DTO boundary, ownership, business invariant và HTTP error contract | `JAVA-01`, `SPR-01`, `API-01`, `SEC-06` | `UC-P0` | H/H | `FOUNDATION / CASE` |
| `FOLLOW-UC-01` | Viewer bấm follow/unfollow nhiều lần hoặc client retry nhưng không tạo duplicate relation/count | Idempotent API, unique constraint, transaction boundary và repeatable tests | `API-01`, `TX-01`, `SQL-01` | `UC-P0` | H/M | `FOUNDATION / CASE` |
| `MESSAGE-UC-01` | Viewer kết nối, subscribe và gửi chat; user chưa đăng nhập/sai room/message quá dài phải bị từ chối đúng chỗ | CONNECT/SUBSCRIBE/SEND authorization, validation, error contract và negative tests | `RT-01`, `SEC-06`, `SPR-01` | `UC-P0` | H/M | `FOUNDATION / CASE` |
| `STREAM-UC-01` | Hai webhook start/end đến đồng thời hoặc đảo thứ tự nhưng stream chỉ chuyển `CREATED -> LIVE -> ENDED` hợp lệ | State machine, conditional update/lock, repeatable race test và transition audit | `CON-01`, `SPR-01` | `UC-P0` | H/H | `SENIOR / CASE` |
| `FEED-UC-01` | Liệt kê hàng triệu stream mà không `findAll`, manual N+1 hoặc duplicate/missing giữa các trang | Projection/batch query, stable cursor, query count và before/after plan | `DB-01`, `SQL-01`, `API-01` | `UC-P0` | H/H | `SENIOR / CASE+LAB` |
| `CACHE-UC-01` | Stream nổi tiếng vừa hết TTL hoặc Redis down khiến hàng nghìn request cùng fallback PostgreSQL | TTL jitter, single-flight/bounded fallback, breaker và DB protection evidence | `RED-01`, `RES-03`, `TX-01` | `UC-P0` | H/H | `SENIOR / CASE+LAB` |
| `WEBHOOK-UC-01` | Attacker giả hoặc replay webhook media-server để tự ý start/end stream | HMAC, timestamp/event ID, secret rotation, idempotency và negative tests | `SEC-05`, `CON-01` | `UC-P0` | H/M | `SENIOR / CASE` |
| `SESSION-UC-01` | User bấm logout-all nhưng access token/session cache cũ vẫn gọi được API | Session-backed validation, revoke version, Redis invalidation và cache-down policy | `SEC-01`, `SEC-02`, `RED-01` | `UC-P0` | H/H | `SENIOR / CASE` |
| `HISTORY-UC-01` | Chat/ledger history tăng tới hàng trăm triệu row và cần query theo thời gian cùng retention/archive | Time partition, pruning, local index, detach/archive và benchmark | `DB-03`, `DB-04` | `UC-P1` | H/H | `SENIOR / LAB` |
| `DEPLOY-UC-01` | Rolling deploy khi còn HTTP request, WebSocket session và message đang xử lý nhưng không mất hoặc duplicate work | Readiness/drain, graceful shutdown, mixed-version contract và rollback rehearsal | `OPS-01`, `MQ-01`, `RT-01` | `UC-P1` | H/H | `SENIOR / CASE+LAB` |
| `RUNTIME-UC-01` | Nâng backend đang phục vụ traffic từ Java 17 lên Java 21/25, rồi chọn platform thread, virtual thread hay WebFlux mà không đổi business behavior | Compatibility matrix, build/runtime pin, blocking-I/O workload, thread/connection budget, rollback và migrate/defer decision | `JDK-01`, `JDK-02`, `REACT-01`, `OPS-01` | `UC-P1` | H/M | `SENIOR / DECISION+LAB` |
| `TEST-UC-01` | Gift/webhook test pass trên máy dev nhưng fail ngẫu nhiên trên CI hoặc phụ thuộc database/Redis còn sót từ lần chạy trước | Hermetic Testcontainers setup, deterministic clock/data, risk-based test boundary, concurrency/property/fault evidence | `TEST-01`, `TEST-02` | `UC-P0` | H/H | `SENIOR / CASE+LAB` |
| `MIGRATION-UC-01` | Deploy schema gift/stream mới khi old và new application cùng chạy, không downtime và fresh database vẫn bootstrap được | Flyway baseline, expand-contract, backfill, compatibility window, lock/rollback rehearsal | `MIG-01`, `DB-04`, `OPS-01` | `UC-P0` | H/H | `SENIOR / CASE+LAB` |
| `CONFIG-UC-01` | Production khởi động với default secret, nhầm dev profile hoặc public Swagger/diagnostic endpoint | Typed config, environment isolation, fail-fast secret validation, public-surface negative test và startup evidence | `CFG-01`, `SEC-03`, `OPS-01` | `UC-P0` | H/H | `SENIOR / CASE` |
| `AUTHZ-UC-01` | Viewer hoặc streamer khác gọi được admin/stream-owner endpoint vì URL matcher và method authorization lệch nhau | Deny-by-default matrix, role/ownership checks, matcher boundary và negative regression tests | `SEC-06`, `SEC-04`, `TEST-02` | `UC-P0` | H/H | `SENIOR / CASE` |
| `SECRET-UC-01` | Stream key, JWT, webhook secret hoặc credential xuất hiện trong API DTO, exception hay log tập trung | Audience-specific DTO, redaction, secret storage/rotation và log/response scanning test | `SEC-03`, `CFG-01`, `SEC-05` | `UC-P0` | H/H | `SENIOR / CASE` |
| `JVM-UC-01` | Livestream chạy lâu làm heap tăng, GC pause hoặc CPU spike vì viewer/chat allocation nhưng service chưa crash rõ ràng | JFR/heap/thread evidence, allocation profile, GC/JIT explanation và before/after workload | `JVM-01`, `JAVA-01`, `OBS-01` | `UC-P0` | H/H | `SENIOR / LAB` |
| `SPRING-UC-01` | Service gọi nội bộ method `@Transactional`/`@Async`, khiến transaction hoặc proxy advice không chạy và gift/stream state bị ghi dở | Bean/proxy boundary, request path, rollback test, transaction ownership và refactor alternatives | `SPR-01`, `TX-01`, `TEST-02` | `UC-P0` | H/H | `SENIOR / CASE` |
| `VIEWCOUNT-UC-01` | Hàng chục nghìn viewer join/leave đồng thời nhưng displayed count không âm, không tăng vô hạn và chấp nhận sai số có chủ đích | Exact-vs-approximate invariant, atomic update, dedup/expiry, reconciliation và contention measurement | `JAVA-01`, `CON-01`, `RED-01`, `SQL-01` | `UC-P0` | H/H | `SENIOR / CASE+LAB` |
| `PRESENCE-UC-01` | Client mất mạng không gửi disconnect làm viewer/presence “ma” tồn tại mãi | Heartbeat/lease TTL, reconnect identity, cleanup/reconciliation và Redis-down behavior | `RT-01`, `RED-01`, `RES-01` | `UC-P1` | H/M | `SENIOR / CASE+LAB` |
| `RATE-UC-01` | Bot spam chat/login/gift làm cạn thread, connection pool hoặc quota của user thật | Per-actor/room/IP limit, trusted-proxy rule, bounded queue, load shedding và fairness evidence | `RES-03`, `RED-01`, `API-01`, `SEC-04` | `UC-P0` | H/H | `SENIOR / CASE+LAB` |
| `MODERATION-UC-01` | Moderator mute/ban/report content trong lúc message đang bay qua nhiều node nhưng audit và quyền vẫn đúng | Policy/ownership boundary, moderation state propagation, race test, appeal/audit model | `DDD-01`, `SEC-04`, `RT-01`, `MQ-01` | `UC-P1` | H/M | `SENIOR / CASE` |
| `NOTIFY-UC-01` | Một streamer có hàng triệu follower bấm “Go Live” nhưng notification không được gửi trùng, quá muộn hoặc đánh sập transactional path | Fan-out strategy, batching, preference/dedup, retry/DLQ, lag và delivery observability | `MQ-01`, `KFK-01`, `OBS-01`, `API-01` | `UC-P1` | H/H | `SENIOR / CASE+LAB` |
| `SCHEDULE-UC-01` | Hai scheduler/node cùng mở hoặc kết thúc một scheduled livestream khi clock lệch hoặc job retry | Idempotent command, ownership/fencing, database state transition và deterministic time test | `SPR-01`, `CON-01`, `TX-01` | `UC-P1` | M/M | `SENIOR / CASE` |
| `PAYOUT-UC-01` | Cuối ngày chốt doanh thu cho streamer từ hàng triệu gift, có refund/chargeback và job có thể chạy lại | Ledger invariant, immutable adjustment, idempotent settlement, reconciliation và audit/restore path | `DDD-01`, `WAL-01`, `EVT-01`, `DR-01` | `UC-P1` | H/H | `SENIOR / CASE+LAB` |
| `PLAYBACK-UC-01` | Viewer lấy playback URL rồi chia sẻ, dùng sau khi hết hạn hoặc tiếp tục xem sau khi bị ban | Signed short-lived access, audience/entitlement, CDN cache boundary, revoke window và negative tests | `SEC-03`, `SEC-04`, `API-01`, `RED-01` | `UC-P0` | H/M | `SENIOR / CASE` |

### 3.2. Architect và Expert cases

| ID | Bài toán concrete | Phải xây/chứng minh | Owner item | Priority | RW/PV | Difficulty / mode |
| --- | --- | --- | --- | --- | --- | --- |
| `DR-UC-01` | Operator xóa nhầm gift/ledger data và phải phục hồi tới trước thời điểm lỗi | Isolated restore/PITR, actual RPO/RTO, invariant verification và controlled merge/cutover | `DR-01` | `UC-P1` | M/H | `ARCHITECT / LAB` |
| `MS-UC-01` | Analytics làm core application deploy/chạy chậm; quyết định tách service hay giữ modular monolith | Module boundary, service-owned data, event projection, contract và extraction scorecard | `DDD-01`, `MS-01` | `UC-P2` | M/H | `ARCHITECT / CAPSTONE` |
| `API-UC-01` | Public API đi qua DNS/TLS/CDN/load balancer/gateway và chịu bot/retry/DDoS-like traffic | Timeout budget, cache/quota/idempotency, trusted proxy boundary, shedding và multi-region routing | `API-01`, `RES-01`, `SEC-04`, `ARCH-01` | `UC-P0` | H/H | `ARCHITECT / CAPSTONE+LAB` |
| `DISCOVERY-UC-01` | Hàng triệu livestream cần search/recommend/feed theo tag, ngôn ngữ, trạng thái live và ranking thay đổi liên tục | Access pattern, freshness, cursor/ranking stability, PostgreSQL-vs-search projection, backfill và cost | `DB-01`, `DATA-01`, `CLOUD-01` | `UC-P1` | H/M | `ARCHITECT / CAPSTONE+LAB` |
| `RECORDING-UC-01` | Sau khi live kết thúc, recording/thumbnail/transcode callback đến chậm, trùng hoặc thất bại nhưng video vẫn phải sẵn sàng đúng trạng thái | Object metadata, async workflow, idempotent callback, retention, retry/reconciliation và storage cost | `DATA-01`, `CLOUD-01`, `MQ-01`, `OPS-01` | `UC-P2` | M/M | `ARCHITECT / CAPSTONE` |
| `INCIDENT-UC-01` | Gift latency tăng và consumer lag tích tụ trong giờ cao điểm; team phải giảm tác động, phục hồi và ngăn tái diễn | SLO/alert, incident command, hypothesis timeline, rollback/degraded mode, postmortem/ADR và mentoring follow-up | `OBS-01`, `OPS-01`, `LEAD-01`, `TEST-02` | `UC-P1` | H/H | `ARCHITECT / INCIDENT LAB` |

## 4. Coverage map tới toàn bộ learning items

Coverage dưới đây có nghĩa là **đã có ít nhất một scenario để học item**, không có nghĩa item đã đạt depth hoặc có evidence. Roadmap vẫn sở hữu Stage/order/learning priority.

| Roadmap group | Learning item -> use case concrete |
| --- | --- |
| Stage 0 — Foundation | `JDK-01`, `JDK-02` -> `RUNTIME-UC-01`; `TEST-01` -> `TEST-UC-01`; `MIG-01` -> `MIGRATION-UC-01`; `CFG-01` -> `CONFIG-UC-01`; `SEC-01`, `SEC-02` -> `SESSION-UC-01`; `SEC-06` -> `AUTHZ-UC-01`; `SEC-03` -> `SECRET-UC-01`; `SEC-05` -> `WEBHOOK-UC-01` |
| Stage 1 — Java/JVM/concurrency | `JAVA-01` -> `GIFT-UC-01`, `VIEWCOUNT-UC-01`; `JVM-01` -> `JVM-UC-01`; `CON-01` -> `STREAM-UC-01`, `GIFT-UC-01` |
| Stage 2 — Spring/API/reliability | `SPR-01` -> `SPRING-UC-01`; `API-01` -> `API-UC-01`, `FEED-UC-01`; `TX-01` -> `GIFT-UC-01`, `SPRING-UC-01`; `RES-01`, `RES-02` -> `RECONNECT-UC-01`; `RES-03` -> `CACHE-UC-01`, `RATE-UC-01` |
| Stage 3 — PostgreSQL | `SQL-01`, `DB-01` -> `FEED-UC-01`; `WAL-01` -> `GIFT-UC-01`, `PAYOUT-UC-01`; `DB-04` -> `MIGRATION-UC-01`, `HISTORY-UC-01` |
| Stage 4 — Redis | `RED-01` -> `CACHE-UC-01`, `VIEWCOUNT-UC-01`, `PRESENCE-UC-01` |
| Stage 5–6 — Messaging/event | `MQ-01` -> `EVT-UC-01`, `NOTIFY-UC-01`; `KFK-01` -> `HOT-UC-01`, `ANALYTICS-UC-01`; `EVT-01` -> `EVT-UC-01`, `PAYOUT-UC-01` |
| Stage 7 — Identity/realtime | `SEC-04` -> `BAN-UC-01`, `AUTHZ-UC-01`; `RT-01` -> `CHAT-UC-01`, `RECONNECT-UC-01`, `PRESENCE-UC-01` |
| Stage 8 — Operability | `OBS-01` -> `LIVE-UC-01`, `INCIDENT-UC-01`; `OPS-01` -> `DEPLOY-UC-01`, `CONFIG-UC-01`, `INCIDENT-UC-01` |
| Stage 9 — Data lifecycle | `DB-02` -> `REGION-UC-01`; `DB-03` -> `HISTORY-UC-01`; `DR-01` -> `DR-UC-01`, `PAYOUT-UC-01` |
| Stage 10 — Architecture | `DDD-01` -> `MS-UC-01`, `PAYOUT-UC-01`; `DS-01` -> `REGION-UC-01`, `EVT-UC-01`; `MS-01` -> `ANALYTICS-UC-01`, `MS-UC-01` |
| Stage 11 — Solution architecture | `ARCH-01` -> `LIVE-UC-01`, `API-UC-01`, `HOT-UC-01` |
| Cross-cutting tracks | `TEST-02` -> `TEST-UC-01` và evidence của mọi case; `LEAD-01` -> `INCIDENT-UC-01` và review/ADR/teach-back của mọi case |
| P2 extensions | `DATA-01` -> `ANALYTICS-UC-01`, `DISCOVERY-UC-01`, `RECORDING-UC-01`; `CLOUD-01` -> `REGION-UC-01`, `DEPLOY-UC-01`, `RECORDING-UC-01`; `REACT-01` -> `RUNTIME-UC-01`, `LIVE-UC-01` |

Tại snapshot này, cả `43/43` learning item trong backlog chính, cross-cutting track và P2 extension đều có scenario owner. Con số này phải được kiểm tra lại khi roadmap thêm/rename/xóa item; không cập nhật thủ công nếu validation cho kết quả khác.

## 5. Từ catalog tới learning case

Khi checkpoint tới `LEARNING_CASE`, Agent thực hiện đúng thứ tự:

1. Chọn use-case ID trong file này có prerequisite khớp Stage hiện tại.
2. Chọn **một failure slice** đủ nhỏ; không active toàn bộ capstone 100k viewers như một feature khổng lồ.
3. Tạo `docs/learning/cases/<owner-item>-<concrete-scenario>.md` từ learning-case template.
4. Giữ tiêu đề concrete; ghi theory/question bank dưới phần knowledge links.
5. Chỉ chuyển tới implementation khi reproducer và design gate đã đạt. `CAPSTONE` có thể đóng bằng design + experiment evidence mà không sửa application.

Ví dụ phân rã `LIVE-UC-01`:

- Slice 1: đo một application instance giữ được bao nhiêu simulated viewer sessions.
- Slice 2: Redis viewer heartbeat bị chậm/down thì read path degrade ra sao.
- Slice 3: 30.000 client reconnect trong 60 giây có làm cạn connection pool không.
- Slice 4: mất một node thì remaining capacity và p99 thay đổi thế nào.

Các slice có thể dùng chung một capstone, nhưng tại một thời điểm chỉ có một learning case chính `ACTIVE`.

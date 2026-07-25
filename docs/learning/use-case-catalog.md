# Livestream Domain Use-case Catalog

> Trạng thái: `CANONICAL DOMAIN SCENARIO CATALOG`<br>
> Phạm vi: bài toán Livestream dùng để học, implement, chạy lab và luyện System Design<br>
> Cập nhật: `2026-07-26`

File này trả lời câu hỏi: **“Tôi sẽ giải bài toán Livestream cụ thể nào?”**

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

## 2. Chín bài toán lớn, kinh điển

Đây là spine dùng để luyện Senior/Solution Architect. Không chạy theo thứ tự bảng; roadmap và prerequisite quyết định thời điểm kích hoạt.

### LIVE-UC-01

**Bài toán:** Xử lý 100.000 người cùng xem một livestream tại một thời điểm, trong đó một phòng có thể chiếm phần lớn traffic.

- Phải chứng minh: capacity assumption, connection/concurrency budget, viewer-state strategy, p95/p99, bottleneck đầu tiên, headroom khi mất một node/zone và degraded mode.
- Invariant: control plane vẫn xác định đúng stream đang `LIVE`; overload viewer/read path không được làm hỏng durable stream state.
- Output chính: capacity sheet, workload/fault lab, architecture dossier và bản trình bày 2/15/45 phút.
- Owner learning items: `ARCH-01`, `RT-01`, `RED-01`, `OBS-01`.
- Mode: `CAPSTONE + LAB`; không mặc định cần media server thật.

### CHAT-UC-01

**Bài toán:** Vận hành phòng chat có 50.000 người kết nối, hàng nghìn message mỗi giây và một nhóm client đọc rất chậm.

- Phải chứng minh: auth ở CONNECT/SUBSCRIBE/SEND, per-room ordering scope, bounded outbound queue, slow-consumer policy, rate limit và message-size limit.
- Invariant: client chậm không được làm đầy memory hoặc chặn toàn bộ room; user bị mute/ban không phát được message.
- Output chính: WebSocket negative tests, load test, queue-age/bytes metric và recovery runbook.
- Owner learning items: `RT-01`, `RES-03`, `RED-01`, `OBS-01`.
- Mode: `CASE + LAB`.

### RECONNECT-UC-01

**Bài toán:** Sau khi gateway restart hoặc mạng chập chờn, 30.000 client reconnect trong 60 giây mà auth, Redis và database không bị đánh sập.

- Phải chứng minh: exponential backoff + jitter phía client, admission control, session resume, token refresh budget và staggered recovery.
- Invariant: reconnect không tạo duplicate durable action/subscription trái quyền; backlog phục hồi có giới hạn.
- Output chính: reconnect-storm workload, saturation timeline và before/after recovery evidence.
- Owner learning items: `RT-01`, `RES-01`, `RES-02`, `RES-03`, `ARCH-01`.
- Mode: `LAB + CAPSTONE`.

### GIFT-UC-01

**Bài toán:** Trong một sự kiện livestream, xử lý 10.000 yêu cầu tặng quà trong một phút mà ví không âm, không double-spend và client retry không tạo giao dịch thứ hai.

- Phải chứng minh: money representation, idempotency key, atomic ledger update, contention strategy và response khi outcome bị timeout/không rõ.
- Invariant: tổng debit/credit được bảo toàn; một business command chỉ được ghi nhận một lần.
- Output chính: concurrency reproducer, ledger constraints, idempotency tests và conflict/throughput measurement.
- Owner learning items: `JAVA-01`, `CON-01`, `WAL-01`, `TX-01`, `API-01`.
- Mode: `CASE + IMPLEMENTATION + LAB`.

### EVT-UC-01

**Bài toán:** Sau khi trừ tiền mua quà, hệ thống phải tạo gift, publish event và cập nhật projection dù process chết ở bất kỳ điểm nào.

- Phải chứng minh: business write + outbox atomic, relay at-least-once, inbox/dedup, ACK/offset sau durable processing và reconciliation.
- Invariant: không mất durable intent, không gửi gift hai lần và không double-settle wallet.
- Output chính: kill-at-crash-point experiment, invariant query, trace request-to-consumer và DLQ/replay runbook.
- Owner learning items: `EVT-01`, `MQ-01`, `KFK-01`, `TX-01`, `OBS-01`.
- Mode: `CASE + IMPLEMENTATION + FAULT LAB`.

### HOT-UC-01

**Bài toán:** Một streamer nổi tiếng tạo hot room/hot key/hot partition lớn gấp hàng trăm lần traffic trung bình.

- Phải chứng minh: phát hiện skew theo room/key/partition, fan-out strategy, hot-key mitigation, fairness và giới hạn của việc chỉ scale thêm node.
- Invariant: một room nóng không được hút hết connection, broker, Redis hoặc database capacity của các room khác.
- Output chính: skewed workload, partition/key metrics, alternative comparison và load-shedding decision.
- Owner learning items: `RED-01`, `KFK-01`, `RT-01`, `ARCH-01`.
- Mode: `LAB + CAPSTONE`.

### BAN-UC-01

**Bài toán:** Ban một user đang có nhiều access token, session Redis và WebSocket connection để lệnh ban có hiệu lực trên REST, chat và event path.

- Phải chứng minh: ownership/role checks, revoke-all, cache invalidation, active connection handling, audit và bounded stale window.
- Invariant: user bị ban không tiếp tục thao tác qua token/connection cũ; không log token hoặc dữ liệu nhạy cảm.
- Output chính: threat timeline, REST/WebSocket negative tests, revoke fault test và audit evidence.
- Owner learning items: `SEC-02`, `SEC-04`, `RT-01`, `RED-01`.
- Mode: `CASE + SECURITY LAB`.

### ANALYTICS-UC-01

**Bài toán:** Xây near-real-time analytics cho lượt xem/gift/chat, có thể replay và backfill mà không làm chậm transactional path.

- Phải chứng minh: event contract/version, partition key, consumer lag, idempotent projection, late event, replay vào shadow target và reconciliation.
- Invariant: analytics có thể chậm nhưng không được làm sai hoặc chặn wallet/stream transaction; replay không phát external side effect lần hai.
- Output chính: event pipeline lab, lag/replay evidence, storage decision và extraction scorecard.
- Owner learning items: `KFK-01`, `EVT-01`, `MS-01`, `DATA-01`.
- Mode: `LAB + CAPSTONE`; tách service chỉ khi scorecard chứng minh có lợi.

### REGION-UC-01

**Bài toán:** Phục vụ người xem ở nhiều region nhưng vẫn giữ single-writer hoặc regional ownership cho stream/wallet và có kế hoạch failover rõ.

- Phải chứng minh: traffic routing, read freshness, write ownership, RPO/RTO, fencing, failover/failback và chi phí egress/operation.
- Invariant: không xuất hiện hai writer cùng sở hữu một wallet/stream invariant; failover không âm thầm mất hoặc nhân đôi durable command.
- Output chính: multi-region architecture dossier, failure matrix, capacity/cost model và DR runbook.
- Owner learning items: `DB-02`, `DR-01`, `DS-01`, `ARCH-01`, `CLOUD-01`.
- Mode: `CAPSTONE`; không mặc định implement multi-region thật.

## 3. Mười use case supporting để implement hoặc chạy lab

| ID | Bài toán cụ thể | Phải xây/chứng minh | Owner item | Mode |
| --- | --- | --- | --- | --- |
| `STREAM-UC-01` | Hai webhook start/end đến đồng thời hoặc đảo thứ tự nhưng stream chỉ chuyển `CREATED -> LIVE -> ENDED` hợp lệ | State machine, conditional update/lock, repeatable race test và transition audit | `CON-01`, `SPR-01` | `CASE` |
| `FEED-UC-01` | Liệt kê hàng triệu stream mà không `findAll`, không manual N+1 và không duplicate/missing giữa các trang | Projection/batch query, stable cursor, query count và before/after plan | `DB-01`, `SQL-01`, `API-01` | `CASE/LAB` |
| `CACHE-UC-01` | Stream nổi tiếng vừa hết TTL hoặc Redis down khiến hàng nghìn request cùng fallback PostgreSQL | TTL jitter, single-flight/bounded fallback, breaker và DB protection evidence | `RED-01`, `RES-03`, `TX-01` | `CASE/LAB` |
| `WEBHOOK-UC-01` | Attacker giả hoặc replay webhook media-server để tự ý start/end stream | HMAC, timestamp/event ID, secret rotation, idempotency và negative tests | `SEC-05`, `CON-01` | `CASE` |
| `SESSION-UC-01` | User bấm logout-all nhưng access token/session cache cũ vẫn gọi được API | Session-backed validation, revoke version, Redis invalidation và cache-down policy | `SEC-01`, `SEC-02`, `RED-01` | `CASE` |
| `HISTORY-UC-01` | Chat/ledger history tăng tới hàng trăm triệu row và cần query theo thời gian cùng retention/archive | Time partition, pruning, local index, detach/archive và benchmark | `DB-03`, `DB-04` | `LAB` |
| `DR-UC-01` | Operator xóa nhầm gift/ledger data và phải phục hồi tới trước thời điểm lỗi | Isolated restore/PITR, actual RPO/RTO, invariant verification và controlled merge/cutover | `DR-01` | `LAB` |
| `DEPLOY-UC-01` | Rolling deploy khi còn HTTP request, WebSocket session và message đang xử lý nhưng không làm mất hoặc duplicate work | Readiness/drain, graceful shutdown, mixed-version contract và rollback rehearsal | `OPS-01`, `MQ-01`, `RT-01` | `CASE/LAB` |
| `MS-UC-01` | Analytics làm core application deploy/chạy chậm; quyết định tách service hay giữ modular monolith | Module boundary, service-owned data, event projection, contract và extraction scorecard | `DDD-01`, `MS-01` | `CAPSTONE` |
| `API-UC-01` | Public API đi qua DNS/TLS/CDN/load balancer/gateway và chịu bot/retry/DDoS-like traffic | Timeout budget, cache/quota/idempotency, trusted proxy boundary, shedding và multi-region routing | `API-01`, `RES-01`, `SEC-04`, `ARCH-01` | `CAPSTONE/LAB` |

## 4. Từ catalog tới learning case

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

# Security Interview Question Bank — Webhook HMAC, Replay Protection and Secret Rotation

> Status: `DRAFT`<br>
> Domain owner: `security / HTTP API / event delivery`<br>
> Active slice: `NONE`; preview target `SEC-05 — webhook HMAC, timestamp/event ID, replay and secret rotation`<br>
> Related roadmap: [Stage 0](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-0---stabilize-the-laboratory)<br>
> Related depth rubric: [Security](../../../knowledge-depth-rubric.md#38-security-và-identity--p0-target-d3), [HTTP/API](../../../knowledge-depth-rubric.md#36-http-api-design-và-network-fundamentals--p0-target-d3), [Distributed systems](../../../knowledge-depth-rubric.md#312-distributed-systems-fundamentals--p0-target-d3)<br>
> Related project guide: [RTMP Webhook Guide](../../../../engineering/rtmp-webhook-guide.md), [API contract](../../../../contracts/api-contract.md#rtmp-webhook)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/security/theory/core/webhook-authentication-replay-and-idempotency.md`<br>
> Updated: `2026-07-25`

Preview này không implement `SEC-05`, không active case và không tạo evidence. `Interview likelihood` là heuristic trong phạm vi backend/API security, không phải tỷ lệ thị trường đã đo. Mọi câu giữ `UNANSWERED`, test `NOT RUN`.

## Project anchor

Current RTMP webhook dùng `X-Webhook-Secret` tĩnh có default development value và so sánh trực tiếp. Request có `streamKey`/`timestamp` nhưng chưa có HMAC trên raw body, timestamp window, event ID, idempotency claim hoặc key rotation. Đây là điểm vận dụng sau nhóm câu phổ biến, không phải câu mở đầu.

## Coverage

| Topic | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Webhook authentication and integrity | 3 | 2 | 1 | 0 |
| Replay, delivery and idempotency | 2 | 4 | 1 | 0 |
| Testing, operations and rotation | 0 | 2 | 0 | 1 |
| **Tổng** | **5** | **8** | **2** | **1** |

## Recommended practice order

1. First pass — câu phổ biến: `SEC-WEBHOOK-001` đến `SEC-WEBHOOK-006`, `SEC-WEBHOOK-008`, `SEC-WEBHOOK-009`, `SEC-WEBHOOK-010`, `SEC-WEBHOOK-012`.
2. Senior follow-up: `SEC-WEBHOOK-007`, `SEC-WEBHOOK-011`, `SEC-WEBHOOK-013`.
3. Project application: dùng `SEC-WEBHOOK-003` đến `SEC-WEBHOOK-013` để phân tích current RTMP webhook.
4. Architect/Expert stretch: `SEC-WEBHOOK-014` đến `SEC-WEBHOOK-016`.

## Questions

### SEC-WEBHOOK-001 — `FOUNDATION`
**Question:** Webhook là gì, khác polling và API request thông thường ở điểm nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu mở đầu phổ biến khi thảo luận integration/event callback.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Hiểu provider chủ động callback, delivery không hoàn hảo và endpoint public không đồng nghĩa caller đáng tin.<br>
**Answer outline:** Webhook là HTTP callback do provider phát khi có event; giảm polling/latency nhưng receiver phải xác thực nguồn, chịu duplicate/retry/out-of-order và xử lý bất đồng bộ hoặc idempotent.<br>
**Required trade-offs:** Polling dễ kiểm soát nhịp nhưng tốn request/trễ; webhook nhanh hơn nhưng tăng security/reliability responsibility.<br>
**Follow-up ladder:** Timeout? Retry? Ordering? Nếu receiver down?<br>
**Red flags:** Webhook được coi là message delivery exactly-once.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-05 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-WEBHOOK-002 — `FOUNDATION`
**Question:** Webhook nên xác thực ai, và tại sao thường không dùng JWT của end user?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu authentication boundary cơ bản.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Phân biệt service identity với user identity và business authorization.<br>
**Answer outline:** Webhook xác thực provider/service gửi callback; user không trực tiếp gọi nên user JWT sai trust model. Sau service authentication vẫn phải validate event scope/resource và business state transition.<br>
**Required trade-offs:** Shared-secret HMAC đơn giản; mTLS/OAuth client credentials quản trị identity mạnh hơn nhưng vận hành phức tạp hơn.<br>
**Follow-up ladder:** Nhiều provider? Tenant-specific key? IP allowlist có đủ không?<br>
**Red flags:** Endpoint webhook public nên không cần authentication.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-05 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-WEBHOOK-003 — `FOUNDATION`
**Question:** HMAC là gì, và tốt hơn gửi nguyên shared secret trong header ở điểm nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu webhook security cốt lõi.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Message authentication, integrity và giới hạn của shared-secret scheme.<br>
**Answer outline:** Sender dùng secret và MAC algorithm ký message; receiver tính lại để xác minh caller biết secret và bytes không bị đổi. Secret không truyền theo mỗi request, nhưng TLS, secret storage và rotation vẫn bắt buộc.<br>
**Required trade-offs:** HMAC đơn giản/nhanh nhưng hai bên cùng giữ secret; asymmetric signature giảm shared-secret blast radius nhưng tăng key/certificate complexity.<br>
**Follow-up ladder:** HMAC khác hash? Có mã hóa payload không? Secret lộ thì sao?<br>
**Red flags:** HMAC được mô tả như encryption hoặc thay thế TLS.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-05 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-WEBHOOK-004 — `FOUNDATION`
**Question:** Replay attack với webhook là gì, và vì sao signature hợp lệ vẫn chưa đủ?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — follow-up gần như trực tiếp sau HMAC.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Freshness/uniqueness tách khỏi authenticity/integrity.<br>
**Answer outline:** Attacker gửi lại request đã ký hợp lệ; HMAC vẫn valid vì bytes không đổi. Bind timestamp và unique event ID vào signed message, reject ngoài time window và claim event ID để chặn xử lý lặp.<br>
**Required trade-offs:** Window ngắn giảm replay nhưng nhạy clock/network delay; dedup store tăng state/cost.<br>
**Follow-up ladder:** Nếu attacker replay trong window? Clock skew? Event ID retention?<br>
**Red flags:** Đổi secret sau mỗi request hoặc chỉ kiểm timestamp là đủ.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-05 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-WEBHOOK-005 — `FOUNDATION`
**Question:** Idempotency trong webhook nghĩa là gì, và khác “nhận request đúng một lần” thế nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu integration/retry rất phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** At-least-once delivery và invariant của side effect.<br>
**Answer outline:** Cùng logical event có thể đến nhiều lần nhưng observable business effect chỉ xảy ra một lần hoặc hội tụ cùng state; không cần giả định transport exactly-once, receiver dùng event ID/unique constraint/state guard.<br>
**Required trade-offs:** Dedup chính xác cần durable state; operation tự nhiên idempotent đơn giản hơn nhưng vẫn phải xét side effects phụ.<br>
**Follow-up ladder:** PUT có luôn idempotent? Email/event publish? Retry sau timeout?<br>
**Red flags:** Trả `200` cho duplicate nhưng vẫn publish side effect lần hai.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-05 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-WEBHOOK-006 — `SENIOR`
**Question:** Vì sao phải ký raw request body hoặc một canonical payload được định nghĩa chặt, không ký object JSON sau deserialize?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — implementation pitfall phổ biến của webhook HMAC.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Byte-level determinism qua whitespace, field order, Unicode và number formatting.<br>
**Answer outline:** Deserialize/reserialize có thể đổi bytes dù semantics giống nhau; signature contract phải chỉ rõ exact bytes/canonical string và bind method/path/timestamp/event ID nếu cần. Capture body một lần để verify rồi parse.<br>
**Required trade-offs:** Raw-body signing dễ tương thích nhưng middleware phải preserve bytes; canonicalization linh hoạt hơn nhưng specification/implementation phức tạp.<br>
**Follow-up ladder:** Gzip? Charset? Proxy transform? Body stream chỉ đọc một lần?<br>
**Red flags:** Dùng `object.toString()` hoặc map iteration order để ký.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-05 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-WEBHOOK-007 — `SENIOR`
**Question:** Thứ tự validate một webhook HMAC an toàn nên như thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — follow-up implementation thường gặp.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Fail-fast có giới hạn, authentication trước business parsing và side-effect boundary.<br>
**Answer outline:** Bound request size; parse/validate required signature metadata; chọn allowed key ID; reject timestamp policy; compute HMAC trên signed bytes và compare constant-time; claim event ID; sau đó parse schema/apply guarded transition. Không side effect trước auth/dedup gate.<br>
**Required trade-offs:** Early cheap rejects giảm CPU nhưng response/timing không nên trở thành oracle; dedup claim cần recovery nếu processing fail.<br>
**Follow-up ladder:** Unknown key ID? Malformed hex? Rate limit? Claim trước hay cùng transaction?<br>
**Red flags:** Deserialize và thay đổi DB trước khi verify signature.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-05 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-WEBHOOK-008 — `SENIOR`
**Question:** Thiết kế timestamp window chống replay thế nào khi có clock skew và network delay?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — replay-protection scenario phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Freshness policy, trusted clock và boundary tests.<br>
**Answer outline:** Timestamp phải nằm trong signed payload/headers; dùng server clock có sync, bounded past/future skew, explicit window và deterministic clock trong test. Timestamp chỉ giới hạn replay window, event ID mới chặn duplicate trong window.<br>
**Required trade-offs:** Window rộng tăng replay exposure; window hẹp tăng false reject khi clock/network bất ổn.<br>
**Follow-up ladder:** NTP outage? Queue delayed event? Unix seconds vs milliseconds?<br>
**Red flags:** Tin timestamp trong body nhưng không đưa nó vào signature.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-05 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-WEBHOOK-009 — `SENIOR`
**Question:** Event ID nên được lưu và claim thế nào để hai request duplicate đồng thời không cùng chạy side effect?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — concurrency/idempotency follow-up điển hình.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Atomic uniqueness, transaction boundary và retry-after-failure semantics.<br>
**Answer outline:** Dùng durable unique key theo provider/event ID hoặc inbox row; insert/claim atomic, gắn processing status/result; business transition và claim cần transaction/recovery contract; duplicate đọc outcome thay vì chạy lại mù quáng.<br>
**Required trade-offs:** PostgreSQL bền/chính xác nhưng thêm write latency; Redis nhanh nhưng expiry/failover có thể phá dedup invariant nếu là authority duy nhất.<br>
**Follow-up ladder:** Processing crash sau claim? TTL? Same ID khác payload? Poison event?<br>
**Red flags:** Check-then-insert không unique constraint hoặc in-memory set trên nhiều instance.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-05 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-WEBHOOK-010 — `SENIOR`
**Question:** Webhook receiver nên trả HTTP status nào để provider retry đúng mà không tạo retry storm?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — câu API integration vận hành phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Permanent/transient failure classification và acknowledgement timing.<br>
**Answer outline:** Contract provider-specific; 2xx khi accepted hoặc duplicate đã được xử lý idempotently; malformed/auth failures là reject rõ; transient internal/unavailable có retryable response với backoff/jitter/budget. Không acknowledge trước durable acceptance nếu event không thể phục hồi.<br>
**Required trade-offs:** Synchronous processing đơn giản nhưng dễ timeout; durable enqueue/inbox cho ack nhanh hơn nhưng thêm component và eventual consistency.<br>
**Follow-up ladder:** `202` vs `200`? Timeout sau commit? `Retry-After`? DLQ ownership?<br>
**Red flags:** Mọi exception đều trả `200` hoặc mọi `4xx/5xx` đều retry vô hạn.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-05 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-WEBHOOK-011 — `SENIOR`
**Question:** Duplicate, delayed hoặc out-of-order `stream-started`/`stream-ended` nên được xử lý bằng state machine nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — scenario domain-specific sau idempotency foundation.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Event dedup không thay thế transition invariant và ordering policy.<br>
**Answer outline:** Định nghĩa allowed transitions và version/event time policy; duplicate cùng event không đổi state/side effect; stale end/start không được overwrite state mới; concurrent transition dùng conditional update/version/lock và test barrier.<br>
**Required trade-offs:** Strict ordering có thể reject event hợp lệ đến trễ; reconciliation tăng complexity nhưng phục hồi được missing event.<br>
**Follow-up ladder:** End trước start? Reconnect tạo session mới? Timestamp bằng nhau? Redis fail sau DB commit?<br>
**Red flags:** Chỉ dựa vào thứ tự request đến controller.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-05 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-WEBHOOK-012 — `SENIOR`
**Question:** Bộ negative/integration tests tối thiểu cho webhook HMAC và replay protection gồm gì?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — “bạn test security design thế nào?” là Senior follow-up tự nhiên.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Test contract trên bytes, time, concurrency và side effects thật.<br>
**Answer outline:** Valid signature; missing/wrong/malformed signature; body/header tamper; stale/future timestamp; duplicate và concurrent same event ID; unknown/old/new key; out-of-order transition; assert DB/Redis/event side effects không chạy khi reject.<br>
**Required trade-offs:** Unit test crypto nhanh; MockMvc/integration test mới chứng minh filter/body caching/serialization/transaction behavior.<br>
**Follow-up ladder:** Fixed clock? Real PostgreSQL? Log capture? Property/fuzz cases?<br>
**Red flags:** Chỉ test helper trả true với một happy-path string.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-05 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-WEBHOOK-013 — `SENIOR`
**Question:** Rotate webhook secret không downtime bằng key ID và bounded overlap như thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — credential-lifecycle follow-up cho Senior.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Versioned key lookup, rollout order, revoke deadline và rollback.<br>
**Answer outline:** Receiver hỗ trợ current+next key IDs; distribute next secret securely; sender chuyển sang next; observe success; revoke old sau overlap deadline. Không thử mọi key mù quáng và không chấp nhận old key vô hạn.<br>
**Required trade-offs:** Overlap giúp availability/rollback nhưng tăng attack surface; zero-overlap dễ gây outage.<br>
**Follow-up ladder:** Key compromised? Multiple senders? Secret manager outage? Audit metadata?<br>
**Red flags:** Thay secret đồng thời hai bên và “restart thật nhanh”.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-05 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-WEBHOOK-014 — `ARCHITECT`
**Question:** Thiết kế idempotency store cho webhook multi-instance/multi-region theo consistency, retention và failover nào?<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `LOW` — distributed-systems stretch, không phải first-pass question.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Global uniqueness, partition policy và bounded dedup horizon.<br>
**Answer outline:** Chọn authoritative region/store hoặc globally consistent key claim theo requirement; namespace provider/tenant; retention dài hơn retry/replay horizon; payload fingerprint phát hiện ID collision; reconciliation khi partition/failover.<br>
**Required trade-offs:** Synchronous global uniqueness tăng latency/giảm availability; regional acceptance cần conflict/convergence policy.<br>
**Follow-up ladder:** Region split brain? GDPR retention? Hot provider? Disaster recovery?<br>
**Red flags:** Local-memory dedup hoặc Redis TTL được coi là exactly-once toàn cầu.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-05 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-WEBHOOK-015 — `ARCHITECT`
**Question:** Nên verify webhook ở API gateway, Spring application hay cả hai?<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `MEDIUM` — platform/security architecture follow-up.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Trust boundary, raw-body preservation, key ownership và defense in depth.<br>
**Answer outline:** Edge có thể rate-limit/IP/TLS/schema-size gate; application thường sở hữu provider-specific signed bytes, key ID, replay/idempotency và business transition. Nếu gateway verify, phải bảo vệ identity assertion tới app và không làm đổi signed body.<br>
**Required trade-offs:** Central gateway giảm duplication nhưng tăng blast radius/coupling; app-level rõ domain hơn nhưng policy dễ phân tán.<br>
**Follow-up ladder:** Service mesh/mTLS? Internal bypass path? Multiple providers? Body transformation?<br>
**Red flags:** Gateway verify signature rồi app tin mọi request nội bộ không có authenticated hop.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-05 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-WEBHOOK-016 — `EXPERT`
**Question:** Webhook secret bị lộ và attacker đã replay event hợp lệ. Thiết kế containment, forensic và recovery mà không phá business state.<br>
**Target depth:** `D4`<br>
**Interview likelihood:** `LOW` — incident-response discriminator.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Compromise timeline, revocation, event provenance và reversible reconciliation.<br>
**Answer outline:** Disable/revoke compromised key hoặc isolate sender; preserve/redact evidence; identify key ID/event IDs/time/resource transitions; compare provider source; quarantine suspicious events; reconcile DB/cache/downstream side effects; rotate, close replay gap, add detections và postmortem.<br>
**Required trade-offs:** Immediate shutdown reduces abuse but can lose legitimate events; bounded buffering/reconciliation preserves service with operational cost.<br>
**Follow-up ladder:** Không có event ID lịch sử? Provider logs unavailable? Compensation unsafe? Customer impact?<br>
**Red flags:** Chỉ rotate secret hoặc chỉ xóa duplicate rows mà không điều tra side effects.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-05 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `SEC-05` active: tạo core/deep-dive, khóa exact signing contract, nối actual controller/body/filter/transaction path, tạo deterministic time/concurrency/replay tests và thay marker bằng evidence thật. Stream-key disclosure thuộc `SEC-03`; generic outbox/inbox thuộc `EVT-01`; multi-region delivery thuộc distributed-systems stages. Stable IDs không tái sử dụng.

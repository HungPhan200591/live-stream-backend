# Redis Interview Question Bank — Atomic Data Structures, Rate Limiting and Leaderboards

> Status: `DRAFT`<br>
> Domain owner: `Redis`<br>
> Active slice: `NONE`; preview target: `RED-01`<br>
> Related roadmap: [Stage 4](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-4---redis-as-a-distributed-data-structure)<br>
> Related depth rubric: [Redis](../../../knowledge-depth-rubric.md#315-redis--p1-target-d3)<br>
> Related theory: [Core theory](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md)<br>
> Updated: `2026-07-26`

Preview only; không active/implement `RED-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `RED-DS-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### RED-DS-001 — `FOUNDATION`
**Question:** String, Hash, Set, Sorted Set, List và Stream phù hợp access pattern nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Data structure semantics thay vì dùng Redis như map chung.<br>
**Answer outline:** String cho scalar/blob/counter; Hash fields; Set uniqueness; ZSET ordered score; List queue đơn giản; Stream append log/consumer groups. Chọn theo operations/cardinality/TTL.<br>
**Required trade-offs:** Structure chuyên biệt nhanh nhưng migration/memory model khác.<br>
**Follow-up ladder:** Bitmap/HLL? Per-field TTL?<br>
**Red flags:** Mọi value nên serialize JSON String.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-002 — `FOUNDATION`
**Question:** Lệnh Redis atomic ở phạm vi nào; pipeline và transaction khác Lua thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Single-command atomicity, batching và server-side composition.<br>
**Answer outline:** Một command atomic trên event loop; pipeline giảm RTT không atomic; MULTI/EXEC nhóm commands nhưng không rollback như DB; Lua/function chạy atomic nhưng block server nếu lâu.<br>
**Required trade-offs:** Server-side atomicity giảm race nhưng tăng latency blast radius.<br>
**Follow-up ladder:** WATCH optimistic transaction?<br>
**Red flags:** MULTI rollback từng command khi lỗi.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-003 — `FOUNDATION`
**Question:** Token bucket, fixed window và sliding window rate limit khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Burst, fairness, memory và atomic update.<br>
**Answer outline:** Fixed window đơn giản nhưng boundary burst; sliding log chính xác tốn memory; sliding counter xấp xỉ; token bucket cho burst có refill rate. Key scope và atomic script bắt buộc.<br>
**Required trade-offs:** Fairness/precision vs cost.<br>
**Follow-up ladder:** Leaky bucket? 429 headers?<br>
**Red flags:** INCR rồi EXPIRE hai lệnh luôn race-free.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-004 — `FOUNDATION`
**Question:** Sorted Set xây leaderboard và range/rank như thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Score ordering, tie và update semantics.<br>
**Answer outline:** ZADD member-score, ZREVRANGE/RANK; member unique và score update; tie order cần contract/tie-breaker ngoài score hoặc composite encoding cẩn thận.<br>
**Required trade-offs:** Real-time rank nhanh nhưng source/rebuild/audit cần rõ.<br>
**Follow-up ladder:** Top N per period? Floating score?<br>
**Red flags:** ZSET là durable ledger.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-005 — `SENIOR`
**Question:** Viết atomic Lua rate limiter cần bảo vệ các edge case nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Key time, TTL, numeric bounds và cluster slots.<br>
**Answer outline:** Validate input, dùng Redis/server time nếu cần, update+expiry atomically, return remaining/reset; cap keys/cardinality, script timeout và hash-tag keys cho multi-key cluster.<br>
**Required trade-offs:** Lua chính xác nhưng long script chặn shard và khó debug.<br>
**Follow-up ladder:** EVALSHA/functions? Clock skew?<br>
**Red flags:** Lua có thể gọi network/DB bên trong.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-006 — `SENIOR`
**Question:** Đếm unique/current viewers bằng HLL và ZSET heartbeat khác nhau thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Approximate cardinality vs time-aware membership.<br>
**Answer outline:** HLL memory nhỏ cho approximate uniques, không liệt kê members; ZSET score=lastSeen cho active window, cần cleanup and hot-key capacity. Durable business count vẫn ở owner phù hợp.<br>
**Required trade-offs:** Accuracy/listability vs memory/write rate.<br>
**Follow-up ladder:** PFCOUNT error? Shard/merge?<br>
**Red flags:** HLL trả danh sách user unique.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-007 — `SENIOR`
**Question:** Hot key trên leaderboard/viewer set gây vấn đề gì và giảm tải ra sao?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Single-shard CPU/network, large key và fan-out.<br>
**Answer outline:** Đo per-key ops/bytes/latency; cache local/read replicas chỉ cho safe reads, shard/bucket với merge trade-off, batch updates và cap result; tránh big delete blocking.<br>
**Required trade-offs:** Sharding tăng throughput nhưng mất atomic total/rank đơn giản.<br>
**Follow-up ladder:** UNLINK/SCAN? Celebrity key?<br>
**Red flags:** Thêm node tự shard một key.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-008 — `SENIOR`
**Question:** Version/serialization migration cho Redis structures khi rolling deploy thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Old/new reader compatibility và rebuild.<br>
**Answer outline:** Version namespace/payload, dual-read có deadline hoặc rebuild từ PostgreSQL/event log, TTL old keys, metrics fallback; không mutate encoding in-place không plan.<br>
**Required trade-offs:** Cold rebuild/load vs dual-format complexity.<br>
**Follow-up ladder:** Cluster key tags? Serializer allowlist?<br>
**Red flags:** FLUSHALL rồi deploy là migration.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-009 — `ARCHITECT`
**Question:** Thiết kế Redis cluster topology và data ownership theo failure domain thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Shard/replica, consistency và degraded mode.<br>
**Answer outline:** Phân loại cache, ephemeral coordination và rate state; key distribution/cardinality, replica/failover semantics, persistence/RPO nếu cần; mỗi feature có source/rebuild/outage policy.<br>
**Required trade-offs:** Availability/latency vs consistency/ops cost.<br>
**Follow-up ladder:** Cluster vs Sentinel? Multi-region?<br>
**Red flags:** Một Redis chung cho cache, locks và security state không isolation.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RED-DS-010 — `EXPERT`
**Question:** Failover làm lock/rate-limit state mất hoặc duplicate; bảo vệ invariant thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Async replication window và fencing/business safety.<br>
**Answer outline:** Primary ACK trước replica có thể mất recent write khi promote; rate limit có fail-open/closed policy, critical lock cần DB invariant/fencing token từ durable monotonic owner; inject failover và quantify window.<br>
**Required trade-offs:** Strong safety giảm availability/latency.<br>
**Follow-up ladder:** WAIT command? Split brain?<br>
**Red flags:** Redis replication tạo linearizability.<br>
**Evidence:** Theory [Core](../theory/core/atomic-data-structures-rate-limiting-and-leaderboards.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `RED-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.

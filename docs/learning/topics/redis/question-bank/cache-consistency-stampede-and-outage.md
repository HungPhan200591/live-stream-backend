# Redis Interview Question Bank — Cache Consistency, Stampede and Outage

> Status: `DRAFT`  
> Domain owner: `Redis`  
> Active slice: `NONE`; preview target: `RED-01`  
> Related roadmap: [Stage 4](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-4---redis-as-a-distributed-data-structure)  
> Related depth rubric: [Redis](../../../knowledge-depth-rubric.md#315-redis--p1-target-d3)  
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/redis/theory/core/cache-consistency-stampede-and-outage.md`  
> Updated: `2026-07-26`

Preview only; không active/implement `RED-01`. PostgreSQL là source of truth. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `REDIS-CACHE-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### REDIS-CACHE-001 — `FOUNDATION`
**Question:** Cache-aside, read-through và write-through/write-behind khác nhau thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Read/write ownership và failure windows.  
**Answer outline:** Cache-aside app load on miss/invalidate on write; read-through cache loader owns read; write-through sync cache+store abstraction; write-behind async flush rủi ro data loss/order. Source of truth phải explicit.  
**Required trade-offs:** Simplicity/control vs coupling/consistency.  
**Follow-up ladder:** Refresh-ahead?  
**Red flags:** Write-behind phù hợp mọi business mutation.  
**Evidence:** Theory `NOT CREATED`; case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-002 — `FOUNDATION`
**Question:** TTL dùng để làm gì và vì sao không phải consistency strategy hoàn chỉnh?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Bounded staleness, eviction và refresh.  
**Answer outline:** TTL giới hạn lifetime/stale horizon và memory, nhưng dữ liệu vẫn stale tới expiry, expiry đồng loạt gây stampede; cần invalidation/versioning/fallback. TTL có jitter và được chọn theo business SLO.  
**Required trade-offs:** TTL ngắn fresh hơn nhưng tăng DB load/miss.  
**Follow-up ladder:** Sliding TTL? No expiry?  
**Red flags:** TTL 5 phút bảo đảm eventual consistency trong 5 phút mọi failure.  
**Evidence:** Theory `NOT CREATED`; project Redis guide `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-003 — `FOUNDATION`
**Question:** Cache penetration, breakdown/hot-key và stampede khác nhau thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Invalid-key misses, hot expiry và synchronized misses.  
**Answer outline:** Penetration là repeated absent keys xuống DB; hot-key breakdown là key nóng mất/expire; stampede là nhiều concurrent misses/rebuild. Dùng negative cache/bounds, jitter, single-flight/lock, stale-while-revalidate tùy semantics.  
**Required trade-offs:** Negative cache có stale-not-found; lock tăng wait/failure mode.  
**Follow-up ladder:** Bloom filter? Hot key sharding?  
**Red flags:** Tăng Redis memory chữa stampede.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-004 — `FOUNDATION`
**Question:** Cache key và cached DTO nên được version hóa vì sao?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Namespace, serializer và deployment compatibility.  
**Answer outline:** Key gồm namespace/version/stable identity; typed DTO độc lập entity; version đổi schema/semantics để old/new deploy không deserialize/misread; TTL/invalidation explicit.  
**Required trade-offs:** Bump version tạo cold cache/memory overlap.  
**Follow-up ladder:** Serializer migration? Multi-tenant key?  
**Red flags:** Cache JPA entity bằng default serializer.  
**Evidence:** Theory `NOT CREATED`; project Redis guide `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-005 — `SENIOR`
**Question:** Race “DB update → delete cache” vẫn có thể repopulate stale value thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Concurrent miss/read timing và after-commit invalidation.  
**Answer outline:** Reader miss đọc old DB trước writer commit, writer commit+delete, reader sau đó set old value. Mitigate versioned values, delayed second delete có giới hạn, single-flight/fencing hoặc event invalidation; TTL bounds residual.  
**Required trade-offs:** Stronger coordination tăng latency/complexity.  
**Follow-up ladder:** Update cache vs delete?  
**Red flags:** Delete after commit loại mọi race.  
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-006 — `SENIOR`
**Question:** Redis outage nên fallback PostgreSQL ra sao để không làm sập DB?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Fail-open/read fallback với bulkhead/shedding.  
**Answer outline:** Timeout ngắn, circuit breaker, bounded DB fallback concurrency/rate, cache optional reads fallback có telemetry; mutation/session/security semantics không được fail-open mù quáng. Có degradation policy và recovery warmup.  
**Required trade-offs:** Availability read vs DB overload/stale/security correctness.  
**Follow-up ladder:** Local cache? Cold-start storm?  
**Red flags:** Catch Redis exception rồi mọi request query DB không giới hạn.  
**Evidence:** Theory `NOT CREATED`; fault test `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-007 — `SENIOR`
**Question:** Distributed lock bằng Redis cần token, TTL và fencing như thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Ownership-safe release và expired holder.  
**Answer outline:** Acquire atomic with unique token+TTL, release compare-and-delete; TTL expiry cho liveness nhưng old holder có thể tiếp tục, nên downstream cần fencing token/version nếu correctness critical.  
**Required trade-offs:** Lock convenience vs partitions/clock/lease complexity; DB constraint thường safety net tốt hơn.  
**Follow-up ladder:** Lease renewal? Redlock debate?  
**Red flags:** DEL key khi xong bất kể owner.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-008 — `SENIOR`
**Question:** Đo cache effectiveness bằng metrics nào ngoài hit ratio?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`  
**Interviewer evaluates:** Latency, origin load, staleness và memory/eviction.  
**Answer outline:** Hit/miss theo cache/operation bounded labels; latency, DB avoided/QPS, loader concurrency, evictions, memory, errors/timeouts, stale age/invalidation lag và hot keys. Hit cao vẫn vô ích nếu cache non-critical cheap query.  
**Required trade-offs:** Fine-grained key metrics dễ cardinality leak.  
**Follow-up ladder:** Cost-adjusted hit ratio?  
**Red flags:** 99% hit rate tự chứng minh cache tốt.  
**Evidence:** Telemetry `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-009 — `ARCHITECT`
**Question:** Thiết kế multi-region caching với invalidation và read-your-writes thế nào?  
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`  
**Interviewer evaluates:** Regional ownership, lag và consistency token.  
**Answer outline:** Region-local cache, versioned source events/invalidation, TTL bound; write response mang version để session route/read bypass until observed; define failover/rebuild and conflict ownership.  
**Required trade-offs:** Local latency/availability vs global freshness/coordination.  
**Follow-up ladder:** Active-active writes? Event reorder?  
**Red flags:** Global Redis cluster tự giải quyết data sovereignty/latency.  
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-010 — `EXPERT`
**Question:** Cache flush/large expiry wave gây DB collapse; điều hành recovery thế nào?  
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`  
**Interviewer evaluates:** Stampede feedback loop và controlled warming.  
**Answer outline:** Shed/bound cache loaders, prioritize hot keys, serve bounded stale nếu safe, ramp warmup với jitter; monitor DB/pool/cache. Sau incident thêm TTL jitter, single-flight, prewarm plan và capacity test; không full warm đồng loạt.  
**Required trade-offs:** Stale response vs origin survival.  
**Follow-up ladder:** Which keys first? Recovery SLO?  
**Red flags:** Tăng request retry để cache nóng nhanh.  
**Evidence:** Incident drill `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `RED-01` active, fault-inject expiry/outage và lưu latency/load evidence; không đổi/reuse stable IDs.

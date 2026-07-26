# Ngân hàng câu hỏi phỏng vấn Redis — tính nhất quán cache, cache stampede và outage

> Status: `DRAFT`<br>
> Domain owner: `Redis`<br>
> Active slice: `NONE`; preview target: `RED-01`<br>
> Related roadmap: [Stage 4](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-4---redis-as-a-distributed-data-structure)<br>
> Related depth rubric: [Redis](../../../knowledge-depth-rubric.md#315-redis--p1-target-d3)<br>
> Related theory: [Core theory](../theory/core/cache-consistency-stampede-and-outage.md) · [Deep-dive](../theory/deep-dives/cache-versioning-stampede-and-owner-recovery.md)<br>
> Updated: `2026-07-26`

Bản xem trước; không kích hoạt hoặc triển khai `RED-01`. PostgreSQL là nguồn sự thật. Mọi câu vẫn `UNANSWERED`, kiểm thử `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `REDIS-CACHE-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### REDIS-CACHE-001 — `FOUNDATION`
**Question:** Cache-aside, read-through và write-through/write-behind khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Owner của luồng đọc/ghi và các cửa sổ có thể lỗi.<br>
**Answer outline:** Cache-aside để application load khi miss và invalidate khi ghi; read-through giao luồng đọc cho cache loader; write-through đồng bộ cache với store; write-behind flush bất đồng bộ nên có rủi ro mất dữ liệu/sai thứ tự. Nguồn sự thật phải tường minh.<br>
**Required trade-offs:** Đơn giản và quyền kiểm soát đổi lấy coupling và consistency.<br>
**Follow-up ladder:** Refresh-ahead?<br>
**Red flags:** Cho rằng write-behind phù hợp mọi mutation nghiệp vụ.<br>
**Evidence:** Theory [Core](../theory/core/cache-consistency-stampede-and-outage.md) + [Deep-dive](../theory/deep-dives/cache-versioning-stampede-and-owner-recovery.md); case `RED-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-002 — `FOUNDATION`
**Question:** TTL dùng để làm gì và vì sao không phải consistency strategy hoàn chỉnh?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Staleness có giới hạn, eviction và refresh.<br>
**Answer outline:** TTL giới hạn thời gian sống, staleness và memory nhưng dữ liệu vẫn có thể cũ tới lúc hết hạn; hết hạn đồng loạt gây stampede. Cần invalidation, versioning và fallback; TTL có jitter và bám business SLO.<br>
**Required trade-offs:** TTL ngắn cho dữ liệu mới hơn nhưng tăng miss và tải database.<br>
**Follow-up ladder:** Sliding TTL? No expiry?<br>
**Red flags:** TTL 5 phút bảo đảm eventual consistency trong 5 phút mọi failure.<br>
**Evidence:** Theory [Core](../theory/core/cache-consistency-stampede-and-outage.md) + [Deep-dive](../theory/deep-dives/cache-versioning-stampede-and-owner-recovery.md); project Redis guide `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-003 — `FOUNDATION`
**Question:** Cache penetration, breakdown/hot-key và stampede khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Miss do key không tồn tại, hot key hết hạn và miss đồng loạt.<br>
**Answer outline:** Penetration là key không tồn tại lặp lại xuyên xuống DB; breakdown là hot key bị mất/hết hạn; stampede là nhiều miss cùng rebuild. Dùng negative cache có giới hạn, jitter, single-flight/lock và stale-while-revalidate theo semantics.<br>
**Required trade-offs:** Negative cache có thể che object mới; lock làm tăng chờ và kiểu lỗi mới.<br>
**Follow-up ladder:** Bloom filter? Hot key sharding?<br>
**Red flags:** Tăng Redis memory chữa stampede.<br>
**Evidence:** Theory [Core](../theory/core/cache-consistency-stampede-and-outage.md) + [Deep-dive](../theory/deep-dives/cache-versioning-stampede-and-owner-recovery.md); experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-004 — `FOUNDATION`
**Question:** Cache key và cached DTO nên được version hóa vì sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Namespace, serializer và tương thích khi deploy nhiều version.<br>
**Answer outline:** Key gồm namespace, version và identity ổn định; DTO có kiểu tách khỏi entity. Đổi version khi schema/semantics đổi để instance cũ/mới không đọc sai; TTL và invalidation phải rõ.<br>
**Required trade-offs:** Tăng version tạo cold cache và hai namespace cùng chiếm memory.<br>
**Follow-up ladder:** Serializer migration? Multi-tenant key?<br>
**Red flags:** Cache JPA entity bằng default serializer.<br>
**Evidence:** Theory [Core](../theory/core/cache-consistency-stampede-and-outage.md) + [Deep-dive](../theory/deep-dives/cache-versioning-stampede-and-owner-recovery.md); project Redis guide `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-005 — `SENIOR`
**Question:** Race “DB update → delete cache” vẫn có thể repopulate stale value thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Timing của miss/read đồng thời và invalidation sau commit.<br>
**Answer outline:** Reader miss và đọc DB cũ trước writer commit; writer commit rồi xóa cache; reader tới muộn lại set value cũ. Giảm lỗi bằng value có version, lần xóa thứ hai hữu hạn, single-flight/fencing hoặc event invalidation; TTL chỉ giới hạn phần rủi ro còn lại.<br>
**Required trade-offs:** Phối hợp mạnh hơn làm tăng latency và độ phức tạp.<br>
**Follow-up ladder:** Update cache vs delete?<br>
**Red flags:** Delete after commit loại mọi race.<br>
**Evidence:** Theory [Core](../theory/core/cache-consistency-stampede-and-outage.md) + [Deep-dive](../theory/deep-dives/cache-versioning-stampede-and-owner-recovery.md); reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-006 — `SENIOR`
**Question:** Redis outage nên fallback PostgreSQL ra sao để không làm sập DB?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Fail-open và fallback đọc có bulkhead/load shedding.<br>
**Answer outline:** Timeout ngắn, circuit breaker và giới hạn concurrency/rate fallback DB. Đường đọc cache tùy chọn có thể fallback kèm telemetry; mutation/session/security không fail-open mù. Cần policy hạ cấp và warmup khi phục hồi.<br>
**Required trade-offs:** Availability của luồng đọc đổi lấy nguy cơ quá tải DB, dữ liệu stale và sai security.<br>
**Follow-up ladder:** Local cache? Cold-start storm?<br>
**Red flags:** Catch Redis exception rồi mọi request query DB không giới hạn.<br>
**Evidence:** Theory [Core](../theory/core/cache-consistency-stampede-and-outage.md) + [Deep-dive](../theory/deep-dives/cache-versioning-stampede-and-owner-recovery.md); fault test `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-007 — `SENIOR`
**Question:** Distributed lock bằng Redis cần token, TTL và fencing như thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Release lock đúng owner và holder đã hết lease.<br>
**Answer outline:** Acquire nguyên tử bằng token duy nhất cùng TTL; release bằng compare-and-delete. TTL bảo đảm liveness nhưng holder cũ vẫn có thể chạy, nên downstream cần fencing token/version nếu correctness quan trọng.<br>
**Required trade-offs:** Sự tiện lợi của lock đổi lấy phức tạp do partition, clock và lease; DB constraint thường là safety net tốt hơn.<br>
**Follow-up ladder:** Lease renewal? Redlock debate?<br>
**Red flags:** DEL key khi xong bất kể owner.<br>
**Evidence:** Theory [Core](../theory/core/cache-consistency-stampede-and-outage.md) + [Deep-dive](../theory/deep-dives/cache-versioning-stampede-and-owner-recovery.md); experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-008 — `SENIOR`
**Question:** Đo cache effectiveness bằng metrics nào ngoài hit ratio?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Latency, tải nguồn, staleness, memory và eviction.<br>
**Answer outline:** Đo hit/miss theo cache và operation với label hữu hạn; latency, QPS DB tránh được, loader concurrency, eviction, memory, error/timeout, tuổi dữ liệu, invalidation lag và hot key. Hit cao vẫn vô ích nếu chỉ cache query rẻ không quan trọng.<br>
**Required trade-offs:** Metric quá chi tiết theo key dễ nổ cardinality và lộ ID.<br>
**Follow-up ladder:** Cost-adjusted hit ratio?<br>
**Red flags:** 99% hit rate tự chứng minh cache tốt.<br>
**Evidence:** Theory [Core](../theory/core/cache-consistency-stampede-and-outage.md) + [Deep-dive](../theory/deep-dives/cache-versioning-stampede-and-owner-recovery.md); telemetry `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-009 — `ARCHITECT`
**Question:** Thiết kế multi-region caching với invalidation và read-your-writes thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Owner theo region, lag và consistency token.<br>
**Answer outline:** Cache theo region, event/invalidation có version và TTL hữu hạn; response ghi mang version để session route hoặc bỏ qua cache tới khi đã quan sát; định nghĩa failover, rebuild và owner khi conflict.<br>
**Required trade-offs:** Latency/availability cục bộ đổi lấy freshness và coordination toàn cục.<br>
**Follow-up ladder:** Active-active writes? Event reorder?<br>
**Red flags:** Global Redis cluster tự giải quyết data sovereignty/latency.<br>
**Evidence:** Theory [Core](../theory/core/cache-consistency-stampede-and-outage.md) + [Deep-dive](../theory/deep-dives/cache-versioning-stampede-and-owner-recovery.md); design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REDIS-CACHE-010 — `EXPERT`
**Question:** Cache flush/large expiry wave gây DB collapse; điều hành recovery thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Vòng phản hồi của stampede và warm cache có kiểm soát.<br>
**Answer outline:** Giới hạn hoặc shed loader, ưu tiên hot key, phục vụ stale hữu hạn nếu an toàn, tăng warmup dần với jitter và theo dõi DB/pool/cache. Sau incident thêm TTL jitter, single-flight, kế hoạch prewarm và capacity test; không warm toàn bộ cùng lúc.<br>
**Required trade-offs:** Response stale đổi lấy việc giữ nguồn dữ liệu sống.<br>
**Follow-up ladder:** Warm key nào trước? Recovery SLO là gì?<br>
**Red flags:** Tăng request retry để cache nóng nhanh.<br>
**Evidence:** Theory [Core](../theory/core/cache-consistency-stampede-and-outage.md) + [Deep-dive](../theory/deep-dives/cache-versioning-stampede-and-owner-recovery.md); incident drill `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `RED-01` active, fault-inject expiry/outage và lưu latency/load evidence; không đổi/reuse stable IDs.

# Database Interview Question Bank — JPA N+1, Pagination and Query Plans

> Status: `DRAFT`<br>
> Domain owner: `JPA/PostgreSQL`<br>
> Active slice: `NONE`; preview target: `DB-01`<br>
> Related roadmap: [Stage 3](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-3---postgresql-model-index-và-query-engineering)<br>
> Related depth rubric: [PostgreSQL/JPA](../../../knowledge-depth-rubric.md#39-postgresql-sql-và-data-modeling--p0-target-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/database/theory/core/jpa-n-plus-one-pagination-and-query-plans.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `DB-01`. Project vẫn dùng explicit relation IDs; không đề xuất thêm JPA association. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `JPA-QUERY-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### JPA-QUERY-001 — `FOUNDATION`
**Question:** Persistence context, managed/detached entity và dirty checking là gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Unit of work và flush timing.<br>
**Answer outline:** Context giữ identity map/managed snapshots; thay managed entity được detect và flush thành SQL; detached không tự persist. Flush không đồng nghĩa commit và có thể xảy ra trước query/commit.<br>
**Required trade-offs:** Unit of work tiện nhưng SQL/timing ẩn.<br>
**Follow-up ladder:** Clear/detach? Flush mode?<br>
**Red flags:** `save()` luôn lập tức commit.<br>
**Evidence:** Theory `NOT CREATED`; case `DB-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-002 — `FOUNDATION`
**Question:** N+1 query là gì và nhận diện bằng cách nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** One list query plus per-row lookup.<br>
**Answer outline:** Load N parents rồi truy cập dữ liệu liên quan gây N query thêm; nhận diện qua SQL count/APM/query metrics/test budget, không chỉ latency dev data nhỏ. Explicit IDs vẫn có N+1 nếu loop repository lookup.<br>
**Required trade-offs:** Batch/fetch/projection giảm round trips nhưng có overfetch/cartesian risk.<br>
**Follow-up ladder:** EntityGraph? Batch size?<br>
**Red flags:** Không dùng association thì không thể N+1.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-003 — `FOUNDATION`
**Question:** Lazy và eager loading khác nhau; vì sao đổi sang EAGER không phải cách chữa N+1 chung?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Fetch timing vs query shape.<br>
**Answer outline:** Lazy trì hoãn access, eager yêu cầu loaded nhưng provider có thể vẫn phát nhiều queries; eager gây overfetch/cycle. Chọn query projection/fetch plan theo use case.<br>
**Required trade-offs:** Convenience graph traversal vs predictable SQL/DTO boundary.<br>
**Follow-up ladder:** LazyInitializationException? OSIV?<br>
**Red flags:** EAGER luôn một join.<br>
**Evidence:** Theory `NOT CREATED`; case `DB-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-004 — `FOUNDATION`
**Question:** Entity query, DTO projection và native SQL nên chọn khi nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Mutation vs read shape và portability.<br>
**Answer outline:** Entity cho transactional domain state; DTO projection cho read model/select ít cột; native SQL cho PostgreSQL feature/complex query sau khi có evidence. Không trả entity trực tiếp qua API.<br>
**Required trade-offs:** Native tối ưu/control cao nhưng mapping/portability/test burden.<br>
**Follow-up ladder:** Interface projection? Tuple?<br>
**Red flags:** Mọi query phải dùng entity để “đúng JPA”.<br>
**Evidence:** Theory `NOT CREATED`; project convention `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-005 — `SENIOR`
**Question:** Vì sao fetch join collection với pagination thường sai/nguy hiểm?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Row multiplication, in-memory pagination và count.<br>
**Answer outline:** One-to-many join nhân parent rows; SQL limit áp rows chứ không phải unique parents hoặc provider paginate memory. Dùng two-step page IDs rồi fetch/projection, batch query, hoặc read model.<br>
**Required trade-offs:** Hai query thêm round trip nhưng semantics/size predictable.<br>
**Follow-up ladder:** Count query? Multiple bags?<br>
**Red flags:** Thêm DISTINCT luôn sửa pagination.<br>
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-006 — `SENIOR`
**Question:** Tối ưu query từ repository đến PostgreSQL plan theo workflow nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `DIAGNOSTIC`<br>
**Interviewer evaluates:** Generated SQL, binds, cardinality và plan evidence.<br>
**Answer outline:** Capture exact SQL/params và query count; reproduce representative data; `EXPLAIN ANALYZE BUFFERS`; sửa shape/projection/index/stats; đo lại latency/rows/buffers và regression test.<br>
**Required trade-offs:** Index read win đổi write/storage cost.<br>
**Follow-up ladder:** Prepared generic plan? Hibernate statistics?<br>
**Red flags:** Tune JPQL mà không xem SQL thực.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-007 — `SENIOR`
**Question:** Bulk update/delete lệch persistence context thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Bypass managed state và stale entities/cache.<br>
**Answer outline:** JPQL/native bulk DML chạy trực tiếp DB, không cập nhật object đã managed; flush trước và clear/refresh có chủ đích, invalidate cache liên quan; kiểm tra affected rows.<br>
**Required trade-offs:** Bulk nhanh nhưng bỏ lifecycle callbacks/dirty checking.<br>
**Follow-up ladder:** `clearAutomatically`? Version increment?<br>
**Red flags:** Context tự đồng bộ hai chiều với DB.<br>
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-008 — `SENIOR`
**Question:** Thiết kế repository cho explicit foreign-key IDs mà vẫn tránh query chắp vá thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Query ownership, joins/projections và service orchestration.<br>
**Answer outline:** Entity giữ IDs theo convention; repository cung cấp use-case projection/join query hoặc batched `IN`, không loop lookup; service giữ transaction/business rule; query tests assert result và count/plan khi critical.<br>
**Required trade-offs:** Query-specific repository methods tăng surface nhưng performance rõ.<br>
**Follow-up ladder:** QueryDSL/specification? Module boundary?<br>
**Red flags:** Thêm `@ManyToOne` trái convention chỉ để fetch tiện.<br>
**Evidence:** Project convention `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-009 — `ARCHITECT`
**Question:** Tách write model JPA và read model tối ưu query khi nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Complexity threshold, freshness và ownership.<br>
**Answer outline:** Khi aggregate write model gây overfetch/joins phức tạp và read SLO khác; bắt đầu bằng projection cùng DB, chỉ materialize/event-driven khi evidence. Define lag/rebuild/reconciliation/schema contracts.<br>
**Required trade-offs:** Read scale/clarity vs duplication/eventual consistency/ops.<br>
**Follow-up ladder:** CQRS có cần service riêng?<br>
**Red flags:** Dùng CQRS từ đầu cho mọi CRUD.<br>
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-010 — `EXPERT`
**Question:** Query regression chỉ xảy ra sau traffic/data growth: phân biệt ORM, plan, lock và pool bottleneck thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Cross-layer evidence correlation.<br>
**Answer outline:** Timeline APM/query count/pool waits/DB locks/plan/buffers/GC; nếu queries/request tăng là ORM access; same SQL plan/rows đổi là data/stats; acquire wait có thể downstream DB saturation; test từng hypothesis và preserve samples.<br>
**Required trade-offs:** Sampling nhẹ production vs diagnostic fidelity.<br>
**Follow-up ladder:** Plan cache? Lock convoy?<br>
**Red flags:** Bật EAGER hoặc tăng pool trước khi đo.<br>
**Evidence:** Incident drill `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `DB-01` active, gắn repository thật và lưu SQL/plan/query-count evidence; không đổi/reuse stable IDs.

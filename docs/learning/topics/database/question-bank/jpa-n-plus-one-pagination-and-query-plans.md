# Database Interview Question Bank — JPA N+1, Pagination and Query Plans

> Status: `DRAFT`  
> Domain owner: `JPA/PostgreSQL`  
> Active slice: `NONE`; preview target: `DB-01`  
> Related roadmap: [Stage 3](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-3---postgresql-model-index-và-query-engineering)  
> Related depth rubric: [PostgreSQL/JPA](../../../knowledge-depth-rubric.md#39-postgresql-sql-và-data-modeling--p0-target-d3)  
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/database/theory/core/jpa-n-plus-one-pagination-and-query-plans.md`  
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
**Question:** Persistence context, managed/detached entity và dirty checking là gì?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Unit of work và flush timing.  
**Answer outline:** Context giữ identity map/managed snapshots; thay managed entity được detect và flush thành SQL; detached không tự persist. Flush không đồng nghĩa commit và có thể xảy ra trước query/commit.  
**Required trade-offs:** Unit of work tiện nhưng SQL/timing ẩn.  
**Follow-up ladder:** Clear/detach? Flush mode?  
**Red flags:** `save()` luôn lập tức commit.  
**Evidence:** Theory `NOT CREATED`; case `DB-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-002 — `FOUNDATION`
**Question:** N+1 query là gì và nhận diện bằng cách nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** One list query plus per-row lookup.  
**Answer outline:** Load N parents rồi truy cập dữ liệu liên quan gây N query thêm; nhận diện qua SQL count/APM/query metrics/test budget, không chỉ latency dev data nhỏ. Explicit IDs vẫn có N+1 nếu loop repository lookup.  
**Required trade-offs:** Batch/fetch/projection giảm round trips nhưng có overfetch/cartesian risk.  
**Follow-up ladder:** EntityGraph? Batch size?  
**Red flags:** Không dùng association thì không thể N+1.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-003 — `FOUNDATION`
**Question:** Lazy và eager loading khác nhau; vì sao đổi sang EAGER không phải cách chữa N+1 chung?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Fetch timing vs query shape.  
**Answer outline:** Lazy trì hoãn access, eager yêu cầu loaded nhưng provider có thể vẫn phát nhiều queries; eager gây overfetch/cycle. Chọn query projection/fetch plan theo use case.  
**Required trade-offs:** Convenience graph traversal vs predictable SQL/DTO boundary.  
**Follow-up ladder:** LazyInitializationException? OSIV?  
**Red flags:** EAGER luôn một join.  
**Evidence:** Theory `NOT CREATED`; case `DB-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-004 — `FOUNDATION`
**Question:** Entity query, DTO projection và native SQL nên chọn khi nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Mutation vs read shape và portability.  
**Answer outline:** Entity cho transactional domain state; DTO projection cho read model/select ít cột; native SQL cho PostgreSQL feature/complex query sau khi có evidence. Không trả entity trực tiếp qua API.  
**Required trade-offs:** Native tối ưu/control cao nhưng mapping/portability/test burden.  
**Follow-up ladder:** Interface projection? Tuple?  
**Red flags:** Mọi query phải dùng entity để “đúng JPA”.  
**Evidence:** Theory `NOT CREATED`; project convention `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-005 — `SENIOR`
**Question:** Vì sao fetch join collection với pagination thường sai/nguy hiểm?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Row multiplication, in-memory pagination và count.  
**Answer outline:** One-to-many join nhân parent rows; SQL limit áp rows chứ không phải unique parents hoặc provider paginate memory. Dùng two-step page IDs rồi fetch/projection, batch query, hoặc read model.  
**Required trade-offs:** Hai query thêm round trip nhưng semantics/size predictable.  
**Follow-up ladder:** Count query? Multiple bags?  
**Red flags:** Thêm DISTINCT luôn sửa pagination.  
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-006 — `SENIOR`
**Question:** Tối ưu query từ repository đến PostgreSQL plan theo workflow nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `DIAGNOSTIC`  
**Interviewer evaluates:** Generated SQL, binds, cardinality và plan evidence.  
**Answer outline:** Capture exact SQL/params và query count; reproduce representative data; `EXPLAIN ANALYZE BUFFERS`; sửa shape/projection/index/stats; đo lại latency/rows/buffers và regression test.  
**Required trade-offs:** Index read win đổi write/storage cost.  
**Follow-up ladder:** Prepared generic plan? Hibernate statistics?  
**Red flags:** Tune JPQL mà không xem SQL thực.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-007 — `SENIOR`
**Question:** Bulk update/delete lệch persistence context thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Bypass managed state và stale entities/cache.  
**Answer outline:** JPQL/native bulk DML chạy trực tiếp DB, không cập nhật object đã managed; flush trước và clear/refresh có chủ đích, invalidate cache liên quan; kiểm tra affected rows.  
**Required trade-offs:** Bulk nhanh nhưng bỏ lifecycle callbacks/dirty checking.  
**Follow-up ladder:** `clearAutomatically`? Version increment?  
**Red flags:** Context tự đồng bộ hai chiều với DB.  
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-008 — `SENIOR`
**Question:** Thiết kế repository cho explicit foreign-key IDs mà vẫn tránh query chắp vá thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`  
**Interviewer evaluates:** Query ownership, joins/projections và service orchestration.  
**Answer outline:** Entity giữ IDs theo convention; repository cung cấp use-case projection/join query hoặc batched `IN`, không loop lookup; service giữ transaction/business rule; query tests assert result và count/plan khi critical.  
**Required trade-offs:** Query-specific repository methods tăng surface nhưng performance rõ.  
**Follow-up ladder:** QueryDSL/specification? Module boundary?  
**Red flags:** Thêm `@ManyToOne` trái convention chỉ để fetch tiện.  
**Evidence:** Project convention `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-009 — `ARCHITECT`
**Question:** Tách write model JPA và read model tối ưu query khi nào?  
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`  
**Interviewer evaluates:** Complexity threshold, freshness và ownership.  
**Answer outline:** Khi aggregate write model gây overfetch/joins phức tạp và read SLO khác; bắt đầu bằng projection cùng DB, chỉ materialize/event-driven khi evidence. Define lag/rebuild/reconciliation/schema contracts.  
**Required trade-offs:** Read scale/clarity vs duplication/eventual consistency/ops.  
**Follow-up ladder:** CQRS có cần service riêng?  
**Red flags:** Dùng CQRS từ đầu cho mọi CRUD.  
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### JPA-QUERY-010 — `EXPERT`
**Question:** Query regression chỉ xảy ra sau traffic/data growth: phân biệt ORM, plan, lock và pool bottleneck thế nào?  
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`  
**Interviewer evaluates:** Cross-layer evidence correlation.  
**Answer outline:** Timeline APM/query count/pool waits/DB locks/plan/buffers/GC; nếu queries/request tăng là ORM access; same SQL plan/rows đổi là data/stats; acquire wait có thể downstream DB saturation; test từng hypothesis và preserve samples.  
**Required trade-offs:** Sampling nhẹ production vs diagnostic fidelity.  
**Follow-up ladder:** Plan cache? Lock convoy?  
**Red flags:** Bật EAGER hoặc tăng pool trước khi đo.  
**Evidence:** Incident drill `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `DB-01` active, gắn repository thật và lưu SQL/plan/query-count evidence; không đổi/reuse stable IDs.

# Database Interview Question Bank — Isolation, Locking and After-Commit Consistency

> Status: `DRAFT`  
> Domain owner: `Transaction/Data Consistency`  
> Active slice: `NONE`; preview target: `TX-02`  
> Related roadmap: [Stage 2](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-2---spring-internals-http-api-và-transaction-semantics)  
> Related depth rubric: [Transaction](../../../knowledge-depth-rubric.md#37-transaction-và-data-consistency--p0-target-d3)  
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/database/theory/core/isolation-locking-and-after-commit-consistency.md`  
> Updated: `2026-07-26`

Preview only; không active/implement `TX-02`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `TX-ISO-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### TX-ISO-001 — `FOUNDATION`
**Question:** Dirty read, non-repeatable read, phantom và lost update là gì?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Phân biệt read anomaly và write conflict.  
**Answer outline:** Dirty đọc uncommitted; non-repeatable cùng row đổi; phantom tập kết quả predicate đổi; lost update hai writer ghi đè. Mapping theo isolation phụ thuộc DB/implementation, cần test PostgreSQL thực.  
**Required trade-offs:** Isolation cao giảm anomaly nhưng tăng abort/wait.  
**Follow-up ladder:** Write skew? Read skew?  
**Red flags:** Repeatable Read giải quyết mọi race.  
**Evidence:** Theory `NOT CREATED`; case `TX-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-ISO-002 — `FOUNDATION`
**Question:** MVCC hoạt động ở mức khái niệm như thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Row versions/snapshot và cleanup.  
**Answer outline:** Reader thấy version phù hợp snapshot thay vì luôn block writer; updates tạo row version metadata; vacuum/cleanup thu hồi version cũ. MVCC không loại bỏ write locks/conflicts.  
**Required trade-offs:** Read concurrency tốt đổi lấy storage/bloat/vacuum pressure.  
**Follow-up ladder:** Long transaction ảnh hưởng vacuum?  
**Red flags:** MVCC nghĩa không có lock.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-ISO-003 — `FOUNDATION`
**Question:** Optimistic và pessimistic locking khác nhau; chọn khi nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Conflict detect vs wait/serialize.  
**Answer outline:** Optimistic dùng version/conditional update và retry khi conflict, tốt khi contention thấp; pessimistic lock sớm, phù hợp critical section ngắn/contention dự đoán được nhưng có wait/deadlock.  
**Required trade-offs:** Retry cost vs lock hold/throughput.  
**Follow-up ladder:** `SELECT FOR UPDATE`? `SKIP LOCKED`?  
**Red flags:** Pessimistic luôn an toàn và nhanh hơn.  
**Evidence:** Theory `NOT CREATED`; case `TX-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-ISO-004 — `FOUNDATION`
**Question:** Vì sao DB commit và Redis/cache update không atomic?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Dual-write failure windows.  
**Answer outline:** Hai resource có commit/failure độc lập; cache trước DB có thể lộ uncommitted/rollback, cache sau DB có cửa sổ stale hoặc crash. PostgreSQL là source of truth; cache-aside/invalidation/reconciliation phải explicit.  
**Required trade-offs:** Strong cross-resource coordination đắt; bounded staleness thường thực tế hơn.  
**Follow-up ladder:** Transaction manager 2PC? TTL?  
**Red flags:** `@Transactional` tự rollback Redis.  
**Evidence:** Theory `NOT CREATED`; case `TX-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-ISO-005 — `SENIOR`
**Question:** Dùng conditional update để bảo vệ invariant số dư/trạng thái thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Atomic predicate và affected-row check.  
**Answer outline:** `UPDATE ... SET balance=balance-? WHERE id=? AND balance>=?` hoặc state/version predicate; success khi affected rows=1, nếu 0 phân loại not found/conflict/business rejection. Constraint DB là safety net.  
**Required trade-offs:** Atomic SQL mạnh nhưng domain diagnostics/readability cần mapping rõ.  
**Follow-up ladder:** Concurrent transfer? Retry?  
**Red flags:** Read-check-write trong Java không lock.  
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-ISO-006 — `SENIOR`
**Question:** After-commit event và transactional outbox giải quyết những failure window nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Commit ordering và durable publication.  
**Answer outline:** After-commit tránh side effect trước rollback nhưng process crash sau commit vẫn mất action; outbox ghi business+event cùng DB transaction, relay publish at-least-once, consumer idempotent.  
**Required trade-offs:** Outbox bền hơn nhưng thêm table, relay lag, cleanup/observability.  
**Follow-up ladder:** Duplicate publish? Ordering? CDC?  
**Red flags:** After-commit callback bảo đảm delivery.  
**Evidence:** Theory `NOT CREATED`; design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-ISO-007 — `SENIOR`
**Question:** Chẩn đoán và xử lý database deadlock production thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `DIAGNOSTIC`  
**Interviewer evaluates:** Wait graph, lock order, transaction length và retry.  
**Answer outline:** Thu DB deadlock log/query/transaction IDs, reconstruct lock order; sort resource access nhất quán, rút ngắn transaction/index đúng; retry whole transaction có bounded backoff nếu operation idempotent.  
**Required trade-offs:** Retry phục hồi transient victim nhưng che thiết kế contention nếu không metric.  
**Follow-up ladder:** Lock timeout khác deadlock?  
**Red flags:** Catch exception và retry vô hạn từng statement.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-ISO-008 — `SENIOR`
**Question:** Với stream state ở PostgreSQL và Redis, cập nhật/invalidate sau commit ra sao?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`  
**Interviewer evaluates:** Source of truth, stale window và fallback.  
**Answer outline:** Commit DB trước; after-commit invalidate/cache-aside, TTL bounded, read miss về DB; nếu invalidation quan trọng cần outbox/retry/reconciliation. Define cache-down behavior và metrics stale/miss.  
**Required trade-offs:** Delete cache đơn giản hơn write-through nhưng tạo miss/stampede.  
**Follow-up ladder:** Concurrent reader repopulates stale value?  
**Red flags:** Redis được coi source of truth ngầm.  
**Evidence:** Theory `NOT CREATED`; project Redis guide `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-ISO-009 — `ARCHITECT`
**Question:** Chọn consistency model và SLO cho dữ liệu trải nhiều storage/service thế nào?  
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`  
**Interviewer evaluates:** Invariant classification, staleness budget và recovery.  
**Answer outline:** Tách hard invariant cần atomic owner khỏi derived/read model; định lượng stale/lag/error budget, idempotent events, reconciliation và operator controls. Document client-visible semantics.  
**Required trade-offs:** Availability/latency vs freshness/coordination.  
**Follow-up ladder:** Read-your-writes? Multi-region?  
**Red flags:** “Eventually consistent” không có bound/monitor/recovery.  
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### TX-ISO-010 — `EXPERT`
**Question:** Write skew dưới snapshot isolation phá invariant nhiều-row thế nào và chống ra sao?  
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`  
**Interviewer evaluates:** Predicate invariant và serialization conflict.  
**Answer outline:** Hai transaction đọc cùng snapshot, sửa hai row khác nhau nên không write-write conflict nhưng combined invariant sai; dùng serializable+retry, explicit predicate/advisory lock hoặc redesign constraint/aggregate.  
**Required trade-offs:** Serializable an toàn hơn nhưng abort tăng khi contention.  
**Follow-up ladder:** PostgreSQL SSI? Constraint materialization?  
**Red flags:** Version column từng row chặn mọi write skew.  
**Evidence:** Reproducer `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `TX-02` active, tái hiện anomaly/after-commit failure với PostgreSQL thật; không đổi/reuse stable IDs.

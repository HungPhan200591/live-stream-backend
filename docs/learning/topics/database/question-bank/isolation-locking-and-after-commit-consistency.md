# Database Interview Question Bank — Isolation, Locking and After-Commit Consistency

> Status: `DRAFT`<br>
> Domain owner: `Transaction/Data Consistency`<br>
> Active slice: `NONE`; preview target: `TX-01`<br>
> Related roadmap: [Stage 2](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-2---spring-internals-http-api-và-transaction-semantics)<br>
> Related depth rubric: [Transaction](../../../knowledge-depth-rubric.md#37-transaction-và-data-consistency--p0-target-d3)<br>
> Related theory: [Core theory](../theory/core/isolation-locking-and-after-commit-consistency.md) · [Deep-dive](../theory/deep-dives/isolation-anomalies-deadlocks-and-after-commit-crash-gaps.md)<br>
> Updated: `2026-07-26`

Preview only; không active/implement `TX-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `TX-ISO-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### TX-ISO-001 — `FOUNDATION`
**Question:** Dirty read, non-repeatable read, phantom và lost update là gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Phân biệt read anomaly và write conflict.<br>
**Answer outline:** Dirty đọc uncommitted; non-repeatable cùng row đổi; phantom tập kết quả predicate đổi; lost update hai writer ghi đè. Mapping theo isolation phụ thuộc DB/implementation, cần test PostgreSQL thực.<br>
**Required trade-offs:** Isolation cao giảm anomaly nhưng tăng abort/wait.<br>
**Follow-up ladder:** Write skew? Read skew?<br>
**Red flags:** Repeatable Read giải quyết mọi race.<br>
**Evidence:** Theory [Core](../theory/core/isolation-locking-and-after-commit-consistency.md) + [Deep-dive](../theory/deep-dives/isolation-anomalies-deadlocks-and-after-commit-crash-gaps.md); case `TX-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-ISO-002 — `FOUNDATION`
**Question:** MVCC hoạt động ở mức khái niệm như thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Row versions/snapshot và cleanup.<br>
**Answer outline:** Reader thấy version phù hợp snapshot thay vì luôn block writer; updates tạo row version metadata; vacuum/cleanup thu hồi version cũ. MVCC không loại bỏ write locks/conflicts.<br>
**Required trade-offs:** Read concurrency tốt đổi lấy storage/bloat/vacuum pressure.<br>
**Follow-up ladder:** Long transaction ảnh hưởng vacuum?<br>
**Red flags:** MVCC nghĩa không có lock.<br>
**Evidence:** Theory [Core](../theory/core/isolation-locking-and-after-commit-consistency.md) + [Deep-dive](../theory/deep-dives/isolation-anomalies-deadlocks-and-after-commit-crash-gaps.md); experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-ISO-003 — `FOUNDATION`
**Question:** Optimistic và pessimistic locking khác nhau; chọn khi nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Conflict detect vs wait/serialize.<br>
**Answer outline:** Optimistic dùng version/conditional update và retry khi conflict, tốt khi contention thấp; pessimistic lock sớm, phù hợp critical section ngắn/contention dự đoán được nhưng có wait/deadlock.<br>
**Required trade-offs:** Retry cost vs lock hold/throughput.<br>
**Follow-up ladder:** `SELECT FOR UPDATE`? `SKIP LOCKED`?<br>
**Red flags:** Pessimistic luôn an toàn và nhanh hơn.<br>
**Evidence:** Theory [Core](../theory/core/isolation-locking-and-after-commit-consistency.md) + [Deep-dive](../theory/deep-dives/isolation-anomalies-deadlocks-and-after-commit-crash-gaps.md); case `TX-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-ISO-004 — `FOUNDATION`
**Question:** Vì sao DB commit và Redis/cache update không atomic?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Dual-write failure windows.<br>
**Answer outline:** Hai resource có commit/failure độc lập; cache trước DB có thể lộ uncommitted/rollback, cache sau DB có cửa sổ stale hoặc crash. PostgreSQL là source of truth; cache-aside/invalidation/reconciliation phải explicit.<br>
**Required trade-offs:** Strong cross-resource coordination đắt; bounded staleness thường thực tế hơn.<br>
**Follow-up ladder:** Transaction manager 2PC? TTL?<br>
**Red flags:** `@Transactional` tự rollback Redis.<br>
**Evidence:** Theory [Core](../theory/core/isolation-locking-and-after-commit-consistency.md) + [Deep-dive](../theory/deep-dives/isolation-anomalies-deadlocks-and-after-commit-crash-gaps.md); case `TX-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-ISO-005 — `SENIOR`
**Question:** Dùng conditional update để bảo vệ invariant số dư/trạng thái thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Atomic predicate và affected-row check.<br>
**Answer outline:** `UPDATE ... SET balance=balance-? WHERE id=? AND balance>=?` hoặc state/version predicate; success khi affected rows=1, nếu 0 phân loại not found/conflict/business rejection. Constraint DB là safety net.<br>
**Required trade-offs:** Atomic SQL mạnh nhưng domain diagnostics/readability cần mapping rõ.<br>
**Follow-up ladder:** Concurrent transfer? Retry?<br>
**Red flags:** Read-check-write trong Java không lock.<br>
**Evidence:** Theory [Core](../theory/core/isolation-locking-and-after-commit-consistency.md) + [Deep-dive](../theory/deep-dives/isolation-anomalies-deadlocks-and-after-commit-crash-gaps.md); reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-ISO-006 — `SENIOR`
**Question:** After-commit event và transactional outbox giải quyết những failure window nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Commit ordering và durable publication.<br>
**Answer outline:** After-commit tránh side effect trước rollback nhưng process crash sau commit vẫn mất action; outbox ghi business+event cùng DB transaction, relay publish at-least-once, consumer idempotent.<br>
**Required trade-offs:** Outbox bền hơn nhưng thêm table, relay lag, cleanup/observability.<br>
**Follow-up ladder:** Duplicate publish? Ordering? CDC?<br>
**Red flags:** After-commit callback bảo đảm delivery.<br>
**Evidence:** Theory [Core](../theory/core/isolation-locking-and-after-commit-consistency.md) + [Deep-dive](../theory/deep-dives/isolation-anomalies-deadlocks-and-after-commit-crash-gaps.md); design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-ISO-007 — `SENIOR`
**Question:** Chẩn đoán và xử lý database deadlock production thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `DIAGNOSTIC`<br>
**Interviewer evaluates:** Wait graph, lock order, transaction length và retry.<br>
**Answer outline:** Thu DB deadlock log/query/transaction IDs, reconstruct lock order; sort resource access nhất quán, rút ngắn transaction/index đúng; retry whole transaction có bounded backoff nếu operation idempotent.<br>
**Required trade-offs:** Retry phục hồi transient victim nhưng che thiết kế contention nếu không metric.<br>
**Follow-up ladder:** Lock timeout khác deadlock?<br>
**Red flags:** Catch exception và retry vô hạn từng statement.<br>
**Evidence:** Theory [Core](../theory/core/isolation-locking-and-after-commit-consistency.md) + [Deep-dive](../theory/deep-dives/isolation-anomalies-deadlocks-and-after-commit-crash-gaps.md); experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-ISO-008 — `SENIOR`
**Question:** Với stream state ở PostgreSQL và Redis, cập nhật/invalidate sau commit ra sao?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Source of truth, stale window và fallback.<br>
**Answer outline:** Commit DB trước; after-commit invalidate/cache-aside, TTL bounded, read miss về DB; nếu invalidation quan trọng cần outbox/retry/reconciliation. Define cache-down behavior và metrics stale/miss.<br>
**Required trade-offs:** Delete cache đơn giản hơn write-through nhưng tạo miss/stampede.<br>
**Follow-up ladder:** Concurrent reader repopulates stale value?<br>
**Red flags:** Redis được coi source of truth ngầm.<br>
**Evidence:** Theory [Core](../theory/core/isolation-locking-and-after-commit-consistency.md) + [Deep-dive](../theory/deep-dives/isolation-anomalies-deadlocks-and-after-commit-crash-gaps.md); project Redis guide `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-ISO-009 — `ARCHITECT`
**Question:** Chọn consistency model và SLO cho dữ liệu trải nhiều storage/service thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Invariant classification, staleness budget và recovery.<br>
**Answer outline:** Tách hard invariant cần atomic owner khỏi derived/read model; định lượng stale/lag/error budget, idempotent events, reconciliation và operator controls. Document client-visible semantics.<br>
**Required trade-offs:** Availability/latency vs freshness/coordination.<br>
**Follow-up ladder:** Read-your-writes? Multi-region?<br>
**Red flags:** “Eventually consistent” không có bound/monitor/recovery.<br>
**Evidence:** Theory [Core](../theory/core/isolation-locking-and-after-commit-consistency.md) + [Deep-dive](../theory/deep-dives/isolation-anomalies-deadlocks-and-after-commit-crash-gaps.md); Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TX-ISO-010 — `EXPERT`
**Question:** Write skew dưới snapshot isolation phá invariant nhiều-row thế nào và chống ra sao?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Predicate invariant và serialization conflict.<br>
**Answer outline:** Hai transaction đọc cùng snapshot, sửa hai row khác nhau nên không write-write conflict nhưng combined invariant sai; dùng serializable+retry, explicit predicate/advisory lock hoặc redesign constraint/aggregate.<br>
**Required trade-offs:** Serializable an toàn hơn nhưng abort tăng khi contention.<br>
**Follow-up ladder:** PostgreSQL SSI? Constraint materialization?<br>
**Red flags:** Version column từng row chặn mọi write skew.<br>
**Evidence:** Theory [Core](../theory/core/isolation-locking-and-after-commit-consistency.md) + [Deep-dive](../theory/deep-dives/isolation-anomalies-deadlocks-and-after-commit-crash-gaps.md); Reproducer `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `TX-01` active, tái hiện anomaly/after-commit failure với PostgreSQL thật; không đổi/reuse stable IDs.

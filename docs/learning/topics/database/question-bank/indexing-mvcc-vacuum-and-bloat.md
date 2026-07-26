# Database Interview Question Bank — Indexing, MVCC, Vacuum and Bloat

> Status: `DRAFT`<br>
> Domain owner: `PostgreSQL`<br>
> Active slice: `NONE`; preview target: `DB-01`<br>
> Related roadmap: [Stage 3](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-3---postgresql-model-index-và-query-engineering)<br>
> Related depth rubric: [PostgreSQL](../../../knowledge-depth-rubric.md#39-postgresql-sql-và-data-modeling--p0-target-d3)<br>
> Related theory: [Core theory](../theory/core/indexing-mvcc-vacuum-and-bloat.md) · [Deep-dive](../theory/deep-dives/mvcc-snapshot-bloat-and-vacuum-emergency.md)<br>
> Updated: `2026-07-26`

Preview only; không active/implement `DB-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `DB-INDEX-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### DB-INDEX-001 — `FOUNDATION`
**Question:** B-tree index hỗ trợ loại predicate/order nào và vì sao không phải mọi query?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Ordered keys, equality/range/prefix and planner cost.<br>
**Answer outline:** B-tree supports equality/range/order on leading columns; function/type/collation and low selectivity can prevent benefit. Planner may choose seq scan when many rows.<br>
**Required trade-offs:** Read latency vs write/storage/vacuum cost.<br>
**Follow-up ladder:** LIKE prefix? Expression index?<br>
**Red flags:** Có index nghĩa query chắc dùng.<br>
**Evidence:** Theory [Core](../theory/core/indexing-mvcc-vacuum-and-bloat.md) + [Deep-dive](../theory/deep-dives/mvcc-snapshot-bloat-and-vacuum-emergency.md); case `DB-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-INDEX-002 — `FOUNDATION`
**Question:** Composite index column order được chọn theo access pattern thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Leading prefix, equality/range/order.<br>
**Answer outline:** Place columns to support real predicate/order combinations, equality before range often useful but verify plan/cardinality; one composite is not equivalent to all single-column indexes.<br>
**Required trade-offs:** Wide indexes cover reads but amplify writes/cache footprint.<br>
**Follow-up ladder:** Skip scan/version? Sort direction?<br>
**Red flags:** Luôn đặt column selectivity cao nhất trước.<br>
**Evidence:** Theory [Core](../theory/core/indexing-mvcc-vacuum-and-bloat.md) + [Deep-dive](../theory/deep-dives/mvcc-snapshot-bloat-and-vacuum-emergency.md); case `DB-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-INDEX-003 — `FOUNDATION`
**Question:** Partial, covering/INCLUDE và unique index khác nhau ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Subset predicate, index-only payload và constraint.<br>
**Answer outline:** Partial indexes only rows matching predicate and query must imply it; INCLUDE carries non-key columns; unique enforces key invariant including null semantics/version specifics.<br>
**Required trade-offs:** Smaller/faster reads vs workload-specific maintenance and semantics.<br>
**Follow-up ladder:** Index-only scan visibility map?<br>
**Red flags:** INCLUDE columns participate in uniqueness.<br>
**Evidence:** Theory [Core](../theory/core/indexing-mvcc-vacuum-and-bloat.md) + [Deep-dive](../theory/deep-dives/mvcc-snapshot-bloat-and-vacuum-emergency.md); case `DB-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-INDEX-004 — `FOUNDATION`
**Question:** MVCC row versions và VACUUM liên hệ thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Dead tuples, snapshots and reclaim.<br>
**Answer outline:** Updates/deletes leave dead versions visible to old snapshots; vacuum marks space reusable and maintains visibility/freeze, analyze updates stats. Long transactions/slots can block cleanup.<br>
**Required trade-offs:** MVCC read concurrency vs bloat/maintenance IO.<br>
**Follow-up ladder:** Autovacuum thresholds? Wraparound?<br>
**Red flags:** Vacuum shrinks table file every time.<br>
**Evidence:** Theory [Core](../theory/core/indexing-mvcc-vacuum-and-bloat.md) + [Deep-dive](../theory/deep-dives/mvcc-snapshot-bloat-and-vacuum-emergency.md); case `DB-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-INDEX-005 — `SENIOR`
**Question:** Đọc row-estimation error và chọn index từ `EXPLAIN ANALYZE BUFFERS` thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Actual vs estimate, loops, buffers and query owner.<br>
**Answer outline:** Find expensive node actual time×loops/buffers, compare estimated/actual cardinality, inspect filter removals/sort spill; fix stats/query shape then index and remeasure representative workload.<br>
**Required trade-offs:** Index may hide bad model and increase write cost.<br>
**Follow-up ladder:** Extended statistics? Heap fetches?<br>
**Red flags:** Seq scan trong plan luôn là root cause.<br>
**Evidence:** Theory [Core](../theory/core/indexing-mvcc-vacuum-and-bloat.md) + [Deep-dive](../theory/deep-dives/mvcc-snapshot-bloat-and-vacuum-emergency.md); case `DB-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-INDEX-006 — `SENIOR`
**Question:** Long-running transaction gây bloat và replica lag/incident thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Old snapshot retains tuples/WAL and resource coupling.<br>
**Answer outline:** Old transaction prevents vacuum cleanup, raises table/index size, IO and WAL retention; identify backend/xmin/owner, safely terminate only with impact review, fix transaction boundary/timeouts.<br>
**Required trade-offs:** Kill frees resources but rolls back work and may retry storm.<br>
**Follow-up ladder:** Idle in transaction? Replication slot?<br>
**Red flags:** Autovacuum mạnh hơn luôn vượt qua old snapshot.<br>
**Evidence:** Theory [Core](../theory/core/indexing-mvcc-vacuum-and-bloat.md) + [Deep-dive](../theory/deep-dives/mvcc-snapshot-bloat-and-vacuum-emergency.md); case `DB-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-INDEX-007 — `SENIOR`
**Question:** HOT update, fillfactor và frequently updated columns ảnh hưởng index thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Heap-only tuple eligibility and page space.<br>
**Answer outline:** HOT possible when indexed columns unchanged and page has space; lower fillfactor reserves room, reducing index churn at storage cost. Measure workload/bloat before tuning.<br>
**Required trade-offs:** Write optimization consumes disk/cache.<br>
**Follow-up ladder:** Why index on status hurts HOT?<br>
**Red flags:** HOT means update never writes WAL.<br>
**Evidence:** Theory [Core](../theory/core/indexing-mvcc-vacuum-and-bloat.md) + [Deep-dive](../theory/deep-dives/mvcc-snapshot-bloat-and-vacuum-emergency.md); case `DB-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-INDEX-008 — `SENIOR`
**Question:** Index build/drop/reindex production cần kiểm soát lock/IO ra sao?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Concurrent operations, invalid state and capacity.<br>
**Answer outline:** Use concurrent variants when available with correct transaction constraints, monitor progress/IO/replica lag, timeouts and invalid artifacts; stage one change and verify plan/use before cleanup.<br>
**Required trade-offs:** Lower blocking takes longer/more IO and failure handling.<br>
**Follow-up ladder:** REINDEX CONCURRENTLY? Duplicate indexes?<br>
**Red flags:** CREATE INDEX nhanh ở staging nên an toàn prod.<br>
**Evidence:** Theory [Core](../theory/core/indexing-mvcc-vacuum-and-bloat.md) + [Deep-dive](../theory/deep-dives/mvcc-snapshot-bloat-and-vacuum-emergency.md); case `DB-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-INDEX-009 — `ARCHITECT`
**Question:** Thiết kế index governance cho nhiều workload và tenant thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Query ownership, budget and lifecycle.<br>
**Answer outline:** Inventory top queries, each index has owner/benefit/write/storage cost, detect unused/duplicate indexes with observation window, capacity/autovacuum budget and migration review.<br>
**Required trade-offs:** Central limits prevent sprawl but teams need exception path.<br>
**Follow-up ladder:** Per-tenant skew? Hypothetical index?<br>
**Red flags:** Tự động tạo index theo mọi slow query.<br>
**Evidence:** Theory [Core](../theory/core/indexing-mvcc-vacuum-and-bloat.md) + [Deep-dive](../theory/deep-dives/mvcc-snapshot-bloat-and-vacuum-emergency.md); case `DB-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-INDEX-010 — `EXPERT`
**Question:** Planner đổi plan đột ngột sau data skew/statistics change: điều tra và ổn định thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Distribution, correlation, stats target and parameter plans.<br>
**Answer outline:** Capture old/new plans/data distribution/settings/params, refresh/increase stats or extended stats, rewrite/index for robust selectivity; test generic/custom plan and avoid permanent forced plan without revisit.<br>
**Required trade-offs:** Plan stability vs adaptability as data evolves.<br>
**Follow-up ladder:** Prepared statements? Correlated columns?<br>
**Red flags:** Pin plan là fix dài hạn tốt nhất.<br>
**Evidence:** Theory [Core](../theory/core/indexing-mvcc-vacuum-and-bloat.md) + [Deep-dive](../theory/deep-dives/mvcc-snapshot-bloat-and-vacuum-emergency.md); case `DB-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `DB-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.

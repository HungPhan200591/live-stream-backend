# Database Interview Question Bank — Time Partitioning, Retention and Archive

> Status: `DRAFT`<br>
> Domain owner: `PostgreSQL Data Lifecycle`<br>
> Active slice: `NONE`; preview target: `DB-03`<br>
> Related roadmap: [Stage 9](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-9---primaryreplica-partitioning-và-data-lifecycle)<br>
> Related depth rubric: [Data operations](../../../knowledge-depth-rubric.md#319-data-operations-và-lifecycle--p1-target-d2-d3)<br>
> Related theory: [Core theory](../theory/core/time-partitioning-retention-and-archive.md)<br>
> Updated: `2026-07-26`

Preview only; không active/implement `DB-03`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `DB-PART-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### DB-PART-001 — `FOUNDATION`
**Question:** Table partitioning giải quyết gì và không giải quyết gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Data lifecycle/pruning vs generic query speed.<br>
**Answer outline:** Partitioning splits logical table into children for pruning/maintenance/retention; it does not automatically speed queries without partition predicate or replace indexes/modeling.<br>
**Required trade-offs:** Operational manageability vs planning/schema complexity.<br>
**Follow-up ladder:** Sharding difference?<br>
**Red flags:** Bảng lớn luôn nên partition.<br>
**Evidence:** Theory [Core](../theory/core/time-partitioning-retention-and-archive.md); case `DB-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-PART-002 — `FOUNDATION`
**Question:** Range, list và hash partitioning phù hợp pattern nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Key selection from query/retention/distribution.<br>
**Answer outline:** Range for time windows/retention, list for finite categories/tenant groups, hash spreads keys when no range lifecycle. Partition key must appear in common filters and constraints.<br>
**Required trade-offs:** Lifecycle locality vs skew/repartition complexity.<br>
**Follow-up ladder:** Subpartition? Default partition?<br>
**Red flags:** Chọn key có nhiều distinct nhất.<br>
**Evidence:** Theory [Core](../theory/core/time-partitioning-retention-and-archive.md); case `DB-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-PART-003 — `FOUNDATION`
**Question:** Partition pruning hoạt động và được xác minh thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Planner/executor excludes irrelevant partitions.<br>
**Answer outline:** Predicate compatible with partition key enables pruning; functions/casts/prepared plans may affect it. Verify EXPLAIN actual partitions and buffers with representative query.<br>
**Required trade-offs:** Explicit predicates may leak storage detail into API/repository.<br>
**Follow-up ladder:** Runtime pruning?<br>
**Red flags:** WHERE có timestamp bất kỳ chắc chắn prune.<br>
**Evidence:** Theory [Core](../theory/core/time-partitioning-retention-and-archive.md); case `DB-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-PART-004 — `FOUNDATION`
**Question:** Local index và unique constraint trên partitioned table có caveat gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Per-partition structures and global uniqueness.<br>
**Answer outline:** Indexes typically per partition; uniqueness across all partitions generally requires partition key in constraint or external design. Maintenance/new partitions need index/template automation.<br>
**Required trade-offs:** Including time in key changes business identity semantics.<br>
**Follow-up ladder:** Primary key design?<br>
**Red flags:** Unique index trên parent luôn global mọi version.<br>
**Evidence:** Theory [Core](../theory/core/time-partitioning-retention-and-archive.md); case `DB-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-PART-005 — `SENIOR`
**Question:** Thiết kế monthly partition cho ledger/chat từ query và retention SLO thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Window size, volume, late data and maintenance.<br>
**Answer outline:** Estimate rows/bytes/query ranges, choose granularity avoiding too many/huge partitions, precreate future/default handling, indexes per access path, late-event policy and automated detach/drop.<br>
**Required trade-offs:** Fine partitions improve pruning/drop but planning/ops overhead.<br>
**Follow-up ladder:** Timezone boundary? Backfill?<br>
**Red flags:** Một partition mỗi ngày luôn tối ưu.<br>
**Evidence:** Theory [Core](../theory/core/time-partitioning-retention-and-archive.md); case `DB-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-PART-006 — `SENIOR`
**Question:** Detach/archive/drop partition an toàn hơn mass delete ra sao?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Metadata operation, lock and recovery.<br>
**Answer outline:** Old partition can detach then export/verify and drop quickly, avoiding huge row deletes/vacuum; still plan locks, foreign keys, backups, legal hold and restore dependency.<br>
**Required trade-offs:** Fast deletion reduces rollback window; archive adds storage/catalog cost.<br>
**Follow-up ladder:** Concurrent detach version?<br>
**Red flags:** DROP old partition không cần backup/legal review.<br>
**Evidence:** Theory [Core](../theory/core/time-partitioning-retention-and-archive.md); case `DB-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-PART-007 — `SENIOR`
**Question:** Benchmark before/after partitioning cần đo gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Pruning, planning, execution, writes and maintenance.<br>
**Answer outline:** Representative dataset/queries, plan/buffers/p50-p99, planning time, insert/update, autovacuum and retention job; compare equivalent indexes and cold/warm cache.<br>
**Required trade-offs:** Partition wins lifecycle even if query latency unchanged.<br>
**Follow-up ladder:** Too many partitions threshold?<br>
**Red flags:** One fast query proves partition success.<br>
**Evidence:** Theory [Core](../theory/core/time-partitioning-retention-and-archive.md); case `DB-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-PART-008 — `SENIOR`
**Question:** Schema migration/index rollout cho hàng trăm partitions được điều hành thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Catalog consistency, batching and partial failure.<br>
**Answer outline:** Inventory partitions, automate idempotent per-child DDL, throttle/monitor locks/IO, checkpoint failures, validate all children and future-creation template before parent query relies on schema.<br>
**Required trade-offs:** Parallelism speeds rollout but can saturate DB.<br>
**Follow-up ladder:** Attach validation? Missing index detection?<br>
**Red flags:** Loop DDL không logging là đủ.<br>
**Evidence:** Theory [Core](../theory/core/time-partitioning-retention-and-archive.md); case `DB-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-PART-009 — `ARCHITECT`
**Question:** Phân biệt partitioning, sharding và archival tier trong growth plan thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Single-node lifecycle, horizontal ownership and cold storage.<br>
**Answer outline:** Partition first for local lifecycle/query pruning; replicas for reads; archive cold immutable data; shard only when node write/storage/failure limits require, with routing/rebalancing/transactions cost quantified.<br>
**Required trade-offs:** Scale headroom vs architectural complexity.<br>
**Follow-up ladder:** Tenant sharding? Resharding?<br>
**Red flags:** Partitioning trải data qua nhiều servers tự động.<br>
**Evidence:** Theory [Core](../theory/core/time-partitioning-retention-and-archive.md); case `DB-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-PART-010 — `EXPERT`
**Question:** Partition key sai làm hot current partition và planner overhead; evolve online thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Dual schema/backfill/cutover and invariant.<br>
**Answer outline:** Measure hot writes/query distribution/planning; create new partition scheme/table, dual-write or CDC with source ownership, backfill/checksum, switch reads/writes and retire old via expand-contract.<br>
**Required trade-offs:** Migration doubles write/storage and creates consistency windows.<br>
**Follow-up ladder:** Logical replication? Global IDs?<br>
**Red flags:** ALTER partition key in place là trivial.<br>
**Evidence:** Theory [Core](../theory/core/time-partitioning-retention-and-archive.md); case `DB-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `DB-03` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.

# Database Interview Question Bank — Expand-Contract Schema Migration

> Status: `DRAFT`<br>
> Domain owner: `Schema Evolution`<br>
> Active slice: `NONE`; preview target: `DB-04`<br>
> Related roadmap: [Stage 3](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-3---postgresql-model-index-và-query-engineering)<br>
> Related depth rubric: [PostgreSQL/SQL](../../../knowledge-depth-rubric.md#39-postgresql-sql-và-data-modeling--p0-target-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/database/theory/core/expand-contract-schema-migration.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `DB-04`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `DB-MIG-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### DB-MIG-001 — `FOUNDATION`
**Question:** Expand-contract migration là gì và vì sao cần cho rolling deployment?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Mixed-version compatibility.<br>
**Answer outline:** Expand thêm schema tương thích; deploy code đọc/ghi transition; backfill/verify; switch; contract xóa phần cũ khi không còn consumer. Old/new app phải cùng chạy an toàn.<br>
**Required trade-offs:** Nhiều bước chậm hơn nhưng giảm downtime/rollback risk.<br>
**Follow-up ladder:** Rollback code sau contract?<br>
**Red flags:** Rename/drop column và deploy app cùng lúc.<br>
**Evidence:** Theory `NOT CREATED`; case `DB-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-MIG-002 — `FOUNDATION`
**Question:** Flyway versioned và repeatable migration khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Immutable history, checksum và rerun semantics.<br>
**Answer outline:** Versioned chạy một lần theo version và không sửa sau apply; repeatable rerun khi checksum đổi, phù hợp view/function/reference artifacts có chủ đích. Repair không dùng để che drift.<br>
**Required trade-offs:** Immutable migrations tạo file nhiều nhưng audit/reproducibility tốt.<br>
**Follow-up ladder:** Baseline? Out-of-order?<br>
**Red flags:** Edit migration production đã chạy.<br>
**Evidence:** Existing Flyway bank `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-MIG-003 — `FOUNDATION`
**Question:** Thêm cột `NOT NULL` vào bảng lớn an toàn thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Expand, backfill, validation và lock.<br>
**Answer outline:** Add nullable hoặc safe default theo PG version; deploy dual-compatible write; backfill batches; validate no null/constraint theo low-lock approach; set NOT NULL; sau cùng remove fallback.<br>
**Required trade-offs:** Backfill dài tăng operational work nhưng tránh long lock/rewrite.<br>
**Follow-up ladder:** Default rewrite behavior? Check NOT VALID?<br>
**Red flags:** Một ALTER và hy vọng maintenance window đủ.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-MIG-004 — `FOUNDATION`
**Question:** DDL lock có thể gây outage thế nào dù statement chạy nhanh?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Lock acquisition queue và long transaction.<br>
**Answer outline:** DDL chờ lock sau long transaction; các queries mới xếp sau DDL tạo pile-up. Đặt lock/statement timeout, quan sát blockers, chạy bước nhỏ và có abort plan.<br>
**Required trade-offs:** Timeout bảo vệ traffic nhưng migration cần retry/orchestration.<br>
**Follow-up ladder:** Concurrent index? Transactional DDL?<br>
**Red flags:** Estimated execution 100ms nên không thể outage.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-MIG-005 — `SENIOR`
**Question:** Rename/split một column không downtime được triển khai thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Dual read/write, backfill và cutover.<br>
**Answer outline:** Add new column(s), deploy writer transition (prefer one owner), backfill idempotent batches, compare/reconcile, switch reads, stop old writes, observe, drop old later. Version events/cache too.<br>
**Required trade-offs:** Dual-write có inconsistency window; trigger atomic hơn nhưng logic ẩn/temporary.<br>
**Follow-up ladder:** Source of truth trong transition?<br>
**Red flags:** COALESCE hai cột vĩnh viễn.<br>
**Evidence:** Theory `NOT CREATED`; migration plan `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-MIG-006 — `SENIOR`
**Question:** Backfill production cần checkpoint, throttling và verification thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Idempotent batches và operational safety.<br>
**Answer outline:** Scan keyset theo stable PK, small transactions, update only missing/version, persist checkpoint, rate-limit theo DB health, retry idempotent; metrics remaining/error/lag và independent counts/checksum/sample verification.<br>
**Required trade-offs:** Nhanh hoàn tất vs replication lag/lock/IO impact.<br>
**Follow-up ladder:** Concurrent writes? Vacuum bloat?<br>
**Red flags:** Một UPDATE toàn bảng trong peak traffic.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-MIG-007 — `SENIOR`
**Question:** Tạo index trên bảng lớn an toàn và biết index hữu ích bằng cách nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `DIAGNOSTIC`<br>
**Interviewer evaluates:** Concurrent build, invalid index và plan evidence.<br>
**Answer outline:** Dùng concurrent option phù hợp ngoài transaction, monitor progress/IO/locks; xử lý invalid artifact khi fail; so explain/buffers/query metrics và write overhead trước/sau.<br>
**Required trade-offs:** Concurrent giảm blocking nhưng lâu/tốn IO và có failure cleanup.<br>
**Follow-up ladder:** Partial/covering index?<br>
**Red flags:** Index tồn tại nghĩa planner sẽ dùng.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-MIG-008 — `SENIOR`
**Question:** Test migration từ dữ liệu/version cũ thay vì chỉ clean bootstrap thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Upgrade path, mixed state và constraints.<br>
**Answer outline:** Restore representative prior schema/data, chạy chain migration, assert schema/data/invariant; test fresh install riêng; fixtures cho null/duplicate/large batches; smoke old/new app compatibility nếu rolling.<br>
**Required trade-offs:** Upgrade matrix tốn CI nhưng bắt lỗi production-only.<br>
**Follow-up ladder:** Snapshot per release? Roll-forward recovery?<br>
**Red flags:** H2 create-drop chứng minh PostgreSQL migration.<br>
**Evidence:** Existing migration bank `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-MIG-009 — `ARCHITECT`
**Question:** Governance schema evolution cho nhiều service/team dùng chung database thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Ownership, compatibility window và deployment coordination.<br>
**Answer outline:** Schema/table owner rõ; migration review/lock budget; contract registry hoặc consumer inventory; additive-first, telemetry usage, deprecation deadline; ưu tiên tách ownership thay shared writes.<br>
**Required trade-offs:** Governance giảm velocity cục bộ nhưng ngăn blast radius.<br>
**Follow-up ladder:** Emergency migration? Tenant schemas?<br>
**Red flags:** Shared DB nên ai cũng ALTER tùy ý.<br>
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DB-MIG-010 — `EXPERT`
**Question:** Migration đã deploy làm latency tăng nhưng rollback DDL nguy hiểm; điều hành incident thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Containment, lock/plan evidence và roll-forward.<br>
**Answer outline:** Stop rollout/backfill, inspect waits/plans/IO/bloat, disable new code path via safe flag, kill only verified blockers if authorized; ưu tiên roll-forward compatible fix/index/config, preserve data; rollback schema chỉ có tested plan.<br>
**Required trade-offs:** Fast rollback code vs data already written in new shape.<br>
**Follow-up ladder:** Invalid index cleanup? Replication lag?<br>
**Red flags:** Down migration tự động trong production.<br>
**Evidence:** Incident drill `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `DB-04` active, tạo upgrade/backfill/lock experiment thật; không đổi/reuse stable IDs.

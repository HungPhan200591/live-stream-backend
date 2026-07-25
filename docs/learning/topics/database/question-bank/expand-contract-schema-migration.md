# Database Interview Question Bank — Expand-Contract Schema Migration

> Status: `DRAFT`  
> Domain owner: `Schema Evolution`  
> Active slice: `NONE`; preview target: `DB-04`  
> Related roadmap: [Stage 3](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-3---postgresql-model-index-và-query-engineering)  
> Related depth rubric: [PostgreSQL/SQL](../../../knowledge-depth-rubric.md#39-postgresql-sql-và-data-modeling--p0-target-d3)  
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/database/theory/core/expand-contract-schema-migration.md`  
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
**Question:** Expand-contract migration là gì và vì sao cần cho rolling deployment?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Mixed-version compatibility.  
**Answer outline:** Expand thêm schema tương thích; deploy code đọc/ghi transition; backfill/verify; switch; contract xóa phần cũ khi không còn consumer. Old/new app phải cùng chạy an toàn.  
**Required trade-offs:** Nhiều bước chậm hơn nhưng giảm downtime/rollback risk.  
**Follow-up ladder:** Rollback code sau contract?  
**Red flags:** Rename/drop column và deploy app cùng lúc.  
**Evidence:** Theory `NOT CREATED`; case `DB-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### DB-MIG-002 — `FOUNDATION`
**Question:** Flyway versioned và repeatable migration khác nhau thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Immutable history, checksum và rerun semantics.  
**Answer outline:** Versioned chạy một lần theo version và không sửa sau apply; repeatable rerun khi checksum đổi, phù hợp view/function/reference artifacts có chủ đích. Repair không dùng để che drift.  
**Required trade-offs:** Immutable migrations tạo file nhiều nhưng audit/reproducibility tốt.  
**Follow-up ladder:** Baseline? Out-of-order?  
**Red flags:** Edit migration production đã chạy.  
**Evidence:** Existing Flyway bank `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### DB-MIG-003 — `FOUNDATION`
**Question:** Thêm cột `NOT NULL` vào bảng lớn an toàn thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Expand, backfill, validation và lock.  
**Answer outline:** Add nullable hoặc safe default theo PG version; deploy dual-compatible write; backfill batches; validate no null/constraint theo low-lock approach; set NOT NULL; sau cùng remove fallback.  
**Required trade-offs:** Backfill dài tăng operational work nhưng tránh long lock/rewrite.  
**Follow-up ladder:** Default rewrite behavior? Check NOT VALID?  
**Red flags:** Một ALTER và hy vọng maintenance window đủ.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### DB-MIG-004 — `FOUNDATION`
**Question:** DDL lock có thể gây outage thế nào dù statement chạy nhanh?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Lock acquisition queue và long transaction.  
**Answer outline:** DDL chờ lock sau long transaction; các queries mới xếp sau DDL tạo pile-up. Đặt lock/statement timeout, quan sát blockers, chạy bước nhỏ và có abort plan.  
**Required trade-offs:** Timeout bảo vệ traffic nhưng migration cần retry/orchestration.  
**Follow-up ladder:** Concurrent index? Transactional DDL?  
**Red flags:** Estimated execution 100ms nên không thể outage.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### DB-MIG-005 — `SENIOR`
**Question:** Rename/split một column không downtime được triển khai thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Dual read/write, backfill và cutover.  
**Answer outline:** Add new column(s), deploy writer transition (prefer one owner), backfill idempotent batches, compare/reconcile, switch reads, stop old writes, observe, drop old later. Version events/cache too.  
**Required trade-offs:** Dual-write có inconsistency window; trigger atomic hơn nhưng logic ẩn/temporary.  
**Follow-up ladder:** Source of truth trong transition?  
**Red flags:** COALESCE hai cột vĩnh viễn.  
**Evidence:** Theory `NOT CREATED`; migration plan `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### DB-MIG-006 — `SENIOR`
**Question:** Backfill production cần checkpoint, throttling và verification thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Idempotent batches và operational safety.  
**Answer outline:** Scan keyset theo stable PK, small transactions, update only missing/version, persist checkpoint, rate-limit theo DB health, retry idempotent; metrics remaining/error/lag và independent counts/checksum/sample verification.  
**Required trade-offs:** Nhanh hoàn tất vs replication lag/lock/IO impact.  
**Follow-up ladder:** Concurrent writes? Vacuum bloat?  
**Red flags:** Một UPDATE toàn bảng trong peak traffic.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### DB-MIG-007 — `SENIOR`
**Question:** Tạo index trên bảng lớn an toàn và biết index hữu ích bằng cách nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `DIAGNOSTIC`  
**Interviewer evaluates:** Concurrent build, invalid index và plan evidence.  
**Answer outline:** Dùng concurrent option phù hợp ngoài transaction, monitor progress/IO/locks; xử lý invalid artifact khi fail; so explain/buffers/query metrics và write overhead trước/sau.  
**Required trade-offs:** Concurrent giảm blocking nhưng lâu/tốn IO và có failure cleanup.  
**Follow-up ladder:** Partial/covering index?  
**Red flags:** Index tồn tại nghĩa planner sẽ dùng.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### DB-MIG-008 — `SENIOR`
**Question:** Test migration từ dữ liệu/version cũ thay vì chỉ clean bootstrap thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`  
**Interviewer evaluates:** Upgrade path, mixed state và constraints.  
**Answer outline:** Restore representative prior schema/data, chạy chain migration, assert schema/data/invariant; test fresh install riêng; fixtures cho null/duplicate/large batches; smoke old/new app compatibility nếu rolling.  
**Required trade-offs:** Upgrade matrix tốn CI nhưng bắt lỗi production-only.  
**Follow-up ladder:** Snapshot per release? Roll-forward recovery?  
**Red flags:** H2 create-drop chứng minh PostgreSQL migration.  
**Evidence:** Existing migration bank `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### DB-MIG-009 — `ARCHITECT`
**Question:** Governance schema evolution cho nhiều service/team dùng chung database thế nào?  
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`  
**Interviewer evaluates:** Ownership, compatibility window và deployment coordination.  
**Answer outline:** Schema/table owner rõ; migration review/lock budget; contract registry hoặc consumer inventory; additive-first, telemetry usage, deprecation deadline; ưu tiên tách ownership thay shared writes.  
**Required trade-offs:** Governance giảm velocity cục bộ nhưng ngăn blast radius.  
**Follow-up ladder:** Emergency migration? Tenant schemas?  
**Red flags:** Shared DB nên ai cũng ALTER tùy ý.  
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### DB-MIG-010 — `EXPERT`
**Question:** Migration đã deploy làm latency tăng nhưng rollback DDL nguy hiểm; điều hành incident thế nào?  
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`  
**Interviewer evaluates:** Containment, lock/plan evidence và roll-forward.  
**Answer outline:** Stop rollout/backfill, inspect waits/plans/IO/bloat, disable new code path via safe flag, kill only verified blockers if authorized; ưu tiên roll-forward compatible fix/index/config, preserve data; rollback schema chỉ có tested plan.  
**Required trade-offs:** Fast rollback code vs data already written in new shape.  
**Follow-up ladder:** Invalid index cleanup? Replication lag?  
**Red flags:** Down migration tự động trong production.  
**Evidence:** Incident drill `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `DB-04` active, tạo upgrade/backfill/lock experiment thật; không đổi/reuse stable IDs.

# Database Interview Question Bank — Ledger, Lost Update and Money Invariants

> Status: `DRAFT`  
> Domain owner: `Data Consistency`  
> Active slice: `NONE`; preview target: `WAL-01`  
> Related roadmap: [Stage 3](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-3---postgresql-model-index-và-query-engineering)  
> Related depth rubric: [PostgreSQL/SQL](../../../knowledge-depth-rubric.md#39-postgresql-sql-và-data-modeling--p0-target-d3)  
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/database/theory/core/ledger-lost-update-and-money-invariants.md`  
> Updated: `2026-07-26`

Preview only; không active/implement `WAL-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `LEDGER-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### LEDGER-001 — `FOUNDATION`
**Question:** Vì sao tiền không nên lưu bằng `double`/`float`?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Binary floating point, scale và rounding.  
**Answer outline:** Nhiều decimal không biểu diễn chính xác bằng binary float, phép tính tích lũy sai; dùng integer minor units hoặc BigDecimal/NUMERIC với currency, scale và rounding policy rõ.  
**Required trade-offs:** Minor units nhanh/đơn giản nhưng currency scale khác nhau; decimal linh hoạt nhưng cần canonicalization.  
**Follow-up ladder:** `equals` vs `compareTo` BigDecimal?  
**Red flags:** Làm tròn ở response là đủ.  
**Evidence:** Theory `NOT CREATED`; case `WAL-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### LEDGER-002 — `FOUNDATION`
**Question:** Balance table và append-only ledger khác nhau thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Current state, audit và reconstruction.  
**Answer outline:** Balance là snapshot đọc nhanh nhưng overwrite khó audit; ledger ghi immutable entries, balance là derived/materialized value. Hệ thống thường giữ cả hai với transaction/reconciliation invariant.  
**Required trade-offs:** Auditability/replay vs storage/query complexity.  
**Follow-up ladder:** Double-entry? Reversal?  
**Red flags:** Append-only nghĩa không cần constraint.  
**Evidence:** Theory `NOT CREATED`; case `WAL-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### LEDGER-003 — `FOUNDATION`
**Question:** Lost update xảy ra trong read-modify-write balance thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Concurrent writers và stale read.  
**Answer outline:** Hai transaction đọc cùng balance, tính riêng rồi writer sau ghi đè writer trước. Dùng atomic update có predicate/version, row lock hoặc serializable tùy contention/invariant.  
**Required trade-offs:** Optimistic retry vs pessimistic blocking.  
**Follow-up ladder:** JPA dirty checking có ngăn không?  
**Red flags:** `@Transactional` mặc định tự serialize mọi writer.  
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### LEDGER-004 — `FOUNDATION`
**Question:** Các invariant tối thiểu của transfer/ledger là gì?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Conservation, non-negative rule, currency và uniqueness.  
**Answer outline:** Debit/credit cân bằng theo currency, amount positive/canonical, account/currency match, idempotent command unique, state transition hợp lệ; overdraft policy explicit. Constraints DB bảo vệ invariant biểu diễn được.  
**Required trade-offs:** Constraint chặt tăng safety nhưng migration/business exception cần quản lý.  
**Follow-up ladder:** Pending/available balance? Fees?  
**Red flags:** Chỉ validate ở controller.  
**Evidence:** Theory `NOT CREATED`; design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### LEDGER-005 — `SENIOR`
**Question:** Thiết kế idempotent transfer khi client retry sau timeout thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Unique command, payload fingerprint và result replay.  
**Answer outline:** Unique `(caller, operationKey)`, persist immutable request hash/state/result trong cùng transaction với ledger; duplicate cùng payload replay, khác payload conflict; status query và reconciliation cho in-progress.  
**Required trade-offs:** Key retention/storage vs duplicate protection horizon.  
**Follow-up ladder:** Crash after commit before response?  
**Red flags:** Check tồn tại rồi insert không unique constraint.  
**Evidence:** Theory `NOT CREATED`; design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### LEDGER-006 — `SENIOR`
**Question:** Khóa hai account khi transfer thế nào để tránh deadlock?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Deterministic lock ordering và transaction length.  
**Answer outline:** Lock accounts theo canonical ID order bất kể direction, transaction ngắn; validate/calculate/write ledger+balance atomically; deadlock victim có bounded whole-transaction retry.  
**Required trade-offs:** Row locking đơn giản nhưng hot account giới hạn throughput.  
**Follow-up ladder:** Atomic conditional updates? Sharding?  
**Red flags:** Lock source rồi destination theo request order.  
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### LEDGER-007 — `SENIOR`
**Question:** Reversal khác delete/update ledger entry thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Immutability, audit chain và business time.  
**Answer outline:** Không mutate history; reversal là transaction mới tham chiếu original, opposite postings và reason/actor/time; idempotent reversal key, policy cho partial/fee/exchange.  
**Required trade-offs:** History đầy đủ làm query/current state phức tạp hơn.  
**Follow-up ladder:** Correction vs reversal? Chargeback?  
**Red flags:** Sửa amount cũ để balance đúng.  
**Evidence:** Theory `NOT CREATED`; design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### LEDGER-008 — `SENIOR`
**Question:** Reconciliation phát hiện và xử lý lệch balance/ledger ra sao?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`  
**Interviewer evaluates:** Independent recomputation, alert và safe repair.  
**Answer outline:** Recompute từ immutable postings theo checkpoint, so materialized balance/invariants, metric mismatch; quarantine/alert, repair bằng auditable compensating entry hoặc rebuild, không silent update.  
**Required trade-offs:** Full scan chính xác nhưng đắt; incremental cần trustworthy checkpoints.  
**Follow-up ladder:** Online reconciliation? Backfill versioning?  
**Red flags:** Cron set balance bằng SUM mà không audit.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### LEDGER-009 — `ARCHITECT`
**Question:** Thiết kế ledger nhiều currency/service boundary với ownership rõ thế nào?  
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`  
**Interviewer evaluates:** Source of truth, FX/rounding, event contracts và audit.  
**Answer outline:** Một bounded owner cho posting/invariant; currency/scale explicit, FX quote immutable và gain/loss postings; publish outbox events, consumers idempotent; reporting derived, reconciliation end-to-end.  
**Required trade-offs:** Central ledger consistency vs organizational/scaling coupling.  
**Follow-up ladder:** Multi-region writes? Regulatory retention?  
**Red flags:** Mỗi service tự tính “balance đúng” riêng.  
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### LEDGER-010 — `EXPERT`
**Question:** Thiết kế incident response khi invariant conservation bị phá nhưng traffic vẫn chạy.  
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`  
**Interviewer evaluates:** Containment, evidence preservation và repair.  
**Answer outline:** Stop/limit affected mutations, preserve logs/ledger snapshots, scope by command/version/time, reconcile independently, classify code/data/duplicate issue; repair bằng reviewed compensating entries, verify totals, postmortem và permanent guard.  
**Required trade-offs:** Freeze writes bảo vệ correctness nhưng ảnh hưởng availability/business.  
**Follow-up ladder:** Customer communication? Replay safety?  
**Red flags:** Chạy SQL update trực tiếp rồi xóa dấu vết.  
**Evidence:** Incident drill `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `WAL-01` active, tái hiện concurrent transfer và reconciliation evidence; không đổi/reuse stable IDs.

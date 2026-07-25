# Database Interview Question Bank — Ledger, Lost Update and Money Invariants

> Status: `DRAFT`<br>
> Domain owner: `Data Consistency`<br>
> Active slice: `NONE`; preview target: `WAL-01`<br>
> Related roadmap: [Stage 3](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-3---postgresql-model-index-và-query-engineering)<br>
> Related depth rubric: [PostgreSQL/SQL](../../../knowledge-depth-rubric.md#39-postgresql-sql-và-data-modeling--p0-target-d3)<br>
> Related theory: [Core theory](../theory/core/ledger-lost-update-and-money-invariants.md)<br>
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
**Question:** Vì sao tiền không nên lưu bằng `double`/`float`?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Binary floating point, scale và rounding.<br>
**Answer outline:** Nhiều decimal không biểu diễn chính xác bằng binary float, phép tính tích lũy sai; dùng integer minor units hoặc BigDecimal/NUMERIC với currency, scale và rounding policy rõ.<br>
**Required trade-offs:** Minor units nhanh/đơn giản nhưng currency scale khác nhau; decimal linh hoạt nhưng cần canonicalization.<br>
**Follow-up ladder:** `equals` vs `compareTo` BigDecimal?<br>
**Red flags:** Làm tròn ở response là đủ.<br>
**Evidence:** Theory [Core](../theory/core/ledger-lost-update-and-money-invariants.md); case `WAL-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEDGER-002 — `FOUNDATION`
**Question:** Balance table và append-only ledger khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Current state, audit và reconstruction.<br>
**Answer outline:** Balance là snapshot đọc nhanh nhưng overwrite khó audit; ledger ghi immutable entries, balance là derived/materialized value. Hệ thống thường giữ cả hai với transaction/reconciliation invariant.<br>
**Required trade-offs:** Auditability/replay vs storage/query complexity.<br>
**Follow-up ladder:** Double-entry? Reversal?<br>
**Red flags:** Append-only nghĩa không cần constraint.<br>
**Evidence:** Theory [Core](../theory/core/ledger-lost-update-and-money-invariants.md); case `WAL-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEDGER-003 — `FOUNDATION`
**Question:** Lost update xảy ra trong read-modify-write balance thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Concurrent writers và stale read.<br>
**Answer outline:** Hai transaction đọc cùng balance, tính riêng rồi writer sau ghi đè writer trước. Dùng atomic update có predicate/version, row lock hoặc serializable tùy contention/invariant.<br>
**Required trade-offs:** Optimistic retry vs pessimistic blocking.<br>
**Follow-up ladder:** JPA dirty checking có ngăn không?<br>
**Red flags:** `@Transactional` mặc định tự serialize mọi writer.<br>
**Evidence:** Theory [Core](../theory/core/ledger-lost-update-and-money-invariants.md); reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEDGER-004 — `FOUNDATION`
**Question:** Các invariant tối thiểu của transfer/ledger là gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Conservation, non-negative rule, currency và uniqueness.<br>
**Answer outline:** Debit/credit cân bằng theo currency, amount positive/canonical, account/currency match, idempotent command unique, state transition hợp lệ; overdraft policy explicit. Constraints DB bảo vệ invariant biểu diễn được.<br>
**Required trade-offs:** Constraint chặt tăng safety nhưng migration/business exception cần quản lý.<br>
**Follow-up ladder:** Pending/available balance? Fees?<br>
**Red flags:** Chỉ validate ở controller.<br>
**Evidence:** Theory [Core](../theory/core/ledger-lost-update-and-money-invariants.md); design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEDGER-005 — `SENIOR`
**Question:** Thiết kế idempotent transfer khi client retry sau timeout thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Unique command, payload fingerprint và result replay.<br>
**Answer outline:** Unique `(caller, operationKey)`, persist immutable request hash/state/result trong cùng transaction với ledger; duplicate cùng payload replay, khác payload conflict; status query và reconciliation cho in-progress.<br>
**Required trade-offs:** Key retention/storage vs duplicate protection horizon.<br>
**Follow-up ladder:** Crash after commit before response?<br>
**Red flags:** Check tồn tại rồi insert không unique constraint.<br>
**Evidence:** Theory [Core](../theory/core/ledger-lost-update-and-money-invariants.md); design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEDGER-006 — `SENIOR`
**Question:** Khóa hai account khi transfer thế nào để tránh deadlock?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Deterministic lock ordering và transaction length.<br>
**Answer outline:** Lock accounts theo canonical ID order bất kể direction, transaction ngắn; validate/calculate/write ledger+balance atomically; deadlock victim có bounded whole-transaction retry.<br>
**Required trade-offs:** Row locking đơn giản nhưng hot account giới hạn throughput.<br>
**Follow-up ladder:** Atomic conditional updates? Sharding?<br>
**Red flags:** Lock source rồi destination theo request order.<br>
**Evidence:** Theory [Core](../theory/core/ledger-lost-update-and-money-invariants.md); reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEDGER-007 — `SENIOR`
**Question:** Reversal khác delete/update ledger entry thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Immutability, audit chain và business time.<br>
**Answer outline:** Không mutate history; reversal là transaction mới tham chiếu original, opposite postings và reason/actor/time; idempotent reversal key, policy cho partial/fee/exchange.<br>
**Required trade-offs:** History đầy đủ làm query/current state phức tạp hơn.<br>
**Follow-up ladder:** Correction vs reversal? Chargeback?<br>
**Red flags:** Sửa amount cũ để balance đúng.<br>
**Evidence:** Theory [Core](../theory/core/ledger-lost-update-and-money-invariants.md); design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEDGER-008 — `SENIOR`
**Question:** Reconciliation phát hiện và xử lý lệch balance/ledger ra sao?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Independent recomputation, alert và safe repair.<br>
**Answer outline:** Recompute từ immutable postings theo checkpoint, so materialized balance/invariants, metric mismatch; quarantine/alert, repair bằng auditable compensating entry hoặc rebuild, không silent update.<br>
**Required trade-offs:** Full scan chính xác nhưng đắt; incremental cần trustworthy checkpoints.<br>
**Follow-up ladder:** Online reconciliation? Backfill versioning?<br>
**Red flags:** Cron set balance bằng SUM mà không audit.<br>
**Evidence:** Theory [Core](../theory/core/ledger-lost-update-and-money-invariants.md); experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEDGER-009 — `ARCHITECT`
**Question:** Thiết kế ledger nhiều currency/service boundary với ownership rõ thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Source of truth, FX/rounding, event contracts và audit.<br>
**Answer outline:** Một bounded owner cho posting/invariant; currency/scale explicit, FX quote immutable và gain/loss postings; publish outbox events, consumers idempotent; reporting derived, reconciliation end-to-end.<br>
**Required trade-offs:** Central ledger consistency vs organizational/scaling coupling.<br>
**Follow-up ladder:** Multi-region writes? Regulatory retention?<br>
**Red flags:** Mỗi service tự tính “balance đúng” riêng.<br>
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEDGER-010 — `EXPERT`
**Question:** Thiết kế incident response khi invariant conservation bị phá nhưng traffic vẫn chạy.<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Containment, evidence preservation và repair.<br>
**Answer outline:** Stop/limit affected mutations, preserve logs/ledger snapshots, scope by command/version/time, reconcile independently, classify code/data/duplicate issue; repair bằng reviewed compensating entries, verify totals, postmortem và permanent guard.<br>
**Required trade-offs:** Freeze writes bảo vệ correctness nhưng ảnh hưởng availability/business.<br>
**Follow-up ladder:** Customer communication? Replay safety?<br>
**Red flags:** Chạy SQL update trực tiếp rồi xóa dấu vết.<br>
**Evidence:** Incident drill `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `WAL-01` active, tái hiện concurrent transfer và reconciliation evidence; không đổi/reuse stable IDs.

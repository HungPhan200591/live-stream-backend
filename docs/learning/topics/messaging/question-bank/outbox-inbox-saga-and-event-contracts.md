# Messaging Interview Question Bank — Outbox, Inbox, Saga and Event Contracts

> Status: `DRAFT`<br>
> Domain owner: `Reliable Event Workflow`<br>
> Active slice: `NONE`; preview target: `EVT-01`<br>
> Related roadmap: [Stage 6](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-6---reliable-event-driven-workflow)<br>
> Related depth rubric: [Event workflow](../../../knowledge-depth-rubric.md#316-rabbitmq-kafka-và-event-driven-workflow--p1-target-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/messaging/theory/core/outbox-inbox-saga-and-event-contracts.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `EVT-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `EVT-REL-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### EVT-REL-001 — `FOUNDATION`
**Question:** Dual write giữa database và broker có những crash window nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Hai resource commit độc lập.<br>
**Answer outline:** Publish trước DB có ghost event nếu rollback; DB trước publish có lost event nếu crash; retry có duplicate. Cần durable intent/outbox và idempotent consumer.<br>
**Required trade-offs:** Coordination tăng reliability nhưng thêm lag/storage/ops.<br>
**Follow-up ladder:** 2PC khi nào? After-commit đủ không?<br>
**Red flags:** `@Transactional` bao luôn broker.<br>
**Evidence:** Theory `NOT CREATED`; case `EVT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### EVT-REL-002 — `FOUNDATION`
**Question:** Transactional outbox hoạt động end-to-end như thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Atomic business+event write và asynchronous relay.<br>
**Answer outline:** Cùng DB transaction ghi aggregate và outbox; relay claim/publish/confirm rồi mark; at-least-once nên có duplicate; monitor lag và cleanup.<br>
**Required trade-offs:** Polling đơn giản nhưng latency/load; CDC mạnh hơn nhưng ops phức tạp.<br>
**Follow-up ladder:** Claim nhiều workers? Ordering?<br>
**Red flags:** Outbox tạo exactly-once tự động.<br>
**Evidence:** Theory `NOT CREATED`; case `EVT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### EVT-REL-003 — `FOUNDATION`
**Question:** Inbox/dedup bảo vệ consumer ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Atomic processed marker với business side effect.<br>
**Answer outline:** Consumer lưu message/business key unique cùng transaction với side effect; duplicate skip/replay result; ACK/offset chỉ sau durable commit.<br>
**Required trade-offs:** Retention dài tốn storage; ngắn giới hạn dedup horizon.<br>
**Follow-up ladder:** Concurrent duplicate? Poison payload?<br>
**Red flags:** In-memory cache đủ cho dedup.<br>
**Evidence:** Theory `NOT CREATED`; case `EVT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### EVT-REL-004 — `FOUNDATION`
**Question:** Saga, compensation và semantic rollback khác database rollback thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Long-running distributed state transition.<br>
**Answer outline:** Saga là chuỗi local transactions; failure kích hoạt compensating action theo business, không xóa lịch sử hay hoàn tác mọi external effect; trạng thái/timeout/retry explicit.<br>
**Required trade-offs:** Availability/autonomy vs temporary inconsistency/complex recovery.<br>
**Follow-up ladder:** Orchestration vs choreography?<br>
**Red flags:** Compensation luôn khôi phục đúng trạng thái ban đầu.<br>
**Evidence:** Theory `NOT CREATED`; case `EVT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### EVT-REL-005 — `SENIOR`
**Question:** Thiết kế gift purchase để không double-spend khi API/broker/consumer retry thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** End-to-end idempotency và ledger invariant.<br>
**Answer outline:** API key unique, wallet ledger+gift+outbox atomic; relay at-least-once; inbox/conditional projection; reconciliation query nối command-ledger-gift-event.<br>
**Required trade-offs:** Extra writes/latency vs financial correctness.<br>
**Follow-up ladder:** Crash sau debit? Duplicate notification?<br>
**Red flags:** Broker message ID một mình bảo vệ wallet.<br>
**Evidence:** Theory `NOT CREATED`; case `EVT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### EVT-REL-006 — `SENIOR`
**Question:** Event ordering và optimistic conflict được xử lý theo aggregate key thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Per-key order, version và stale/out-of-order event.<br>
**Answer outline:** Event mang aggregate ID/version; partition/routing giữ order khi possible; consumer conditional apply expected version, park/retry gaps và idempotently ignore old duplicate.<br>
**Required trade-offs:** Strict order giảm parallelism và hot-key scale.<br>
**Follow-up ladder:** Global order có cần? Gap timeout?<br>
**Red flags:** Timestamp đủ làm total order.<br>
**Evidence:** Theory `NOT CREATED`; case `EVT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### EVT-REL-007 — `SENIOR`
**Question:** Evolve event schema backward/forward compatible thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Producer/consumer rollout và semantic version.<br>
**Answer outline:** Add optional/default fields, stable type/meaning, tolerant readers có giới hạn, schema/contract tests, consumer telemetry và deprecation; new semantic event thay vì đổi nghĩa field cũ.<br>
**Required trade-offs:** Compatibility giữ legacy debt; versioning tăng topic/handler complexity.<br>
**Follow-up ladder:** Enum evolution? Upcaster?<br>
**Red flags:** JSON schema pass nghĩa semantic compatible.<br>
**Evidence:** Theory `NOT CREATED`; case `EVT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### EVT-REL-008 — `SENIOR`
**Question:** DLQ quarantine và replay cần authorization/audit gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Replay là privileged mutation, không chỉ queue operation.<br>
**Answer outline:** Store failure reason/payload reference safely, owner/alert; inspect/fix, select range, dry-run/throttle, idempotent replay, record actor/time/result và protect secrets.<br>
**Required trade-offs:** Fast recovery vs re-trigger side effects/PII exposure.<br>
**Follow-up ladder:** Poison message repair? Replay ordering?<br>
**Red flags:** Bấm requeue toàn DLQ là runbook.<br>
**Evidence:** Theory `NOT CREATED`; case `EVT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### EVT-REL-009 — `ARCHITECT`
**Question:** Govern event workflow nhiều team với ownership, SLO và reconciliation thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Contract owner, delivery lag và business completeness.<br>
**Answer outline:** Mỗi event có producer owner/schema; consumer owns idempotency; SLO outbox lag/consumer lag/DLQ, lineage/correlation, invariant reconciliation và retirement policy.<br>
**Required trade-offs:** Decoupling teams vs governance/observability cost.<br>
**Follow-up ladder:** Event catalog? Shared library?<br>
**Red flags:** Broker uptime là đủ chứng minh workflow healthy.<br>
**Evidence:** Theory `NOT CREATED`; case `EVT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### EVT-REL-010 — `EXPERT`
**Question:** Kill process tại từng crash point và chứng minh durable intent/no double-spend thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Failure matrix, linearization point và evidence.<br>
**Answer outline:** Enumerate before/after API idempotency claim, DB commit, relay publish/confirm/mark, consumer commit/ACK; inject kill, restart/replay, assert ledger conservation/unique gift/inbox and trace lineage.<br>
**Required trade-offs:** Exhaustive fault lab tốn thời gian nhưng khóa invariant critical.<br>
**Follow-up ladder:** Network partition vs process kill? Jepsen-like limit?<br>
**Red flags:** Một happy-path integration test chứng minh mọi crash window.<br>
**Evidence:** Theory `NOT CREATED`; case `EVT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `EVT-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.


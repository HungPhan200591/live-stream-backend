# Architecture Interview Question Bank — Distributed Consistency, Clocks and Coordination

> Status: `DRAFT`<br>
> Domain owner: `Distributed Systems`<br>
> Active slice: `NONE`; preview target: `DS-01`<br>
> Related roadmap: [Stage 10](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-10---modular-monolith-to-microservices)<br>
> Related depth rubric: [Distributed systems](../../../knowledge-depth-rubric.md#312-distributed-systems-fundamentals--p0-target-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/architecture/theory/core/distributed-consistency-clocks-and-coordination.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `DS-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `DS-CONS-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### DS-CONS-001 — `FOUNDATION`
**Question:** Partial failure và unbounded delay khác local exception thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Cannot know remote state from timeout.<br>
**Answer outline:** Some nodes/links fail while others work; response delay indistinguishable from failure within finite wait. Caller timeout means unknown outcome, requiring idempotency/status/reconciliation.<br>
**Required trade-offs:** Waiting longer gains certainty chance but holds resources.<br>
**Follow-up ladder:** Two generals? Failure detector?<br>
**Red flags:** Timeout chứng minh remote operation thất bại.<br>
**Evidence:** Theory `NOT CREATED`; case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-002 — `FOUNDATION`
**Question:** Strong, eventual, read-your-writes và monotonic consistency khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Guarantees visible to client per operation/session.<br>
**Answer outline:** Strong/linearizable operations appear single current order; eventual converges; RYW and monotonic are session guarantees. State guarantee per use case, not database slogan.<br>
**Required trade-offs:** Stronger consistency costs latency/availability/coordination.<br>
**Follow-up ladder:** Causal consistency?<br>
**Red flags:** Eventual consistency nghĩa data tùy ý.<br>
**Evidence:** Theory `NOT CREATED`; case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-003 — `FOUNDATION`
**Question:** CAP và PACELC nên dùng trong context cụ thể ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Partition choice and normal latency-consistency trade-off.<br>
**Answer outline:** During network partition choose availability or consistency for an operation; else PACELC highlights latency vs consistency. Real systems choose per path/quorum/config, not whole-product label.<br>
**Required trade-offs:** Theorem frames limits but doesn't select product.<br>
**Follow-up ladder:** What is partition?<br>
**Red flags:** NoSQL là AP, SQL là CP mọi lúc.<br>
**Evidence:** Theory `NOT CREATED`; case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-004 — `FOUNDATION`
**Question:** Wall clock, monotonic clock và logical/version clock khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Duration vs timestamp vs causality/order.<br>
**Answer outline:** Monotonic clock for elapsed deadlines, wall clock for human time can jump/skew, logical/version counters capture causal/order within scope. Timestamp alone cannot prove causality/global order.<br>
**Required trade-offs:** Coordination for order costs latency/availability.<br>
**Follow-up ladder:** NTP? Lamport/vector clock?<br>
**Red flags:** System.currentTimeMillis tạo unique total order.<br>
**Evidence:** Theory `NOT CREATED`; case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-005 — `SENIOR`
**Question:** Quorum read/write với N/R/W reasoning thế nào và có giới hạn gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Overlap, replica freshness and sloppy/conflict behavior.<br>
**Answer outline:** R+W>N can overlap under assumptions, W quorum affects durability, but latency, failed/stale nodes, hinted handoff and conflict resolution matter; quorum doesn't automatically produce linearizability without protocol.<br>
**Required trade-offs:** Higher quorum consistency/durability vs availability/latency.<br>
**Follow-up ladder:** Read repair? Leaderless?<br>
**Red flags:** Majority vote luôn trả latest value.<br>
**Evidence:** Theory `NOT CREATED`; case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-006 — `SENIOR`
**Question:** Leader, lease và fencing token bảo vệ coordination thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Single authority and stale leader writes.<br>
**Answer outline:** Consensus/election chooses term; lease expiry alone may leave paused old leader; downstream accepts monotonically increasing fencing term and rejects stale writes. Business constraints remain safety net.<br>
**Required trade-offs:** Fencing requires downstream support and coordination.<br>
**Follow-up ladder:** GC pause? Clock lease?<br>
**Red flags:** Redis lock TTL bảo đảm old owner dừng.<br>
**Evidence:** Theory `NOT CREATED`; case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-007 — `SENIOR`
**Question:** Duplicate/out-of-order events được xử lý bằng version/state machine ra sao?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Idempotency, causal scope and gap policy.<br>
**Answer outline:** Stable event ID dedup; aggregate version conditional apply, ignore old duplicate, buffer/retry/repair gaps; partition key when order needed. Define retention and poison handling.<br>
**Required trade-offs:** Strict gap waiting can block progress.<br>
**Follow-up ladder:** Commutative events?<br>
**Red flags:** Sort all events by timestamp.<br>
**Evidence:** Theory `NOT CREATED`; case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-008 — `SENIOR`
**Question:** Thiết kế failure matrix cho remote interaction gồm những gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Before/during/after commit, timeout/retry/cancel and recovery.<br>
**Answer outline:** Enumerate DNS/connect/TLS/timeout, server crash pre/post side effect, response loss, duplicate/reorder, dependency overload; for each define deadline, idempotency, retry budget, state query, compensation, telemetry/runbook.<br>
**Required trade-offs:** More safeguards add state/ops cost; prioritize invariant/blast radius.<br>
**Follow-up ladder:** Byzantine failures?<br>
**Red flags:** Happy path plus one timeout test đủ.<br>
**Evidence:** Theory `NOT CREATED`; case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-009 — `ARCHITECT`
**Question:** Chọn consistency model cho multi-region wallet, profile và analytics khác nhau thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Per-domain invariant and regional topology.<br>
**Answer outline:** Wallet single writer/consensus or regional ownership with ledger; profile may accept LWW/RYW; analytics async replay. State RPO/RTO/latency and conflict/reconciliation policy per domain.<br>
**Required trade-offs:** Global correctness vs local latency/availability.<br>
**Follow-up ladder:** Follow-the-sun ownership?<br>
**Red flags:** Một multi-region database setting cho mọi data.<br>
**Evidence:** Theory `NOT CREATED`; case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-010 — `EXPERT`
**Question:** Dẫn incident network partition với two writers và divergent side effects thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Fencing, source of truth and reconciliation.<br>
**Answer outline:** Contain writes, establish authoritative epoch/ledger, preserve both histories, match idempotency/business IDs and external effects, compensate audibly, rebuild replicas and repair routing/election; update partition drills.<br>
**Required trade-offs:** Availability during partition may have created costly conflicts.<br>
**Follow-up ladder:** Split-brain detection delay?<br>
**Red flags:** Merge rows bằng latest timestamp.<br>
**Evidence:** Theory `NOT CREATED`; case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `DS-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.


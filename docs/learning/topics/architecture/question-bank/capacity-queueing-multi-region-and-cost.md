# Architecture Interview Question Bank — Capacity, Queueing, Multi-Region and Cost

> Status: `DRAFT`<br>
> Domain owner: `Solution Architecture`<br>
> Active slice: `NONE`; preview target: `ARCH-01`<br>
> Related roadmap: [Stage 11](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-11---solution-architecture-capstones)<br>
> Related depth rubric: [Solution architecture](../../../knowledge-depth-rubric.md#313-solution-architecture--p0-target-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/architecture/theory/core/capacity-queueing-multi-region-and-cost.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `ARCH-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `ARCH-CAP-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### ARCH-CAP-001 — `FOUNDATION`
**Question:** Throughput, latency, concurrency và utilization liên hệ thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Work completed/time, response distribution, inflight and saturation.<br>
**Answer outline:** Concurrency roughly arrival rate × residence time under stable conditions (Little's Law); utilization near bottleneck capacity drives queueing/tail latency. State units/assumptions.<br>
**Required trade-offs:** More concurrency can increase throughput until saturation then worsen latency.<br>
**Follow-up ladder:** Service time vs response time?<br>
**Red flags:** Tăng threads luôn tăng throughput.<br>
**Evidence:** Theory `NOT CREATED`; case `ARCH-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### ARCH-CAP-002 — `FOUNDATION`
**Question:** Availability, reliability, durability và resilience khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Service success, sustained correctness, data survival and recovery.<br>
**Answer outline:** Availability is usable time/requests; reliability consistent success; durability data persistence; resilience degrades/recovers under failure. Metrics and controls differ.<br>
**Required trade-offs:** Maximizing one may increase cost/latency or hurt another.<br>
**Follow-up ladder:** Fault tolerance?<br>
**Red flags:** Nhiều replicas bảo đảm cả bốn.<br>
**Evidence:** Theory `NOT CREATED`; case `ARCH-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### ARCH-CAP-003 — `FOUNDATION`
**Question:** Vertical/horizontal scaling và stateless/stateful components khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Capacity unit and partition/state movement.<br>
**Answer outline:** Scale up bigger node simple but bounded/blast radius; scale out distributes across nodes, easier for stateless, stateful needs partition/replication/coordination. Identify bottleneck first.<br>
**Required trade-offs:** Scale out adds network/consistency/ops.<br>
**Follow-up ladder:** Sticky session?<br>
**Red flags:** Kubernetes HPA làm mọi service horizontally scalable.<br>
**Evidence:** Theory `NOT CREATED`; case `ARCH-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### ARCH-CAP-004 — `FOUNDATION`
**Question:** RPO/RTO, failure domain và headroom được đưa vào design ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Recovery objectives and spare capacity under failure.<br>
**Answer outline:** Map zone/region/dependency failures, choose data replication/backups and capacity N+1 so remaining fleet meets degraded SLO; RPO/RTO drive cost/runbook/drills.<br>
**Required trade-offs:** Headroom idle cost vs recovery ability.<br>
**Follow-up ladder:** Error budget?<br>
**Red flags:** Normal peak capacity đủ cho zone failure.<br>
**Evidence:** Theory `NOT CREATED`; case `ARCH-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### ARCH-CAP-005 — `SENIOR`
**Question:** Ước lượng 100k concurrent viewers/chat fan-out từ assumptions nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Connection count, message rate, fan-out bytes and bottlenecks.<br>
**Answer outline:** State viewers/rooms/messages per user, payload/protocol overhead, connections/node, egress, auth/reconnect rate; calculate publish fan-out, broker/gateway queues, memory/FD and test bottleneck with headroom.<br>
**Required trade-offs:** Approximation guides test but false precision dangerous.<br>
**Follow-up ladder:** Celebrity room? Regional distribution?<br>
**Red flags:** 100k users = 100k requests/sec.<br>
**Evidence:** Theory `NOT CREATED`; case `ARCH-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### ARCH-CAP-006 — `SENIOR`
**Question:** Gift sale spike và event backlog được capacity-plan mà giữ wallet invariant thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Admission, DB transaction capacity, queue drain and correctness.<br>
**Answer outline:** Estimate arrival/burst, DB commits/sec/locks, bounded admission/idempotency, outbox/broker throughput, consumer drain time and stale SLO; shed noncritical work, never relax ledger invariant.<br>
**Required trade-offs:** Reject/queue improves safety but impacts UX/revenue.<br>
**Follow-up ladder:** Oversell? Retry amplification?<br>
**Red flags:** Kafka/Rabbit buffer vô hạn hấp thụ mọi spike.<br>
**Evidence:** Theory `NOT CREATED`; case `ARCH-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### ARCH-CAP-007 — `SENIOR`
**Question:** Multi-region single-writer, regional ownership và active-active khác nhau thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Write routing, latency, conflicts and failover.<br>
**Answer outline:** Single writer simple consistency but remote latency; regional ownership partitions invariant by key; active-active needs conflict-free/coordination model. Reads/cache and failover tokens explicit.<br>
**Required trade-offs:** Local latency/availability vs global consistency/complexity.<br>
**Follow-up ladder:** Home region migration?<br>
**Red flags:** Active-active chỉ cần database multi-master.<br>
**Evidence:** Theory `NOT CREATED`; case `ARCH-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### ARCH-CAP-008 — `SENIOR`
**Question:** Đưa cost vào architecture decision mà không tối ưu mù quáng thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Unit economics, utilization, egress, people and risk.<br>
**Answer outline:** Model cost/request/user/GB incl compute/storage/network/licenses/on-call, peak/headroom/DR and migration; compare against SLO/business value and revisit with actual billing/metrics.<br>
**Required trade-offs:** Cheapest infra may raise engineering/incident cost.<br>
**Follow-up ladder:** Managed service TCO? Reserved capacity?<br>
**Red flags:** Chỉ so giá VM hàng tháng.<br>
**Evidence:** Theory `NOT CREATED`; case `ARCH-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### ARCH-CAP-009 — `ARCHITECT`
**Question:** Trình bày một capstone 2/15/45 phút với cùng decision spine thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Assumptions, requirements, design, tradeoffs and evolution.<br>
**Answer outline:** Lead with requirements/scale/invariants; data/control flow, capacity/bottleneck, consistency/security/failure/observability/cost; alternatives and phased validation. Depth changes, core decisions remain.<br>
**Required trade-offs:** Concise version omits detail but not assumptions/risks.<br>
**Follow-up ladder:** Whiteboard order? Stakeholder tailoring?<br>
**Red flags:** Liệt kê công nghệ trước requirement.<br>
**Evidence:** Theory `NOT CREATED`; case `ARCH-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### ARCH-CAP-010 — `EXPERT`
**Question:** Celebrity/reconnect storm đồng thời zone failure: thiết kế và incident response ra sao?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Compound failure, queueing feedback and prioritized degradation.<br>
**Answer outline:** Reserve cross-zone capacity, jitter reconnect/admission, partition hot rooms, bounded queues/backpressure, shed presence/history before auth/core chat, protect DB/broker and monitor recovery drain; rehearse compound fault.<br>
**Required trade-offs:** Availability for all features vs survival of critical path.<br>
**Follow-up ladder:** Global load balancing? Brownout?<br>
**Red flags:** Autoscale after CPU rises sẽ cứu kịp mọi storm.<br>
**Evidence:** Theory `NOT CREATED`; case `ARCH-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `ARCH-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.


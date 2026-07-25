# Resilience Interview Question Bank — Circuit Breaker, Bulkhead and Load Shedding

> Status: `DRAFT`<br>
> Domain owner: `Resilience`<br>
> Active slice: `NONE`; preview target: `RES-03`<br>
> Related roadmap: [Stage 2](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-2---spring-internals-http-api-và-transaction-semantics)<br>
> Related depth rubric: [Distributed systems](../../../knowledge-depth-rubric.md#312-distributed-systems-fundamentals--p0-target-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/resilience/theory/core/circuit-breaker-bulkhead-and-load-shedding.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `RES-03`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `RES-CB-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### RES-CB-001 — `FOUNDATION`
**Question:** Circuit breaker có các state nào và giải quyết vấn đề gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Closed/open/half-open và fail-fast.<br>
**Answer outline:** Closed ghi nhận outcomes; vượt threshold mở để fail-fast; sau wait chuyển half-open cho probe giới hạn rồi close/reopen. Nó bảo vệ khỏi dependency failing/slow, không sửa dependency.<br>
**Required trade-offs:** Mở breaker giảm load nhưng từ chối request có thể đã thành công.<br>
**Follow-up ladder:** Count/time window? Slow-call threshold?<br>
**Red flags:** Breaker thay thế timeout.<br>
**Evidence:** Theory `NOT CREATED`; case `RES-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-CB-002 — `FOUNDATION`
**Question:** Bulkhead là gì; semaphore và thread-pool bulkhead khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Failure isolation và bounded concurrency.<br>
**Answer outline:** Bulkhead chia capacity để một dependency/tenant không hút hết resource; semaphore giới hạn concurrent calls ít overhead; dedicated pool cô lập thread/queue nhưng tăng scheduling/context propagation.<br>
**Required trade-offs:** Isolation tốt hơn nhưng giảm pooling efficiency và cần sizing.<br>
**Follow-up ladder:** Virtual threads thay đổi gì?<br>
**Red flags:** Mỗi request một pool.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-CB-003 — `FOUNDATION`
**Question:** Load shedding và rate limiting khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Policy quota vs overload protection.<br>
**Answer outline:** Rate limit áp policy theo client/window dù hệ thống khỏe; shedding từ chối khi capacity/saturation/deadline không đáp ứng. Có thể dùng cùng token/admission controls nhưng mục tiêu và response/metrics khác.<br>
**Required trade-offs:** Shed sớm giữ latency cho accepted traffic nhưng giảm availability tức thời.<br>
**Follow-up ladder:** 429 vs 503? Retry-After?<br>
**Red flags:** Queue vô hạn tốt hơn reject.<br>
**Evidence:** Theory `NOT CREATED`; case `RES-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-CB-004 — `FOUNDATION`
**Question:** Fallback tốt và fallback nguy hiểm khác nhau ở đâu?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Semantic correctness và stale/default data.<br>
**Answer outline:** Tốt khi business chấp nhận degraded/stale response được gắn rõ và bounded; nguy hiểm khi giả success, che authorization/payment failure hoặc trả default sai invariant. Fallback phải observable.<br>
**Required trade-offs:** UX liên tục vs correctness/trust.<br>
**Follow-up ladder:** Cache fallback? Feature disable?<br>
**Red flags:** Catch mọi exception trả empty list.<br>
**Evidence:** Theory `NOT CREATED`; design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-CB-005 — `SENIOR`
**Question:** Chọn threshold/window/minimum calls cho breaker thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Traffic volume, error/slow-call rate và false trips.<br>
**Answer outline:** Dựa baseline/SLO và dependency capacity; minimum calls tránh mẫu nhỏ, time/count window phù hợp QPS, tách lỗi được tính; half-open probes bounded. Validate bằng fault/load tests.<br>
**Required trade-offs:** Nhạy mở sớm nhưng flapping; chậm bảo vệ thì saturation lan rộng.<br>
**Follow-up ladder:** Per-instance vs shared state?<br>
**Red flags:** Copy threshold 50% cho mọi service.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-CB-006 — `SENIOR`
**Question:** Thứ tự timeout, retry, breaker và bulkhead nên reasoning thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Composition semantics và resource scope.<br>
**Answer outline:** Không có order mù quáng: timeout bounded từng attempt/end-to-end; bulkhead giới hạn actual calls; breaker quan sát outcomes đúng tầng; retry không vượt deadline và thường mỗi attempt đi qua protection. Test call graph/advisor order.<br>
**Required trade-offs:** Order thay metric, capacity và breaker sampling.<br>
**Follow-up ladder:** Retry có tính từng failure vào breaker?<br>
**Red flags:** Chỉ cần annotations, order không quan trọng.<br>
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-CB-007 — `SENIOR`
**Question:** Breaker flapping hoặc luôn open phải chẩn đoán thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `DIAGNOSTIC`<br>
**Interviewer evaluates:** State transition metrics, failure classification và probe load.<br>
**Answer outline:** Xem timeline state, call volume, error class/latency, min calls/window, half-open concurrency và downstream health; tìm retries/fallback bị tính sai. Tune sau khi sửa root cause, có manual override an toàn.<br>
**Required trade-offs:** Force-close khôi phục traffic nhưng có thể tái tạo cascade.<br>
**Follow-up ladder:** Config drift giữa instances?<br>
**Red flags:** Restart app để reset breaker là recovery plan.<br>
**Evidence:** Theory `NOT CREATED`; incident drill `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-CB-008 — `SENIOR`
**Question:** Ưu tiên/shed traffic khi overload mà không làm starvation tenant nhỏ thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Fairness, quotas và critical path.<br>
**Answer outline:** Partition capacity/reserved budget, weighted fair queues/token buckets, deadline-aware admission; protect critical control/auth paths; measure rejection per tenant/class và cap noisy neighbor.<br>
**Required trade-offs:** Reserved capacity có thể idle trong bình thường nhưng bảo đảm critical workload.<br>
**Follow-up ladder:** VIP traffic ethics? Queue discipline?<br>
**Red flags:** First-come-first-served luôn công bằng.<br>
**Evidence:** Design `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-CB-009 — `ARCHITECT`
**Question:** Thiết kế graceful degradation cho live-stream backend khi Redis/RabbitMQ/PostgreSQL lỗi khác nhau thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Source-of-truth classification và feature criticality.<br>
**Answer outline:** PostgreSQL source of truth nên mutation fail rõ khi mất DB; Redis cache có thể fallback DB với bulkhead/rate guard; broker failure cần durable outbox/deferred processing nếu business cho phép. Công bố degraded behavior/SLO.<br>
**Required trade-offs:** Availability của read vs overload DB; accept command vs delayed visibility.<br>
**Follow-up ladder:** WebSocket fan-out? Operator kill switch?<br>
**Red flags:** Một fallback empty response cho mọi dependency.<br>
**Evidence:** Project context `EXISTS`; design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-CB-010 — `EXPERT`
**Question:** Breaker per-instance gây synchronized probe hoặc uneven recovery ra sao; xử lý thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Distributed local state và recovery waves.<br>
**Answer outline:** Instances có traffic/config khác nên state lệch; cùng wait duration có thể half-open đồng loạt. Jitter recovery/probes, cap global concurrency qua downstream admission, aggregate telemetry; shared breaker state chỉ khi coordination cost hợp lý.<br>
**Required trade-offs:** Local breaker đơn giản/resilient; shared state nhất quán nhưng tạo dependency mới.<br>
**Follow-up ladder:** New instance cold start? Autoscaling?<br>
**Red flags:** Đồng bộ breaker qua Redis luôn tốt hơn.<br>
**Evidence:** Reproducer `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `RES-03` active, fault-inject và ghi transition/load evidence thật; không đổi/reuse stable IDs.

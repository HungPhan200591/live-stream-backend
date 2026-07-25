# Resilience Interview Question Bank — Circuit Breaker, Bulkhead and Load Shedding

> Status: `DRAFT`  
> Domain owner: `Resilience`  
> Active slice: `NONE`; preview target: `RES-03`  
> Related roadmap: [Stage 2](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-2---spring-internals-http-api-và-transaction-semantics)  
> Related depth rubric: [Distributed systems](../../../knowledge-depth-rubric.md#312-distributed-systems-fundamentals--p0-target-d3)  
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/resilience/theory/core/circuit-breaker-bulkhead-and-load-shedding.md`  
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
**Question:** Circuit breaker có các state nào và giải quyết vấn đề gì?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Closed/open/half-open và fail-fast.  
**Answer outline:** Closed ghi nhận outcomes; vượt threshold mở để fail-fast; sau wait chuyển half-open cho probe giới hạn rồi close/reopen. Nó bảo vệ khỏi dependency failing/slow, không sửa dependency.  
**Required trade-offs:** Mở breaker giảm load nhưng từ chối request có thể đã thành công.  
**Follow-up ladder:** Count/time window? Slow-call threshold?  
**Red flags:** Breaker thay thế timeout.  
**Evidence:** Theory `NOT CREATED`; case `RES-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-CB-002 — `FOUNDATION`
**Question:** Bulkhead là gì; semaphore và thread-pool bulkhead khác nhau thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Failure isolation và bounded concurrency.  
**Answer outline:** Bulkhead chia capacity để một dependency/tenant không hút hết resource; semaphore giới hạn concurrent calls ít overhead; dedicated pool cô lập thread/queue nhưng tăng scheduling/context propagation.  
**Required trade-offs:** Isolation tốt hơn nhưng giảm pooling efficiency và cần sizing.  
**Follow-up ladder:** Virtual threads thay đổi gì?  
**Red flags:** Mỗi request một pool.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-CB-003 — `FOUNDATION`
**Question:** Load shedding và rate limiting khác nhau thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Policy quota vs overload protection.  
**Answer outline:** Rate limit áp policy theo client/window dù hệ thống khỏe; shedding từ chối khi capacity/saturation/deadline không đáp ứng. Có thể dùng cùng token/admission controls nhưng mục tiêu và response/metrics khác.  
**Required trade-offs:** Shed sớm giữ latency cho accepted traffic nhưng giảm availability tức thời.  
**Follow-up ladder:** 429 vs 503? Retry-After?  
**Red flags:** Queue vô hạn tốt hơn reject.  
**Evidence:** Theory `NOT CREATED`; case `RES-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-CB-004 — `FOUNDATION`
**Question:** Fallback tốt và fallback nguy hiểm khác nhau ở đâu?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Semantic correctness và stale/default data.  
**Answer outline:** Tốt khi business chấp nhận degraded/stale response được gắn rõ và bounded; nguy hiểm khi giả success, che authorization/payment failure hoặc trả default sai invariant. Fallback phải observable.  
**Required trade-offs:** UX liên tục vs correctness/trust.  
**Follow-up ladder:** Cache fallback? Feature disable?  
**Red flags:** Catch mọi exception trả empty list.  
**Evidence:** Theory `NOT CREATED`; design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-CB-005 — `SENIOR`
**Question:** Chọn threshold/window/minimum calls cho breaker thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Traffic volume, error/slow-call rate và false trips.  
**Answer outline:** Dựa baseline/SLO và dependency capacity; minimum calls tránh mẫu nhỏ, time/count window phù hợp QPS, tách lỗi được tính; half-open probes bounded. Validate bằng fault/load tests.  
**Required trade-offs:** Nhạy mở sớm nhưng flapping; chậm bảo vệ thì saturation lan rộng.  
**Follow-up ladder:** Per-instance vs shared state?  
**Red flags:** Copy threshold 50% cho mọi service.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-CB-006 — `SENIOR`
**Question:** Thứ tự timeout, retry, breaker và bulkhead nên reasoning thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Composition semantics và resource scope.  
**Answer outline:** Không có order mù quáng: timeout bounded từng attempt/end-to-end; bulkhead giới hạn actual calls; breaker quan sát outcomes đúng tầng; retry không vượt deadline và thường mỗi attempt đi qua protection. Test call graph/advisor order.  
**Required trade-offs:** Order thay metric, capacity và breaker sampling.  
**Follow-up ladder:** Retry có tính từng failure vào breaker?  
**Red flags:** Chỉ cần annotations, order không quan trọng.  
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-CB-007 — `SENIOR`
**Question:** Breaker flapping hoặc luôn open phải chẩn đoán thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `DIAGNOSTIC`  
**Interviewer evaluates:** State transition metrics, failure classification và probe load.  
**Answer outline:** Xem timeline state, call volume, error class/latency, min calls/window, half-open concurrency và downstream health; tìm retries/fallback bị tính sai. Tune sau khi sửa root cause, có manual override an toàn.  
**Required trade-offs:** Force-close khôi phục traffic nhưng có thể tái tạo cascade.  
**Follow-up ladder:** Config drift giữa instances?  
**Red flags:** Restart app để reset breaker là recovery plan.  
**Evidence:** Theory `NOT CREATED`; incident drill `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-CB-008 — `SENIOR`
**Question:** Ưu tiên/shed traffic khi overload mà không làm starvation tenant nhỏ thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`  
**Interviewer evaluates:** Fairness, quotas và critical path.  
**Answer outline:** Partition capacity/reserved budget, weighted fair queues/token buckets, deadline-aware admission; protect critical control/auth paths; measure rejection per tenant/class và cap noisy neighbor.  
**Required trade-offs:** Reserved capacity có thể idle trong bình thường nhưng bảo đảm critical workload.  
**Follow-up ladder:** VIP traffic ethics? Queue discipline?  
**Red flags:** First-come-first-served luôn công bằng.  
**Evidence:** Design `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-CB-009 — `ARCHITECT`
**Question:** Thiết kế graceful degradation cho live-stream backend khi Redis/RabbitMQ/PostgreSQL lỗi khác nhau thế nào?  
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`  
**Interviewer evaluates:** Source-of-truth classification và feature criticality.  
**Answer outline:** PostgreSQL source of truth nên mutation fail rõ khi mất DB; Redis cache có thể fallback DB với bulkhead/rate guard; broker failure cần durable outbox/deferred processing nếu business cho phép. Công bố degraded behavior/SLO.  
**Required trade-offs:** Availability của read vs overload DB; accept command vs delayed visibility.  
**Follow-up ladder:** WebSocket fan-out? Operator kill switch?  
**Red flags:** Một fallback empty response cho mọi dependency.  
**Evidence:** Project context `EXISTS`; design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-CB-010 — `EXPERT`
**Question:** Breaker per-instance gây synchronized probe hoặc uneven recovery ra sao; xử lý thế nào?  
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`  
**Interviewer evaluates:** Distributed local state và recovery waves.  
**Answer outline:** Instances có traffic/config khác nên state lệch; cùng wait duration có thể half-open đồng loạt. Jitter recovery/probes, cap global concurrency qua downstream admission, aggregate telemetry; shared breaker state chỉ khi coordination cost hợp lý.  
**Required trade-offs:** Local breaker đơn giản/resilient; shared state nhất quán nhưng tạo dependency mới.  
**Follow-up ladder:** New instance cold start? Autoscaling?  
**Red flags:** Đồng bộ breaker qua Redis luôn tốt hơn.  
**Evidence:** Reproducer `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `RES-03` active, fault-inject và ghi transition/load evidence thật; không đổi/reuse stable IDs.

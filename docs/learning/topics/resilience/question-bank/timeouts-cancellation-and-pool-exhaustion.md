# Resilience Interview Question Bank — Timeouts, Cancellation and Pool Exhaustion

> Status: `DRAFT`<br>
> Domain owner: `Resilience`<br>
> Active slice: `NONE`; preview target: `RES-01`<br>
> Related roadmap: [Stage 2](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-2---spring-internals-http-api-và-transaction-semantics)<br>
> Related depth rubric: [Distributed systems](../../../knowledge-depth-rubric.md#312-distributed-systems-fundamentals--p0-target-d3)<br>
> Related theory: [Timeouts, Cancellation and Pool Exhaustion](../theory/core/timeouts-cancellation-and-pool-exhaustion.md), [coordinated resilience deep-dive](../theory/deep-dives/timeout-retry-circuit-bulkhead-and-overload-control.md) — `DRAFT`, evidence `NOT RUN`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `RES-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `RES-TIME-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### RES-TIME-001 — `FOUNDATION`
**Question:** Connect, read/socket, request và transaction timeout khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Timeout theo từng phase/resource.<br>
**Answer outline:** Connect giới hạn thiết lập kết nối; read/socket chờ bytes; request/deadline bao end-to-end; transaction giới hạn DB unit. Mỗi layer cần budget nhất quán, không có một timeout chung tự bao phủ tất cả.<br>
**Required trade-offs:** Ngắn fail-fast nhưng false timeout; dài giữ resource và tăng tail latency.<br>
**Follow-up ladder:** DNS/TLS timeout? Idle timeout?<br>
**Red flags:** Chỉ cấu hình client read timeout là đủ.<br>
**Evidence:** Theory `NOT CREATED`; case `RES-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-TIME-002 — `FOUNDATION`
**Question:** Timeout khác cancellation thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Caller stops waiting vs work actually stops.<br>
**Answer outline:** Timeout là quyết định của observer/caller; downstream task có thể tiếp tục. Cancellation cần tín hiệu được propagate và code/driver cooperative; side effect đã commit không tự hoàn tác.<br>
**Required trade-offs:** Aggressive cancellation tiết kiệm resource nhưng cần cleanup/idempotency.<br>
**Follow-up ladder:** Thread interrupt? HTTP disconnect?<br>
**Red flags:** Future timeout tự kill thread và rollback remote side effect.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-TIME-003 — `FOUNDATION`
**Question:** Connection pool và thread pool bảo vệ điều gì; vì sao queue vô hạn nguy hiểm?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Bounded resource và queueing latency.<br>
**Answer outline:** Pool giới hạn concurrency tới resource; queue vô hạn giấu overload, tăng memory/wait rồi timeout hàng loạt. Cần capacity, bounded queue, acquisition timeout và rejection policy.<br>
**Required trade-offs:** Queue nhỏ reject sớm; queue lớn hấp thụ burst nhưng tăng latency.<br>
**Follow-up ladder:** Little's Law? Caller-runs?<br>
**Red flags:** Tăng pool/queue luôn tăng throughput.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-TIME-004 — `FOUNDATION`
**Question:** Tail latency và percentile p95/p99 quan trọng hơn average ở đâu?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** User experience và fan-out amplification.<br>
**Answer outline:** Average che request chậm; p99 mô tả tail. Fan-out nhiều dependency khiến xác suất ít nhất một call chậm tăng; đo theo endpoint/status/dependency với histogram đúng.<br>
**Required trade-offs:** Cardinality chi tiết hỗ trợ debug nhưng tăng telemetry cost.<br>
**Follow-up ladder:** Coordinated omission? SLO percentile?<br>
**Red flags:** Average 100 ms nghĩa mọi request nhanh.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-TIME-005 — `SENIOR`
**Question:** Phân bổ end-to-end deadline xuống các downstream call thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Remaining budget, cleanup và fail-fast.<br>
**Answer outline:** Từ SLO trừ ingress/processing/egress reserve; propagate absolute deadline/remaining budget, mỗi hop timeout ngắn hơn caller; tránh retry vượt deadline và đo timeout phase.<br>
**Required trade-offs:** Budget cứng ổn định tail nhưng có thể giảm success khi dependency jitter.<br>
**Follow-up ladder:** Clock skew? Parallel fan-out?<br>
**Red flags:** Mỗi downstream đặt 30s dù request budget 2s.<br>
**Evidence:** Theory `NOT CREATED`; design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-TIME-006 — `SENIOR`
**Question:** Pool exhaustion biểu hiện ra sao và chẩn đoán bằng evidence nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `DIAGNOSTIC`<br>
**Interviewer evaluates:** Active/idle/pending, thread dump, DB waits và causal chain.<br>
**Answer outline:** Theo dõi pool utilization/acquire latency/timeouts, request queue, thread dumps, slow query/locks và dependency latency; phân biệt leak, long transaction, overload, undersizing. Correlate theo timeline.<br>
**Required trade-offs:** Tăng pool có thể chuyển overload sang DB.<br>
**Follow-up ladder:** Hikari leak detection? Virtual threads?<br>
**Red flags:** Thấy pending là tăng max pool ngay.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-TIME-007 — `SENIOR`
**Question:** Khi caller hủy request, code Spring/Java nên cleanup và bảo toàn invariant thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Interrupt handling, resource closure và commit ambiguity.<br>
**Answer outline:** Preserve interrupt status, close resource bằng structured scopes/try-with-resources, propagate cancellation nơi driver hỗ trợ; không assume disconnect rollback. Durable mutation cần idempotency/status query.<br>
**Required trade-offs:** Cancellation cooperative không bảo đảm tức thời với blocking/non-interruptible code.<br>
**Follow-up ladder:** `InterruptedException` handling? CompletableFuture cancel?<br>
**Red flags:** Catch interrupt rồi bỏ qua.<br>
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-TIME-008 — `SENIOR`
**Question:** Load test timeout/pool saturation thế nào để không tạo kết luận giả?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `EXPERIMENT_DESIGN`<br>
**Interviewer evaluates:** Controlled workload, warmup, percentiles và bottleneck evidence.<br>
**Answer outline:** Nêu hypothesis; warmup; fixed hardware/data; ramp concurrency; đo throughput, p50/p95/p99, queue/acquire/GC/DB; inject latency; tách client bottleneck và coordinated omission; lưu config/raw output.<br>
**Required trade-offs:** Synthetic test repeatable nhưng không phản ánh traffic mix đầy đủ.<br>
**Follow-up ladder:** Open vs closed model? Soak test?<br>
**Red flags:** Chỉ báo requests/second trung bình.<br>
**Evidence:** Experiment plan `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-TIME-009 — `ARCHITECT`
**Question:** Thiết kế admission control và capacity budget xuyên nhiều tier thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Bottleneck ownership, fairness và overload behavior.<br>
**Answer outline:** Capacity model theo bottleneck; bounded concurrency/queue từng tier; priority/fairness; propagate deadlines; shed trước scarce resource; autoscale theo leading indicator và test overload recovery.<br>
**Required trade-offs:** Rejection bảo vệ hệ thống nhưng cần product policy cho request nào bị từ chối.<br>
**Follow-up ladder:** Multi-tenant noisy neighbor? Brownout?<br>
**Red flags:** Autoscaling thay thế backpressure.<br>
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-TIME-010 — `EXPERT`
**Question:** Giải thích cascading failure từ slow DB đến timeout storm và recovery collapse.<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Queueing feedback loop và evidence timeline.<br>
**Answer outline:** Slow DB giữ connections→request threads/virtual tasks chờ→queues/pools đầy→caller timeout nhưng work còn chạy→retry tăng load→GC/context switching; recovery bị backlog đè. Cắt bằng deadlines, cancellation, bounded concurrency, shedding và retry budget.<br>
**Required trade-offs:** Fail-fast giảm completion rate tức thời để giữ system recoverable.<br>
**Follow-up ladder:** Evidence đầu tiên? Drain strategy?<br>
**Red flags:** Root cause duy nhất là CPU app cao.<br>
**Evidence:** Incident drill `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `RES-01` active, tạo fault injection/load evidence thật; không đổi/reuse stable IDs.

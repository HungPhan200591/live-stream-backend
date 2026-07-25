# Java Interview Question Bank — Executors, CompletableFuture and Concurrency Control

> Status: `DRAFT`<br>
> Domain owner: `Java concurrency / async execution`<br>
> Active slice: `NONE`; preview target `CON-01`<br>
> Runtime baseline: `Java 21`<br>
> Related roadmap: [Stage 1](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-1---java-core-state-và-concurrency)<br>
> Related depth rubric: [Concurrency/JMM](../../../knowledge-depth-rubric.md#34-concurrency-jmm-và-async-model--p0-target-d3)<br>
> Related theory: [Executors, CompletableFuture and Concurrency Control](../theory/core/executors-completablefuture-and-concurrency-control.md), [saturation/cancellation deep-dive](../theory/deep-dives/executors-cancellation-context-and-backpressure.md) — `DRAFT`, evidence `NOT RUN`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `CON-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Slice | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Executor/CF core | 4 | 2 | 0 | 0 |
| Failure/capacity | 0 | 2 | 1 | 1 |
| **Tổng** | **4** | **4** | **1** | **1** |

## Recommended practice order

1. First pass: `CON-EXEC-001` đến `CON-EXEC-006`.
2. Senior follow-up: `CON-EXEC-007`, `CON-EXEC-008`.
3. Project application: `CON-EXEC-006`, `CON-EXEC-008`.
4. Stretch: `CON-EXEC-009`, `CON-EXEC-010`.

## Questions

### CON-EXEC-001 — `FOUNDATION`
**Question:** `Runnable`, `Callable`, `Future` và `CompletableFuture` khác nhau thế nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — async Java foundation.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Result/exception/cancellation và composition.<br>
**Answer outline:** Runnable no result/checked throw contract; Callable returns/throws; Future is handle for result/cancel/block; CompletableFuture adds completion and stage composition.<br>
**Required trade-offs:** Composition giảm blocking nhưng error/cancellation/context semantics phức tạp.<br>
**Follow-up ladder:** `get` vs `join`? CompletionStage?<br>
**Red flags:** CompletableFuture tự tạo thread cho mỗi stage.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-EXEC-002 — `FOUNDATION`
**Question:** Vì sao dùng `ExecutorService` thay vì tự tạo thread cho mỗi task?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — thread-pool question phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Lifecycle, reuse, queue/bounds và shutdown.<br>
**Answer outline:** Executor tách submission/execution, reuse/bound resources, expose queue/rejection/shutdown; raw thread per task khó capacity/context/cleanup. Virtual threads thay cost model nhưng vẫn cần admission/resource bounds.<br>
**Required trade-offs:** Pool bảo vệ resources nhưng queue ẩn latency nếu unbounded.<br>
**Follow-up ladder:** `shutdown` vs `shutdownNow`? Await termination?<br>
**Red flags:** Fixed pool tự tạo backpressure.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-EXEC-003 — `FOUNDATION`
**Question:** Core/max size, keep-alive, work queue và rejection policy tương tác thế nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — ThreadPoolExecutor classic.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Queue-first growth semantics và overload behavior.<br>
**Answer outline:** Pool grows to core, then queues, then toward max only when queue cannot accept; saturation invokes rejection. Exact behavior depends queue type; bounds/metrics/rejection are part of correctness.<br>
**Required trade-offs:** Large queue smooth burst nhưng tăng latency/memory; more threads increase contention/downstream load.<br>
**Follow-up ladder:** CallerRuns? SynchronousQueue? Prestart?<br>
**Red flags:** Max pool size luôn được dùng dù queue unbounded.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-EXEC-004 — `FOUNDATION`
**Question:** `thenApply`, `thenCompose`, `thenCombine` và async variants khác nhau thế nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — CompletableFuture core.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Map, flatMap, independent combine và execution context.<br>
**Answer outline:** Apply maps result; compose flattens dependent future; combine joins independent results; non-async may run on completing thread, async uses default/specified executor.<br>
**Required trade-offs:** Implicit executor/thread execution dễ gây blocking/starvation; pass owned executor where isolation matters.<br>
**Follow-up ladder:** `allOf`? `anyOf`? Thread of callback?<br>
**Red flags:** Async suffix nghĩa là non-blocking I/O tự động.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-EXEC-005 — `SENIOR`
**Question:** Exception, timeout và cancellation propagate qua CompletableFuture chain thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — async failure question phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Exceptional completion, handle/recover và cancellation limits.<br>
**Answer outline:** Failed stage skips normal dependents; `exceptionally/handle/whenComplete` có semantics khác; timeout/cancel completion không đảm bảo underlying I/O dừng nếu task không cooperative. Preserve cause/deadline.<br>
**Required trade-offs:** Recovery fallback tăng availability nhưng có thể che partial failure/stale data.<br>
**Follow-up ladder:** `get` wrapper? Interrupt? OrTimeout?<br>
**Red flags:** Cancel future chắc chắn rollback side effect.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-EXEC-006 — `SENIOR`
**Question:** Size pool cho CPU-bound và blocking I/O dựa trên gì?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — capacity question phổ biến.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** CPU cores, wait/compute ratio, downstream capacity và measurement.<br>
**Answer outline:** CPU pool quanh available cores theo profile; blocking concurrency theo latency/throughput/downstream connection limits và queueing; benchmark under quota. Virtual threads reduce thread cost, không tăng DB connections/CPU.<br>
**Required trade-offs:** More concurrency giảm wait idle nhưng gây saturation/context switching/downstream collapse.<br>
**Follow-up ladder:** Little's Law? Container cores? DB pool?<br>
**Red flags:** Threads = requests hoặc cores×2 cho mọi workload.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-EXEC-007 — `SENIOR`
**Question:** ThreadLocal/MDC/security context bị leak hoặc mất qua thread pool thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — server async follow-up.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Thread reuse, explicit propagation và cleanup.<br>
**Answer outline:** Pool thread sống qua requests nên ThreadLocal không clear leaks data; async stage đổi thread nên context mất. Capture minimal context, decorate task, restore/clear in finally; không truyền credential rộng.<br>
**Required trade-offs:** Automatic propagation tiện nhưng tăng hidden coupling/security surface.<br>
**Follow-up ladder:** Virtual thread? InheritableThreadLocal? Trace context?<br>
**Red flags:** ThreadLocal là request-local theo mặc định.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-EXEC-008 — `SENIOR`
**Question:** Chọn platform pool, virtual threads hay async composition cho blocking service path thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — Java 21 application.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Workload, pinning, backpressure và ecosystem.<br>
**Answer outline:** Platform pool bounds scarce threads; virtual threads simplify thread-per-task blocking but still bound downstream and inspect pinning; async composition phù hợp nonblocking APIs/fan-out but harder context/debug. Measure JFR/load.<br>
**Required trade-offs:** Simplicity, throughput, memory, cancellation và operational familiarity.<br>
**Follow-up ladder:** CPU work? synchronized pinning? JDBC?<br>
**Red flags:** Virtual threads bỏ nhu cầu rate limit/pool DB.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-EXEC-009 — `ARCHITECT`
**Question:** Thiết kế executor ownership và concurrency budget xuyên request, scheduler, broker consumer.<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `LOW` — architecture stretch.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Isolation, queue bounds, deadlines, shutdown và metrics.<br>
**Answer outline:** Separate workload/failure domains; bind concurrency to CPU/downstream; bounded queues/rejection/deadline/cancel; expose active/queued/rejected/age; graceful drain; no shared common pool accidental.<br>
**Required trade-offs:** More pools isolate but waste capacity/complex operations.<br>
**Follow-up ladder:** Bulkhead? Priority? Autoscaling?<br>
**Red flags:** Một global executor cho mọi workload.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-EXEC-010 — `EXPERT`
**Question:** Pool starvation deadlock xảy ra khi task chờ task cùng pool thế nào?<br>
**Target depth:** `D4`<br>
**Interview likelihood:** `LOW` — pathological concurrency discriminator.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Dependency graph, bounded workers và blocking joins.<br>
**Answer outline:** All workers submit dependent work then block/join; queued dependencies không có worker. Reproduce with tiny pool/barrier, inspect dump/queue; compose nonblocking, separate executor or avoid nested submission.<br>
**Required trade-offs:** Larger pool masks cycle, not proof of correctness.<br>
**Follow-up ladder:** Common ForkJoin compensation? Recursive tasks? Timeout?<br>
**Red flags:** Chỉ tăng max threads.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `CON-01` active: tạo saturation/cancellation/context experiments với owned executors. Stable IDs không tái sử dụng.

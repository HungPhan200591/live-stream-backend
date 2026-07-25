# Reactive Interview Question Bank — Reactive Streams, WebFlux and Virtual-Thread Decision

> Status: `DRAFT`<br>
> Domain owner: `Reactive/Execution Model`<br>
> Active slice: `NONE`; preview target: `REACT-01`<br>
> Related roadmap: [Stage 11 extensions](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-11---solution-architecture-capstones)<br>
> Related depth rubric: [Reactive/WebFlux](../../../knowledge-depth-rubric.md#323-reactive-programming-và-webflux--p2-target-d1-d2)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/reactive/theory/core/reactive-streams-webflux-and-virtual-thread-decision.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `REACT-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `REACT-MODEL-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### REACT-MODEL-001 — `FOUNDATION`
**Question:** Blocking, non-blocking, asynchronous và reactive khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Thread wait, completion timing and demand-aware composition.<br>
**Answer outline:** Blocking holds caller thread; non-blocking returns without waiting; async completes later; reactive models streams/composition with signals/backpressure. They overlap but are not synonyms.<br>
**Required trade-offs:** Non-blocking scalability vs programming/debug complexity.<br>
**Follow-up ladder:** Event loop? Callback?<br>
**Red flags:** CompletableFuture automatically makes blocking I/O non-blocking.<br>
**Evidence:** Theory `NOT CREATED`; case `REACT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REACT-MODEL-002 — `FOUNDATION`
**Question:** `Publisher`, `Subscriber`, `Subscription` và backpressure trong Reactive Streams là gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Demand protocol and signal rules.<br>
**Answer outline:** Subscriber subscribes, requests n via Subscription; Publisher emits at most demand then complete/error, cancellation stops interest. Backpressure controls producer-to-consumer flow where source supports it.<br>
**Required trade-offs:** Demand protects buffers but external push source may still need drop/boundary.<br>
**Follow-up ladder:** Processor? Cold/hot publisher?<br>
**Red flags:** Backpressure làm producer database tự chậm mọi lúc.<br>
**Evidence:** Theory `NOT CREATED`; case `REACT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REACT-MODEL-003 — `FOUNDATION`
**Question:** `Mono` và `Flux` khác nhau; lazy execution có hệ quả gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** 0..1 vs 0..N and subscription triggers work.<br>
**Answer outline:** Pipelines describe work; subscription triggers cold source. Multiple subscriptions can repeat side effects unless shared/cached intentionally; errors are signals.<br>
**Required trade-offs:** Laziness/composability vs surprising duplicate work.<br>
**Follow-up ladder:** Hot source? `cache()` TTL?<br>
**Red flags:** Gọi method trả Mono đã thực hiện DB write.<br>
**Evidence:** Theory `NOT CREATED`; case `REACT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REACT-MODEL-004 — `FOUNDATION`
**Question:** Spring MVC, MVC + virtual threads và WebFlux phù hợp workload nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Programming model, blocking dependencies and concurrency.<br>
**Answer outline:** MVC platform threads simplest for moderate blocking; virtual threads keep imperative style for high blocking concurrency if pinning/resource bounds handled; WebFlux fits end-to-end non-blocking streaming/high concurrency and reactive ecosystem.<br>
**Required trade-offs:** Throughput/threads vs complexity/observability/library compatibility.<br>
**Follow-up ladder:** CPU-bound? SSE?<br>
**Red flags:** WebFlux luôn nhanh hơn MVC.<br>
**Evidence:** Theory `NOT CREATED`; case `REACT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REACT-MODEL-005 — `SENIOR`
**Question:** Blocking call trên event-loop gây starvation thế nào và phát hiện ra sao?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Few event-loop threads and queueing.<br>
**Answer outline:** A JDBC/file/remote blocking call occupies event loop, delaying unrelated requests; detect thread names/JFR/block detector/latency under concurrency, isolate on bounded scheduler or choose blocking model.<br>
**Required trade-offs:** Offload prevents loop block but adds queues/context switches and doesn't make dependency scalable.<br>
**Follow-up ladder:** `boundedElastic` limits?<br>
**Red flags:** Wrap bằng `Mono.just` là non-blocking.<br>
**Evidence:** Theory `NOT CREATED`; case `REACT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REACT-MODEL-006 — `SENIOR`
**Question:** Context, transaction và security propagation trong reactive chain khác ThreadLocal thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Execution hops and Reactor Context.<br>
**Answer outline:** Signals may run different threads, so context travels in Reactor Context and reactive transaction manager binds to subscription; imperative ThreadLocal/JPA transaction assumptions fail.<br>
**Required trade-offs:** Explicit context robust but library integration/debug harder.<br>
**Follow-up ladder:** MDC bridge?<br>
**Red flags:** `@Transactional` imperative hoạt động giống nhau trên reactive repository.<br>
**Evidence:** Theory `NOT CREATED`; case `REACT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REACT-MODEL-007 — `SENIOR`
**Question:** Backpressure, buffering, dropping và latest/coalescing chọn theo message semantics thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Overload policy, not operator memorization.<br>
**Answer outline:** Bound buffer for absorbable burst, drop/sample/coalesce telemetry/presence, error/disconnect for critical stream or persist/replay; observe queue age and cancellation.<br>
**Required trade-offs:** Lossless requires storage/latency; drop preserves liveness but loses events.<br>
**Follow-up ladder:** `onBackpressureBuffer` risk?<br>
**Red flags:** Unbounded buffer là lossless backpressure.<br>
**Evidence:** Theory `NOT CREATED`; case `REACT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REACT-MODEL-008 — `SENIOR`
**Question:** Benchmark MVC/virtual threads/WebFlux công bằng cần kiểm soát gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Same business/I/O, pools, workload and diagnostics.<br>
**Answer outline:** Same endpoint/data/dependency, tune bounded connections, warmup, open-load model, measure throughput/p99/CPU/memory/threads/GC and JFR pinning/event-loop block; include code/ops complexity.<br>
**Required trade-offs:** Microbenchmark may not represent ecosystem/production.<br>
**Follow-up ladder:** Coordinated omission?<br>
**Red flags:** So sánh hello-world RPS rồi chọn toàn platform.<br>
**Evidence:** Theory `NOT CREATED`; case `REACT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REACT-MODEL-009 — `ARCHITECT`
**Question:** Chọn execution model cho nhiều modules/services và governance ra sao?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Avoid accidental mixed model and define boundaries.<br>
**Answer outline:** Default simplest imperative model; reactive only for justified streaming/non-blocking path, adapters at explicit edges, library compatibility and observability standards; virtual thread rollout by workload/pinning evidence.<br>
**Required trade-offs:** Standardization reduces cognitive load but may deny local optimization.<br>
**Follow-up ladder:** R2DBC vs JDBC?<br>
**Red flags:** Một app trộn mọi model tự do để tối ưu từng method.<br>
**Evidence:** Theory `NOT CREATED`; case `REACT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### REACT-MODEL-010 — `EXPERT`
**Question:** Reactive pipeline vẫn OOM dù có backpressure: tìm hidden unbounded boundary thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Operator queues, hot source, network broker and slow sink.<br>
**Answer outline:** Trace demand/operators/schedulers, inspect buffers/groupBy/window/flatMap concurrency, bridge that ignores demand and broker prefetch; bound/drop/persist or redesign, then load/fault test cancellation.<br>
**Required trade-offs:** Tighter bounds may reject/drop and change semantics.<br>
**Follow-up ladder:** Fusion? `publishOn` prefetch?<br>
**Red flags:** Reactive Streams interface bảo đảm không bao giờ buffer vô hạn.<br>
**Evidence:** Theory `NOT CREATED`; case `REACT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `REACT-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.

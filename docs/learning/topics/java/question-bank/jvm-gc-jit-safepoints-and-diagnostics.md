# JVM Interview Question Bank — GC, JIT, Safepoints and Diagnostics

> Status: `DRAFT`<br>
> Domain owner: `JVM diagnostics`<br>
> Active slice: `NONE`; preview target `JVM-01`<br>
> Runtime baseline: `Java 21`<br>
> Related roadmap: [Stage 1](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-1---java-core-state-và-concurrency)<br>
> Related depth rubric: [JVM runtime](../../../knowledge-depth-rubric.md#33-jvm-runtime-và-diagnostics--p0-target-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/java/theory/deep-dives/jvm-gc-jit-safepoints-and-diagnostics.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `JVM-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Slice | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| GC/JIT core | 4 | 2 | 0 | 0 |
| Diagnostics/selection | 0 | 2 | 1 | 1 |
| **Tổng** | **4** | **4** | **1** | **1** |

## Recommended practice order

1. First pass: `JVM-DIAG-001` đến `JVM-DIAG-006`.
2. Senior follow-up: `JVM-DIAG-007`, `JVM-DIAG-008`.
3. Project application: `JVM-DIAG-006`, `JVM-DIAG-007`.
4. Stretch: `JVM-DIAG-009`, `JVM-DIAG-010`.

## Questions

### JVM-DIAG-001 — `FOUNDATION`
**Question:** GC làm gì và generational hypothesis là gì?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — JVM interview core.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Reachability/reclamation và đa số object ngắn sống.<br>
**Answer outline:** GC tìm live objects/reclaim unreachable memory; generational design tối ưu vì nhiều object chết trẻ, nhưng promotion/old live set vẫn quyết định cost.<br>
**Required trade-offs:** Throughput, pause, footprint và CPU không tối ưu đồng thời.<br>
**Follow-up ladder:** Young/old? Promotion? Humongous object?<br>
**Red flags:** GC định kỳ xóa mọi object cũ.<br>
**Evidence:** Theory `NOT CREATED`; case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-DIAG-002 — `FOUNDATION`
**Question:** G1 và ZGC khác mục tiêu chính nào trên Java 21?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — modern collector comparison phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Region-based balanced default vs low-pause concurrent collector.<br>
**Answer outline:** G1 cân bằng throughput/pause với regions và pause target; ZGC đẩy phần lớn work concurrent để pause rất thấp trên heap lớn, đổi CPU/footprint/operational profile. Chọn bằng SLO và workload.<br>
**Required trade-offs:** Low pause không đồng nghĩa throughput/cost tốt nhất.<br>
**Follow-up ladder:** Default collector? Heap size? Allocation rate?<br>
**Red flags:** ZGC luôn tốt hơn G1.<br>
**Evidence:** Theory `NOT CREATED`; case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-DIAG-003 — `FOUNDATION`
**Question:** Young collection, mixed/full collection và stop-the-world nên hiểu ra sao?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — GC vocabulary phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Collector-specific terminology và pause scope.<br>
**Answer outline:** Young/mixed/full mô tả vùng/live-set xử lý tùy collector; nhiều collector có concurrent phases nhưng vẫn có STW phases. Đọc đúng collector log thay vì áp CMS-era terms máy móc.<br>
**Required trade-offs:** Frequent short pause và rare long pause có user impact khác cùng average.<br>
**Follow-up ladder:** Concurrent marking? Evacuation failure? Allocation stall?<br>
**Red flags:** Concurrent collector nghĩa là không bao giờ STW.<br>
**Evidence:** Theory `NOT CREATED`; case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-DIAG-004 — `FOUNDATION`
**Question:** Interpreter, JIT, tiered compilation và warm-up liên hệ thế nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — JIT foundation thường gặp.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Profile-guided optimization/deoptimization và benchmark warm-up.<br>
**Answer outline:** Code bắt đầu interpreted/lower tier, runtime profile hot methods rồi compile/optimize; assumptions có thể deopt. Startup và steady-state khác, benchmark cần fork/warm-up đúng.<br>
**Required trade-offs:** Aggressive compilation tăng peak performance nhưng dùng CPU/code cache/startup time.<br>
**Follow-up ladder:** Inlining? OSR? Deoptimization?<br>
**Red flags:** Javac tạo native code tối ưu cuối cùng.<br>
**Evidence:** Theory `NOT CREATED`; case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-DIAG-005 — `SENIOR`
**Question:** Safepoint là gì và vì sao time-to-safepoint cũng gây latency?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — latency diagnostic follow-up.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Global VM operations, thread rendezvous và pause attribution.<br>
**Answer outline:** Safepoint là trạng thái threads có thể dừng an toàn cho VM operation; pause gồm chờ threads tới safepoint và operation time. Native/long loops/locks có thể ảnh hưởng; dùng logs/JFR để phân biệt.<br>
**Required trade-offs:** Tuning GC không sửa safepoint delay do code/native path.<br>
**Follow-up ladder:** Thread dump? Biased locking legacy? JNI?<br>
**Red flags:** Mọi STW pause đều là GC.<br>
**Evidence:** Theory `NOT CREATED`; case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-DIAG-006 — `SENIOR`
**Question:** Phân biệt memory leak, allocation pressure và heap quá nhỏ bằng evidence nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — production diagnosis phổ biến.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Live-set trend, allocation rate, post-GC floor và retaining path.<br>
**Answer outline:** Leak: post-GC live set tăng/retaining owner; pressure: allocation/GC rate cao nhưng floor ổn; heap nhỏ: working set gần limit/thrash. Dùng GC log/JFR/heap histogram/dump đúng thời điểm.<br>
**Required trade-offs:** Tăng heap giảm frequency nhưng có thể che leak/tăng footprint.<br>
**Follow-up ladder:** OOM dump? Native OOM? Promotion failure?<br>
**Red flags:** Thấy nhiều GC là kết luận memory leak.<br>
**Evidence:** Theory `NOT CREATED`; case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-DIAG-007 — `SENIOR`
**Question:** Khi nào dùng JFR, GC log, heap dump và thread dump?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — diagnostic workflow follow-up.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Tool-to-hypothesis mapping và capture cost.<br>
**Answer outline:** JFR cho timeline CPU/allocation/lock/I/O; GC log cho collector/live-set; heap dump cho retained graph; thread dump cho state/deadlock. Bắt baseline+incident và preserve timestamp/workload.<br>
**Required trade-offs:** Heap dump nặng/nhạy cảm; continuous low-overhead signals tốt hơn chờ incident rồi đoán.<br>
**Follow-up ladder:** Multiple thread dumps? PII? Jcmd?<br>
**Red flags:** Heap dump là công cụ đầu tiên cho mọi CPU spike.<br>
**Evidence:** Theory `NOT CREATED`; case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-DIAG-008 — `SENIOR`
**Question:** CPU spike do application, GC, JIT hay lock contention được tách thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — incident scenario.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Timeline correlation, samples và no-premature-tuning.<br>
**Answer outline:** Correlate host/container CPU, GC/JIT events, JFR method samples, allocation/lock and request load; reproduce/profile; identify dominant CPU owner before changing flags/code.<br>
**Required trade-offs:** Sampling ít overhead nhưng statistical; instrumentation chi tiết hơn nhưng perturbation/cost.<br>
**Follow-up ladder:** Warm-up spike? Compiler threads? Spin lock?<br>
**Red flags:** Tăng thread pool để xử lý CPU spike.<br>
**Evidence:** Theory `NOT CREATED`; case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-DIAG-009 — `ARCHITECT`
**Question:** Chọn collector và heap policy theo latency SLO/cost/capacity như thế nào?<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `LOW` — runtime architecture stretch.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Workload experiment, headroom và rollback.<br>
**Answer outline:** Pin workload/heap/allocation/live set/container CPU; compare p99/pause/throughput/RSS/cost on G1/ZGC; set guardrail/dashboard/canary/rollback; no flag cargo cult.<br>
**Required trade-offs:** Latency, throughput, footprint và team operability.<br>
**Follow-up ladder:** Burst? Autoscaling? JDK upgrade?<br>
**Red flags:** Collector choice chỉ theo heap size rule-of-thumb.<br>
**Evidence:** Theory `NOT CREATED`; case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-DIAG-010 — `EXPERT`
**Question:** Điều tra p99 regression không đổi average do safepoint/allocation/code-cache interaction.<br>
**Target depth:** `D4`<br>
**Interview likelihood:** `LOW` — pathological JVM discriminator.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Multi-signal causal timeline và falsifiable hypotheses.<br>
**Answer outline:** Align latency trace với JFR safepoint/GC/deopt/code-cache/allocation events; compare before/after; isolate workload/code path; test one change and retain raw evidence.<br>
**Required trade-offs:** Deep JVM tuning có maintenance/version cost; prefer code/workload fix when equivalent.<br>
**Follow-up ladder:** Coordinated omission? Deopt storm? Humongous allocation?<br>
**Red flags:** Dựa một flame graph không timestamp để kết luận.<br>
**Evidence:** Theory `NOT CREATED`; case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `JVM-01` active: tạo reproducible workload và raw JFR/GC/dump evidence. Stable IDs không tái sử dụng.

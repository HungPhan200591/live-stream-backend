# Operations Interview Question Bank — Linux, JVM Container Runtime and Resource Limits

> Status: `DRAFT`<br>
> Domain owner: `Runtime/Containers`<br>
> Active slice: `NONE`; preview target: `OPS-01`<br>
> Related roadmap: [Stage 8](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-8---observability-testing-runtime-và-delivery-engineering)<br>
> Related depth rubric: [Runtime/delivery](../../../knowledge-depth-rubric.md#318-git-linux-container-build-và-cicd--p1-target-d2-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/operations/theory/core/linux-jvm-container-runtime-and-resource-limits.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `OPS-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `OPS-RUNTIME-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### OPS-RUNTIME-001 — `FOUNDATION`
**Question:** Process, thread, file descriptor, socket và signal liên hệ thế nào trong Java service?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** OS resource model và JVM mapping.<br>
**Answer outline:** JVM là process có threads; sockets/files consume descriptors; signals điều khiển terminate/dump; limits exhaustion gây accept/open failures dù CPU thấp.<br>
**Required trade-offs:** Tăng limits hỗ trợ scale nhưng che leak/capacity issue.<br>
**Follow-up ladder:** PID 1? `SIGTERM` vs `SIGKILL`?<br>
**Red flags:** Java abstraction làm OS limits không còn quan trọng.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-002 — `FOUNDATION`
**Question:** Container image, container và virtual machine khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Shared kernel, filesystem layers và isolation.<br>
**Answer outline:** Image immutable template/layers; container là isolated process namespaces/cgroups dùng host kernel; VM có guest kernel. Container không phải security boundary tuyệt đối.<br>
**Required trade-offs:** Containers nhẹ/nhanh nhưng kernel shared và config/runtime discipline cao.<br>
**Follow-up ladder:** Rootless? Distroless?<br>
**Red flags:** Container là mini VM.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-003 — `FOUNDATION`
**Question:** CPU request/limit và memory request/limit ảnh hưởng JVM ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Scheduling guarantee, throttling và OOM kill.<br>
**Answer outline:** Request dùng scheduling/capacity, CPU limit có CFS throttling tăng latency, memory limit hard cap có OOM kill; JVM container-aware sizing nhưng native/direct/metaspace/thread stacks cũng ăn memory.<br>
**Required trade-offs:** Headroom giảm density/cost nhưng bảo vệ tail/recovery.<br>
**Follow-up ladder:** Heap percentage? Swap?<br>
**Red flags:** Set Xmx bằng memory limit là tối ưu.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-004 — `FOUNDATION`
**Question:** Startup, readiness và liveness probe khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Boot allowance, traffic eligibility và deadlock recovery.<br>
**Answer outline:** Startup bảo vệ slow boot; readiness quyết định nhận traffic/dependency policy; liveness chỉ restart khi process unrecoverable. Liveness không nên phụ thuộc transient downstream.<br>
**Required trade-offs:** Probe nhạy phục hồi nhanh nhưng dễ restart storm.<br>
**Follow-up ladder:** Grace period? Readiness on DB?<br>
**Red flags:** Một `/health` endpoint dùng giống nhau cho cả ba.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-005 — `SENIOR`
**Question:** Graceful shutdown/drain HTTP và message consumer cần thứ tự gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Stop admission, finish/timeout work và release resources.<br>
**Answer outline:** Mark unready, stop new requests/deliveries, drain inflight within budget, ack/commit only durable work, close pools then exit before orchestrator grace expires; idempotency handles forced kill.<br>
**Required trade-offs:** Drain dài giảm loss nhưng chậm rollout/capacity release.<br>
**Follow-up ladder:** WebSocket drain? SIGTERM hook?<br>
**Red flags:** Gọi `System.exit` khi nhận SIGTERM.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-006 — `SENIOR`
**Question:** Chẩn đoán CPU throttling, OOM kill và native-memory pressure thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** cgroup vs JVM metrics và evidence.<br>
**Answer outline:** Correlate container throttled periods/working set/OOM reason with GC/JFR/thread dump/NMT, heap/direct/metaspace/thread count; distinguish heap OOME from kernel kill and leak from limit sizing.<br>
**Required trade-offs:** Diagnostic detail may add overhead; capture before restart when safe.<br>
**Follow-up ladder:** Exit code 137? RSS vs heap?<br>
**Red flags:** Heap dump luôn có khi container OOMKilled.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-007 — `SENIOR`
**Question:** File descriptor/socket exhaustion biểu hiện và điều tra ra sao?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Accept/connect failures, leak, TIME_WAIT và limits.<br>
**Answer outline:** Check process limits/open descriptors by type, connection states, pool/HTTP keepalive, DNS and leak paths; bound clients, close resources and capacity-test rather than only raise `ulimit`.<br>
**Required trade-offs:** Keepalive reduces handshakes but holds descriptors.<br>
**Follow-up ladder:** Ephemeral ports? CLOSE_WAIT?<br>
**Red flags:** Nhiều TIME_WAIT luôn là leak.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-008 — `SENIOR`
**Question:** Minimal non-root image và immutable configuration có lợi/rủi ro gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Attack surface, reproducibility và debug operability.<br>
**Answer outline:** Pin trusted base/digest, multi-stage build, non-root/read-only filesystem, no shell/package manager when possible, config/secrets external and fail-fast; provide ephemeral debug path/runbook.<br>
**Required trade-offs:** Distroless lowers surface but incident tooling harder.<br>
**Follow-up ladder:** CA cert/timezone? UID permissions?<br>
**Red flags:** Alpine/distroless tự động an toàn và nhỏ nhất cho JVM.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-009 — `ARCHITECT`
**Question:** Lập capacity/headroom policy cho containerized JVM fleet thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Workload resource curves, bin packing và failure reserve.<br>
**Answer outline:** Benchmark CPU/memory/native/FD per concurrency, set requests from normal percentile and limits with burst/headroom, reserve node/zone failure, autoscale on leading saturation not CPU only.<br>
**Required trade-offs:** Utilization cao giảm cost nhưng giảm recovery headroom.<br>
**Follow-up ladder:** VPA/HPA? Noisy neighbor?<br>
**Red flags:** Target 100% CPU để tận dụng hạ tầng.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-010 — `EXPERT`
**Question:** Pod restart loop dưới memory pressure nhưng heap trông ổn: dẫn investigation thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Cross-layer native allocation/cgroup/probe feedback.<br>
**Answer outline:** Inspect termination reason/events, RSS/cgroup, direct buffers, metaspace, thread stacks, libc/JIT/code cache and sidecars; probe timeout under throttling may compound. Reproduce load, set budgets and fix owner.<br>
**Required trade-offs:** Increase limit buys time but may move node pressure.<br>
**Follow-up ladder:** NMT unavailable? Kernel pressure stall?<br>
**Red flags:** Heap usage thấp loại trừ memory issue.<br>
**Evidence:** Theory `NOT CREATED`; case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `OPS-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.


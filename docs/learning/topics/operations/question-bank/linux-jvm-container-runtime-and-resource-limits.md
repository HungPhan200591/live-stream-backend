# Operations Interview Question Bank — Linux, JVM Container Runtime and Resource Limits

> Status: `DRAFT`<br>
> Domain owner: `Runtime/Containers`<br>
> Active slice: `NONE`; preview target: `OPS-01`<br>
> Related roadmap: [Stage 8](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-8---observability-testing-runtime-và-delivery-engineering)<br>
> Related depth rubric: [Runtime/delivery](../../../knowledge-depth-rubric.md#318-git-linux-container-build-và-cicd--p1-target-d2-d3)<br>
> Related theory: [Core](../theory/core/linux-jvm-container-runtime-and-resource-limits.md) · [Deep-dive](../theory/deep-dives/cgroup-memory-native-jvm-and-graceful-drain-failures.md)<br>
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
**Evidence:** Theory [core](../theory/core/linux-jvm-container-runtime-and-resource-limits.md) + [deep-dive](../theory/deep-dives/cgroup-memory-native-jvm-and-graceful-drain-failures.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-002 — `FOUNDATION`
**Question:** Container image, container và virtual machine khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Shared kernel, filesystem layers và isolation.<br>
**Answer outline:** Image immutable template/layers; container là isolated process namespaces/cgroups dùng host kernel; VM có guest kernel. Container không phải security boundary tuyệt đối.<br>
**Required trade-offs:** Containers nhẹ/nhanh nhưng kernel shared và config/runtime discipline cao.<br>
**Follow-up ladder:** Rootless? Distroless?<br>
**Red flags:** Container là mini VM.<br>
**Evidence:** Theory [core](../theory/core/linux-jvm-container-runtime-and-resource-limits.md) + [deep-dive](../theory/deep-dives/cgroup-memory-native-jvm-and-graceful-drain-failures.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-003 — `FOUNDATION`
**Question:** CPU request/limit và memory request/limit ảnh hưởng JVM ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Scheduling guarantee, throttling và OOM kill.<br>
**Answer outline:** Request dùng scheduling/capacity, CPU limit có CFS throttling tăng latency, memory limit hard cap có OOM kill; JVM container-aware sizing nhưng native/direct/metaspace/thread stacks cũng ăn memory.<br>
**Required trade-offs:** Headroom giảm density/cost nhưng bảo vệ tail/recovery.<br>
**Follow-up ladder:** Heap percentage? Swap?<br>
**Red flags:** Set Xmx bằng memory limit là tối ưu.<br>
**Evidence:** Theory [core](../theory/core/linux-jvm-container-runtime-and-resource-limits.md) + [deep-dive](../theory/deep-dives/cgroup-memory-native-jvm-and-graceful-drain-failures.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-004 — `FOUNDATION`
**Question:** Startup, readiness và liveness probe khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Boot allowance, traffic eligibility và deadlock recovery.<br>
**Answer outline:** Startup bảo vệ slow boot; readiness quyết định nhận traffic/dependency policy; liveness chỉ restart khi process unrecoverable. Liveness không nên phụ thuộc transient downstream.<br>
**Required trade-offs:** Probe nhạy phục hồi nhanh nhưng dễ restart storm.<br>
**Follow-up ladder:** Grace period? Readiness on DB?<br>
**Red flags:** Một `/health` endpoint dùng giống nhau cho cả ba.<br>
**Evidence:** Theory [core](../theory/core/linux-jvm-container-runtime-and-resource-limits.md) + [deep-dive](../theory/deep-dives/cgroup-memory-native-jvm-and-graceful-drain-failures.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-005 — `SENIOR`
**Question:** Graceful shutdown/drain HTTP và message consumer cần thứ tự gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Stop admission, finish/timeout work và release resources.<br>
**Answer outline:** Đánh dấu unready, dừng request/delivery mới, drain inflight trong budget, chỉ ack/commit công việc đã bền vững, đóng pool rồi exit trước khi grace period hết; idempotency xử lý trường hợp bị kill cưỡng bức.<br>
**Required trade-offs:** Drain dài giảm loss nhưng chậm rollout/capacity release.<br>
**Follow-up ladder:** WebSocket drain? SIGTERM hook?<br>
**Red flags:** Gọi `System.exit` khi nhận SIGTERM.<br>
**Evidence:** Theory [core](../theory/core/linux-jvm-container-runtime-and-resource-limits.md) + [deep-dive](../theory/deep-dives/cgroup-memory-native-jvm-and-graceful-drain-failures.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-006 — `SENIOR`
**Question:** Chẩn đoán CPU throttling, OOM kill và native-memory pressure thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** cgroup vs JVM metrics và evidence.<br>
**Answer outline:** Đối chiếu thời gian container bị throttle, working set và lý do OOM với GC/JFR/thread dump/NMT, heap/direct/metaspace và thread count; phân biệt heap OOME với kernel kill, memory leak với limit đặt sai.<br>
**Required trade-offs:** Thu diagnostic chi tiết có overhead; nên capture trước restart khi an toàn.<br>
**Follow-up ladder:** Exit code 137? RSS vs heap?<br>
**Red flags:** Heap dump luôn có khi container OOMKilled.<br>
**Evidence:** Theory [core](../theory/core/linux-jvm-container-runtime-and-resource-limits.md) + [deep-dive](../theory/deep-dives/cgroup-memory-native-jvm-and-graceful-drain-failures.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-007 — `SENIOR`
**Question:** File descriptor/socket exhaustion biểu hiện và điều tra ra sao?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Accept/connect failures, leak, TIME_WAIT và limits.<br>
**Answer outline:** Kiểm process limit và open descriptor theo loại, trạng thái connection, pool/HTTP keepalive, DNS và đường leak; đặt bound cho client, đóng resource và capacity test thay vì chỉ tăng `ulimit`.<br>
**Required trade-offs:** Keepalive reduces handshakes but holds descriptors.<br>
**Follow-up ladder:** Ephemeral ports? CLOSE_WAIT?<br>
**Red flags:** Nhiều TIME_WAIT luôn là leak.<br>
**Evidence:** Theory [core](../theory/core/linux-jvm-container-runtime-and-resource-limits.md) + [deep-dive](../theory/deep-dives/cgroup-memory-native-jvm-and-graceful-drain-failures.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-008 — `SENIOR`
**Question:** Minimal non-root image và immutable configuration có lợi/rủi ro gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Attack surface, reproducibility và debug operability.<br>
**Answer outline:** Pin base image/digest đáng tin, dùng multi-stage build, non-root/read-only filesystem, bỏ shell/package manager khi có thể; để config/secret bên ngoài và fail-fast; cung cấp đường debug tạm thời cùng runbook.<br>
**Required trade-offs:** Distroless lowers surface but incident tooling harder.<br>
**Follow-up ladder:** CA cert/timezone? UID permissions?<br>
**Red flags:** Alpine/distroless tự động an toàn và nhỏ nhất cho JVM.<br>
**Evidence:** Theory [core](../theory/core/linux-jvm-container-runtime-and-resource-limits.md) + [deep-dive](../theory/deep-dives/cgroup-memory-native-jvm-and-graceful-drain-failures.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-009 — `ARCHITECT`
**Question:** Lập capacity/headroom policy cho containerized JVM fleet thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Workload resource curves, bin packing và failure reserve.<br>
**Answer outline:** Benchmark CPU, heap/native memory và file descriptor theo concurrency; đặt resource request từ percentile tải bình thường và limit có burst/headroom; dự phòng lỗi node/zone; autoscale theo tín hiệu saturation dẫn trước chứ không chỉ CPU.<br>
**Required trade-offs:** Utilization cao giảm cost nhưng giảm recovery headroom.<br>
**Follow-up ladder:** VPA/HPA? Noisy neighbor?<br>
**Red flags:** Target 100% CPU để tận dụng hạ tầng.<br>
**Evidence:** Theory [core](../theory/core/linux-jvm-container-runtime-and-resource-limits.md) + [deep-dive](../theory/deep-dives/cgroup-memory-native-jvm-and-graceful-drain-failures.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OPS-RUNTIME-010 — `EXPERT`
**Question:** Pod restart loop dưới memory pressure nhưng heap trông ổn: dẫn investigation thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Cross-layer native allocation/cgroup/probe feedback.<br>
**Answer outline:** Kiểm termination reason/event, RSS/cgroup, direct buffer, metaspace, thread stack, libc/JIT/code cache và sidecar; probe timeout dưới throttling có thể làm lỗi nặng hơn. Tái hiện tải, đặt budget và sửa đúng owner.<br>
**Required trade-offs:** Increase limit buys time but may move node pressure.<br>
**Follow-up ladder:** NMT unavailable? Kernel pressure stall?<br>
**Red flags:** Heap usage thấp loại trừ memory issue.<br>
**Evidence:** Theory [core](../theory/core/linux-jvm-container-runtime-and-resource-limits.md) + [deep-dive](../theory/deep-dives/cgroup-memory-native-jvm-and-graceful-drain-failures.md); case `OPS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `OPS-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.

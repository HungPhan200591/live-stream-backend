# Java Interview Question Bank — JMM, Synchronization and Thread Safety

> Status: `DRAFT`<br>
> Domain owner: `Java concurrency / JMM`<br>
> Active slice: `NONE`; preview target `CON-01`<br>
> Runtime baseline: `Java 21`<br>
> Related roadmap: [Stage 1](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-1---java-core-state-và-concurrency)<br>
> Related depth rubric: [Concurrency/JMM](../../../knowledge-depth-rubric.md#34-concurrency-jmm-và-async-model--p0-target-d3)<br>
> Related theory: [JMM, Synchronization and Thread Safety](../theory/core/jmm-synchronization-and-thread-safety.md), [happens-before deep-dive](../theory/deep-dives/jmm-happens-before-publication-and-locking.md) — `DRAFT`, evidence `NOT RUN`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `CON-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Slice | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| JMM/synchronization core | 4 | 2 | 0 | 0 |
| Failure/design | 0 | 2 | 1 | 1 |
| **Tổng** | **4** | **4** | **1** | **1** |

## Recommended practice order

1. First pass: `CON-JMM-001` đến `CON-JMM-006`.
2. Senior follow-up: `CON-JMM-007`, `CON-JMM-008`.
3. Project application: `CON-JMM-007`.
4. Stretch: `CON-JMM-009`, `CON-JMM-010`.

## Questions

### CON-JMM-001 — `FOUNDATION`
**Question:** Concurrency và parallelism khác nhau thế nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — concurrency foundation phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Overlapping progress khác simultaneous execution.<br>
**Answer outline:** Concurrency quản lý nhiều tasks tiến triển chồng lấn; parallelism chạy đồng thời trên nhiều cores. Concurrent design vẫn có race dù một core; parallelism không tự tăng throughput khi bottleneck khác.<br>
**Required trade-offs:** Concurrency tăng responsiveness/utilization nhưng thêm ordering/state complexity.<br>
**Follow-up ladder:** Async có parallel không? CPU vs I/O?<br>
**Red flags:** Nhiều thread luôn chạy song song.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-JMM-002 — `FOUNDATION`
**Question:** Race condition và data race khác nhau thế nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — thread-safety core.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Outcome phụ thuộc interleaving và unsynchronized conflicting accesses.<br>
**Answer outline:** Race condition rộng: correctness phụ thuộc timing/order; data race là conflicting memory accesses không có happens-before và có write. Atomic operations riêng lẻ vẫn có compound race.<br>
**Required trade-offs:** Loại shared mutation đơn giản hơn thêm lock.<br>
**Follow-up ladder:** Check-then-act? Lost update? Immutable state?<br>
**Red flags:** Không thấy exception nghĩa là thread-safe.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-JMM-003 — `FOUNDATION`
**Question:** Atomicity, visibility và ordering khác nhau thế nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — JMM question phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Không đồng nhất three properties.<br>
**Answer outline:** Atomicity là indivisible operation; visibility là thread thấy write; ordering là constraints trên reorder/observation. `volatile` giúp visibility/order nhưng không biến `count++` atomic.<br>
**Required trade-offs:** Strong synchronization dễ reasoning nhưng contention; weaker primitives cần invariant hẹp.<br>
**Follow-up ladder:** 64-bit read? CAS? Reordering example?<br>
**Red flags:** Atomic variable làm toàn object thread-safe.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-JMM-004 — `FOUNDATION`
**Question:** Happens-before là gì? Nêu các cạnh phổ biến.<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — Senior JMM core.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Visibility/order guarantee qua program order, monitor, volatile, start/join.<br>
**Answer outline:** Nếu A happens-before B thì effects A visible/ordered trước B; edges gồm unlock→subsequent lock cùng monitor, volatile write→read, start và join, cộng transitivity.<br>
**Required trade-offs:** Guarantee phải được chỉ ra bằng edge cụ thể, không dựa “thường chạy đúng”.<br>
**Follow-up ladder:** Final field publication? Executor submission?<br>
**Red flags:** Happens-before là thứ tự wall-clock tuyệt đối.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-JMM-005 — `SENIOR`
**Question:** `volatile` khác `synchronized` thế nào và khi nào volatile đủ?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — concurrency interview classic.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Visibility/order vs mutual exclusion/compound invariant.<br>
**Answer outline:** Volatile read/write có visibility/order nhưng không mutual exclusion; đủ cho independent flag/single-writer publication; synchronized bảo vệ compound state và tạo monitor HB edge.<br>
**Required trade-offs:** Volatile nhẹ nhưng dễ dùng sai; lock rõ invariant nhưng contention/blocking.<br>
**Follow-up ladder:** Volatile counter? Double-check locking? AtomicReference?<br>
**Red flags:** Volatile là lock không blocking.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-JMM-006 — `SENIOR`
**Question:** `synchronized` lock trên object nào, có reentrant không và static method khác gì?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — synchronization mechanics phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Instance monitor, Class monitor, scope và reentrancy.<br>
**Answer outline:** Instance synchronized lock `this`; static lock Class object; block lock expression; monitor reentrant. Correctness cần mọi access cùng invariant dùng cùng lock.<br>
**Required trade-offs:** Coarse lock đơn giản nhưng contention; fine locks tăng deadlock/maintenance risk.<br>
**Follow-up ladder:** Null lock? Lock release on exception? Wait/notify?<br>
**Red flags:** Hai synchronized methods ở hai instances loại trừ nhau.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-JMM-007 — `SENIOR`
**Question:** Bảo vệ transition `CREATED -> LIVE -> ENDED` trước hai webhook đồng thời bằng cách nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — project concurrency application.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Invariant, process-local vs database coordination và race test.<br>
**Answer outline:** Define allowed transition; conditional DB update/version/lock là authority xuyên instances; Java lock chỉ local; duplicate/out-of-order idempotency; barrier integration test chứng minh one transition.<br>
**Required trade-offs:** Pessimistic lock dễ order nhưng contend; optimistic/conditional update cần conflict handling.<br>
**Follow-up ladder:** Redis side effect? Retry? Multiple instances?<br>
**Red flags:** `synchronized` controller đủ cho cluster.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-JMM-008 — `SENIOR`
**Question:** Deadlock, livelock và starvation khác nhau; chẩn đoán bằng gì?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — production follow-up.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Wait cycle, active-no-progress và unfair denial.<br>
**Answer outline:** Deadlock threads chờ cycle; livelock đổi state nhưng không tiến; starvation không được resource. Dùng repeated thread dumps/JFR lock events/metrics, lock ordering và bounded retry/backoff.<br>
**Required trade-offs:** Fairness giảm starvation nhưng có throughput cost.<br>
**Follow-up ladder:** Database deadlock? Timed lock? Detection?<br>
**Red flags:** Tăng threads chữa deadlock.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-JMM-009 — `ARCHITECT`
**Question:** Chọn immutability, confinement, CAS, lock hay database primitive theo tiêu chí nào?<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `LOW` — design stretch.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Scope of coordination, contention, fairness và failure domain.<br>
**Answer outline:** Prefer no sharing/immutable/confinement; CAS cho narrow state/low contention; lock cho compound local invariant; DB conditional/lock cho durable multi-instance invariant; measure conflicts/latency.<br>
**Required trade-offs:** Lock-free không tự simpler/fair; distributed coordination đắt hơn local.<br>
**Follow-up ladder:** ABA? Fencing? Hot key?<br>
**Red flags:** Một primitive tốt nhất cho mọi invariant.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### CON-JMM-010 — `EXPERT`
**Question:** Safe publication và double-checked locking sai có thể tạo object “nửa khởi tạo” thế nào?<br>
**Target depth:** `D4`<br>
**Interview likelihood:** `LOW` — JMM discriminator.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Reordering, volatile reference và final-field semantics.<br>
**Answer outline:** Không có HB, reference có thể visible trước initialization effects; DCL cần volatile reference hoặc holder/static init; prefer DI/eager safe publication and prove edge with jcstress-style test.<br>
**Required trade-offs:** Lazy singleton complexity hiếm đáng giá so với container lifecycle.<br>
**Follow-up ladder:** Final fields? This escape? Constructor thread start?<br>
**Red flags:** DCL an toàn chỉ vì inner null check.<br>
**Evidence:** Theory `NOT CREATED`; case `CON-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `CON-01` active: tạo happens-before timeline và repeatable race tests. Stable IDs không tái sử dụng.

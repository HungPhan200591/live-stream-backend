# Java Interview Question Bank

> Status: `DRAFT`<br>
> Domain owner: `java / JVM / concurrency`<br>
> Active slice: `JDK-01 — Java 21 platform baseline and virtual-thread decision`<br>
> Related theory: [Java 21 platform baseline](../theory/core/java21-platform-baseline.md), [Virtual threads and pinning](../theory/deep-dives/virtual-threads-and-pinning.md)<br>
> Updated: `2026-07-25`

Question bank chứa câu hỏi và evaluation rubric, không chứa bài luận hoàn chỉnh. `Answer outline` chỉ là chuẩn chấm; người học phải trả lời trước khi mở outline. Tests/experiment và interview note của JDK-01 vẫn chưa tồn tại, nên mọi câu giữ `UNANSWERED` hoặc `NEEDS_WORK`, không được đánh dấu `EVIDENCE_BACKED`.

## Level rubric

| Level | Trọng tâm |
| --- | --- |
| `FOUNDATION` | Vocabulary, distinction và mechanism cơ bản |
| `SENIOR` | Failure mode, diagnosis, migration/test gate và trade-off |
| `ARCHITECT` | Platform evolution, capacity, operations, ownership và rollback |
| `EXPERT` | Cross-layer causal chain, pathological workload, version boundary và experiment design |

Level nằm trên từng câu hỏi; không chia folder theo level.

## Coverage

| Topic | Foundation | Senior | Architect | Expert | Theory source |
| --- | ---: | ---: | ---: | ---: | --- |
| Platform/toolchain/migration | 1 | 2 | 1 | 0 | [Core](../theory/core/java21-platform-baseline.md) |
| Feature lifecycle | 1 | 0 | 0 | 0 | [Core §3.2](../theory/core/java21-platform-baseline.md#32-final-preview-và-incubator-là-ba-compatibility-contract-khác-nhau) |
| Virtual-thread execution/capacity | 1 | 2 | 1 | 1 | [Deep-dive](../theory/deep-dives/virtual-threads-and-pinning.md) |
| Pinning/version/diagnostics | 0 | 1 | 0 | 1 | [Deep-dive §3.4-3.5](../theory/deep-dives/virtual-threads-and-pinning.md#34-pinning-trên-java-21) |
| **Tổng** | **3** | **5** | **2** | **2** | 12 questions |

## Questions

### JAVA-JDK-001 — `FOUNDATION`

**Question:**

Một project ghi `<java.version>21</java.version>` đã đủ để kết luận build và production đều dùng Java 21 chưa? Hãy phân biệt build JDK, compiler release và runtime JDK.

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có nhìn platform như nhiều lớp hay chỉ nhớ property trong POM.

**Answer outline:**

1. Property là configuration intent, không chọn host JDK hoặc runtime image một cách tự động.
2. Build JDK chạy Maven/plugins; `--release` khóa language/API/class-file contract; runtime JDK thực thi artifact.
3. Cần version evidence ở local/CI/runtime và compatibility của framework/plugins.

**Required trade-offs:**

- Toolchain giúp selection nhưng không thay runtime pinning hay tests.

**Follow-up ladder:**

- Foundation: `java -version` và Maven version trả lời hai câu hỏi nào?
- Senior: Drift nào có thể pass local nhưng fail CI?
- Architect: Pin vendor/patch tới mức nào?
- Expert: Agent/JNI/internal API làm compatibility matrix thay đổi ra sao?

**Red flags:**

- “Có `<java.version>` là đủ”; đồng nhất source compatibility với runtime compatibility.

**Evidence:**

- Theory: [Core §3.1](../theory/core/java21-platform-baseline.md#31-platform-baseline-là-một-chuỗi-không-phải-một-con-số-trong-pom)
- Learning case: [JDK-01](../../../cases/jdk-01-java21-platform-baseline.md)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK-002 — `FOUNDATION`

**Question:**

Final, preview và incubator feature khác nhau thế nào? Java 21 là LTS có làm mọi feature trong release trở thành production-ready không?

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có hiểu feature lifecycle và flag/module contract thay vì dùng “LTS” như nhãn an toàn tuyệt đối.

**Answer outline:**

1. Final đã delivered; preview cần `--enable-preview` ở compile/runtime và có thể đổi; incubator có contract thử nghiệm/module riêng.
2. LTS là support lifecycle của vendor, độc lập feature status và dependency compatibility.
3. Phân loại cụ thể: Virtual Threads final; Structured Concurrency/Scoped Values preview; Vector API incubator trong Java 21.

**Required trade-offs:**

- Preview có thể hữu ích cho lab nhưng tạo migration/operational cost nếu vào production.

**Follow-up ladder:**

- Foundation: Kể hai feature final của Java 21.
- Senior: Điều gì xảy ra nếu compile có preview flag nhưng runtime thiếu?
- Architect: Policy nào cho preview trong monorepo/multi-service?
- Expert: Làm sao loại preview API khỏi public contract?

**Red flags:**

- “LTS nghĩa là mọi API ổn định”; không biết preview cần runtime flag.

**Evidence:**

- Theory: [Core §3.2](../theory/core/java21-platform-baseline.md#32-final-preview-và-incubator-là-ba-compatibility-contract-khác-nhau)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK-003 — `FOUNDATION`

**Question:**

Virtual thread khác platform thread ở đâu, và carrier thread là gì?

**Target depth:** `D2`

**Interviewer evaluates:**

- Có giải thích được M:N scheduling/mount-unmount mà không nói “thread ảo chạy vô hạn”.

**Answer outline:**

1. Cả hai đều là `Thread`; platform thread thường giữ OS thread, virtual thread do JDK schedule lên carrier.
2. Virtual thread có thể unmount khi chờ blocking operation được hỗ trợ; carrier chạy task khác.
3. CPU parallelism vẫn bị giới hạn bởi core/carrier; lợi ích chính là concurrency I/O wait.

**Required trade-offs:**

- Sequential code/observability dễ hơn async pipeline, nhưng resource policy vẫn cần riêng.

**Follow-up ladder:**

- Foundation: Mount và unmount có terminate thread không?
- Senior: Workload nào không hưởng lợi?
- Architect: Carrier parallelism có nên tăng tùy ý?
- Expert: Scheduler saturation khác pinning thế nào?

**Red flags:**

- “Một virtual thread là một core”; “virtual thread làm code CPU nhanh hơn”.

**Evidence:**

- Theory: [Deep-dive §3.1](../theory/deep-dives/virtual-threads-and-pinning.md#31-virtual-thread-vẫn-là-javalangthread)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK-004 — `SENIOR`

**Question:**

Bạn sẽ nâng một Spring Boot service từ Java 17 lên Java 21 theo các gate nào để vừa cô lập regression vừa rollback được?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Migration thinking: inventory, baseline, compatibility, verification, rollout và rollback.

**Answer outline:**

1. Inventory JDK/Maven/plugins/dependencies/agents/container/CI; chụp current version/build/test/startup.
2. Pin Java 21 build + compiler release + runtime, không trộn framework major upgrade.
3. Clean compile, risk-based tests, startup/runtime checks, warning/agent/reflection/serialization review.
4. Canary/rehearsal theo maturity và giữ artifact/runtime rollback.
5. Virtual threads là experiment sau baseline, không phải acceptance criterion của migration.

**Required trade-offs:**

- Safety net hiện tại hẹp thì chỉ claim characterization M1, không claim full compatibility.

**Follow-up ladder:**

- Foundation: Baseline command nào cần lưu?
- Senior: Binary compatibility khác behavioral compatibility thế nào?
- Architect: Rollout nhiều service/JDK image ra sao?
- Expert: Làm thế nào bisect khi agent chỉ fail dưới production flags?

**Red flags:**

- Nâng JDK, Spring Boot và toàn bộ dependencies cùng một commit; không có rollback boundary.

**Evidence:**

- Theory: [Core §3.4](../theory/core/java21-platform-baseline.md#34-migration-gate-an-toàn)
- Learning case: [JDK-01](../../../cases/jdk-01-java21-platform-baseline.md)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK-005 — `SENIOR`

**Question:**

`javac --release 21` giải quyết điều gì? Vì sao nó không thay thế Maven Toolchains, CI JDK pinning và runtime verification?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có hiểu compiler contract và toolchain/runtime boundary.

**Answer outline:**

1. `--release` khóa language rules, generated class-file target và Java SE public API surface.
2. Nó không chọn executable chạy Maven, vendor/patch, plugin JDK, container JDK hoặc third-party compatibility.
3. Toolchain chọn JDK cho plugin hỗ trợ; CI/runtime vẫn phải pin và log evidence.

**Required trade-offs:**

- Toolchain thêm config nhưng giảm host drift; cần kiểm tra plugin nào thực sự toolchain-aware.

**Follow-up ladder:**

- Foundation: Khác gì `-source` + `-target`?
- Senior: Có thể build release 21 bằng JDK 25 không?
- Architect: Khi nào cần multi-JDK matrix?
- Expert: Multi-release JAR làm contract phức tạp thế nào?

**Red flags:**

- Chỉ nói `--release` là bytecode version; cho rằng toolchain tự pin production runtime.

**Evidence:**

- Theory: [Core §3.1](../theory/core/java21-platform-baseline.md#31-platform-baseline-là-một-chuỗi-không-phải-một-con-số-trong-pom)
- Official: [Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/examples/set-compiler-release.html)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK-006 — `SENIOR`

**Question:**

Vì sao không nên pool virtual threads? Nếu database chỉ cho phép 20 concurrent queries thì bạn giới hạn bằng gì?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có tách execution mechanism khỏi resource capacity/backpressure policy.

**Answer outline:**

1. Virtual threads rẻ và intended thread-per-task; pooling không còn mục tiêu tái sử dụng OS-thread resource.
2. Giới hạn DB bằng pool connection hiện hữu kết hợp semaphore/bulkhead/admission/timeout phù hợp.
3. Tránh để số waiter vô hạn; đo pool wait, timeout và reject/degrade behavior.

**Required trade-offs:**

- Semaphore đơn giản nhưng cần fairness/timeout/cancellation; admission control có thể reject sớm để bảo vệ latency.

**Follow-up ladder:**

- Foundation: `newVirtualThreadPerTaskExecutor()` có phải fixed pool không?
- Senior: Hikari pool đã đủ backpressure chưa?
- Architect: Capacity budget chia giữa endpoint ra sao?
- Expert: Làm sao tránh convoy/starvation ở semaphore?

**Red flags:**

- Tạo fixed virtual-thread pool chỉ để “an toàn”; coi connection pool là queue vô hạn miễn phí.

**Evidence:**

- Theory: [Deep-dive §3.3](../theory/deep-dives/virtual-threads-and-pinning.md#33-không-pool-virtual-threads)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK-007 — `SENIOR`

**Question:**

Pinned virtual thread trên Java 21 là gì? Khi nào nó thật sự đáng sửa và bạn chứng minh bằng gì?

**Target depth:** `D3`

**Interviewer evaluates:**

- Có causal explanation, version scope và evidence-based remediation.

**Answer outline:**

1. Java 21 virtual thread block trong `synchronized` hoặc native/foreign boundary có thể không unmount, giữ carrier.
2. Chỉ frequent + long-lived + hot-path pinning mới thường gây scalability risk.
3. Dùng JFR `jdk.VirtualThreadPinned`, `jdk.tracePinnedThreads`, stack/duration cùng latency/throughput/carrier symptom.
4. Refactor targeted blocking monitor path; không thay mọi `synchronized` cơ học.

**Required trade-offs:**

- `ReentrantLock` linh hoạt nhưng dễ lỗi unlock và không phải universal performance fix.

**Follow-up ladder:**

- Foundation: Pinning có làm sai correctness không?
- Senior: Vì sao tăng scheduler parallelism không sửa root cause?
- Architect: Library third-party pin thì rollout/defer thế nào?
- Expert: Câu trả lời thay đổi gì từ JDK 24?

**Red flags:**

- “Thấy `synchronized` là pin”; không ghi JDK version; refactor không có JFR evidence.

**Evidence:**

- Theory: [Deep-dive §3.4](../theory/deep-dives/virtual-threads-and-pinning.md#34-pinning-trên-java-21)
- Official: [JEP 444](https://openjdk.org/jeps/444)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK-008 — `SENIOR`

**Question:**

Bật `spring.threads.virtual.enabled=true` trong Spring Boot service còn những rủi ro nào ngoài pinning?

**Target depth:** `D3`

**Interviewer evaluates:**

- Cross-layer audit: executor coverage, daemon lifecycle, ThreadLocal, downstream pool, timeout/cancellation và observability.

**Answer outline:**

1. Global flag không đổi mọi custom executor/library và không tạo downstream capacity.
2. Virtual threads là daemon; scheduler/lifecycle có thể cần keep-alive verification.
3. Pool properties có thể mất ý nghĩa ở auto-configured executor; explicit admission vẫn cần.
4. Audit ThreadLocal memory/context, interruption/cancellation, metrics/JFR và rollback config.

**Required trade-offs:**

- Global enable đơn giản nhưng blast radius lớn hơn scoped executor/lab.

**Follow-up ladder:**

- Foundation: Vì sao daemon thread liên quan JVM exit?
- Senior: Kiểm thử `@Scheduled` thế nào?
- Architect: Rollout flag bằng canary ra sao?
- Expert: Context propagation và tracing có thể drift thế nào?

**Red flags:**

- Chỉ kiểm tra throughput happy path; cho rằng Spring tự giải quyết pool/backpressure.

**Evidence:**

- Theory: [Deep-dive §10.2](../theory/deep-dives/virtual-threads-and-pinning.md#102-spring-boot-mode)
- Learning case: [JDK-01 scheduler boundary](../../../cases/jdk-01-java21-platform-baseline.md#4-current-baseline)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK-009 — `ARCHITECT`

**Question:**

Project đang Java 17/Spring Boot 3.4, Java 21 là target gần còn JDK 25 là LTS mới hơn. Bạn chọn nâng theo bước nào và bảo vệ quyết định trước stakeholder ra sao?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Evolution strategy, blast radius, support horizon, safety net, rollback và decision revisit.

**Answer outline:**

1. Java 21 trước để cô lập JDK change trên framework line hiện có compatibility; chụp build/test/startup evidence.
2. Không bật virtual threads như migration side effect; chạy experiment riêng.
3. JDK 25 + exact supported Spring Boot/BOM là decision gate sau TEST-01, có compatibility matrix và rollback.
4. Có thể `TIME_BOXED_DEFERRED` nếu benefit chưa vượt regression/operation cost; đặt owner/revisit trigger.

**Required trade-offs:**

- Nhiều bước tăng effort ngắn hạn nhưng cải thiện diagnosability; combined upgrade giảm số rollout nhưng tăng blast radius.

**Follow-up ladder:**

- Foundation: Vì sao Java 21 vẫn hợp lý khi không còn latest LTS?
- Senior: Evidence tối thiểu của bước một?
- Architect: Fleet upgrade order và support policy?
- Expert: Khi nào buộc skip Java 21 và đi thẳng 25?

**Red flags:**

- Chọn version chỉ vì “mới nhất”; không có exact framework/BOM candidate hoặc defer criteria.

**Evidence:**

- Theory: [Core §9](../theory/core/java21-platform-baseline.md#9-trade-off-matrix)
- Learning case: [JDK-01](../../../cases/jdk-01-java21-platform-baseline.md)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK-010 — `ARCHITECT`

**Question:**

Bạn chọn Spring MVC platform threads, MVC virtual threads hay WebFlux cho một service có JDBC, Redis và outbound HTTP như thế nào?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Workload/resource/team/operations decision thay vì technology preference.

**Answer outline:**

1. Characterize I/O wait ratio, concurrency, streaming/backpressure, library ecosystem và current failure.
2. Platform threads phù hợp concurrency vừa/simplicity; virtual threads phù hợp blocking I/O + sequential model; WebFlux phù hợp end-to-end non-blocking/streaming composition khi team/tooling chịu được complexity.
3. Với mọi option, downstream limits, timeout, cancellation, observability và overload policy vẫn bắt buộc.
4. Benchmark cùng workload/controls và giữ rollback path.

**Required trade-offs:**

- Developer/debugging cost, context propagation, blocking leakage, memory và ecosystem maturity.

**Follow-up ladder:**

- Foundation: CPU-bound case chọn gì?
- Senior: Blocking JDBC trong WebFlux gây gì?
- Architect: Mixed workload/endpoint có cần một model toàn service không?
- Expert: Streaming response và slow consumer đổi quyết định ra sao?

**Red flags:**

- “Virtual threads thay thế reactive”; “WebFlux luôn nhanh hơn”; benchmark không kiểm soát pool.

**Evidence:**

- Theory: [Deep-dive §9](../theory/deep-dives/virtual-threads-and-pinning.md#9-trade-off-matrix)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK-011 — `EXPERT`

**Question:**

Sau khi chuyển sang virtual threads, OS thread giảm mạnh nhưng throughput vẫn phẳng và p99 tăng. Hãy đưa ra causal hypothesis, signal cần đo và thứ tự điều tra.

**Target depth:** `D4`

**Interviewer evaluates:**

- Có điều tra bottleneck theo evidence thay vì kết luận virtual threads “không hiệu quả”.

**Answer outline:**

1. Kiểm tra workload equivalence/error trước; sau đó CPU saturation, DB/HTTP pool wait, downstream latency/quota, admission queue, pinning Java 21, GC/allocation và cancellation leak.
2. Correlate throughput + p95/p99 + errors với Hikari active/pending/timeout, CPU, JFR pinned events, allocation/GC và downstream metrics.
3. Thay một variable: concurrency/pool limit/platform-vs-virtual/pinning path; giữ dataset/warm-up/duration cố định.
4. Decision có thể là virtual threads vẫn đúng nhưng capacity policy sai, hoặc defer vì workload CPU/pool-bound.

**Required trade-offs:**

- Tăng DB pool có thể chuyển tải sang database; reject sớm bảo vệ latency nhưng giảm accepted throughput.

**Follow-up ladder:**

- Foundation: Vì sao thread count giảm không phải success metric?
- Senior: Pool wait và query latency phân biệt ra sao?
- Architect: Capacity budget từ SLO thế nào?
- Expert: Dùng Little's Law kiểm tra số in-flight ra sao?

**Red flags:**

- Chỉ tăng carrier parallelism/DB pool; chỉ nhìn average latency; bỏ qua errors và rejected work.

**Evidence:**

- Theory: [Deep-dive §3.2 và §7](../theory/deep-dives/virtual-threads-and-pinning.md#32-concurrency-không-phải-parallelism)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK-012 — `EXPERT`

**Question:**

Một interview candidate nói “virtual thread bị pin bởi `synchronized`, nên phải thay toàn bộ bằng `ReentrantLock`”. Hãy review câu trả lời cho Java 21 và JDK 24/25, rồi thiết kế experiment bác bỏ hoặc hỗ trợ refactor.

**Target depth:** `D4`

**Interviewer evaluates:**

- Version-aware reasoning, causal experiment và restraint khi refactor concurrency primitive.

**Answer outline:**

1. Java 21: blocking khi giữ monitor có thể pin carrier; chỉ frequent/long-lived/hot path đáng xử lý.
2. JDK 24 JEP 491 loại gần hết monitor pinning; native/foreign boundary và contention vẫn còn concern.
3. Experiment pin/non-pin workload cùng JDK version, lock scope, I/O delay, concurrency; thu JFR pinned events, latency/throughput/carrier symptom.
4. Chỉ refactor path có evidence; so correctness/fairness/interruptibility/complexity và rerun tests.

**Required trade-offs:**

- `synchronized` đơn giản và tự release; `ReentrantLock` linh hoạt nhưng cần `try/finally` và có failure modes riêng.

**Follow-up ladder:**

- Foundation: Pinning khác contention?
- Senior: JFR event nào trên Java 21?
- Architect: Library không kiểm soát source xử lý thế nào?
- Expert: Vì sao cùng source cần rerun assumption khi đổi 21 -> 25?

**Red flags:**

- Không ghi runtime version; mass refactor không benchmark; coi hết pinning là hết lock/overload risk.

**Evidence:**

- Theory: [Deep-dive §3.4-3.5](../theory/deep-dives/virtual-threads-and-pinning.md#34-pinning-trên-java-21)
- Official: [JEP 491](https://openjdk.org/jeps/491)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

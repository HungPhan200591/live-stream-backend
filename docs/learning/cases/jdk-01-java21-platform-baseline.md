# JDK-01: Java 21 platform baseline and virtual-thread decision

> Status: `ACTIVE`<br>
> Maturity target: `M1 - Demo`<br>
> Roadmap stage: `Stage 0 - Stabilize the laboratory`<br>
> Prerequisites: [Senior Roadmap](../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md), `pom.xml`, Maven Wrapper, current smoke test<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-25`

## Scope lock

Case này đưa project từ Java 17 declared baseline sang **Java 21 LTS** có toolchain/CI evidence, sau đó đánh giá virtual threads bằng một lab có workload và JFR. Nó không nâng Spring Boot major version, không nâng trực tiếp lên JDK 25, không refactor mọi executor/thread pool và không bật virtual threads trên runtime chính chỉ để dùng công nghệ mới.

JDK 25 là latest LTS tại thời điểm roadmap này được cập nhật. Tài liệu Spring Boot 3.4.13 công bố compatibility tới Java 24, Spring Boot 3.5.16 tới Java 25 và current Spring Boot 4.1.0 tới Java 26. JDK 25 + exact target Spring Boot/BOM thuộc `JDK-02` với compatibility decision gate riêng và phải re-check candidate tại lúc active; không gộp framework/JDK upgrade đó vào JDK-01 trước khi có TEST-01 safety net.

## 1. Interview objective

### Câu hỏi chính

> Khi nào một Senior Java nên nâng baseline JDK của Spring Boot service, và vì sao virtual threads không phải là một cờ cấu hình bật lên để tăng throughput một cách mặc định?

### Follow-up dự kiến

1. Java 21 mang lại gì ngoài virtual threads cho codebase Java 17?
2. Vì sao Java 21 là bước migration phù hợp hơn JDK 25 ngay lúc này?
3. Virtual thread khác platform thread và WebFlux ở workload blocking I/O, CPU-bound và downstream pool như thế nào?
4. Vì sao không pool virtual threads, và pinned virtual thread là gì?
5. Spring Boot virtual-thread mode ảnh hưởng gì tới scheduler/keep-alive và các thread-pool property?

### Năng lực cần chứng minh

- Theory: Java 21 language/runtime changes, virtual-thread lifecycle, pinning, JFR và migration compatibility.
- Implementation: Maven/compiler/runtime/CI baseline Java 21; virtual-thread decision được ghi rõ enabled hoặc deferred.
- Measurement: build/test/startup baseline; nếu thử virtual threads phải có workload, throughput/latency và JFR evidence.
- Trade-off communication: Java 21 now vs remain Java 17 vs JDK 25 + Spring Boot upgrade.

## 2. Problem và invariant

### Hành vi mong đợi

Project build, test và chạy bằng một Java 21 baseline được khai báo, kiểm chứng và tái lập. Virtual threads chỉ được bật sau khi baseline và workload chứng minh nó phù hợp.

### Invariant

1. JDK dùng bởi Maven/CI, compiler target và runtime evidence không được mâu thuẫn âm thầm.
2. Upgrade JDK không được thay API/business/security behavior mà không có test hoặc migration note.
3. Không khẳng định performance benefit của virtual threads nếu chưa có workload + JFR/metric evidence.
4. Không upgrade project lên JDK 25 khi Spring Boot target chưa có compatibility plan đã kiểm chứng.

### Không nằm trong scope

- Nâng Spring Boot 3.4 sang major version khác.
- Thay Spring MVC bằng WebFlux hoặc refactor mọi blocking call.
- Tuning GC/heap production hoặc benchmark hình thức không có workload đại diện.
- Implement SEC-01, TEST-01 hoặc bất kỳ security/business case nào.

## 3. Knowledge links và case-specific interpretation

### Reusable knowledge

- Core theory: [Java 21 platform baseline](../topics/java/theory/core/java21-platform-baseline.md) — `DRAFT`, chờ learner write-back/self-check.
- Deep-dive: [Virtual threads, pinning và downstream backpressure](../topics/java/theory/deep-dives/virtual-threads-and-pinning.md) — preview `DRAFT`, chưa đạt `THEORY_DEEP_DIVE` gate.
- Question bank: [JDK Platform Interview Question Bank](../topics/java/question-bank/jdk-platform.md) — preview `DRAFT`, mọi câu JDK-01 chưa có evidence.
- Depth rubric: [Java language/runtime](../knowledge-depth-rubric.md#31-java-language-collections-algorithm-và-complexity--p0-target-d3), [Concurrency](../knowledge-depth-rubric.md#34-concurrency-jmm-và-async-model--p0-target-d3), [JVM](../knowledge-depth-rubric.md#33-jvm-runtime-và-diagnostics--p0-target-d3).

### Áp dụng vào case này

`pom.xml` hiện khai báo Java 17, trong khi snapshot current-state ghi nhận test từng chạy bằng Java 22.0.2 và chưa có Maven Toolchains/CI lock. Đây là platform drift: code có thể compile hôm nay nhưng không chứng minh được runtime target hoặc reproducibility. Java 21 tạo baseline LTS hỗ trợ virtual threads; Spring Boot 3.4 có cấu hình virtual threads nhưng phải được dùng như experiment, không như default optimization.

### Misconception phát hiện từ case

- “LTS mới nhất” không đồng nghĩa “upgrade project ngay” nếu framework/dependency compatibility chưa có evidence.
- Virtual threads làm blocking I/O dễ scale hơn, không làm CPU-bound work hoặc downstream database/connection pool tự nhiên nhanh hơn.
- Running on a newer JDK ngẫu nhiên không thay thế cho declared/toolchain/CI baseline.

## 4. Current baseline

### Code path

`Maven Wrapper -> pom.xml java.version=17 -> Spring Boot 3.4 application -> smoke test -> PostgreSQL-dependent context`

### Bằng chứng hiện tại

- Source: [`pom.xml`](../../../pom.xml) khai báo `<java.version>17</java.version>`; chưa có Maven Toolchains/CI workflow trong repository.
- Test: chỉ có [`LiveStreamBackendApplicationTests`](../../../src/test/java/com/stream/demo/LiveStreamBackendApplicationTests.java), một context smoke test.
- Runtime: [Current Gaps](../../002_CURRENT_STATE_AND_GAP_ANALYSIS.md) ghi nhận snapshot từng chạy với Java 22.0.2, khác declared baseline.
- Scheduler: [`SessionCleanupScheduler`](../../../src/main/java/com/stream/demo/scheduler/SessionCleanupScheduler.java) dùng `@Scheduled`; phải được xét khi thử Spring Boot virtual-thread mode vì virtual threads là daemon threads.
- Documentation drift: roadmap cũ đặt Java 17 làm baseline và virtual threads chỉ là lab tùy chọn.

### Failure reproducer

1. Setup: ghi `java -version`, `./mvnw.cmd -version` và effective Maven JDK trên máy sạch/CI.
2. Action: chạy `./mvnw.cmd -DskipTests compile`, `./mvnw.cmd test` và smoke startup với Java 21.
3. Expected failure trước khi sửa: baseline/toolchain không được khóa; không thể chứng minh Maven/CI thực sự dùng JDK nào chỉ từ `<java.version>`.
4. Verification command: lưu output version và kết quả Maven vào experiment khi checkpoint tới `EXPERIMENT_EVIDENCE`.

## 5. Hypothesis

> Nếu project khai báo và kiểm chứng Java 21 nhất quán trong Maven/CI/runtime, thì build/test/startup giữ behavior hiện có và Java 21 features có thể được học/đánh giá trên code thật; virtual threads chỉ được bật khi workload + JFR không cho thấy pinning hoặc downstream saturation che mất lợi ích.

### Success criteria

| Signal | Baseline | Target | Cách đo |
| --- | --- | --- | --- |
| Declared/compiler/runtime JDK | Java 17 declared; runtime drift | Java 21 rõ ràng, tái lập | `java -version`, Maven version/effective config, CI log |
| Compile/test | Chưa có Java 21 evidence | Compile + relevant tests pass trên Java 21 | Maven Wrapper output |
| Application startup | Chưa có Java 21 evidence | Startup/smoke pass, không đổi API behavior | Local/profile test evidence |
| Virtual-thread claim | Chưa đo | `enabled` hoặc `deferred` có benchmark/JFR rationale | Experiment + ADR/decision note |

## 6. Alternatives và decision

| Option | Correctness | Complexity | Performance | Operability | Khi nên dùng |
| --- | --- | --- | --- | --- | --- |
| A. Giữ Java 17 | Không giải quyết toolchain drift/new runtime baseline | Thấp lúc đầu | Không có virtual threads | Support/licensing/migration horizon kém hơn | Chỉ khi dependency/platform chưa hỗ trợ Java 21 |
| B. Nâng Java 21, virtual threads deferred/lab | Compatibility nhỏ nhất với Spring Boot 3.4 | Vừa | Có cơ sở đo trước khi bật | Toolchain rõ, rollback đơn giản | **Chọn cho project hiện tại** |
| C. Nâng thẳng JDK 25 + supported Spring Boot line | Có thể tốt dài hạn nhưng trộn JDK/framework risk trước safety net | Cao | Chưa có evidence cho workload | Cần compatibility/regression/rollback plan riêng | Chỉ sau `JDK-02` gate |

### Chọn

Chọn Java 21 làm runtime/platform baseline đầu tiên. Sau build/test/startup evidence, chạy virtual-thread lab tách biệt và ghi quyết định `enabled` hoặc `deferred`; default dự kiến là deferred cho tới khi có workload blocking-I/O đại diện.

### Không chọn

Không giữ Java 17 chỉ vì code hiện còn compile. Không dùng JDK 25 trong JDK-01 vì current Spring Boot 3.4 line chưa công bố compatibility và việc đồng thời đổi framework line sẽ làm mờ nguyên nhân regression trước khi TEST-01 tồn tại. Theo snapshot hiện tại, JDK-02 phải so sánh ít nhất Spring Boot 3.5.16 và 4.1.0, re-check exact patch/BOM tại lúc active, rồi kết thúc bằng migrate-now hoặc time-boxed defer.

### ADR

`N/A` cho đến khi Java 21 baseline hoặc virtual-thread policy trở thành quyết định dài hạn đã có evidence.

## 7. Design

### Happy path

`Developer/CI -> pinned Java 21 -> Maven Wrapper compile/test -> application smoke -> JFR/benchmark lab (nếu virtual threads được thử) -> decision record`

### Failure/crash points

| Point | Failure | Expected behavior | Recovery |
| --- | --- | --- | --- |
| F1 | Maven chạy bằng JDK khác declared target | Fail fast hoặc evidence chỉ rõ version drift | Sửa toolchain/CI; chạy lại clean build |
| F2 | Dependency/plugin không tương thích Java 21 | Compile/test/startup failure | Upgrade/replace dependency có regression evidence hoặc rollback Java baseline |
| F3 | Virtual thread bị pin hoặc downstream pool bão hòa | Throughput không tăng/latency xấu đi | Tắt property, giữ MVC platform-thread baseline, ghi JFR evidence |
| F4 | Scheduler không giữ JVM sống khi virtual threads bật | Process thoát hoặc scheduled task không chạy như kỳ vọng | Không bật global mode hoặc cấu hình keep-alive sau test rõ ràng |

### Data/API/event changes

- Migration: không có schema migration trong JDK-01.
- API contract: không đổi.
- Cache/event: không đổi.
- Compatibility: Java 21 là target của case này; JDK 25 yêu cầu framework/BOM/dependency compatibility matrix riêng dù một Spring Boot line mới hơn đã công bố support.

### Security

- Actor/role/ownership: `N/A`.
- Asset/secret: build provenance, dependency versions và CI JDK selection.
- Abuse/replay threat: `N/A`.
- Audit/redaction: không log secret/token trong diagnostic/build output.

## 8. Implementation checkpoints

- [ ] Viết core theory và self-check Java 21/virtual-thread mental model.
- [ ] Chụp baseline `java`/Maven/runtime version và current test result.
- [ ] Audit Spring Boot/dependency/plugin/container/CI compatibility với Java 21.
- [ ] Đổi declared compiler/toolchain/CI baseline sang Java 21 bằng change nhỏ nhất.
- [ ] Chạy compile, relevant tests và startup/smoke trên Java 21.
- [ ] Thiết kế workload cho virtual-thread lab; không bật global property trước benchmark.
- [ ] Chạy lab, JFR/pinning check và ghi decision enabled/deferred.
- [ ] Đồng bộ roadmap, current map, docs/tooling và review diff.

## 9. Verification matrix

| Level | Scenario | Tool/command | Expected |
| --- | --- | --- | --- |
| Unit | `N/A` | Không có algorithm/business change | Ghi rõ không áp dụng |
| Build | Maven Wrapper compile dưới Java 21 | `./mvnw.cmd -DskipTests compile` | Pass, version evidence được lưu |
| Integration | Existing context test dưới Java 21 | `./mvnw.cmd test` | Pass hoặc failure được triage rõ |
| Runtime | Application startup/profile phù hợp | Smoke procedure | Không regression startup/scheduler rõ ràng |
| Performance | Virtual-thread workload | k6/Gatling + JFR khi lab tới | Không claim nếu chưa có số liệu |
| Fault | Dependency/toolchain mismatch; pinned virtual thread | Deliberate version/lab scenario | Fail fast hoặc feature deferred an toàn |
| Contract | `N/A` | Không đổi API | Ghi rõ không áp dụng |

## 10. Experiment report

### Environment

- Git commit: `TBD khi bắt đầu implementation`.
- JDK/application version: declared Java 17; target Java 21; Spring Boot 3.4.0.
- CPU/RAM/OS: `TBD`.
- Infrastructure: PostgreSQL/Redis/RabbitMQ chỉ khi startup/test scope cần.
- Workload: compile/test/startup trước; virtual-thread workload chỉ sau design gate.

### Raw results

`NOT RUN` — case mới active, chưa có Java 21 compatibility/benchmark evidence.

### Summary

| Metric | Before | After | Delta |
| --- | --- | --- | --- |
| Maven JDK | Chưa ghi | Target Java 21 | Pending |
| Compile/test | Chưa có Java 21 result | Pass | Pending |
| Virtual-thread throughput/latency | N/A | Chỉ đo nếu lab có workload | Pending |

### Interpretation

- Hypothesis: chưa được kiểm chứng.
- Confounding factors: test hiện chỉ là context smoke và có dependency hạ tầng local; `TEST-01` sẽ tăng độ tin cậy sau JDK-01.
- Điều chưa đo: workload đại diện, pinning, connection-pool saturation và production container resource behavior.

## 11. Observability và operations

- Log event: build/runtime JDK version, migration failure category; không log secret.
- Metric: `N/A` cho platform baseline; virtual-thread lab dùng latency, throughput, active thread/carrier signal nếu instrumentation có sẵn.
- Trace: `N/A`.
- SLI/SLO: không đặt mới trong JDK-01.
- Alert: `N/A`.
- Recovery: revert declared/toolchain baseline nếu Java 21 regression chưa xử lý được; không để host JDK ngẫu nhiên quyết định runtime.

## 12. Review findings và residual risk

| Severity | Finding | Resolution/status |
| --- | --- | --- |
| High | POM Java 17 nhưng snapshot từng chạy test bằng Java 22.0.2 | Open; mục tiêu chính JDK-01 |
| High | Chỉ có một context smoke test, nên Java 21 pass không chứng minh behavior đủ rộng | Chấp nhận M1; TEST-01 là case kế tiếp |
| Medium | Virtual threads chưa có workload/JFR evidence | Deferred cho tới lab |
| Medium | JDK 25 cần đổi khỏi current Spring Boot 3.4 line hoặc target framework khác đã công bố support | Chốt tại JDK-02 sau TEST-01; không gộp |

## 13. Interview debrief

### Canonical interview note

Chỉ tạo `docs/learning/interview-notes/jdk-01-java21-platform-baseline.md` sau evidence.

### Câu trả lời 2 phút — draft outline, chưa evidence-backed

Outline do Agent chuẩn bị: current drift -> Java 21 platform invariant -> virtual-thread workload boundary -> enable/defer criteria -> JDK 25/JDK-02 boundary. Người học chỉ viết full personal answer sau experiment/review evidence; không dùng outline này như câu trả lời đã sở hữu.

### Deep dive 15 phút

1. Current Java 17 declared/runtime drift và risk.
2. Java 21 migration boundary, toolchain/CI/build evidence.
3. Virtual thread mechanism, pinning, no-pooling và downstream bottleneck.
4. Spring Boot virtual-thread mode, scheduler/keep-alive caveat.
5. Alternatives Java 17, Java 21, JDK 25 + framework upgrade.
6. Benchmark/JFR design và rollback/defer criteria.

### Điều tôi trả lời chưa tốt

- [ ] Tự giải thích carrier thread, pinning và JFR signal mà không nhìn notes.
- [ ] Phân biệt source/target/release/toolchain/runtime container image.
- [ ] Nêu được workload khiến virtual threads xấu hơn hoặc không cải thiện gì.

### Flash questions

1. **Q:** Virtual threads có cần thread pool không?<br>
   **A:** Không pool theo cách platform threads; tạo thread cho task và giới hạn bằng downstream/resource/backpressure policy.
2. **Q:** Java 21 làm JDBC connection pool lớn hơn không?<br>
   **A:** Không; database connection vẫn là resource hữu hạn và cần sizing riêng.
3. **Q:** Vì sao JDK 25 chưa phải bước đầu?<br>
   **A:** Vì framework line hiện tại chưa công bố compatibility và project chưa có safety net; Boot 3.5.16 có support nhưng đổi JDK + framework cùng lúc phải có matrix, regression và rollback plan riêng.

## 14. Closure gate

- [ ] Java 21 theory/self-check hoàn tất.
- [ ] Declared/toolchain/CI/runtime version nhất quán và có evidence.
- [ ] Compile/test/startup Java 21 pass hoặc residual failure có owner.
- [ ] Virtual-thread decision có workload/JFR evidence hoặc được `DEFERRED` với lý do rõ ràng.
- [ ] JDK 25 boundary/compatibility gate được ghi rõ.
- [ ] Docs/status/cursor được đồng bộ.
- [ ] Tôi tự giải thích được mà không đọc AI output.

## 15. Links

- Roadmap: [Senior Java Interview Roadmap](../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#6-case-backlog-ưu-tiên)
- Build: [`pom.xml`](../../../pom.xml)
- Test: [`LiveStreamBackendApplicationTests`](../../../src/test/java/com/stream/demo/LiveStreamBackendApplicationTests.java)
- Scheduler: [`SessionCleanupScheduler`](../../../src/main/java/com/stream/demo/scheduler/SessionCleanupScheduler.java)
- Current evidence: [Current Gaps](../../002_CURRENT_STATE_AND_GAP_ANALYSIS.md), [Implementation Map](../../implementation/current-implementation-map.md)
- Official references: [Oracle Java SE Support Roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html), [JEP 444 - Virtual Threads](https://openjdk.org/jeps/444), [Spring Boot 3.4 System Requirements](https://docs.spring.io/spring-boot/3.4/system-requirements.html), [Spring Boot 3.5 System Requirements](https://docs.spring.io/spring-boot/3.5/system-requirements.html), [Spring Boot current System Requirements](https://docs.spring.io/spring-boot/system-requirements.html), [Spring Boot virtual threads](https://docs.spring.io/spring-boot/3.4/reference/features/spring-application.html#features.spring-application.virtual-threads)

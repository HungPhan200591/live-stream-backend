# Java Interview Question Bank — JDK 25 and Spring Boot Migration Decision

> Status: `DRAFT`<br>
> Domain owner: `java / build / Spring platform`<br>
> Active slice: `NONE`; preview target `JDK-02 — JDK 25 plus exact Spring Boot/BOM candidate decision`<br>
> Related roadmap: [Stage 0 and JDK-02](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-0---stabilize-the-laboratory)<br>
> Related depth rubric: [JVM runtime](../../../knowledge-depth-rubric.md#33-jvm-runtime-và-diagnostics--p0-target-d3), [Spring Boot](../../../knowledge-depth-rubric.md#35-spring-framework-và-spring-boot--p0-target-d3), [Build and CI/CD](../../../knowledge-depth-rubric.md#318-git-linux-container-build-và-cicd--p1-target-d2-d3)<br>
> Related theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md), [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md) — `TEACHABLE_DRAFT`, evidence `NOT RUN`<br>
> Version snapshot checked: `2026-07-25`; must be re-checked when `JDK-02` becomes active<br>
> Updated: `2026-07-25`

Question bank này được chuẩn bị trước cho decision item `JDK-02`. Nó không chọn candidate, không kết luận `MIGRATE_NOW`, không active case thứ hai và không chứng minh compatibility. Người học phải trả lời trước khi mở `Answer outline`; mọi test, experiment và interview note giữ `NOT RUN`/`NOT CREATED`.

Snapshot chỉ dùng để đặt câu hỏi: JDK 25 đã GA ngày 2025-09-16; OpenJDK mô tả đây là LTS từ phần lớn vendor. Tại thời điểm kiểm tra, Spring Boot 3.5.16 công bố tương thích Java 17-25; Spring Boot 4.1.0 công bố tương thích Java 17-26 và lưu ý third-party support có thể có requirement khác. Exact candidate/BOM vẫn phải pin lại lúc activation.

## Official sources for the snapshot

- [OpenJDK — JDK 25 release](https://openjdk.org/projects/jdk/25/)
- [Oracle — JDK 25 Migration Guide](https://docs.oracle.com/en/java/javase/25/migrate/preparing-migration.html)
- [Spring Boot 3.5.16 — System Requirements](https://docs.spring.io/spring-boot/3.5/system-requirements.html)
- [Spring Boot 4.1.0 — System Requirements](https://docs.spring.io/spring-boot/4.1/system-requirements.html)

## Level rubric

| Level | Trọng tâm |
| --- | --- |
| `FOUNDATION` | Release/support vocabulary, compile/runtime contract và framework compatibility |
| `SENIOR` | Compatibility audit, dependency/plugin breakage, behavioral regression và test gate |
| `ARCHITECT` | Candidate strategy, rollout/rollback, support horizon, cost và organizational ownership |
| `EXPERT` | Mixed-version pathological cases, incomplete analysis và decision under uncertainty |

Level được gắn trên từng câu hỏi; không tách folder theo level.

## Coverage

| Topic | Foundation | Senior | Architect | Expert | Theory source |
| --- | ---: | ---: | ---: | ---: | --- |
| Release, support và compatibility vocabulary | 2 | 1 | 1 | 0 | [Core](../theory/core/jdk-platform-migration-strategy.md) / [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md) |
| JDK migration, tools và behavioral drift | 1 | 2 | 0 | 1 | [Core](../theory/core/jdk-platform-migration-strategy.md) / [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md) |
| Spring Boot/BOM/ecosystem candidate | 1 | 2 | 1 | 0 | [Core](../theory/core/jdk-platform-migration-strategy.md) / [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md) |
| Decision gate, rollout và rollback | 0 | 1 | 2 | 1 | [Core](../theory/core/jdk-platform-migration-strategy.md) / [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md) |
| **Tổng** | **4** | **6** | **4** | **2** | 16 questions |

## Questions

### JAVA-JDK25-001 — `FOUNDATION`

**Question:**

JDK 25 là một Java SE release, còn “LTS” là cam kết của ai? Vì sao nhãn LTS không tự xác định vendor, patch cadence, license hoặc thời hạn hỗ trợ production?

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có tách platform specification/release khỏi vendor support contract.

**Answer outline:**

1. Java SE/JDK release xác định platform và reference implementation; LTS distribution/support phụ thuộc vendor và commercial/community policy.
2. Cần pin vendor, distribution, architecture, patch version, image provenance và update policy.
3. “JDK 25 LTS” không trả lời ai vá CVE, bao lâu nhận patch, license nào áp dụng hoặc runtime image nào được deploy.

**Required trade-offs:**

- Pin chặt tăng reproducibility nhưng cần quy trình cập nhật security patch đều đặn.

**Follow-up ladder:**

- Foundation: Feature release và patch release khác nhau thế nào?
- Senior: Đổi vendor cùng major version có cần regression test không?
- Architect: Chọn vendor theo support, supply chain và fleet standard nào?
- Expert: Làm sao tránh reproducibility trở thành lý do giữ patch có CVE?

**Red flags:**

- “LTS nghĩa là OpenJDK tự hỗ trợ miễn phí nhiều năm”; chỉ pin major `25`.

**Evidence:**

- Theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md)
- Deep-dive: [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md)
- Learning case: `JDK-02 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK25-002 — `FOUNDATION`

**Question:**

Phân biệt build JDK, compiler `--release`, test runtime và production runtime. Có thể dùng JDK 25 để build artifact vẫn chạy trên Java 21 không?

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có hiểu multi-layer platform baseline và class-file/API contract.

**Answer outline:**

1. Build JDK chạy Maven/plugins; `--release 21` giới hạn language/API/class-file contract mà compiler hỗ trợ cho target 21.
2. Test runtime quyết định behavior khi chạy suite; production runtime thực thi artifact thật.
3. Có thể compile target 21 bằng JDK 25 nếu tool/plugin hỗ trợ, nhưng annotation processor, plugin, agent, generated code và runtime behavior vẫn cần compatibility evidence.

**Required trade-offs:**

- Build trên JDK mới giúp toolchain tiến hóa nhưng có thể tạo drift nếu CI/test/runtime không được pin rõ.

**Follow-up ladder:**

- Foundation: `source`, `target` và `release` khác nhau gì?
- Senior: Plugin chạy trong Maven JVM hay target runtime?
- Architect: Matrix build-JDK/target-runtime nào là tối thiểu?
- Expert: Multi-release JAR làm compatibility reasoning thay đổi ra sao?

**Red flags:**

- Đồng nhất `java.version` trong POM với mọi layer; cho rằng `--release` kiểm tra mọi dependency runtime.

**Evidence:**

- Theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md)
- Deep-dive: [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md)
- Learning case: `JDK-02 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK25-003 — `FOUNDATION`

**Question:**

Final, preview, incubator và experimental feature trong JDK 25 có compatibility/enablement contract khác nhau thế nào? JDK 25 là LTS có làm mọi feature production-ready không?

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có đọc lifecycle của từng JEP thay vì suy luận từ nhãn release.

**Answer outline:**

1. Final feature thuộc delivered platform contract; preview cần opt-in compile/runtime và có thể đổi ở release sau.
2. Incubator thường nằm trong module/API thử nghiệm riêng; experimental VM capability có stability/enablement contract riêng.
3. LTS không biến preview/incubator/experimental feature thành API ổn định; production adoption cần support, fallback và upgrade plan.

**Required trade-offs:**

- Early adoption đổi lấy capability/learning sớm nhưng tăng compatibility và maintenance risk.

**Follow-up ladder:**

- Foundation: Preview code cần flag ở những phase nào?
- Senior: Dependency dùng preview feature ảnh hưởng consumer thế nào?
- Architect: Policy nào cho phép preview trong production?
- Expert: Serialization/persistence của model dựa trên preview feature tạo lock-in gì?

**Red flags:**

- “Có trong LTS nên stable”; không biết flag/runtime coupling.

**Evidence:**

- Theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md)
- Deep-dive: [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md)
- Learning case: `JDK-02 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK25-004 — `FOUNDATION`

**Question:**

Spring Boot công bố “compatible up to Java 25” chứng minh điều gì, và chưa chứng minh điều gì cho một project cụ thể?

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có phân biệt framework support envelope với end-to-end application compatibility.

**Answer outline:**

1. Nó cho biết line Spring Boot đó được project framework công bố hỗ trợ trong Java range tương ứng.
2. Chưa chứng minh starter/dependency bên thứ ba, driver, agent, native/JNI, build plugin, container base image hoặc application code tương thích.
3. Cần exact Boot patch/BOM, transitive dependency tree và runtime tests; third-party project có support matrix riêng.

**Required trade-offs:**

- Framework line mới mở support platform mới nhưng có thể kéo ecosystem/API/operational change rộng hơn.

**Follow-up ladder:**

- Foundation: BOM quản lý gì?
- Senior: Một library compile được nhưng vẫn fail runtime do đâu?
- Architect: Support statement nào là hard gate, statement nào chỉ là signal?
- Expert: “Works in tests” và “vendor-supported” khác nhau về risk acceptance thế nào?

**Red flags:**

- Dùng một dòng system requirements làm bằng chứng toàn stack production-ready.

**Evidence:**

- Theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md)
- Deep-dive: [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md)
- Learning case: `JDK-02 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK25-005 — `SENIOR`

**Question:**

Thiết kế compatibility matrix tối thiểu cho migration từ Java 21 sang Java 25 của một Spring Boot backend gồm những chiều nào?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có audit cả build/runtime/ecosystem/operations thay vì chỉ đổi POM.

**Answer outline:**

1. Pin current và candidate: JDK vendor/patch, Boot patch/BOM, Spring Framework, Maven Wrapper/plugins và container image.
2. Inventory driver/client/serializer/security/JWT/logging/observability agent, annotation processor, bytecode/reflection/native dependency.
3. Matrix compile, unit/integration, startup, critical flow, migration, performance/JFR và operational tooling.
4. Ghi owner, official support source, actual result, blocker, workaround expiry và rollback impact cho từng row.

**Required trade-offs:**

- Matrix rộng giảm unknown risk nhưng tốn thời gian; scope theo blast radius và unsupported/internal-API exposure.

**Follow-up ladder:**

- Foundation: Vì sao annotation processor thuộc matrix?
- Senior: Database driver pass compile còn cần test gì?
- Architect: Matrix nào bắt buộc trước decision, matrix nào chạy sau canary?
- Expert: Làm sao xử lý dependency không công bố support nhưng business đang phụ thuộc?

**Red flags:**

- Một checkbox “tests pass”; không pin exact candidate hoặc không có source/owner.

**Evidence:**

- Theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md)
- Deep-dive: [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md)
- Learning case: `JDK-02 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK25-006 — `SENIOR`

**Question:**

Vì sao Oracle khuyến nghị thử chạy application trên JDK mới trước khi recompile, rồi mới kết hợp dependency update, compile và analysis? Bạn tổ chức experiment này thế nào?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có cô lập source of breakage và tạo baseline so sánh.

**Answer outline:**

1. Chạy artifact hiện tại trên runtime mới tách binary/runtime compatibility khỏi source/compiler/dependency changes.
2. Sau đó recompile cùng target hiện tại, audit warnings/internal APIs, rồi nâng candidate dependencies theo change nhỏ có diff/evidence.
3. So sánh startup, critical behavior, locale/time/security/TLS, performance và runtime warnings với Java 21 baseline.
4. Mỗi step có command, artifact hash, exact runtime và rollback point.

**Required trade-offs:**

- Tách migration thành bước nhỏ dễ diagnosis/rollback nhưng kéo dài matrix; big-bang nhanh ban đầu nhưng khó attribution.

**Follow-up ladder:**

- Foundation: Binary compatibility và source compatibility khác gì?
- Senior: Khi nào runtime-first không khả thi?
- Architect: Artifact promotion bảo đảm build-once semantics thế nào?
- Expert: Bạn phát hiện silent semantic drift bằng oracle nào?

**Red flags:**

- Đổi JDK, Boot và mọi dependency trong một commit rồi chỉ chạy smoke test.

**Evidence:**

- Theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md)
- Deep-dive: [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md)
- Learning case: `JDK-02 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK25-007 — `SENIOR`

**Question:**

`jdeps -jdkinternals` giúp gì trong JDK migration và có blind spot nào? Bạn bổ sung evidence gì để không đánh đồng static analysis với compatibility proof?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có hiểu giới hạn static reachability và dynamic/runtime mechanisms.

**Answer outline:**

1. `jdeps` phát hiện dependency/package và một số JDK-internal API references trong class files.
2. Nó có thể bỏ sót reflection, generated bytecode, service loading, optional path, JNI/native agent và code chỉ tải theo environment.
3. Kết hợp compiler warnings, dependency/vendor matrix, startup/critical-flow tests, agent instrumentation và runtime logs.
4. `--add-opens/--add-exports` chỉ là workaround có owner/expiry, không phải closure mặc định.

**Required trade-offs:**

- Static analysis nhanh và repeatable nhưng không thay dynamic coverage; dynamic tests cũng không chứng minh path chưa chạy.

**Follow-up ladder:**

- Foundation: JDK internal API có risk gì?
- Senior: Reflection failure thường xuất hiện ở startup hay lazy path?
- Architect: Workaround flag được quản trị và loại bỏ thế nào?
- Expert: Agent tạo bytecode runtime làm analysis boundary thay đổi ra sao?

**Red flags:**

- “`jdeps` sạch nên migration an toàn”; giữ `--add-opens` vô thời hạn.

**Evidence:**

- Theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md)
- Deep-dive: [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md)
- Learning case: `JDK-02 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK25-008 — `SENIOR`

**Question:**

Spring Boot parent/BOM, Maven dependency mediation và explicit dependency version tương tác thế nào khi chọn candidate? Làm sao phát hiện accidental downgrade/override?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có hiểu effective dependency graph và ownership của version.

**Answer outline:**

1. Boot dependency management/BOM cung cấp tested version set; Maven mediation chọn dependency theo graph/rule, còn explicit override có thể phá tested set.
2. So sánh effective POM/dependency tree current-candidate, tìm omitted/conflict/duplicate classes và unmanaged versions.
3. Giải thích từng override bằng compatibility/security need, test riêng và removal/revisit plan.
4. Pin build plugins/Wrapper riêng; dependency BOM không quản lý toàn bộ toolchain runtime.

**Required trade-offs:**

- Theo BOM giảm integration burden; override giúp unblock CVE/feature nhưng chuyển compatibility ownership sang team.

**Follow-up ladder:**

- Foundation: Direct và transitive dependency khác gì?
- Senior: Vì sao dependency tree giống nhau vẫn có runtime drift?
- Architect: Enforce convergence ở build gate tới mức nào?
- Expert: Nhiều BOM import tạo ownership/conflict model nào?

**Red flags:**

- Nâng từng library lên “latest” bất chấp BOM; không kiểm tra effective model.

**Evidence:**

- Theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md)
- Deep-dive: [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md)
- Learning case: `JDK-02 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK25-009 — `SENIOR`

**Question:**

Bạn chọn nâng từ Spring Boot 3.4 sang 3.5 hay đồng thời sang major 4.x dựa trên tiêu chí nào? Vì sao “đã nâng JDK thì nâng luôn framework major” là reasoning yếu?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có quản lý change surface, prerequisite và business value thay vì chạy theo latest.

**Answer outline:**

1. Xác định goal: JDK 25 support, support horizon, security, feature hay ecosystem requirement.
2. So exact candidates về Framework/Servlet/container line, removed/deprecated behavior, starters, observability/security/data integrations và third-party support.
3. Chọn smallest supported step nếu nó đạt goal và giảm blast radius; chọn major khi value/support horizon bù migration cost và có safety net.
4. Tách decision/commit nếu có thể để attribution và rollback rõ.

**Required trade-offs:**

- Incremental upgrade giảm risk mỗi bước nhưng có thể trả migration cost hai lần; major jump rút ngắn đường dài nhưng tăng simultaneous uncertainty.

**Follow-up ladder:**

- Foundation: Major/minor/patch truyền đạt compatibility intent gì?
- Senior: So sánh deprecation/removal report thế nào?
- Architect: Support horizon ảnh hưởng TCO ra sao?
- Expert: Khi nào intermediate version tạo dead-end hoặc duplicate migration?

**Red flags:**

- “Latest luôn tốt nhất”; chọn version chỉ vì JDK range mà bỏ qua ecosystem.

**Evidence:**

- Theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md)
- Deep-dive: [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md)
- Learning case: `JDK-02 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK25-010 — `SENIOR`

**Question:**

Một migration compile và startup thành công vẫn có thể tạo behavioral regression nào? Thiết kế regression oracle cho locale/time, security, TLS, serialization và JVM defaults ra sao?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có tìm silent semantic drift chứ không dừng ở binary compatibility.

**Answer outline:**

1. Default locale/charset/time-zone data, crypto/TLS policy, reflection/access, serialization, regex/library behavior và VM defaults có thể đổi.
2. Chụp golden contract ở business boundary thay vì snapshot toàn output dễ brittle; pin input/environment và so current/candidate.
3. Test security negative paths, token/JSON/database/cache compatibility và external protocol handshake.
4. Theo dõi warnings, JFR/GC/startup/latency/resource metrics; performance change không tự là regression nếu SLO/capacity vẫn đạt.

**Required trade-offs:**

- Golden comparison phát hiện drift rộng nhưng dễ khóa intentional change; semantic assertion cần effort thiết kế cao hơn.

**Follow-up ladder:**

- Foundation: Startup success chứng minh được gì?
- Senior: Serialized cache/session cũ cần compatibility test nào?
- Architect: Ai chấp nhận intentional behavior change?
- Expert: Làm sao phát hiện drift chỉ xuất hiện dưới production data distribution?

**Red flags:**

- Chỉ chạy context smoke; snapshot toàn response không phân loại intentional change.

**Evidence:**

- Theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md)
- Deep-dive: [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md)
- Learning case: `JDK-02 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK25-011 — `ARCHITECT`

**Question:**

So sánh candidate “Java 25 + Spring Boot 3.5.x” với “Java 25 + Spring Boot 4.x” bằng decision matrix nào? Những criterion nào là hard gate và criterion nào được weighted?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có formalize decision, constraint và uncertainty thay vì dùng preference.

**Answer outline:**

1. Hard gate: official support, critical dependency/agent compatibility, security/compliance, test/bootstrap và rollback feasibility.
2. Weighted criteria: support horizon, migration effort, performance, feature value, operability, team skill, ecosystem maturity và future upgrade path.
3. Pin exact patch/BOM/vendor/image; lưu source date và actual experiment result cho từng claim.
4. Chạy sensitivity analysis: decision có đổi nếu một estimate sai hoặc support timeline thay đổi không.
5. Kết luận `MIGRATE_NOW` hoặc `TIME_BOXED_DEFERRED`, không để “đánh giá thêm” vô thời hạn.

**Required trade-offs:**

- Matrix làm reasoning minh bạch nhưng weight có thể tạo vẻ khách quan giả; hard constraint và uncertainty phải được nêu riêng.

**Follow-up ladder:**

- Foundation: Hard gate khác score thế nào?
- Senior: Evidence nào thay estimate bằng actual result?
- Architect: Ai approve weight và risk acceptance?
- Expert: Khi candidates có option value khác nhau, NPV/TCO chưa đủ ở điểm nào?

**Red flags:**

- Chấm điểm mà không pin candidate/source; weight được điều chỉnh để hợp thức hóa lựa chọn có trước.

**Evidence:**

- Theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md)
- Deep-dive: [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md)
- Learning case: `JDK-02 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK25-012 — `ARCHITECT`

**Question:**

Thiết kế build, test và rollout strategy để tránh CI dùng JDK 25 nhưng production vẫn chạy image cũ hoặc ngược lại. “Build once, deploy many” áp dụng thế nào với JVM runtime?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có bảo vệ artifact/runtime provenance xuyên pipeline.

**Answer outline:**

1. Pin Maven Wrapper/build image/toolchain và production base image; capture `java -version`, artifact/image digest và SBOM/provenance.
2. Build một artifact đã kiểm chứng rồi promote; runtime image cũng immutable và được ghép/pin theo release manifest.
3. Run compatibility suite trên candidate runtime image, không chỉ host CI; startup/smoke/canary xác minh deployed digest/version.
4. Policy ngăn mutable tag và environment tự chọn JDK; deployment report version drift.

**Required trade-offs:**

- Artifact tách runtime tăng reuse nhưng cần manifest pairing; self-contained image đơn giản promotion nhưng tăng rebuild/security-patch workflow.

**Follow-up ladder:**

- Foundation: Artifact digest và image digest bảo vệ hai thứ gì?
- Senior: Vì sao `java -version` ở build log chưa đủ?
- Architect: Base-image patch có rebuild application artifact không?
- Expert: Reproducible build và supply-chain attestation khác nhau thế nào?

**Red flags:**

- Dùng tag `latest`; test jar trên host nhưng deploy vào runtime image chưa kiểm chứng.

**Evidence:**

- Theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md)
- Deep-dive: [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md)
- Learning case: `JDK-02 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK25-013 — `ARCHITECT`

**Question:**

Rollback một JDK/Spring Boot migration gồm những gì ngoài việc deploy lại binary cũ? Phân loại change nào làm rollback mất tính đối xứng.

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có nhìn cross-layer state/config/protocol/data compatibility và operational recovery.

**Answer outline:**

1. Rollback plan pin old artifact/runtime/config và command/procedure; kiểm tra observability/health sau rollback.
2. Database migration, serialized cache/session/event payload, config key semantics, protocol/TLS và dependency side effect có thể không backward-compatible.
3. Dùng expand/contract hoặc dual-compatible format nếu rolling/rollback yêu cầu; cache có version/eviction strategy.
4. Định nghĩa rollback trigger, decision owner, time window và forward-fix path khi rollback nguy hiểm hơn.
5. Diễn tập rollback trên representative state, không chỉ viết tài liệu.

**Required trade-offs:**

- Backward compatibility tăng implementation/storage cost nhưng mua rollback/canary safety.

**Follow-up ladder:**

- Foundation: Binary rollback và data rollback khác gì?
- Senior: Cache serialization drift xử lý thế nào?
- Architect: Khi nào chọn forward fix thay rollback?
- Expert: Chứng minh mixed-version read/write compatibility bằng invariant nào?

**Red flags:**

- “Revert commit là rollback”; không xét state được ghi bởi version mới.

**Evidence:**

- Theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md)
- Deep-dive: [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md)
- Learning case: `JDK-02 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK25-014 — `ARCHITECT`

**Question:**

Một quyết định `TIME_BOXED_DEFERRED` cần những trường nào để là closure hợp lệ thay vì backlog treo? Bạn đặt revisit trigger dựa trên thời gian và sự kiện ra sao?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có quản trị deferred risk, owner và opportunity cost.

**Answer outline:**

1. Ghi exact candidate đã đánh giá, evidence, blockers, residual risk, current support runway và reason defer.
2. Có accountable owner, revisit date, event trigger như EOL/CVE/dependency support/feature need và effort budget.
3. Định nghĩa điều kiện đổi sang migrate, tiếp tục defer hoặc loại candidate; cập nhật compatibility snapshot tại revisit.
4. Giữ preparatory actions cần thiết: safety net, dependency cleanup, observability và removal of temporary workaround.

**Required trade-offs:**

- Defer giữ focus và tránh premature risk nhưng tiêu hao support runway; migrate sớm mua learning/time buffer với cost hiện tại.

**Follow-up ladder:**

- Foundation: Defer khác reject thế nào?
- Senior: Blocker nào có thể chủ động giảm trước revisit?
- Architect: Ai chịu risk khi owner rời team?
- Expert: Làm sao định giá option value của việc giữ compatibility với hai paths?

**Red flags:**

- “Để sau”; không date/owner/trigger; evidence cũ được tái sử dụng sau nhiều release mà không re-check.

**Evidence:**

- Theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md)
- Deep-dive: [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md)
- Learning case: `JDK-02 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK25-015 — `EXPERT`

**Question:**

Trong rolling deployment có cả Java 21 và Java 25 instances, những cross-version failure nào có thể chỉ xuất hiện khi chúng cùng đọc/ghi database, Redis, session hoặc message broker? Thiết kế compatibility experiment thế nào?

**Target depth:** `D4`

**Interviewer evaluates:**

- Có reasoning về mixed fleet, shared state và bidirectional compatibility thay vì test từng version độc lập.

**Answer outline:**

1. Hai version có thể khác serialization/default behavior, dependency protocol, cache/session schema, generated data hoặc timing/resource profile.
2. Test ma trận old-write/new-read, new-write/old-read, concurrent read/write và rollback-after-new-write trên shared-state versioned fixture.
3. Giữ database/schema/payload expand-contract; consumer tolerance và unknown-field policy phải explicit.
4. Inject partial rollout, restart, stale cache/session và broker redelivery; correlate instance/runtime version với failure.
5. Canary chỉ an toàn nếu traffic/state sharing thực sự đi qua compatibility paths cần chứng minh.

**Required trade-offs:**

- Mixed-version support cho rollout an toàn nhưng tăng protocol/schema complexity; stop-the-world cutover giảm matrix nhưng tăng availability/rollback risk.

**Follow-up ladder:**

- Foundation: Forward và backward compatibility khác gì?
- Senior: Java serialization mặc định nguy hiểm thế nào trong shared state?
- Architect: Version envelope hỗ trợ bao nhiêu release?
- Expert: Làm sao phát hiện timing drift tạo emergent failure dù payload tương thích?

**Red flags:**

- Test Java 21 và 25 riêng rồi kết luận rolling upgrade an toàn; không test new-write/old-read.

**Evidence:**

- Theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md)
- Deep-dive: [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md)
- Learning case: `JDK-02 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### JAVA-JDK25-016 — `EXPERT`

**Question:**

Static analysis, framework support matrix và test suite đều xanh nhưng production agent hiếm khi attach làm JVM crash trên JDK 25. Decision framework đã thiếu gì, và bạn tiến hóa nó thế nào mà không biến matrix thành vô hạn?

**Target depth:** `D4`

**Interviewer evaluates:**

- Có xử lý unknown unknown, optional runtime path, failure injection và learning feedback loop.

**Answer outline:**

1. Inventory/coverage bỏ sót optional operational component và attach path; “green” chỉ có nghĩa trong boundary đã quan sát.
2. Bổ sung production-topology inventory: agents, profiler/APM, JNI/JVMTI, security scanner, sidecar và emergency tooling với activation conditions.
3. Chạy canary/failure-injection cho critical optional paths, capture crash dump/JFR/native diagnostics và vendor support evidence.
4. Ưu tiên matrix theo probability × impact × detectability; dùng incident để thêm category/rule, không chỉ thêm một test quá cụ thể.
5. Có kill switch/agent-disable path, rollback trigger và owner; cập nhật migration template cho các decision sau.

**Required trade-offs:**

- Không thể exhaustively test mọi combination; cần risk sampling, progressive delivery và recoverability để quản unknowns.

**Follow-up ladder:**

- Foundation: Optional dependency vẫn có thể là production-critical thế nào?
- Senior: JVM crash artifact nào cần giữ?
- Architect: Ai sở hữu compatibility của observability tooling?
- Expert: Bạn cân bằng canary exposure với blast radius của native crash thế nào?

**Red flags:**

- Đổ lỗi “JDK bug” trước causal evidence; thêm mọi combination vào blocking matrix không có prioritization.

**Evidence:**

- Theory: [Core theory](../theory/core/jdk-platform-migration-strategy.md)
- Deep-dive: [Deep-dive](../theory/deep-dives/java21-to-java25-lts-compatibility-and-rollout.md)
- Learning case: `JDK-02 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `JDK-02` thực sự active:

1. Re-check JDK, Spring Boot, Spring Framework, build tool và third-party support từ official sources; pin exact vendor/patch/BOM/image.
2. Tạo theory/deep-dive theo checkpoint rồi thay marker `NOT CREATED` bằng canonical links.
3. Tạo compatibility matrix và actual experiment; chỉ sau đó mới kết luận `MIGRATE_NOW` hoặc `TIME_BOXED_DEFERRED`.
4. Giữ stable question IDs; deprecated question phải trỏ replacement, không tái sử dụng ID.

# Testing Interview Question Bank — Hermetic Integration Testing

> Status: `DRAFT`<br>
> Domain owner: `testing / build reliability`<br>
> Active slice: `NONE`; preview target `TEST-01 — hermetic integration-test harness and risk-based boundary`<br>
> Related roadmap: [Stage 0 and TEST-01](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-0---stabilize-the-laboratory)<br>
> Related depth rubric: [Testing and quality strategy — P0, target D3](../../../knowledge-depth-rubric.md#310-testing-và-quality-strategy--p0-target-d3)<br>
> Related theory: [Testing strategy and hermetic tests](../theory/core/testing-strategy-and-hermetic-tests.md); [Testcontainers lifecycle deep-dive](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)<br>
> Updated: `2026-07-26`

Question bank này được chuẩn bị trước cho `TEST-01` theo yêu cầu của người học. File chỉ chứa question ladder và evaluation rubric; không chứng minh `TEST-01` đã active hoặc checkpoint hiện tại đã hoàn tất. Người học phải trả lời trước khi mở `Answer outline`. Mọi test, experiment và interview note giữ `NOT RUN`/`NOT CREATED` cho tới khi có artifact thật.

## Level rubric

| Level | Trọng tâm |
| --- | --- |
| `FOUNDATION` | Test taxonomy, vocabulary, isolation và mechanism nền tảng |
| `SENIOR` | Chọn boundary theo risk, tái hiện failure, determinism, flakiness và infrastructure semantics |
| `ARCHITECT` | Test portfolio, CI topology, release confidence, ownership, cost và migration safety |
| `EXPERT` | Pathological concurrency/distributed failure, confidence modeling và giới hạn của test suite |

Level được gắn trên từng câu hỏi; không tách folder theo level.

## Coverage

| Topic | Foundation | Senior | Architect | Expert | Theory source |
| --- | ---: | ---: | ---: | ---: | --- |
| Test taxonomy và risk-based boundary | 2 | 1 | 1 | 0 | [Core](../theory/core/testing-strategy-and-hermetic-tests.md) |
| Hermeticism, determinism và test data | 1 | 2 | 0 | 1 | [Core](../theory/core/testing-strategy-and-hermetic-tests.md) |
| Testcontainers và infrastructure semantics | 1 | 2 | 1 | 0 | [Deep-dive](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md) |
| CI, flakiness và release confidence | 0 | 1 | 2 | 1 | [Deep-dive](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md) |
| **Tổng** | **4** | **6** | **4** | **2** | 16 questions |

## Questions

### TEST-HERMETIC-001 — `FOUNDATION`

**Question:**

Phân biệt unit test, slice test, integration test, contract test và end-to-end test. Mỗi loại đang bảo vệ boundary nào và failure nào nó không thể chứng minh?

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có phân loại theo boundary/evidence hay chỉ theo annotation và tốc độ chạy.

**Answer outline:**

1. Unit test cô lập một unit logic; slice test nạp một phần framework; integration test kiểm tra collaboration với infrastructure thật hoặc tương đương production.
2. Contract test bảo vệ interaction contract giữa consumer/provider; end-to-end kiểm tra critical flow qua nhiều boundary đã deploy.
3. Không layer nào tự đủ: unit test không chứng minh mapping/query/serialization; end-to-end khó định vị lỗi và không thay exhaustive branch tests.

**Required trade-offs:**

- Confidence và production fidelity tăng thường kéo theo runtime, setup cost và khó chẩn đoán tăng.

**Follow-up ladder:**

- Foundation: MockMvc standalone và `@SpringBootTest` khác boundary nào?
- Senior: Với authorization rule, branch nào nên unit test và branch nào phải qua HTTP/security filter chain?
- Architect: Contract test thay end-to-end test được tới đâu?
- Expert: Khi nào taxonomy dạng pyramid không mô tả đúng một event-driven system?

**Red flags:**

- Gọi mọi test có Spring context là integration test; cho rằng end-to-end test chứng minh mọi invariant.

**Evidence:**

- Theory: [Core](../theory/core/testing-strategy-and-hermetic-tests.md)
- Deep-dive: [Lifecycle/isolation/CI failure modes](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)
- Learning case: `TEST-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### TEST-HERMETIC-002 — `FOUNDATION`

**Question:**

Mock, stub, fake và spy khác nhau thế nào? Vì sao mock quá nhiều có thể tạo false confidence dù coverage cao?

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có hiểu test double là công cụ thay boundary, không phải mặc định cho mọi dependency.

**Answer outline:**

1. Stub trả dữ liệu định trước; mock xác minh interaction; fake có implementation đơn giản nhưng hoạt động; spy bọc object để quan sát hoặc override một phần.
2. Mock implementation detail khiến test pass theo collaboration do chính test tưởng tượng, không theo behavior thật của database, serializer, transaction hay remote protocol.
3. Dùng state/output assertion cho business invariant; chỉ verify interaction khi interaction chính là contract có ý nghĩa.

**Required trade-offs:**

- Test double giúp nhanh và fault injection dễ, nhưng giảm fidelity và có chi phí đồng bộ với dependency thật.

**Follow-up ladder:**

- Foundation: Khi nào dùng fake clock thay mock clock?
- Senior: Mock repository có thể che lỗi JPA nào?
- Architect: Ai sở hữu việc kiểm tra fake còn tương thích với service thật?
- Expert: Làm sao phát hiện test suite đang khóa implementation thay vì behavior?

**Red flags:**

- “Unit test thì phải mock mọi thứ”; verify toàn bộ lời gọi private collaboration.

**Evidence:**

- Theory: [Core](../theory/core/testing-strategy-and-hermetic-tests.md)
- Deep-dive: [Lifecycle/isolation/CI failure modes](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)
- Learning case: `TEST-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### TEST-HERMETIC-003 — `FOUNDATION`

**Question:**

Một test “hermetic” có nghĩa là gì? Hãy nêu các nguồn phụ thuộc môi trường thường làm test pass trên máy developer nhưng fail trên CI.

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có nhận diện đầy đủ input ẩn và shared state hay chỉ nói “dùng Docker”.

**Answer outline:**

1. Test hermetic kiểm soát dependency, configuration, data và lifecycle; kết quả không phụ thuộc service hoặc state có sẵn ngoài test boundary đã khai báo.
2. Input ẩn gồm clock/timezone/locale, random/ID, port, filesystem, network/DNS, environment variable, database/cache/broker state và execution order.
3. Container là một cơ chế provisioning; hermeticity còn cần isolation, readiness, deterministic cleanup và version pinning.

**Required trade-offs:**

- Isolation mạnh tăng repeatability nhưng có thể tăng startup time và resource consumption.

**Follow-up ladder:**

- Foundation: Repeatable và deterministic có hoàn toàn giống nhau không?
- Senior: Vì sao test vẫn không hermetic dù mỗi run tạo container mới?
- Architect: Boundary nào có thể dùng shared infrastructure mà vẫn kiểm soát contamination?
- Expert: Bạn mô hình hóa entropy budget của test suite như thế nào?

**Red flags:**

- Đồng nhất hermetic với “chạy offline”; bỏ qua clock, random và global/static state.

**Evidence:**

- Theory: [Core](../theory/core/testing-strategy-and-hermetic-tests.md)
- Deep-dive: [Lifecycle/isolation/CI failure modes](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)
- Learning case: `TEST-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### TEST-HERMETIC-004 — `FOUNDATION`

**Question:**

Testcontainers giải quyết vấn đề gì so với database in-memory hoặc mock repository? Nó không tự giải quyết những vấn đề gì?

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có hiểu production fidelity của engine/protocol và giới hạn của provisioning tool.

**Answer outline:**

1. Testcontainers chạy dependency thật theo version pin, giúp kiểm tra dialect, constraint, transaction, serialization và network protocol gần production hơn.
2. In-memory substitute có thể khác SQL dialect/locking/type/extension; mock repository không thực thi persistence semantics.
3. Testcontainers không tự thiết kế fixture, readiness, cleanup, parallel isolation, migration gate hoặc assertion đúng.

**Required trade-offs:**

- Fidelity cao hơn đổi lấy Docker dependency, image/startup cost và CI resource management.

**Follow-up ladder:**

- Foundation: Khi nào H2 vẫn là lựa chọn hợp lý?
- Senior: Container chạy nhưng schema chưa ready thì failure biểu hiện thế nào?
- Architect: Pin image theo major, minor hay digest dựa trên policy nào?
- Expert: Làm sao kiểm tra compatibility với nhiều engine/version mà không làm matrix nổ chi phí?

**Red flags:**

- “Có Testcontainers là integration tests đã production-like”; dùng image `latest` không có compatibility policy.

**Evidence:**

- Theory: [Core](../theory/core/testing-strategy-and-hermetic-tests.md)
- Deep-dive: [Lifecycle/isolation/CI failure modes](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)
- Learning case: `TEST-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### TEST-HERMETIC-005 — `SENIOR`

**Question:**

Bạn chọn test boundary nhỏ nhất nhưng đủ mạnh để bảo vệ một business/security invariant như thế nào? Minh họa với rule “refresh token không được dùng như access token”.

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có bắt đầu từ risk/invariant và đường thực thi hay chọn annotation theo thói quen.

**Answer outline:**

1. Viết invariant, threat/failure path và blast radius trước; xác định layer nào có thể phá rule.
2. Unit test token classification/decision branch; HTTP security integration test phải đi qua parser, filter chain, authorization và response mapping thật.
3. Thêm negative case dùng refresh token ở protected endpoint; nếu session/database semantics liên quan thì dùng persistence boundary thật.
4. Không cần end-to-end cho mọi permutation nếu các lower-level tests đã chứng minh từng boundary và có một critical-flow smoke test.

**Required trade-offs:**

- Boundary quá hẹp bỏ sót wiring/security configuration; boundary quá rộng làm feedback chậm và failure khó định vị.

**Follow-up ladder:**

- Foundation: Happy-path test và negative test bảo vệ hai claim nào?
- Senior: Vì sao mock `JwtService` có thể vô hiệu hóa chính risk cần test?
- Architect: Xếp priority test theo likelihood, impact và detectability thế nào?
- Expert: Làm sao chứng minh không còn bypass path ngoài endpoint đã test?

**Red flags:**

- Dùng coverage percentage để chọn boundary; chỉ test service method mà kết luận filter chain an toàn.

**Evidence:**

- Theory: [Core](../theory/core/testing-strategy-and-hermetic-tests.md)
- Deep-dive: [Lifecycle/isolation/CI failure modes](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)
- Learning case: `TEST-01 NOT CREATED`; future security evidence có thể link `SEC-01`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### TEST-HERMETIC-006 — `SENIOR`

**Question:**

Thiết kế deterministic seams cho clock, UUID/ID, randomness và test data như thế nào mà không làm production code bị “test-only pollution”?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có dùng dependency boundary rõ ràng và giữ invariant dữ liệu hay hard-code giá trị khắp test.

**Answer outline:**

1. Inject `Clock` hoặc domain time provider; bọc ID/random generation sau interface nhỏ khi behavior phụ thuộc chúng.
2. Test data builder cung cấp default hợp lệ và override field liên quan, tránh fixture khổng lồ/implicit invalid state.
3. Production wiring dùng implementation thật; test wiring dùng fixed/sequence provider mà không thêm branch `if (test)` trong business code.
4. Assertion tập trung vào observable behavior, không khóa exact UUID/timestamp nếu exact value không thuộc contract.

**Required trade-offs:**

- Thêm seam tăng abstraction; chỉ tạo seam cho nguồn nondeterminism ảnh hưởng behavior/testability.

**Follow-up ladder:**

- Foundation: Vì sao gọi `Instant.now()` trực tiếp gây flaky boundary test?
- Senior: Sequence ID provider phải reset/isolate khi chạy parallel ra sao?
- Architect: Chuẩn hóa test builder toàn team mà không tạo “god fixture library” thế nào?
- Expert: Randomized/property tests cần deterministic seed và replay contract gì?

**Red flags:**

- `Thread.sleep` để chờ thời gian; production flag dành riêng cho test; builder tạo object vi phạm invariant mặc định.

**Evidence:**

- Theory: [Core](../theory/core/testing-strategy-and-hermetic-tests.md)
- Deep-dive: [Lifecycle/isolation/CI failure modes](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)
- Learning case: `TEST-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### TEST-HERMETIC-007 — `SENIOR`

**Question:**

Vì sao một integration test được bọc transaction và rollback sau mỗi method có thể che lỗi chỉ xuất hiện lúc commit, sau commit hoặc ở transaction khác?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có hiểu transaction visibility/flush/commit boundary thay vì coi rollback là isolation miễn phí.

**Answer outline:**

1. Flush có thể bị trì hoãn; deferred constraint, commit failure hoặc callback after-commit chưa chạy trước assertion.
2. Code test và application có thể chia cùng transaction, làm query nhìn thấy uncommitted state mà request/consumer thật không thấy.
3. Async/event handler chạy transaction/thread khác có timing và visibility khác.
4. Với claim liên quan commit, gọi qua boundary thật hoặc commit rõ ràng rồi verify từ connection/transaction mới; cleanup bằng data isolation thay vì che lifecycle.

**Required trade-offs:**

- Rollback giúp test nhanh và sạch, nhưng không phù hợp cho mọi transaction/event invariant.

**Follow-up ladder:**

- Foundation: Flush và commit khác nhau ở đâu?
- Senior: Làm sao test một event chỉ được publish after commit?
- Architect: Chọn cleanup bằng rollback, truncate, schema/database-per-test theo tiêu chí gì?
- Expert: Test nào chứng minh crash window giữa database commit và broker publish?

**Red flags:**

- “Test có `@Transactional` nên giống production”; assertion trong cùng persistence context rồi kết luận dữ liệu đã durable.

**Evidence:**

- Theory: [Core](../theory/core/testing-strategy-and-hermetic-tests.md)
- Deep-dive: [Lifecycle/isolation/CI failure modes](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)
- Learning case: `TEST-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### TEST-HERMETIC-008 — `SENIOR`

**Question:**

Container đã mở TCP port có đồng nghĩa dependency đã sẵn sàng cho test không? Hãy thiết kế readiness strategy cho PostgreSQL, Redis và RabbitMQ.

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có phân biệt process/network readiness với application-level readiness.

**Answer outline:**

1. Port open chỉ chứng minh listener tồn tại; recovery, authentication, schema/plugin/vhost hoặc broker topology có thể chưa ready.
2. Dùng wait strategy phù hợp dependency: health/log/protocol query có timeout hữu hạn và diagnostic output.
3. Sau infrastructure readiness còn application bootstrap: migration hoàn tất, properties được inject trước context creation và required topology tồn tại.
4. Không chữa race bằng sleep cố định; failure phải chỉ ra dependency và phase không ready.

**Required trade-offs:**

- Readiness check sâu tăng startup cost nhưng giảm flaky bootstrap và cải thiện diagnosis.

**Follow-up ladder:**

- Foundation: Liveness và readiness khác nhau thế nào?
- Senior: Dynamic properties phải đăng ký trước Spring context ở thời điểm nào?
- Architect: Ai sở hữu topology creation: container, migration, application hay test fixture?
- Expert: Làm sao inject slow recovery để kiểm chứng timeout/backoff path?

**Red flags:**

- `Thread.sleep(5000)`; retry vô hạn; chỉ kiểm tra container state “running”.

**Evidence:**

- Theory: [Core](../theory/core/testing-strategy-and-hermetic-tests.md)
- Deep-dive: [Lifecycle/isolation/CI failure modes](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)
- Learning case: `TEST-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### TEST-HERMETIC-009 — `SENIOR`

**Question:**

Bạn quyết định container lifecycle và isolation khi chạy test song song như thế nào: container-per-method, per-class, singleton per JVM hay shared external service?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có cân bằng startup cost với state isolation và hiểu parallel contamination.

**Answer outline:**

1. Container-per-method isolation mạnh nhưng đắt; per-class/JVM tái sử dụng nhanh hơn nhưng cần namespace/data cleanup chặt.
2. Tách database/schema/key prefix/vhost/topic/queue theo test worker hoặc run ID; không dựa vào execution order.
3. Singleton lifecycle phải khớp Spring context cache; tránh container chết trong khi cached context vẫn giữ connection cũ.
4. Shared external service giảm startup nhưng thêm state drift, availability và ownership ngoài test process.

**Required trade-offs:**

- Tối ưu runtime không được hy sinh repeatability; isolation strategy phụ thuộc semantics của từng dependency.

**Follow-up ladder:**

- Foundation: Static container giúp gì?
- Senior: Redis key prefix có đủ để cô lập mọi command không?
- Architect: Chia CI workers theo resource budget và suite topology thế nào?
- Expert: Reuse local container có nên được dùng làm CI evidence không?

**Red flags:**

- Chạy tuần tự để che contamination; `FLUSHALL` hoặc xóa toàn broker/database dùng chung không có scope.

**Evidence:**

- Theory: [Core](../theory/core/testing-strategy-and-hermetic-tests.md)
- Deep-dive: [Lifecycle/isolation/CI failure modes](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)
- Learning case: `TEST-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### TEST-HERMETIC-010 — `SENIOR`

**Question:**

Một integration test thỉnh thoảng fail trên CI nhưng pass khi rerun. Bạn điều tra và xử lý thế nào? Retry có vai trò gì và không được dùng để làm gì?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có workflow diagnosis dựa trên evidence và ownership thay vì gắn retry để làm xanh pipeline.

**Answer outline:**

1. Giữ failure artifact: seed, timestamps, thread dump/log, container log, resource metrics, test order/worker và environment version.
2. Phân loại timing/race, shared state, resource starvation, external dependency, nondeterministic input hoặc product defect.
3. Reproduce bằng repeated/parallel/stress run có cùng seed/topology; sửa root cause và thêm assertion/diagnostic guard.
4. Retry chỉ dùng có giới hạn để đo/giảm transient external risk đã hiểu; phải đếm retry rate và không biến first-attempt failure thành pass im lặng.

**Required trade-offs:**

- Quarantine bảo vệ signal của pipeline tạm thời nhưng tạo quality debt cần owner, deadline và visibility.

**Follow-up ladder:**

- Foundation: Flaky test và flaky product khác nhau thế nào?
- Senior: Làm sao phát hiện test order dependency?
- Architect: Policy quarantine/retry nào ngăn suite mất niềm tin?
- Expert: Bạn dùng statistical evidence nào để khẳng định đã hết flaky?

**Red flags:**

- Retry vô hạn; tăng sleep/timeout không có hypothesis; xóa test vì “CI chậm”.

**Evidence:**

- Theory: [Core](../theory/core/testing-strategy-and-hermetic-tests.md)
- Deep-dive: [Lifecycle/isolation/CI failure modes](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)
- Learning case: `TEST-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### TEST-HERMETIC-011 — `ARCHITECT`

**Question:**

Thiết kế risk-based test portfolio cho backend dùng Spring Security, PostgreSQL, Redis và RabbitMQ. Bạn phân bổ invariant nào vào unit, integration, contract và end-to-end layer?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có nối business/security/data/delivery risk với evidence layer, ownership và cost.

**Answer outline:**

1. Lập risk register theo impact, likelihood, change frequency và detectability; xác định invariant không được phép escape.
2. Unit/property tests cho business state transition; HTTP/security integration cho authentication/authorization/status/payload.
3. PostgreSQL integration cho constraint/query/transaction; Redis integration cho serialization/TTL/fallback; RabbitMQ integration cho publish/consume/topology/retry semantics.
4. Contract tests cho producer-consumer/API compatibility; chỉ một số critical journeys đi end-to-end, kèm smoke/rollback checks.
5. Mỗi layer có owner, runtime budget và failure diagnostics; không đặt cùng một assertion ở mọi layer nếu không thêm signal.

**Required trade-offs:**

- Portfolio phải tối ưu confidence-per-cost, không tối đa số test hoặc coverage.

**Follow-up ladder:**

- Foundation: Risk matrix gồm các chiều nào?
- Senior: Cache fallback invariant nên test ở boundary nào?
- Architect: Khi suite vượt runtime budget, bạn cắt test theo nguyên tắc gì?
- Expert: Làm sao định lượng confidence khi các test không độc lập về failure mode?

**Red flags:**

- Một pyramid tỷ lệ cố định cho mọi system; mọi scenario đều end-to-end; không có negative/security tests.

**Evidence:**

- Theory: [Core](../theory/core/testing-strategy-and-hermetic-tests.md)
- Deep-dive: [Lifecycle/isolation/CI failure modes](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)
- Learning case: `TEST-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### TEST-HERMETIC-012 — `ARCHITECT`

**Question:**

Thiết kế CI topology cho integration-test suite có nhiều container như thế nào để vừa tái lập, vừa đủ nhanh, không làm runner cạn CPU/memory/port?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có capacity model, isolation boundary và strategy chia pipeline thay vì chỉ bật parallelism.

**Answer outline:**

1. Đo startup/test time, CPU, memory, disk/network và connection demand theo suite; đặt concurrency từ runner capacity, không từ core count đơn thuần.
2. Chia fast deterministic gate và slower infrastructure/risk suites; dùng dependency/image cache có version pin, không reuse mutable data state.
3. Namespace resource theo build/worker; đặt timeout, cleanup và artifact collection khi fail.
4. Dùng change/risk selection thận trọng, nhưng merge/release gate vẫn chạy mandatory invariant suite.
5. Theo dõi queue time, p95 duration, failure/retry rate và cost để điều chỉnh topology.

**Required trade-offs:**

- Parallelism giảm wall-clock nhưng có thể tăng contention/flakiness; sharding tăng complexity và nguy cơ bỏ sót dependency giữa tests.

**Follow-up ladder:**

- Foundation: Cache image khác reuse container state thế nào?
- Senior: Vì sao tăng worker có thể làm suite chậm hơn?
- Architect: PR, main và release pipeline cần gate khác nhau ra sao?
- Expert: Làm sao chống “selective testing” bỏ lọt change có blast radius khó suy luận?

**Red flags:**

- Parallel không giới hạn; chỉ tối ưu average duration; bỏ cleanup vì runner “ephemeral”.

**Evidence:**

- Theory: [Core](../theory/core/testing-strategy-and-hermetic-tests.md)
- Deep-dive: [Lifecycle/isolation/CI failure modes](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)
- Learning case: `TEST-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### TEST-HERMETIC-013 — `ARCHITECT`

**Question:**

Bạn thiết kế test data và schema-migration strategy nào để chứng minh ứng dụng bootstrap được từ database sạch đồng thời vẫn nâng cấp được dữ liệu đang tồn tại?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có phân biệt clean bootstrap, upgrade path và representative data; không coi một happy-path migration là đủ.

**Answer outline:**

1. Clean-database test chạy toàn bộ versioned migrations rồi startup ứng dụng; schema không được tạo ngầm bởi ORM.
2. Upgrade tests bắt đầu từ supported prior schema/data snapshots, chạy migration và verify invariant/compatibility.
3. Fixture tối thiểu bảo vệ branch; representative dataset kiểm tra constraint, type, cardinality và performance-sensitive query riêng.
4. Migration phải idempotent theo deployment contract phù hợp, có rollback/forward-fix decision và kiểm tra mixed-version nếu rollout yêu cầu.
5. Snapshot phải có provenance/version và không chứa dữ liệu nhạy cảm.

**Required trade-offs:**

- Snapshot lớn tăng realism nhưng chậm, dễ stale và khó review; synthetic fixture dễ kiểm soát nhưng có thể bỏ sót data pathology.

**Follow-up ladder:**

- Foundation: Clean bootstrap và migration upgrade test khác gì?
- Senior: Vì sao `ddl-auto` có thể che migration thiếu?
- Architect: Support bao nhiêu prior versions trong matrix?
- Expert: Test expand/contract migration dưới rolling deployment thế nào?

**Red flags:**

- Chỉ test schema cuối; lấy production dump thô làm fixture; rollback database được giả định luôn an toàn.

**Evidence:**

- Theory: [Core](../theory/core/testing-strategy-and-hermetic-tests.md)
- Deep-dive: [Lifecycle/isolation/CI failure modes](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)
- Learning case: `TEST-01 NOT CREATED`; future migration evidence thuộc `MIG-01/DB-04`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### TEST-HERMETIC-014 — `ARCHITECT`

**Question:**

Bạn định nghĩa release-confidence gate cho một team như thế nào? Coverage, test pass rate và absence of known failures có đủ để quyết định release không?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có xây gate từ invariant/risk/evidence và residual risk, không từ một vanity metric.

**Answer outline:**

1. Gate gồm mandatory invariant suites, security/static/dependency checks phù hợp, migration/rollback readiness và operational smoke signals.
2. Coverage chỉ chỉ ra code được chạy, không chứng minh assertion đúng hoặc failure mode đã cover; pass rate vô nghĩa nếu tests flaky/irrelevant.
3. Known failure cần severity, blast radius, mitigation, owner và explicit risk acceptance; critical invariant failure chặn release.
4. Dùng escaped defect, flaky rate, mutation/negative-test quality, change risk và production signal để cải tiến portfolio.
5. Gate phải nhanh đủ cho feedback nhưng tách merge, deploy và progressive-delivery verification khi cần.

**Required trade-offs:**

- Gate quá yếu tạo regression escape; gate quá nặng làm team bypass hoặc batch change lớn hơn, tăng risk.

**Follow-up ladder:**

- Foundation: Line coverage không nói được điều gì?
- Senior: Một test bị quarantine có được tính pass không?
- Architect: Ai có quyền override gate và audit trail nào bắt buộc?
- Expert: Làm sao hiệu chỉnh gate bằng production incident mà không overfit sự cố gần nhất?

**Red flags:**

- “80% coverage là release được”; override không owner/expiry; coi pipeline xanh là bằng chứng hệ thống an toàn tuyệt đối.

**Evidence:**

- Theory: [Core](../theory/core/testing-strategy-and-hermetic-tests.md)
- Deep-dive: [Lifecycle/isolation/CI failure modes](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)
- Learning case: `TEST-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### TEST-HERMETIC-015 — `EXPERT`

**Question:**

Thiết kế một deterministic test để tái hiện race hoặc distributed failure hiếm gặp mà không dựa vào `sleep`. Làm sao chứng minh test thật sự ép đúng interleaving/failure window?

**Target depth:** `D4`

**Interviewer evaluates:**

- Có kiểm soát scheduler/failure boundary, quan sát causal chain và phân biệt deterministic reproducer với stress test xác suất.

**Answer outline:**

1. Xác định invariant, actors, shared state, happens-before/failure window và interleaving tối thiểu làm invariant vỡ.
2. Thêm controllable seam ở boundary có ý nghĩa: barrier/latch, transaction hook, broker proxy, fake clock hoặc fault injector; không chèn test flag vào logic quyết định.
3. Dùng barrier buộc actor A dừng sau state transition cụ thể, chạy actor B/failure, rồi release A; assertion kiểm tra cả invariant và intermediate evidence.
4. Instrument event/order/transaction IDs để chứng minh causal sequence; test phải fail trên buggy version và pass sau fix.
5. Bổ sung stress/property/fault run để tìm interleaving chưa mô hình hóa; deterministic reproducer giữ regression gate.

**Required trade-offs:**

- Seam tăng khả năng kiểm soát nhưng có thể làm model lệch production; stress test realistic hơn về schedule nhưng không bảo đảm tái lập.

**Follow-up ladder:**

- Foundation: Barrier khác sleep ở điểm nào?
- Senior: Test lost update giữa hai transaction thế nào?
- Architect: Fault-injection seam nên thuộc application, proxy hay test harness?
- Expert: Bạn kiểm tra observer effect của instrumentation ra sao?

**Red flags:**

- Chạy loop 10.000 lần rồi gọi là deterministic; sleep dài hơn; không chứng minh failure window đã được đi qua.

**Evidence:**

- Theory: [Core](../theory/core/testing-strategy-and-hermetic-tests.md)
- Deep-dive: [Lifecycle/isolation/CI failure modes](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)
- Learning case: `TEST-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### TEST-HERMETIC-016 — `EXPERT`

**Question:**

Một test suite mất 90 phút nhưng vẫn để lọt regression nghiêm trọng. Bạn quyết định giữ, bỏ, viết lại hoặc chuyển layer từng nhóm test dựa trên evidence nào?

**Target depth:** `D4`

**Interviewer evaluates:**

- Có tối ưu confidence portfolio theo failure-detection value, dependency và operational cost thay vì cắt test theo thời gian đơn thuần.

**Answer outline:**

1. Map test tới invariant/risk/owner và lịch sử defect nó phát hiện; tìm duplicate assertions, blind spots và tests chỉ khóa implementation.
2. Đo duration, flakiness, failure uniqueness, mutation sensitivity hoặc seeded-defect detection và diagnostic quality.
3. Chuyển exhaustive branch xuống layer rẻ hơn; giữ integration/contract/end-to-end cho boundary semantics mà layer dưới không chứng minh được.
4. Xóa hoặc viết lại test không bảo vệ claim rõ ràng; mọi deletion của critical coverage phải có replacement evidence.
5. Dùng escaped regressions/incident postmortem để cập nhật risk model, không phản ứng bằng cách thêm end-to-end test cho mọi bug.

**Required trade-offs:**

- Suite nhanh cải thiện feedback nhưng giảm redundancy quá mức có thể tạo single-point blind spot; mutation/seeded-fault analysis cũng có runtime và maintenance cost.

**Follow-up ladder:**

- Foundation: Duplicate test và defense-in-depth khác nhau thế nào?
- Senior: Test chậm nhưng duy nhất phát hiện serialization drift nên xử lý ra sao?
- Architect: Đặt runtime budget và SLO cho suite thế nào?
- Expert: Làm sao ước lượng marginal confidence của một test khi failures có correlation?

**Red flags:**

- Xóa test chậm nhất trước; giữ mọi test “vì biết đâu cần”; dùng coverage làm tiêu chí duy nhất.

**Evidence:**

- Theory: [Core](../theory/core/testing-strategy-and-hermetic-tests.md)
- Deep-dive: [Lifecycle/isolation/CI failure modes](../theory/deep-dives/testcontainers-lifecycle-isolation-and-ci-failure-modes.md)
- Learning case: `TEST-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `TEST-01` thực sự active:

1. Tạo core theory/deep-dive theo checkpoint rồi thay các marker `NOT CREATED` bằng canonical links.
2. Nối câu hỏi với current project test path, reproducer và experiment thật; không điền evidence hồi tố từ nội dung AI.
3. Review lại coverage theo failure đã quan sát và chỉ thêm/bớt câu khi rubric còn blind spot hoặc trùng lặp.
4. Giữ nguyên stable ID; nếu bỏ câu, đánh dấu deprecated và trỏ sang câu thay thế thay vì tái sử dụng ID.

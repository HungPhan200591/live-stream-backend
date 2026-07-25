# Senior Java Knowledge Depth Rubric

> Trạng thái: `CANONICAL SELF-ASSESSMENT RUBRIC`<br>
> Owner phạm vi/priority: [Senior Java Roadmap](../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md)<br>
> Cập nhật: 2026-07-25

## 1. Mục đích và cách đọc

D1-D4 là thang đo **biết sâu đến đâu** cho từng năng lực; nó tách khỏi P0-P3, vốn đo **mức quan trọng**. Một capability P0 thường cần đạt D3 cho mục tiêu Senior, nhưng không vì thế mà mọi technology trong capability đó đều phải được triển khai ở production.

| Depth | Nghĩa là | Bằng chứng tối thiểu |
| --- | --- | --- |
| **D1 - Nhận diện đúng** | Biết khái niệm, vocabulary, vị trí nó xuất hiện và boundary cơ bản | Giải thích ngắn đúng thuật ngữ, nhận ra trong code/log/design; không nhầm với khái niệm gần kề |
| **D2 - Giải thích và chọn** | Nói được mechanism, so sánh alternative và biết khi nào dùng/không dùng | Trả lời được câu hỏi `why/when`, vẽ được flow/timeline hoặc decision matrix và nêu trade-off |
| **D3 - Áp dụng và debug** | Tái hiện failure, implement/test, đo evidence và xử lý failure thực tế | Reproducer hoặc test, implementation/ADR phù hợp, metric/log/trace/query plan và recovery/negative case khi cần |
| **D4 - Dẫn dắt và tiến hóa** | Thiết kế qua nhiều constraint, xử lý incident, teach-back/mentor và bảo vệ quyết định | Architecture/incident evidence, review/ADR, 2 phút + 15 phút teach-back và kế hoạch tiến hóa có owner/trigger revisit |

Chỉ tự nhận một mức khi **toàn bộ mức thấp hơn đã đạt**. D3/D4 không được suy ra từ số năm kinh nghiệm, số bài đọc hoặc số endpoint đã viết. Maturity M0-M4 trong current-state assessment vẫn đo độ trưởng thành của implementation; đừng dùng nó thay cho D1-D4.

## 2. Cách tự đánh giá và ghi tiến độ

1. Chọn capability từ roadmap và đọc D-target của nó.
2. Đọc lần lượt D1 đến D4 bên dưới; đánh dấu mức cao nhất có thể chứng minh hôm nay.
3. Nếu thiếu một bullet, ghi mức thấp hơn và chọn **một** next action nhỏ ở learning cursor/case active.
4. Link evidence thật: theory tự diễn giải, question-bank answer, test/reproducer, experiment, ADR, dashboard/runbook hoặc interview note. Không dùng lời tự nhận làm evidence.
5. Cập nhật cột `Current depth` và `Evidence / next action` trong tracker của tài liệu này chỉ sau khi evidence tồn tại; [Learning System](index.md) chỉ giữ routing/checkpoint. Khi đổi mức, ghi link artifact hoặc case ID; không tự nâng vì đã đọc tài liệu.

### 2.1. Current depth tracker

Đây là baseline tracking của hệ thống, không phải đánh giá năng lực sẵn có của người học. Điền mức sau teach-back hoặc evidence thật; dùng `NEEDS_REVALIDATION` khi evidence đã cũ hoặc system context thay đổi.

| Capability | Target | Current depth | Evidence / next action |
| --- | --- | --- | --- |
| Java language, collections, algorithm và complexity | D3 | Chưa đánh giá | Chọn case/lab khi Stage 1 active |
| Object-oriented design và refactoring | D3 | Chưa đánh giá | Chọn case/lab khi Stage 1 active |
| JVM runtime và diagnostics | D3 | Chưa đánh giá | Chọn case/lab khi Stage 1/8 active |
| Concurrency, JMM và async model | D3 | Chưa đánh giá | Chọn case/lab khi Stage 1 active |
| Spring Framework và Spring Boot | D3 | Chưa đánh giá | Chọn case/lab khi Stage 2 active |
| HTTP, API design và network fundamentals | D3 | Chưa đánh giá | Chọn case/lab khi Stage 2 active |
| Transaction và data consistency | D3 | Chưa đánh giá | Chọn case/lab khi Stage 2 active |
| Security và identity | D3 | Chưa đánh giá | SEC-01 đang ở `THEORY_CORE`; chưa có theory/evidence |
| PostgreSQL và data modeling | D3 | Chưa đánh giá | Chọn case/lab khi Stage 3 active |
| Testing và quality strategy | D3 | Chưa đánh giá | Chọn case/lab khi Stage 0/8 active |
| Observability, reliability và incident response | D3 | Chưa đánh giá | Chọn case/lab khi Stage 8 active |
| Distributed systems fundamentals | D3 | Chưa đánh giá | Chọn case/lab khi Stage 10/11 active |
| Solution architecture | D3 | Chưa đánh giá | Chọn capstone khi Stage 11 active |
| Technical leadership và delivery | D3 | Chưa đánh giá | Ghi evidence từ review/ADR/incident thật |
| Redis | D3 | Chưa đánh giá | Chọn case/lab khi Stage 4 active |
| RabbitMQ, Kafka và event-driven workflow | D3 | Chưa đánh giá | Chọn case/lab khi Stage 5/6 active |
| Domain modeling và modular architecture | D3 | Chưa đánh giá | Chọn case/lab khi Stage 10 active |
| Git, Linux, container, build và CI/CD | D2-D3 | Chưa đánh giá | Chọn case/lab khi Stage 0/8 active |
| Data operations và lifecycle | D2-D3 | Chưa đánh giá | Chọn case/lab khi Stage 3/9 active |
| Microservice architecture | D2-D3 | Chưa đánh giá | Chọn case/lab khi Stage 10 active |
| Cloud, Kubernetes và IaC | D1-D2 | Chưa đánh giá | Chỉ chọn khi target role/case yêu cầu |
| Storage selection ngoài RDBMS | D1-D2 | Chưa đánh giá | Chỉ chọn khi capstone/case yêu cầu |
| Reactive programming và WebFlux | D1-D2 | Chưa đánh giá | Chỉ chọn khi workload/case yêu cầu |
| gRPC, GraphQL, native image và platform-specific stack | D1 | Chưa đánh giá | Chỉ chọn khi target role/case yêu cầu |

## 3. Rubric theo capability

### 3.1. Java language, collections, algorithm và complexity — P0, target D3

- **D1:** Phân biệt primitive/reference, immutable/mutable, `equals`/`hashCode`, `List`/`Set`/`Map`, array/list/tree/hash/queue và Big-O cơ bản.
- **D2:** Giải thích generics/type erasure, exception contract, Stream API, time/locale/money boundary; chọn data structure dựa trên access pattern, ordering, memory và complexity.
- **D3:** Viết test cho equality/money/time edge case; thay collection hoặc algorithm trên hot path, đo allocation/latency và giải thích before/after; xử lý serialization compatibility tại boundary.
- **D4:** Đặt guideline chọn collection/value object cho module, review được complexity claim sai và dẫn một refactor có benchmark, compatibility và rollback plan.

### 3.2. Object-oriented design và refactoring — P0, target D3

- **D1:** Nhận ra encapsulation, composition, inheritance, polymorphism, coupling/cohesion và các mùi code như god service, primitive obsession, switch phình to.
- **D2:** So sánh state/strategy/adapter/decorator với hàm/if đơn giản; giải thích SOLID là heuristic và chỉ ra khi abstraction làm code khó hiểu hơn.
- **D3:** Viết characterization/invariant test trước refactor; tách boundary hoặc thay pattern mà không đổi behavior, ghi alternative bị loại và kiểm tra dependency direction.
- **D4:** Dẫn review/refactor xuyên module, cân bằng maintainability với delivery risk, mentor người khác qua trade-off và đặt trigger khi abstraction cần được đơn giản hóa.

### 3.3. JVM runtime và diagnostics — P0, target D3

- **D1:** Biết vai trò class loading, stack, heap, metaspace, direct memory, GC roots, JIT, warm-up, safepoint, G1 và ZGC.
- **D2:** Giải thích allocation rate, promotion, pause, CPU-bound/blocking, memory leak và thread leak khác nhau thế nào; chọn đúng công cụ JFR, GC log, heap dump hoặc thread dump.
- **D3:** Tạo hoặc bắt được triệu chứng CPU/memory/GC/lock thật; đọc JFR/dump để nối symptom với code path, đưa hypothesis rồi kiểm chứng bằng thay đổi/measurement.
- **D4:** Dẫn incident performance, đặt budget heap/latency/allocation, review tuning proposal có rủi ro và trình bày vì sao không tối ưu JVM khi bottleneck nằm ở I/O/database.

### 3.4. Concurrency, JMM và async model — P0, target D3

- **D1:** Phân biệt atomicity, visibility, ordering, race condition, deadlock, starvation, `synchronized`, `Lock`, atomic class, executor và `ThreadLocal`.
- **D2:** Giải thích happens-before, contention, bounded queue, cancellation, backpressure; so sánh platform thread, virtual thread và reactive model theo loại workload.
- **D3:** Viết race/repeat test hoặc reproducer, giữ invariant bằng lock/conditional update/idempotency phù hợp, đo conflict/throughput và debug deadlock/thread starvation.
- **D4:** Thiết kế concurrency policy liên module/service, review ownership/cancellation/context propagation, xử lý incident saturation và teach-back trade-off giữa simplicity, throughput và operability.

### 3.5. Spring Framework và Spring Boot — P0, target D3

- **D1:** Nhận ra IoC/DI, bean lifecycle, scope, proxy/AOP, configuration properties, auto-configuration, validation và MVC request pipeline.
- **D2:** Giải thích proxy/self-invocation, condition report, filter vs interceptor vs controller advice, bean startup failure và configuration precedence.
- **D3:** Tái hiện một lỗi proxy/configuration/pipeline bằng slice/context test; sửa đúng layer và chứng minh auto-configuration/validation/security behavior bằng test hoặc log.
- **D4:** Thiết kế module boundary và bootstrapping policy, review startup/performance/config drift, hướng dẫn team debug Spring internals thay vì thêm annotation thử ngẫu nhiên.

### 3.6. HTTP, API design và network fundamentals — P0, target D3

- **D1:** Biết HTTP method/status/header, safe/idempotent semantics, URI/query/path, JSON boundary, TLS, DNS, TCP, proxy và load balancer ở mức vai trò.
- **D2:** Giải thích idempotency key, pagination, cache/ETag, versioning/deprecation, backward compatibility, timeout categories và retry safety.
- **D3:** Thiết kế/sửa API có contract và negative test cho idempotency/compatibility; tái hiện client timeout hoặc proxy/header trust issue, đo request timeline và đặt timeout/retry đúng chỗ.
- **D4:** Dẫn evolution public API qua nhiều consumer, đặt quota/cache/deprecation plan, xử lý incident network/client và bảo vệ trade-off giữa compatibility, security, latency và cost.

### 3.7. Transaction và data consistency — P0, target D3

- **D1:** Biết transaction boundary, commit/rollback, isolation, propagation, lock, lost update và khác biệt giữa database side effect với external side effect.
- **D2:** Giải thích `REQUIRED`/`REQUIRES_NEW`, self-invocation, rollback rule, write skew, after-commit, idempotency và vì sao remote call trong transaction nguy hiểm.
- **D3:** Viết integration test tái hiện anomaly/crash window; chọn isolation/constraint/lock/outbox phù hợp, đọc transaction timeline và chứng minh invariant vẫn đúng khi failure.
- **D4:** Thiết kế consistency contract xuyên module/service, review transaction boundary, dẫn phân tích incident double-spend/partial failure và xác định compensating/recovery policy.

### 3.8. Security và identity — P0, target D3

- **D1:** Phân biệt authentication, authorization, session, JWT, access/refresh token, OAuth2, OIDC, RBAC, ownership, secret, hash, encryption và HMAC.
- **D2:** Giải thích token audience/scope/expiry/rotation, authorization code + PKCE, webhook replay protection, CORS/CSRF/SSRF, TLS termination và OWASP API risks.
- **D3:** Viết negative authorization/security test; tái hiện token misuse, replay hoặc broken object authorization; triển khai/đánh giá key-secret rotation, redaction, dependency scan và incident response path.
- **D4:** Lập threat model xuyên trust boundary, review security design với product/ops, dẫn credential compromise drill và bảo vệ trade-off giữa UX, least privilege, audit, compliance và recovery.

### 3.9. PostgreSQL và data modeling — P0, target D3

- **D1:** Biết table/key/constraint, normalization, index, MVCC, transaction isolation, lock, vacuum, query plan, connection pool và ORM persistence context.
- **D2:** Giải thích N+1, dirty checking/flush, fetch/projection, B-tree/composite/partial index, selectivity, write amplification, bloat và pool exhaustion.
- **D3:** Thiết kế migration + constraint giữ invariant; đọc `EXPLAIN (ANALYZE, BUFFERS)`, tái hiện lock/deadlock/N+1/pool exhaustion và cải thiện bằng query/index/schema có before/after evidence.
- **D4:** Dẫn review data model/query strategy, đặt data access SLO/capacity, xử lý DB incident và quyết định khi nào không thêm index/denormalization vì write/operation cost.

### 3.10. Testing và quality strategy — P0, target D3

- **D1:** Phân biệt unit, slice, integration, contract, property, concurrency, load và fault test; hiểu assertion, fixture, mock và Testcontainers.
- **D2:** Chọn test layer theo risk, biết false confidence của mock/coverage, giải thích deterministic time/ID/data và test pyramid theo hệ thống này.
- **D3:** Viết test tái hiện business/security/concurrency failure, giữ test hermetic/repeatable trong CI, review flaky test và dùng evidence để quyết định test nào cần thêm/bỏ.
- **D4:** Thiết kế quality gate/release confidence cho team, dẫn xử lý regression escape, review test strategy theo blast radius và mentor cách viết test bảo vệ invariant thay vì implementation detail.

### 3.11. Observability, reliability và incident response — P0, target D3

- **D1:** Biết log/metric/trace, correlation ID, SLI/SLO, p50/p95/p99, saturation, alert, runbook, error budget và incident timeline.
- **D2:** Giải thích metric nào trả lời symptom nào, sampling/redaction, dashboard vs alert, leading/lagging signal và vì sao một alert phải actionable.
- **D3:** Instrument hot path, lần một failure qua log/trace/metric, tạo alert + runbook, inject DB/Redis/broker/resource fault và ghi evidence recovery.
- **D4:** Điều phối incident drill/postmortem, đặt SLO/capacity policy, ưu tiên reliability work theo impact và truyền đạt residual risk tới stakeholder.

### 3.12. Distributed systems fundamentals — P0, target D3

- **D1:** Biết partial failure, timeout, duplicate, reorder, network partition, clock skew, consistency, availability, leader/quorum và backpressure.
- **D2:** Giải thích CAP/PACELC trong context cụ thể, read-your-writes, eventual consistency, retry/idempotency, ordering key và vì sao timeout không đồng nghĩa operation đã thất bại.
- **D3:** Viết failure matrix/timeline cho flow distributed, tái hiện duplicate/out-of-order/timeout, chọn consistency/retry/dedup/recovery policy và kiểm chứng invariant.
- **D4:** Thiết kế cross-service consistency model, dẫn trade-off với product/ops, review failure domain và dạy team tránh dùng distributed-system slogan thay cho evidence.

### 3.13. Solution architecture — P0, target D3

- **D1:** Biết capacity, throughput, latency, bottleneck, HA, DR, RPO/RTO, failure domain, cost và evolution path.
- **D2:** Ước lượng capacity bằng assumption rõ ràng; so sánh scale up/out, sync/async, single/multi-region, managed/self-managed và chỉ ra bottleneck đầu tiên.
- **D3:** Viết architecture dossier cho một capstone gồm data flow, capacity math, threat/failure model, consistency, cost, observability và phased rollout; phản biện được alternative.
- **D4:** Dẫn architecture review đa stakeholder, cập nhật design sau incident/workload mới, quản lý decision debt và trình bày cùng một thiết kế ở bản 2/15/45 phút.

### 3.14. Technical leadership và delivery — P0, target D3

- **D1:** Biết ADR, code review, incident/postmortem, technical debt, prioritization, mentorship, stakeholder và STAR/CAR story.
- **D2:** Phân biệt blocker/suggestion, reversible/irreversible decision, impact/urgency, ownership/accountability và cách đưa feedback có thể hành động.
- **D3:** Viết/review ADR, làm review có finding cụ thể, dẫn một incident drill hoặc delivery plan có rollback, kể được behavioral story gắn evidence thật.
- **D4:** Điều phối decision liên team, mentor/review standard, xử lý conflict minh bạch, cân bằng roadmap/business/risk và thay đổi quy trình dựa trên postmortem data.

### 3.15. Redis — P1, target D3

- **D1:** Biết cache-aside, TTL, eviction, key/value serialization, cache stampede, rate limit, distributed lock, ZSET và HyperLogLog.
- **D2:** Giải thích cache consistency/invalidation, TTL jitter, stale-while-revalidate, single-flight, ownership token/fencing và vì sao Redis không là source of truth cho money/security.
- **D3:** Tạo reproducer cache stampede/Redis timeout; triển khai mitigation, đo hit/miss/error/latency, document key schema/TTL/owner và chứng minh degraded mode/recovery.
- **D4:** Thiết kế cache strategy xuyên service, review memory/cardinality/cost, dẫn Redis outage drill và bảo vệ trade-off consistency, availability và operational complexity.

### 3.16. RabbitMQ, Kafka và event-driven workflow — P1, target D3

- **D1:** Phân biệt work queue với durable event log; biết ACK, DLQ, offset, partition, consumer group, retention, replay, outbox và inbox.
- **D2:** Giải thích at-most/at-least-once, ordering scope, idempotent consumer, retry/backoff, poison message, schema evolution và RabbitMQ/Kafka selection.
- **D3:** Tái hiện consumer crash/duplicate/rebalance/lag; implement outbox/inbox/dedup phù hợp, đo queue/lag và chạy replay/recovery theo runbook.
- **D4:** Thiết kế event contract/ownership nhiều team, dẫn migration/replay incident, review exactly-once claim và cân bằng delivery guarantee với cost/operability.

### 3.17. Domain modeling và modular architecture — P1, target D3

- **D1:** Biết bounded context, ubiquitous language, aggregate, invariant, entity/value object, module API, data owner và domain event.
- **D2:** Giải thích aggregate/transaction boundary, package-by-feature, shared-kernel risk, anti-corruption boundary và khi nào không cần DDD tactical pattern.
- **D3:** Vẽ context/module map, xác định owner/invariant, tạo architecture test hoặc Spring Modulith rule và refactor dependency vi phạm có ADR/evidence.
- **D4:** Dẫn discovery với business, quản lý boundary evolution/ownership conflict, review extraction readiness và teach-back domain trade-off cho team mới.

### 3.18. Git, Linux, container, build và CI/CD — P1, target D2-D3

- **D1:** Biết diff/history/branch/merge/revert/bisect, process/thread/signal/socket/file descriptor, image/container, Maven dependency/BOM và CI pipeline.
- **D2:** Giải thích conflict resolution, reproducible build, dependency mediation, SBOM/CVE, image layer/non-root, cgroup CPU/memory, probe và rolling/canary/rollback.
- **D3:** Dùng Git để tìm/regress/revert change an toàn; debug process/socket/resource issue; tạo build/image/pipeline có artifact, scan, probe, resource policy và rollback rehearsal.
- **D4:** Dẫn release/incident delivery, đặt engineering guardrail cho dependency/supply chain, review platform change và cân bằng developer velocity với reliability/security/cost.

### 3.19. Data operations và lifecycle — P1, target D2-D3

- **D1:** Biết migration, expand-contract, backup logical/physical, WAL archive, PITR, replica, retention, archive, partition và RPO/RTO.
- **D2:** Giải thích migration lock/compatibility, restore vs backup, read replica lag, partition pruning, legal/privacy deletion và failover/failback risk.
- **D3:** Rehearse safe migration trên dataset đại diện; thực hành restore/PITR cô lập, đối soát invariant/thời gian/RPO và viết runbook retention/failover.
- **D4:** Đặt data lifecycle/DR policy với owner, dẫn recovery incident/game day, review data risk/cost/compliance và tiến hóa plan khi volume/retention thay đổi.

### 3.20. Microservice architecture — P1, target D2-D3

- **D1:** Biết service boundary, service-owned data, synchronous call, event contract, gateway, discovery, circuit breaker, bulkhead và load shedding.
- **D2:** Giải thích chi phí network/operation, distributed trace, contract compatibility, extraction scorecard và vì sao modular monolith thường là điểm bắt đầu tốt hơn.
- **D3:** Làm extraction decision bằng coupling/scale/deploy evidence; thiết kế service contract, timeout/retry/idempotency/observability/runbook và kiểm chứng bằng contract/fault test.
- **D4:** Dẫn decomposition/evolution nhiều team, xử lý ownership/versioning incident, review platform cost và quyết định giữ/gộp/tách service dựa trên data thay vì xu hướng.

### 3.21. Cloud, Kubernetes và IaC — P2, target D1-D2

- **D1:** Biết workload, deployment, service, ingress, config/secret, IAM, probe, request/limit, autoscaling, managed service và infrastructure as code.
- **D2:** Giải thích readiness/liveness/startup, cgroup/OOM/throttling, IAM least privilege, managed vs self-managed, IaC drift và cloud cost/lock-in.
- **D3:** Chỉ cần khi role yêu cầu: deploy lab có probe/resource/secret/rollback, quan sát failure và ghi decision về platform/managed service/IaC.
- **D4:** Chỉ khi theo platform/architect track: dẫn multi-team platform policy, incident/cost/security governance và evolution strategy; không mặc định là yêu cầu Senior backend phổ thông.

### 3.22. Storage selection ngoài RDBMS — P2, target D1-D2

- **D1:** Biết mục đích của document store, key-value, search index, object storage, columnar/analytics store và cache.
- **D2:** So sánh access pattern, query model, consistency, indexing, retention, recovery, operations và cost để chọn PostgreSQL/Redis/search/object/columnar storage.
- **D3:** Chỉ khi case cần: làm spike/dataset nhỏ, benchmark query/index/ingestion, viết decision matrix và migration/exit strategy.
- **D4:** Chỉ khi architecture track yêu cầu: quản lý polyglot persistence, ownership/backup/compliance/cost nhiều store và loại bỏ store không còn justification.

### 3.23. Reactive programming và WebFlux — P2, target D1-D2

- **D1:** Biết blocking/non-blocking I/O, Reactor `Mono`/`Flux`, backpressure, event loop, scheduler và context propagation.
- **D2:** Giải thích MVC, MVC + virtual threads và WebFlux khác nhau theo workload, blocking dependency, streaming, debugging và operational constraint.
- **D3:** Chỉ khi case cần: benchmark đúng workload, tái hiện blocking-on-event-loop/context issue, instrument thread/latency và ghi decision không/đổi stack.
- **D4:** Chỉ khi role chuyên sâu: dẫn migration reactive end-to-end, đặt scheduler/context/backpressure policy và xử lý incident saturation/leak xuyên stack.

### 3.24. gRPC, GraphQL, native image và platform-specific stack — P3, target D1

- **D1:** Biết problem mà gRPC/Protobuf, GraphQL hoặc native image giải quyết và vocabulary cơ bản: schema, streaming, resolver, reflection/AOT, startup/memory trade-off.
- **D2:** Chỉ khi job/case yêu cầu: so sánh với REST/JVM deployment theo contract, tooling, compatibility, observability, team skill và operation.
- **D3:** Chỉ khi đã có justification: làm spike nhỏ có contract/test/measurement và ADR nêu benefit, constraint, rollback/exit strategy.
- **D4:** Chỉ theo specialization: sở hữu platform/runtime contract, migration/version governance và production incident/capacity evidence; không đặt làm baseline Senior Java backend.

## 4. Khi nào cần đổi mức?

- Từ **D1 lên D2**: tự trả lời được `why/when/not when` và so sánh ít nhất hai alternative mà không cần đọc lại note.
- Từ **D2 lên D3**: có failure hoặc use case thật, artifact tái lập được và evidence cho quyết định; với topic thuần thiết kế phải có scenario, constraint và review/teach-back thay cho code bắt buộc.
- Từ **D3 lên D4**: đã dùng knowledge để dẫn dắt người khác hoặc quyết định qua nhiều constraint/incident; có evidence về outcome và trigger để revisit.
- Nếu evidence cũ không còn đúng sau migration/incident mới, hạ self-assessment hoặc ghi `NEEDS_REVALIDATION`; độ sâu không phải huy hiệu vĩnh viễn.

# Stream API, Functional Programming and Optional

> Type: `CORE`<br>
> Domain: `java`<br>
> Target depth: `D3 — thiết kế pipeline đúng semantics, phát hiện side effect/N+1/materialization và đo trước khi parallelize`<br>
> Teaching readiness: `OUTLINE_ONLY`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Collections and complexity](collections-data-structures-and-complexity.md)<br>
> Related cases: [`FEED-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`ANALYTICS-UC-01`](../../../../use-case-catalog.md#analytics-uc-01)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

Source canonical cho [Stream/Optional question bank](../../question-bank/stream-api-functional-programming-and-optional.md). Pipeline đẹp về cú pháp không phải evidence về performance hoặc correctness.

## 1. Learning objectives

1. Giải thích functional interface, lazy pipeline, encounter order, stateful/stateless operation và collector contract.
2. Dùng `Optional` để mô tả absence tại return boundary, không biến nó thành null wrapper ở mọi field/parameter.
3. Quyết định loop, sequential stream, parallel stream hoặc database operation dựa trên workload và side-effect boundary.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — mô tả source -> intermediate stages -> terminal operation và nơi side effect/resource lifecycle đi vào.`

## 3. Cơ chế hoạt động

Stream không phải collection; nó là mô tả một computation trên source, thường chỉ chạy khi terminal operation yêu cầu. Intermediate operations có thể được fuse, short-circuit hoặc reorder trong giới hạn semantic contract. Stream thường single-use.

Behavioral parameters nên non-interfering và stateless để pipeline đúng khi implementation thay đổi execution strategy. Stateful operations như `sorted`/`distinct` có thể buffer dữ liệu; `limit`, `findFirst` và encounter order ảnh hưởng khả năng short-circuit/parallelize.

Collector gồm supplier, accumulator, combiner và finisher cùng characteristics. Collector dùng song song chỉ đúng nếu identity/associativity và concurrency/ordering contract phù hợp.

`Optional<T>` là value-based container cho present/absent return. `map` giữ một layer; `flatMap` dùng khi mapper đã trả `Optional`. `orElse` evaluate argument eagerly, còn `orElseGet` lazy. Optional không thay validation, error semantics hoặc collection rỗng.

## 4. Invariant và boundary

1. Pipeline không mutate source/shared state ngoài explicit, reviewed boundary.
2. Resource-backed stream phải được close trong owner scope; không trả lazy stream vượt transaction/resource lifetime.
3. Absence, failure và empty collection là ba semantics khác nhau.
4. Parallelism chỉ được chọn khi function associative/non-interfering, data đủ lớn và executor/downstream không bị oversubscribe.

## 5. Thuật ngữ và distinction

| Thuật ngữ | Định nghĩa | Dễ nhầm | Phân biệt |
| --- | --- | --- | --- |
| Lazy | Chưa traverse cho tới terminal demand | Cached | Mỗi terminal operation trên stream mới/nguồn có thể chạy lại |
| Encounter order | Order do source/pipeline định nghĩa | Sorted order | Ordered không nhất thiết sorted |
| Short-circuit | Có thể kết thúc không đọc hết source | Lazy | Không phải mọi lazy operation short-circuit |
| Non-interference | Không sửa source trong lúc pipeline chạy | Thread-safe | Sequential pipeline vẫn có thể bị interference |
| Optional absence | Không có value bình thường | Exceptional failure | Không nuốt lỗi thành empty tùy tiện |

## 6. Misconceptions

| Misconception | Vì sao sai | Counterexample |
| --- | --- | --- |
| Stream luôn nhanh hơn loop | Có abstraction/allocation và cùng algorithmic cost | Hot small loop có thể nhanh, rõ hơn |
| `parallelStream()` tự tăng throughput | Dùng common pool, split/merge overhead và shared bottleneck | Blocking DB call/N+1 làm tệ hơn |
| Stream không có side effect | `peek`, mapper hoặc collector có thể mutate | Shared list/counter gây race |
| Optional nên dùng cho mọi field/parameter | Framework/serialization/entity semantics xấu đi | Return boundary thường phù hợp hơn |
| `orElse` lazy | Argument được evaluate trước call | Expensive fallback vẫn chạy khi value present |

## 7. Failure modes kinh điển

| Failure | Trigger | Symptom | Root mechanism |
| --- | --- | --- | --- |
| Hidden N+1 | Mapper gọi repository/lazy relation | Query count/latency tăng | Pipeline che I/O per element |
| Unbounded collect | `.toList()` trên source lớn | Heap/GC spike | Materialize toàn bộ result |
| Parallel side effect | Shared mutable accumulator | Missing/duplicate result | Non-associative/non-thread-safe behavior |
| Common-pool starvation | Blocking I/O trong parallel/CF default pool | Unrelated task latency | Shared finite pool bị block |
| Resource leak | Files/JPA-backed stream không close | FD/connection leak | Lazy resource owner không rõ |

## 8. Solution patterns

| Pattern | Bảo vệ | Giới hạn | Khi dùng |
| --- | --- | --- | --- |
| Pure map/filter/reduce | Composability/correctness | Không phù hợp mọi control flow | In-memory transformation bounded |
| Domain collector | Aggregate invariant rõ | Phải chứng minh associativity | Group/aggregate reusable |
| Explicit loop | Control flow/resource/error rõ | Verbose hơn | Complex branching/hot path |
| Set-based DB query | Tránh N+1/materialization | SQL/query ownership | Data lớn nằm ở database |
| Optional at lookup return | Absence explicit | Không mô tả failure detail | Repository/cache lookup |

## 9. Trade-off matrix

| Option | Correctness | Complexity | Performance | Operability | Evolution |
| --- | --- | --- | --- | --- | --- |
| Loop | Explicit state/order | Dễ dài | Predictable, dễ benchmark | Stack trace rõ | Dễ thêm branching |
| Sequential stream | Declarative composition | Vừa | Tốt cho bounded transformations | Pipeline có thể che I/O | Dễ reuse function |
| Parallel stream | Cần strong algebraic assumptions | Cao | Chỉ tốt khi split/CPU/data phù hợp | Common-pool/debug khó | Context propagation khó |
| Push-down DB/broker | Cross-layer contract | Cao hơn | Giảm transfer/materialization | Có query/lag metric | Phụ thuộc capability backend |

## 10. Deep-dive

- [Laziness, collectors, parallelism and resource lifecycle](../deep-dives/stream-laziness-collectors-parallelism-and-resources.md).
- Async composition thuộc [Executors and CompletableFuture](executors-completablefuture-and-concurrency-control.md), không đồng nhất với Stream API.

## 11. Liên hệ learning case

| Case | Áp dụng | Detail giữ ở case |
| --- | --- | --- |
| `FEED-UC-01` | Mapping/bounded materialization/N+1 | Query and pagination evidence |
| `ANALYTICS-UC-01` | Associative aggregation và replay-safe projection | Event/storage topology |
| `NOTIFY-UC-01` | Batch/filter/preference processing | Fan-out/broker workload |

## 12. Self-check

1. **Question:** Vì sao intermediate operation lazy nhưng `sorted` vẫn có thể cần buffer lớn?<br>**My answer:** `LEARNER TODO`
2. **Question:** Khi nào loop tốt hơn stream dù stream ngắn hơn?<br>**My answer:** `LEARNER TODO`
3. **Question:** Điều kiện nào phải đạt trước khi dùng parallel stream trong backend request path?<br>**My answer:** `LEARNER TODO`

## 13. Official references

- [Java SE 21 Stream package](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html)
- [Java SE 21 `Stream`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html)
- [Java SE 21 `Collector`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collector.html)
- [Java SE 21 `Optional`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html)

## 14. Teach-back checklist

- [ ] Tôi giải thích lazy/fusion/short-circuit mà không gọi Stream là collection.
- [ ] Tôi nhận diện được N+1 và unbounded collect trong pipeline.
- [ ] Tôi phân biệt absence, empty và failure.
- [ ] Tôi bảo vệ decision loop/sequential/parallel/push-down bằng workload.
- [ ] Evidence benchmark/query count vẫn `NOT RUN`.

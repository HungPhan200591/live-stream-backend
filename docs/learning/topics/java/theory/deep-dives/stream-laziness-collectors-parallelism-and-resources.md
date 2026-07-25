# Stream Laziness, Collectors, Parallelism and Resources

> Type: `DEEP_DIVE`<br>
> Domain: `java`<br>
> Target depth: `D3 — chẩn đoán pipeline allocation/N+1/ordering và benchmark parallel decision đúng cách`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Stream API core](../core/stream-api-functional-programming-and-optional.md)<br>
> Related cases: [`FEED-UC-01`](../../../use-case-catalog.md#31-foundation-và-senior-cases), [`ANALYTICS-UC-01`](../../../use-case-catalog.md#analytics-uc-01)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

## 1. Learning objectives

1. Reason về stage fusion, short-circuit, stateful operations và spliterator characteristics.
2. Chứng minh collector associativity/identity/ordering trước parallel execution.
3. Giữ resource/transaction/context lifetime không bị lazy pipeline vượt qua.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — vẽ demand từ terminal operation ngược về source và đánh dấu stage cần buffer/order/resource.`

## 3. Internal mechanism

Pipeline thường traverse element qua fused stages thay vì tạo collection cho mỗi `map/filter`, nhưng stateful operation như `sorted`, `distinct` hoặc ordered parallel coordination có thể cần buffer/barrier. `findFirst` giữ encounter order nên có thể đắt hơn `findAny` trong parallel context.

Spliterator mô tả traversal/splitting và characteristics như `ORDERED`, `SIZED`, `SUBSIZED`, `DISTINCT`, `SORTED`, `CONCURRENT`, `IMMUTABLE`. Parallel efficiency phụ thuộc khả năng split cân bằng, computation per element và combine cost; chỉ đổi `.stream()` thành `.parallelStream()` không thay data-source capability.

Reduction song song cần associative accumulator/combiner và identity đúng. Floating-point addition không thật sự associative do rounding; mutable reduce sai có thể reuse cùng identity hoặc violate combiner contract. Collector `CONCURRENT` không có nghĩa mọi downstream object/thread side effect an toàn.

Resource-backed streams như file lines hoặc persistence/query stream mang close/lifecycle obligation. Lazy stream trả khỏi transaction có thể evaluate sau khi session/connection đóng hoặc giữ resource lâu hơn caller nghĩ.

## 4. Pathological cases

| Case | Causal chain | Symptom |
| --- | --- | --- |
| Parallel small list | Split/schedule/combine > useful work | Slower/variable latency |
| Blocking mapper | Common-pool worker waits I/O | Starvation/interference |
| Ordered parallel limit | Coordination preserves prefix | Low scaling/high buffer |
| Stateful side effect | Execution order/thread varies | Duplicate/missing state |
| Lazy ORM stream | Transaction closes before terminal op | Lazy/connection failure |
| Infinite source + non-short-circuit | Terminal needs all elements | Never completes/memory growth |

## 5. Cross-layer interaction

- Stream abstraction can hide repository/network call inside mapper; query count/trace must reveal N+1.
- `.toList()` returns unmodifiable list in modern JDK contract; mutability expectation must be explicit.
- Parallel stream/common pool competes with `CompletableFuture` default async work.
- Reactor/Reactive Streams backpressure is different model; Java Stream is pull/traversal and usually finite/synchronous.

## 6. Experiment implication

1. Benchmark loop vs sequential vs parallel with JMH, multiple data sizes and CPU cost; isolate I/O.
2. Count DB calls/allocation/materialized size for project mapping pipeline.
3. Test collector sequential and parallel with randomized partitioning/order.
4. Verify resource close on success/failure/short-circuit. Evidence remains `NOT RUN`.

## 7. Trade-off matrix

| Option | Semantic risk | Performance | Resource/control | Debuggability |
| --- | --- | --- | --- | --- |
| Loop | Low/explicit | Often predictable | Full control | High |
| Sequential stream | Low if pure | Good bounded mapping | Lazy/resource caveat | Medium |
| Parallel stream | High if state/order/I/O | Workload-dependent | Common pool unless custom mechanism | Lower |
| DB/set operation | Cross-layer semantics | Best for large resident data | DB transaction/resource | Query-plan tools |

## 8. Liên hệ case

| Case | Deep implication | Evidence chưa có |
| --- | --- | --- |
| `FEED-UC-01` | N+1/materialization/order | Query-count and plan |
| `ANALYTICS-UC-01` | Associative projection/replay | Event workload |
| `NOTIFY-UC-01` | Batch/filter without shared side effect | Fan-out load |

## 9. Self-check

1. **Question:** Operation nào phá streaming/fusion và vì sao?<br>**My answer:** `LEARNER TODO`
2. **Question:** Collector nào hợp lệ sequential nhưng sai parallel, hãy nêu causal chain?<br>**My answer:** `LEARNER TODO`
3. **Question:** Stream resource/transaction lifetime phải thuộc layer nào?<br>**My answer:** `LEARNER TODO`

## 10. Official references

- [Java SE 21 Stream package](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html)
- [Java SE 21 `Spliterator`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Spliterator.html)
- [Java SE 21 `Collector`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collector.html)

## 11. Teach-back checklist

- [ ] Tôi giải thích stateful barrier/short-circuit/order.
- [ ] Tôi chứng minh collector algebra thay vì nói “thread-safe”.
- [ ] Tôi nhận diện common-pool và resource-lifetime boundary.
- [ ] Tôi chỉ parallelize sau workload/JMH evidence.
- [ ] Evidence vẫn `NOT RUN`.

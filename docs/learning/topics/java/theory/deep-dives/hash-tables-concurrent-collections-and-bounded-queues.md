# Hash Tables, Concurrent Collections and Bounded Queues

> Type: `DEEP_DIVE`<br>
> Domain: `java`<br>
> Target depth: `D3 — giải thích collision/compound race/queue collapse và đo trên skewed concurrent workload`<br>
> Teaching readiness: `OUTLINE_ONLY`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Collections, Data Structures and Complexity](../core/collections-data-structures-and-complexity.md), [JMM](../core/jmm-synchronization-and-thread-safety.md)<br>
> Related cases: [`CACHE-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`CHAT-UC-01`](../../../../use-case-catalog.md#chat-uc-01)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

## 1. Learning objectives

1. Giải thích hash spread, resize, collision/tree-bin behavior mà không biến implementation detail thành API guarantee.
2. Phân biệt thread-safe single operation với atomic compound invariant.
3. Thiết kế bounded queue có overload policy và metric queue age/depth.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — mô tả key -> hash -> bucket/bin, concurrent state transition và arrival/service/queue relationship.`

## 3. Internal mechanism

Hash-table capacity/load factor kiểm soát trade-off memory với collision/resize frequency. Resize là rare but expensive operation nên append/put cost thường được nói theo amortized model. Modern `HashMap` có thể chuyển collision chain dài thành tree bin khi đủ điều kiện; threshold/capacity là implementation detail, không phải lý do chấp nhận hash key kém hoặc adversarial input.

`ConcurrentHashMap` tổ chức concurrent access không bằng một global lock cho mọi operation. Weakly consistent iteration không phải immutable snapshot. Atomic methods như `compute`, `merge`, `putIfAbsent` giúp giữ một map-key transition, nhưng mapping function phải ngắn, không recursive/unbounded I/O và không tự động bảo vệ invariant ở database/map khác.

Blocking queue liên kết producer/consumer bằng capacity và wait/reject semantics. Unbounded queue khiến executor nhìn như không reject nhưng chuyển overload thành queue wait, memory retention và stale tasks. Queue depth một mình chưa đủ; queue age, arrival/service rate và oldest item age phản ánh latency debt.

Copy-on-write phù hợp read-mostly/small collection; mỗi write copy array nên catastrophic nếu write frequent/large. Concurrent sorted/skip-list structures đổi memory/constant/ordering để có range/concurrent behavior.

## 4. Edge/pathological cases

| Case | Mechanism | Symptom |
| --- | --- | --- |
| Hot key | Nhiều operation cùng key/bin/lock | Contention dù map overall lớn |
| Adversarial hash | Collision tập trung | CPU latency spike |
| `computeIfAbsent` slow loader | Mapping function block | Requests cùng key chờ/loader amplification |
| Weak iteration misuse | Treat iterator as point-in-time snapshot | Reconciliation/count sai expectation |
| Unbounded queue | Sustained arrival > service | OOM/timeout after delayed collapse |
| Drop without semantics | Queue full and silent discard | Durable/user action mất |

## 5. Cross-layer interaction

- Cache single-flight chỉ gom load trong một process/key; cluster vẫn có multiple loaders nếu không có broader strategy.
- WebSocket outbound queue cần per-connection/per-room budget; one global queue làm hot room ảnh hưởng room khác.
- Database uniqueness/conditional update vẫn là owner cho durable invariant; concurrent map chỉ có process scope.
- Retry đưa task trở lại arrival stream và có thể phá queue recovery nếu không có budget/jitter.

## 6. Experiment implication

1. So sánh uniform với skewed key distribution; đo throughput, p99 và contention.
2. Chạy compound `get/check/put` đối đầu `compute/merge` bằng barrier/repeated test.
3. Saturate bounded queue; ghi accepted/rejected, queue age, memory và recovery time.
4. Không ghi result mẫu; raw evidence hiện `NOT RUN`.

## 7. Trade-off matrix

| Option | Correctness | Throughput | Memory/latency | Operability |
| --- | --- | --- | --- | --- |
| Plain HashMap + confinement | Rất rõ nếu single owner | Cao | Thấp | Ownership phải chắc |
| ConcurrentHashMap | Per-key ops tốt | Cao khi keys phân tán | Overhead metadata | Need contention/key metrics |
| Coarse lock map | Compound invariant dễ | Thấp khi hot | Queue at lock | Thread dump rõ |
| Unbounded queue | Không reject ngay | Burst tốt ngắn hạn | Collapse không bound | Khó recovery |
| Bounded queue | Explicit overload | Stable | Reject/drop/block policy | Metric/actionable |

## 8. Liên hệ case

| Case | Deep implication | Evidence chưa có |
| --- | --- | --- |
| `CACHE-UC-01` | Hot key/single-flight/bounded fallback | Redis-down workload |
| `CHAT-UC-01` | Slow-consumer queue/drop policy | WS load test |
| `VIEWCOUNT-UC-01` | Per-viewer dedup/atomic counter | Concurrent joins/leaves |

## 9. Self-check

1. **Question:** Vì sao tree bin không biến bad/adversarial hash thành non-issue?<br>**My answer:** `LEARNER TODO`
2. **Question:** `compute` bảo vệ được invariant nào và không bảo vệ được boundary nào?<br>**My answer:** `LEARNER TODO`
3. **Question:** Queue depth và queue age kể hai câu chuyện overload khác nhau thế nào?<br>**My answer:** `LEARNER TODO`

## 10. Official references

- [Java SE 21 `HashMap`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/HashMap.html)
- [Java SE 21 `ConcurrentHashMap`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html)
- [Java SE 21 `BlockingQueue`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/BlockingQueue.html)
- [Java SE 21 `CopyOnWriteArrayList`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CopyOnWriteArrayList.html)

## 11. Teach-back checklist

- [ ] Tôi phân biệt API contract và current implementation detail.
- [ ] Tôi nhận diện per-key contention/compound race.
- [ ] Tôi thiết kế queue capacity và overflow semantics.
- [ ] Tôi nối process-local collection tới durable/distributed boundary.
- [ ] Load evidence vẫn `NOT RUN`.

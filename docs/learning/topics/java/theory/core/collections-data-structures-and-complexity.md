# Collections, Data Structures and Complexity

> Type: `CORE`<br>
> Domain: `java`<br>
> Target depth: `D3 — chọn cấu trúc theo invariant/access pattern, giải thích complexity và đo hot path trên workload thật`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Object semantics and generics](language-object-semantics-and-generics.md)<br>
> Related cases: [`FEED-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`VIEWCOUNT-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

Source canonical cho [Collections question bank](../../question-bank/collections-data-structures-and-complexity.md). Complexity trong file này là model; D3 chỉ đạt khi có dataset/workload và measurement.

## 1. Learning objectives

1. Chọn `List`, `Set`, `Map`, queue/deque/heap theo operation và invariant quan trọng nhất.
2. Phân tích time/space complexity gồm average/worst/amortized và chi phí ẩn từ allocation/cache locality.
3. Nhận diện unbounded materialization, mutable key, bad comparator và concurrent-access failure.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — giải thích vì sao data structure là quyết định về invariant và workload, không phải tên class quen tay.`

## 3. Cơ chế hoạt động

Collection interface mô tả contract; implementation chọn representation và cost profile. `ArrayList` lưu phần tử liên tiếp, random access nhanh và append amortized O(1), nhưng insert/remove giữa phải dịch chuyển. Linked structure tối ưu một số relink operation khi đã có node/iterator, không làm lookup index nhanh hơn và thường tốn locality/allocation.

Hash table tính hash, chọn bucket rồi dùng equality giải collision. Average O(1) phụ thuộc hash distribution, load factor và stable key semantics; nó không phải guarantee mọi input. Ordered tree dựa comparator, thường O(log n) và cung cấp range/order semantics. Heap/priority queue tối ưu lấy min/max, không phải arbitrary search.

Big-O bỏ constant và machine effects để mô tả growth. Amortized analysis phân bổ rare resize/rebuild trên nhiều operation. Trong backend, phải cộng thêm database/network round trip, serialization, allocation và materialization: tối ưu O(n) trong memory không cứu được `findAll()` hàng triệu row.

## 4. Invariant và boundary

1. Collection type phải biểu diễn đúng semantics: uniqueness, ordering, multiplicity, priority hoặc bounded FIFO.
2. Equality/comparator phải nhất quán với operation mà collection dựa vào.
3. Memory/result size phải có bound; pagination/streaming là API boundary, không chỉ implementation detail.
4. Thread safety là property của toàn compound operation, không chỉ của collection class.

## 5. Thuật ngữ và distinction

| Thuật ngữ | Định nghĩa | Dễ nhầm | Phân biệt |
| --- | --- | --- | --- |
| Average complexity | Kỳ vọng dưới input/hash assumptions | Worst-case | Adversarial collision có thể khác xa average |
| Amortized | Trung bình trên chuỗi operation | Average input | Không cần distribution xác suất |
| Stable ordering | Thứ tự lặp có contract | Sorted ordering | Insertion/order contract không đồng nghĩa sort |
| Comparator consistency | `compare(a,b)==0` phù hợp equality expectation | Total order | Vi phạm gây mất/merge phần tử trong sorted set/map |
| Bounded queue | Capacity hữu hạn và overflow policy | Queue thông thường | Boundary bảo vệ memory/backpressure |

## 6. Misconceptions

| Misconception | Vì sao sai | Counterexample |
| --- | --- | --- |
| `HashMap` luôn O(1) | Phụ thuộc collision/resize/key contract | Bad hash hoặc mutable key |
| `LinkedList` insert luôn O(1) | Tìm vị trí vẫn O(n) | `add(index, value)` phải traverse |
| `ConcurrentHashMap` làm workflow atomic | Nhiều get/check/put vẫn race | Cần `compute`, CAS hoặc lock/invariant owner |
| Dùng `Set` tự động sửa duplicate business data | Nó chỉ dedup theo equality hiện tại | Sai equality che lỗi thay vì bảo vệ DB invariant |
| Parallel processing sửa được algorithm xấu | Work split/merge có overhead và shared bottleneck | N+1/database round trips vẫn còn |

## 7. Failure modes kinh điển

| Failure | Trigger | Symptom | Root mechanism |
| --- | --- | --- | --- |
| Unbounded list | `findAll`/collect toàn bộ | Heap/GC/latency tăng | Result materialization không bound |
| Collision/mutable key | Hash kém hoặc key đổi | Lookup sai/CPU spike | Bucket/equality invariant bị phá |
| Comparator bug | Non-transitive/inconsistent comparator | Missing/duplicated ordering | Tree contract không còn total order hợp lệ |
| Compound race | Check-then-act trên shared map | Duplicate/negative count | Các call riêng thread-safe, sequence không atomic |
| Priority starvation | Priority queue không aging/fairness | Low-priority item chờ mãi | Policy không có starvation bound |

## 8. Solution patterns

| Pattern | Bảo vệ | Giới hạn | Khi dùng |
| --- | --- | --- | --- |
| Immutable stable key | Hash/tree membership | Mapping overhead | Cache/dedup/index key |
| Cursor pagination | Bounded memory và stable traversal | Cần deterministic key | Feed/history lớn |
| Bounded deque/queue | Memory/backpressure | Phải chọn reject/drop/block | Async buffer/slow consumer |
| Frequency map/set | O(n) membership/count | Memory O(k) | Dedup, counting, two-sum style logic |
| Heap/top-K | O(n log k) thay full sort | Không hỗ trợ arbitrary ranking update rẻ | Leaderboard/window top-K |

## 9. Trade-off matrix

| Option | Correctness | Complexity | Performance | Operability | Evolution |
| --- | --- | --- | --- | --- | --- |
| List + linear scan | Dễ hiểu, duplicate cho phép | Thấp | O(n) mỗi lookup | Dễ profile | Tệ khi scale |
| Hash map/set | Cần stable equality | Vừa | Average O(1), memory cao hơn | Collision khó thấy nếu thiếu metric | Tốt cho membership |
| Tree map/set | Cần comparator đúng | Vừa | O(log n), range/order | Predictable hơn | Tốt cho ordered query |
| Database/query owner | Durable invariant/query planner | Cross-layer | Network/I/O nhưng tránh full materialization | Có plan/index metric | Phù hợp dữ liệu lớn |

## 10. Deep-dive

- [Hash tables, tree bins, concurrent collections and bounded queues](../deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md).
- Complexity model phải được nối sang SQL query plan hoặc load test khi data vượt process memory.

## 11. Liên hệ learning case

| Case | Áp dụng | Detail giữ ở case |
| --- | --- | --- |
| `FEED-UC-01` | Pagination, bounded result, sort key | Dataset/query plan/API cursor |
| `VIEWCOUNT-UC-01` | Counter map/set, dedup và compound atomicity | Exact/approximate implementation |
| `CHAT-UC-01` | Bounded queue và slow-consumer policy | WebSocket workload/drop policy |

## 12. Self-check

1. **Question:** Với membership lookup nhiều và iteration có thứ tự, tôi chọn gì và assumption nào phải đo?<br>**My answer:** `LEARNER TODO`
2. **Question:** Vì sao `HashMap` average O(1) vẫn có thể gây incident CPU hoặc lookup sai?<br>**My answer:** `LEARNER TODO`
3. **Question:** Khi nào O(n) in-memory không phải bottleneck chính vì database/network mới là owner cost?<br>**My answer:** `LEARNER TODO`

## 13. Official references

- [Java SE 21 Collections Framework](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/doc-files/coll-overview.html)
- [Java SE 21 `Map`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Map.html)
- [Java SE 21 concurrent package](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/package-summary.html)

## 14. Teach-back checklist

- [ ] Tôi chọn collection từ operation/invariant thay vì thói quen.
- [ ] Tôi giải thích average/worst/amortized bằng ví dụ.
- [ ] Tôi nêu memory bound và overflow policy.
- [ ] Tôi không gọi compound workflow atomic chỉ vì dùng concurrent collection.
- [ ] Evidence đo complexity/workload vẫn `NOT RUN`.

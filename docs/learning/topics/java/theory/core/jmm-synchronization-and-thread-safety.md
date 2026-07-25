# Java Memory Model, Synchronization and Thread Safety

> Type: `CORE`<br>
> Domain: `java`<br>
> Target depth: `D3 — tái hiện race, chỉ ra happens-before thiếu và sửa bằng ownership/synchronization có test lặp`<br>
> Teaching readiness: `OUTLINE_ONLY`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Object semantics](language-object-semantics-and-generics.md), [JVM runtime](jvm-class-loading-bytecode-and-memory.md)<br>
> Related cases: [`STREAM-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`VIEWCOUNT-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

Source canonical cho [JMM question bank](../../question-bank/jmm-synchronization-and-thread-safety.md). File phân biệt language memory model với database transaction/distributed consistency.

## 1. Learning objectives

1. Phân biệt atomicity, visibility, ordering và liveness.
2. Dùng happens-before để chứng minh publication/communication đúng thay vì dựa vào “thường chạy được”.
3. Chọn confinement, immutability, lock, volatile, atomic/CAS hoặc message passing theo invariant.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — mô tả hai thread đọc/ghi state và vẽ synchronization edge khiến write được phép quan sát.`

## 3. Cơ chế hoạt động

Data race xuất hiện khi hai thread truy cập cùng variable, ít nhất một write và không có ordering/synchronization phù hợp. JMM cho phép compiler/CPU/JIT reorder miễn single-thread semantics và memory-model rule được giữ; vì vậy wall-clock order hoặc log order không chứng minh visibility.

Happens-before là partial order đảm bảo visibility/ordering. Các edge quan trọng gồm program order, monitor unlock-before-lock, volatile write-before-read cùng variable, thread start/join, class initialization và transitivity.

`synchronized` cung cấp mutual exclusion và memory effects ở monitor boundary. `volatile` cung cấp visibility/order cho variable và liên quan, nhưng compound read-modify-write như `count++` vẫn không atomic. Atomic classes dùng CAS/retry cho single-variable state; multi-variable invariant vẫn cần state ownership/lock/immutable snapshot phù hợp.

Safe publication có thể qua final-field construction rule đúng, static initialization, volatile reference, lock hoặc concurrent container contract. `this` escape trong constructor có thể làm thread khác quan sát object chưa fully constructed.

Thread safety không chỉ không corrupt data; còn có progress. Deadlock, livelock, starvation và unbounded blocking là liveness failures.

## 4. Invariant và boundary

1. Mọi shared mutable invariant có owner và synchronization protocol duy nhất, được document/test.
2. Read/write ngoài protocol không được coi “harmless” chỉ vì operation primitive/reference atomic.
3. Lock scope không chứa unbounded external I/O nếu có thể tách state decision khỏi side effect.
4. In-process synchronization không bảo vệ nhiều JVM/database/message consumer; boundary phải chuyển sang durable owner/coordination phù hợp.

## 5. Thuật ngữ và distinction

| Thuật ngữ | Định nghĩa | Dễ nhầm | Phân biệt |
| --- | --- | --- | --- |
| Atomicity | Operation/invariant không quan sát intermediate state | Visibility | Atomic write chưa chắc thread khác thấy kịp |
| Visibility | Write được thread khác quan sát theo model | Wall-clock recency | Cần happens-before edge |
| Ordering | Constraint trên observable actions | Execution timestamp | Implementation có thể reorder hợp lệ |
| Linearizability | Operation trông như xảy ra tại một instant giữa invoke/return | Serializability | Khác database transaction schedule |
| Lock-free | System-wide progress không phụ thuộc một stalled thread | Wait-free | Individual operation vẫn có thể retry/starve |

## 6. Misconceptions

| Misconception | Vì sao sai | Counterexample |
| --- | --- | --- |
| `volatile int count; count++` thread-safe | Increment là read-modify-write | Lost update |
| Dùng concurrent collection là workflow atomic | Compound sequence có race | get/check/put duplicate |
| `final` làm object graph immutable | Referenced object vẫn mutable | Final list bị add/remove |
| Unit test pass chứng minh race không có | Interleaving nondeterministic | Stress/repeat/barrier test mới tăng khả năng tái hiện |
| In-memory lock bảo vệ cluster | Mỗi JVM có lock riêng | Hai node cùng update database |

## 7. Failure modes kinh điển

| Failure | Trigger | Symptom | Root mechanism |
| --- | --- | --- | --- |
| Lost update | Concurrent read-modify-write | Counter/balance thiếu | Không atomic/serialized |
| Unsafe publication | Reference escape không HB | Default/stale field hiếm gặp | Construction visibility thiếu |
| Deadlock | Lock-order cycle | Threads BLOCKED, no progress | Circular wait |
| Livelock | Threads liên tục backoff/respond nhau | CPU cao, no commit | Activity không tạo progress |
| Starvation | Unfair lock/pool/priority | Request chờ vô hạn | Resource access không bounded/fair |

## 8. Solution patterns

| Pattern | Bảo vệ | Giới hạn | Khi dùng |
| --- | --- | --- | --- |
| Thread confinement | Loại shared mutation | Handoff/copy cost | Request-local/actor-owned state |
| Immutability + safe publication | Read concurrency | Update tạo version mới | Config/snapshot/value object |
| Monitor/explicit lock | Multi-field invariant | Contention/deadlock | Critical section bounded |
| Atomic/CAS | Single state transition | Retry/ABA/composition | Counter/state word |
| Durable conditional update | Cross-node invariant | Database contention | Stream/wallet state |

## 9. Trade-off matrix

| Option | Correctness | Complexity | Performance | Operability | Evolution |
| --- | --- | --- | --- | --- | --- |
| Coarse lock | Dễ chứng minh | Thấp | Contention cao | Thread dump rõ | Khó scale hot key |
| Fine locks | Có thể parallel hơn | Cao | Overhead/deadlock risk | Diagnose khó | Fragile khi invariant đổi |
| Lock-free/CAS | Tốt cho state nhỏ | Rất cao | Tốt khi contention vừa; retry khi cao | Hard to debug | Khó compose |
| Single owner/queue | Sequential invariant rõ | Architecture cost | Bounded by owner throughput | Queue lag observable | Scale bằng partition |
| DB constraint/lock | Cross-node durable | Cross-layer | I/O/lock wait | Query/lock metric | Strong invariant owner |

## 10. Deep-dive

- [Happens-before, publication, locking and race diagnosis](../deep-dives/jmm-happens-before-publication-and-locking.md).
- Async executor/cancellation/backpressure thuộc [Executors core](executors-completablefuture-and-concurrency-control.md).

## 11. Liên hệ learning case

| Case | Áp dụng | Detail giữ ở case |
| --- | --- | --- |
| `STREAM-UC-01` | State transition race/atomicity | Current webhook/repository path |
| `VIEWCOUNT-UC-01` | Counter exactness/CAS/ownership | Exact-vs-approximate design |
| `GIFT-UC-01` | Lost update and multi-node boundary | Wallet transaction/DB invariant |

## 12. Self-check

1. **Question:** Atomicity, visibility và ordering khác nhau bằng ví dụ `volatile` nào?<br>**My answer:** `LEARNER TODO`
2. **Question:** Chứng minh safe publication bằng happens-before edge ra sao?<br>**My answer:** `LEARNER TODO`
3. **Question:** Khi nào in-memory lock sai boundary và invariant phải thuộc database/queue owner?<br>**My answer:** `LEARNER TODO`

## 13. Official references

- [JLS 17 — Threads and Locks](https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html)
- [Java SE 21 `java.util.concurrent`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/package-summary.html)
- [Java SE 21 `AtomicInteger`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/AtomicInteger.html)

## 14. Teach-back checklist

- [ ] Tôi vẽ happens-before graph cho publication/state change.
- [ ] Tôi không trộn atomicity với visibility.
- [ ] Tôi nêu liveness failure bên cạnh data corruption.
- [ ] Tôi chọn synchronization theo invariant boundary, kể cả multi-node.
- [ ] Concurrency reproducer/evidence vẫn `NOT RUN`.

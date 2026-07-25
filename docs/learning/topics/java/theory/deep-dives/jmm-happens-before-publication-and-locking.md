# JMM Happens-before, Publication and Locking

> Type: `DEEP_DIVE`<br>
> Domain: `java`<br>
> Target depth: `D3 — chứng minh race bằng happens-before graph, stress test và lock/progress evidence`<br>
> Teaching readiness: `OUTLINE_ONLY`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [JMM, Synchronization and Thread Safety](../core/jmm-synchronization-and-thread-safety.md)<br>
> Related cases: [`STREAM-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`GIFT-UC-01`](../../../../use-case-catalog.md#gift-uc-01)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

## 1. Learning objectives

1. Dựng happens-before proof cho publication, handoff và state transition.
2. Phân tích lock/CAS failure gồm contention, deadlock, starvation và retry amplification.
3. Thiết kế concurrency reproducer kiểm soát interleaving thay vì dựa vào sleep.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — vẽ actions của T1/T2, program-order edges, synchronization edges và read nào được phép thấy write nào.`

## 3. Happens-before reasoning

Happens-before không khẳng định physical CPU chạy action A “trước thời gian” B; nó là ordering relation trong memory model đảm bảo effects của A visible/ordered với B. Nếu conflicting accesses không ordered, execution có data race và observed values có thể khác intuition sequential.

Monitor unlock happens-before later lock cùng monitor; volatile write happens-before later read thấy ordering qua cùng volatile variable; actions trước `Thread.start` visible cho started thread; thread actions happen-before successful `join` return. Transitivity nối data preparation với publication edge.

Final-field semantics hỗ trợ initialized state khi construction đúng và object không escape; nó không đóng băng referenced mutable graph và không sửa constructor publication sai.

Double-checked locking cần volatile publication để không quan sát reference trước fully initialized effects. Initialization-on-demand holder/static initialization thường đơn giản hơn khi lifecycle phù hợp.

## 4. Locking/CAS internals

Intrinsic/explicit lock tạo mutual exclusion nhưng scope/order quyết định liveness. Lock convoy/long critical section làm p99 tăng dù data đúng. `ReentrantLock` thêm interruptible/timed acquisition/fair option/conditions, nhưng fair lock có throughput cost và không tự ngăn logic deadlock.

CAS đọc state, thử replace nếu unchanged và retry khi conflict. Under contention, failed retry tiêu CPU; multi-field invariant cần immutable state aggregate/version or other ownership. ABA nghĩa value trở lại representation cũ trong khi state history đổi; version/stamp/higher-level design có thể cần.

`LongAdder` giảm contention cho statistical counter bằng cells nhưng `sum()` không phải atomic snapshot cho money/strict invariant.

## 5. Pathological cases

| Case | Causal chain | Symptom |
| --- | --- | --- |
| Unsafe DCL | Non-volatile publication/reordering | Rare partially initialized observation |
| Lock-order inversion | T1 A->B, T2 B->A | Deadlock |
| Lock + remote call | Critical section waits unbounded I/O | Convoy/pool exhaustion |
| CAS storm | Many writers same state | CPU high, poor progress |
| `LongAdder` as ledger | Non-atomic aggregate read | Business invariant violated |
| Sleep-based test | Scheduler happens to serialize | Flaky/false confidence |

## 6. Reproducer design

1. Use latch/barrier/phaser to align reads/writes and loop many iterations.
2. Separate invariant checker from worker; record seed/interleaving summary when possible.
3. For deadlock, acquire locks in controlled opposite order and assert via thread dump/deadlock detector with timeout.
4. For DB/multi-node race, Java barrier only starts requests; durable invariant evidence must come from DB result/constraint.
5. Do not claim “race fixed” from one green run; define repeat/stress bound. Evidence is `NOT RUN`.

## 7. Cross-layer interaction

- Spring singleton beans are shared; request-local assumption must be explicit.
- `@Transactional` coordinates database state, not arbitrary Java shared memory before/after callback.
- Redis/database/message broker each add their own consistency/atomicity boundary.
- Virtual threads change thread cost, not JMM rules or lock contention.

## 8. Trade-off matrix

| Option | Proof simplicity | Contention | Liveness risk | Scope |
| --- | --- | --- | --- | --- |
| Confinement/immutability | Highest | Low | Low | Handoff required |
| Coarse lock | High | High | Convoy/deadlock if nested | One JVM |
| Fine lock | Lower | Lower potential | Higher ordering complexity | One JVM |
| CAS/atomic state | Medium for small state | Retry under hot key | Starvation/ABA | One JVM |
| DB conditional update | Durable proof | DB lock/conflict | Transaction wait/deadlock | Multi-node |

## 9. Liên hệ case

| Case | Deep implication | Evidence chưa có |
| --- | --- | --- |
| `STREAM-UC-01` | Transition linearization/DB boundary | Race test |
| `GIFT-UC-01` | Lost update/multi-field money invariant | Concurrent ledger test |
| `VIEWCOUNT-UC-01` | LongAdder/exact-vs-approximate | Load/reconciliation |

## 10. Self-check

1. **Question:** Hãy dựng happens-before chain cho producer publish immutable snapshot cho consumer.<br>**My answer:** `LEARNER TODO`
2. **Question:** Vì sao volatile sửa visibility nhưng không sửa compound invariant?<br>**My answer:** `LEARNER TODO`
3. **Question:** Stress test nào chứng minh race và boundary nào Java lock không thể bảo vệ?<br>**My answer:** `LEARNER TODO`

## 11. Official references

- [JLS 17.4 — Memory Model](https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html#jls-17.4)
- [JLS 17.5 — Final Field Semantics](https://docs.oracle.com/javase/specs/jls/se21/html/jls-17.html#jls-17.5)
- [Java SE 21 `ReentrantLock`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/ReentrantLock.html)
- [Java SE 21 atomic package](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/package-summary.html)

## 12. Teach-back checklist

- [ ] Tôi chứng minh bằng edge, không bằng log/wall-clock intuition.
- [ ] Tôi phân biệt data safety và liveness/progress.
- [ ] Tôi thiết kế controlled interleaving không dùng sleep làm chính.
- [ ] Tôi chỉ ra JVM versus durable/distributed boundary.
- [ ] Concurrency evidence vẫn `NOT RUN`.

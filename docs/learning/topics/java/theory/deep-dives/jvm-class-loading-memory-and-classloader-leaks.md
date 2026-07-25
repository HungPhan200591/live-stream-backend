# JVM Class Loading, Memory and Classloader Leaks

> Type: `DEEP_DIVE`<br>
> Domain: `java`<br>
> Target depth: `D3 — đọc class-loading/memory evidence và tái hiện retention qua loader/thread-local/listener`<br>
> Teaching readiness: `TEACHABLE_DRAFT`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [JVM Class Loading, Bytecode and Memory](../core/jvm-class-loading-bytecode-and-memory.md)<br>
> Related cases: [`JVM-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`DEPLOY-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

## 0. Cách học file này

Tách ba graph: delegation/class identity, object retention và process memory budget. Một classloader leak chỉ được chứng minh khi tìm thấy GC-root path giữ defining loader cũ; metaspace tăng một lần do warm-up chưa đủ kết luận.

## 1. Learning objectives

1. Phân biệt loading/linking/initialization trigger và lỗi tương ứng.
2. Theo retention path từ GC root tới classloader/object graph.
3. Lập process memory budget gồm heap và non-heap/native regions.

## 2. Mental model do người dạy cung cấp

Mỗi defining loader tạo một namespace type. Loader giữ các `Class`; class giữ static state; object/thread/listener từ loader sống lâu hơn dự kiến có thể nối graph về loader và ngăn unload toàn bộ deployment. Vì vậy leak vài reference có thể giữ rất nhiều metadata và object.

```mermaid
flowchart TB
    R["GC root<br/>parent thread/listener"] --> O["Object của app cũ"]
    O --> C["Class"]
    C --> L["Defining classloader"]
    L --> G["Toàn bộ class/static graph<br/>không unload"]
    style R fill:#4CAF50,stroke:#fff,stroke-width:2px,color:#fff
    style O fill:#2196F3,stroke:#fff,stroke-width:2px,color:#fff
    style C fill:#9C27B0,stroke:#fff,stroke-width:2px,color:#fff
    style L fill:#FF9800,stroke:#fff,stroke-width:2px,color:#fff
    style G fill:#F44336,stroke:#fff,stroke-width:2px,color:#fff
```

## 3. Internal mechanism

Verification kiểm bytecode structural/type safety; preparation tạo static fields/default values; resolution chuyển symbolic reference khi JVM chọn resolve; initialization chạy class initialization method với synchronization semantics. Failure có thể được wrap/cache theo lifecycle, nên lần dùng sau không nhất thiết chạy lại initializer như lần đầu.

Parent delegation thường hỏi parent trước, bảo vệ platform type identity. Child-first/plugin model cho isolation/versioning nhưng tăng duplicate type/cast/resource leak risk. Class unloading cần defining loader và tất cả class/object liên quan không còn reachable.

Static field thuộc `Class`, và `Class` thuộc loader. Thread context classloader, thread-local value/key, registered driver/listener, scheduler thread hoặc global cache từ parent loader có thể giữ child loader sau redeploy.

Process RSS bao gồm heap committed/resident portions, metaspace/class space, JIT code cache, thread stacks, direct/native allocations và shared libraries. Container limit quá sát `-Xmx` có thể OOMKill mà JVM không kịp ném heap OOME/dump.

### Worked example — redeploy leak

Application cũ đăng ký listener vào singleton thuộc parent loader rồi không unregister. Listener instance mang class của child loader; parent singleton là GC root sống qua redeploy. Sau mỗi lần deploy, loader cũ cùng static caches/classes vẫn reachable, class count và metaspace used-after-GC tăng theo bậc. Fix là lifecycle cleanup/unregister và shutdown thread/executor, sau đó lặp cùng chu kỳ để chứng minh plateau.

## 4. Pathological cases

| Case | Retention/failure chain | Signal |
| --- | --- | --- |
| Static collection | Root class -> static map -> domain graph | Heap live set grows |
| ThreadLocal leak | Pool thread survives request/app lifecycle | Per-thread stale values |
| Redeploy loader | Parent thread/listener -> child class/object | Metaspace/classes increase each deploy |
| Direct buffer/native | Off-heap allocation not budgeted | RSS/OOMKill, heap normal |
| Too many threads | Stack/native scheduler overhead | Native OOM/context switching |
| Initialization cycle/failure | Static initializer dependency/exception | `ExceptionInInitializerError`, unusable class |

## 5. Diagnostic sequence

1. Pin commit/JDK/container limits/workload/time window.
2. Compare heap used-after-GC, allocation rate, class count/metaspace, thread count, direct/native/RSS.
3. Heap dump dominator/retained path for heap retention; thread dump for thread/ThreadLocal owner; NMT/native tools if enabled for non-heap.
4. Verify hypothesis by removing owner/bounding lifecycle and repeating same workload.
5. Không tăng heap như final fix nếu retained graph vẫn tăng.

## 6. Cross-layer interaction

- Spring devtools/reload/container classloaders make type identity/lifecycle visible; project runtime choice must be measured, not assumed.
- Executor/scheduler/application listener must shutdown/unregister on context close.
- JDBC driver, logging and serialization caches can bridge loader lifetimes.
- Kubernetes/container memory metric and JVM heap metric must be correlated.

## 7. Experiment implication

1. Repeatedly load/unload isolated classloader with static/listener/thread leak, record class count/metaspace.
2. Allocate direct buffer/thread count under known container/process budget.
3. Capture heap/thread/JFR evidence before and after cleanup. Current evidence `NOT RUN`.

## 8. Trade-off matrix

| Option | Isolation | Memory/lifecycle risk | Operability | Use case |
| --- | --- | --- | --- | --- |
| Single app loader | Simple | Low loader complexity | Easy | Monolith service |
| Plugin/child loader | Version isolation | Leak/type-identity risk | Harder | True plugin boundary |
| Larger Xmx | Heap headroom | Starves native/container budget | Simple until OOMKill | Only after live-set evidence |
| Explicit lifecycle/bounds | Correct recovery | Engineering cost | Strong | Long-running service |

## 9. Interview answer outline

Giải thích identity `(name, defining loader)`, đường root→object→Class→loader và điều kiện unload. Phân biệt heap/metaspace/direct/thread/RSS, rồi đưa diagnostic sequence và before/after redeploy evidence.

## 10. Tóm tắt và learner write-back

- Cùng binary name nhưng khác defining loader là type khác.
- Unload cần toàn bộ loader graph unreachable.
- ThreadLocal/listener/thread/global cache thường bắc cầu qua lifecycle.
- Container budget phải chừa headroom ngoài Xmx.

`LEARNER TODO — vẽ một retained path và memory budget giả định 1 GiB.`

## 11. Guided self-check

1. **Question:** Cùng binary name nhưng cast fail vì sao?<br>**Đọc lại nếu bí:** mục 2–3.<br>**Rubric:** defining loader là phần của identity.<br>**My answer:** `LEARNER TODO`
2. **Question:** Old loader bị giữ bởi root path nào?<br>**Đọc lại nếu bí:** diagram, example và mục 4.<br>**Rubric:** static/thread/thread-local/listener/cache bridge và retained path.<br>**My answer:** `LEARNER TODO`
3. **Question:** Budget 1 GiB/Xmx 768 MiB còn gì?<br>**Đọc lại nếu bí:** mục 3, 5 và 8.<br>**Rubric:** metaspace/code cache/stacks/direct/native/RSS/headroom.<br>**My answer:** `LEARNER TODO`

## 12. Official references

- [JVMS 5 — Loading, Linking, and Initializing](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-5.html)
- [JLS 12 — Execution](https://docs.oracle.com/javase/specs/jls/se21/html/jls-12.html)
- [Java SE 21 `ClassLoader`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/ClassLoader.html)
- [Java SE 21 Troubleshooting Guide](https://docs.oracle.com/en/java/javase/21/troubleshoot/)

## 13. Teach-back checklist

- [ ] Tôi giải thích loader identity và lifecycle.
- [ ] Tôi phân biệt heap/metaspace/direct/thread/process memory.
- [ ] Tôi đi từ symptom tới retained path, không tuning mù.
- [ ] Tôi nêu cleanup/shutdown boundary khi deploy.
- [ ] Evidence vẫn `NOT RUN`.

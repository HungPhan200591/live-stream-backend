# JVM Interview Question Bank — Class Loading, Bytecode and Memory

> Status: `DRAFT`<br>
> Domain owner: `JVM runtime`<br>
> Active slice: `NONE`; preview target `JVM-01`<br>
> Runtime baseline: `Java 21`<br>
> Related roadmap: [Stage 1](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-1---java-core-state-và-concurrency)<br>
> Related depth rubric: [JVM runtime](../../../knowledge-depth-rubric.md#33-jvm-runtime-và-diagnostics--p0-target-d3)<br>
> Related theory: [Core theory](../theory/core/jvm-class-loading-bytecode-and-memory.md), [Deep-dive](../theory/deep-dives/jvm-class-loading-memory-and-classloader-leaks.md) — `TEACHABLE_DRAFT`, evidence `NOT RUN`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `JVM-01`. Likelihood là heuristic trong topic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Slice | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Core runtime | 4 | 2 | 0 | 0 |
| Failure/capacity | 0 | 2 | 1 | 1 |
| **Tổng** | **4** | **4** | **1** | **1** |

## Recommended practice order

1. First pass: `JVM-MEM-001` đến `JVM-MEM-006`.
2. Senior follow-up: `JVM-MEM-007`, `JVM-MEM-008`.
3. Project application: `JVM-MEM-007`, `JVM-MEM-008`.
4. Stretch: `JVM-MEM-009`, `JVM-MEM-010`.

## Questions

### JVM-MEM-001 — `FOUNDATION`
**Question:** JDK, JVM và runtime image khác vai trò nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — JVM foundation phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Toolchain khác execution engine/runtime modules.<br>
**Answer outline:** JDK chứa compiler/tools/runtime; JVM load/verify/execute bytecode và quản lý runtime; deployed image có thể chỉ chứa modules cần thiết. Declared/build/runtime version phải được kiểm chứng độc lập.<br>
**Required trade-offs:** Full JDK dễ chẩn đoán nhưng image lớn hơn; trimmed runtime nhỏ nhưng cần giữ diagnostic capability.<br>
**Follow-up ladder:** `javac` tạo gì? `java` làm gì? Container dùng JRE/JDK?<br>
**Red flags:** JVM là compiler Java hoặc JRE/JDK luôn đồng nhất version.<br>
**Evidence:** [Core theory](../theory/core/jvm-class-loading-bytecode-and-memory.md) · [Deep-dive](../theory/deep-dives/jvm-class-loading-memory-and-classloader-leaks.md); case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-MEM-002 — `FOUNDATION`
**Question:** Loading, linking và initialization của class khác nhau thế nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — class-loading question kinh điển.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Verification/preparation/resolution và static initialization boundary.<br>
**Answer outline:** Load tạo Class từ bytes; linking verify, prepare static storage và resolve symbolic references; initialization chạy static initializers theo trigger/order. Initialization lỗi có thể làm class unusable tiếp tục trong loader đó.<br>
**Required trade-offs:** Lazy resolution/startup giảm upfront work nhưng failure có thể xuất hiện muộn.<br>
**Follow-up ladder:** Constant initialization? Class literal? `Class.forName`?<br>
**Red flags:** Class được initialize ngay khi file `.class` được đọc.<br>
**Evidence:** [Core theory](../theory/core/jvm-class-loading-bytecode-and-memory.md) · [Deep-dive](../theory/deep-dives/jvm-class-loading-memory-and-classloader-leaks.md); case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-MEM-003 — `FOUNDATION`
**Question:** Parent delegation của class loader nhằm giải quyết gì?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — JVM internals phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Bootstrap/platform/application loaders, duplicate core classes và identity.<br>
**Answer outline:** Loader thường hỏi parent trước để core/shared classes nhất quán và giảm spoofing/duplication; custom/plugin containers có thể dùng child-first có kiểm soát. Class identity gồm binary name và defining loader.<br>
**Required trade-offs:** Isolation hỗ trợ plugin/reload nhưng gây cast/linkage leak phức tạp.<br>
**Follow-up ladder:** Bootstrap loader biểu diễn thế nào? SPI/context loader?<br>
**Red flags:** Hai class cùng fully qualified name luôn cùng type.<br>
**Evidence:** [Core theory](../theory/core/jvm-class-loading-bytecode-and-memory.md) · [Deep-dive](../theory/deep-dives/jvm-class-loading-memory-and-classloader-leaks.md); case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-MEM-004 — `FOUNDATION`
**Question:** Stack, heap, metaspace và direct/native memory chứa gì?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — memory-area question rất phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Per-thread frames khác shared objects/class metadata/off-heap.<br>
**Answer outline:** Stack giữ frames/local/operand per thread; heap giữ objects/arrays; metaspace giữ class metadata; direct/native gồm buffers, thread stacks, code cache và runtime structures. RSS có thể vượt `-Xmx` hợp lệ.<br>
**Required trade-offs:** Tăng heap không sửa native/thread/direct exhaustion và có thể tăng GC footprint.<br>
**Follow-up ladder:** String pool? Code cache? StackOverflow vs OOM?<br>
**Red flags:** Mọi Java memory đều nằm trong heap.<br>
**Evidence:** [Core theory](../theory/core/jvm-class-loading-bytecode-and-memory.md) · [Deep-dive](../theory/deep-dives/jvm-class-loading-memory-and-classloader-leaks.md); case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-MEM-005 — `SENIOR`
**Question:** Khi nào class initialization chạy và static initialization failure biểu hiện ra sao?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — common class-init scenario.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Active use, superclass order và `ExceptionInInitializerError`/subsequent failure.<br>
**Answer outline:** Active use như new/static method/non-constant static field/reflection có thể trigger; superclass initialize trước. Exception lần đầu được wrap; lần sau class có thể báo initialization failure mà không chạy lại.<br>
**Required trade-offs:** Heavy/static I/O startup đơn giản nhưng khó retry/test và tạo global failure.<br>
**Follow-up ladder:** Compile-time constant? Interface init? Lazy holder idiom?<br>
**Red flags:** Catch exception rồi JVM tự initialize lại class lần sau.<br>
**Evidence:** [Core theory](../theory/core/jvm-class-loading-bytecode-and-memory.md) · [Deep-dive](../theory/deep-dives/jvm-class-loading-memory-and-classloader-leaks.md); case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-MEM-006 — `SENIOR`
**Question:** GC roots và reachability giải thích memory leak trong managed runtime thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — memory-leak question phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Reachable-but-unused objects và retaining path.<br>
**Answer outline:** GC chỉ reclaim object không reachable từ roots như threads/statics/JNI; leak là object không còn hữu ích nhưng vẫn reachable qua cache/listener/ThreadLocal/classloader. Heap dump dominator/retained path chứng minh owner.<br>
**Required trade-offs:** Cache/listener tăng performance/extensibility nhưng cần bound/lifecycle cleanup.<br>
**Follow-up ladder:** Weak reference? ThreadLocal pool? Static collection?<br>
**Red flags:** Java có GC nên không thể memory leak.<br>
**Evidence:** [Core theory](../theory/core/jvm-class-loading-bytecode-and-memory.md) · [Deep-dive](../theory/deep-dives/jvm-class-loading-memory-and-classloader-leaks.md); case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-MEM-007 — `SENIOR`
**Question:** Class-loader leak trong long-running Spring/container process xảy ra thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — runtime failure follow-up.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Static/thread/context-loader/reference chain qua redeploy.<br>
**Answer outline:** Old loader bị giữ bởi thread, ThreadLocal, driver, logger/cache hoặc callback nên toàn class graph không unload; xác minh class histogram/heap retaining path và cleanup lifecycle.<br>
**Required trade-offs:** Hot reload/plugin isolation hữu ích nhưng tăng lifecycle ownership.<br>
**Follow-up ladder:** ContextClassLoader? Metaspace growth? Devtools?<br>
**Red flags:** Tăng MaxMetaspaceSize được coi là fix.<br>
**Evidence:** [Core theory](../theory/core/jvm-class-loading-bytecode-and-memory.md) · [Deep-dive](../theory/deep-dives/jvm-class-loading-memory-and-classloader-leaks.md); case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-MEM-008 — `SENIOR`
**Question:** Đo allocation pressure do DTO, boxing và temporary collection thế nào trước khi tối ưu?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — diagnostic application.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** JFR/allocation profile, workload và retained-vs-transient distinction.<br>
**Answer outline:** Chụp JFR/allocation flame graph/GC rate trên workload; tìm allocation site/rate/lifetime; thay đổi nhỏ rồi đo throughput/latency/GC. Không suy từ source syntax rằng object chắc chắn escape/allocate.<br>
**Required trade-offs:** Giảm allocation có thể làm code phức tạp; chỉ giữ khi evidence đáng kể.<br>
**Follow-up ladder:** TLAB? Escape analysis? Primitive stream? Pool object?<br>
**Red flags:** Dùng object pool cho DTO ngắn sống mà không benchmark.<br>
**Evidence:** [Core theory](../theory/core/jvm-class-loading-bytecode-and-memory.md) · [Deep-dive](../theory/deep-dives/jvm-class-loading-memory-and-classloader-leaks.md); case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-MEM-009 — `ARCHITECT`
**Question:** Lập memory budget cho JVM trong container gồm những vùng nào?<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `LOW` — capacity stretch.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Heap + native + threads + headroom thay vì `Xmx=limit`.<br>
**Answer outline:** Budget heap, metaspace, code cache, direct buffers, native libs, thread stacks/JVM overhead và safety headroom; correlate container RSS/OOMKill với NMT/JFR/metrics; set limits theo workload/concurrency.<br>
**Required trade-offs:** Heap lớn giảm allocation pressure risk nhưng ép native headroom và pause footprint.<br>
**Follow-up ladder:** Virtual threads? Direct buffer? Sidecar? cgroup?<br>
**Red flags:** Đặt Xmx bằng 100% memory limit.<br>
**Evidence:** [Core theory](../theory/core/jvm-class-loading-bytecode-and-memory.md) · [Deep-dive](../theory/deep-dives/jvm-class-loading-memory-and-classloader-leaks.md); case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JVM-MEM-010 — `EXPERT`
**Question:** Chẩn đoán class-initialization deadlock hoặc cùng class name nhưng khác loader identity.<br>
**Target depth:** `D4`<br>
**Interview likelihood:** `LOW` — pathological JVM discriminator.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Initialization lock/thread dump và loader-qualified type identity.<br>
**Answer outline:** Dựng minimal reproducer; đọc thread dump lock cycle/class init frames hoặc `ClassCastException` kèm loader; inspect class-loading logs; loại static cyclic work hay sửa loader boundary thay vì catch.<br>
**Required trade-offs:** Custom loader isolation đổi lấy diagnosability/compatibility cost.<br>
**Follow-up ladder:** Deadlock detector thấy init lock? LinkageError? Module layer?<br>
**Red flags:** Restart/tăng heap là root-cause fix.<br>
**Evidence:** [Core theory](../theory/core/jvm-class-loading-bytecode-and-memory.md) · [Deep-dive](../theory/deep-dives/jvm-class-loading-memory-and-classloader-leaks.md); case `JVM-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `JVM-01` active: tạo theory/lab, chụp class-loading/JFR/heap evidence trên workload thật. Stable IDs không tái sử dụng.

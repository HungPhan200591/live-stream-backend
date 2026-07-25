# Java Interview Question Bank — Stream API, Functional Programming and Optional

> Status: `DRAFT`<br>
> Domain owner: `Java language / Stream API / functional style`<br>
> Active slice: `NONE`; preview target `JAVA-01 — Stream API, functional interfaces and Optional`<br>
> Runtime baseline: `Java 21`; later-LTS migration policy belongs to `JDK-02`<br>
> Related roadmap: [Stage 1](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-1---java-core-state-và-concurrency)<br>
> Related depth rubric: [Java language, collections, algorithm and complexity](../../../knowledge-depth-rubric.md#31-java-language-collections-algorithm-và-complexity--p0-target-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/java/theory/core/stream-api-functional-programming-and-optional.md`<br>
> Updated: `2026-07-26`

Preview này không implement `JAVA-01`, không active case và không tạo evidence. `Interview likelihood` là heuristic trong phạm vi Senior Java backend, không phải tỷ lệ thị trường đã đo. Mọi câu giữ `UNANSWERED`, test `NOT RUN`.

## Scope boundary

File này sở hữu lambda/functional interface, Stream pipeline, collectors, Optional và parallel-stream decision. Collection implementation/Big-O nằm ở `collections-data-structures-and-complexity.md`; `CompletableFuture`, executor, JMM và virtual threads thuộc `CON-01`/`JDK-01`; reactive streams/backpressure thuộc extension riêng.

## Project anchor

Current services dùng `.toList()` để map entity sang DTO, `Collectors.toSet()` để build roles, `Collectors.joining()` cho validation message và `Optional` ở repository/cache path. Các pipeline này là điểm review cho mutability/order, side effect, N+1/materialization và absence semantics khi `JAVA-01` active.

## Coverage

| Topic | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Functional and Stream semantics | 8 | 5 | 1 | 0 |
| Optional, collectors and API boundary | 1 | 4 | 0 | 0 |
| Performance, resources and parallelism | 0 | 2 | 1 | 1 |
| **Tổng** | **9** | **11** | **2** | **1** |

## Recommended practice order

1. First pass — câu phổ biến: `JAVA-STREAM-001` đến `JAVA-STREAM-008`, `JAVA-STREAM-010` đến `JAVA-STREAM-017`, `JAVA-STREAM-020`.
2. Senior/Architect follow-up xác suất vừa: `JAVA-STREAM-009`, `JAVA-STREAM-018`, `JAVA-STREAM-019`, `JAVA-STREAM-021`.
3. Project application: `JAVA-STREAM-011`, `JAVA-STREAM-012`, `JAVA-STREAM-017`, `JAVA-STREAM-022`.
4. Architect/Expert stretch: `JAVA-STREAM-021` đến `JAVA-STREAM-023`.

## Questions

### JAVA-STREAM-001 — `FOUNDATION`
**Question:** Lambda expression là gì, và functional interface được định nghĩa thế nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu Java 8+ nền tảng rất phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Target typing, single abstract method và intent của `@FunctionalInterface`.<br>
**Answer outline:** Lambda cung cấp implementation cho functional interface có đúng một abstract method; default/static/Object methods không làm mất tính functional. `@FunctionalInterface` yêu cầu compiler kiểm contract và diễn đạt intent.<br>
**Required trade-offs:** Lambda giảm boilerplate nhưng lambda dài/có nhiều state làm code khó đọc/debug hơn named method/class.<br>
**Follow-up ladder:** Anonymous class khác gì? `this` trong lambda? Checked exception?<br>
**Red flags:** Lambda là object/function độc lập không cần target type.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-002 — `FOUNDATION`
**Question:** Phân biệt `Predicate`, `Function`, `Consumer`, `Supplier` và `UnaryOperator`.<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — standard functional interfaces thường được hỏi.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Input/output shape và cách compose behavior.<br>
**Answer outline:** Predicate T→boolean; Function T→R; Consumer T→void side effect; Supplier ()→T; UnaryOperator T→T. Chọn interface theo contract thay vì tạo custom type không cần thiết.<br>
**Required trade-offs:** Standard interfaces interoperable nhưng custom domain interface có thể diễn đạt checked exception/semantic name tốt hơn.<br>
**Follow-up ladder:** `BiFunction`? Primitive specializations? Composition order?<br>
**Red flags:** Consumer được dùng cho pure transformation hoặc Supplier nhận input.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-003 — `FOUNDATION`
**Question:** Lambda chỉ capture local variable “effectively final” nghĩa là gì? Có thể mutate object được capture không?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — lambda capture question quen thuộc.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Variable capture khác object mutability và không suy diễn thread safety.<br>
**Answer outline:** Local variable không được reassign sau initialization nếu được capture; lambda giữ giá trị/reference. Object qua reference vẫn có thể mutate, nhưng điều đó không tự an toàn khi chạy song song.<br>
**Required trade-offs:** Pure/stateless lambda dễ compose/parallelize; captured mutable state thuận tiện nhưng tăng coupling/race risk.<br>
**Follow-up ladder:** Instance field? Loop variable? AtomicInteger workaround có nên dùng?<br>
**Red flags:** Effectively final làm object được capture immutable.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-004 — `FOUNDATION`
**Question:** `Stream` khác collection thế nào? Stream có lưu dữ liệu không?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu Stream API mở đầu rất phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Source, computation pipeline, one-shot traversal và ownership.<br>
**Answer outline:** Collection lưu/own elements; Stream mô tả pipeline duyệt nguồn, thường lazy và chỉ tiêu thụ một lần. Stream không tự làm source lazy/bounded và không phải data structure.<br>
**Required trade-offs:** Pipeline declarative/composable nhưng có thể che allocation/I/O/complexity nếu source không rõ.<br>
**Follow-up ladder:** Infinite stream? Reuse? Closeable stream? Parallel?<br>
**Red flags:** Đổi `List` sang `Stream` tự giảm memory hoặc database rows.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-005 — `FOUNDATION`
**Question:** Intermediate và terminal operations khác nhau thế nào? Lazy evaluation hoạt động ra sao?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu Stream fundamentals bắt buộc.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Pipeline construction, terminal trigger, stateless/stateful operation.<br>
**Answer outline:** Intermediate như `map/filter` trả stream mới và chưa chạy; terminal như `collect/count/findFirst` kích hoạt traversal. Operations thường fuse theo element; stateful `sorted/distinct` có thể cần buffer.<br>
**Required trade-offs:** Laziness cho short-circuit/fusion nhưng side-effect/debug timing khó đoán nếu mental model sai.<br>
**Follow-up ladder:** Không có terminal thì gì xảy ra? `peek`? `sorted().limit()`?<br>
**Red flags:** Mỗi intermediate operation tạo ngay một collection đầy đủ.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-006 — `FOUNDATION`
**Question:** `map` và `flatMap` khác nhau thế nào? Cho ví dụ với nested list hoặc `Optional`.<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu transformation rất phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** One-to-one transformation so với map-then-flatten.<br>
**Answer outline:** `map` biến mỗi T thành R, có thể tạo nested wrapper; `flatMap` biến T thành stream/wrapper rồi flatten một lớp, phù hợp list-of-lists hoặc chaining Optional-returning lookup.<br>
**Required trade-offs:** FlatMap diễn đạt composition tốt nhưng pipeline quá nested có thể khó đọc hơn explicit loop/method.<br>
**Follow-up ladder:** `mapMulti`? Null mapper result? `Optional.flatMap`?<br>
**Red flags:** FlatMap chỉ là map nhanh hơn hoặc flatten mọi cấp lồng nhau.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-007 — `FOUNDATION`
**Question:** `reduce` làm gì, identity/accumulator/combiner phải thỏa điều kiện nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — reduction question phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Associativity, neutral identity và sequential/parallel equivalence.<br>
**Answer outline:** Reduce gộp elements thành value; identity phải neutral, accumulator/combiner compatible và associative để partition/merge không đổi kết quả. Order-sensitive/non-associative math có thể khác khi parallel.<br>
**Required trade-offs:** Immutable reduction rõ correctness nhưng copy-heavy cho mutable containers; `collect` phù hợp mutable reduction hơn.<br>
**Follow-up ladder:** Sum? String concatenation? `BigDecimal`? Floating point?<br>
**Red flags:** Dùng mutable list làm identity rồi mutate trong `reduce`.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-008 — `FOUNDATION`
**Question:** `Optional` giải quyết vấn đề gì và không giải quyết vấn đề gì?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — Java API absence question rất phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Explicit optional return, composition và giới hạn domain/framework.<br>
**Answer outline:** Optional biểu diễn return value có thể vắng và buộc caller xử lý; không thay validation, error detail hoặc mọi nullable field. Tránh `isPresent/get` imperative khi map/flatMap/orElseThrow diễn đạt flow tốt hơn.<br>
**Required trade-offs:** Explicit absence tăng clarity nhưng dùng tràn lan ở field/parameter/DTO làm API/serialization/framework phức tạp.<br>
**Follow-up ladder:** Empty vs error? `orElse` vs `orElseGet`? Optional collection?<br>
**Red flags:** Optional loại bỏ hoàn toàn `NullPointerException` hoặc dùng `Optional.of(nullable)`.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-009 — `FOUNDATION`
**Question:** Method reference khác lambda thế nào và khi nào làm overload resolution khó hiểu?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `MEDIUM` — thường là follow-up syntax/type inference, không phải trọng tâm Senior.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Equivalent target behavior, bound/unbound/static/constructor reference.<br>
**Answer outline:** Method reference là dạng rút gọn khi method signature phù hợp functional target; compiler vẫn dùng target typing. Overload/generic inference có thể ambiguous, khi đó lambda explicit type/name rõ hơn.<br>
**Required trade-offs:** Ngắn gọn khi intent hiển nhiên; không dùng nếu che receiver/argument mapping.<br>
**Follow-up ladder:** `Type::instanceMethod` nhận receiver thế nào? Constructor reference?<br>
**Red flags:** Method reference luôn nhanh hơn lambda ở runtime.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-010 — `SENIOR`
**Question:** Khi nào dùng `reduce`, khi nào dùng `collect`?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — Stream reduction follow-up phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Immutable value reduction vs mutable accumulation.<br>
**Answer outline:** Reduce phù hợp associative value combination như sum/min; collect có supplier/accumulator/combiner cho mutable result container như list/map/string builder. Không mutate shared identity trong reduce.<br>
**Required trade-offs:** Collect giảm copying nhưng collector contract phức tạp hơn khi parallel.<br>
**Follow-up ladder:** Three-arg reduce? `collectingAndThen`? Parallel combiner?<br>
**Red flags:** Hai API hoàn toàn tương đương, chọn cái ngắn hơn.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-011 — `SENIOR`
**Question:** Dùng `groupingBy`, `partitioningBy` và `toMap` thế nào; duplicate key trong `toMap` phải xử lý ra sao?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — collector/coding question rất phổ biến.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Classification, downstream collector, merge policy và map/order contract.<br>
**Answer outline:** Grouping tạo key→collection/aggregate; partition là boolean groups; `toMap` cần unique key hoặc explicit merge function và optional map supplier. Merge phải phản ánh business rule, không im lặng giữ first/last.<br>
**Required trade-offs:** One-pass collection tiện nhưng có thể materialize lớn; database aggregation phù hợp hơn nếu source/query đủ lớn.<br>
**Follow-up ladder:** Preserve order? Concurrent collector? Null key/value? Duplicate roles?<br>
**Red flags:** Duplicate key exception được sửa bằng `(a,b)->a` không có invariant.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-012 — `SENIOR`
**Question:** `Stream.toList()`, `Collectors.toList()` và `Collectors.toUnmodifiableList()` khác contract nào trên Java 21?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — modern Java API trap thường gặp.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Mutability/null/implementation guarantees thay vì dựa vào class quan sát được.<br>
**Answer outline:** `Stream.toList()` trả unmodifiable list; `Collectors.toList()` không hứa type/mutability; `toUnmodifiableList()` trả unmodifiable và không chấp nhận null. Code chỉ dựa vào documented contract, không concrete implementation.<br>
**Required trade-offs:** Immutable result bảo vệ ownership nhưng caller cần copy nếu mutation là requirement.<br>
**Follow-up ladder:** `List.copyOf`? Null element? Serialization? Encounter order?<br>
**Red flags:** `Collectors.toList()` được đảm bảo luôn là mutable `ArrayList`.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-013 — `SENIOR`
**Question:** Vì sao side effect và shared mutable state trong `map/filter/forEach` nguy hiểm?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — Stream correctness question phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Non-interference, statelessness, lazy execution và parallel race.<br>
**Answer outline:** Source/state mutation có thể làm result phụ thuộc traversal/order, gây CME/race và phá optimization/parallel semantics. Transformation nên pure; side effect rõ ràng đặt ở controlled terminal/batch boundary.<br>
**Required trade-offs:** Pure pipeline dễ test nhưng đôi lúc explicit loop/transactional batch rõ side effect hơn.<br>
**Follow-up ladder:** Logging trong `peek`? Atomic counter? DB call trong map?<br>
**Red flags:** Dùng synchronized list trong parallel stream rồi coi pipeline đúng/nhanh.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-014 — `SENIOR`
**Question:** Stream có reuse được không? Tại sao lưu Stream vào field hoặc trả Stream từ resource-owning layer cần cẩn thận?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — lifecycle question quen thuộc.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** One-shot consumption, deferred execution và resource ownership.<br>
**Answer outline:** Sau terminal operation stream không reuse; muốn chạy lại cần source/supplier mới. Stream backed by file/DB/cursor có close/transaction lifetime; API trả stream phải quy định caller closes và source còn valid.<br>
**Required trade-offs:** Lazy exposure giảm upfront work nhưng chuyển lifecycle/error responsibility cho caller.<br>
**Follow-up ladder:** Try-with-resources? Supplier<Stream>? Repository Stream?<br>
**Red flags:** Cache một Stream để nhiều request cùng consume.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-015 — `SENIOR`
**Question:** Short-circuit operations hoạt động ra sao với finite/infinite stream? Thứ tự `filter`, `sorted`, `limit` ảnh hưởng gì?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — lazy evaluation follow-up phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** `limit/find/anyMatch`, stateful buffering và semantic-preserving reorder.<br>
**Answer outline:** Short-circuit có thể dừng sớm; infinite source cần bounded/short-circuit terminal. Filter trước sorted thường giảm input nếu semantics same; sorted trước limit chọn global top/order, limit trước sorted đổi nghĩa.<br>
**Required trade-offs:** Reorder để performance chỉ hợp lệ khi không đổi result/side effects.<br>
**Follow-up ladder:** `distinct` infinite? `takeWhile`? Ordered parallel limit?<br>
**Red flags:** JVM tự reorder mọi operation tối ưu như SQL planner.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-016 — `SENIOR`
**Question:** Encounter order ảnh hưởng `findFirst`, `findAny`, `forEach` và `forEachOrdered` thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — sequential/parallel semantics thường được hỏi.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Source order, deterministic requirement và parallel constraint.<br>
**Answer outline:** Ordered source mang encounter order; `findFirst` giữ first, `findAny` cho phép bất kỳ; parallel `forEach` không giữ order, `forEachOrdered` giữ nhưng hạn chế parallel benefit. Unordered source không tạo stable order tự nhiên.<br>
**Required trade-offs:** Determinism/order có coordination cost; bỏ order chỉ khi contract không cần.<br>
**Follow-up ladder:** `unordered()`? Set source? Collector order? API response?<br>
**Red flags:** Parallel stream luôn trả result khác thứ tự hoặc `findAny` luôn nhanh hơn.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-017 — `SENIOR`
**Question:** Khi nào nên trả `Optional`, và vì sao thường tránh dùng Optional làm entity field, DTO field hoặc method parameter?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — API design question phổ biến.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Absence at return boundary, framework/serialization interoperability và null policy.<br>
**Answer outline:** Phù hợp return value “có thể không tìm thấy”; parameter nên overload/request type/validation rõ; entity/DTO field thường làm JPA/Jackson/schema awkward và có thể tạo Optional null. Collection rỗng thường tốt hơn Optional<List>.<br>
**Required trade-offs:** Optional explicit cho caller Java nhưng không luôn phù hợp wire/persistence boundary.<br>
**Follow-up ladder:** Repository Optional? `orElse` eager? 404 vs empty?<br>
**Red flags:** Mọi nullable value đều bọc Optional, kể cả field và constructor arg.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-018 — `SENIOR`
**Question:** Xử lý checked exception trong lambda/stream pipeline thế nào mà không che mất error contract?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — practical follow-up, không phải câu screening chính.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Functional interface throws contract, wrapping và failure semantics.<br>
**Answer outline:** Extract named method, handle locally khi có recovery, wrap thành domain/runtime exception có cause/context hoặc định nghĩa throwing interface ở internal boundary. Không catch-and-ignore hoặc trả null để pipeline tiếp tục mù quáng.<br>
**Required trade-offs:** Wrapper giúp compose standard APIs nhưng thay checked contract; explicit loop đôi khi rõ partial-failure policy hơn.<br>
**Follow-up ladder:** Batch continue-on-error? Sneaky throw? Error aggregation?<br>
**Red flags:** Catch `Exception` trong map rồi log và bỏ element không có business policy.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-019 — `SENIOR`
**Question:** Boxing, allocation và stateful operations ảnh hưởng performance Stream thế nào? Khi nào dùng `IntStream`/`LongStream`?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — performance follow-up cần measurement.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Primitive specialization, allocation và benchmark discipline.<br>
**Answer outline:** `Stream<Integer>` có boxing/unboxing; primitive streams giảm wrapper/allocation và có sum/average. Lambda/pipeline có thể optimize nhưng sort/distinct/collect vẫn buffer; chỉ đổi code sau profiler/JMH phù hợp, không microbenchmark bằng stopwatch.<br>
**Required trade-offs:** Loop có thể nhanh/rõ hơn hot numeric path; Stream thường tăng readability cho transformation business thông thường.<br>
**Follow-up ladder:** `mapToLong`? JIT escape analysis? JMH warm-up? GC metric?<br>
**Red flags:** Stream luôn chậm hoặc luôn được JVM tối ưu bằng loop.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-020 — `SENIOR`
**Question:** Khi nào `parallelStream()` có thể giúp, và khi nào làm chậm hoặc sai?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — parallel stream question rất phổ biến ở Senior Java.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Splittable large CPU work, overhead, shared pool, associativity và side effects.<br>
**Answer outline:** Hữu ích cho sufficiently large, independent, CPU-bound, splittable work với associative reduction và measurement. Không mặc định cho small data, blocking I/O, ordered/stateful pipeline, shared mutation hoặc request path không kiểm soát pool.<br>
**Required trade-offs:** Throughput tiềm năng đổi lấy scheduling/merge overhead, contention và khó kiểm soát resource/isolation.<br>
**Follow-up ladder:** Common pool? Blocking DB call? Custom pool? Virtual thread?<br>
**Red flags:** Chỉ đổi `.stream()` thành `.parallelStream()` để dùng nhiều core.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-021 — `ARCHITECT`
**Question:** Có nên dùng parallel stream trong Spring Boot request/consumer path? Thiết kế isolation và observability thế nào?<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `MEDIUM` — server-runtime architecture follow-up.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Common-pool contention, request cancellation/deadline, context propagation và capacity budget.<br>
**Answer outline:** Tránh implicit shared common pool cho blocking/request work; xác định CPU budget, executor ownership, timeout/cancellation, MDC/security context, metrics và saturation. So sánh sequential, bounded executor, virtual threads cho blocking I/O và async design bằng workload evidence.<br>
**Required trade-offs:** Per-request parallelism giảm single-request latency nhưng có thể phá total throughput/fairness dưới load.<br>
**Follow-up ladder:** Nested parallelism? Container CPU quota? Backpressure? Shutdown?<br>
**Red flags:** Máy có N cores nên mọi request được dùng N threads.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-022 — `ARCHITECT`
**Question:** Gọi `.stream()` sau `repository.findAll()` có làm database query lazy/streaming không? Thiết kế resource và N+1 boundary ra sao?<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `LOW` — cross-layer Java/JPA discriminator.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Collection đã materialize, persistence cursor lifecycle và per-element query amplification.<br>
**Answer outline:** `findAll()` đã materialize list rồi mới stream in-memory; JPA query stream riêng cần transaction/close/fetch-size/driver behavior. Mapper gọi repository per element vẫn N+1; dùng bounded query/projection/join/pagination theo contract.<br>
**Required trade-offs:** Cursor streaming giảm heap nhưng giữ DB connection/transaction lâu; pagination/batch thường vận hành dễ hơn.<br>
**Follow-up ladder:** Lazy relation? Open Session in View? Backpressure? Error giữa stream?<br>
**Red flags:** Stream API tự push `filter/map` thành SQL như LINQ provider.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-STREAM-023 — `EXPERT`
**Question:** Thiết kế custom `Collector` đúng cho sequential và parallel execution; associativity/characteristics sai gây bug gì?<br>
**Target depth:** `D4`<br>
**Interview likelihood:** `LOW` — Stream internals/contract discriminator.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Supplier, accumulator, combiner, finisher và `CONCURRENT`/`UNORDERED`/`IDENTITY_FINISH` claims.<br>
**Answer outline:** Mỗi partial container phải independent trừ khi collector thật sự concurrent; combiner associative/compatible với accumulator; finisher/characteristics đúng contract. Test partition/merge orders và compare sequential/parallel property, không chỉ happy example.<br>
**Required trade-offs:** Custom collector có thể tránh intermediate allocation nhưng correctness burden cao; compose standard collectors thường an toàn hơn.<br>
**Follow-up ladder:** Concurrent source? Encounter order? Mutable identity? Property test?<br>
**Red flags:** Gắn `CONCURRENT` chỉ vì accumulator collection thread-safe.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `JAVA-01` active: tạo core/deep-dive, viết micro/property tests cho laziness, collector laws, Optional contract và parallel equivalence; review actual service pipelines bằng dataset/query/allocation evidence. Executor/JMM/virtual-thread experiments thuộc `CON-01`/`JDK-01`; JPA query optimization thuộc `DB-01`. Stable IDs không tái sử dụng.

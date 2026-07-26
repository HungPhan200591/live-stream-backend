# Java Interview Question Bank — Collections, Data Structures and Complexity

> Status: `DRAFT`<br>
> Domain owner: `Java collections / data structures / algorithms`<br>
> Active slice: `NONE`; preview target `JAVA-01 — collections, algorithms and complexity`<br>
> Runtime baseline: `Java 21`; implementation details are not API guarantees unless stated<br>
> Related roadmap: [Stage 1](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-1---java-core-state-và-concurrency)<br>
> Related depth rubric: [Java language, collections, algorithm and complexity](../../../knowledge-depth-rubric.md#31-java-language-collections-algorithm-và-complexity--p0-target-d3)<br>
> Related theory: [Core theory](../theory/core/collections-data-structures-and-complexity.md), [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md) — `TEACHABLE_DRAFT`, evidence `NOT RUN`<br>
> Updated: `2026-07-26`

Preview này không implement `JAVA-01`, không active case và không tạo evidence. `Interview likelihood` là heuristic trong phạm vi Senior Java backend, không phải tỷ lệ thị trường đã đo. Mọi câu giữ `UNANSWERED`, test `NOT RUN`.

## Scope boundary

File này sở hữu collection semantics, data-structure selection, common algorithm patterns và time/space complexity. Equality/mutable-key contract nằm ở `language-object-semantics-and-generics.md`; Stream API, collectors và functional composition thuộc bank kế tiếp; JMM/lock internals của concurrent collections thuộc `CON-01`.

## Project anchor

Current authentication path chuyển role relations từ `List` sang `Set` để biểu diễn membership/uniqueness. Stream endpoints và session cleanup đang materialize toàn bộ kết quả vào `List`, tạo điểm nối cho pagination, memory bound và algorithmic-cost reasoning khi `JAVA-01` active.

## Coverage

| Topic | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Collection contracts and implementations | 8 | 5 | 1 | 1 |
| Algorithms and complexity | 1 | 5 | 0 | 0 |
| Capacity and API design | 0 | 0 | 1 | 0 |
| **Tổng** | **9** | **10** | **2** | **1** |

## Recommended practice order

1. First pass — câu phổ biến: `JAVA-COLL-001` đến `JAVA-COLL-015`, `JAVA-COLL-018`.
2. Senior follow-up: `JAVA-COLL-016`, `JAVA-COLL-017`, `JAVA-COLL-019`.
3. Project application: `JAVA-COLL-014`, `JAVA-COLL-019`, `JAVA-COLL-021`.
4. Architect/Expert stretch: `JAVA-COLL-020` đến `JAVA-COLL-022`.

## Questions

### JAVA-COLL-001 — `FOUNDATION`
**Question:** `List`, `Set` và `Map` khác nhau về contract và use case nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu Java Collections mở đầu rất phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Ordering, duplicate, key-value lookup và semantic intent.<br>
**Answer outline:** `List` là sequence có index/duplicate; `Set` biểu diễn uniqueness/membership; `Map` ánh xạ unique key tới value. Chọn theo access pattern và invariant, không theo thói quen.<br>
**Required trade-offs:** Stronger semantics như order/sort/concurrency thường thêm CPU hoặc memory cost.<br>
**Follow-up ladder:** Null? Iteration order? Duplicate key? Immutable variants?<br>
**Red flags:** Chọn `List` cho mọi thứ rồi tự deduplicate/search tuyến tính.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-002 — `FOUNDATION`
**Question:** Array và `ArrayList` khác nhau thế nào về size, type, memory và operation?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu cấu trúc dữ liệu Java cơ bản.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Fixed contiguous storage, resizing, primitive support và generic API.<br>
**Answer outline:** Array fixed length, hỗ trợ primitive/reference và runtime component type; `ArrayList` là resizable object array cho reference types, quản lý capacity/size và boxing primitive. Cả hai random access O(1).<br>
**Required trade-offs:** Array ít abstraction/overhead hơn; `ArrayList` tiện mutation/API nhưng có spare capacity, resize và boxing.<br>
**Follow-up ladder:** Covariance? Generic array? `toArray`? Initial capacity?<br>
**Red flags:** `ArrayList` là linked list hoặc mọi add luôn O(1) worst-case.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-003 — `FOUNDATION`
**Question:** `ArrayList` và `LinkedList` khác nhau thế nào, vì sao `LinkedList` hiếm khi nhanh hơn trong code thực tế?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu so sánh collection kinh điển.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Big-O đi cùng traversal, locality, allocation và actual access path.<br>
**Answer outline:** `ArrayList` get O(1), append amortized O(1), middle insert/remove O(n); `LinkedList` get/search O(n), end/node-known mutation O(1). Pointer chasing, node allocation và cache locality thường khiến `ArrayList`/`ArrayDeque` tốt hơn.<br>
**Required trade-offs:** Linked structure hữu ích khi có node reference/iterator và mutation pattern phù hợp; Big-O không đủ để chọn.<br>
**Follow-up ladder:** Queue nên dùng gì? Insert đầu? Memory per element? Benchmark?<br>
**Red flags:** `LinkedList` insert luôn O(1) dù phải tìm index O(n).<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-004 — `FOUNDATION`
**Question:** `HashMap` hoạt động ở mức khái niệm như thế nào khi `put` và `get`?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — một trong các câu Senior Java phổ biến nhất.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Hash, bucket, equality, collision và average complexity.<br>
**Answer outline:** Hash của key được phân bố tới bucket; trong bucket dùng hash/equality để tìm key; put replace value nếu key equal, nếu không thêm entry. Average get/put O(1) khi distribution/capacity hợp lý; collision làm tăng work.<br>
**Required trade-offs:** Tốc độ lookup đổi lấy memory overhead, unordered contract và dependency vào stable `equals/hashCode`.<br>
**Follow-up ladder:** Null key? Resize? Collision? Mutable key? Tree bin?<br>
**Red flags:** Hash code là index duy nhất hoặc collision nghĩa là mất dữ liệu cũ.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-005 — `FOUNDATION`
**Question:** `HashSet` hoạt động ra sao và dựa vào contract nào để loại duplicate?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — follow-up trực tiếp của HashMap/equality.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Set uniqueness và backing-map mental model.<br>
**Answer outline:** `HashSet` dùng hash-based storage, về khái niệm lưu element như key với placeholder value; duplicate được xác định bằng hash/equality contract, không phải object address.<br>
**Required trade-offs:** Membership average O(1) nhưng không đảm bảo sort/order; `LinkedHashSet`/`TreeSet` thêm order cost.<br>
**Follow-up ladder:** Mutable element? Null? `TreeSet` uniqueness? Iteration order?<br>
**Red flags:** Set tự so mọi phần tử với nhau O(n) cho mỗi add.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-006 — `FOUNDATION`
**Question:** Khi nào chọn `HashMap`, `LinkedHashMap` hoặc `TreeMap`?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu lựa chọn Map thường gặp.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Unordered average lookup, insertion/access order và sorted navigation.<br>
**Answer outline:** `HashMap` cho general key lookup; `LinkedHashMap` giữ insertion order hoặc access order; `TreeMap` giữ key sorted/navigable với O(log n). Contract order phải là requirement thật.<br>
**Required trade-offs:** Ordered/sorted structures thêm memory hoặc log-time cost; hash map không có order contract để API dựa vào.<br>
**Follow-up ladder:** Custom comparator? LRU? Range query? Null key?<br>
**Red flags:** Dựa vào iteration order quan sát được của `HashMap`.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-007 — `FOUNDATION`
**Question:** `Queue`, `Deque` và `PriorityQueue` khác nhau thế nào? Vì sao không nên dùng legacy `Stack` cho code mới?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu data-structure/API phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** FIFO, double-ended/LIFO và priority-head semantics.<br>
**Answer outline:** Queue thường FIFO; `Deque` thêm hai đầu và dùng được làm stack; `PriorityQueue` trả phần tử ưu tiên nhỏ/lớn theo comparator, không giữ toàn bộ iteration sorted. `ArrayDeque` thường phù hợp stack/queue hơn `Stack` legacy.<br>
**Required trade-offs:** Heap hỗ trợ head priority tốt nhưng arbitrary search/remove không O(log n); deque không tự sort.<br>
**Follow-up ladder:** `offer` vs `add`? `poll` vs `remove`? Bounded queue? Max heap?<br>
**Red flags:** Iterating `PriorityQueue` sẽ luôn ra thứ tự sorted.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-008 — `FOUNDATION`
**Question:** `Comparable` và `Comparator` khác nhau thế nào, comparator contract sai gây lỗi gì?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — sorting/TreeSet question phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Natural order, external/multiple order và total-order consistency.<br>
**Answer outline:** `Comparable` đặt natural order trong type; `Comparator` tách strategy và hỗ trợ nhiều ordering. Comparator cần antisymmetry/transitivity/consistency; sorted map/set dùng comparison để xác định key uniqueness nên inconsistency với equals phải được hiểu/document.<br>
**Required trade-offs:** Natural order tiện nhưng coupling; comparator explicit linh hoạt hơn.<br>
**Follow-up ladder:** Null handling? Stable sort? Chaining? Subtraction overflow?<br>
**Red flags:** Comparator trả boolean hoặc dùng `a - b` cho mọi integer range.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-009 — `FOUNDATION`
**Question:** Big-O mô tả điều gì? Phân biệt worst-case, average-case và amortized complexity bằng `ArrayList`/`HashMap`.<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — nền tảng bắt buộc cho algorithm interview.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Growth rate, input variable và không nhầm complexity với milliseconds.<br>
**Answer outline:** Big-O mô tả upper growth theo input; worst/average phụ thuộc distribution; amortized trải chi phí spike trên sequence. `ArrayList.add` amortized O(1) nhưng resize O(n); `HashMap` average O(1), collision/worst behavior khác.<br>
**Required trade-offs:** Complexity loại bỏ constants/hardware; lựa chọn production còn cần memory, locality, contention và measurement.<br>
**Follow-up ladder:** O(n+m)? Space complexity? Nested loops có luôn O(n²)?<br>
**Red flags:** O(1) luôn nhanh hơn O(n) cho mọi input hoặc amortized nghĩa là mỗi operation O(1).<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-010 — `SENIOR`
**Question:** Capacity, load factor, collision và resize ảnh hưởng `HashMap` thế nào? Khi nào pre-size map có ý nghĩa?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — HashMap internals follow-up rất phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Time-memory trade-off thay vì học thuộc threshold constants.<br>
**Answer outline:** Capacity/bucket count và load threshold quyết định resize; quá dense tăng collision, quá sparse tốn memory; resize allocate/redistribute và tạo latency spike. Pre-size khi ước lượng số entry đáng tin và hot path chứng minh cần, không blanket.<br>
**Required trade-offs:** Dư capacity giảm resize/collision nhưng tăng memory/GC footprint.<br>
**Follow-up ladder:** Constructor capacity có bằng max entries? Treeification? Poor hash? Benchmark?<br>
**Red flags:** Pin internal constants như API guarantee hoặc luôn đặt capacity cực lớn.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-011 — `SENIOR`
**Question:** Fail-fast iterator và `ConcurrentModificationException` nghĩa là gì? Xóa phần tử khi iterate thế nào cho đúng?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — câu collection mutation quen thuộc.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Structural modification, iterator contract và best-effort detection.<br>
**Answer outline:** Nhiều iterator phát hiện structural modification ngoài iterator và fail nhanh; đây không phải concurrency safety guarantee. Dùng `Iterator.remove`, `removeIf`, collect result mới hoặc concurrent collection theo requirement.<br>
**Required trade-offs:** Copy/filter dễ reasoning nhưng thêm memory; in-place iterator mutation tiết kiệm hơn nhưng API hạn chế.<br>
**Follow-up ladder:** Set value có structural không? Enhanced for? Snapshot iterator? Multi-thread?<br>
**Red flags:** Catch `ConcurrentModificationException` rồi retry hoặc coi exception luôn do nhiều thread.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-012 — `SENIOR`
**Question:** Unmodifiable view, immutable collection và defensive copy khác nhau thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — immutability/API boundary follow-up phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Aliasing, shallow immutability và ownership.<br>
**Answer outline:** Unmodifiable wrapper chặn mutation qua view nhưng backing collection có thể đổi; copy tách structure tại thời điểm tạo; cả hai vẫn shallow nếu elements mutable. API phải quyết định snapshot/live view và element ownership.<br>
**Required trade-offs:** Copy bảo vệ invariant nhưng tốn O(n) time/memory; live view ít copy nhưng temporal coupling.<br>
**Follow-up ladder:** `List.copyOf`? Null? Mutable element? Serialization?<br>
**Red flags:** `Collections.unmodifiableList` làm backing list immutable.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-013 — `SENIOR`
**Question:** `ConcurrentHashMap` khác `HashMap`, `Hashtable` và `Collections.synchronizedMap` ở điểm nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — concurrent collections question rất phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Thread safety, concurrency granularity, iteration và compound atomic operation.<br>
**Answer outline:** `HashMap` không thread-safe; legacy/global synchronization giảm concurrency; `ConcurrentHashMap` hỗ trợ concurrent access và atomic APIs như `compute`/`merge`, iterator weakly consistent. Thread-safe method riêng lẻ không làm check-then-act sequence atomic.<br>
**Required trade-offs:** Concurrent map thêm coordination/semantic constraints; immutable snapshot hoặc actor ownership có thể đơn giản hơn.<br>
**Follow-up ladder:** Null key/value? `computeIfAbsent` side effect? Size under writes? JMM visibility?<br>
**Red flags:** `containsKey` rồi `put` được coi atomic vì map concurrent.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-014 — `SENIOR`
**Question:** Role/permission membership nên dùng `List` hay `Set`? Chọn dựa trên invariant và access pattern nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — câu chọn collection rất thực tế.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Uniqueness, membership lookup, order contract và data volume.<br>
**Answer outline:** Nếu role unique và thường `contains`, Set biểu đạt intent và average lookup tốt; List phù hợp khi duplicate/order/index có nghĩa. DB unique constraint vẫn là durable invariant; Java Set không sửa duplicate nguồn một cách âm thầm.<br>
**Required trade-offs:** Hash set thêm overhead/không order; với tập rất nhỏ chênh lệch performance có thể không đáng kể nhưng semantics vẫn quan trọng.<br>
**Follow-up ladder:** EnumSet? Stable response order? Case normalization? Authorization cache?<br>
**Red flags:** Chọn Set chỉ vì O(1) mà bỏ qua stable order/API output.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-015 — `SENIOR`
**Question:** Giải bài two-sum hoặc frequency counting bằng `HashMap`; time/space complexity và alternative là gì?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — coding pattern HashMap rất phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** One-pass lookup, duplicate/index semantics và explicit complexity.<br>
**Answer outline:** Scan một lần, lưu seen value/frequency/index; lookup complement/count average O(1), tổng average O(n), space O(n). Sort+two pointers O(n log n), có thể giảm extra map nhưng ảnh hưởng index/input order.<br>
**Required trade-offs:** Hashing dùng memory và average assumptions; sorting ổn định hơn về worst reasoning nhưng chậm hơn và có mutation/copy cost.<br>
**Follow-up ladder:** Duplicate numbers? Overflow? Return all pairs? Streaming input?<br>
**Red flags:** Nói O(n) mà bên trong vẫn scan list cho mỗi element.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-016 — `SENIOR`
**Question:** Tìm top-K phần tử bằng heap khác full sort thế nào? Khi nào `PriorityQueue` phù hợp?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — algorithm follow-up phổ biến nhưng không phải mọi vòng.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** O(n log k), heap orientation và output-order requirement.<br>
**Answer outline:** Giữ min-heap size k, mỗi candidate tốt hơn head thì replace: O(n log k), O(k); full sort O(n log n). Nếu cần output sorted, còn O(k log k); k gần n hoặc repeated queries có alternative khác.<br>
**Required trade-offs:** Heap tiết kiệm time/memory cho k nhỏ nhưng code/constant phức tạp hơn sort.<br>
**Follow-up ladder:** Kth largest? Streaming? Ties? Comparator? Quickselect?<br>
**Red flags:** Dùng max-heap chứa toàn bộ n rồi vẫn gọi là O(k) memory.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-017 — `SENIOR`
**Question:** Thiết kế LRU cache bằng `LinkedHashMap`; giải pháp này còn thiếu gì để dùng production?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — data-structure/design exercise quen thuộc.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Access order, eviction bound và difference giữa exercise với cache system.<br>
**Answer outline:** Access-order `LinkedHashMap` + bounded eldest eviction cho average O(1) get/put. Production còn cần thread safety, TTL/weight, loading/error policy, stampede, metrics và no caching of mutable/secret data without policy.<br>
**Required trade-offs:** Entry-count bound đơn giản nhưng không phản ánh memory weight; global lock dễ đúng nhưng contention.<br>
**Follow-up ladder:** TTL vs LRU? Concurrent access? Eviction callback? Cache library?<br>
**Red flags:** Unbounded map được gọi là cache hoặc tự build thay thư viện mà không có requirement.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-018 — `SENIOR`
**Question:** BFS và DFS khác nhau về data structure, complexity và use case nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — graph/tree traversal question phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Queue/deque, stack/recursion, visited set và O(V+E).<br>
**Answer outline:** BFS dùng queue, tìm shortest path theo số cạnh trong unweighted graph; DFS dùng stack/recursion cho reachability, cycle/topological-style exploration. Cả hai O(V+E), space tới O(V); cần visited với graph.<br>
**Required trade-offs:** BFS frontier có thể tốn memory; recursive DFS có stack-overflow risk trên graph sâu.<br>
**Follow-up ladder:** Tree không visited? Directed cycle? Weighted shortest path? Iterative DFS?<br>
**Red flags:** DFS luôn dùng ít memory hoặc BFS giải weighted shortest path bất kỳ.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-019 — `SENIOR`
**Question:** Phân tích hidden time/space cost của code load toàn bộ streams/sessions rồi map/filter/sort trong memory.<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — production complexity/debugging scenario.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Materialization, DB/network cost, object allocation và algorithm composition.<br>
**Answer outline:** Query trả n rows, entity/DTO allocation O(n) memory/time; filter/map O(n); sort O(n log n); nested per-item lookup có thể thành N+1. Push bounded filter/order/pagination xuống DB khi semantics phù hợp và đo end-to-end.<br>
**Required trade-offs:** In-memory xử lý linh hoạt nhưng không bounded; DB pushdown giảm transfer nhưng tăng query/index responsibility.<br>
**Follow-up ladder:** Cursor pagination? Backpressure? `Stream` có giảm memory tự động? GC evidence?<br>
**Red flags:** Đổi sang Stream API rồi tuyên bố complexity/memory đã tối ưu.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-020 — `ARCHITECT`
**Question:** Collection type trong public API/domain contract phải quy định order, duplicate, mutability và compatibility ra sao?<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `MEDIUM` — API/design follow-up cho Senior/Architect.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Internal implementation không được rò thành accidental external contract.<br>
**Answer outline:** Wire array cần explicit ordering/pagination/duplicate semantics; Java return type nên tối thiểu capability cần thiết và bảo vệ ownership; thay HashSet/List có thể đổi order/equality/client behavior dù schema JSON vẫn là array.<br>
**Required trade-offs:** Stable sort/order tốn compute/index nhưng giúp deterministic clients/tests; unspecified order linh hoạt hơn nhưng client không được dựa vào.<br>
**Follow-up ladder:** Set serialize thành gì? Versioning? Snapshot vs live view? Deterministic signature?<br>
**Red flags:** Trả mutable internal collection hoặc coi implementation iteration order là contract.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-021 — `ARCHITECT`
**Question:** Thiết kế bounded in-memory index/cache cho hàng triệu stream/session theo memory, eviction và failure boundary nào?<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `LOW` — capacity/operability stretch.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Entry overhead, cardinality bound, admission/eviction và source of truth.<br>
**Answer outline:** Estimate bytes/entry × cardinality/headroom; bound size/weight/TTL; define key/value immutability, admission, eviction and rebuild; expose hit/eviction/load metrics; durable authority phải thắng cache và overload không được thành OOM.<br>
**Required trade-offs:** Local cache giảm latency nhưng duplicate memory/stale state mỗi instance; distributed cache thêm network/failure/serialization cost.<br>
**Follow-up ladder:** Hot key? Stampede? GC pressure? Rolling deploy? Tenant quota?<br>
**Red flags:** `ConcurrentHashMap` unbounded được coi là production cache.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-COLL-022 — `EXPERT`
**Question:** Adversarial hash collisions có thể phá latency/CPU ra sao, và Java HashMap tree bins giúp nhưng không loại bỏ rủi ro nào?<br>
**Target depth:** `D4`<br>
**Interview likelihood:** `LOW` — pathological/security-performance discriminator.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Average-vs-worst complexity, implementation boundary và attacker-controlled keys.<br>
**Answer outline:** Nhiều keys cùng bucket tăng comparison/CPU; modern implementations có thể treeify bucket dưới điều kiện nhất định để giảm lookup, nhưng API không hứa internal thresholds, poor/expensive equality vẫn gây cost, resize/memory và hash-flood input vẫn cần bounds/rate limit.<br>
**Required trade-offs:** Stronger/randomized hash hoặc sorted map có CPU/compatibility cost; input limits thường là defense thực dụng hơn tuning internals.<br>
**Follow-up ladder:** Comparable keys? Expensive `equals`? HashDoS? JFR/profile evidence?<br>
**Red flags:** Treeification biến mọi `HashMap` operation thành guaranteed O(log n) hoặc collision không còn đáng quan tâm.<br>
**Evidence:** [Core theory](../theory/core/collections-data-structures-and-complexity.md) · [Deep-dive](../theory/deep-dives/hash-tables-concurrent-collections-and-bounded-queues.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `JAVA-01` active: tạo core/deep-dive, viết micro/property tests cho collection contracts và benchmark có hypothesis trên hot path thật; nối list-all/role-membership path với dataset, memory và query evidence. Stream/collectors thuộc bank kế tiếp; concurrent data-structure internals được đào sâu ở `CON-01`. Stable IDs không tái sử dụng.

# Java Interview Question Bank — Language, Object Semantics and Generics

> Status: `DRAFT`<br>
> Domain owner: `Java language / object semantics / type system`<br>
> Active slice: `NONE`; preview target `JAVA-01 — language, equality/immutability and generics`<br>
> Runtime baseline: `Java 21`; later-LTS migration policy belongs to `JDK-02`<br>
> Related roadmap: [Stage 1](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-1---java-core-state-và-concurrency)<br>
> Related depth rubric: [Java language](../../../knowledge-depth-rubric.md#31-java-language-collections-algorithm-và-complexity--p0-target-d3), [Object-oriented design](../../../knowledge-depth-rubric.md#32-object-oriented-design-và-refactoring--p0-target-d3)<br>
> Related theory: [Language, Object Semantics and Generics](../theory/core/language-object-semantics-and-generics.md), [equality/erasure deep-dive](../theory/deep-dives/equality-erasure-variance-and-mutable-keys.md) — `DRAFT`, evidence `NOT RUN`<br>
> Updated: `2026-07-25`

Preview này không implement `JAVA-01`, không active case và không tạo evidence. `Interview likelihood` là heuristic trong phạm vi Senior Java backend, không phải tỷ lệ thị trường đã đo. Mọi câu giữ `UNANSWERED`, test `NOT RUN`.

## Scope boundary

File này sở hữu Java pass-by-value, equality/hash contract, immutability, inheritance/interface semantics, records/sealed classes và generics/type erasure. Collections, Stream API, algorithm/complexity và JVM internals được tách sang bank khác để first pass không bị loãng.

## Project anchor

Current JPA entities và nhiều DTO đang dùng Lombok `@Data`, vì vậy generated `equals/hashCode/toString` trên mutable/persistent objects là điểm review thực tế. Roadmap còn yêu cầu thiết kế `Money` bằng `BigDecimal`; value semantics, canonical scale và immutability sẽ được kiểm chứng khi `JAVA-01` active.

## Coverage

| Topic | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Language and OOP fundamentals | 4 | 1 | 1 | 0 |
| Equality and immutability | 3 | 4 | 0 | 0 |
| Generics and type system | 1 | 4 | 0 | 1 |
| **Tổng** | **8** | **9** | **1** | **1** |

## Recommended practice order

1. First pass — câu phổ biến: `JAVA-LANG-001` đến `JAVA-LANG-009`, `JAVA-LANG-011` đến `JAVA-LANG-014`.
2. Senior follow-up: `JAVA-LANG-010`, `JAVA-LANG-015`, `JAVA-LANG-016`, `JAVA-LANG-017`.
3. Project application: dùng `JAVA-LANG-009`, `JAVA-LANG-010`, `JAVA-LANG-011`, `JAVA-LANG-017` để review entity/value object của project.
4. Architect/Expert stretch: `JAVA-LANG-018`, `JAVA-LANG-019`.

## Questions

### JAVA-LANG-001 — `FOUNDATION`
**Question:** Java là pass-by-value hay pass-by-reference? Điều gì được copy khi truyền một object vào method?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu Java core rất phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Phân biệt object identity với giá trị reference được copy.<br>
**Answer outline:** Java luôn truyền bản sao của value; với object, value đó là reference. Method có thể mutate object qua reference nhưng reassign parameter không đổi reference của caller.<br>
**Required trade-offs:** Mutable object cho phép side effect qua reference; immutable value làm data flow dễ reasoning hơn.<br>
**Follow-up ladder:** Swap hai object? Primitive wrapper? Array? `final` parameter?<br>
**Red flags:** Nói Java pass-by-reference vì object bị mutate.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-002 — `FOUNDATION`
**Question:** `==` và `equals()` khác nhau thế nào với primitive, wrapper và object?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu equality nền tảng gần như bắt buộc.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Value equality, identity và autoboxing/cache trap.<br>
**Answer outline:** Primitive `==` so value; object `==` so reference identity; `equals()` theo semantic contract của class. Wrapper/String caching có thể làm một vài identity comparison tình cờ đúng nhưng không phải value contract.<br>
**Required trade-offs:** Identity phù hợp singleton/sentinel hẹp; domain value phải dùng semantic equality rõ ràng.<br>
**Follow-up ladder:** `Integer` cache? `BigDecimal.equals`? Enum comparison?<br>
**Red flags:** Dùng `==` cho mọi `String` vì string pool.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-003 — `FOUNDATION`
**Question:** Contract giữa `equals()` và `hashCode()` là gì, và chuyện gì xảy ra nếu chỉ override một method?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu Java/collections phổ biến nhất.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Reflexive/symmetric/transitive/consistent/non-null và hash bucket lookup.<br>
**Answer outline:** Objects equal phải có cùng hash; unequal có thể collision. Override equality thì phải override hash nhất quán trên cùng fields; vi phạm làm hash collection không tìm/deduplicate đúng.<br>
**Required trade-offs:** Hash tốt giảm collision nhưng correctness contract quan trọng hơn micro-optimization.<br>
**Follow-up ladder:** Collision? Inheritance symmetry? Mutable field? Lombok generation?<br>
**Red flags:** Hash khác nhau vẫn có thể equal hoặc hash giống nhau nghĩa là equal.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-004 — `FOUNDATION`
**Question:** Vì sao `String` immutable, và immutability đem lại lợi ích gì?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu Java core kinh điển.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Sharing, pooling, hash caching, security và thread-safety boundary.<br>
**Answer outline:** State không đổi sau construction nên có thể share/pool/cache hash an toàn, dùng làm map key và truyền qua threads dễ hơn; operations tạo value mới. Immutability không tự làm toàn object graph immutable nếu chứa mutable member.<br>
**Required trade-offs:** Dễ reasoning và safe sharing đổi lấy allocation/copy ở workload mutation-heavy.<br>
**Follow-up ladder:** `StringBuilder`? Reflection? char array? Secret trong String?<br>
**Red flags:** Immutable chỉ vì class là `final`.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-005 — `FOUNDATION`
**Question:** `final` variable, final reference, final method và final class khác nhau thế nào? `final` có đồng nghĩa immutable không?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu modifier/immutability phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Reassignment, overriding/inheritance và object state.<br>
**Answer outline:** Final variable chỉ assign một lần; final reference vẫn trỏ tới mutable object; final method không override; final class không subclass. Immutable cần state không đổi và không leak mutable internals, không chỉ thêm `final`.<br>
**Required trade-offs:** Final giảm extension/mutation surface nhưng có thể hạn chế framework/proxy/subclass design.<br>
**Follow-up ladder:** Blank final? Safe publication? Defensive copy? Record fields?<br>
**Red flags:** `final List` không thể add/remove phần tử.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-006 — `FOUNDATION`
**Question:** Overloading và overriding khác nhau thế nào? Method nào được chọn ở compile time và runtime?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu OOP/dispatch thường gặp.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Static overload resolution, dynamic dispatch và method signature.<br>
**Answer outline:** Overload cùng tên khác parameter, compiler chọn theo declared types/applicable conversion; override thay implementation instance method và runtime dispatch theo object type. Static/private/final methods không polymorphic override theo cùng nghĩa.<br>
**Required trade-offs:** Overload tiện API nhưng dễ ambiguous với null/autoboxing/varargs; explicit names đôi khi rõ hơn.<br>
**Follow-up ladder:** Covariant return? Checked exception? Static method hiding? Default method conflict?<br>
**Red flags:** Runtime luôn chọn overload “cụ thể nhất” theo actual object.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-007 — `FOUNDATION`
**Question:** Interface và abstract class khác nhau thế nào, khi nào chọn composition thay inheritance?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu OOP/design phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Contract/capability, shared state/implementation và coupling.<br>
**Answer outline:** Interface mô tả capability và hỗ trợ nhiều type contracts/default methods; abstract class chia sẻ state/constructor/implementation trong một hierarchy. Composition phù hợp khi behavior thay đổi độc lập hoặc quan hệ “has-a”, tránh fragile base class.<br>
**Required trade-offs:** Inheritance tái sử dụng nhanh nhưng coupling mạnh; composition thêm delegation nhưng linh hoạt/testable hơn.<br>
**Follow-up ladder:** Default methods? Marker interface? Template Method vs Strategy?<br>
**Red flags:** Chọn abstract class chỉ vì muốn dùng lại vài dòng code.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-008 — `FOUNDATION`
**Question:** Generics giải quyết vấn đề gì? Raw type và unchecked cast nguy hiểm ra sao?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu generics nền tảng rất phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Compile-time type safety, reuse và migration compatibility.<br>
**Answer outline:** Generics parameterize types để compiler kiểm tra và giảm cast; raw type bỏ kiểm tra, cho phép sai type đi vào collection/API rồi lỗi `ClassCastException` xa nguồn. Raw types chủ yếu tồn tại vì compatibility với code cũ.<br>
**Required trade-offs:** Generic abstraction tăng reuse/type safety nhưng signature phức tạp có thể làm API khó đọc.<br>
**Follow-up ladder:** `List<?>` khác raw `List`? Generic method? Primitive type argument?<br>
**Red flags:** Generics chỉ là cú pháp giúp khỏi viết cast, không ảnh hưởng correctness.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-009 — `SENIOR`
**Question:** Điều gì xảy ra khi một object dùng làm key của `HashMap` bị thay đổi field tham gia `equals/hashCode` sau khi insert?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — follow-up equality/HashMap rất phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Bucket invariant và lý do value/key nên immutable.<br>
**Answer outline:** Entry nằm ở bucket theo old hash; lookup bằng mutated key tính hash/equality mới nên có thể không tìm thấy dù entry vẫn tồn tại. Không mutate equality state khi object đang là key; ưu tiên immutable key.<br>
**Required trade-offs:** Mutable domain entity tiện update nhưng không phù hợp làm hash key nếu identity fields thay đổi.<br>
**Follow-up ladder:** Hash collision? Remove sau mutation? `HashSet`? Concurrent mutation?<br>
**Red flags:** HashMap tự rehash entry khi key thay đổi.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-010 — `SENIOR`
**Question:** Thiết kế `Money` value object bằng `BigDecimal` sao cho equality, scale, currency và rounding nhất quán.<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — domain-modeling scenario rất giá trị nhưng không xuất hiện ở mọi vòng.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Value semantics, canonical representation và monetary invariant.<br>
**Answer outline:** Immutable amount+currency; reject invalid input; canonical scale/rounding policy tại boundary; equality contract phải quyết định `1.0` và `1.00`; arithmetic trả object mới và không dùng `double` cho tiền.<br>
**Required trade-offs:** Normalize scale làm equality đơn giản nhưng phải phù hợp currency/business rule; giữ original scale bảo toàn representation nhưng dễ mismatch.<br>
**Follow-up ladder:** `equals` vs `compareTo` của `BigDecimal`? FX? Serialization? Database scale?<br>
**Red flags:** `new BigDecimal(double)` hoặc rounding ngầm ở mọi operation.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-011 — `SENIOR`
**Question:** Làm thế nào thiết kế một class thực sự immutable khi nó chứa `List`, `Date` hoặc mutable object khác?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — immutability implementation question phổ biến.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Constructor validation, defensive copy và representation exposure.<br>
**Answer outline:** Private final state, validate/copy mutable inputs, không expose mutator, trả immutable view/copy, không leak `this` khi construct; nested elements cũng cần immutable/copy policy. `List.copyOf` là shallow copy.<br>
**Required trade-offs:** Defensive copy tăng allocation nhưng bảo vệ invariant; persistent/immutable collections có cost/model khác.<br>
**Follow-up ladder:** Array getter? Builder reuse? Serialization framework? Deep copy?<br>
**Red flags:** Chỉ bọc `Collections.unmodifiableList` quanh list caller vẫn giữ reference.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-012 — `SENIOR`
**Question:** Vì sao `List<Integer>` không phải subtype của `List<Number>`? `? extends` và `? super` thay đổi quyền đọc/ghi thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — câu variance/wildcard kinh điển.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Invariance và type-safety argument.<br>
**Answer outline:** Generic classes Java invariant; nếu assign được thì có thể add `Double` vào `List<Integer>`. `? extends T` đọc như T nhưng không add value cụ thể; `? super T` add T được nhưng đọc chỉ chắc là Object.<br>
**Required trade-offs:** Wildcard tăng flexibility cho caller nhưng có thể làm signature khó hiểu/capture phức tạp.<br>
**Follow-up ladder:** Array covariance? `null` add? Wildcard capture? Return wildcard?<br>
**Red flags:** `extends` nghĩa là collection chỉ đọc ở runtime.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-013 — `SENIOR`
**Question:** Giải thích PECS và áp dụng vào API copy/process collection như thế nào.<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — follow-up generics được hỏi thường xuyên.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Producer Extends, Consumer Super và API variance thực dụng.<br>
**Answer outline:** Source chỉ produce T dùng `? extends T`; destination consume T dùng `? super T`; nếu vừa đọc vừa ghi exact type thường dùng `List<T>`. Giải thích bằng operation caller cần, không học thuộc slogan.<br>
**Required trade-offs:** Signature flexible hơn nhưng không nên thêm wildcard khi API không cần variance.<br>
**Follow-up ladder:** `Collections.copy`? Comparator? Function input/output variance?<br>
**Red flags:** Mọi generic parameter đều đổi thành wildcard theo PECS.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-014 — `SENIOR`
**Question:** Type erasure là gì, và nó tạo ra những giới hạn nào cho generics Java?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — câu generics internals phổ biến ở Senior Java.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Runtime representation, casts/bridge methods và non-reifiable types.<br>
**Answer outline:** Type parameters chủ yếu bị erase về bound/Object để giữ binary compatibility; compiler chèn cast/bridge khi cần. Vì vậy không `new T()`, không `instanceof List<String>`, không generic array trực tiếp và overload không thể chỉ khác type argument erased.<br>
**Required trade-offs:** Erasure giữ compatibility/interoperability nhưng mất runtime type information; type token có thể truyền metadata tường minh.<br>
**Follow-up ladder:** Reifiable type? Reflection thấy gì? `Class<T>`/`TypeReference`?<br>
**Red flags:** JVM không biết gì về generic signature trong class file hoặc generics hoàn toàn runtime-enforced.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-015 — `SENIOR`
**Question:** Heap pollution là gì, có thể xuất hiện qua raw type/generic varargs ra sao, và `@SafeVarargs` cam kết điều gì?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — generics deep follow-up, ít phổ biến hơn PECS/type erasure.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Non-reifiable arrays, unchecked warning và delayed runtime failure.<br>
**Answer outline:** Variable parameterized type trỏ tới object sai type do unchecked/raw/array aliasing; lỗi có thể nổ ở cast xa nguồn. `@SafeVarargs` chỉ suppress khi author bảo đảm method không mutate/expose varargs array theo cách unsafe.<br>
**Required trade-offs:** Varargs API tiện nhưng generic varargs cần implementation audit; collection parameter thường an toàn/rõ hơn.<br>
**Follow-up ladder:** Array covariance? Warning suppression scope? Private/final/static restriction?<br>
**Red flags:** Gắn `@SafeVarargs` để bỏ warning mà không kiểm body.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-016 — `SENIOR`
**Question:** Khi nào dùng record, sealed class/interface và pattern-matching `switch` trên Java 21? Khi nào class thường vẫn phù hợp hơn?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — câu modern Java ngày càng hữu ích nhưng không thay thế core questions.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Transparent data carrier, closed hierarchy, exhaustiveness và framework boundary.<br>
**Answer outline:** Record phù hợp immutable shallow data aggregate/value DTO với generated value semantics; sealed hierarchy giới hạn variants; pattern switch diễn đạt exhaustive behavior. Class thường cần mutable lifecycle, hidden representation, framework/JPA constraints hoặc custom identity semantics.<br>
**Required trade-offs:** Ít boilerplate/exhaustiveness đổi lấy coupling vào closed variants và serialization/evolution considerations.<br>
**Follow-up ladder:** Record có deep immutable không? Compact constructor? JPA entity? Adding permitted subtype?<br>
**Red flags:** Chuyển mọi entity/DTO sang record vì “Java mới”.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-017 — `SENIOR`
**Question:** Vì sao generated `equals/hashCode/toString` bằng Lombok `@Data` trên mutable JPA entity có thể nguy hiểm?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — rất relevant cho Senior Java/Spring/JPA.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Generated ID lifecycle, mutable fields, proxy/lazy data và sensitive logging.<br>
**Answer outline:** Generated equality có thể gồm mutable/non-identity fields; ID null trước persist rồi đổi sau persist làm hash membership hỏng; proxy/class comparison phức tạp; `toString` có thể leak secret hoặc trigger traversal. Chọn explicit identity/equality theo lifecycle và exclude sensitive fields.<br>
**Required trade-offs:** Business-key equality ổn định nếu key truly immutable/unique; ID equality đơn giản sau persistence nhưng transient entities cần policy rõ.<br>
**Follow-up ladder:** Hibernate proxy? Two transient entities? Natural key? Set membership?<br>
**Red flags:** `@Data` luôn an toàn vì Lombok sinh đúng contract tự động.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-018 — `ARCHITECT`
**Question:** Khi shared domain/API model dùng records, sealed hierarchies và generic wrappers, bạn quản lý source/binary/serialization compatibility thế nào?<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `LOW` — architecture/library evolution stretch.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Closed-world assumption, client rollout và representation contract.<br>
**Answer outline:** Phân biệt in-process type model với wire contract; thêm record component/subtype có thể ảnh hưởng constructor, exhaustive switch và serializer/client; version schema, tolerant readers, consumer inventory và expand-contract rollout; tránh share domain jar xuyên service khi coupling lớn.<br>
**Required trade-offs:** Closed types tăng compiler exhaustiveness nhưng giảm independent evolution; explicit DTO/versioning thêm mapping nhưng bảo vệ boundary.<br>
**Follow-up ladder:** Adding enum constant? Jackson unknown subtype? Rolling deploy? Public library SemVer?<br>
**Red flags:** Compile thành công một module được coi là bằng chứng compatible toàn fleet.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### JAVA-LANG-019 — `EXPERT`
**Question:** Type erasure và bridge method có thể tạo `ClassCastException` ở đâu trong generic inheritance, và bạn chứng minh nguyên nhân bằng bytecode/API nhỏ thế nào?<br>
**Target depth:** `D4`<br>
**Interview likelihood:** `LOW` — Java type-system/bytecode discriminator.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Compiler-generated bridge, erased override dispatch và heap-pollution diagnosis.<br>
**Answer outline:** Subclass specialization cần bridge để giữ erased polymorphic signature; unchecked/raw caller có thể đi qua bridge rồi fail ở compiler-inserted cast. Tạo minimal reproducer, bật lint, inspect `javap -c -v`, xác định nguồn unchecked write thay vì chỉ bắt exception.<br>
**Required trade-offs:** Hiểu bytecode hữu ích khi debug framework/library edge case nhưng không biện minh cho generic API quá phức tạp.<br>
**Follow-up ladder:** Synthetic/bridge flags? Covariant returns? Reflection? Binary compatibility?<br>
**Red flags:** Đổ lỗi JVM “mất type” mà không tìm unchecked boundary tạo object sai type.<br>
**Evidence:** Theory `NOT CREATED`; case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `JAVA-01` active: tạo core/deep-dive, viết micro-tests cho equality/hash/immutability/generics, review `@Data` trên actual entity và thiết kế `Money` value object có property tests. Collections/Stream/algorithm thuộc question bank kế tiếp; JPA proxy internals được nối thêm ở database/Spring stage. Stable IDs không tái sử dụng.

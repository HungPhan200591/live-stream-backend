# Java Language, Object Semantics and Generics

> Type: `CORE`<br>
> Domain: `java`<br>
> Target depth: `D3 — giải thích được type/object semantics, thiết kế value object và tái hiện lỗi equality/generics trên code thật`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Java 21 platform baseline](java21-platform-baseline.md)<br>
> Related cases: [JDK-01](../../../../cases/jdk-01-java21-platform-baseline.md), [`GIFT-UC-01`](../../../../use-case-catalog.md#gift-uc-01)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

Artifact này là source canonical cho [question bank cùng tên](../../question-bank/language-object-semantics-and-generics.md). Nó không chứng minh `JAVA-01` đã hoàn tất; learner write-back, test và project evidence vẫn chưa có.

## 1. Learning objectives

Sau topic này, tôi có thể:

1. Phân biệt value, reference, object identity, logical equality và Java pass-by-value.
2. Thiết kế immutable value object có `equals/hashCode` ổn định và boundary validation rõ.
3. Giải thích invariance, wildcard, type erasure và nhận diện heap pollution/raw-type escape.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — giải thích object/reference, equality contract và generic variance mà không nhìn notes.`

## 3. Cơ chế hoạt động

Java luôn truyền **giá trị**. Với primitive, giá trị được copy là primitive value; với reference type, giá trị được copy là reference tới object. Gán lại parameter không đổi reference của caller, nhưng mutate object qua copied reference có thể làm caller quan sát thấy state mới.

Mỗi object có identity; `==` trên reference hỏi hai reference có trỏ cùng object không. `equals` mô tả logical equality do type định nghĩa. Khi override `equals`, phải giữ reflexive, symmetric, transitive, consistent và false với `null`; object bằng nhau phải có cùng `hashCode`.

Generics chủ yếu được kiểm tra ở compile time. `List<Integer>` không phải subtype của `List<Number>` vì nếu cho phép, caller có thể thêm `Double` vào list thực chất chỉ chứa `Integer`. Wildcard diễn đạt quyền: `? extends T` phù hợp để đọc `T`, `? super T` phù hợp để ghi `T`; mnemonic PECS chỉ là hệ quả của type safety, không phải luật thay thế reasoning.

Type erasure xóa phần lớn type argument khỏi runtime representation, thêm cast/bridge method khi cần. Vì vậy không thể `new T()`, không thể tạo generic array an toàn theo cách trực tiếp và overload chỉ khác type argument sẽ đụng erasure.

Records diễn đạt data carrier với state description và generated accessors/equality; chúng không tự làm object graph sâu bên trong immutable. Sealed types giới hạn subtype set, hữu ích khi domain state/command có tập biến thể đóng.

## 4. Invariant và boundary

1. Field tham gia equality/hash phải ổn định trong suốt thời gian object nằm trong hash-based collection.
2. Value object phải canonicalize/validate tại creation boundary; không để hai representation khác nhau mang cùng business meaning mà equality lại khác.
3. Generic API không được đưa raw/unchecked value ra consumer mà không có checked boundary.
4. JPA entity identity, lifecycle/proxy và generated database ID là boundary khác value-object equality; không copy một equality strategy cho mọi entity.

## 5. Thuật ngữ và distinction

| Thuật ngữ | Định nghĩa ngắn | Dễ nhầm với | Điểm phân biệt |
| --- | --- | --- | --- |
| Identity | Object instance cụ thể | Equality | Hai object khác identity vẫn có thể logically equal |
| Immutability | Observable state không đổi sau construction | `final` reference | `final` không làm object được trỏ tới immutable |
| Invariance | `G<A>` và `G<B>` không có subtype relation chỉ vì `A <: B` | Covariance | Java dùng wildcard để mô tả variance tại use site |
| Erasure | Runtime representation xóa đa số type argument | Reification | Array reified component type; generic type argument thường không |
| Heap pollution | Variable parameterized type trỏ tới value sai type contract | Ordinary cast failure | Thường bắt đầu từ raw type, unchecked cast hoặc generic varargs |

## 6. Misconceptions

| Misconception | Vì sao sai | Counterexample/evidence |
| --- | --- | --- |
| Java truyền object by reference | Java copy reference value | Gán `param = new X()` không đổi reference của caller |
| Override `equals` là đủ | Hash collection dùng cả hash và equality | Equal objects khác hash có thể không được tìm thấy đúng bucket |
| Record luôn immutable | Component có thể trỏ mutable list/entity | Mutate component làm observable record state thay đổi |
| `? extends T` là read-only collection | Không thêm `T` được, nhưng source/captured object vẫn có thể mutate | `clear()` và external mutation vẫn có thể xảy ra |
| Generics đảm bảo runtime type safety tuyệt đối | Raw type/unchecked boundary có thể phá contract | Failure xuất hiện muộn tại compiler-inserted cast |

## 7. Failure modes kinh điển

| Failure | Trigger | Observable symptom | Root mechanism |
| --- | --- | --- | --- |
| Mutable hash key | Field trong `hashCode` đổi sau `put` | `contains/get/remove` trả sai | Object nằm ở bucket theo hash cũ |
| Asymmetric equality | Base/subclass dùng tiêu chí khác | Kết quả phụ thuộc chiều gọi | Liskov/equality contract bị phá |
| Shallow immutable DTO | Expose mutable component | Cache/audit value đổi ngoài ý muốn | Defensive copy thiếu |
| Heap pollution | Raw list/generic varargs/cast | `ClassCastException` ở xa nguồn | Compile-time guarantee bị bypass |
| Entity recursion | Lombok-generated equality/toString qua relation/proxy | Stack overflow/query ngoài ý muốn | Generated object semantics không khớp persistence lifecycle |

## 8. Solution patterns

| Pattern | Bảo vệ điều gì | Giới hạn | Khi nên dùng |
| --- | --- | --- | --- |
| Immutable value object | Equality/invariant ổn định | Cần conversion/serialization rõ | Money, StreamId, typed token kind |
| Static factory/canonical constructor | Validation/canonicalization một chỗ | Có thể tăng type count | Representation có business rule |
| Explicit equality fields | Tránh generated semantics ngoài ý muốn | Phải review lifecycle | Value/entity có identity rule rõ |
| Bounded wildcard | Type-safe reuse | Signature khó đọc nếu lạm dụng | Producer/consumer API |
| Defensive copy | Chặn external mutation | Allocation cost | Collection/array đi qua trust boundary |

## 9. Trade-off matrix

| Option | Correctness | Complexity | Performance | Security/operability | Cost/evolution |
| --- | --- | --- | --- | --- | --- |
| Primitive/String everywhere | Invariant yếu, dễ trộn meaning | Ban đầu thấp | Ít allocation | Audit khó vì semantic mơ hồ | Refactor lan rộng |
| Domain value objects | Invariant/equality rõ | Thêm type/mapping | Allocation thường chấp nhận được | Validation/redaction tập trung | Dễ đổi rule có chủ đích |
| Inheritance hierarchy mở | Dễ extension nhưng equality/exhaustiveness khó | Vừa/cao | Thường không quyết định | Khó audit variant | Binary/source evolution linh hoạt hơn |
| Sealed hierarchy | Exhaustive reasoning tốt | Tập subtype đóng | Tương đương | Review state/command rõ | Thêm variant cần sửa owner module |

## 10. Deep-dive: internals và cross-layer interaction

- [Equality, erasure, variance và mutable hash keys](../deep-dives/equality-erasure-variance-and-mutable-keys.md).
- Persistence boundary: entity/proxy/database identity không đồng nhất với domain value equality.
- Serialization boundary: constructor/field evolution có thể phá compatibility dù Java type vẫn compile.

## 11. Liên hệ learning case

| Case | Theory được áp dụng | Project detail chỉ giữ ở case |
| --- | --- | --- |
| `GIFT-UC-01` | Immutable Money, equality và canonical scale | Wallet schema, request DTO, transaction path |
| `VIEWCOUNT-UC-01` | Value/reference mutation và collection membership | Counter implementation/workload |
| `AUTHZ-UC-01` | Typed role/owner identifiers | Current endpoint/security matcher |

## 12. Self-check

1. **Question:** Java pass-by-value giải thích thế nào khi method mutate được object của caller?<br>
   **My answer:** `LEARNER TODO — viết trước khi mở notes`
2. **Question:** Vì sao mutable JPA entity hoặc mutable key trong `HashSet` dễ gây lỗi khó debug?<br>
   **My answer:** `LEARNER TODO`
3. **Question:** Thiết kế signature đọc từ subtype và ghi vào supertype bằng wildcard thế nào, và giới hạn của PECS là gì?<br>
   **My answer:** `LEARNER TODO`

## 13. Official references

- [Java Language Specification 21](https://docs.oracle.com/javase/specs/jls/se21/html/)
- [JLS 4 — Types, Values, and Variables](https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html)
- [JLS 8 — Classes](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html)
- [JLS 18 — Type Inference](https://docs.oracle.com/javase/specs/jls/se21/html/jls-18.html)
- [JEP 395 — Records](https://openjdk.org/jeps/395)

## 14. Teach-back checklist

- [ ] Tôi giải thích được Java pass-by-value bằng object/reference diagram.
- [ ] Tôi bảo vệ được equality/hash strategy cho value object và entity.
- [ ] Tôi dùng wildcard từ quyền đọc/ghi thay vì học thuộc PECS máy móc.
- [ ] Tôi tái hiện được mutable-key hoặc heap-pollution failure bằng test nhỏ.
- [ ] Tôi link được claim D3 tới test/case thật; hiện tại evidence vẫn `NOT RUN`.

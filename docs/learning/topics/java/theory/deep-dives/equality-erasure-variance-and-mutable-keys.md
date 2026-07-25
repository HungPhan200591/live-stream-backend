# Equality, Erasure, Variance and Mutable Keys

> Type: `DEEP_DIVE`<br>
> Domain: `java`<br>
> Target depth: `D3 — tái hiện contract break/heap pollution và bảo vệ API/entity/value-object boundary`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Language, Object Semantics and Generics](../core/language-object-semantics-and-generics.md)<br>
> Related cases: [`GIFT-UC-01`](../../../../use-case-catalog.md#gift-uc-01), [`VIEWCOUNT-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

Deep-dive này chỉ đào internals/pathological cases; foundation và full equality/generics vocabulary thuộc core note.

## 1. Learning objectives

1. Phân tích equality khi inheritance, proxy, generated ID và mutable state giao nhau.
2. Giải thích wildcard capture/erasure/bridge method và failure xuất hiện muộn do unchecked boundary.
3. Thiết kế reproducer chứng minh mutable hash key hoặc heap pollution thay vì chỉ mô tả.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — vẽ hash bucket trước/sau mutation và generic value đi qua raw boundary tới compiler-inserted cast.`

## 3. Internal mechanism

Hash collection chọn bucket từ hash tại operation time rồi dùng equality trong bucket. Nếu field tham gia hash đổi sau insertion, object vật lý vẫn ở bucket cũ; lookup bằng hash mới có thể không chạm tới nó. Đây là structural corruption ở mức collection usage, không phải `HashMap` mất dữ liệu ngẫu nhiên.

Equality qua inheritance dễ phá symmetry: base class chấp nhận mọi subtype theo base fields trong khi subtype thêm field và chỉ chấp nhận subtype. `getClass()` tránh một số symmetry issue nhưng có thể xung đột persistence proxy/subclass. Entity equality cần policy theo lifecycle: transient object chưa có generated ID, managed/proxy object và detached instance không thể dùng template máy móc.

Erasure biến `List<String>` và `List<Integer>` thành cùng raw runtime class; compiler chèn cast tại read site và bridge method để giữ polymorphism sau erasure. Heap pollution thường không fail tại raw `add`, mà fail ở read/cast xa nguồn. Generic varargs kết hợp reified array với erased component type, nên cần tránh write/escape và chỉ dùng `@SafeVarargs` khi implementation thực sự safe.

Wildcard capture cho phép compiler đặt tên tạm cho unknown type; helper generic method có thể thao tác an toàn mà public API vẫn dùng wildcard. Nó không biến producer thành mutable consumer.

## 4. Pathological/cross-layer cases

| Case | Causal chain | Observable consequence |
| --- | --- | --- |
| Lombok `@Data` entity | Generated equality/toString dùng mutable fields/relations | Hash membership đổi, recursion, lazy query |
| Generated ID equality | Two transient entities có `id=null` | Distinct objects bị equal hoặc hash thay sau persist |
| Proxy equality | `getClass()` strict với subclass proxy | Same DB row nhưng equality false |
| Raw deserialization | Decoder/raw map đưa wrong type | `ClassCastException` tại consumer |
| Generic varargs escape | Caller/implementation write array slot sai | Heap pollution không local |

## 5. Failure experiment implication

1. Test mutable key: insert key, mutate equality field, gọi `contains/remove`, ghi bucket-relevant symptom.
2. Test inheritance symmetry theo cả `a.equals(b)` và `b.equals(a)`, thêm transitivity set.
3. Test raw list pollution: isolate unchecked line, observe failure tại typed read.
4. Với entity/proxy, chỉ claim sau khi chạy persistence test trên actual mapping; hiện `NOT RUN`.

## 6. Design choices

| Choice | Khi phù hợp | Failure cần tránh |
| --- | --- | --- |
| Immutable business key | Key có natural stable identity | Key mutation/collision ambiguity |
| ID equality sau persistence | Aggregate có DB identity | Transient `null` semantics và hash mutation |
| No entity equality override | Reference identity đủ trong unit of work | Cross-session/detached comparison expectation |
| Composition/sealed hierarchy | Variant set/behavior rõ | Fragile equality across open inheritance |
| Checked adapter at raw boundary | Legacy/serialization interop | Unchecked value lan sâu vào core |

## 7. Interview discriminator

- Senior answer không dừng ở “override cả equals/hashCode”; phải hỏi field stability, lifecycle và collection membership.
- Architect answer phải tách value semantics, entity identity, persistence proxy và serialized compatibility.
- Expert answer phải chỉ ra nơi failure xảy ra khác nơi contract bị phá đối với erasure/heap pollution.

## 8. Liên hệ case

| Case | Deep implication | Evidence chưa có |
| --- | --- | --- |
| `GIFT-UC-01` | Money canonical scale/equality | Value-object tests |
| `VIEWCOUNT-UC-01` | Session/viewer identity used for dedup | Concurrent membership workload |
| `SESSION-UC-01` | Cache DTO typed boundary | Serialization compatibility test |

## 9. Self-check

1. **Question:** Vì sao mutable key “mất” khỏi map dù entry vẫn tồn tại?<br>**My answer:** `LEARNER TODO`
2. **Question:** Policy equality nào phù hợp cho value object và generated-ID entity, tại sao khác nhau?<br>**My answer:** `LEARNER TODO`
3. **Question:** Erasure/bridge/cast khiến heap-pollution failure xuất hiện ở đâu?<br>**My answer:** `LEARNER TODO`

## 10. Official references

- [JLS 4.6 — Type Erasure](https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html#jls-4.6)
- [JLS 4.10 — Subtyping](https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html#jls-4.10)
- [JLS 5.1.10 — Capture Conversion](https://docs.oracle.com/javase/specs/jls/se21/html/jls-5.html#jls-5.1.10)
- [Java SE 21 `Object`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Object.html)

## 11. Teach-back checklist

- [ ] Tôi tái hiện được mutable-key và heap-pollution failure.
- [ ] Tôi bảo vệ equality policy theo lifecycle.
- [ ] Tôi giải thích variance/capture từ allowed operations.
- [ ] Tôi không gắn `@SafeVarargs` khi chưa chứng minh implementation safe.
- [ ] Evidence vẫn `NOT RUN`.

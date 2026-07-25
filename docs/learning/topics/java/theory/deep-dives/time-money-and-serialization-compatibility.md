# Time, Money and Serialization Compatibility

> Type: `DEEP_DIVE`<br>
> Domain: `java`<br>
> Target depth: `D3 — tái hiện DST/rounding/mixed-version failure và thiết kế migration/reconciliation`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Exceptions, Time, Money and Serialization](../core/exceptions-time-money-and-serialization-boundaries.md)<br>
> Related cases: [`GIFT-UC-01`](../../../use-case-catalog.md#gift-uc-01), [`PAYOUT-UC-01`](../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

## 1. Learning objectives

1. Phân tích DST gap/overlap, clock drift và expiry boundary theo timeline.
2. Thiết kế Money canonicalization/rounding/allocation giữ ledger invariant.
3. Xây compatibility matrix cho old/new reader/writer và replayed cache/event payload.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — mô tả instant trên timeline, local presentation, Money unit/currency và payload version đi qua mixed deployment.`

## 3. Time internals và failure

Zone rules ánh xạ local time sang zero/one/two valid offsets: DST gap có local time không tồn tại, overlap có hai instant ứng viên. Lưu `LocalDateTime` mà thiếu zone/offset làm mất thông tin. TTL/timeout nên dựa duration/monotonic source khi đo elapsed time; wall clock có thể jump. Business schedule cần explicit zone/rule và policy khi gap/overlap.

Distributed systems không có “now” tuyệt đối đồng nhất. Timestamp không thay causal ordering/sequence/fencing. Token expiry cần bounded skew policy nhưng không mở window tùy tiện.

## 4. Money internals và failure

`BigDecimal` construction từ binary `double` có thể mang decimal representation bất ngờ; prefer decimal string/value factory phù hợp. Division/allocation có remainder nên domain phải chọn rounding và nơi hạch toán phần dư. `stripTrailingZeros` có thể đổi scale (kể cả negative scale), vì vậy canonicalization cần contract, không gọi tùy tiện trước persistence/serialization.

Ledger nên lưu immutable entries/adjustments; current balance là derived/projection hoặc atomic durable state có reconciliation. Refund/chargeback không xóa lịch sử cũ mà ghi compensating entry theo policy.

## 5. Serialization compatibility matrix

| Writer -> Reader | Risk | Required policy |
| --- | --- | --- |
| Old -> New | Missing new fields | Default/migration/validation |
| New -> Old | Unknown field/enum/type | Tolerant reader or rollout order |
| Replay old event -> New | Historic semantics | Upcaster/versioned handler |
| New cache -> Old app | Rolling rollback decode | Versioned key/DTO compatibility |
| Signed payload change | Canonical bytes differ | Explicit signature version |

## 6. Pathological cases

| Case | Symptom | Root mechanism |
| --- | --- | --- |
| DST scheduled start | Job runs twice/never | Ambiguous/nonexistent local time |
| Clock rollback | TTL/session appears valid longer | Wall clock non-monotonic |
| Divide amount | Sum of shares != original | Rounding remainder ignored |
| Scale mismatch | Equality/dedup/cache key differs | Representation not canonical |
| Enum addition | Old consumer crashes | Unknown variant not handled |
| Cache DTO rollout | Redis fallback storm | Mixed app versions cannot decode |

## 7. Experiment implication

1. Inject fixed/mutable `Clock`; test before/at/after expiry and DST gap/overlap zone.
2. Property test Money allocation: sum(parts) equals original, no forbidden scale/currency mix.
3. Golden payload compatibility: old/new reader/writer plus rollback and replay.
4. Evidence remains `NOT RUN`; sample payload is not proof until actual serializer/config is used.

## 8. Trade-off matrix

| Option | Correctness | Complexity | Operability | Evolution |
| --- | --- | --- | --- | --- |
| Local time everywhere | Ambiguous | Thấp | Incident khó correlate | Zone migration khó |
| Instant + zone at boundary | Timeline rõ | Vừa | Audit tốt | Presentation flexible |
| Raw BigDecimal | Rule phân tán | Thấp | Reconciliation khó | Scale drift |
| Money value object | Invariant tập trung | Vừa | Audit rõ | Controlled change |
| In-place DTO change | Mixed-version risk | Thấp | Rollback khó | Fast but fragile |
| Versioned DTO/key/event | Compatibility explicit | Cao hơn | Replay/rollback rõ | Durable |

## 9. Liên hệ case

| Case | Deep implication | Evidence chưa có |
| --- | --- | --- |
| `GIFT-UC-01` | Money allocation/rounding/outcome | Ledger tests |
| `PAYOUT-UC-01` | Adjustment/reconciliation | Settlement dataset |
| `SESSION-UC-01` | Expiry/skew/cache DTO | Clock/serializer tests |

## 10. Self-check

1. **Question:** DST gap/overlap ảnh hưởng scheduled livestream thế nào và policy nằm đâu?<br>**My answer:** `LEARNER TODO`
2. **Question:** Chia Money có remainder thì giữ invariant tổng ra sao?<br>**My answer:** `LEARNER TODO`
3. **Question:** Compatibility matrix nào cần pass để rolling deploy và rollback cache/event an toàn?<br>**My answer:** `LEARNER TODO`

## 11. Official references

- [Java SE 21 Date and Time API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/package-summary.html)
- [Java SE 21 `ZoneRules`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/zone/ZoneRules.html)
- [Java SE 21 `BigDecimal`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/math/BigDecimal.html)
- [Java Object Serialization Specification](https://docs.oracle.com/en/java/javase/21/docs/specs/serialization/)

## 12. Teach-back checklist

- [ ] Tôi phân biệt elapsed duration với wall-clock timestamp.
- [ ] Tôi test DST/clock skew bằng injected Clock.
- [ ] Tôi bảo vệ Money sum/currency/scale invariant.
- [ ] Tôi lập old/new reader/writer matrix trước rollout.
- [ ] Evidence vẫn `NOT RUN`.

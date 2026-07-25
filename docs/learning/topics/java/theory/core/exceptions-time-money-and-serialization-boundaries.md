# Exceptions, Time, Money and Serialization Boundaries

> Type: `CORE`<br>
> Domain: `java`<br>
> Target depth: `D3 — thiết kế boundary deterministic/compatible và tái hiện lỗi exception, clock, money hoặc serialization`<br>
> Teaching readiness: `OUTLINE_ONLY`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Object semantics and generics](language-object-semantics-and-generics.md)<br>
> Related cases: [`GIFT-UC-01`](../../../../use-case-catalog.md#gift-uc-01), [`SESSION-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

Source canonical cho [boundary question bank](../../question-bank/exceptions-time-money-and-serialization-boundaries.md). File tách Java/domain representation khỏi HTTP mapping, database isolation và security policy ở các stage sau.

## 1. Learning objectives

1. Thiết kế exception contract giữ nguyên cause, cleanup và actionable category mà không catch quá rộng.
2. Mô hình hóa instant/local time/zone/clock và viết test deterministic.
3. Bảo vệ Money/BigDecimal invariant và serialization compatibility qua version evolution.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — giải thích ba boundary: control transfer bằng exception, time mapping và representation crossing process/storage.`

## 3. Cơ chế hoạt động

Checked/unchecked là compile-time handling distinction, không phải severity. Exception nên mang semantic category và cause; catch tại layer có đủ context để recover, translate hoặc add diagnostic. Try-with-resources đóng resource theo reverse declaration order và giữ cleanup failures dưới dạng suppressed exception.

`Instant` mô tả một điểm trên UTC timeline; `LocalDateTime` không mang zone/offset nên chưa xác định một instant duy nhất; `ZonedDateTime` kết hợp local representation với zone rules. `Clock` đưa nguồn thời gian thành dependency, giúp test expiry/retry/session boundary deterministic. DST tạo gap/overlap; không cộng “24 giờ” rồi giả định luôn là cùng local time hôm sau.

`BigDecimal` lưu unscaled value và scale. `equals` xét cả scale, `compareTo` xét numerical order; division có thể cần rounding policy. Money cần currency, canonical scale/rounding và arithmetic rule, không chỉ một `BigDecimal` rời rạc.

Serialization là public compatibility boundary. Producer/consumer có thể chạy mixed version; field rename/default/enum expansion/type change phải có policy. Java native serialization không nên là default network/storage contract; DTO schema/version và explicit mapper dễ kiểm soát hơn.

## 4. Invariant và boundary

1. Không swallow exception làm caller hiểu failure thành success/absence.
2. Cleanup phải chạy và original failure/cause vẫn truy vết được.
3. Business timestamp lưu/so sánh trên timeline rõ; local display conversion ở boundary.
4. Money arithmetic không dùng binary floating point và phải giữ currency/rounding invariant.
5. Serialized contract phải đọc được dữ liệu/version nằm trong supported compatibility window.

## 5. Thuật ngữ và distinction

| Thuật ngữ | Định nghĩa | Dễ nhầm | Phân biệt |
| --- | --- | --- | --- |
| Recoverable | Caller/layer có strategy xử lý | Checked | Checked không tự động recoverable |
| Suppressed exception | Failure khi cleanup trong lúc failure khác active | Cause | Không thay original primary exception |
| Instant | Điểm trên UTC timeline | LocalDateTime | LocalDateTime thiếu zone/offset |
| Scale | Số chữ số sau decimal theo representation | Precision | Precision là tổng significant digits |
| Compatible evolution | Old/new reader/writer làm việc trong window | Source compatibility | Runtime data contract có thể hỏng dù code compile |

## 6. Misconceptions

| Misconception | Vì sao sai | Counterexample |
| --- | --- | --- |
| Catch `Exception` giúp hệ thống resilient | Che programming/security/transaction failure | Trả fallback sai khi auth/database fail |
| `LocalDateTime.now()` đủ cho mọi timestamp | Phụ thuộc system clock/zone, test khó | Hai node zone khác hoặc DST overlap |
| `BigDecimal.equals` giống numerical equality | Scale tham gia equals | `1.0` và `1.00` compareTo bằng 0 nhưng equals false |
| JSON thêm field luôn backward-compatible | Strict reader/signature/cache schema có thể vỡ | Mixed producer/consumer version |
| Enum chỉ cần thêm constant | Old consumer có thể reject unknown | Forward compatibility cần unknown policy |

## 7. Failure modes kinh điển

| Failure | Trigger | Symptom | Root mechanism |
| --- | --- | --- | --- |
| Lost cause | Throw exception mới không attach cause | Root stack trace mất | Translation boundary sai |
| Resource leak | Manual close bỏ qua exceptional path | Pool/FD exhaustion | Ownership/cleanup không lexical |
| DST expiry bug | Local arithmetic qua gap/overlap | Token/job chạy sớm/muộn | Timeline và wall clock bị trộn |
| Money drift | `double`, scale/rounding tùy chỗ | Ledger lệch/compare fail | Representation không canonical |
| Cache/message decode fail | DTO/enum/type đổi không version | Fallback storm/consumer poison | Compatibility window không thiết kế |

## 8. Solution patterns

| Pattern | Bảo vệ | Giới hạn | Khi dùng |
| --- | --- | --- | --- |
| Exception taxonomy + cause | Recovery/diagnostic | Đừng tạo class cho mọi message | Stable failure categories |
| Try-with-resources | Deterministic cleanup | Resource phải `AutoCloseable` | I/O/stream/statement |
| Injected `Clock` | Deterministic time tests | Cần wiring convention | Expiry/scheduling/session |
| Money value object | Currency/scale/equality | Mapping/API overhead | Wallet/gift/payout |
| Versioned DTO + tolerant reader | Rolling upgrade/replay | Governance needed | Cache/event/persistent payload |

## 9. Trade-off matrix

| Option | Correctness | Complexity | Performance | Security/operability | Evolution |
| --- | --- | --- | --- | --- | --- |
| Generic exception/string | Semantic yếu | Thấp | Không đáng kể | Alert/recovery khó | Contract trôi |
| Typed exception/result | Failure explicit | Vừa | Mapping overhead nhỏ | Triage tốt hơn | Stable categories |
| System time trực tiếp | Nondeterministic | Thấp | Nhanh | Debug khó | Zone dependency |
| Clock + instant policy | Deterministic | Vừa | Không đáng kể | Audit/replay rõ | Dễ đổi presentation |
| Ad-hoc JSON object | Compatibility yếu | Thấp | Có thể nhỏ | Decode incident khó | High migration cost |
| Versioned typed DTO | Schema rõ | Vừa | Mapping cost | Observable fallback | Controlled evolution |

## 10. Deep-dive

- [Time, Money and serialization compatibility failure modes](../deep-dives/time-money-and-serialization-compatibility.md).
- HTTP error response thuộc `API-01`; transaction rollback semantics thuộc `TX-01`; file này chỉ sở hữu Java/domain boundary.

## 11. Liên hệ learning case

| Case | Áp dụng | Detail giữ ở case |
| --- | --- | --- |
| `GIFT-UC-01` | Money/rounding/equality và exception outcome | Ledger/schema/idempotency |
| `SESSION-UC-01` | Instant/Clock/expiry và cached DTO version | Token/session implementation |
| `PAYOUT-UC-01` | Adjustment/rounding/audit representation | Settlement workflow |

## 12. Self-check

1. **Question:** Catch ở layer nào và khi nào translate exception mà không mất cause?<br>**My answer:** `LEARNER TODO`
2. **Question:** `Instant`, `LocalDateTime`, `OffsetDateTime`, `ZonedDateTime` dùng khác nhau thế nào?<br>**My answer:** `LEARNER TODO`
3. **Question:** Money và serialized event/cache DTO cần invariant/version policy nào?<br>**My answer:** `LEARNER TODO`

## 13. Official references

- [JLS 11 — Exceptions](https://docs.oracle.com/javase/specs/jls/se21/html/jls-11.html)
- [Java SE 21 `java.time`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/package-summary.html)
- [Java SE 21 `BigDecimal`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/math/BigDecimal.html)
- [Java Object Serialization Specification](https://docs.oracle.com/en/java/javase/21/docs/specs/serialization/)

## 14. Teach-back checklist

- [ ] Tôi phân biệt checked/unchecked với recoverable/non-recoverable.
- [ ] Tôi giữ cause/suppressed exception và resource ownership.
- [ ] Tôi giải thích time bằng timeline/zone thay vì chỉ class names.
- [ ] Tôi bảo vệ Money equality/rounding/currency contract.
- [ ] Compatibility và evidence hiện vẫn `NOT RUN`.

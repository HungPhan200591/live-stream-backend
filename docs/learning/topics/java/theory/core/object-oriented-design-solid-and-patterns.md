# Object-Oriented Design, SOLID and Patterns

> Type: `CORE`<br>
> Domain: `java`<br>
> Target depth: `D3 — refactor một boundary thật, giữ behavior bằng test và bảo vệ trade-off thay vì gọi tên pattern`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Object semantics and generics](language-object-semantics-and-generics.md)<br>
> Related cases: [`STREAM-UC-01`](../../../use-case-catalog.md#31-foundation-và-senior-cases), [`PAYOUT-UC-01`](../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

Source canonical cho [OOD question bank](../../question-bank/object-oriented-design-solid-and-patterns.md). Pattern name không phải learning evidence; design chỉ có giá trị khi invariant, coupling và change cost được làm rõ.

## 1. Learning objectives

1. Dùng encapsulation, cohesion, coupling và dependency direction để bảo vệ invariant/change boundary.
2. Áp dụng SOLID như diagnostic heuristics, không như luật tăng số interface/class.
3. Chọn composition/inheritance và pattern từ variability/failure/testability cụ thể.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — giải thích module/object nào sở hữu state transition và vì sao caller không thể tạo trạng thái bất hợp lệ.`

## 3. Cơ chế hoạt động

Encapsulation là kiểm soát state transition qua behavior/API; `private` field cộng getter/setter cho mọi thứ chưa chắc bảo vệ gì. Cohesion hỏi các responsibility có cùng lý do thay đổi không. Coupling gồm compile-time dependency, runtime coordination, shared data và temporal ordering.

SRP tập trung reason to change; OCP yêu cầu extension point ở variability đã biết, không dự đoán mọi tương lai; LSP yêu cầu subtype giữ behavioral contract; ISP tránh ép consumer phụ thuộc capability không dùng; DIP hướng high-level policy tới abstraction tại boundary cần thay thế.

Composition thường làm dependency/ownership rõ và tránh fragile base-class contract. Inheritance phù hợp khi có substitutability thật và invariant chung ổn định. Pattern là vocabulary cho recurring force: Strategy cho policy thay đổi, State cho transition, Decorator cho behavior composition, Adapter cho boundary, Factory cho creation rule. Pattern không xóa failure/cost.

## 4. Invariant và boundary

1. Một business invariant phải có owner rõ; không phân tán check ở controller/cache/consumer.
2. Abstraction nằm ở volatility/trust boundary thật, không mặc định tạo interface cho mọi class.
3. Public API của module không expose persistence/framework detail nếu consumer không cần.
4. Refactor phải giữ observable behavior bằng characterization/regression tests.

## 5. Thuật ngữ và distinction

| Thuật ngữ | Định nghĩa | Dễ nhầm | Phân biệt |
| --- | --- | --- | --- |
| Encapsulation | Bảo vệ invariant qua API/behavior | Information hiding | Hiding hỗ trợ nhưng không đủ |
| Cohesion | Responsibility cùng purpose/change reason | Small class | Class nhỏ vẫn có thể incoherent |
| Coupling | Chi phí phối hợp/thay đổi giữa components | Dependency count | Một shared DB/temporal contract có coupling cao dù ít import |
| Abstraction | Contract che detail tại boundary | Interface | Interface không tự tạo abstraction tốt |
| Pattern | Giải pháp có force/trade-off lặp lại | Template | Phải fit context, không copy structure |

## 6. Misconceptions

| Misconception | Vì sao sai | Counterexample |
| --- | --- | --- |
| SOLID nghĩa là nhiều interface | Indirection không có variability tăng cost | One implementation/pass-through interface |
| Anemic model luôn xấu | Application/domain complexity khác nhau | CRUD/read model có thể hợp lý |
| Composition luôn tốt hơn inheritance | Delegation graph cũng có cost | Stable closed hierarchy/sealed type |
| Pattern làm code senior hơn | Pattern không có problem chỉ là ceremony | Strategy cho một fixed behavior |
| Microservice giảm coupling | Network/data/operation coupling có thể tăng | Shared DB và synchronous call chain |

## 7. Failure modes kinh điển

| Failure | Trigger | Symptom | Root mechanism |
| --- | --- | --- | --- |
| God service | Nhiều policy/integration/state owner | Change/test blast radius lớn | Cohesion thấp |
| Shotgun change | Một rule nằm nhiều layer | Sót path/behavior drift | Invariant không có owner |
| Fragile inheritance | Base hook/order thay | Subclass regression | Hidden temporal contract |
| Leaky abstraction | Domain API expose JPA/cache/client | Consumer phụ thuộc implementation | Boundary đặt sai |
| Pattern ceremony | Factory/strategy/decorator không có variation | Navigation/wiring khó | Abstraction trước problem |

## 8. Solution patterns

| Pattern | Bảo vệ | Giới hạn | Khi dùng |
| --- | --- | --- | --- |
| Value object | Local invariant/equality | Mapping overhead | Money/typed identifier |
| State machine | Valid transition/exhaustiveness | Transition model phải rõ | Stream/session lifecycle |
| Strategy | Policy variability | Indirection | Retry/ranking/moderation policy |
| Adapter/anti-corruption layer | External contract isolation | Translation cost | Media/payment/IdP boundary |
| Hexagonal/module boundary | Dependency direction/testability | Có thể over-engineer CRUD | Core policy cần cô lập khỏi framework |

## 9. Trade-off matrix

| Option | Correctness | Complexity | Performance | Operability | Evolution |
| --- | --- | --- | --- | --- | --- |
| Transaction script | Rule dễ thấy khi nhỏ | Thấp | Direct | Dễ trace | Khó khi invariant phân tán |
| Rich domain model | Invariant gần state | Vừa/cao | Mapping/indirection | Cần observability xuyên layer | Tốt cho domain phức tạp |
| Modular service layer | Boundary explicit | Vừa | Predictable | Deploy đơn giản | Tốt trước khi tách service |
| Framework-centric model | Integration nhanh | Thấp ban đầu | Framework optimized | Upgrade/test coupling | Domain portability kém |

## 10. Deep-dive

Không tách deep-dive riêng trong Batch 1. Internals cụ thể được đào ở state/concurrency, Spring proxy, transaction và DDD stages; file này giữ decision vocabulary dùng lại.

## 11. Liên hệ learning case

| Case | Áp dụng | Detail giữ ở case |
| --- | --- | --- |
| `STREAM-UC-01` | State owner/valid transition | Current stream service/repository |
| `PAYOUT-UC-01` | Aggregate/value object/policy boundary | Ledger and settlement workflow |
| `MS-UC-01` | Module boundary/coupling scorecard | Extraction evidence |

## 12. Self-check

1. **Question:** Encapsulation khác `private` fields và getters/setters thế nào?<br>**My answer:** `LEARNER TODO`
2. **Question:** Khi nào thêm interface/Strategy giảm change cost, khi nào chỉ tăng ceremony?<br>**My answer:** `LEARNER TODO`
3. **Question:** Chứng minh LSP bằng behavioral contract/test thay vì định nghĩa một câu ra sao?<br>**My answer:** `LEARNER TODO`

## 13. Official references

- [JLS 8 — Classes](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html)
- [JLS 9 — Interfaces](https://docs.oracle.com/javase/specs/jls/se21/html/jls-9.html)
- [JEP 409 — Sealed Classes](https://openjdk.org/jeps/409)
- [JEP 395 — Records](https://openjdk.org/jeps/395)

## 14. Teach-back checklist

- [ ] Tôi xác định được invariant owner và reason to change.
- [ ] Tôi dùng SOLID để chỉ ra force/trade-off, không recite acronym.
- [ ] Tôi so sánh composition/inheritance trên behavioral contract.
- [ ] Tôi chỉ dùng pattern khi có variability/failure cụ thể.
- [ ] Refactor/project evidence vẫn `NOT RUN`.

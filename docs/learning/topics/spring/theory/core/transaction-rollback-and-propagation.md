# Transaction, Rollback and Propagation

> Type: `CORE`<br>
> Domain: `spring`<br>
> Target depth: `D3 — dự đoán physical transaction, tái hiện rollback/propagation anomaly và kiểm chứng crash boundary`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Spring proxies](proxies-aop-and-transactional-boundaries.md), relational transaction fundamentals<br>
> Related cases: [`GIFT-UC-01`](../../../../use-case-catalog.md#gift-uc-01), [`SPRING-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

Source canonical cho [transaction question bank](../../question-bank/transaction-rollback-and-propagation.md).

## 1. Learning objectives

1. Phân biệt logical scope do annotation mô tả với physical resource transaction/connection.
2. Giải thích rollback rule, rollback-only, propagation và isolation bằng execution sequence.
3. Thiết kế boundary ngắn, giữ invariant và không giả DB + Redis/message là một atomic transaction.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — kể sequence begin -> read/write -> flush -> commit/rollback và điều gì xảy ra nếu inner scope đánh dấu rollback-only.`

## 3. Cơ chế cốt lõi

Transaction interceptor mở hoặc tham gia transaction theo metadata, bind resource vào execution context, gọi target rồi commit hoặc rollback. Runtime exception/error thường kích hoạt rollback mặc định; checked exception cần rule tường minh nếu muốn rollback. Catch và nuốt exception có thể cho phép commit ngoài ý muốn.

`REQUIRED` tham gia existing physical transaction hoặc tạo mới. `REQUIRES_NEW` suspend outer resource và tạo independent physical transaction, vì vậy cần connection khác và có thể gây pool exhaustion. `NESTED` thường dựa trên savepoint trong cùng physical transaction và phụ thuộc transaction manager/resource support.

Inner `REQUIRED` đánh dấu rollback-only nhưng outer vẫn cố commit có thể dẫn tới `UnexpectedRollbackException`; đó là tín hiệu caller không được tin commit đã thành công. Isolation kiểm soát anomaly giữa concurrent transactions, không sửa mọi lost update nếu read-modify-write/locking/versioning sai.

## 4. Invariants và boundaries

1. Transaction bao phủ một business invariant trên resource có thể coordinate, không bao phủ remote I/O dài.
2. Side effect ngoài DB cần outbox/idempotency/reconciliation phù hợp, không claim atomicity giả.
3. Rollback policy và exception translation phải được test cho checked/runtime/constraint failures.
4. Propagation mới phải có lý do và connection-pool impact.
5. Commit success chỉ chứng minh local transaction; client timeout vẫn có thể tạo ambiguous outcome.

## 5. Failure modes

| Failure | Causal chain | Symptom |
| --- | --- | --- |
| Self-invocation | Bypass proxy | No transaction/new propagation |
| Swallowed exception | Catch without rollback | Partial business state commits |
| Rollback-only surprise | Inner joins and marks rollback | `UnexpectedRollbackException` |
| `REQUIRES_NEW` pressure | Outer holds connection, inner requests another | Pool wait/deadlock-like stall |
| Long transaction | Remote call/large work inside | Lock wait, bloat, p99 increase |
| Dual-write gap | DB commit then Redis/broker fails | Divergent state/missing event |

## 6. Trade-off matrix

| Option | Atomic scope | Cost/risk | Typical use |
| --- | --- | --- | --- |
| `REQUIRED` | Shared local transaction | Coupled rollback | One application operation |
| `REQUIRES_NEW` | Independent local commit | Extra connection, semantic split | Audit only with explicit failure policy |
| `NESTED` | Savepoint | Resource-specific | Partial rollback within one DB tx |
| Optimistic version | Conditional write | Retry/conflict | Moderate contention aggregate |
| Pessimistic lock | Serialized rows | Wait/deadlock | Short critical invariant |
| Outbox | DB state + event record atomic | Relay/duplication handling | Reliable event publication |

## 7. Deep-dive và case

- [Transaction propagation, isolation and crash windows](../deep-dives/transaction-propagation-isolation-and-crash-windows.md).
- `GIFT-UC-01`: balance/ledger invariant, duplicate command và event publication.
- `SPRING-UC-01`: proxy, rollback rule và propagation reproducer.

## 8. Self-check

1. **Question:** Logical transaction scope và physical transaction khác nhau thế nào?<br>**My answer:** `LEARNER TODO`
2. **Question:** Tạo sequence dẫn tới `UnexpectedRollbackException`.<br>**My answer:** `LEARNER TODO`
3. **Question:** Vì sao DB commit + Redis write không atomic và bạn xử lý gap ra sao?<br>**My answer:** `LEARNER TODO`

## 9. Official references

- [Spring — Transaction Management](https://docs.spring.io/spring-framework/reference/data-access/transaction.html)
- [Spring — Transaction Propagation](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-propagation.html)
- [Spring — Rolling Back a Declarative Transaction](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/rolling-back.html)

## 10. Teach-back checklist

- [ ] Tôi dự đoán connection/commit/rollback cho từng propagation.
- [ ] Tôi nêu pool và crash-window consequence.
- [ ] Tôi không claim atomicity xuyên DB/cache/broker.
- [ ] Transaction evidence vẫn `NOT RUN`.

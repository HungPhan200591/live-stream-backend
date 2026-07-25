# Proxies, AOP and Transactional Boundaries

> Type: `CORE`<br>
> Domain: `spring`<br>
> Target depth: `D3 — dự đoán advice có chạy hay không, tái hiện self-invocation và kiểm chứng transaction boundary`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [IoC and bean lifecycle](ioc-bean-lifecycle-and-dependency-injection.md)<br>
> Related cases: [`SPRING-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`GIFT-UC-01`](../../../../use-case-catalog.md#gift-uc-01)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

Source canonical cho [proxy/AOP question bank](../../question-bank/proxies-aop-and-transactional-boundaries.md). Transaction semantics chi tiết ở [transaction core](transaction-rollback-and-propagation.md).

## 1. Learning objectives

1. Phân biệt target, proxy, interceptor/advice và invocation đi qua proxy.
2. Giải thích self-invocation, method visibility, finality và proxy type ảnh hưởng behavior.
3. Đặt transaction, security, retry, cache và async boundary tại public application operation hợp lý.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — vẽ caller -> proxy chain -> target và chỉ vị trí mở/đóng transaction.`

## 3. Cơ chế cốt lõi

Spring AOP thường tạo proxy quanh bean. External caller giữ proxy; proxy chọn interceptor theo pointcut rồi gọi target. Một lời gọi `this.otherMethod()` bên trong target không quay lại proxy, nên advice gắn trên `otherMethod` có thể không chạy.

JDK dynamic proxy dựa trên interface; class-based proxy tạo subclass và không thể override behavior không proxyable như final/private method. Exact limitation phụ thuộc framework/version/configuration, vì vậy phải kiểm tra baseline đang chạy thay vì học thuộc một câu tuyệt đối.

Advice order quan trọng: transaction, authorization, retry, cache, metrics và async có thể thay đổi resource scope hoặc số lần side effect. Annotation presence không chứng minh advice được áp dụng; phải kiểm tra call path, proxy và observable effect.

## 4. Invariants và boundaries

1. Business operation cần cross-cutting concern phải được gọi qua managed proxy hoặc dùng explicit programmatic boundary.
2. Transaction không bao phủ remote system atomically; retry bên ngoài/inside transaction phải được quyết định có chủ ý.
3. Không đặt annotation trên private/internal helper rồi giả định nó tạo boundary mới.
4. Advice order phải bảo toàn authorization, idempotency và metrics semantics.

## 5. Failure modes

| Failure | Trigger | Symptom |
| --- | --- | --- |
| Self-invocation bypass | `this.method()` | Transaction/retry/cache không chạy |
| Object tự tạo bằng `new` | Ngoài container | Không có proxy/advice |
| Wrong proxy contract | Cast/type/final limitation | Startup/runtime failure hoặc no advice |
| Advice order sai | Retry + transaction/async | Transaction quá rộng, duplicate side effect |
| Exception bị nuốt | Catch/convert không đúng | Rollback rule không kích hoạt |

## 6. Patterns và trade-off

| Option | Khi dùng | Trade-off |
| --- | --- | --- |
| Tách application service | External proxy call rõ | Thêm abstraction |
| Self-injection/proxy exposure | Legacy workaround hẹp | Coupling framework, dễ recursion/confusion |
| `TransactionTemplate` | Boundary động/explicit | Infrastructure code trong flow |
| Aspect riêng | Concern tái sử dụng | Pointcut/order/testing complexity |
| Domain decorator explicit | Cần visibility/control | Wiring nhiều nhưng behavior dễ đọc |

## 7. Deep-dive và case

- [Proxy self-invocation, advice order and context](../deep-dives/proxy-self-invocation-advice-order-and-context.md).
- `SPRING-UC-01`: reproducer cho call qua proxy và self-call.
- `GIFT-UC-01`: transaction/retry/idempotency order quanh ledger operation.

## 8. Self-check

1. **Question:** Tại sao method có `@Transactional` vẫn có thể chạy không transaction?<br>**My answer:** `LEARNER TODO`
2. **Question:** Retry nằm ngoài hay trong transaction tạo khác biệt gì?<br>**My answer:** `LEARNER TODO`
3. **Question:** Bạn sẽ chứng minh advice order bằng test/evidence nào?<br>**My answer:** `LEARNER TODO`

## 9. Official references

- [Spring AOP — Proxying Mechanisms](https://docs.spring.io/spring-framework/reference/core/aop/proxying.html)
- [Spring — Declarative Transaction Management](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html)

## 10. Teach-back checklist

- [ ] Tôi vẽ đúng proxy/target/call path.
- [ ] Tôi giải thích self-invocation bằng dispatch, không bằng khẩu quyết.
- [ ] Tôi phân tích advice order và remote boundary.
- [ ] Proxy/transaction evidence vẫn `NOT RUN`.

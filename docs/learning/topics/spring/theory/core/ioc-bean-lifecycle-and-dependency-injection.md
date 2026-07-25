# IoC, Bean Lifecycle and Dependency Injection

> Type: `CORE`<br>
> Domain: `spring`<br>
> Target depth: `D3 — giải thích bean graph, tái hiện lifecycle/scope failure và chẩn đoán startup condition`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: Java object lifecycle, interfaces and exceptions<br>
> Related cases: [`SPRING-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`CREATE-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

Source canonical cho [IoC question bank](../../question-bank/ioc-bean-lifecycle-and-dependency-injection.md). Nội dung mô tả Spring container; chi tiết proxy/AOP thuộc note riêng.

## 1. Learning objectives

1. Giải thích container tạo, cấu hình, post-process và hủy bean theo dependency graph.
2. Chọn constructor injection, scope và lifecycle hook mà không giấu dependency hoặc request state.
3. Chẩn đoán ambiguous bean, cycle, premature initialization và conditional configuration.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — kể lại từ lúc ApplicationContext đọc bean definitions đến khi một singleton sẵn sàng phục vụ request.`

## 3. Cơ chế cốt lõi

IoC chuyển quyền tạo và nối object graph sang container. Bean definition mô tả type, factory, dependencies, scope và lifecycle metadata; `ApplicationContext` bổ sung event, resource, environment và các integration khác trên nền `BeanFactory`.

Container khởi tạo dependency trước dependent bean, inject dependency, gọi aware callbacks/post-processors và init callbacks. `BeanPostProcessor` có thể thay thế instance bằng proxy, nên identity/type quan sát được sau initialization có thể khác object vừa được tạo. Destruction callback áp dụng cho bean container quản lý; prototype destruction không được container theo dõi đầy đủ như singleton.

Constructor injection làm required dependency thành contract rõ và cho phép immutable reference. Setter/factory parameter phù hợp khi dependency thật sự optional hoặc construction cần kiểm soát. Field injection giấu contract và làm plain unit test khó hơn.

Singleton là một instance trên mỗi container/bean definition, không phải một instance toàn JVM. Singleton service phải tránh mutable request state. Request/session scope cần web context và thường dùng scoped proxy khi được inject vào longer-lived bean.

## 4. Invariants và boundaries

1. Mọi required dependency phải hiện trong construction contract và có đúng một candidate hoặc qualifier rõ.
2. Singleton không giữ mutable per-request/per-user state.
3. Lifecycle callback không thực hiện remote I/O không bounded làm startup treo.
4. Circular dependency là tín hiệu ownership/cohesion cần xem lại, không mặc định chữa bằng lazy/setter injection.
5. Bean presence theo profile/property phải có test startup cho cả nhánh enabled và disabled.

## 5. Failure modes

| Failure | Cơ chế | Cách điều tra |
| --- | --- | --- |
| Missing/ambiguous bean | Không có hoặc nhiều candidate | Condition report, bean definitions, qualifier |
| Circular graph | Dependency cycle tại creation | Failure chain, tách responsibility/event/port |
| Scope leak | Narrow-scoped state vào singleton sai cách | Scope/proxy inspection, concurrent request test |
| Premature creation | Post-processor/factory kéo bean lên sớm | Startup log, breakpoint và creation trace |
| Slow startup | Init callback/blocking external I/O | Startup steps, timeout và dependency availability |

## 6. Solution patterns và trade-off

| Pattern | Lợi ích | Giá phải trả |
| --- | --- | --- |
| Constructor injection | Graph rõ, test trực tiếp | Constructor dài phơi bày cohesion kém |
| Explicit `@Bean` factory | Kiểm soát third-party construction | Configuration code nhiều hơn |
| Qualifier/custom annotation | Chọn implementation tường minh | Coupling vào naming/metadata |
| Provider/scoped proxy | Trì hoãn hoặc bridge scope | Runtime indirection, lỗi muộn hơn |
| Conditional configuration | Optional capability | State-space startup tăng, cần test matrix |

## 7. Deep-dive và case

- [Bean creation, scopes, cycles and startup conditions](../deep-dives/bean-creation-scopes-cycles-and-startup-conditions.md).
- `SPRING-UC-01`: startup/bean graph và scope safety.
- `CREATE-UC-01`: service stateless, validation và dependency boundary.

## 8. Self-check

1. **Question:** Bean definition, bean instance và dependency graph khác nhau thế nào?<br>**My answer:** `LEARNER TODO`
2. **Question:** Vì sao singleton không đồng nghĩa thread-safe và scoped proxy giải quyết việc gì?<br>**My answer:** `LEARNER TODO`
3. **Question:** Bạn chẩn đoán startup failure do condition/cycle theo thứ tự nào?<br>**My answer:** `LEARNER TODO`

## 9. Official references

- [Spring — Dependencies and Configuration in Detail](https://docs.spring.io/spring-framework/reference/core/beans/dependencies.html)
- [Spring — Bean Scopes](https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html)
- [Spring — Lifecycle Callbacks](https://docs.spring.io/spring-framework/reference/core/beans/factory-nature.html)

## 10. Teach-back checklist

- [ ] Tôi kể đúng creation/lifecycle path và vai trò post-processor.
- [ ] Tôi phân biệt singleton scope với thread safety.
- [ ] Tôi giải thích cycle là design signal và có cách tái hiện.
- [ ] Startup/scope evidence vẫn `NOT RUN`.

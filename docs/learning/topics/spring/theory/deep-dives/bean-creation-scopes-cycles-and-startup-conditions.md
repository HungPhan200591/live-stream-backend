# Bean Creation, Scopes, Cycles and Startup Conditions

> Type: `DEEP_DIVE`<br>
> Domain: `spring`<br>
> Target depth: `D3 — trace bean creation, scope bridge và conditional startup failure bằng reproducer`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [IoC core](../core/ioc-bean-lifecycle-and-dependency-injection.md)<br>
> Related cases: [`SPRING-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`CONFIG-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

## 1. Deep objectives

1. Phân biệt definition registry, factory post-processing, instantiation, dependency population, initialization và destruction.
2. Giải thích vì sao early reference/cycle có thể tạo object graph khó đảm bảo và proxy mismatch.
3. Thiết kế startup/scope experiment không phụ thuộc log phỏng đoán.

## 2. Creation path và pathological cases

Bean-factory post-processors thay đổi definitions trước phần lớn instance creation; bean post-processors bao quanh instance initialization và có thể tạo proxy. Factory method hoặc post-processor gọi bean quá sớm có thể làm bean bỏ lỡ processing khác. Vì vậy “bean tồn tại” chưa chứng minh nó đã đi qua toàn bộ lifecycle mong muốn.

Constructor cycle không thể có fully constructed object cho cả hai đầu. Một số setter/field cycle từng có thể được giải bằng early reference, nhưng graph đó dễ che ownership sai và tương tác phức tạp với proxies. Tách orchestration, publish domain event hoặc đảo dependency qua port thường tạo boundary rõ hơn.

Prototype injected trực tiếp vào singleton được resolve tại lúc singleton tạo, không tự sinh instance mỗi invocation. `ObjectProvider`, lookup method hoặc scoped proxy thay đổi lookup time nhưng thêm indirection. Request/session bean ngoài active web scope sẽ thất bại nếu không có proxy/provider/context phù hợp.

## 3. Startup condition matrix

| Dimension | Nhánh cần kiểm tra |
| --- | --- |
| Property | missing/default/valid/invalid |
| Profile | dev/test/prod và combination |
| Classpath | optional dependency present/absent |
| Bean presence | user override/default auto-config |
| External dependency | unavailable/slow/misconfigured |

Condition report giải thích tại sao configuration matched hoặc không; nó không thay startup assertion. Context-runner/sliced startup test có thể kiểm tra bean presence, binding error và override behavior mà chưa khởi động toàn hạ tầng.

## 4. Reproducer design

1. Tạo graph tối thiểu cho constructor cycle và ghi exact startup failure.
2. Inject prototype/request bean vào singleton theo direct/provider/proxy variants, so sánh identity/lifecycle.
3. Chạy context matrix cho property/classpath/override; assert bean graph và failure message.
4. Đo startup step nếu init callback có I/O; thêm timeout/failure policy trước khi kết luận.

Tất cả kết quả hiện vẫn `NOT RUN`.

## 5. Cross-layer implications

- Mutable singleton state trở thành Java concurrency problem.
- Startup condition/config drift có thể mở dev/security capability.
- Lazy initialization chuyển lỗi từ startup sang request đầu tiên.
- Native/AOT/runtime upgrades có thể đổi reflection/proxy/config behavior; re-check theo baseline active.

## 6. Trade-off matrix

| Option | Failure timing | Clarity | Cost |
| --- | --- | --- | --- |
| Eager singleton | Startup | Cao | Startup time/resource |
| Lazy bean | First use | Trung bình | Runtime surprise |
| Provider lookup | Invocation | Explicit | Framework coupling |
| Scoped proxy | Invocation/context | Transparent | Hidden indirection |
| Redesign graph | Compile/startup | Cao nhất | Refactor |

## 7. Self-check

1. **Question:** Early bean reference có thể tương tác sai với proxy ra sao?<br>**My answer:** `LEARNER TODO`
2. **Question:** Prototype trong singleton thực sự được tạo khi nào?<br>**My answer:** `LEARNER TODO`
3. **Question:** Startup test matrix tối thiểu cho optional capability là gì?<br>**My answer:** `LEARNER TODO`

## 8. References

- [Spring — Container Extension Points](https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html)
- [Spring — Bean Scopes](https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html)

## 9. Teach-back checklist

- [ ] Tôi trace đúng definition-to-proxy lifecycle.
- [ ] Tôi không dùng cycle workaround như default design.
- [ ] Tôi có scope/condition reproducer plan.
- [ ] Evidence vẫn `NOT RUN`.

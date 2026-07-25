# MVC Request Pipeline, Validation and Error Handling

> Type: `CORE`<br>
> Domain: `spring`<br>
> Target depth: `D3 — lần theo request end-to-end, tái hiện validation/error branch và kiểm chứng security/async context`<br>
> Teaching readiness: `OUTLINE_ONLY`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: HTTP request/response, IoC and servlet fundamentals<br>
> Related cases: [`CREATE-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`AUTHZ-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

Source canonical cho [Spring MVC question bank](../../question-bank/mvc-request-pipeline-validation-and-error-handling.md).

## 1. Learning objectives

1. Lần theo filter/security chain, `DispatcherServlet`, handler mapping/adapter, argument resolution, validation, controller, return handler và exception resolver.
2. Phân biệt malformed request, binding failure, method validation, business invariant và authorization failure.
3. Thiết kế stable error contract, correlation và test cho success/failure/async dispatch.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — vẽ request từ socket/container đến ApiResponse, đánh dấu nơi authn, authz, binding, validation và exception mapping xảy ra.`

## 3. Cơ chế cốt lõi

Servlet filters chạy quanh dispatch; Spring Security thường nằm trong filter chain trước controller. `DispatcherServlet` tìm handler qua `HandlerMapping`, gọi bằng `HandlerAdapter`, resolve arguments, convert/bind/validate input, rồi dùng return-value handler và message converter để tạo response.

Exception có thể được xử lý bởi resolver chain, gồm `@ExceptionHandler`/`@ControllerAdvice`, status mappings và default framework handling. Error contract phải phân biệt client syntax/shape, field validation, business conflict, authentication/authorization và server failure mà không rò chi tiết nhạy cảm.

Bean validation bảo vệ shape/constraint tại boundary; domain/service vẫn phải bảo vệ invariant phụ thuộc current state hoặc concurrency. Async dispatch có lifecycle và context propagation riêng; thread-local/security/trace context không được mặc định tồn tại sau handoff.

## 4. Invariants và boundaries

1. Controller mỏng: boundary validation/authorization, orchestration call và DTO response.
2. Không trả entity trực tiếp; serialization contract không phụ thuộc persistence graph.
3. Một failure class map ổn định sang HTTP status/error code; không catch rộng biến mọi lỗi thành `200` hoặc `500` mơ hồ.
4. Ownership/authorization được kiểm tra tại layer có đủ context và có regression test.
5. Logging có correlation nhưng không ghi token/password/payload nhạy cảm đầy đủ.

## 5. Failure taxonomy

| Failure | Layer điển hình | Kết quả mong đợi |
| --- | --- | --- |
| Malformed JSON/type | Message conversion | `400`, safe detail |
| Constraint violation | Argument/method validation | `400`, field/global violations |
| Unauthorized/forbidden | Security filter/method | `401`/`403` đúng semantics |
| Missing resource | Application service | `404` stable code |
| State conflict | Domain/DB invariant | `409` hoặc semantics đã document |
| Unexpected bug | Resolver cuối | `500`, correlation, no leakage |

## 6. Patterns và trade-off

| Pattern | Lợi ích | Rủi ro |
| --- | --- | --- |
| Global exception mapping | Contract nhất quán | Mapping quá rộng che bug |
| DTO + validation groups | Boundary rõ | Group complexity/coupling |
| Service invariant check + DB constraint | Defense in depth | Cần map race/constraint error |
| Problem-details-like envelope | Machine-readable | Migration/compatibility cost |
| Async MVC | Giải phóng request thread | Context, timeout, cancellation phức tạp |

## 7. Deep-dive và case

- [MVC security, validation, async and error pipeline](../deep-dives/mvc-security-validation-async-and-error-pipeline.md).
- `CREATE-UC-01`: request DTO, invariant và error mapping.
- `AUTHZ-UC-01`: URL rule, method rule và ownership failure.

## 8. Self-check

1. **Question:** Filter, interceptor, argument resolver và exception resolver chạy ở đâu?<br>**My answer:** `LEARNER TODO`
2. **Question:** Validation annotation và business invariant khác nhau thế nào?<br>**My answer:** `LEARNER TODO`
3. **Question:** Test matrix tối thiểu cho error contract và authorization là gì?<br>**My answer:** `LEARNER TODO`

## 9. Official references

- [Spring MVC — Processing](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet/sequence.html)
- [Spring MVC — Validation](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-validation.html)
- [Spring MVC — Exceptions](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet/exceptionhandlers.html)

## 10. Teach-back checklist

- [ ] Tôi lần được request và exception path end-to-end.
- [ ] Tôi tách input validation khỏi state invariant.
- [ ] Tôi nêu async/context/security boundary.
- [ ] MVC/error evidence vẫn `NOT RUN`.

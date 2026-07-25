# Spring Interview Question Bank — MVC Request Pipeline, Validation and Error Handling

> Status: `DRAFT`<br>
> Domain owner: `Spring MVC`<br>
> Active slice: `NONE`; preview target: `SPR-01`<br>
> Related roadmap: [Stage 2](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-2---spring-internals-http-api-và-transaction-semantics)<br>
> Related depth rubric: [Spring](../../../knowledge-depth-rubric.md#35-spring-framework-và-spring-boot--p0-target-d3)<br>
> Related theory: [MVC Request Pipeline, Validation and Error Handling](../theory/core/mvc-request-pipeline-validation-and-error-handling.md), [MVC pipeline deep-dive](../theory/deep-dives/mvc-security-validation-async-and-error-pipeline.md) — `DRAFT`, evidence `NOT RUN`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `SPR-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `SPR-MVC-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### SPR-MVC-001 — `FOUNDATION`
**Question:** Một request đi qua servlet container, filter, `DispatcherServlet` và controller theo thứ tự nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Phân biệt container, security/filter chain và MVC dispatch.<br>
**Answer outline:** Connector nhận request; filters chạy quanh servlet; DispatcherServlet chọn handler/adapters; interceptors, argument resolution, controller, return handlers và exception resolvers xử lý tiếp.<br>
**Required trade-offs:** Mỗi extension point mạnh nhưng sai layer làm control flow khó hiểu.<br>
**Follow-up ladder:** Security filter ở đâu? Async dispatch chạy lại filter không?<br>
**Red flags:** Request đi thẳng vào controller.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-MVC-002 — `FOUNDATION`
**Question:** Filter, HandlerInterceptor và AOP khác nhau; chọn khi nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Boundary HTTP thô, MVC handler và method proxy.<br>
**Answer outline:** Filter cho request/response/container-wide concern; interceptor biết handler/MVC lifecycle; AOP cho bean method cross-cutting không phụ thuộc HTTP.<br>
**Required trade-offs:** Đặt concern quá thấp thiếu context, quá cao bỏ sót path.<br>
**Follow-up ladder:** OncePerRequestFilter? Response body advice?<br>
**Red flags:** Ba cơ chế thay thế nhau hoàn toàn.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-MVC-003 — `FOUNDATION`
**Question:** `@RequestParam`, `@PathVariable`, `@RequestBody` và message converter hoạt động ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Argument resolver và serialization boundary.<br>
**Answer outline:** Resolver lấy path/query/header; converter đọc media type/body thành DTO và ghi response; validation chạy trên resolved argument khi được kích hoạt.<br>
**Required trade-offs:** DTO explicit an toàn hơn binding trực tiếp entity nhưng thêm mapping.<br>
**Follow-up ladder:** 415 vs 400? Content negotiation?<br>
**Red flags:** Jackson tự bind an toàn mọi field của entity.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-MVC-004 — `FOUNDATION`
**Question:** Bean Validation ở controller và service khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Shape validation vs business invariant.<br>
**Answer outline:** Controller validate syntax/shape; service bảo vệ invariant/authorization dưới mọi caller; method validation cần proxy/config; DB constraint là safety net.<br>
**Required trade-offs:** Validation lặp có chủ đích ở trust boundaries nhưng tránh copy rule mâu thuẫn.<br>
**Follow-up ladder:** Validation groups? Cross-field rule?<br>
**Red flags:** `@Valid` thay thế service rule.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-MVC-005 — `SENIOR`
**Question:** Thiết kế `@ControllerAdvice`/exception mapping ổn định thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Typed exception, status/error code và leakage.<br>
**Answer outline:** Map domain/application exceptions sang stable error envelope; validation fields rõ, correlation ID, không trả stack/SQL/secret; unknown exception thành 500 và log protected.<br>
**Required trade-offs:** Chi tiết giúp client nhưng tăng coupling/security exposure.<br>
**Follow-up ladder:** Problem Details? Handler ordering?<br>
**Red flags:** Catch `Exception` trong từng controller và trả 200.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-MVC-006 — `SENIOR`
**Question:** Tại sao đọc request/response body trong filter có thể làm controller nhận body rỗng hoặc tăng memory?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** One-shot stream, wrappers và buffering.<br>
**Answer outline:** Input stream thường đọc một lần; logging cần caching wrapper đúng lifecycle và giới hạn size/redaction; streaming response không nên buffer mù quáng.<br>
**Required trade-offs:** Observability đầy đủ vs memory/PII/latency.<br>
**Follow-up ladder:** Async/large upload? Compression?<br>
**Red flags:** Gọi `getInputStream()` để log không ảnh hưởng downstream.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-MVC-007 — `SENIOR`
**Question:** Async MVC, error dispatch và filter execution tạo duplicate logging/context thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Dispatcher types và thread/context propagation.<br>
**Answer outline:** Request có thể redispatch ASYNC/ERROR; filter cần once semantics theo dispatcher, correlation context phải propagate/clear, completion đo ở đúng callback.<br>
**Required trade-offs:** Một log/request đơn giản nhưng async lifecycle cần state.<br>
**Follow-up ladder:** MDC leak? DeferredResult timeout?<br>
**Red flags:** Filter chắc chắn chỉ chạy một lần.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-MVC-008 — `SENIOR`
**Question:** Test toàn request pipeline thay vì chỉ gọi controller method thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** MockMvc/full context và negative paths.<br>
**Answer outline:** Dùng MockMvc/security context để assert filter/auth/validation/converter/advice/status/body; integration test thật cho container-specific behavior; unit test business service riêng.<br>
**Required trade-offs:** Full context confidence cao nhưng chậm; slice cần import wiring đúng.<br>
**Follow-up ladder:** Standalone MockMvc thiếu gì?<br>
**Red flags:** Unit test controller chứng minh security chain.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-MVC-009 — `ARCHITECT`
**Question:** Chuẩn hóa API pipeline nhiều module mà không tạo global magic thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Ownership của security, validation, errors và observability.<br>
**Answer outline:** Platform cung cấp bounded conventions/error contract/filter order; modules sở hữu handlers/rules; architecture/contract tests và explicit extension points/versioning.<br>
**Required trade-offs:** Global consistency vs local autonomy/upgrade blast radius.<br>
**Follow-up ladder:** Starter nội bộ? Multi-tenancy context?<br>
**Red flags:** Một mega ControllerAdvice biết mọi domain.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SPR-MVC-010 — `EXPERT`
**Question:** Diagnose response đã commit rồi exception handler cố đổi status như thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Buffer/stream/flush boundary và async failure.<br>
**Answer outline:** Reconstruct timeline headers/body flush, converter/streaming callback và proxy error; nếu committed chỉ log/abort connection, thiết kế prevalidation hoặc protocol-level error cho stream.<br>
**Required trade-offs:** Streaming giảm memory/latency nhưng không thể đổi HTTP status sau commit.<br>
**Follow-up ladder:** SSE/WebSocket khác gì? Client disconnect?<br>
**Red flags:** Global handler luôn sửa được response.<br>
**Evidence:** Theory `NOT CREATED`; case `SPR-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `SPR-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.

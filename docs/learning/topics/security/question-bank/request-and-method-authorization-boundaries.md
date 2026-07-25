# Security Interview Question Bank — Request and Method Authorization Boundaries

> Status: `DRAFT`<br>
> Domain owner: `security / Spring Security / authorization`<br>
> Active slice: `NONE`; preview target `SEC-06 — public/private matcher and method authorization boundary`<br>
> Related roadmap: [Stage 0 and SEC-06](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-0---stabilize-the-laboratory)<br>
> Related depth rubric: [Security and identity](../../../knowledge-depth-rubric.md#38-security-và-identity--p0-target-d3), [Spring Boot](../../../knowledge-depth-rubric.md#35-spring-framework-và-spring-boot--p0-target-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/security/theory/core/request-and-method-authorization.md`<br>
> Version snapshot checked: `2026-07-25`<br>
> Updated: `2026-07-25`

Question bank này chuẩn bị trước cho `SEC-06`; không sửa `SecurityConfig`, không active case và không chứng minh endpoint đã được bảo vệ. Mọi evidence giữ `NOT RUN`/`NOT CREATED`.

## Official sources

- [Spring Security 6.5 — Authorize HTTP Requests](https://docs.spring.io/spring-security/reference/6.5/servlet/authorization/authorize-http-requests.html)
- [Spring Security — Method Security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html)
- [Spring Security — Java Configuration and Multiple Filter Chains](https://docs.spring.io/spring-security/reference/servlet/configuration/java.html)

## Coverage

| Topic | Foundation | Senior | Architect | Expert | Theory source |
| --- | ---: | ---: | ---: | ---: | --- |
| Chain selection và request matching | 2 | 1 | 1 | 1 | `PLANNED` |
| URL, method, ownership và denial semantics | 1 | 2 | 1 | 1 | `PLANNED` |
| Verification và policy governance | 0 | 1 | 0 | 0 | `PLANNED` |
| **Tổng** | **3** | **4** | **2** | **2** | 11 questions |

## Questions

### SEC-AUTHZ-001 — `FOUNDATION`

**Question:** Phân biệt `securityMatcher`, `requestMatchers` và `anyRequest`. Chúng quyết định chain nào chạy và rule nào áp dụng ra sao?

**Target depth:** `D1-D2`

**Interviewer evaluates:** Có tách chain selection khỏi authorization bên trong chain.

**Answer outline:**

1. `securityMatcher` chọn `SecurityFilterChain`; first matching chain xử lý request.
2. `requestMatchers` chọn authorization rule trong chain; rules order-specific và `anyRequest` là fallback.
3. Không chain nào match nghĩa là request không được Spring Security bảo vệ.

**Required trade-offs:** Nhiều chain cô lập concern nhưng tăng ordering/gap risk.

**Follow-up ladder:** Foundation: matcher match path nào? Senior: first-match gây shadowing ra sao? Architect: bao nhiêu chain là hợp lý? Expert: chứng minh toàn bộ request space được cover thế nào?

**Red flags:** Đồng nhất `securityMatcher` với `permitAll`; giả định default chain luôn tồn tại.

**Evidence:** Theory: `NOT CREATED`; Deep-dive: `NOT CREATED`; Learning case: `SEC-06 NOT CREATED`; Tests/experiment: `NOT RUN`; Interview note: `NOT CREATED`.

**Self-assessment:** `UNANSWERED`

### SEC-AUTHZ-002 — `FOUNDATION`

**Question:** `permitAll`, `authenticated`, role/authority và ownership check bảo vệ các invariant khác nhau thế nào?

**Target depth:** `D1-D2`

**Interviewer evaluates:** Có phân biệt authentication, coarse authorization và object-level authorization.

**Answer outline:**

1. `permitAll` không yêu cầu principal; `authenticated` chỉ cần principal hợp lệ.
2. Role/authority kiểm tra capability rộng; ownership kiểm tra quan hệ principal-resource.
3. Business state invariant vẫn thuộc service/domain, không được thay bằng role check.

**Required trade-offs:** URL rules dễ audit diện rộng; method/service rules gần domain data hơn.

**Follow-up ladder:** Foundation: role khác authority gì? Senior: admin có bypass ownership không? Architect: policy owner ở đâu? Expert: ABAC thay model thế nào?

**Red flags:** “Authenticated thì được truy cập mọi resource”; controller tự tin vào user ID từ request.

**Evidence:** Theory: `NOT CREATED`; Deep-dive: `NOT CREATED`; Learning case: `SEC-06 NOT CREATED`; Tests/experiment: `NOT RUN`; Interview note: `NOT CREATED`.

**Self-assessment:** `UNANSWERED`

### SEC-AUTHZ-003 — `FOUNDATION`

**Question:** Vì sao `/api/auth/**.permitAll()` là broad trust boundary nguy hiểm khi group chứa login, refresh, `/me`, logout và logout-all?

**Target depth:** `D1-D2`

**Interviewer evaluates:** Có reason theo method/path contract thay vì controller name.

**Answer outline:**

1. Prefix matcher công khai mọi endpoint hiện tại/tương lai dưới namespace.
2. Chỉ register/login/refresh có public proof riêng; identity/logout endpoints cần authentication hoặc explicit credential proof.
3. Dùng exact method+path allowlist và authenticated fallback; thêm negative tests.

**Required trade-offs:** Exact allowlist verbose hơn nhưng fail-closed khi endpoint mới xuất hiện.

**Follow-up ladder:** Foundation: refresh public nghĩa là không validate gì? Senior: logout dùng access hay refresh proof? Architect: review API-policy drift thế nào? Expert: generated routes phá allowlist ra sao?

**Red flags:** “Auth controller thì phải public”; dựa duy nhất vào method annotation chưa bật.

**Evidence:** Theory: `NOT CREATED`; Deep-dive: `NOT CREATED`; Learning case: `SEC-06 NOT CREATED`; Tests/experiment: `NOT RUN`; Interview note: `NOT CREATED`.

**Self-assessment:** `UNANSWERED`

### SEC-AUTHZ-004 — `SENIOR`

**Question:** Thiết kế matcher tránh method confusion, path-variable overmatch, trailing slash và rule shadowing như thế nào?

**Target depth:** `D2-D3`

**Interviewer evaluates:** Có test actual routing semantics và rule order.

**Answer outline:**

1. Match HTTP method + canonical path; specific rules trước broad rules, authenticated deny/fallback cuối.
2. Đồng bộ MVC routing, context path/proxy normalization và security matcher semantics.
3. Test near-miss paths, alternate methods, encoded segments, trailing slash và new endpoints.

**Required trade-offs:** Matcher generic giảm config nhưng tăng accidental exposure; exact matcher cần maintenance automation.

**Follow-up ladder:** Foundation: GET-public có làm POST-public không? Senior: encoded slash xử lý ở layer nào? Architect: contract-test routes ra sao? Expert: normalization mismatch thành bypass thế nào?

**Red flags:** Chỉ test happy path; broad wildcard đặt trước admin rule.

**Evidence:** Theory: `NOT CREATED`; Deep-dive: `NOT CREATED`; Learning case: `SEC-06 NOT CREATED`; Tests/experiment: `NOT RUN`; Interview note: `NOT CREATED`.

**Self-assessment:** `UNANSWERED`

### SEC-AUTHZ-005 — `SENIOR`

**Question:** Khi nào đặt rule ở URL layer, khi nào dùng `@PreAuthorize` tại service, và vì sao cần defense-in-depth thay vì duplicate mù?

**Target depth:** `D2-D3`

**Interviewer evaluates:** Có chọn boundary theo available context và alternate invocation paths.

**Answer outline:**

1. URL layer bảo vệ coarse public/authenticated/role surface trước controller.
2. Method security bảo vệ service invocation dựa trên parameters/domain ownership, kể cả non-HTTP caller.
3. Shared policy component tránh logic drift; test mỗi boundary và direct service invocation.

**Required trade-offs:** Hai lớp tăng assurance nhưng có thể drift/error semantics khác nhau nếu copy expression.

**Follow-up ladder:** Foundation: method security tự bật không? Senior: self-invocation có bypass proxy không? Architect: policy-as-code ở đâu? Expert: transaction/advice ordering ảnh hưởng gì?

**Red flags:** Chỉ annotate controller; tin annotation dù `@EnableMethodSecurity` chưa bật.

**Evidence:** Theory: `NOT CREATED`; Deep-dive: `NOT CREATED`; Learning case: `SEC-06 NOT CREATED`; Tests/experiment: `NOT RUN`; Interview note: `NOT CREATED`.

**Self-assessment:** `UNANSWERED`

### SEC-AUTHZ-006 — `SENIOR`

**Question:** Method security dựa trên Spring AOP tạo những failure mode nào: self-invocation, non-managed object, private/final method, parameter-name/SpEL và annotation conflict?

**Target depth:** `D2-D3`

**Interviewer evaluates:** Có hiểu proxy execution boundary.

**Answer outline:**

1. Authorization interceptor chỉ chạy khi invocation đi qua managed proxy/advisor phù hợp.
2. Internal call hoặc object tự tạo có thể không qua proxy; expression sai/parameter discovery gây denial hoặc bypass theo config.
3. Đặt protected operation trên public service boundary, dùng typed policy bean/custom annotation và direct negative tests.

**Required trade-offs:** Annotation expressive nhưng policy phân tán; centralized manager dễ audit hơn nhưng có coupling.

**Follow-up ladder:** Foundation: `@PreAuthorize` chạy trước method? Senior: self-invocation test thế nào? Architect: custom annotation giúp gì? Expert: AOT/proxy mode ảnh hưởng gì?

**Red flags:** Gắn annotation lên private helper rồi coi đã bảo vệ; không test direct bean call.

**Evidence:** Theory: `NOT CREATED`; Deep-dive: `NOT CREATED`; Learning case: `SEC-06 NOT CREATED`; Tests/experiment: `NOT RUN`; Interview note: `NOT CREATED`.

**Self-assessment:** `UNANSWERED`

### SEC-AUTHZ-007 — `SENIOR`

**Question:** Xây authorization regression matrix nào cho public, anonymous, invalid token, authenticated, wrong role, non-owner và owner?

**Target depth:** `D2-D3`

**Interviewer evaluates:** Có negative HTTP + method tests và status/side-effect assertion.

**Answer outline:**

1. Inventory method/path/expected rule từ API contract; test anonymous, invalid credential, wrong/right authority và ownership.
2. Assert `401` authentication vs `403` authorization, response contract và không có side effect.
3. Test filter chain thật bằng MockMvc/integration và direct service method security; detect unmapped/new routes.

**Required trade-offs:** Full matrix lớn; prioritize risk nhưng mọi public surface và deny fallback phải covered.

**Follow-up ladder:** Foundation: 401/403 khác gì? Senior: mock JWT service che lỗi nào? Architect: generated policy matrix? Expert: mutation test authorization hữu ích ra sao?

**Red flags:** Chỉ happy-path owner/admin; controller test bypass security filters.

**Evidence:** Theory: `NOT CREATED`; Deep-dive: `NOT CREATED`; Learning case: `SEC-06 NOT CREATED`; Tests/experiment: `NOT RUN`; Interview note: `NOT CREATED`.

**Self-assessment:** `UNANSWERED`

### SEC-AUTHZ-008 — `ARCHITECT`

**Question:** Thiết kế authorization policy ownership để API contract, SecurityFilterChain, method annotations và gateway không drift khi hệ thống lớn lên.

**Target depth:** `D3-D4`

**Interviewer evaluates:** Có canonical policy, review/evidence và change governance.

**Answer outline:**

1. Contract xác định public/auth/role/ownership intent; code enforcement có owner và traceability.
2. CI inventory routes và test matrix so với contract; security review cho public-surface change.
3. Gateway là outer defense, service vẫn enforce resource/domain policy; policy decision/log không lộ sensitive data.

**Required trade-offs:** Central policy tăng consistency nhưng có latency/coupling; local policy gần domain nhưng dễ drift.

**Follow-up ladder:** Foundation: gateway auth đủ không? Senior: endpoint mới fail-open hay closed? Architect: policy version rollout? Expert: partial policy deployment xử lý thế nào?

**Red flags:** Spreadsheet không test; gateway là lớp authorization duy nhất.

**Evidence:** Theory: `NOT CREATED`; Deep-dive: `NOT CREATED`; Learning case: `SEC-06 NOT CREATED`; Tests/experiment: `NOT RUN`; Interview note: `NOT CREATED`.

**Self-assessment:** `UNANSWERED`

### SEC-AUTHZ-009 — `ARCHITECT`

**Question:** Nhiều `SecurityFilterChain` cho API, Actuator và static/public surface nên được order và fail-safe thế nào?

**Target depth:** `D3-D4`

**Interviewer evaluates:** Có chain coverage, first-match và management-plane isolation.

**Answer outline:**

1. Specific chains có explicit order/securityMatcher; cuối cùng có catch-all authenticated/deny policy.
2. Actuator/management plane dùng network + authentication/role riêng; public static chain scope hẹp.
3. Test chain attribution, overlap, no-match và startup configuration across profiles.

**Required trade-offs:** Separate chains giảm cross-concern complexity nhưng shadowing/no-match risk tăng.

**Follow-up ladder:** Foundation: first matching chain nghĩa gì? Senior: chain không match hậu quả gì? Architect: catch-all deny hay authenticated? Expert: dynamic tenant chains scale ra sao?

**Red flags:** Không catch-all; order dựa vào tình cờ bean discovery.

**Evidence:** Theory: `NOT CREATED`; Deep-dive: `NOT CREATED`; Learning case: `SEC-06 NOT CREATED`; Tests/experiment: `NOT RUN`; Interview note: `NOT CREATED`.

**Self-assessment:** `UNANSWERED`

### SEC-AUTHZ-010 — `EXPERT`

**Question:** Reverse proxy và servlet container normalize path khác Spring MVC/Security matcher, tạo authorization bypass thế nào? Thiết kế canonicalization invariant ra sao?

**Target depth:** `D4`

**Interviewer evaluates:** Có cross-layer request-target reasoning.

**Answer outline:**

1. Encoded slash/dot segment, duplicate slash, context path, forwarded prefix và matrix parameter có thể được layers decode/normalize khác nhau.
2. Invariant: routing và authorization quyết định trên cùng canonical request identity; reject ambiguous forms sớm.
3. Pin proxy/container settings, trust forwarded headers từ proxy allowlist và fuzz near-equivalent paths end-to-end.

**Required trade-offs:** Strict rejection có thể phá legacy clients; permissive normalization mở ambiguity/bypass.

**Follow-up ladder:** Foundation: URL decoding xảy ra đâu? Senior: proxy test nào bắt drift? Architect: canonicalization owner? Expert: cache key và auth path mismatch gây gì?

**Red flags:** Chỉ unit-test matcher với clean path; tin mọi forwarded header.

**Evidence:** Theory: `NOT CREATED`; Deep-dive: `NOT CREATED`; Learning case: `SEC-06 NOT CREATED`; Tests/experiment: `NOT RUN`; Interview note: `NOT CREATED`.

**Self-assessment:** `UNANSWERED`

### SEC-AUTHZ-011 — `EXPERT`

**Question:** Authorization check đọc ownership trước transaction, resource đổi owner/state trước write. Phát biểu invariant và đóng TOCTOU window thế nào?

**Target depth:** `D4`

**Interviewer evaluates:** Có nối authorization với transaction/concurrency, không coi pre-check là atomic.

**Answer outline:**

1. Pre-check và mutation tách transaction tạo time-of-check/time-of-use race.
2. Enforce ownership/state trong cùng transaction bằng conditional DML/lock/version và verify affected rows.
3. Method/URL auth chặn coarse caller; data-layer atomic predicate giữ invariant dưới concurrency.
4. Deterministic race test owner/state change giữa check và write.

**Required trade-offs:** Lock mạnh đơn giản correctness nhưng tăng contention; optimistic conditional update cần conflict contract.

**Follow-up ladder:** Foundation: ownership check là business rule? Senior: conditional SQL mẫu? Architect: policy-data locality? Expert: remote PDP stale decision xử lý sao?

**Red flags:** `findById` rồi `if owner` rồi update không concurrency control; tin method annotation giải quyết race.

**Evidence:** Theory: `NOT CREATED`; Deep-dive: `NOT CREATED`; Learning case: `SEC-06 NOT CREATED`; Tests/experiment: `NOT RUN`; Interview note: `NOT CREATED`.

**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `SEC-06` active: tạo theory/deep-dive, nối actual route/matcher/method inventory, tạo negative MockMvc + direct-service tests và thay `NOT CREATED` bằng links thật. Giữ scope matcher/method boundary; token purpose thuộc `SEC-01`, stale-session invalidation thuộc `SEC-02`. Stable IDs không được tái sử dụng.

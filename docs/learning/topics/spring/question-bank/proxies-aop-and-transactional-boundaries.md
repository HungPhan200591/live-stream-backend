# Spring Interview Question Bank — Proxies, AOP and Transactional Boundaries

> Status: `DRAFT`  
> Domain owner: `Spring Framework`  
> Active slice: `NONE`; preview target: `SPR-02`  
> Related roadmap: [Stage 2](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-2---spring-internals-http-api-và-transaction-semantics)  
> Related depth rubric: [Spring](../../../knowledge-depth-rubric.md#35-spring-framework-và-spring-boot--p0-target-d3)  
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/spring/theory/core/proxies-aop-and-transactional-boundaries.md`  
> Updated: `2026-07-26`

Preview only; không active/implement `SPR-02`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `SPR-PROXY-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### SPR-PROXY-001 — `FOUNDATION`
**Question:** Spring AOP giải quyết bài toán gì; join point, pointcut và advice là gì?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Phân biệt cross-cutting concern với business logic.  
**Answer outline:** AOP gom logging, security, transaction... quanh method execution; pointcut chọn join point, advice định nghĩa hành vi before/after/around. Spring AOP chủ yếu proxy method của bean.  
**Required trade-offs:** Giảm lặp nhưng làm control flow khó thấy.  
**Follow-up ladder:** AspectJ khác gì? Around advice có rủi ro gì?  
**Red flags:** AOP là cách thay thế mọi decorator/business service.  
**Evidence:** Theory `NOT CREATED`; case `SPR-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SPR-PROXY-002 — `FOUNDATION`
**Question:** JDK dynamic proxy và CGLIB/class-based proxy khác nhau thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Interface proxy, subclass proxy và giới hạn method.  
**Answer outline:** JDK proxy triển khai interface; class proxy tạo subclass. Class/final method không thể bị override theo cách proxy thông thường; caller phải đi qua proxy.  
**Required trade-offs:** Interface rõ contract; class proxy tiện nhưng gắn với inheritance/proxy limitations.  
**Follow-up ladder:** Spring chọn loại nào? `proxyTargetClass`?  
**Red flags:** CGLIB sao chép business object thành object độc lập.  
**Evidence:** Theory `NOT CREATED`; case `SPR-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SPR-PROXY-003 — `FOUNDATION`
**Question:** Vì sao `@Transactional` cần lời gọi đi qua Spring proxy?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Interceptor mở/commit/rollback transaction quanh method.  
**Answer outline:** Annotation chỉ là metadata; transaction interceptor trên proxy đọc metadata, bind resource vào thread, gọi target rồi commit/rollback. Gọi thẳng target không kích hoạt interceptor.  
**Required trade-offs:** Declarative transaction gọn nhưng boundary không hiện rõ tại call site.  
**Follow-up ladder:** TransactionSynchronizationManager? Reactive transaction?  
**Red flags:** JVM tự hiểu `@Transactional`.  
**Evidence:** Theory `NOT CREATED`; case `SPR-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SPR-PROXY-004 — `FOUNDATION`
**Question:** Method `private`, `final`, `static` hoặc object tự tạo bằng `new` ảnh hưởng advice thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Proxy interception boundary.  
**Answer outline:** Object phải do container quản lý và call qua proxy; private/static không phải overridable proxy join point, final chặn class proxy override. Kiểm tra cụ thể theo proxy mode/version.  
**Required trade-offs:** Mở method chỉ để proxy làm yếu encapsulation; nên đặt boundary ở public service contract.  
**Follow-up ladder:** Interface default method? AspectJ weaving?  
**Red flags:** Gắn annotation ở đâu cũng chạy.  
**Evidence:** Theory `NOT CREATED`; case `SPR-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SPR-PROXY-005 — `SENIOR`
**Question:** Self-invocation làm mất transaction/advice thế nào và sửa ra sao?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Nhận diện `this.inner()` bypass proxy.  
**Answer outline:** External call vào proxy được intercept, nhưng method cùng object gọi nhau qua `this`; tách collaborator có boundary rõ là ưu tiên. Self-injection/AopContext chỉ là escape hatch khó bảo trì.  
**Required trade-offs:** Tách service thêm abstraction nhưng làm transaction contract quan sát được.  
**Follow-up ladder:** Test nào chứng minh? `REQUIRES_NEW` có chạy không?  
**Red flags:** Thêm annotation thứ hai là đủ.  
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SPR-PROXY-006 — `SENIOR`
**Question:** Nhiều advice như transaction, security, retry và metrics được order ra sao; order sai gây gì?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Nested interceptor chain và semantic boundary.  
**Answer outline:** Mỗi interceptor bọc phần còn lại; order quyết định retry có tạo transaction mới, metric đo phần nào, authorization chạy trước resource access. Xác minh bằng order config và integration test, không đoán.  
**Required trade-offs:** Central advice nhất quán nhưng composition tạo emergent behavior.  
**Follow-up ladder:** Retry ngoài hay trong transaction? Exception translation ở đâu?  
**Red flags:** Order không quan trọng vì annotations độc lập.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SPR-PROXY-007 — `SENIOR`
**Question:** Chẩn đoán một `@Transactional` “không chạy” bằng evidence nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `DIAGNOSTIC`  
**Interviewer evaluates:** Proxy class, call path, transaction state và rollback assertion.  
**Answer outline:** Xác minh bean container-managed, proxy type, visibility/finality, caller path; log transaction interceptor hoặc kiểm tra active transaction; viết test làm hỏng giữa chừng và assert DB rollback.  
**Required trade-offs:** Debug logging hữu ích nhưng phải giới hạn noise/secret.  
**Follow-up ladder:** Checked exception? Test transaction che kết quả?  
**Red flags:** Sửa bằng `rollbackFor=Throwable` trước khi tìm call path.  
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SPR-PROXY-008 — `SENIOR`
**Question:** `@Async`, `@Cacheable` và `@Transactional` trên cùng workflow có những bẫy proxy/context nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`  
**Interviewer evaluates:** Thread boundary, transaction context và cache timing.  
**Answer outline:** Async chuyển thread nên transaction/thread-local context không tự truyền; cache có thể ghi trước/sau commit tùy order; self-invocation vẫn bypass. Tách orchestration, after-commit action và test failure windows.  
**Required trade-offs:** Annotation composition nhanh nhưng lifecycle khó kiểm soát.  
**Follow-up ladder:** SecurityContext propagation? Cache eviction rollback?  
**Red flags:** Mọi context tự đi theo async thread.  
**Evidence:** Theory `NOT CREATED`; case `SPR-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SPR-PROXY-009 — `ARCHITECT`
**Question:** Thiết kế ownership của cross-cutting concerns ở quy mô nhiều module/service thế nào?  
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`  
**Interviewer evaluates:** Policy boundary, observability và avoid magic.  
**Answer outline:** Chuẩn hóa policy có contract/version, giữ business invariant trong domain/service; aspect chỉ cho concern thực sự orthogonal; architecture tests và telemetry chứng minh coverage.  
**Required trade-offs:** Platform convention tăng consistency nhưng giảm local autonomy.  
**Follow-up ladder:** Library hay sidecar/gateway? Rollout/versioning?  
**Red flags:** Một global aspect bắt mọi method.  
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### SPR-PROXY-010 — `EXPERT`
**Question:** Nested proxies, object identity và early bean reference có thể tạo lỗi production khó tái hiện thế nào?  
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`  
**Interviewer evaluates:** Proxy chain, equals/hashCode, lifecycle ordering.  
**Answer outline:** Một reference có thể là raw/early proxy hoặc proxy chain khác; identity/annotation lookup/advice order lệch. Capture bean graph, target class, advisors và minimal reproducer; loại circular dependency thay vì phụ thuộc internals.  
**Required trade-offs:** Extension hooks mạnh nhưng nhạy framework upgrade.  
**Follow-up ladder:** Ultimate target class? Double proxy? Native image?  
**Red flags:** Unwrap proxy tùy tiện trong business code.  
**Evidence:** Reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `SPR-02` active, tạo proxy/transaction reproducer và gắn evidence thật; không đổi/reuse stable IDs.

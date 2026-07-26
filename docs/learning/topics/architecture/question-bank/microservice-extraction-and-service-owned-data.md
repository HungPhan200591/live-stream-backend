# Architecture Interview Question Bank — Microservice Extraction and Service-Owned Data

> Status: `DRAFT`<br>
> Domain owner: `Microservice Architecture`<br>
> Active slice: `NONE`; preview target: `MS-01`<br>
> Related roadmap: [Stage 10](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-10---modular-monolith-to-microservices)<br>
> Related depth rubric: [Microservices](../../../knowledge-depth-rubric.md#320-microservice-architecture--p1-target-d2-d3)<br>
> Related theory: [Core](../theory/core/microservice-extraction-and-service-owned-data.md) · [Deep-dive](../theory/deep-dives/strangler-data-handoff-and-failed-extraction-recovery.md)<br>
> Updated: `2026-07-26`

Preview only; không active/implement `MS-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `MS-EXTRACT-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### MS-EXTRACT-001 — `FOUNDATION`
**Question:** Modular monolith và microservices khác nhau ở deployment/failure/data boundary nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** In-process modules vs independently deployed networked services.<br>
**Answer outline:** Modular monolith có một deployment/process nhưng ép ranh giới qua module API; microservice sở hữu deployment/runtime/data và giao tiếp qua mạng không đáng tin cậy. Kích thước codebase nhỏ không tự định nghĩa mô hình nào.<br>
**Required trade-offs:** Independent scaling/deploy vs distributed ops/consistency cost.<br>
**Follow-up ladder:** Distributed monolith?<br>
**Red flags:** Nhiều repositories/controllers nghĩa microservices.<br>
**Evidence:** Theory [core](../theory/core/microservice-extraction-and-service-owned-data.md) + [deep-dive](../theory/deep-dives/strangler-data-handoff-and-failed-extraction-recovery.md); case `MS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### MS-EXTRACT-002 — `FOUNDATION`
**Question:** Service-owned data nghĩa là gì và vì sao shared database writes nguy hiểm?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Invariant/write ownership and coupling.<br>
**Answer outline:** Một service sở hữu schema và write semantics; service khác dùng API, event hoặc replicated read model. Shared write đi vòng business rule, ghép chặt deployment/migration và mở rộng blast radius dù table tách riêng.<br>
**Required trade-offs:** Duplication/eventual consistency vs autonomy.<br>
**Follow-up ladder:** Shared read-only access?<br>
**Red flags:** Database per service bắt buộc server vật lý riêng.<br>
**Evidence:** Theory [core](../theory/core/microservice-extraction-and-service-owned-data.md) + [deep-dive](../theory/deep-dives/strangler-data-handoff-and-failed-extraction-recovery.md); case `MS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### MS-EXTRACT-003 — `FOUNDATION`
**Question:** Synchronous API và asynchronous event phù hợp interaction nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Immediate response vs decoupled durable fact.<br>
**Answer outline:** Dùng sync khi caller cần quyết định/kết quả tức thời và dependency có bound; dùng async để truyền fact, xử lý nền hoặc làm phẳng traffic. Dù cách nào, command/event vẫn cần ownership và idempotency.<br>
**Required trade-offs:** Sync simple but availability coupling; async adds state/replay.<br>
**Follow-up ladder:** Request-reply over broker?<br>
**Red flags:** Async tự động decoupled.<br>
**Evidence:** Theory [core](../theory/core/microservice-extraction-and-service-owned-data.md) + [deep-dive](../theory/deep-dives/strangler-data-handoff-and-failed-extraction-recovery.md); case `MS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### MS-EXTRACT-004 — `FOUNDATION`
**Question:** Service discovery, gateway và load balancer giải quyết các concern nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Endpoint resolution, traffic distribution and edge policy.<br>
**Answer outline:** Service discovery ánh xạ service logic tới instance; load balancer phân phối và xét health; gateway xử lý routing ngoài, auth, quota và translation. Chỉ thêm khi topology cần, không biến thành nơi chứa business logic.<br>
**Required trade-offs:** Central edge simplifies clients but can bottleneck/couple.<br>
**Follow-up ladder:** Client-side vs server-side discovery?<br>
**Red flags:** Gateway thay thế service authorization.<br>
**Evidence:** Theory [core](../theory/core/microservice-extraction-and-service-owned-data.md) + [deep-dive](../theory/deep-dives/strangler-data-handoff-and-failed-extraction-recovery.md); case `MS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### MS-EXTRACT-005 — `SENIOR`
**Question:** Extraction scorecard nên đo pain/evidence nào trước khi tách?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Coupling, cadence, scale, ownership and blast radius.<br>
**Answer outline:** Đo coupling khi thay đổi, team ownership, nhu cầu scale/resource độc lập, xung đột deploy, reliability và data invariant; tính cả năng lực vận hành và phương án không tách.<br>
**Required trade-offs:** Delay extraction retains pain; premature split creates permanent network cost.<br>
**Follow-up ladder:** Analytics vs wallet first?<br>
**Red flags:** LOC lớn là đủ lý do tách.<br>
**Evidence:** Theory [core](../theory/core/microservice-extraction-and-service-owned-data.md) + [deep-dive](../theory/deep-dives/strangler-data-handoff-and-failed-extraction-recovery.md); case `MS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### MS-EXTRACT-006 — `SENIOR`
**Question:** Strangler migration giữ compatibility/data consistency thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Incremental routing, source owner and exit criteria.<br>
**Answer outline:** Định nghĩa seam/API, route một tập request, giữ single write owner; replicate read model qua outbox/CDC, shadow compare, contract test, cutover dần rồi xóa đường cũ sau cửa sổ rollback.<br>
**Required trade-offs:** Dual systems increase temporary complexity/cost.<br>
**Follow-up ladder:** Dual write? Backfill?<br>
**Red flags:** Copy table rồi đổi DNS một lần.<br>
**Evidence:** Theory [core](../theory/core/microservice-extraction-and-service-owned-data.md) + [deep-dive](../theory/deep-dives/strangler-data-handoff-and-failed-extraction-recovery.md); case `MS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### MS-EXTRACT-007 — `SENIOR`
**Question:** Mỗi remote call cần timeout/retry/idempotency/backpressure policy gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Explicit failure contract at network boundary.<br>
**Answer outline:** Dùng deadline end-to-end, concurrency/queue hữu hạn; chỉ retry lỗi tạm thời và operation idempotent trong budget; thêm circuit/load shedding, operation status/compensation và telemetry. Tránh chuỗi gọi quá chatty.<br>
**Required trade-offs:** Cơ chế resilience thêm latency và state, thậm chí khuếch đại lỗi nếu ghép sai.<br>
**Follow-up ladder:** Cancellation propagation?<br>
**Red flags:** Service mesh defaults giải quyết hết.<br>
**Evidence:** Theory [core](../theory/core/microservice-extraction-and-service-owned-data.md) + [deep-dive](../theory/deep-dives/strangler-data-handoff-and-failed-extraction-recovery.md); case `MS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### MS-EXTRACT-008 — `SENIOR`
**Question:** Contract evolution và independent deployment được chứng minh thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Backward compatibility, consumer tests and rollout.<br>
**Answer outline:** Additive/versioned API/events, provider+consumer contract tests, consumer usage telemetry, mixed-version canary and deprecation; schema/data changes expand-contract.<br>
**Required trade-offs:** Compatibility slows cleanup but is price of autonomy.<br>
**Follow-up ladder:** Unknown consumers?<br>
**Red flags:** Deploy độc lập nghĩa không cần coordinate bao giờ.<br>
**Evidence:** Theory [core](../theory/core/microservice-extraction-and-service-owned-data.md) + [deep-dive](../theory/deep-dives/strangler-data-handoff-and-failed-extraction-recovery.md); case `MS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### MS-EXTRACT-009 — `ARCHITECT`
**Question:** Thiết kế analytics extraction nhưng giữ wallet/identity trong monolith ra sao?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Choose low consistency path and data projection.<br>
**Answer outline:** Analytics consume event outbox có version vào store riêng, hỗ trợ replay/backfill và có lag SLO; không ghi vào wallet database. Monolith vẫn là source of truth cho invariant tài chính/identity. Team analytics sở hữu dashboard và runbook.<br>
**Required trade-offs:** Analytics eventual data vs reduced core load/cadence.<br>
**Follow-up ladder:** Correction events? GDPR delete?<br>
**Red flags:** Analytics query trực tiếp replica là service ownership.<br>
**Evidence:** Theory [core](../theory/core/microservice-extraction-and-service-owned-data.md) + [deep-dive](../theory/deep-dives/strangler-data-handoff-and-failed-extraction-recovery.md); case `MS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### MS-EXTRACT-010 — `EXPERT`
**Question:** Sau extraction latency và incidents tăng nhưng quay lại khó: quyết định merge-back/evolve thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Evidence, reversible migration and sunk-cost avoidance.<br>
**Answer outline:** So scorecard cam kết với SLO, cost và team velocity thực tế; tìm gốc ở network/data/ownership. Đơn giản contract, co-locate hoặc merge theo kế hoạch source-of-truth/backfill; giữ tương thích bên ngoài và xem lại ADR.<br>
**Required trade-offs:** Merge reduces ops but couples deploy/teams again.<br>
**Follow-up ladder:** Macroservice? Platform maturity?<br>
**Red flags:** Giữ service vì đã đầu tư nhiều.<br>
**Evidence:** Theory [core](../theory/core/microservice-extraction-and-service-owned-data.md) + [deep-dive](../theory/deep-dives/strangler-data-handoff-and-failed-extraction-recovery.md); case `MS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `MS-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.

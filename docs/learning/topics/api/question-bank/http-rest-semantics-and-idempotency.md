# API Interview Question Bank — HTTP, REST Semantics and Idempotency

> Status: `DRAFT`<br>
> Domain owner: `HTTP/API`<br>
> Active slice: `NONE`; preview target: `API-01`<br>
> Related roadmap: [Stage 2](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-2---spring-internals-http-api-và-transaction-semantics)<br>
> Related depth rubric: [HTTP/API](../../../knowledge-depth-rubric.md#36-http-api-design-và-network-fundamentals--p0-target-d3)<br>
> Related theory: [HTTP, REST Semantics and Idempotency](../theory/core/http-rest-semantics-and-idempotency.md), [idempotency deep-dive](../theory/deep-dives/idempotency-ambiguous-outcomes-and-conditional-requests.md) — `DRAFT`, evidence `NOT RUN`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `API-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `API-HTTP-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### API-HTTP-001 — `FOUNDATION`
**Question:** Safe và idempotent trong HTTP khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Semantics của GET/PUT/DELETE và retry.<br>
**Answer outline:** Safe không yêu cầu thay state server; idempotent nghĩa lặp cùng request có intended effect như một lần. GET safe+idempotent; PUT/DELETE idempotent theo semantics; POST không mặc định. Logging vẫn có side effect phụ.<br>
**Required trade-offs:** Idempotency giúp retry nhưng cần state/key/retention.<br>
**Follow-up ladder:** PATCH có idempotent không? DELETE lần hai trả gì?<br>
**Red flags:** Idempotent nghĩa mọi response giống hệt.<br>
**Evidence:** Theory `NOT CREATED`; case `API-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-HTTP-002 — `FOUNDATION`
**Question:** Khi nào dùng POST, PUT và PATCH?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Resource identity, full replacement và partial update.<br>
**Answer outline:** POST thường tạo dưới collection/server chọn ID hoặc command; PUT đặt/thay representation tại URI biết trước; PATCH mô tả partial change với media-type/semantics rõ.<br>
**Required trade-offs:** CRUD purity đơn giản nhưng command endpoint có thể diễn đạt invariant rõ hơn.<br>
**Follow-up ladder:** JSON Merge Patch vs JSON Patch? Upsert?<br>
**Red flags:** Chọn verb theo method service.<br>
**Evidence:** Theory `NOT CREATED`; case `API-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-HTTP-003 — `FOUNDATION`
**Question:** Phân biệt `200`, `201`, `202`, `204`; `Location` dùng khi nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Synchronous result, accepted async và response contract.<br>
**Answer outline:** 200 có representation; 201 resource đã tạo và thường kèm Location; 202 mới accepted cần status resource/callback; 204 thành công không body.<br>
**Required trade-offs:** 202 giảm blocking nhưng bắt buộc lifecycle/observability.<br>
**Follow-up ladder:** 200 cho create có sai? Polling interval?<br>
**Red flags:** 202 nghĩa công việc chắc chắn thành công.<br>
**Evidence:** Theory `NOT CREATED`; case `API-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-HTTP-004 — `FOUNDATION`
**Question:** Chọn giữa `400`, `401`, `403`, `404`, `409`, `422`, `429` thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Authn/authz, validation, conflict và throttling.<br>
**Answer outline:** 400 malformed; 401 thiếu/sai authentication; 403 authenticated nhưng cấm; 404 absent hoặc concealment có chủ đích; 409 conflict state; 422 semantic validation nếu contract dùng; 429 rate limit kèm retry guidance.<br>
**Required trade-offs:** Error precision hỗ trợ client nhưng có thể lộ resource/security signal.<br>
**Follow-up ladder:** Duplicate key? Stale version? Problem Details?<br>
**Red flags:** Mọi business error trả 400/500.<br>
**Evidence:** Theory `NOT CREATED`; case `API-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-HTTP-005 — `SENIOR`
**Question:** Thiết kế idempotency key cho create/payment-like command thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Scope, request fingerprint, atomic claim và replay.<br>
**Answer outline:** Key scoped theo caller+operation; lưu request hash, state và canonical response; claim atomically, cùng key khác payload phải reject; định nghĩa TTL, in-progress và retry response.<br>
**Required trade-offs:** Retention dài an toàn hơn nhưng tốn storage/privacy; DB unique mạnh hơn cache-only.<br>
**Follow-up ladder:** Concurrent duplicates? Crash sau commit?<br>
**Red flags:** Chỉ dùng UUID client mà không persist/compare payload.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-HTTP-006 — `SENIOR`
**Question:** Client timeout sau khi server commit: retry an toàn và phản hồi thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Ambiguous outcome và reconciliation.<br>
**Answer outline:** Timeout không chứng minh failure; retry cùng idempotency key hoặc query operation/resource status; server replay kết quả đã commit. Trace/correlation phục vụ điều tra nhưng không thay invariant.<br>
**Required trade-offs:** Synchronous certainty không thể bảo đảm qua network failure; cần explicit operation state.<br>
**Follow-up ladder:** Nếu response chưa lưu? Exactly-once?<br>
**Red flags:** Timeout thì luôn gửi request mới với key mới.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-HTTP-007 — `SENIOR`
**Question:** ETag và conditional request ngăn lost update/cache race ra sao?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** `If-Match`, `If-None-Match`, version representation.<br>
**Answer outline:** Server phát ETag/version; update kèm If-Match chỉ chạy nếu version còn khớp, ngược lại 412; GET If-None-Match có thể 304. ETag phải gắn đúng representation/variant.<br>
**Required trade-offs:** Optimistic concurrency tăng round trip/conflict handling nhưng tránh silent overwrite.<br>
**Follow-up ladder:** Weak vs strong ETag? 409 vs 412?<br>
**Red flags:** Last-write-wins luôn an toàn.<br>
**Evidence:** Theory `NOT CREATED`; case `API-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-HTTP-008 — `SENIOR`
**Question:** Thiết kế error envelope để ổn định cho client và vẫn debug được production thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Stable code, field errors, trace ID, secret hygiene.<br>
**Answer outline:** Status đúng semantics; machine-readable stable error code/type, safe message, field violations, correlation ID; stack/internal SQL chỉ ở protected logs. Contract tests giữ backward compatibility.<br>
**Required trade-offs:** Message chi tiết giúp UX nhưng tăng leakage/coupling/localization burden.<br>
**Follow-up ladder:** RFC Problem Details? Retryability field?<br>
**Red flags:** Trả exception message trực tiếp.<br>
**Evidence:** Theory `NOT CREATED`; project contract `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-HTTP-009 — `ARCHITECT`
**Question:** Chuẩn hóa idempotency xuyên gateway và nhiều service mà không hứa exactly-once thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** End-to-end ownership và failure domains.<br>
**Answer outline:** Gateway chống duplicate transport không thay service invariant; mỗi owner persist dedup/result, downstream dùng business key/outbox/inbox; công bố scope, TTL, replay và reconciliation.<br>
**Required trade-offs:** Central policy dễ dùng nhưng state consistency thuộc từng service.<br>
**Follow-up ladder:** Multi-region? GDPR retention?<br>
**Red flags:** Load balancer dedupe tạo exactly-once.<br>
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-HTTP-010 — `EXPERT`
**Question:** Phân tích crash windows từ nhận request đến DB commit và lưu idempotent response.<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Atomicity, recovery state machine và invariant.<br>
**Answer outline:** Liệt kê before claim, after claim, before/after business commit, before response persist, after response lost; co-locate claim/result với transaction khi có thể, trạng thái recoverable và reconciliation cho orphan in-progress.<br>
**Required trade-offs:** Atomic DB design đơn giản hơn distributed store nhưng có contention/retention.<br>
**Follow-up ladder:** Lease expiry? Poison key? Side effect ngoài DB?<br>
**Red flags:** Redis SETNX một mình đóng mọi crash window.<br>
**Evidence:** Reproducer `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `API-01` active, gắn endpoint thật, contract tests và failure evidence; không đổi/reuse stable IDs.

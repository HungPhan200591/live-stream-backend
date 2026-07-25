# API Interview Question Bank — HTTP, REST Semantics and Idempotency

> Status: `DRAFT`  
> Domain owner: `HTTP/API`  
> Active slice: `NONE`; preview target: `API-01`  
> Related roadmap: [Stage 2](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-2---spring-internals-http-api-và-transaction-semantics)  
> Related depth rubric: [HTTP/API](../../../knowledge-depth-rubric.md#36-http-api-design-và-network-fundamentals--p0-target-d3)  
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/api/theory/core/http-rest-semantics-and-idempotency.md`  
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
**Question:** Safe và idempotent trong HTTP khác nhau thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Semantics của GET/PUT/DELETE và retry.  
**Answer outline:** Safe không yêu cầu thay state server; idempotent nghĩa lặp cùng request có intended effect như một lần. GET safe+idempotent; PUT/DELETE idempotent theo semantics; POST không mặc định. Logging vẫn có side effect phụ.  
**Required trade-offs:** Idempotency giúp retry nhưng cần state/key/retention.  
**Follow-up ladder:** PATCH có idempotent không? DELETE lần hai trả gì?  
**Red flags:** Idempotent nghĩa mọi response giống hệt.  
**Evidence:** Theory `NOT CREATED`; case `API-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-HTTP-002 — `FOUNDATION`
**Question:** Khi nào dùng POST, PUT và PATCH?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Resource identity, full replacement và partial update.  
**Answer outline:** POST thường tạo dưới collection/server chọn ID hoặc command; PUT đặt/thay representation tại URI biết trước; PATCH mô tả partial change với media-type/semantics rõ.  
**Required trade-offs:** CRUD purity đơn giản nhưng command endpoint có thể diễn đạt invariant rõ hơn.  
**Follow-up ladder:** JSON Merge Patch vs JSON Patch? Upsert?  
**Red flags:** Chọn verb theo method service.  
**Evidence:** Theory `NOT CREATED`; case `API-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-HTTP-003 — `FOUNDATION`
**Question:** Phân biệt `200`, `201`, `202`, `204`; `Location` dùng khi nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Synchronous result, accepted async và response contract.  
**Answer outline:** 200 có representation; 201 resource đã tạo và thường kèm Location; 202 mới accepted cần status resource/callback; 204 thành công không body.  
**Required trade-offs:** 202 giảm blocking nhưng bắt buộc lifecycle/observability.  
**Follow-up ladder:** 200 cho create có sai? Polling interval?  
**Red flags:** 202 nghĩa công việc chắc chắn thành công.  
**Evidence:** Theory `NOT CREATED`; case `API-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-HTTP-004 — `FOUNDATION`
**Question:** Chọn giữa `400`, `401`, `403`, `404`, `409`, `422`, `429` thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Authn/authz, validation, conflict và throttling.  
**Answer outline:** 400 malformed; 401 thiếu/sai authentication; 403 authenticated nhưng cấm; 404 absent hoặc concealment có chủ đích; 409 conflict state; 422 semantic validation nếu contract dùng; 429 rate limit kèm retry guidance.  
**Required trade-offs:** Error precision hỗ trợ client nhưng có thể lộ resource/security signal.  
**Follow-up ladder:** Duplicate key? Stale version? Problem Details?  
**Red flags:** Mọi business error trả 400/500.  
**Evidence:** Theory `NOT CREATED`; case `API-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-HTTP-005 — `SENIOR`
**Question:** Thiết kế idempotency key cho create/payment-like command thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Scope, request fingerprint, atomic claim và replay.  
**Answer outline:** Key scoped theo caller+operation; lưu request hash, state và canonical response; claim atomically, cùng key khác payload phải reject; định nghĩa TTL, in-progress và retry response.  
**Required trade-offs:** Retention dài an toàn hơn nhưng tốn storage/privacy; DB unique mạnh hơn cache-only.  
**Follow-up ladder:** Concurrent duplicates? Crash sau commit?  
**Red flags:** Chỉ dùng UUID client mà không persist/compare payload.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-HTTP-006 — `SENIOR`
**Question:** Client timeout sau khi server commit: retry an toàn và phản hồi thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Ambiguous outcome và reconciliation.  
**Answer outline:** Timeout không chứng minh failure; retry cùng idempotency key hoặc query operation/resource status; server replay kết quả đã commit. Trace/correlation phục vụ điều tra nhưng không thay invariant.  
**Required trade-offs:** Synchronous certainty không thể bảo đảm qua network failure; cần explicit operation state.  
**Follow-up ladder:** Nếu response chưa lưu? Exactly-once?  
**Red flags:** Timeout thì luôn gửi request mới với key mới.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-HTTP-007 — `SENIOR`
**Question:** ETag và conditional request ngăn lost update/cache race ra sao?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** `If-Match`, `If-None-Match`, version representation.  
**Answer outline:** Server phát ETag/version; update kèm If-Match chỉ chạy nếu version còn khớp, ngược lại 412; GET If-None-Match có thể 304. ETag phải gắn đúng representation/variant.  
**Required trade-offs:** Optimistic concurrency tăng round trip/conflict handling nhưng tránh silent overwrite.  
**Follow-up ladder:** Weak vs strong ETag? 409 vs 412?  
**Red flags:** Last-write-wins luôn an toàn.  
**Evidence:** Theory `NOT CREATED`; case `API-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-HTTP-008 — `SENIOR`
**Question:** Thiết kế error envelope để ổn định cho client và vẫn debug được production thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`  
**Interviewer evaluates:** Stable code, field errors, trace ID, secret hygiene.  
**Answer outline:** Status đúng semantics; machine-readable stable error code/type, safe message, field violations, correlation ID; stack/internal SQL chỉ ở protected logs. Contract tests giữ backward compatibility.  
**Required trade-offs:** Message chi tiết giúp UX nhưng tăng leakage/coupling/localization burden.  
**Follow-up ladder:** RFC Problem Details? Retryability field?  
**Red flags:** Trả exception message trực tiếp.  
**Evidence:** Theory `NOT CREATED`; project contract `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-HTTP-009 — `ARCHITECT`
**Question:** Chuẩn hóa idempotency xuyên gateway và nhiều service mà không hứa exactly-once thế nào?  
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`  
**Interviewer evaluates:** End-to-end ownership và failure domains.  
**Answer outline:** Gateway chống duplicate transport không thay service invariant; mỗi owner persist dedup/result, downstream dùng business key/outbox/inbox; công bố scope, TTL, replay và reconciliation.  
**Required trade-offs:** Central policy dễ dùng nhưng state consistency thuộc từng service.  
**Follow-up ladder:** Multi-region? GDPR retention?  
**Red flags:** Load balancer dedupe tạo exactly-once.  
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-HTTP-010 — `EXPERT`
**Question:** Phân tích crash windows từ nhận request đến DB commit và lưu idempotent response.  
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`  
**Interviewer evaluates:** Atomicity, recovery state machine và invariant.  
**Answer outline:** Liệt kê before claim, after claim, before/after business commit, before response persist, after response lost; co-locate claim/result với transaction khi có thể, trạng thái recoverable và reconciliation cho orphan in-progress.  
**Required trade-offs:** Atomic DB design đơn giản hơn distributed store nhưng có contention/retention.  
**Follow-up ladder:** Lease expiry? Poison key? Side effect ngoài DB?  
**Red flags:** Redis SETNX một mình đóng mọi crash window.  
**Evidence:** Reproducer `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `API-01` active, gắn endpoint thật, contract tests và failure evidence; không đổi/reuse stable IDs.

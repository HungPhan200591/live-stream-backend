# Security Interview Question Bank — Logout-All and Session Cache Invalidation

> Status: `DRAFT`<br>
> Domain owner: `security / session / Redis consistency`<br>
> Active slice: `NONE`; preview target `SEC-02 — logout-all with stale Redis session`<br>
> Related roadmap: [Stage 0](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-0---stabilize-the-laboratory)<br>
> Related depth rubric: [Security](../../../knowledge-depth-rubric.md#38-security-và-identity--p0-target-d3), [Redis](../../../knowledge-depth-rubric.md#315-redis--p1-target-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/security/theory/core/session-revocation-and-cache-consistency.md`<br>
> Updated: `2026-07-25`

Preview này không implement logout, không active case và không tạo evidence. PostgreSQL là authority; Redis chỉ là cache. Mọi câu giữ `UNANSWERED`, test `NOT RUN`.

## Coverage

| Topic | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Session/revocation semantics | 2 | 1 | 1 | 0 |
| Cache invalidation/failure | 0 | 2 | 1 | 1 |
| Concurrency/operations | 0 | 1 | 0 | 1 |
| **Tổng** | **2** | **4** | **2** | **2** |

## Questions

### SEC-SESSION-001 — `FOUNDATION`
**Question:** Logout một session và logout-all khác invariant nào?<br>
**Target depth:** `D1-D2`<br>
**Interviewer evaluates:** Phân biệt session identity, user-wide revocation và access-token residual lifetime.<br>
**Answer outline:** Một-session revoke đúng session; logout-all làm mọi session hiện tại của user không còn refresh/validate được; token cryptographically valid vẫn phải thua revoked durable state theo contract.<br>
**Required trade-offs:** Immediate revocation cần state lookup/invalidation; short TTL chỉ giới hạn exposure.<br>
**Follow-up ladder:** TTL có thay revoke không? Multi-device UX? Global logout SLO? Multi-region boundary?<br>
**Red flags:** Chỉ xóa token phía client.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-002 — `FOUNDATION`
**Question:** Vì sao PostgreSQL phải là source of truth còn Redis session entry chỉ là cache?<br>
**Target depth:** `D1-D2`<br>
**Interviewer evaluates:** Authority, durability và stale-state reasoning.<br>
**Answer outline:** DB giữ active/revoked/expiry durable; cache tăng tốc và có TTL/version; cache miss fallback DB, cache hit không được resurrect state cũ.<br>
**Required trade-offs:** Cache tăng availability/latency nhưng thêm consistency risk.<br>
**Follow-up ladder:** Redis restart? DB down? Negative cache? Revocation version?<br>
**Red flags:** Redis hit luôn được tin.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-003 — `SENIOR`
**Question:** Logout-all tìm và invalidate toàn bộ `session:v1:{sessionId}` thế nào mà không dùng `KEYS` hoặc scan toàn Redis?<br>
**Target depth:** `D2-D3`<br>
**Interviewer evaluates:** Có bounded user-session index hoặc durable query.<br>
**Answer outline:** Query active sessions theo user từ DB hoặc maintain versioned user-session index; revoke atomically trong DB; invalidate exact keys after commit; stale index phải được reconcile.<br>
**Required trade-offs:** Index tăng write/cleanup cost nhưng làm invalidation bounded.<br>
**Follow-up ladder:** Index set TTL? Session limit? Partial delete? Orphan keys?<br>
**Red flags:** `FLUSHALL`, `KEYS session:*` production.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-004 — `SENIOR`
**Question:** DB revoke commit thành công nhưng Redis delete fail. Request sau đó cache-hit stale phải xử lý thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interviewer evaluates:** Crash-window và fail-safe design.<br>
**Answer outline:** Cache value mang session/revocation version hoặc security path revalidates authority; invalidation retry/outbox is idempotent; TTL bounds stale window; never treat deletion success as DB transaction.<br>
**Required trade-offs:** DB check strengthens correctness but costs latency/load.<br>
**Follow-up ladder:** Retry ownership? Outbox? Revocation SLO? Redis partition?<br>
**Red flags:** Swallow delete exception and declare logout complete.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-005 — `SENIOR`
**Question:** Thiết kế integration tests cho logout-all, cache hit/miss, Redis down và stale entry như thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interviewer evaluates:** Negative evidence qua real persistence/cache boundary.<br>
**Answer outline:** Tạo nhiều sessions; warm cache; logout-all; assert DB revoked và mọi refresh/access path bị từ chối; inject stale cache/delete failure/Redis outage; assert no raw token logs.<br>
**Required trade-offs:** Real Redis/PostgreSQL tests chậm hơn nhưng mock không chứng minh serialization/TTL/failure semantics.<br>
**Follow-up ladder:** Deterministic clock? Parallel refresh? Retry assertion? Cleanup isolation?<br>
**Red flags:** Chỉ verify `delete()` được gọi.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-006 — `SENIOR`
**Question:** Hai request refresh và logout-all chạy đồng thời: linearization point và expected outcomes là gì?<br>
**Target depth:** `D2-D3`<br>
**Interviewer evaluates:** Transaction/version conditional-update reasoning.<br>
**Answer outline:** DB revoke commit/version is authority; refresh may succeed only if its atomic session transition precedes revoke; after boundary no new token may commit; cache update includes version to reject stale writes.<br>
**Required trade-offs:** Locks simplify ordering but contend; optimistic version needs safe conflict behavior.<br>
**Follow-up ladder:** Token signed before commit? Retry? User-wide version? Barrier test?<br>
**Red flags:** “Request đến trước thắng” không định nghĩa commit order.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-007 — `ARCHITECT`
**Question:** Chọn per-session invalidation, user revocation epoch hay introspection cho fleet lớn theo tiêu chí nào?<br>
**Target depth:** `D3-D4`<br>
**Interviewer evaluates:** Scale, latency, security SLO và operational cost.<br>
**Answer outline:** Per-key precise nhưng enumeration costly; user epoch makes old tokens/sessions reject cheaply; introspection centralizes immediate control but adds availability dependency; choose by token volume/risk/revocation SLA.<br>
**Required trade-offs:** Stronger immediate control versus request latency and auth-service blast radius.<br>
**Follow-up ladder:** Epoch in token? Cache epoch? Multi-device selective logout? Region lag?<br>
**Red flags:** Chọn theo “Redis nhanh” không capacity/failure model.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-008 — `ARCHITECT`
**Question:** Đặt SLO/telemetry nào cho revocation correctness mà không log token hoặc tạo cardinality explosion?<br>
**Target depth:** `D3-D4`<br>
**Interviewer evaluates:** Measurable security/operations contract.<br>
**Answer outline:** Đo revoke latency, invalidation failure/retry, stale-version rejection, cache hit/miss/fallback và denied-after-revoke; dùng bounded reason tags, session hash/correlation trong secured audit.<br>
**Required trade-offs:** Rich audit aids incident response but increases sensitive-data/retention cost.<br>
**Follow-up ladder:** Alert threshold? Audit access? Sampling? False positive?<br>
**Red flags:** User/session ID làm metric label không giới hạn.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-009 — `EXPERT`
**Question:** Cache invalidation message bị duplicate, reordered hoặc lost trong multi-region. Phát biểu convergence invariant và recovery model.<br>
**Target depth:** `D4`<br>
**Interviewer evaluates:** Monotonic version/idempotency/eventual convergence.<br>
**Answer outline:** Revocation state/version chỉ tiến; consumers apply max version idempotently; lost event bounded by reconciliation/TTL/authority check; no region may overwrite newer revoke with older active state.<br>
**Required trade-offs:** Cross-region synchronous revoke lowers exposure but hurts availability/latency.<br>
**Follow-up ladder:** Partition policy? Replay? Clock versus sequence? Region recovery?<br>
**Red flags:** Pub/Sub delivery được giả định exactly-once.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-010 — `EXPERT`
**Question:** Logout-all trả success nhưng một token vẫn được chấp nhận. Thiết kế forensic workflow phân biệt stale cache, race, replica lag, matcher bypass và token-purpose bug.

**Target depth:** `D4`<br>
**Interviewer evaluates:** Cross-layer causal diagnosis.<br>
**Answer outline:** Correlate revoke commit/version, session/token IDs đã hash, cache version/TTL, request region/instance/path và auth decision reason; reproduce each hypothesis; preserve evidence; fix invariant source, không chỉ giảm TTL.<br>
**Required trade-offs:** Diagnostic metadata phải đủ causal nhưng không lộ credential/PII.<br>
**Follow-up ladder:** Timeline authority? Clock skew? Read replica? Incident containment?<br>
**Red flags:** Flush Redis như “fix”; không kiểm tra URL/auth filter path.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `SEC-02` active: tạo theory/deep-dive, nối actual session/cache code, thêm deterministic integration/race tests và thay marker bằng evidence thật. Generic Redis resilience thuộc `RED-01`; token purpose thuộc `SEC-01`. Stable IDs không tái sử dụng.

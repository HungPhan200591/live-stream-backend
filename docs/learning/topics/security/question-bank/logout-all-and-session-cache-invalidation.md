# Ngân hàng câu hỏi phỏng vấn Security — logout-all và invalidation session cache

> Status: `DRAFT`<br>
> Domain owner: `security / session / Redis consistency`<br>
> Active slice: `NONE`; preview target `SEC-02 — logout-all with stale Redis session`<br>
> Related roadmap: [Stage 0](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-0---stabilize-the-laboratory)<br>
> Related depth rubric: [Security](../../../knowledge-depth-rubric.md#38-security-và-identity--p0-target-d3), [Redis](../../../knowledge-depth-rubric.md#315-redis--p1-target-d3)<br>
> Related theory: [Core theory](../theory/core/session-revocation-and-cache-consistency.md); [Deep-dive](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md)<br>
> Updated: `2026-07-26`

Bản xem trước này không triển khai logout, không kích hoạt case và không tạo bằng chứng. PostgreSQL là nguồn có thẩm quyền; Redis chỉ là cache. Mọi câu giữ `UNANSWERED`, kiểm thử `NOT RUN`. `Interview likelihood` chỉ là ước lượng trong chủ đề JWT/session, không phải tỷ lệ thị trường đã đo.

## Coverage

| Topic | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Session/revocation semantics | 4 | 3 | 1 | 0 |
| Cache invalidation/failure | 1 | 3 | 1 | 1 |
| Concurrency/operations | 0 | 1 | 0 | 1 |
| **Tổng** | **5** | **7** | **2** | **2** |

## Recommended practice order

1. First pass — câu phổ biến: `SEC-SESSION-011`, `SEC-SESSION-012`, `SEC-SESSION-001`, `SEC-SESSION-014`, `SEC-SESSION-013`, `SEC-SESSION-015`, `SEC-SESSION-016`.
2. Senior follow-up: `SEC-SESSION-002`, `SEC-SESSION-004`, `SEC-SESSION-005`.
3. Project application: `SEC-SESSION-003`.
4. Architect/Expert stretch: `SEC-SESSION-006` đến `SEC-SESSION-010`.

## Questions

### SEC-SESSION-001 — `FOUNDATION`
**Question:** Logout một session và logout-all khác invariant nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — biến thể quen thuộc khi hỏi JWT/session logout.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Phân biệt session identity, user-wide revocation và access-token residual lifetime.<br>
**Answer outline:** Một-session revoke đúng session; logout-all làm mọi session hiện tại của user không còn refresh/validate được; token cryptographically valid vẫn phải thua revoked durable state theo contract.<br>
**Required trade-offs:** Immediate revocation cần state lookup/invalidation; short TTL chỉ giới hạn exposure.<br>
**Follow-up ladder:** TTL có thay revoke không? Multi-device UX? Global logout SLO? Multi-region boundary?<br>
**Red flags:** Chỉ xóa token phía client.<br>
**Evidence:** Theory [Core](../theory/core/session-revocation-and-cache-consistency.md); Deep-dive [Advanced](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md); case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-002 — `FOUNDATION`
**Question:** Vì sao PostgreSQL phải là source of truth còn Redis session entry chỉ là cache?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `MEDIUM` — follow-up quen thuộc khi hệ thống dùng session store/cache.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Authority, durability và stale-state reasoning.<br>
**Answer outline:** DB giữ active/revoked/expiry durable; cache tăng tốc và có TTL/version; cache miss fallback DB, cache hit không được resurrect state cũ.<br>
**Required trade-offs:** Cache tăng availability/latency nhưng thêm consistency risk.<br>
**Follow-up ladder:** Redis restart? DB down? Negative cache? Revocation version?<br>
**Red flags:** Redis hit luôn được tin.<br>
**Evidence:** Theory [Core](../theory/core/session-revocation-and-cache-consistency.md); Deep-dive [Advanced](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md); case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-003 — `SENIOR`
**Question:** Logout-all tìm và invalidate toàn bộ `session:v1:{sessionId}` thế nào mà không dùng `KEYS` hoặc scan toàn Redis?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `LOW` — hữu ích cho project application, không phải câu mở đầu phổ biến.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Có bounded user-session index hoặc durable query.<br>
**Answer outline:** Query active sessions theo user từ DB hoặc maintain versioned user-session index; revoke atomically trong DB; invalidate exact keys after commit; stale index phải được reconcile.<br>
**Required trade-offs:** Index tăng write/cleanup cost nhưng làm invalidation bounded.<br>
**Follow-up ladder:** Index set TTL? Session limit? Partial delete? Orphan keys?<br>
**Red flags:** `FLUSHALL`, `KEYS session:*` production.<br>
**Evidence:** Theory [Core](../theory/core/session-revocation-and-cache-consistency.md); Deep-dive [Advanced](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md); case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-004 — `SENIOR`
**Question:** DB revoke commit thành công nhưng Redis delete fail. Request sau đó cache-hit stale phải xử lý thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — cache invalidation failure là follow-up Senior tự nhiên.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Crash-window và fail-safe design.<br>
**Answer outline:** Cache value mang session/revocation version hoặc security path revalidates authority; invalidation retry/outbox is idempotent; TTL bounds stale window; never treat deletion success as DB transaction.<br>
**Required trade-offs:** DB check strengthens correctness but costs latency/load.<br>
**Follow-up ladder:** Retry ownership? Outbox? Revocation SLO? Redis partition?<br>
**Red flags:** Swallow delete exception and declare logout complete.<br>
**Evidence:** Theory [Core](../theory/core/session-revocation-and-cache-consistency.md); Deep-dive [Advanced](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md); case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-005 — `SENIOR`
**Question:** Thiết kế integration tests cho logout-all, cache hit/miss, Redis down và stale entry như thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — thường xuất hiện dưới dạng “bạn test giải pháp này thế nào?”.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Negative evidence qua real persistence/cache boundary.<br>
**Answer outline:** Tạo nhiều sessions; warm cache; logout-all; assert DB revoked và mọi refresh/access path bị từ chối; inject stale cache/delete failure/Redis outage; assert no raw token logs.<br>
**Required trade-offs:** Real Redis/PostgreSQL tests chậm hơn nhưng mock không chứng minh serialization/TTL/failure semantics.<br>
**Follow-up ladder:** Deterministic clock? Parallel refresh? Retry assertion? Cleanup isolation?<br>
**Red flags:** Chỉ verify `delete()` được gọi.<br>
**Evidence:** Theory [Core](../theory/core/session-revocation-and-cache-consistency.md); Deep-dive [Advanced](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md); case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-006 — `SENIOR`
**Question:** Hai request refresh và logout-all chạy đồng thời: linearization point và expected outcomes là gì?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `LOW` — concurrency discriminator sau khi đã trả lời logout flow.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Transaction/version conditional-update reasoning.<br>
**Answer outline:** DB revoke commit/version is authority; refresh may succeed only if its atomic session transition precedes revoke; after boundary no new token may commit; cache update includes version to reject stale writes.<br>
**Required trade-offs:** Locks simplify ordering but contend; optimistic version needs safe conflict behavior.<br>
**Follow-up ladder:** Token signed before commit? Retry? User-wide version? Barrier test?<br>
**Red flags:** “Request đến trước thắng” không định nghĩa commit order.<br>
**Evidence:** Theory [Core](../theory/core/session-revocation-and-cache-consistency.md); Deep-dive [Advanced](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md); case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-007 — `ARCHITECT`
**Question:** Chọn per-session invalidation, user revocation epoch hay introspection cho fleet lớn theo tiêu chí nào?<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `LOW` — câu system-design phụ thuộc quy mô và security SLA.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Scale, latency, security SLO và operational cost.<br>
**Answer outline:** Per-key precise nhưng enumeration costly; user epoch makes old tokens/sessions reject cheaply; introspection centralizes immediate control but adds availability dependency; choose by token volume/risk/revocation SLA.<br>
**Required trade-offs:** Stronger immediate control versus request latency and auth-service blast radius.<br>
**Follow-up ladder:** Epoch in token? Cache epoch? Multi-device selective logout? Region lag?<br>
**Red flags:** Chọn theo “Redis nhanh” không capacity/failure model.<br>
**Evidence:** Theory [Core](../theory/core/session-revocation-and-cache-consistency.md); Deep-dive [Advanced](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md); case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-008 — `ARCHITECT`
**Question:** Đặt SLO/telemetry nào cho revocation correctness mà không log token hoặc tạo cardinality explosion?<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `LOW` — observability/security follow-up cho vai trò lead hoặc architect.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Measurable security/operations contract.<br>
**Answer outline:** Đo revoke latency, invalidation failure/retry, stale-version rejection, cache hit/miss/fallback và denied-after-revoke; dùng bounded reason tags, session hash/correlation trong secured audit.<br>
**Required trade-offs:** Rich audit aids incident response but increases sensitive-data/retention cost.<br>
**Follow-up ladder:** Alert threshold? Audit access? Sampling? False positive?<br>
**Red flags:** User/session ID làm metric label không giới hạn.<br>
**Evidence:** Theory [Core](../theory/core/session-revocation-and-cache-consistency.md); Deep-dive [Advanced](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md); case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-009 — `EXPERT`
**Question:** Cache invalidation message bị duplicate, reordered hoặc lost trong multi-region. Phát biểu convergence invariant và recovery model.<br>
**Target depth:** `D4`<br>
**Interview likelihood:** `LOW` — pathological distributed-systems stretch.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Monotonic version/idempotency/eventual convergence.<br>
**Answer outline:** Revocation state/version chỉ tiến; consumers apply max version idempotently; lost event bounded by reconciliation/TTL/authority check; no region may overwrite newer revoke with older active state.<br>
**Required trade-offs:** Cross-region synchronous revoke lowers exposure but hurts availability/latency.<br>
**Follow-up ladder:** Partition policy? Replay? Clock versus sequence? Region recovery?<br>
**Red flags:** Pub/Sub delivery được giả định exactly-once.<br>
**Evidence:** Theory [Core](../theory/core/session-revocation-and-cache-consistency.md); Deep-dive [Advanced](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md); case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-010 — `EXPERT`
**Question:** Logout-all trả success nhưng một token vẫn được chấp nhận. Thiết kế forensic workflow phân biệt stale cache, race, replica lag, matcher bypass và token-purpose bug.

**Target depth:** `D4`<br>
**Interview likelihood:** `LOW` — incident discriminator, không phải câu core phải học đầu tiên.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Cross-layer causal diagnosis.<br>
**Answer outline:** Correlate revoke commit/version, session/token IDs đã hash, cache version/TTL, request region/instance/path và auth decision reason; reproduce each hypothesis; preserve evidence; fix invariant source, không chỉ giảm TTL.<br>
**Required trade-offs:** Diagnostic metadata phải đủ causal nhưng không lộ credential/PII.<br>
**Follow-up ladder:** Timeline authority? Clock skew? Read replica? Incident containment?<br>
**Red flags:** Flush Redis như “fix”; không kiểm tra URL/auth filter path.<br>
**Evidence:** Theory [Core](../theory/core/session-revocation-and-cache-consistency.md); Deep-dive [Advanced](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md); case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-011 — `FOUNDATION`
**Question:** Với JWT stateless, logout hoạt động thế nào khi server không thể “xóa” access token đã phát hành?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu JWT logout rất phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Hiểu self-contained token, expiry và nhu cầu state khi cần revoke sớm.<br>
**Answer outline:** Client xóa token chỉ kết thúc phía client; server giới hạn access-token TTL hoặc kiểm tra denylist/session/revocation version nếu contract yêu cầu revoke tức thời; refresh token phải bị revoke ở authority.<br>
**Required trade-offs:** Stateless validation nhanh/độc lập nhưng khó immediate revocation; stateful check tăng control và dependency.<br>
**Follow-up ladder:** Access TTL bao lâu? Denylist TTL? Key rotation có phải logout không?<br>
**Red flags:** “Xóa JWT trên server” dù server không lưu token.<br>
**Evidence:** Theory [Core](../theory/core/session-revocation-and-cache-consistency.md); Deep-dive [Advanced](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md); case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-012 — `FOUNDATION`
**Question:** Sau khi logout, access token hiện tại và refresh token nên còn hiệu lực ra sao?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — kiểm tra nền tảng access/refresh lifecycle.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Phân biệt residual access lifetime với quyền phát hành token mới.<br>
**Answer outline:** Refresh token/session phải bị revoke để không phát access token mới; access token có thể sống tới expiry hoặc bị chặn sớm bằng session/version lookup tùy security contract.<br>
**Required trade-offs:** Short access TTL giảm exposure nhưng tăng refresh traffic; immediate access revoke tăng lookup cost.<br>
**Follow-up ladder:** Logout một device? Logout-all? Password change?<br>
**Red flags:** Access và refresh token được xử lý giống hệt nhau.<br>
**Evidence:** Theory [Core](../theory/core/session-revocation-and-cache-consistency.md); Deep-dive [Advanced](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md); case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-013 — `SENIOR`
**Question:** Những chiến lược phổ biến để revoke JWT/session là gì, và khi nào chọn denylist, server-side session hoặc token version?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — follow-up phổ biến sau câu JWT stateless.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** So sánh solution theo immediate control, lookup cost, granularity và cleanup.<br>
**Answer outline:** Denylist phù hợp revoke token cụ thể; server-side session cho lifecycle/device control; user/token version revoke theo nhóm rẻ hơn nhưng coarse; luôn bound retention bằng expiry/TTL.<br>
**Required trade-offs:** Granularity và immediate consistency đổi lấy state, latency và operational cost.<br>
**Follow-up ladder:** Multi-device? Compromised token? Region outage? Cleanup?<br>
**Red flags:** Một giải pháp được tuyên bố tốt nhất cho mọi threat model.<br>
**Evidence:** Theory [Core](../theory/core/session-revocation-and-cache-consistency.md); Deep-dive [Advanced](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md); case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-014 — `FOUNDATION`
**Question:** Cache-aside là gì, và tại sao update/delete dữ liệu nguồn phải đi kèm cache invalidation?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu caching nền tảng thường gặp.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Đọc miss từ DB, populate cache và stale-data window.<br>
**Answer outline:** Application đọc cache trước, miss thì đọc authority rồi populate; khi authority thay đổi phải invalidate/update cache theo policy, nếu không cache hit trả state cũ tới TTL.<br>
**Required trade-offs:** Delete-on-write đơn giản nhưng có miss; update cache giảm miss nhưng tăng race/dual-write risk.<br>
**Follow-up ladder:** TTL có đủ không? Write-through? Cache stampede?<br>
**Red flags:** Coi TTL là consistency guarantee tức thời.<br>
**Evidence:** Theory [Core](../theory/core/session-revocation-and-cache-consistency.md); Deep-dive [Advanced](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md); case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-015 — `SENIOR`
**Question:** Có nên lưu blacklist cho mọi access token đã logout trong Redis không?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — biến thể thực tế của JWT revocation.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Memory bound, lookup path và alternative design.<br>
**Answer outline:** Có thể khi risk đòi immediate per-token revoke và TTL blacklist bằng remaining token lifetime; ở volume lớn cân nhắc short-lived access token, server session hoặc revocation epoch để tránh state tăng theo token.<br>
**Required trade-offs:** Chính xác theo token đổi lấy memory, lookup latency và Redis availability dependency.<br>
**Follow-up ladder:** Estimate memory? Redis down? False logout? Key format?<br>
**Red flags:** Blacklist không TTL hoặc lưu vĩnh viễn.<br>
**Evidence:** Theory [Core](../theory/core/session-revocation-and-cache-consistency.md); Deep-dive [Advanced](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md); case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SESSION-016 — `SENIOR`
**Question:** Authentication/session flow nên làm gì khi Redis unavailable?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — câu failure-mode phổ biến khi đưa Redis vào security path.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Fail-open/fail-closed theo operation và fallback về durable authority.<br>
**Answer outline:** Không chọn một policy cho mọi endpoint; sensitive validation/revoke ưu tiên fail-safe hoặc fallback DB có timeout/load guard, trong khi non-security cache có thể degrade; metric và recovery phải rõ.<br>
**Required trade-offs:** Security/correctness versus availability và database overload.<br>
**Follow-up ladder:** Circuit breaker? Rate limit fallback? DB cũng down? SLO?<br>
**Red flags:** Luôn fail-open vì availability hoặc luôn retry vô hạn.<br>
**Evidence:** Theory [Core](../theory/core/session-revocation-and-cache-consistency.md); Deep-dive [Advanced](../theory/deep-dives/session-revocation-races-epochs-and-cache-recovery.md); case `SEC-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `SEC-02` active: tạo theory/deep-dive, nối actual session/cache code, thêm deterministic integration/race tests và thay marker bằng evidence thật. Generic Redis resilience thuộc `RED-01`; token purpose thuộc `SEC-01`. Stable IDs không tái sử dụng.

# API Interview Question Bank — Pagination, Versioning and Network Boundaries

> Status: `DRAFT`<br>
> Domain owner: `HTTP/API`<br>
> Active slice: `NONE`; preview target: `API-01`<br>
> Related roadmap: [Stage 2](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-2---spring-internals-http-api-và-transaction-semantics)<br>
> Related depth rubric: [HTTP/API](../../../knowledge-depth-rubric.md#36-http-api-design-và-network-fundamentals--p0-target-d3)<br>
> Related theory: [Core](../theory/core/api-pagination-versioning-and-network-boundaries.md) · [Deep-dive](../theory/deep-dives/cursor-pagination-compatible-evolution-and-proxy-boundaries.md) — DRAFT, evidence NOT RUN<br>
> Updated: `2026-07-26`

Preview only; không active/implement `API-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `API-EVOL-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### API-EVOL-001 — `FOUNDATION`
**Question:** Offset pagination và cursor/keyset pagination khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Cost, consistency và navigation semantics.<br>
**Answer outline:** Offset dễ random page nhưng deep offset tốn scan và drift khi insert/delete; keyset dùng last sort key, nhanh/ổn định hơn nhưng khó jump/count và cần ordering/index phù hợp.<br>
**Required trade-offs:** UX page-number vs scale/consistency.<br>
**Follow-up ladder:** Total count? Backward cursor?<br>
**Red flags:** Cursor chỉ là base64 của offset nên giải quyết mọi vấn đề.<br>
**Evidence:** Theory [core](../theory/core/api-pagination-versioning-and-network-boundaries.md) + [deep-dive](../theory/deep-dives/cursor-pagination-compatible-evolution-and-proxy-boundaries.md); case `API-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-EVOL-002 — `FOUNDATION`
**Question:** Vì sao pagination cần stable ordering và tie-breaker duy nhất?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Duplicate/missing rows.<br>
**Answer outline:** Sort field không unique khiến thứ tự tùy plan; dùng composite order như `(createdAt, id)` và cursor mang đủ keys, cùng direction/null semantics. Index nên cùng prefix.<br>
**Required trade-offs:** Thêm ID vào cursor lộ implementation nếu không encode/version.<br>
**Follow-up ladder:** Timestamps trùng? Null? Sort descending?<br>
**Red flags:** `ORDER BY created_at` luôn deterministic.<br>
**Evidence:** Theory [core](../theory/core/api-pagination-versioning-and-network-boundaries.md) + [deep-dive](../theory/deep-dives/cursor-pagination-compatible-evolution-and-proxy-boundaries.md); case `API-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-EVOL-003 — `FOUNDATION`
**Question:** Thay đổi API nào backward compatible và thay đổi nào breaking?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Consumer perspective thay vì server compile.<br>
**Answer outline:** Xóa/rename/đổi type/semantics/required field thường breaking; thêm optional response field thường compatible nhưng strict clients có thể vỡ. Compatibility gồm wire, behavior, auth, timing và enum evolution.<br>
**Required trade-offs:** Tương thích dài hạn làm contract cồng kềnh.<br>
**Follow-up ladder:** Enum value mới? Default changed?<br>
**Red flags:** JSON có field mới luôn không breaking.<br>
**Evidence:** Theory [core](../theory/core/api-pagination-versioning-and-network-boundaries.md) + [deep-dive](../theory/deep-dives/cursor-pagination-compatible-evolution-and-proxy-boundaries.md); case `API-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-EVOL-004 — `FOUNDATION`
**Question:** Một HTTPS request đi qua các bước/hop chính nào trước controller?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** DNS, TCP/QUIC, TLS, LB/proxy, server queue.<br>
**Answer outline:** DNS resolve, connection/handshake, TLS, gateway/LB/reverse proxy, app connector/filter/security/controller; mỗi hop có queue/timeout/retry/header rewrite. Keep-alive giảm setup cost.<br>
**Required trade-offs:** Nhiều hop tăng policy/observability nhưng thêm latency/failure modes.<br>
**Follow-up ladder:** HTTP/2 multiplexing? TLS termination?<br>
**Red flags:** Network time chỉ là thời gian controller.<br>
**Evidence:** Theory [core](../theory/core/api-pagination-versioning-and-network-boundaries.md) + [deep-dive](../theory/deep-dives/cursor-pagination-compatible-evolution-and-proxy-boundaries.md); experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-EVOL-005 — `SENIOR`
**Question:** Thiết kế cursor opaque, tamper-resistant và có thể evolve thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Sort/filter binding, version và validation.<br>
**Answer outline:** Encode version, direction, last composite keys và query/filter fingerprint; sign/MAC nếu client sửa gây security/cost risk; validate expiry/schema và trả lỗi contract rõ. Không nhét secret.<br>
**Required trade-offs:** Stateless cursor dễ scale nhưng rotation/evolution phức tạp.<br>
**Follow-up ladder:** Snapshot consistency? Key rotation?<br>
**Red flags:** Decode client cursor rồi ghép SQL trực tiếp.<br>
**Evidence:** Theory [core](../theory/core/api-pagination-versioning-and-network-boundaries.md) + [deep-dive](../theory/deep-dives/cursor-pagination-compatible-evolution-and-proxy-boundaries.md); experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-EVOL-006 — `SENIOR`
**Question:** Version API bằng URL, header hay media type; khi nào không cần version mới?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Contract governance và migration.<br>
**Answer outline:** URL rõ/tool-friendly; header/media type sạch URI nhưng tooling/cache/debug khó hơn. Ưu tiên compatible evolution; version mới khi semantics/schema không thể bridge an toàn, kèm deprecation và telemetry consumer.<br>
**Required trade-offs:** Song song nhiều version tăng test/ops/security cost.<br>
**Follow-up ladder:** Sunset header? Internal API?<br>
**Red flags:** Version mỗi lần thêm field.<br>
**Evidence:** Theory [core](../theory/core/api-pagination-versioning-and-network-boundaries.md) + [deep-dive](../theory/deep-dives/cursor-pagination-compatible-evolution-and-proxy-boundaries.md); design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-EVOL-007 — `SENIOR`
**Question:** Trust `X-Forwarded-*`/`Forwarded` sai cách gây security bug gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `SECURITY_SCENARIO`<br>
**Interviewer evaluates:** Trusted proxy boundary và spoofing.<br>
**Answer outline:** Client có thể giả IP/scheme/host nếu app trust mọi header, phá rate limit, redirects, secure cookies, audit. Chỉ accept từ trusted proxies và cấu hình hop processing/overwrite rõ.<br>
**Required trade-offs:** Proxy-aware config cần topology chính xác; sai fail-open rất nguy hiểm.<br>
**Follow-up ladder:** Host header attack? RFC 7239?<br>
**Red flags:** Header có prefix X nên đáng tin.<br>
**Evidence:** Theory [core](../theory/core/api-pagination-versioning-and-network-boundaries.md) + [deep-dive](../theory/deep-dives/cursor-pagination-compatible-evolution-and-proxy-boundaries.md); security test `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-EVOL-008 — `SENIOR`
**Question:** Contract tests và observability hỗ trợ deprecation/migration client thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Consumer inventory, compatibility gates và rollout.<br>
**Answer outline:** Schema/consumer contract tests chặn breaking drift; metric theo route/version/client cho biết adoption, cảnh báo deadline; dual-read/write chỉ khi có exit criteria và reconciliation.<br>
**Required trade-offs:** Consumer-driven contracts tăng confidence nhưng có governance/maintenance cost.<br>
**Follow-up ladder:** Unknown consumers? Mobile clients?<br>
**Red flags:** Gửi email deprecation là đủ.<br>
**Evidence:** Theory [core](../theory/core/api-pagination-versioning-and-network-boundaries.md) + [deep-dive](../theory/deep-dives/cursor-pagination-compatible-evolution-and-proxy-boundaries.md); project contract `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-EVOL-009 — `ARCHITECT`
**Question:** Lập chiến lược API evolution cho public API nhiều năm và nhiều consumer thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Compatibility budget, ownership và retirement.<br>
**Answer outline:** Contract governance, additive-first, schema diff gate, consumer identity/telemetry, published support window, migration kit, sunset enforcement và security patch policy cho old versions.<br>
**Required trade-offs:** Stability tăng trust nhưng làm chậm semantic cleanup.<br>
**Follow-up ladder:** Emergency breaking change? Regional rollout?<br>
**Red flags:** Giữ mọi version vĩnh viễn.<br>
**Evidence:** Theory [core](../theory/core/api-pagination-versioning-and-network-boundaries.md) + [deep-dive](../theory/deep-dives/cursor-pagination-compatible-evolution-and-proxy-boundaries.md); Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### API-EVOL-010 — `EXPERT`
**Question:** Cursor pagination dưới concurrent insert/update/delete có thể bỏ sót hoặc trùng dữ liệu thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Isolation/snapshot, mutable sort keys và guarantees.<br>
**Answer outline:** Keyset ổn định vị trí nhưng không tạo snapshot; row mới/cập nhật vượt boundary có thể xuất hiện/bỏ qua. Chọn immutable keys, snapshot token/watermark hoặc công bố eventual browsing semantics; dedupe client khi phù hợp.<br>
**Required trade-offs:** Snapshot consistency cần transaction/state retention và có thể không scale cho session dài.<br>
**Follow-up ladder:** CDC watermark? Delete tombstone?<br>
**Red flags:** Opaque cursor tự đảm bảo snapshot.<br>
**Evidence:** Theory [core](../theory/core/api-pagination-versioning-and-network-boundaries.md) + [deep-dive](../theory/deep-dives/cursor-pagination-compatible-evolution-and-proxy-boundaries.md); Reproducer `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `API-01` active, tạo query/cursor reproducer và contract evidence; không đổi/reuse stable IDs.

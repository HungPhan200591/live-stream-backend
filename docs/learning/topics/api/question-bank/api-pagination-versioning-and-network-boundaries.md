# API Interview Question Bank — Pagination, Versioning and Network Boundaries

> Status: `DRAFT`  
> Domain owner: `HTTP/API`  
> Active slice: `NONE`; preview target: `API-02`  
> Related roadmap: [Stage 2](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-2---spring-internals-http-api-và-transaction-semantics)  
> Related depth rubric: [HTTP/API](../../../knowledge-depth-rubric.md#36-http-api-design-và-network-fundamentals--p0-target-d3)  
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/api/theory/core/api-pagination-versioning-and-network-boundaries.md`  
> Updated: `2026-07-26`

Preview only; không active/implement `API-02`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `API-EVOL-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### API-EVOL-001 — `FOUNDATION`
**Question:** Offset pagination và cursor/keyset pagination khác nhau thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Cost, consistency và navigation semantics.  
**Answer outline:** Offset dễ random page nhưng deep offset tốn scan và drift khi insert/delete; keyset dùng last sort key, nhanh/ổn định hơn nhưng khó jump/count và cần ordering/index phù hợp.  
**Required trade-offs:** UX page-number vs scale/consistency.  
**Follow-up ladder:** Total count? Backward cursor?  
**Red flags:** Cursor chỉ là base64 của offset nên giải quyết mọi vấn đề.  
**Evidence:** Theory `NOT CREATED`; case `API-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-EVOL-002 — `FOUNDATION`
**Question:** Vì sao pagination cần stable ordering và tie-breaker duy nhất?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Duplicate/missing rows.  
**Answer outline:** Sort field không unique khiến thứ tự tùy plan; dùng composite order như `(createdAt, id)` và cursor mang đủ keys, cùng direction/null semantics. Index nên cùng prefix.  
**Required trade-offs:** Thêm ID vào cursor lộ implementation nếu không encode/version.  
**Follow-up ladder:** Timestamps trùng? Null? Sort descending?  
**Red flags:** `ORDER BY created_at` luôn deterministic.  
**Evidence:** Theory `NOT CREATED`; case `API-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-EVOL-003 — `FOUNDATION`
**Question:** Thay đổi API nào backward compatible và thay đổi nào breaking?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Consumer perspective thay vì server compile.  
**Answer outline:** Xóa/rename/đổi type/semantics/required field thường breaking; thêm optional response field thường compatible nhưng strict clients có thể vỡ. Compatibility gồm wire, behavior, auth, timing và enum evolution.  
**Required trade-offs:** Tương thích dài hạn làm contract cồng kềnh.  
**Follow-up ladder:** Enum value mới? Default changed?  
**Red flags:** JSON có field mới luôn không breaking.  
**Evidence:** Theory `NOT CREATED`; case `API-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-EVOL-004 — `FOUNDATION`
**Question:** Một HTTPS request đi qua các bước/hop chính nào trước controller?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** DNS, TCP/QUIC, TLS, LB/proxy, server queue.  
**Answer outline:** DNS resolve, connection/handshake, TLS, gateway/LB/reverse proxy, app connector/filter/security/controller; mỗi hop có queue/timeout/retry/header rewrite. Keep-alive giảm setup cost.  
**Required trade-offs:** Nhiều hop tăng policy/observability nhưng thêm latency/failure modes.  
**Follow-up ladder:** HTTP/2 multiplexing? TLS termination?  
**Red flags:** Network time chỉ là thời gian controller.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-EVOL-005 — `SENIOR`
**Question:** Thiết kế cursor opaque, tamper-resistant và có thể evolve thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Sort/filter binding, version và validation.  
**Answer outline:** Encode version, direction, last composite keys và query/filter fingerprint; sign/MAC nếu client sửa gây security/cost risk; validate expiry/schema và trả lỗi contract rõ. Không nhét secret.  
**Required trade-offs:** Stateless cursor dễ scale nhưng rotation/evolution phức tạp.  
**Follow-up ladder:** Snapshot consistency? Key rotation?  
**Red flags:** Decode client cursor rồi ghép SQL trực tiếp.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-EVOL-006 — `SENIOR`
**Question:** Version API bằng URL, header hay media type; khi nào không cần version mới?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Contract governance và migration.  
**Answer outline:** URL rõ/tool-friendly; header/media type sạch URI nhưng tooling/cache/debug khó hơn. Ưu tiên compatible evolution; version mới khi semantics/schema không thể bridge an toàn, kèm deprecation và telemetry consumer.  
**Required trade-offs:** Song song nhiều version tăng test/ops/security cost.  
**Follow-up ladder:** Sunset header? Internal API?  
**Red flags:** Version mỗi lần thêm field.  
**Evidence:** Theory `NOT CREATED`; design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-EVOL-007 — `SENIOR`
**Question:** Trust `X-Forwarded-*`/`Forwarded` sai cách gây security bug gì?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `SECURITY_SCENARIO`  
**Interviewer evaluates:** Trusted proxy boundary và spoofing.  
**Answer outline:** Client có thể giả IP/scheme/host nếu app trust mọi header, phá rate limit, redirects, secure cookies, audit. Chỉ accept từ trusted proxies và cấu hình hop processing/overwrite rõ.  
**Required trade-offs:** Proxy-aware config cần topology chính xác; sai fail-open rất nguy hiểm.  
**Follow-up ladder:** Host header attack? RFC 7239?  
**Red flags:** Header có prefix X nên đáng tin.  
**Evidence:** Theory `NOT CREATED`; security test `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-EVOL-008 — `SENIOR`
**Question:** Contract tests và observability hỗ trợ deprecation/migration client thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`  
**Interviewer evaluates:** Consumer inventory, compatibility gates và rollout.  
**Answer outline:** Schema/consumer contract tests chặn breaking drift; metric theo route/version/client cho biết adoption, cảnh báo deadline; dual-read/write chỉ khi có exit criteria và reconciliation.  
**Required trade-offs:** Consumer-driven contracts tăng confidence nhưng có governance/maintenance cost.  
**Follow-up ladder:** Unknown consumers? Mobile clients?  
**Red flags:** Gửi email deprecation là đủ.  
**Evidence:** Theory `NOT CREATED`; project contract `EXISTS`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-EVOL-009 — `ARCHITECT`
**Question:** Lập chiến lược API evolution cho public API nhiều năm và nhiều consumer thế nào?  
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`  
**Interviewer evaluates:** Compatibility budget, ownership và retirement.  
**Answer outline:** Contract governance, additive-first, schema diff gate, consumer identity/telemetry, published support window, migration kit, sunset enforcement và security patch policy cho old versions.  
**Required trade-offs:** Stability tăng trust nhưng làm chậm semantic cleanup.  
**Follow-up ladder:** Emergency breaking change? Regional rollout?  
**Red flags:** Giữ mọi version vĩnh viễn.  
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### API-EVOL-010 — `EXPERT`
**Question:** Cursor pagination dưới concurrent insert/update/delete có thể bỏ sót hoặc trùng dữ liệu thế nào?  
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`  
**Interviewer evaluates:** Isolation/snapshot, mutable sort keys và guarantees.  
**Answer outline:** Keyset ổn định vị trí nhưng không tạo snapshot; row mới/cập nhật vượt boundary có thể xuất hiện/bỏ qua. Chọn immutable keys, snapshot token/watermark hoặc công bố eventual browsing semantics; dedupe client khi phù hợp.  
**Required trade-offs:** Snapshot consistency cần transaction/state retention và có thể không scale cho session dài.  
**Follow-up ladder:** CDC watermark? Delete tombstone?  
**Red flags:** Opaque cursor tự đảm bảo snapshot.  
**Evidence:** Reproducer `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `API-02` active, tạo query/cursor reproducer và contract evidence; không đổi/reuse stable IDs.

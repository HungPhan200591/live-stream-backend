# Security Interview Question Bank — Access, Refresh and Session Semantics

> Status: `DRAFT`<br>
> Domain owner: `security / identity / session`<br>
> Active slice: `NONE`; preview target `SEC-01 — access-token versus refresh-token confusion`<br>
> Related roadmap: [Stage 0 and SEC-01](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-0---stabilize-the-laboratory)<br>
> Related case: [SEC-01](../../../cases/sec-01-access-vs-refresh-token.md)<br>
> Related depth rubric: [Security and identity](../../../knowledge-depth-rubric.md#38-security-và-identity--p0-target-d3)<br>
> Related theory: [Core theory](../theory/core/token-purpose-and-session-semantics.md)<br>
> Standards snapshot checked: `2026-07-25`<br>
> Updated: `2026-07-26`

Question bank này chuẩn bị trước cho case `SEC-01` đang `PAUSED`. Nó không re-activate case, không sửa JWT code và không chứng minh security invariant. Custom session-backed JWT của project được so sánh với OAuth concepts, không được gọi là một OAuth authorization server hoàn chỉnh. Mọi evidence giữ `NOT RUN`/`NOT CREATED`.

## Primary standards

- [RFC 6749 — OAuth 2.0 access and refresh tokens](https://www.rfc-editor.org/rfc/rfc6749.html)
- [RFC 7519 — JSON Web Token](https://www.rfc-editor.org/rfc/rfc7519.html)
- [RFC 8725 — JWT Best Current Practices](https://www.rfc-editor.org/rfc/rfc8725.html)
- [RFC 9700 — OAuth 2.0 Security Best Current Practice](https://www.rfc-editor.org/rfc/rfc9700.html)

## Level rubric

| Level | Trọng tâm |
| --- | --- |
| `FOUNDATION` | Token purpose, JWT validation layers và session/revocation vocabulary |
| `SENIOR` | Mutually exclusive validation, negative tests, rotation/reuse và cache consistency |
| `ARCHITECT` | Client threat model, key/token evolution, scale và availability/security trade-off |
| `EXPERT` | Concurrent refresh/logout invariants và cross-service token confusion |

## Coverage

| Topic | Foundation | Senior | Architect | Expert | Theory source |
| --- | ---: | ---: | ---: | ---: | --- |
| Access/refresh purpose và JWT validation | 2 | 2 | 0 | 1 | `PLANNED` |
| Session, revocation và refresh lifecycle | 1 | 2 | 1 | 1 | `PLANNED` |
| Client/key/scale architecture | 0 | 0 | 2 | 0 | `PLANNED` |
| **Tổng** | **3** | **4** | **3** | **2** | 12 questions |

## Questions

### SEC-TOKEN-001 — `FOUNDATION`

**Question:**

Access token và refresh token khác nhau về purpose, recipient, lifetime và exposure như thế nào? Vì sao refresh token không được gửi tới resource endpoint?

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có hiểu credential boundary thay vì chỉ nói “refresh sống lâu hơn”.

**Answer outline:**

1. Access token dùng truy cập protected resource; resource server validate scope/audience/time.
2. Refresh token chỉ dùng với authorization/token endpoint để lấy token mới, thường có lifetime dài và giá trị chiếm quyền lớn hơn.
3. Purpose-specific recipient/validation làm refresh token bị từ chối trên access path dù signature đúng.

**Required trade-offs:**

- Access lifetime ngắn giảm exposure nhưng tăng refresh frequency và dependency vào session/token service.

**Follow-up ladder:**

- Foundation: JWT có bắt buộc cho hai token không?
- Senior: Sai purpose nên trả `401` hay `403`?
- Architect: Opaque refresh token đổi operational model thế nào?
- Expert: Sender-constrained token giảm replay nhưng thêm key lifecycle gì?

**Red flags:**

- “Hai token chỉ khác TTL”; cho refresh token vào Bearer header của resource request.

**Evidence:**

- Theory: [Core](../theory/core/token-purpose-and-session-semantics.md)
- Deep-dive: `NOT CREATED`
- Learning case: [SEC-01](../../../cases/sec-01-access-vs-refresh-token.md)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SEC-TOKEN-002 — `FOUNDATION`

**Question:**

JWT signature hợp lệ và chưa hết hạn đã đủ để authenticate/authorize chưa? Hãy tách cryptographic, temporal, semantic và authorization validation.

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có layered validation và fail-closed reasoning.

**Answer outline:**

1. Verify allowed algorithm/key/signature; validate `exp`, `nbf` và bounded clock skew.
2. Validate issuer, audience, subject, purpose/type và required claims theo đúng token profile.
3. Sau authentication còn role/scope/ownership/business authorization và session status nếu contract stateful.

**Required trade-offs:**

- Local JWT verification nhanh nhưng revocation/session semantics yêu cầu state hoặc short lifetime.

**Follow-up ladder:**

- Foundation: `aud` bảo vệ gì?
- Senior: `typ` header và application-purpose claim khác nhau thế nào?
- Architect: Separate keys có thay mutually exclusive rules không?
- Expert: Algorithm/key confusion bị chặn ở đâu?

**Red flags:**

- “Signature đúng là tin mọi claim”; dùng role claim thay ownership check.

**Evidence:**

- Theory: [Core](../theory/core/token-purpose-and-session-semantics.md)
- Deep-dive: `NOT CREATED`
- Learning case: [SEC-01](../../../cases/sec-01-access-vs-refresh-token.md)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SEC-TOKEN-003 — `FOUNDATION`

**Question:**

“Session-backed JWT” có nghĩa gì? PostgreSQL session, Redis cache và JWT mỗi thành phần sở hữu sự thật nào?

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có hiểu hybrid stateful model và source-of-truth hierarchy.

**Answer outline:**

1. JWT mang signed claims nhưng refresh/revocation phụ thuộc session record; mô hình không hoàn toàn stateless.
2. PostgreSQL sở hữu durable session state; Redis là cache/acceleration có thể stale; token là credential snapshot có expiry.
3. Revoked/expired durable session phải thắng cache hit và signature-valid token theo contract.

**Required trade-offs:**

- Session lookup tăng revocation control nhưng thêm latency, consistency và availability dependency.

**Follow-up ladder:**

- Foundation: Cache miss xử lý thế nào?
- Senior: Cache hit stale nguy hiểm ra sao?
- Architect: Access request có cần session lookup mỗi lần không?
- Expert: Revocation latency invariant được định nghĩa thế nào?

**Red flags:**

- Gọi hệ thống stateless chỉ vì token là JWT; coi Redis là durable authority.

**Evidence:**

- Theory: [Core](../theory/core/token-purpose-and-session-semantics.md)
- Deep-dive: `NOT CREATED`
- Learning case: [SEC-01](../../../cases/sec-01-access-vs-refresh-token.md)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SEC-TOKEN-004 — `SENIOR`

**Question:**

Thiết kế mutually exclusive validation rules để access path luôn từ chối refresh token và refresh path luôn từ chối access token như thế nào?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có purpose-specific API và validation contract, không generic validator trả claims rồi caller tự nhớ.

**Answer outline:**

1. Định nghĩa required purpose/type, audience, issuer, claims và key policy riêng cho từng profile.
2. Validator nhận expected profile hoặc có hai entry point rõ; fail-closed khi claim thiếu/unknown.
3. Filter access chỉ gọi access validator; refresh service chỉ gọi refresh validator và active-session check.
4. Negative tests cả hai hướng, token thiếu type và tampered claim.

**Required trade-offs:**

- Separate keys/audiences tăng isolation nhưng tăng rotation/routing complexity; vẫn cần explicit validation.

**Follow-up ladder:**

- Foundation: Vì sao TTL không đủ phân loại?
- Senior: Check purpose trước hay sau signature?
- Architect: Rollout token profile mới không làm mixed fleet fail thế nào?
- Expert: Multiple issuers tạo validator confusion ra sao?

**Red flags:**

- Decode claim trước khi verify; mọi path dùng cùng generic `isValid(token)`.

**Evidence:**

- Theory: [Core](../theory/core/token-purpose-and-session-semantics.md)
- Deep-dive: `NOT CREATED`
- Learning case: [SEC-01](../../../cases/sec-01-access-vs-refresh-token.md)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SEC-TOKEN-005 — `SENIOR`

**Question:**

Bạn thiết kế refresh-token rotation và reuse detection thế nào? Hai refresh request đồng thời dùng cùng token phải cho kết quả gì?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có token-family state, atomic consume và replay response.

**Answer outline:**

1. Mỗi refresh token có identifier/family/generation gắn session; rotation phát token mới và vô hiệu token cũ atomically.
2. Concurrent consume dùng conditional update/lock để chỉ một request thắng; request còn lại là reuse/replay, không phát thêm valid branch.
3. Reuse policy có thể revoke family/session và emit security signal; tránh log raw token.
4. RFC 9700 yêu cầu public-client refresh tokens sender-constrained hoặc rotation.

**Required trade-offs:**

- Strict reuse revocation chống theft nhưng có thể logout user do benign retry/network race; cần idempotency/grace policy được threat-model.

**Follow-up ladder:**

- Foundation: Rotation khác reissue cùng token thế nào?
- Senior: Atomicity nằm ở DB hay cache?
- Architect: Multi-device family model ra sao?
- Expert: Grace window không mở replay window thế nào?

**Red flags:**

- Token cũ còn dùng vô hạn; hai refresh đồng thời đều tạo independent valid families.

**Evidence:**

- Theory: [Core](../theory/core/token-purpose-and-session-semantics.md)
- Deep-dive: `NOT CREATED`
- Learning case: [SEC-01](../../../cases/sec-01-access-vs-refresh-token.md)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SEC-TOKEN-006 — `SENIOR`

**Question:**

Refresh validation đọc Redis trước rồi fallback PostgreSQL. Làm sao bảo đảm revoked/expired session không được chấp nhận qua stale cache?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có consistency invariant và failure policy, không chỉ cache-aside happy path.

**Answer outline:**

1. Durable session là authority; revoke phải update DB và invalidate/update mọi cache key/index liên quan.
2. Cache entry có bounded TTL/version/revocation semantics; security-sensitive hit không được sống lâu hơn revocation SLA.
3. Test revoke + stale hit, cache miss, Redis down và DB down; fail-open/closed phải explicit theo operation.

**Required trade-offs:**

- DB check mỗi refresh đơn giản correctness nhưng tăng load; cache giảm load nhưng thêm revocation-latency risk.

**Follow-up ladder:**

- Foundation: TTL có phải invalidation không?
- Senior: Logout-all tìm mọi session cache key thế nào?
- Architect: Revocation event giúp và thất bại ra sao?
- Expert: Network partition khiến security/availability trade-off thế nào?

**Red flags:**

- Cache hit luôn được tin; Redis down thì bỏ qua session validation.

**Evidence:**

- Theory: [Core](../theory/core/token-purpose-and-session-semantics.md)
- Deep-dive: `NOT CREATED`
- Learning case: [SEC-01](../../../cases/sec-01-access-vs-refresh-token.md)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SEC-TOKEN-007 — `SENIOR`

**Question:**

Thiết kế HTTP negative-test matrix và error/logging contract cho invalid, expired, wrong-purpose, revoked và insufficient-permission token.

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có phân biệt authentication failure với authorization failure và tránh oracle/leakage.

**Answer outline:**

1. Missing/malformed/bad signature/expired/wrong issuer-audience-purpose/revoked credential → authentication failure; authenticated nhưng thiếu quyền → authorization failure.
2. Assert status, stable error contract, filter/endpoint path và no side effect; test refresh-as-access và access-as-refresh.
3. Log event category/correlation/token hash or ID khi cần, không raw token/claims nhạy cảm; client message không tiết lộ verifier detail.

**Required trade-offs:**

- Chi tiết giúp client/debug nhưng có thể thành attacker oracle; internal telemetry và public error tách nhau.

**Follow-up ladder:**

- Foundation: `401` và `403` khác gì?
- Senior: Expired token có được parse claims để logout không?
- Architect: Security-event cardinality/rate limit thế nào?
- Expert: Timing differences giữa failure branches tạo side channel ra sao?

**Red flags:**

- Mọi lỗi trả `500`; log toàn JWT; chỉ test service method không qua filter chain.

**Evidence:**

- Theory: [Core](../theory/core/token-purpose-and-session-semantics.md)
- Deep-dive: `NOT CREATED`
- Learning case: [SEC-01](../../../cases/sec-01-access-vs-refresh-token.md)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SEC-TOKEN-008 — `ARCHITECT`

**Question:**

Chọn storage/transport cho access và refresh token trên browser, mobile và server client dựa trên threat model nào?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có phân tích XSS, CSRF, device compromise, replay và client capability.

**Answer outline:**

1. Browser memory/storage/cookie đổi XSS-vs-CSRF exposure; `HttpOnly`, `Secure`, `SameSite` và CSRF defense phải đi cùng flow.
2. Mobile dùng OS secure storage và redirect/PKCE model phù hợp; server client có confidential credential/key controls.
3. Refresh token được bảo vệ mạnh hơn, không gửi resource requests; access token ngắn hạn/audience-bound.
4. Custom first-party session model phải nêu giới hạn so với chuẩn authorization server/IdP.

**Required trade-offs:**

- Không có storage “an toàn tuyệt đối”; chọn theo attacker/client UX và recovery.

**Follow-up ladder:**

- Foundation: `HttpOnly` chống gì và không chống gì?
- Senior: Cookie refresh endpoint cần CSRF control nào?
- Architect: BFF thay browser token exposure thế nào?
- Expert: Sender constraint quản key trên browser/mobile ra sao?

**Red flags:**

- LocalStorage luôn an toàn; dùng cùng transport/policy cho mọi client.

**Evidence:**

- Theory: [Core](../theory/core/token-purpose-and-session-semantics.md)
- Deep-dive: `NOT CREATED`
- Learning case: [SEC-01](../../../cases/sec-01-access-vs-refresh-token.md)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SEC-TOKEN-009 — `ARCHITECT`

**Question:**

Thiết kế signing-key và token-profile rotation trong multi-instance deployment thế nào để old/new tokens có bounded compatibility mà không chấp nhận wrong-purpose token?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có key ID, verifier allowlist, issuance cutover và retirement evidence.

**Answer outline:**

1. Publish/distribute trusted keys với `kid` allowlist; verifier hỗ trợ old/new key trong bounded window, issuer chỉ dùng new key sau readiness.
2. Token profile version/type/audience rules vẫn mutually exclusive; key overlap không cho phép semantic overlap.
3. Quan sát token population/error, retire old key sau max lifetime + skew/revocation policy; emergency rotation có kill path.

**Required trade-offs:**

- Overlap giữ availability nhưng kéo dài compromised-key exposure; hard cutover tăng outage/login risk.

**Follow-up ladder:**

- Foundation: `kid` có được tin để fetch URL tùy ý không?
- Senior: Rotation order issuer/verifier là gì?
- Architect: Symmetric và asymmetric keys khác blast radius ra sao?
- Expert: Stale JWKS cache xử lý thế nào?

**Red flags:**

- `kid` chọn filesystem/URL không allowlist; xóa old key trước khi token hết hạn.

**Evidence:**

- Theory: [Core](../theory/core/token-purpose-and-session-semantics.md)
- Deep-dive: `NOT CREATED`
- Learning case: [SEC-01](../../../cases/sec-01-access-vs-refresh-token.md)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SEC-TOKEN-010 — `ARCHITECT`

**Question:**

Ở quy mô lớn, bạn cân bằng local access-token verification, centralized introspection/session lookup, cache và revocation latency như thế nào?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có capacity/failure/security model thay vì “JWT để khỏi gọi DB”.

**Answer outline:**

1. Local verification tối ưu latency/availability nhưng revocation chậm theo expiry; lookup/introspection tăng immediate control nhưng tạo dependency/saturation risk.
2. Chọn theo endpoint risk: short-lived audience-bound access token, stateful refresh; high-risk action có thể step-up/session check.
3. Cache có TTL/version/invalidation; đặt revocation SLO, capacity, timeout và fail policy; đo hit/miss/stale/error.

**Required trade-offs:**

- Fail-open giữ availability nhưng có thể vi phạm security invariant; fail-closed bảo vệ trust nhưng khuếch đại auth outage.

**Follow-up ladder:**

- Foundation: Short TTL giới hạn gì?
- Senior: Introspection cache key/value gồm gì?
- Architect: Token service outage budget liên hệ app SLO ra sao?
- Expert: Regional partition và logout-global semantics thế nào?

**Red flags:**

- Không định nghĩa revocation latency; Redis là single source of truth.

**Evidence:**

- Theory: [Core](../theory/core/token-purpose-and-session-semantics.md)
- Deep-dive: `NOT CREATED`
- Learning case: [SEC-01](../../../cases/sec-01-access-vs-refresh-token.md)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SEC-TOKEN-011 — `EXPERT`

**Question:**

Refresh, logout và logout-all chạy đồng thời. Hãy phát biểu formal invariant và thiết kế transaction/cache ordering để không có token hợp lệ được sinh sau revocation boundary.

**Target depth:** `D4`

**Interviewer evaluates:**

- Có linearization point, conditional state transition và adversarial interleaving.

**Answer outline:**

1. Invariant: sau committed revocation version/time, không refresh operation quan sát state cũ được commit token generation mới.
2. Consume/rotate refresh và revoke dùng conditional update/lock trên session/family version; issuance chỉ sau successful atomic transition.
3. DB commit là authority; cache invalidation/update sau commit với version để stale entry không override newer state.
4. Deterministic barrier test các interleaving refresh-before/after revoke, duplicate refresh và invalidation failure.

**Required trade-offs:**

- Strong serialization đơn giản invariant nhưng tăng contention; optimistic version cần retry semantics an toàn.

**Follow-up ladder:**

- Foundation: Linearization point là gì?
- Senior: Token được ký trước DB commit có crash window nào?
- Architect: Logout-all khóa theo user hay sessions?
- Expert: Multi-region replication lag phá invariant thế nào?

**Red flags:**

- Chỉ “xóa cache rồi update DB”; test bằng sleep không ép interleaving.

**Evidence:**

- Theory: [Core](../theory/core/token-purpose-and-session-semantics.md)
- Deep-dive: `NOT CREATED`
- Learning case: [SEC-01](../../../cases/sec-01-access-vs-refresh-token.md)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SEC-TOKEN-012 — `EXPERT`

**Question:**

Nhiều services/issuers dùng JWT khác mục đích: access, refresh, ID token, email-action và service token. Làm sao ngăn cross-JWT confusion kể cả khi một số token dùng chung key?

**Target depth:** `D4`

**Interviewer evaluates:**

- Có mutually exclusive profiles, trust-domain routing và malicious-header defense.

**Answer outline:**

1. Mỗi token kind có explicit type, issuer/audience, required claims, allowed algorithm/key set và dedicated validator.
2. Validation rules mutually exclusive; wrong kind bị reject dù signature hợp lệ. Tách keys/issuers tăng defense nhưng không thay semantic checks.
3. `kid`, `jku`, `x5u` không được dẫn lookup tùy ý; allowlist issuer/key source và chống SSRF/injection.
4. Contract tests token từ issuer/kind A qua mọi validator B, key rotation/malformed headers và mixed fleet.

**Required trade-offs:**

- Shared infrastructure giảm operational cost nhưng tăng confusion blast radius; isolated trust domains tăng key/discovery complexity.

**Follow-up ladder:**

- Foundation: `aud` và `typ` giải quyết hai chiều nào?
- Senior: ID token có dùng gọi API được không?
- Architect: Central gateway validation có thay service validation không?
- Expert: Issuer compromise containment theo audiences/keys ra sao?

**Red flags:**

- Một validator cho mọi JWT; fetch JWKS từ URL trong untrusted header.

**Evidence:**

- Theory: [Core](../theory/core/token-purpose-and-session-semantics.md)
- Deep-dive: `NOT CREATED`
- Learning case: [SEC-01](../../../cases/sec-01-access-vs-refresh-token.md)
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `SEC-01` được re-activate:

1. Tạo theory/deep-dive rồi thay marker `NOT CREATED` bằng canonical links; giữ OAuth standard và custom project contract tách bạch.
2. Nối từng câu với actual validator/filter/service path và negative HTTP tests; không điền evidence từ case design chưa chạy.
3. Giữ scope token-purpose confusion; logout-all cache invalidation thuộc `SEC-02`, URL matcher thuộc `SEC-06`.
4. Giữ stable IDs; deprecated question phải trỏ replacement.

# Security Interview Question Bank — OAuth2, OIDC, Resource Server and Token Lifecycle

> Status: `DRAFT`<br>
> Domain owner: `Security/Identity`<br>
> Active slice: `NONE`; preview target: `SEC-04`<br>
> Related roadmap: [Stage 7](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-7---realtime-security-và-abuse-resistance)<br>
> Related depth rubric: [Security](../../../knowledge-depth-rubric.md#38-security-và-identity--p0-target-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/security/theory/core/oauth2-oidc-resource-server-and-token-lifecycle.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `SEC-04`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `SEC-OIDC-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### SEC-OIDC-001 — `FOUNDATION`
**Question:** Authentication, authorization, delegation và federation khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Identity proof, permission decision và third-party trust.<br>
**Answer outline:** Authentication xác minh subject; authorization quyết định action; delegation cho client hành động trong scope; federation tin identity từ domain/IdP khác.<br>
**Required trade-offs:** Federation/delegation giảm credential sharing nhưng tăng trust/config complexity.<br>
**Follow-up ladder:** SSO nằm ở đâu? Impersonation?<br>
**Red flags:** OAuth2 là authentication protocol.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-002 — `FOUNDATION`
**Question:** OAuth2 và OpenID Connect giải quyết bài toán khác nhau ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Authorization framework vs identity layer.<br>
**Answer outline:** OAuth2 cấp quyền truy cập resource bằng access token; OIDC thêm ID token/UserInfo và authentication semantics. API resource server validate access token, không dùng ID token làm API credential.<br>
**Required trade-offs:** OIDC chuẩn hóa identity nhưng không thay app authorization.<br>
**Follow-up ladder:** Scopes vs roles? Audience?<br>
**Red flags:** Có JWT là đã dùng OIDC.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-003 — `FOUNDATION`
**Question:** Authorization Code + PKCE flow gồm những actor/bước nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Browser redirect, code exchange và verifier binding.<br>
**Answer outline:** Client tạo verifier/challenge, redirect authorization endpoint; user auth/consent; code quay lại; token endpoint chỉ đổi code khi verifier đúng. State chống CSRF, nonce bảo vệ OIDC replay/context.<br>
**Required trade-offs:** Redirect flow an toàn hơn password grant nhưng callback/secret storage vẫn cần hardening.<br>
**Follow-up ladder:** Public vs confidential client?<br>
**Red flags:** PKCE mã hóa access token.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-004 — `FOUNDATION`
**Question:** Access token, refresh token và ID token có audience/lifetime/use khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Token confusion prevention.<br>
**Answer outline:** Access token ngắn hạn cho resource audience/scope; refresh token chỉ token endpoint và rotation/revocation; ID token cho client xác nhận authentication, không gửi API tùy tiện.<br>
**Required trade-offs:** Token ngắn giảm exposure nhưng tăng refresh/availability load.<br>
**Follow-up ladder:** Opaque token? Token exchange?<br>
**Red flags:** Ba token hoán đổi được vì đều JWT.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-005 — `SENIOR`
**Question:** Resource server validate JWT/opaque token và key rotation thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Issuer, audience, signature, time, scope và JWKS lifecycle.<br>
**Answer outline:** Validate alg allowlist/signature/iss/aud/exp/nbf và authorization claim; cache JWKS có refresh/backoff, chấp nhận overlap kid trong rotation; opaque token introspect với timeout/cache policy.<br>
**Required trade-offs:** Offline JWT availability cao nhưng revocation chậm; introspection fresh hơn nhưng thêm dependency.<br>
**Follow-up ladder:** Clock skew? Unknown kid storm?<br>
**Red flags:** Decode payload và kiểm tra exp là đủ.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-006 — `SENIOR`
**Question:** Refresh-token rotation phát hiện replay và xử lý token family thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** One-time rotation, reuse detection và session binding.<br>
**Answer outline:** Mỗi refresh đổi token, lưu hash/family/status; token cũ dùng lại đánh dấu compromise và revoke family/session theo policy; transaction/unique constraint xử lý concurrent refresh.<br>
**Required trade-offs:** Strict reuse detection tăng security nhưng multi-tab/network retry có false compromise.<br>
**Follow-up ladder:** Grace window? Device sessions?<br>
**Red flags:** Chỉ đổi JWT string không cần server state.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-007 — `SENIOR`
**Question:** Custom session-backed JWT hiện tại nên giữ hay chuyển authorization server/IdP theo tiêu chí nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Build-vs-buy, standards, threat/ops burden.<br>
**Answer outline:** Giữ khi scope nội bộ hẹp và team sở hữu security lifecycle; dùng mature IdP khi federation/MFA/SSO/clients/compliance/key ops tăng. Lập gap/threat/cost/migration, không tự xây IdP để học keyword.<br>
**Required trade-offs:** Control/customization vs vulnerability/operation cost.<br>
**Follow-up ladder:** Spring Authorization Server? Migration coexistence?<br>
**Red flags:** JWT custom hiện chạy nên luôn rẻ hơn IdP.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-008 — `SENIOR`
**Question:** Logout, revocation và access-token compromise có thể bảo đảm gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Short-lived bearer semantics và server session checks.<br>
**Answer outline:** Revoke refresh/session ngăn token mới; access token stateless còn hiệu lực tới expiry trừ denylist/introspection/session check. Chọn per-risk endpoint và incident key rotation.<br>
**Required trade-offs:** Immediate revocation tăng lookup/availability coupling.<br>
**Follow-up ladder:** Logout all? Signing-key compromise?<br>
**Red flags:** Xóa token phía browser vô hiệu token đã copy.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-009 — `ARCHITECT`
**Question:** Thiết kế identity trust boundary cho nhiều web/mobile/service clients thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Issuer/client separation, scopes, service identity và tenant.<br>
**Answer outline:** Central policy/issuer governance, distinct client registrations/redirects/audiences, least scopes, workload identity cho service, key/secret rotation, audit và break-glass; app giữ domain authorization.<br>
**Required trade-offs:** Central identity consistency vs blast radius/vendor dependency.<br>
**Follow-up ladder:** Multi-tenant issuer? Token exchange?<br>
**Red flags:** Một audience và admin scope cho mọi service.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-010 — `EXPERT`
**Question:** Điều hành signing-key compromise mà vẫn duy trì dịch vụ thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Containment, rotation overlap, token invalidation và communication.<br>
**Answer outline:** Xác định kid/issuance window, stop issuer/use, rotate/promote keys, revoke sessions/force reauth theo risk, update caches/verifiers, monitor replay và audit; rehearse rollback nếu new key lỗi.<br>
**Required trade-offs:** Fast invalidation ảnh hưởng toàn user/availability; overlap kéo exposure.<br>
**Follow-up ladder:** Offline clients? Refresh tokens signed/encrypted?<br>
**Red flags:** Chỉ xóa private key cũ là incident kết thúc.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `SEC-04` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.


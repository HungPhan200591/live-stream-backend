# Security Interview Question Bank — Application Abuse, OWASP Risks and Supply Chain

> Status: `DRAFT`<br>
> Domain owner: `Application Security`<br>
> Active slice: `NONE`; preview target: `SEC-04`<br>
> Related roadmap: [Stage 7](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-7---realtime-security-và-abuse-resistance)<br>
> Related depth rubric: [Security](../../../knowledge-depth-rubric.md#38-security-và-identity--p0-target-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/security/theory/core/application-security-abuse-and-supply-chain.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `SEC-04`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `SEC-APP-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### SEC-APP-001 — `FOUNDATION`
**Question:** Broken object-level và function-level authorization khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Ownership on resource vs permission on operation.<br>
**Answer outline:** BOLA cho phép truy cập object người khác qua ID; BFLA gọi function/role bị cấm. URL rules chỉ coarse; service/method checks ownership/role và negative tests.<br>
**Required trade-offs:** Concealment 404 giảm enumeration nhưng logging/audit vẫn cần.<br>
**Follow-up ladder:** Mass assignment liên quan gì?<br>
**Red flags:** Authenticated user được phép mọi object có ID.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-002 — `FOUNDATION`
**Question:** CORS, CSRF và SameSite giải quyết các threat khác nhau ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Browser origin/read policy vs credentialed request forgery.<br>
**Answer outline:** CORS kiểm soát browser đọc/gửi cross-origin theo policy, không auth; CSRF lợi dụng automatic credentials, chống bằng SameSite/token/origin; bearer header không tự gửi khác cookie.<br>
**Required trade-offs:** Strict policy tăng security nhưng ảnh hưởng integrations/legacy browser.<br>
**Follow-up ladder:** Preflight? WebSocket origin?<br>
**Red flags:** CORS chặn curl/attacker server.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-003 — `FOUNDATION`
**Question:** Injection, SSRF và unsafe deserialization khác nhau ở trust boundary nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Untrusted data thành code/query, outbound target hoặc object graph.<br>
**Answer outline:** Bind SQL/query parameters; allowlist outbound scheme/host/IP và chặn metadata/private ranges; DTO/type allowlist, không native deserialize object không tin cậy.<br>
**Required trade-offs:** Validation chặt giảm flexibility; proxy/eDNS rebinding cần network controls.<br>
**Follow-up ladder:** Template injection? Redirect SSRF?<br>
**Red flags:** Regex sanitize mọi input là đủ.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-004 — `FOUNDATION`
**Question:** Password hashing khác encryption; migration hash algorithm ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** One-way adaptive KDF, salt và rehash.<br>
**Answer outline:** Dùng Argon2/bcrypt/PBKDF2 parameters phù hợp, unique salt; verify rồi rehash khi login hoặc staged reset. Pepper nếu dùng phải quản lý như secret.<br>
**Required trade-offs:** Cost cao chống cracking nhưng tăng login DoS/CPU.<br>
**Follow-up ladder:** Account enumeration? Breached-password check?<br>
**Red flags:** SHA-256 nhiều vòng thủ công đủ.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-005 — `SENIOR`
**Question:** Thiết kế login/recovery/MFA chống brute force và credential stuffing thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Layered abuse controls và recovery as auth path.<br>
**Answer outline:** Generic responses, per-account/IP/device adaptive rate, breached credential signal, MFA/risk step-up, secure recovery token/session invalidation, audit; tránh lockout dễ DoS.<br>
**Required trade-offs:** Friction/false positives vs account takeover risk.<br>
**Follow-up ladder:** CAPTCHA? Password spray?<br>
**Red flags:** Khóa tài khoản vô hạn sau 3 lần sai.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-006 — `SENIOR`
**Question:** Mass assignment/property binding trong Spring phát sinh và phòng ngừa thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** DTO allowlist và server-owned fields.<br>
**Answer outline:** Binding request trực tiếp entity/domain cho attacker set role/owner/status; dùng request DTO explicit, mapper allowlist, service derives owner/role, authorization/invariant test.<br>
**Required trade-offs:** DTO thêm code nhưng giữ API/domain/security boundary.<br>
**Follow-up ladder:** PATCH semantics? Jackson unknown fields?<br>
**Red flags:** Ẩn field khỏi OpenAPI là đã an toàn.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-007 — `SENIOR`
**Question:** Secret lifecycle từ local, CI tới production cần controls gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Generation, storage, access, rotation và redaction.<br>
**Answer outline:** Không commit/default secret; secret manager/scoped identity, short-lived credentials, audit access, rotation overlap/revoke, scan repo/build/log; test fail-fast production config.<br>
**Required trade-offs:** Managed secret tăng dependency/cost nhưng giảm sprawl.<br>
**Follow-up ladder:** Envelope encryption? Break glass?<br>
**Red flags:** Environment variable tự động an toàn.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-008 — `SENIOR`
**Question:** SBOM/CVE finding được triage và remediation thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Reachability, exploitability, severity, ownership và provenance.<br>
**Answer outline:** Inventory exact artifact/transitive version, verify advisory/reachability/exposure, patch/mitigate or time-bound accept with owner; trusted repositories/checksum/signing/provenance and regression rollout.<br>
**Required trade-offs:** Update nhanh giảm exposure nhưng có compatibility risk.<br>
**Follow-up ladder:** False positive? Malicious package?<br>
**Red flags:** CVSS thấp luôn bỏ qua; scan pass nghĩa supply chain an toàn.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-009 — `ARCHITECT`
**Question:** Threat-model live-stream platform theo asset/trust boundary/attacker story thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Systematic abuse and security design.<br>
**Answer outline:** Map tokens/keys/wallet/chat/admin, browser-app-broker-media boundaries, attackers/capabilities; enumerate authz/replay/exhaustion/supply-chain paths, rank impact/likelihood and link controls/detection/recovery owners.<br>
**Required trade-offs:** Threat modeling tốn workshop time nhưng ưu tiên đúng controls.<br>
**Follow-up ladder:** STRIDE/abuse cases? Residual risk?<br>
**Red flags:** Liệt kê OWASP Top 10 mà không gắn data flow.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-010 — `EXPERT`
**Question:** Điều hành credential stuffing kết hợp cache/broker degradation thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Cross-layer abuse saturation và safe containment.<br>
**Answer outline:** Correlate auth failures/device/IP, Redis limiter health, DB/hash CPU, queues; enforce layered local/gateway limits, degrade recovery safely, rotate exposed secrets/sessions, preserve forensics và avoid account enumeration.<br>
**Required trade-offs:** Fail-closed bảo vệ account nhưng có availability/DoS trade-off.<br>
**Follow-up ladder:** Botnet distributed IP? Customer communication?<br>
**Red flags:** Tăng bcrypt cost trong incident để chặn bot.<br>
**Evidence:** Theory `NOT CREATED`; case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `SEC-04` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.


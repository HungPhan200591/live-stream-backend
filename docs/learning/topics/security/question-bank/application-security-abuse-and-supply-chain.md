# Ngân hàng câu hỏi phỏng vấn Security — lạm dụng ứng dụng, rủi ro OWASP và chuỗi cung ứng

> Status: `DRAFT`<br>
> Domain owner: `Application Security`<br>
> Active slice: `NONE`; preview target: `SEC-04`<br>
> Related roadmap: [Stage 7](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-7---realtime-security-và-abuse-resistance)<br>
> Related depth rubric: [Security](../../../knowledge-depth-rubric.md#38-security-và-identity--p0-target-d3)<br>
> Related theory: [Core theory](../theory/core/application-security-abuse-and-supply-chain.md); [Deep-dive](../theory/deep-dives/abuse-resistance-supply-chain-and-security-testing.md)<br>
> Updated: `2026-07-26`

Bản xem trước; không kích hoạt hoặc triển khai `SEC-04`. Khả năng xuất hiện chỉ là ước lượng. Mọi câu vẫn `UNANSWERED`, kiểm thử `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

Lượt đầu học `SEC-APP-001..006`; câu hỏi tiếp cho Senior `007..008`; câu mở rộng `009..010`.

## Questions

### SEC-APP-001 — `FOUNDATION`
**Question:** Broken object-level và function-level authorization khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Phân biệt quyền sở hữu resource với quyền thực hiện operation.<br>
**Answer outline:** BOLA cho phép truy cập object của người khác qua ID; BFLA cho gọi chức năng bị cấm theo role. Rule URL chỉ là cổng thô; service/method phải kiểm ownership/role và có negative test.<br>
**Required trade-offs:** Concealment 404 giảm enumeration nhưng logging/audit vẫn cần.<br>
**Follow-up ladder:** Mass assignment liên quan gì?<br>
**Red flags:** Authenticated user được phép mọi object có ID.<br>
**Evidence:** Theory [Core](../theory/core/application-security-abuse-and-supply-chain.md); Deep-dive [Advanced](../theory/deep-dives/abuse-resistance-supply-chain-and-security-testing.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-002 — `FOUNDATION`
**Question:** CORS, CSRF và SameSite giải quyết các threat khác nhau ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Phân biệt chính sách origin của browser với giả mạo request mang credential.<br>
**Answer outline:** CORS kiểm soát việc browser đọc/gửi cross-origin, không phải authentication. CSRF lợi dụng credential tự gửi; chống bằng SameSite, CSRF token và kiểm origin. Bearer header khác cookie ở chỗ browser không tự gắn vào request khác origin.<br>
**Required trade-offs:** Policy nghiêm tăng an toàn nhưng có thể ảnh hưởng integration và browser cũ.<br>
**Follow-up ladder:** Preflight? WebSocket origin?<br>
**Red flags:** Cho rằng CORS chặn được curl hoặc server của attacker.<br>
**Evidence:** Theory [Core](../theory/core/application-security-abuse-and-supply-chain.md); Deep-dive [Advanced](../theory/deep-dives/abuse-resistance-supply-chain-and-security-testing.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-003 — `FOUNDATION`
**Question:** Injection, SSRF và unsafe deserialization khác nhau ở trust boundary nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Nhận ra dữ liệu không tin cậy có thể thành code/query, đích outbound hoặc object graph.<br>
**Answer outline:** Bind parameter cho SQL/query; allowlist scheme/host/IP outbound và chặn metadata/private range; dùng DTO/type allowlist, không deserialize native object từ input không tin cậy.<br>
**Required trade-offs:** Validation chặt giảm tính linh hoạt; proxy và DNS rebinding vẫn cần control ở network.<br>
**Follow-up ladder:** Template injection? Redirect SSRF?<br>
**Red flags:** Cho rằng một regex sanitize mọi input là đủ.<br>
**Evidence:** Theory [Core](../theory/core/application-security-abuse-and-supply-chain.md); Deep-dive [Advanced](../theory/deep-dives/abuse-resistance-supply-chain-and-security-testing.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-004 — `FOUNDATION`
**Question:** Password hashing khác encryption; migration hash algorithm ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Hiểu KDF một chiều có điều chỉnh chi phí, salt và rehash.<br>
**Answer outline:** Dùng Argon2/bcrypt/PBKDF2 với tham số phù hợp và salt riêng; verify rồi rehash khi login hoặc reset theo giai đoạn. Pepper nếu có phải quản lý như secret.<br>
**Required trade-offs:** Chi phí băm cao chống cracking tốt hơn nhưng tăng CPU và nguy cơ login DoS.<br>
**Follow-up ladder:** Account enumeration? Breached-password check?<br>
**Red flags:** SHA-256 nhiều vòng thủ công đủ.<br>
**Evidence:** Theory [Core](../theory/core/application-security-abuse-and-supply-chain.md); Deep-dive [Advanced](../theory/deep-dives/abuse-resistance-supply-chain-and-security-testing.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-005 — `SENIOR`
**Question:** Thiết kế login/recovery/MFA chống brute force và credential stuffing thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Control chống lạm dụng nhiều lớp và coi recovery là một đường authentication.<br>
**Answer outline:** Response không lộ account, rate thích ứng theo account/IP/device, tín hiệu credential đã lộ, MFA/step-up theo rủi ro, recovery token an toàn, invalidation session và audit; tránh lockout dễ bị lợi dụng làm DoS.<br>
**Required trade-offs:** Ma sát và false positive đổi lấy giảm nguy cơ chiếm tài khoản.<br>
**Follow-up ladder:** CAPTCHA? Password spray?<br>
**Red flags:** Khóa tài khoản vô hạn sau 3 lần sai.<br>
**Evidence:** Theory [Core](../theory/core/application-security-abuse-and-supply-chain.md); Deep-dive [Advanced](../theory/deep-dives/abuse-resistance-supply-chain-and-security-testing.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-006 — `SENIOR`
**Question:** Mass assignment/property binding trong Spring phát sinh và phòng ngừa thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** DTO theo allowlist và field do server sở hữu.<br>
**Answer outline:** Bind request trực tiếp vào entity/domain cho attacker đặt role/owner/status. Dùng request DTO tường minh, mapper theo allowlist; service tự suy ra owner/role và test authorization/invariant.<br>
**Required trade-offs:** DTO làm tăng code nhưng giữ rõ ranh giới API, domain và security.<br>
**Follow-up ladder:** PATCH semantics? Jackson unknown fields?<br>
**Red flags:** Ẩn field khỏi OpenAPI là đã an toàn.<br>
**Evidence:** Theory [Core](../theory/core/application-security-abuse-and-supply-chain.md); Deep-dive [Advanced](../theory/deep-dives/abuse-resistance-supply-chain-and-security-testing.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-007 — `SENIOR`
**Question:** Secret lifecycle từ local, CI tới production cần controls gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Vòng đời sinh, lưu, truy cập, xoay và che secret.<br>
**Answer outline:** Không commit hoặc dùng secret mặc định; dùng secret manager và identity có scope, credential ngắn hạn, audit truy cập, overlap/revoke khi xoay, scan repository/build/log và test production config fail-fast.<br>
**Required trade-offs:** Secret được quản lý tăng dependency/chi phí nhưng giảm phát tán mất kiểm soát.<br>
**Follow-up ladder:** Envelope encryption? Break glass?<br>
**Red flags:** Cho rằng environment variable tự động an toàn.<br>
**Evidence:** Theory [Core](../theory/core/application-security-abuse-and-supply-chain.md); Deep-dive [Advanced](../theory/deep-dives/abuse-resistance-supply-chain-and-security-testing.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-008 — `SENIOR`
**Question:** SBOM/CVE finding được triage và remediation thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Khả năng đường lỗi thực sự reachable/exploitable, mức độ, owner và provenance.<br>
**Answer outline:** Kiểm kê đúng artifact và transitive version; xác minh advisory, reachability và exposure; vá, giảm thiểu hoặc chấp nhận có thời hạn và owner. Dùng repository đáng tin, checksum, signing, provenance và rollout có regression test.<br>
**Required trade-offs:** Update nhanh giảm thời gian lộ nhưng tăng rủi ro tương thích.<br>
**Follow-up ladder:** False positive? Malicious package?<br>
**Red flags:** Luôn bỏ qua CVSS thấp; coi scan pass là bằng chứng chuỗi cung ứng an toàn.<br>
**Evidence:** Theory [Core](../theory/core/application-security-abuse-and-supply-chain.md); Deep-dive [Advanced](../theory/deep-dives/abuse-resistance-supply-chain-and-security-testing.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-009 — `ARCHITECT`
**Question:** Threat-model live-stream platform theo asset/trust boundary/attacker story thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Khả năng thiết kế chống lạm dụng và bảo mật có hệ thống.<br>
**Answer outline:** Lập bản đồ token, key, wallet, chat, admin và ranh giới browser–app–broker–media; xác định attacker/capability; liệt kê đường authorization, replay, cạn tài nguyên và chuỗi cung ứng; xếp impact/likelihood rồi gắn control, detection, recovery cùng owner.<br>
**Required trade-offs:** Threat modeling tốn thời gian workshop nhưng giúp ưu tiên đúng control.<br>
**Follow-up ladder:** STRIDE/abuse cases? Residual risk?<br>
**Red flags:** Chỉ liệt kê OWASP Top 10 mà không gắn với luồng dữ liệu.<br>
**Evidence:** Theory [Core](../theory/core/application-security-abuse-and-supply-chain.md); Deep-dive [Advanced](../theory/deep-dives/abuse-resistance-supply-chain-and-security-testing.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-APP-010 — `EXPERT`
**Question:** Điều hành credential stuffing kết hợp cache/broker degradation thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Quá tải do lạm dụng xuyên tầng và cách khoanh vùng an toàn.<br>
**Answer outline:** Liên hệ lỗi auth với device/IP, sức khỏe Redis limiter, CPU cho DB/hash và queue; giới hạn nhiều lớp ở local/gateway, hạ cấp recovery an toàn, xoay secret/session đã lộ, giữ bằng chứng điều tra và tránh lộ account.<br>
**Required trade-offs:** Fail-closed bảo vệ account nhưng đánh đổi availability và có thể bị lợi dụng làm DoS.<br>
**Follow-up ladder:** Botnet phân tán IP thì sao? Thông báo khách hàng thế nào?<br>
**Red flags:** Tăng chi phí bcrypt ngay trong incident để chặn bot.<br>
**Evidence:** Theory [Core](../theory/core/application-security-abuse-and-supply-chain.md); Deep-dive [Advanced](../theory/deep-dives/abuse-resistance-supply-chain-and-security-testing.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `SEC-04` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.

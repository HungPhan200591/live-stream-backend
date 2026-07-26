# Ngân hàng câu hỏi phỏng vấn Security — OAuth2, OIDC, resource server và vòng đời token

> Status: `DRAFT`<br>
> Domain owner: `Security/Identity`<br>
> Active slice: `NONE`; preview target: `SEC-04`<br>
> Related roadmap: [Stage 7](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-7---realtime-security-và-abuse-resistance)<br>
> Related depth rubric: [Security](../../../knowledge-depth-rubric.md#38-security-và-identity--p0-target-d3)<br>
> Related theory: [Core theory](../theory/core/oauth2-oidc-resource-server-and-token-lifecycle.md); [Deep-dive](../theory/deep-dives/oauth2-oidc-pkce-resource-server-and-key-rotation.md)<br>
> Updated: `2026-07-26`

Bản xem trước; không kích hoạt hoặc triển khai `SEC-04`. Khả năng xuất hiện chỉ là ước lượng. Mọi câu vẫn `UNANSWERED`, kiểm thử `NOT RUN`.

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
**Interviewer evaluates:** Bằng chứng identity, quyết định quyền và trust với bên thứ ba.<br>
**Answer outline:** Authentication xác minh subject; authorization quyết định action; delegation cho client hành động trong scope; federation chấp nhận identity từ domain/IdP khác theo trust contract.<br>
**Required trade-offs:** Federation/delegation giảm chia sẻ credential nhưng tăng độ phức tạp của trust và configuration.<br>
**Follow-up ladder:** SSO nằm ở đâu? Impersonation?<br>
**Red flags:** OAuth2 là authentication protocol.<br>
**Evidence:** Theory [Core](../theory/core/oauth2-oidc-resource-server-and-token-lifecycle.md); Deep-dive [Advanced](../theory/deep-dives/oauth2-oidc-pkce-resource-server-and-key-rotation.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-002 — `FOUNDATION`
**Question:** OAuth2 và OpenID Connect giải quyết bài toán khác nhau ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Phân biệt framework ủy quyền với lớp identity.<br>
**Answer outline:** OAuth2 cấp quyền truy cập resource bằng access token; OIDC thêm ID token/UserInfo và ngữ nghĩa authentication. API resource server kiểm access token, không dùng ID token làm credential gọi API.<br>
**Required trade-offs:** OIDC chuẩn hóa identity nhưng không thay authorization nghiệp vụ của ứng dụng.<br>
**Follow-up ladder:** Scopes vs roles? Audience?<br>
**Red flags:** Có JWT là đã dùng OIDC.<br>
**Evidence:** Theory [Core](../theory/core/oauth2-oidc-resource-server-and-token-lifecycle.md); Deep-dive [Advanced](../theory/deep-dives/oauth2-oidc-pkce-resource-server-and-key-rotation.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-003 — `FOUNDATION`
**Question:** Authorization Code + PKCE flow gồm những actor/bước nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Redirect trên browser, đổi code và bind với verifier.<br>
**Answer outline:** Client tạo verifier/challenge rồi redirect tới authorization endpoint; user đăng nhập/đồng ý; code quay lại; token endpoint chỉ đổi code khi verifier đúng. State chống CSRF, nonce bind OIDC response với transaction và chống replay.<br>
**Required trade-offs:** Redirect flow an toàn hơn password grant nhưng callback và nơi lưu secret vẫn phải gia cố.<br>
**Follow-up ladder:** Public vs confidential client?<br>
**Red flags:** PKCE mã hóa access token.<br>
**Evidence:** Theory [Core](../theory/core/oauth2-oidc-resource-server-and-token-lifecycle.md); Deep-dive [Advanced](../theory/deep-dives/oauth2-oidc-pkce-resource-server-and-key-rotation.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-004 — `FOUNDATION`
**Question:** Access token, refresh token và ID token có audience/lifetime/use khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Cách ngăn dùng nhầm loại token.<br>
**Answer outline:** Access token ngắn hạn dành cho audience/scope của resource; refresh token chỉ đi tới token endpoint và cần rotation/revocation; ID token để client xác nhận đăng nhập, không gửi tùy tiện tới API.<br>
**Required trade-offs:** Token ngắn giảm thời gian lộ nhưng tăng tải refresh và phụ thuộc availability.<br>
**Follow-up ladder:** Opaque token? Token exchange?<br>
**Red flags:** Ba token hoán đổi được vì đều JWT.<br>
**Evidence:** Theory [Core](../theory/core/oauth2-oidc-resource-server-and-token-lifecycle.md); Deep-dive [Advanced](../theory/deep-dives/oauth2-oidc-pkce-resource-server-and-key-rotation.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-005 — `SENIOR`
**Question:** Resource server validate JWT/opaque token và key rotation thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Issuer, audience, chữ ký, thời gian, scope và vòng đời JWKS.<br>
**Answer outline:** Kiểm allowlist thuật toán, signature, `iss`, `aud`, `exp`, `nbf` và claim phân quyền; JWKS cache có refresh/backoff và chấp nhận overlap `kid` khi xoay; opaque token introspection cần timeout/cache policy.<br>
**Required trade-offs:** JWT verify offline có availability cao nhưng revoke chậm; introspection mới hơn nhưng thêm dependency.<br>
**Follow-up ladder:** Clock skew? Unknown kid storm?<br>
**Red flags:** Decode payload và kiểm tra exp là đủ.<br>
**Evidence:** Theory [Core](../theory/core/oauth2-oidc-resource-server-and-token-lifecycle.md); Deep-dive [Advanced](../theory/deep-dives/oauth2-oidc-pkce-resource-server-and-key-rotation.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-006 — `SENIOR`
**Question:** Refresh-token rotation phát hiện replay và xử lý token family thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Rotation dùng một lần, phát hiện reuse và bind với session.<br>
**Answer outline:** Mỗi refresh đổi token và lưu hash/family/status; dùng lại token cũ là tín hiệu compromise và revoke family/session theo policy; transaction/unique constraint xử lý refresh đồng thời.<br>
**Required trade-offs:** Phát hiện reuse nghiêm tăng an toàn nhưng nhiều tab hoặc retry do mạng có thể bị hiểu nhầm là compromise.<br>
**Follow-up ladder:** Grace window? Device sessions?<br>
**Red flags:** Chỉ đổi JWT string không cần server state.<br>
**Evidence:** Theory [Core](../theory/core/oauth2-oidc-resource-server-and-token-lifecycle.md); Deep-dive [Advanced](../theory/deep-dives/oauth2-oidc-pkce-resource-server-and-key-rotation.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-007 — `SENIOR`
**Question:** Custom session-backed JWT hiện tại nên giữ hay chuyển authorization server/IdP theo tiêu chí nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Quyết định tự xây hay mua, chuẩn áp dụng và gánh nặng bảo mật/vận hành.<br>
**Answer outline:** Có thể giữ giải pháp nội bộ khi scope hẹp và team sở hữu trọn vòng đời security; dùng IdP trưởng thành khi federation, MFA, SSO, số client, compliance và vận hành key tăng. Đánh giá gap, threat, cost và migration, không tự xây IdP chỉ để học keyword.<br>
**Required trade-offs:** Control/customization vs vulnerability/operation cost.<br>
**Follow-up ladder:** Spring Authorization Server? Migration coexistence?<br>
**Red flags:** JWT custom hiện chạy nên luôn rẻ hơn IdP.<br>
**Evidence:** Theory [Core](../theory/core/oauth2-oidc-resource-server-and-token-lifecycle.md); Deep-dive [Advanced](../theory/deep-dives/oauth2-oidc-pkce-resource-server-and-key-rotation.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-008 — `SENIOR`
**Question:** Logout, revocation và access-token compromise có thể bảo đảm gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Ngữ nghĩa bearer token ngắn hạn và bước kiểm session phía server.<br>
**Answer outline:** Revoke refresh/session ngăn phát token mới; access token stateless vẫn hợp lệ tới expiry trừ khi có denylist, introspection hoặc session check. Chọn theo rủi ro từng endpoint và có quy trình xoay key khi sự cố.<br>
**Required trade-offs:** Immediate revocation tăng lookup/availability coupling.<br>
**Follow-up ladder:** Logout all? Signing-key compromise?<br>
**Red flags:** Xóa token phía browser vô hiệu token đã copy.<br>
**Evidence:** Theory [Core](../theory/core/oauth2-oidc-resource-server-and-token-lifecycle.md); Deep-dive [Advanced](../theory/deep-dives/oauth2-oidc-pkce-resource-server-and-key-rotation.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-009 — `ARCHITECT`
**Question:** Thiết kế identity trust boundary cho nhiều web/mobile/service clients thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Tách issuer/client, scope, service identity và tenant.<br>
**Answer outline:** Governance tập trung cho policy/issuer; registration, redirect và audience riêng theo client; scope tối thiểu; workload identity cho service; xoay key/secret, audit và break-glass. Ứng dụng vẫn sở hữu domain authorization.<br>
**Required trade-offs:** Central identity consistency vs blast radius/vendor dependency.<br>
**Follow-up ladder:** Multi-tenant issuer? Token exchange?<br>
**Red flags:** Một audience và admin scope cho mọi service.<br>
**Evidence:** Theory [Core](../theory/core/oauth2-oidc-resource-server-and-token-lifecycle.md); Deep-dive [Advanced](../theory/deep-dives/oauth2-oidc-pkce-resource-server-and-key-rotation.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-OIDC-010 — `EXPERT`
**Question:** Điều hành signing-key compromise mà vẫn duy trì dịch vụ thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Khoanh vùng, overlap khi xoay, vô hiệu token và truyền thông sự cố.<br>
**Answer outline:** Xác định `kid` và cửa sổ phát token; dừng issuer/use bị ảnh hưởng; xoay/promote key; revoke session hoặc buộc đăng nhập lại theo rủi ro; cập nhật cache/verifier, theo dõi replay và audit; diễn tập rollback nếu key mới lỗi.<br>
**Required trade-offs:** Vô hiệu nhanh ảnh hưởng user/availability; overlap kéo dài thời gian lộ.<br>
**Follow-up ladder:** Offline clients? Refresh tokens signed/encrypted?<br>
**Red flags:** Chỉ xóa private key cũ là incident kết thúc.<br>
**Evidence:** Theory [Core](../theory/core/oauth2-oidc-resource-server-and-token-lifecycle.md); Deep-dive [Advanced](../theory/deep-dives/oauth2-oidc-pkce-resource-server-and-key-rotation.md); case `SEC-04 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `SEC-04` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.

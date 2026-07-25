# SEC-01: Access token vs refresh token confusion

> Status: `PAUSED`<br>
> Maturity target: `M2 - Correct`<br>
> Roadmap stage: `Stage 0 - Stabilize the laboratory`<br>
> Prerequisites: `CURRENT auth/session implementation`, [Security Flow](../../security/authorization-flow.md), [API Contract](../../contracts/api-contract.md)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-25`

> Pause reason: SEC-01 thuộc Stage 0 nhưng đứng sau `JDK-01`, `TEST-01`, `JDK-02`, `MIG-01` và `CFG-01`; các item này phải đóng hoặc được reschedule có reason/dependency/revisit point trong cursor trước khi re-activate. Không có implementation, reproducer hay experiment nào của SEC-01 đã chạy.

## Scope lock

Case này chỉ xử lý **token purpose confusion**: refresh token đi qua access-token authentication path hoặc access token đi qua refresh path. Không sửa code trong bước chuẩn bị này.

Các vấn đề `/api/auth/**` matcher quá rộng (`SEC-06`), logout-all với stale Redis cache, refresh-token rotation, stream-key/webhook, key rotation và production secret management thuộc case hoặc checkpoint khác. Chúng chỉ được nhắc khi cần làm rõ boundary, không được kéo vào vertical slice SEC-01.

## 1. Interview objective

### Câu hỏi chính

> Vì sao JWT có signature hợp lệ và chưa hết hạn vẫn có thể không hợp lệ cho một request cụ thể, và thiết kế validation thế nào để access token và refresh token không thể dùng thay nhau?

### Follow-up dự kiến

1. Signature validation, claims validation và authorization khác nhau thế nào?
2. Vì sao cùng signing key không tự động là lỗ hổng, nhưng làm tăng blast radius nếu thiếu purpose validation?
3. Nên biểu diễn token purpose bằng payload claim, audience, issuer hay tách signing key?
4. JOSE header `typ` khác gì application claim `token_type`/`token_use`?
5. Access token stateless còn refresh token session-backed ảnh hưởng revocation thế nào?
6. Token cũ không có purpose claim nên fail-open hay fail-closed?
7. Vì sao chỉ kiểm tra refresh token có `session_id` là một heuristic yếu?
8. Test negative path ở unit, filter và HTTP boundary ra sao?
9. Khi scale nhiều service, service nào được phép validate hoặc phát hành từng loại token?
10. Nếu rotate signing key hoặc đổi claim contract, rollout tương thích thế nào?

### Năng lực cần chứng minh

- **Theory:** phân biệt authentication, authorization, cryptographic validity, semantic validity và token lifecycle.
- **Implementation:** thiết kế purpose-specific token generation/validation và fail-closed boundary.
- **Measurement:** tái hiện được refresh-as-access trước khi sửa và chứng minh negative tests sau khi sửa.
- **Trade-off communication:** so sánh explicit claim, separate keys và opaque refresh token theo correctness, complexity và operability.

## 2. Problem và invariant

### Hành vi mong đợi

- Bearer token tại access authentication path chỉ được chấp nhận nếu là access token hợp lệ.
- `/api/auth/refresh` chỉ chấp nhận refresh token hợp lệ, có session binding hợp lệ.
- Token đúng signature/expiry nhưng sai purpose phải bị từ chối trước khi dựng principal hoặc truy cập session workflow.
- Authentication failure trả `401`; không biến thành anonymous success, `403` hoặc generic `500`.

### Invariant

1. `ACCESS` token là loại token duy nhất có thể tạo `Authentication` cho protected resource request.
2. `REFRESH` token chỉ dùng để xin access token mới và phải gắn với một active `UserSession`.
3. Token thiếu purpose claim, purpose không biết hoặc purpose không khớp validation path phải fail-closed.
4. Signature/expiry hợp lệ là điều kiện cần, không phải điều kiện đủ.
5. Không log raw token, signature, refresh-session proof hoặc claims nhạy cảm.

### Không nằm trong scope

- Sửa URL matcher của toàn bộ `/api/auth/**` (`SEC-06`).
- Giải quyết logout-all cache invalidation (`SEC-02`).
- Refresh-token rotation/reuse detection hoặc token family.
- Tách signing service, asymmetric key/JWKS hoặc key rotation production.
- Thay toàn bộ refresh token JWT bằng opaque token ngay trong case này.
- Hardening stream key (`SEC-03`) hoặc webhook (`SEC-05`).
- Thay đổi role/ownership business rules.

## 3. Theory notes bằng lời của tôi

> Đây là bản nháp do AI hỗ trợ. Trước khi đóng case, người học phải viết lại mental model bằng lời của mình và tự trả lời follow-up mà không đọc phần này.

### Mental model

JWT là một envelope có claims được ký. Xác minh signature chỉ chứng minh token do bên giữ key phát hành và payload chưa bị sửa; expiry chỉ chứng minh token còn trong time window. Hai kiểm tra này không trả lời token được phát hành **để làm gì**.

Access token là credential ngắn hạn được gửi tới resource server để dựng principal. Refresh token là credential dài hạn dùng với authorization/authentication service để phát hành access token mới. Trong project hiện tại, refresh token còn mang `session_id`, nên nó kết nối JWT với durable session state trong PostgreSQL. Nếu filter chỉ hỏi “token có chữ ký đúng và chưa hết hạn không?”, refresh token vẫn trả lời “có” và bị nâng sai quyền thành access credential.

Token purpose phải là một phần của security contract và được enforce ở từng boundary. Một claim chỉ có giá trị bảo mật khi validator kiểm tra nó. Việc refresh token có thêm `session_id` không thay thế purpose claim: schema có thể phát triển, access token tương lai cũng có session-related claim, và heuristic “có field X” dễ fail-open.

Trong case này, `token_type` là **payload claim ứng dụng** với giá trị `ACCESS` hoặc `REFRESH`. Nó không phải JOSE header `typ`, vốn thường mô tả media type như `JWT`. Validation nên được tách theo intent: `validateAccessToken` và `validateRefreshToken` hoặc một validator nhận expected purpose, thay vì generic boolean được mọi caller dùng giống nhau.

### Thuật ngữ

| Thuật ngữ | Định nghĩa ngắn | Dễ nhầm với |
| --- | --- | --- |
| Cryptographic validity | Signature đúng với key/algorithm được tin cậy | Token được phép dùng cho mọi endpoint |
| Semantic validity | Claims như issuer, audience, purpose, time khớp use case | Authorization business rule |
| Access token | Credential dùng tại resource access path | Refresh token |
| Refresh token | Credential dùng để xin access token mới, thường lifetime dài hơn | Access token dài hạn |
| Token purpose | Contract nói token được dùng ở boundary nào | `tokenType = Bearer` trong HTTP response |
| Bearer | Ai sở hữu token có thể trình token; không tự chứng minh sender | Token purpose |
| JOSE `typ` header | Media type hint của JWT object | Payload claim `token_type` |
| Session-backed refresh | Refresh token chỉ có hiệu lực khi server-side session còn active | Access token stateless |
| Fail-closed | Thiếu/không hiểu security metadata thì từ chối | Fallback sang generic validation |

### Classic cases

- Refresh token được gửi trong `Authorization: Bearer` và resource server chấp nhận vì signature/expiry đúng.
- Access token được gửi tới refresh endpoint; generic validation pass rồi workflow lỗi muộn khi đọc `session_id`.
- Legacy token không có purpose claim vẫn được chấp nhận vì validator coi claim là optional.
- Validator kiểm tra purpose khi refresh nhưng access filter vẫn gọi generic validation path.
- Hai token dùng key khác nhau nhưng verifier thử cả hai key tại mọi endpoint, làm separate-key boundary mất tác dụng.
- Error handler biến invalid token thành anonymous request; public matcher che mất security regression.
- Log ghi raw token để debug, biến log system thành nơi phát tán bearer credential.

### Misconceptions cần tránh

- “JWT verify được nghĩa là token hợp lệ cho request hiện tại.”
- “Refresh token có `session_id` nên tự động không thể dùng như access token.”
- “Dùng hai signing key là đủ, không cần claim hoặc validator boundary.”
- “`Bearer` trong `AuthResponse.tokenType` phân biệt access và refresh token.”
- “Refresh token bị revoke thì access filter hiện tại cũng biết.” Access filter không kiểm tra session.
- “`401` và `403` thay thế cho nhau.” Sai purpose là authentication failure (`401`).
- “Client không nên làm vậy nên server không cần negative test.” Bearer credential phải được kiểm tra ở trust boundary.

## 4. Current baseline

### Code path

#### Login/register

```text
AuthController
  -> AuthService.login/register
  -> SessionService.createSession
  -> JwtTokenProvider.generateToken
  -> JwtTokenProvider.generateRefreshToken
  -> AuthResponse(accessToken, refreshToken)
```

#### Access request hiện tại

```text
Authorization: Bearer <token>
  -> JwtAuthenticationFilter.extractTokenFromRequest
  -> JwtTokenProvider.validateToken(signature + expiry only)
  -> JwtTokenProvider.getUsernameFromToken
  -> CustomUserDetailsService.loadUserByUsername
  -> SecurityContext.setAuthentication
  -> URL/method authorization
```

#### Refresh hiện tại

```text
POST /api/auth/refresh
  -> AuthService.refreshAccessToken
  -> JwtTokenProvider.validateToken(signature + expiry only)
  -> getSessionIdFromToken
  -> SessionService.validateSession
  -> generateToken
```

### Bằng chứng hiện tại

- [`JwtTokenProvider`](../../../src/main/java/com/stream/demo/security/JwtTokenProvider.java): access token không có purpose claim; refresh token có `session_id`/`device_id`, nhưng không có explicit purpose claim; cả hai dùng `app.jwt.secret`.
- [`validateToken`](../../../src/main/java/com/stream/demo/security/JwtTokenProvider.java) chỉ parse signed claims, nên không kiểm tra expected token purpose, issuer hoặc audience.
- [`JwtAuthenticationFilter`](../../../src/main/java/com/stream/demo/security/JwtAuthenticationFilter.java) gọi generic `validateToken`, lấy subject và dựng authenticated principal cho bất kỳ JWT nào pass.
- [`AuthService`](../../../src/main/java/com/stream/demo/service/AuthService.java) cũng gọi generic validation ở refresh path rồi mới yêu cầu `session_id`.
- [`SecurityConfig`](../../../src/main/java/com/stream/demo/config/SecurityConfig.java) đang permit toàn bộ `/api/auth/**`; đây là gap liên quan nhưng không phải nguyên nhân của refresh-as-access tại protected `/api/users/**`.
- [`UserController`](../../../src/main/java/com/stream/demo/controller/UserController.java) có `GET /api/users/{userId}` yêu cầu `isAuthenticated()`, phù hợp làm protected endpoint cho reproducer.
- [`application.yml`](../../../src/main/resources/application.yml) đang đặt access TTL khoảng 100 giờ cho local demo và refresh TTL 7 ngày; lifetime dài làm impact của token misuse rõ hơn nhưng TTL không phải root cause.
- Test hiện chỉ có `LiveStreamBackendApplicationTests.contextLoads`; chưa có unit, filter hoặc MockMvc security regression test.
- [Security Flow](../../security/authorization-flow.md) đã đánh dấu token-purpose confusion là SEC-01; [API Contract](../../contracts/api-contract.md) route matcher gap riêng sang SEC-06.

### Documentation drift

- Security target hiện diễn đạt `typ=access/refresh`. Case này đề xuất dùng payload claim `token_type=ACCESS/REFRESH` để không nhầm với JOSE header `typ`; khi implementation được chấp nhận phải đồng bộ tên claim trong contract/security docs.
- Comment “Access Token” trong filter mô tả intent, không phải evidence rằng filter đã enforce token type.

### Failure reproducer

#### Manual baseline

1. Khởi động local dependencies và application với một user đã biết.
2. Gọi `POST /api/auth/login`, lấy `refreshToken` từ response.
3. Gọi protected endpoint bằng refresh token:

```http
GET {{host}}/api/users/1
Authorization: Bearer {{refreshToken}}
```

4. **Current vulnerable result dự đoán từ code:** filter dựng `Authentication`; endpoint có thể trả `200` nếu user ID tồn tại.
5. **Target:** `401`, SecurityContext không có authenticated principal, controller/service không được gọi.

Kết quả `200` hiện mới là hypothesis từ static inspection, chưa phải runtime evidence. Phải ghi actual status/body khi tạo red test hoặc chạy request; không được đánh dấu reproduced chỉ từ tài liệu này.

#### Automated red tests đầu tiên

- `JwtTokenProviderTest.refreshTokenMustFailAccessValidation`: hiện chưa thể viết đúng nghĩa vì chưa có purpose-specific validator; test sẽ mô tả target API trước.
- `AuthTokenPurposeSecurityTest.refreshTokenCannotAuthenticateProtectedRequest`: dùng refresh token hợp lệ cho `GET /api/users/{id}`; test phải đỏ trên baseline nếu endpoint trả `200` hoặc principal được dựng.
- `AuthTokenPurposeSecurityTest.accessTokenCannotRefreshSession`: access token tại `/api/auth/refresh` phải trả `401`, không đi tới session lookup.
- `AuthTokenPurposeSecurityTest.tokenWithoutPurposeFailsClosed`: legacy/generic signed token phải bị từ chối ở cả hai purpose-specific path.

### Verification commands dự kiến

```powershell
./mvnw.cmd -Dtest=JwtTokenProviderTest test
./mvnw.cmd -Dtest=AuthTokenPurposeSecurityTest test
./mvnw.cmd test
```

Trên Windows `cmd.exe` có thể dùng `mvnw.cmd`; trong PowerShell repository convention ưu tiên `./mvnw.cmd` hoặc `.\mvnw.cmd`.

## 5. Hypothesis

> Nếu mỗi token được phát hành với immutable application-level purpose claim và mọi validation path bắt buộc so khớp expected purpose trước khi dựng principal/session workflow, thì refresh-as-access và access-as-refresh sẽ bị từ chối bằng `401`, vì cryptographic validity không còn được dùng thay cho semantic validity.

### Success criteria

| Signal | Baseline | Target | Cách đo |
| --- | --- | --- | --- |
| Refresh token tại protected access endpoint | Dự đoán có thể authenticate | `401`, controller không chạy | MockMvc/filter integration test |
| Access token tại refresh endpoint | Generic validation pass, lỗi muộn vì thiếu `session_id` | `401` do purpose mismatch trước session lookup | Service/HTTP negative test |
| Token thiếu/không biết purpose | Generic validation có thể pass | Fail-closed | Unit parameterized test |
| Access token đúng purpose | Happy path hiện có | Vẫn authenticate | Positive regression test |
| Refresh token đúng purpose + active session | Happy path hiện có | Vẫn cấp access token mới | Service/integration test |
| Raw token trong log | Chưa có automated assertion | Không xuất hiện | Log capture test/review |

Không có performance claim trong SEC-01; latency/throughput benchmark là `N/A`.

## 6. Alternatives và decision

| Option | Correctness | Complexity | Performance | Operability | Khi nên dùng |
| --- | --- | --- | --- | --- | --- |
| A. Explicit payload `token_type` + purpose-specific validation | Giải quyết trực tiếp confusion; cần enforce ở mọi caller | Thấp-trung bình | Một claim comparison, không đáng kể | Dễ test và rollout | Chọn cho vertical slice hiện tại |
| B. Separate signing keys cho access/refresh + explicit purpose | Defense in depth, giảm blast radius key/path misuse | Trung bình-cao | Có thể cần nhiều verifier/key lookup | Rotation, secret distribution và monitoring phức tạp hơn | Nhiều service/issuer hoặc risk cao hơn |
| C. Opaque random refresh token, lưu hash server-side | Strong server-side control, dễ revoke/reuse detection | Cao hơn, stateful | Mỗi refresh cần lookup; access vẫn stateless | Cần storage lifecycle, rotation, cleanup | Production auth cần token family/reuse detection |
| D. Suy purpose từ sự tồn tại của `session_id` | Chỉ chặn schema hiện tại, dễ bypass khi model đổi | Thấp | Không đáng kể | Khó giải thích và dễ drift | Không chọn làm security contract |
| E. Chỉ rút ngắn TTL | Giảm exposure window, không loại bỏ confusion | Thấp | Tăng refresh frequency | Dễ nhưng không sửa root cause | Chỉ là defense bổ sung |

### Chọn

Chọn **Option A** cho SEC-01:

- Payload claim `token_type` nhận một trong hai giá trị `ACCESS` hoặc `REFRESH`.
- Token generation luôn ghi claim; validation yêu cầu expected type, không coi claim optional.
- Access filter chỉ gọi access-specific validation.
- Refresh service chỉ gọi refresh-specific validation và sau đó mới kiểm tra `session_id`/session state.
- Token không có type hoặc type lạ bị reject; không fallback generic validation.

Compatibility decision cho demo: token cũ không có claim sẽ hết hiệu lực ngay sau deployment. Với production rolling deployment, cần versioned issuer/validator hoặc dual-read window có deadline và metric; không đưa compatibility machinery đó vào SEC-01.

### Không chọn

- Chưa chọn separate keys vì project hiện là một application/issuer và mục tiêu đầu tiên là enforce semantic boundary. Có thể nâng cấp khi tách auth service hoặc cần độc lập rotation/blast radius.
- Chưa chuyển opaque refresh token vì kéo theo token storage/rotation/reuse detection, vượt vertical slice.
- Không chọn `session_id` heuristic vì schema presence không phải purpose contract.

### ADR

`N/A` cho bước hiện tại: decision cục bộ trong auth module và có thể đảo ngược. Tạo ADR nếu sau này chọn separate key, asymmetric signing/JWKS hoặc opaque refresh token vì các lựa chọn đó ảnh hưởng deployment và nhiều service.

## 7. Design target

### Happy path

```text
Login
  -> issue ACCESS(token_type=ACCESS, short TTL)
  -> issue REFRESH(token_type=REFRESH, session_id, device_id, long TTL)

Protected request
  -> parse + signature/time validation
  -> require token_type=ACCESS
  -> load principal
  -> authorize request

Refresh request
  -> parse + signature/time validation
  -> require token_type=REFRESH
  -> require session_id
  -> validate active UserSession
  -> issue new ACCESS token
```

### Failure/crash points

| Point | Failure | Expected behavior | Recovery |
| --- | --- | --- | --- |
| F1 | Missing/unknown `token_type` | `401`, không dựng principal | Client login lại |
| F2 | `REFRESH` tại access filter | `401`, controller không chạy | Dùng access token đúng |
| F3 | `ACCESS` tại refresh endpoint | `401`, không query session | Dùng refresh token đúng |
| F4 | Refresh đúng type nhưng thiếu/malformed `session_id` | `401`, structured safe error | Login lại; không log token |
| F5 | Refresh đúng type nhưng session revoked/expired | `401` | Login lại |
| F6 | Access đúng type nhưng signature/expiry sai | `401` | Client refresh/login theo lifecycle |
| F7 | Validator throw unexpected parsing exception | Security context giữ anonymous; response chuẩn hóa `401` | Log reason code không có raw token |

### Data/API/event changes dự kiến

- Migration: không có database migration.
- API contract: token payload thêm `token_type`; HTTP response shape không đổi.
- Cache key/TTL: không đổi.
- Event schema/key/order: `N/A`.
- Compatibility: reject token cũ thiếu purpose claim; ghi rõ deployment impact.

### Security

- Actor: authenticated user hoặc attacker sở hữu/lấy được bearer token.
- Asset: protected API access và quyền phát hành access token mới.
- Threat: credential substitution, long-lived refresh token privilege escalation, fail-open parser, token leakage trong log.
- Audit/redaction: chỉ log failure category/correlation ID; không log raw token hoặc full claims.

## 8. Implementation checkpoints

- [ ] Ghi actual baseline từ manual request hoặc red test.
- [ ] Thêm token-purpose enum/constant và explicit claim contract.
- [ ] Thêm purpose-specific generation/validation.
- [ ] Enforce `ACCESS` trong `JwtAuthenticationFilter`.
- [ ] Enforce `REFRESH` trước session lookup trong `AuthService`.
- [ ] Chuẩn hóa invalid/mismatched token thành `401`.
- [ ] Unit tests cho type, missing type, unknown type, signature và expiry.
- [ ] HTTP/filter tests cho refresh-as-access và access-as-refresh.
- [ ] Positive regression tests cho access và refresh happy paths.
- [ ] Kiểm tra raw token không xuất hiện trong log/error payload.
- [ ] Đồng bộ OpenAPI, `.http`, API contract và security flow khi behavior đổi.
- [ ] Chạy `$review-livestream-change` và ghi residual risks.

Không bắt đầu checkpoint implementation khi design này chưa được người học review/teach-back.

## 9. Verification matrix

| Level | Scenario | Tool/command | Expected |
| --- | --- | --- | --- |
| Unit | Generate access/refresh có type đúng | `JwtTokenProviderTest` | Claims lần lượt là `ACCESS`/`REFRESH` |
| Unit | Missing/unknown/mismatched type | Parameterized provider tests | Purpose validation trả invalid/fail-closed |
| Unit | Access token tại refresh service | `AuthServiceTest` với mocked `SessionService` | `401`; verify session service không được gọi |
| Security | Refresh token gọi protected `/api/users/{id}` | MockMvc/filter integration | `401`; controller/service không được gọi |
| Security | Access token gọi protected endpoint | MockMvc/filter integration | Request vẫn authenticate đúng |
| Integration | Refresh đúng type + active session | PostgreSQL-backed auth integration | Access token mới được phát hành |
| Integration | Refresh đúng type + revoked/expired session | PostgreSQL-backed auth integration | `401`, không phát token |
| Contract | Error status/payload và auth endpoint docs | MockMvc + OpenAPI/`.http` review | Contract đồng bộ, không lộ token |
| Concurrency | `N/A` | Không có shared-state race mới trong SEC-01 | Ghi rõ không áp dụng |
| Load | `N/A` | Không có performance claim | Không benchmark hình thức |
| Fault | Parser exception hoặc malformed JWT | Negative test | Không dựng principal; safe `401` |

### Verification order

1. Viết red test cho refresh-as-access và lưu actual result.
2. Viết provider-level target tests.
3. Implement nhỏ nhất để unit/security tests xanh.
4. Chạy auth-related tests rồi full suite.
5. Review diff và đồng bộ docs/contracts.

## 10. Experiment report

### Environment

- Git commit: `TBD khi bắt đầu implementation`.
- JDK/application: lấy từ POM/`AGENTS.md` tại lúc re-activate; snapshot khi case được tạo là Java 17, Spring Boot 3.4.
- Infrastructure: PostgreSQL/Redis theo test scope; không cần RabbitMQ cho SEC-01.
- Dataset: một user và một active session là đủ cho reproducer.
- Workload: single request; không có performance workload.

### Raw results

`NOT RUN` — case đang `PAUSED`, chưa implement và chưa chạy reproducer.

### Summary

| Signal | Before | After | Status |
| --- | --- | --- | --- |
| Refresh-as-access HTTP result | Chưa đo; static inspection dự đoán authenticate | Target `401` | Pending |
| Access-as-refresh HTTP result | Chưa đo; dự đoán lỗi muộn tại `session_id` | Target `401` trước session lookup | Pending |

### Interpretation

- Hypothesis: chưa được kiểm chứng.
- Confounding factor chính: `/api/auth/**` matcher public có thể che lỗi ở `/api/auth/me`; reproducer chính dùng `/api/users/{id}` để cô lập token-purpose issue.
- Điều chưa đo: actual status/error payload và log output.

## 11. Observability và operations

- Log event: authentication failure với `reason_code`, request path và correlation ID; không có raw token.
- Metric đề xuất: `auth_token_rejected_total{reason,purpose,path_group}` với label bounded; không dùng username/session ID/token làm label.
- Trace: `N/A` ở M2 nếu chưa có tracing baseline; không block SEC-01.
- SLI/SLO: không đặt mới trong case này.
- Alert: `N/A` cho local learning slice; metric tăng bất thường có thể thành signal sau OBS-01.
- Recovery: deployment làm token cũ thiếu type invalid; người dùng login lại.

## 12. Review findings và residual risk

| Severity | Finding | Resolution/status |
| --- | --- | --- |
| High | Generic `validateToken` không enforce purpose | Open; mục tiêu chính SEC-01 |
| High | Refresh token có thể dựng access principal | Chưa reproduced runtime; red test bắt buộc |
| Medium | Access token tại refresh path lỗi muộn thay vì purpose rejection | Open |
| Medium | Token cũ thiếu purpose sẽ bị invalid sau rollout | Chấp nhận cho demo; production cần rollout plan |
| Out of scope | `/api/auth/**` matcher quá rộng | Theo dõi SEC-06, không gộp implementation |
| Out of scope | Logout-all cache invalidation | SEC-02 |

## 13. Interview debrief

### Câu trả lời 2 phút — draft

JWT signature hợp lệ chỉ chứng minh issuer/key và integrity, không chứng minh token phù hợp với endpoint. Trong baseline, access và refresh token dùng cùng key; filter chỉ kiểm tra signature/expiry rồi dựng principal. Vì refresh token cũng có subject hợp lệ, nó có thể bị dùng như access token. Invariant của tôi là access path chỉ nhận `ACCESS`, refresh path chỉ nhận `REFRESH` và active session. Tôi chọn explicit payload claim cùng purpose-specific validation vì nó sửa đúng semantic boundary với complexity thấp. Separate keys và opaque refresh token là defense mạnh hơn nhưng chưa cần cho vertical slice. Evidence phải là negative HTTP tests cho cả hai hướng và fail-closed token thiếu type.

### Deep dive 15 phút

1. Vẽ current login/access/refresh path và chỉ ra generic validator.
2. Phân biệt cryptographic, temporal, semantic validation và authorization.
3. Reproduce refresh-as-access bằng protected endpoint.
4. So sánh claim, separate keys, opaque refresh token và TTL-only.
5. Giải thích selected contract, fail-closed và compatibility.
6. Trình bày unit/filter/integration verification matrix.
7. Thảo luận evolution sang JWKS/auth service/token family khi scale.

### Điều tôi trả lời chưa tốt

- [ ] Tự giải thích JOSE header `typ` và application purpose claim mà không nhìn notes.
- [ ] Trình bày rollout strategy cho token contract change trong multi-instance deployment.
- [ ] Giải thích khi nào opaque refresh token tốt hơn JWT refresh token.

### Flash questions

1. **Q:** Signature đúng có đủ để authorize không?<br>
   **A:** Không; còn phải validate time, issuer/audience/purpose và áp authorization rule.
2. **Q:** Vì sao refresh token nguy hiểm hơn khi bị dùng như access?<br>
   **A:** Lifetime thường dài và nó mở rộng credential có scope hẹp thành quyền truy cập resource trực tiếp.
3. **Q:** Separate keys có thay purpose claim không?<br>
   **A:** Không hoàn toàn; nếu verifier/key routing sai hoặc mọi path tin cả hai key, confusion vẫn có thể xảy ra. Claim giúp contract rõ và testable.
4. **Q:** Token thiếu type xử lý thế nào?<br>
   **A:** Fail-closed; production rollout cần compatibility window có deadline/telemetry nếu không thể invalidate ngay.
5. **Q:** Sai purpose trả `401` hay `403`?<br>
   **A:** `401`, vì credential không hợp lệ cho authentication path hiện tại.

## 14. Closure gate

- [ ] Invariant có automated evidence.
- [ ] Failure đã được tái hiện trước hoặc bằng negative security test.
- [x] Performance claim được xác định là `N/A`, không tạo benchmark hình thức.
- [x] Decision có ít nhất ba alternatives và trade-off.
- [ ] Security concerns đã được `$review-livestream-change` review.
- [ ] Error/log redaction đã được kiểm chứng.
- [ ] API/security docs và status đã sync sau implementation.
- [ ] Tôi tự giải thích được mà không đọc AI output.

Case giữ trạng thái `PAUSED` cho tới khi JDK-01 + TEST-01 đạt gate và các foundation decision trước nó đã đóng/reschedule có reason trong cursor. Sau khi re-activate, không đổi sang `EVIDENCE_READY` chỉ vì document đã đầy đủ.

## 15. Links

- Roadmap: [Senior Java Interview Roadmap](../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#6-case-backlog-ưu-tiên)
- Template: [Learning Case Template](../../templates/learning-case-template.md)
- Security contract: [Authorization Flow](../../security/authorization-flow.md)
- API contract: [API Contract](../../contracts/api-contract.md)
- Manual requests: [Auth HTTP requests](../../../.http/auth-controller.http), [User HTTP requests](../../../.http/user-controller.http)
- Code: [`JwtTokenProvider`](../../../src/main/java/com/stream/demo/security/JwtTokenProvider.java), [`JwtAuthenticationFilter`](../../../src/main/java/com/stream/demo/security/JwtAuthenticationFilter.java), [`AuthService`](../../../src/main/java/com/stream/demo/service/AuthService.java)
- Tests: `TBD` — auth security tests chưa tồn tại.
- ADR: `N/A` cho local decision hiện tại.
- Experiment: chưa chạy.
- Dashboard/runbook: `N/A` cho M2 security correctness case.
- Official references: sẽ bổ sung khi người học deep-dive theory; không copy dài vào case.

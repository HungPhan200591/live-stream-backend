# Security Interview Question Bank — Secret Exposure, Audience DTO and Log Redaction

> Status: `DRAFT`<br>
> Domain owner: `security / REST API / observability`<br>
> Active slice: `NONE`; preview target `SEC-03 — stream-key/secret exposure, audience DTO and log redaction`<br>
> Related roadmap: [Stage 0](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-0---stabilize-the-laboratory)<br>
> Related depth rubric: [Security](../../../knowledge-depth-rubric.md#38-security-và-identity--p0-target-d3), [HTTP/API](../../../knowledge-depth-rubric.md#36-http-api-design-và-network-fundamentals--p0-target-d3), [Observability](../../../knowledge-depth-rubric.md#311-observability-reliability-và-incident-response--p0-target-d3)<br>
> Related contract: [API contract — Stream](../../../../contracts/api-contract.md#stream)<br>
> Related theory: [Core theory](../theory/core/secret-exposure-and-audience-boundaries.md)<br>
> Updated: `2026-07-26`

Preview này không implement `SEC-03`, không active case và không tạo evidence. `Interview likelihood` là heuristic trong phạm vi REST/API security của vai trò backend, không phải tỷ lệ thị trường đã đo. Mọi câu giữ `UNANSWERED`, test `NOT RUN`.

## Project anchor

Current code dùng một `StreamDTO` chứa `streamKey` cho public, owner và webhook response; `StreamService`/`WebhookController` còn log trực tiếp key. Các path này chỉ là điểm vận dụng sau khi đã trả lời nhóm câu phổ biến, không biến toàn bank thành câu đố riêng của project.

## Coverage

| Topic | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| API/DTO boundary | 3 | 2 | 1 | 0 |
| Secret and logging lifecycle | 2 | 3 | 1 | 1 |
| Testing, migration and incident | 0 | 1 | 1 | 1 |
| **Tổng** | **5** | **6** | **3** | **2** |

## Recommended practice order

1. First pass — câu phổ biến: `SEC-SECRET-001`, `SEC-SECRET-002`, `SEC-SECRET-005`, `SEC-SECRET-004`, `SEC-SECRET-006`, `SEC-SECRET-007`, `SEC-SECRET-009`.
2. Senior follow-up: `SEC-SECRET-003`, `SEC-SECRET-008`, `SEC-SECRET-010`, `SEC-SECRET-011`.
3. Project application: dùng `SEC-SECRET-006` đến `SEC-SECRET-011` để phân tích current `StreamDTO` và log path.
4. Architect/Expert stretch: `SEC-SECRET-012` đến `SEC-SECRET-016`.

## Questions

### SEC-SECRET-001 — `FOUNDATION`
**Question:** Dữ liệu nào được coi là secret hoặc sensitive data trong một backend, và vì sao stream key thuộc nhóm đó?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu mở đầu tự nhiên trong API/security review.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Phân loại credential, token, PII và business-sensitive data theo impact khi lộ.<br>
**Answer outline:** Secret cho phép caller thực hiện hành động hoặc truy cập tài nguyên; stream key chứng minh quyền publish nên phải được bảo vệ như credential, khác metadata public của stream.<br>
**Required trade-offs:** Khả năng debug và UX không được đánh đổi bằng việc trả/log secret rộng rãi.<br>
**Follow-up ladder:** API key? Password hash? Email? Correlation ID? Secret có hết nhạy cảm sau expiry không?<br>
**Red flags:** Chỉ password mới là dữ liệu nhạy cảm.<br>
**Evidence:** Theory [Core](../theory/core/secret-exposure-and-audience-boundaries.md); case `SEC-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SECRET-002 — `FOUNDATION`
**Question:** DTO khác Entity ở mục đích nào, và tại sao REST API không nên trả JPA entity trực tiếp?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — câu REST/Spring backend rất phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** API contract, persistence encapsulation, lazy relation và over-posting/exposure risk.<br>
**Answer outline:** Entity mô hình persistence; DTO định nghĩa contract theo use case/caller, chỉ expose field cần thiết, ổn định hơn schema DB và tách mapping/validation khỏi ORM lifecycle.<br>
**Required trade-offs:** DTO/mapping thêm code nhưng đổi lại security boundary, compatibility và testability.<br>
**Follow-up ladder:** Projection? MapStruct? Record DTO? Versioning? N+1 do mapping?<br>
**Red flags:** Entity và DTO chỉ khác tên class.<br>
**Evidence:** Theory [Core](../theory/core/secret-exposure-and-audience-boundaries.md); case `SEC-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SECRET-003 — `FOUNDATION`
**Question:** Tại sao cùng một resource có thể cần public DTO, owner DTO và internal DTO khác nhau?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `MEDIUM` — follow-up quen thuộc của DTO/API authorization.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Field-level disclosure theo audience, không chỉ endpoint-level authentication.<br>
**Answer outline:** Mỗi audience có minimum necessary fields khác nhau; public view không chứa credential, owner view có management data, internal view có thể giữ operational identifiers nhưng không mặc định serialize ra ngoài.<br>
**Required trade-offs:** DTO riêng tránh accidental exposure nhưng làm tăng mapping/contract maintenance.<br>
**Follow-up ladder:** Admin có luôn thấy secret? GraphQL field auth? Mobile client compatibility?<br>
**Red flags:** Đăng nhập rồi thì được nhận mọi field.<br>
**Evidence:** Theory [Core](../theory/core/secret-exposure-and-audience-boundaries.md); case `SEC-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SECRET-004 — `FOUNDATION`
**Question:** Redaction, masking, hashing và encryption khác nhau thế nào?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — vocabulary security/logging thường được hỏi.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Chọn transformation đúng mục đích, không dùng thuật ngữ lẫn lộn.<br>
**Answer outline:** Redaction loại bỏ; masking chỉ lộ một phần; hashing một chiều để so sánh/fingerprint; encryption có thể giải mã bằng key. Log thường ưu tiên loại bỏ hoặc fingerprint đã bound, không “encrypt rồi log tất cả”.<br>
**Required trade-offs:** Correlation/debug utility versus re-identification và key-management risk.<br>
**Follow-up ladder:** Salt/HMAC? Last four characters? Token fingerprint? GDPR deletion?<br>
**Red flags:** Base64 được gọi là encryption; hash nào cũng an toàn cho low-entropy secret.<br>
**Evidence:** Theory [Core](../theory/core/secret-exposure-and-audience-boundaries.md); case `SEC-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SECRET-005 — `FOUNDATION`
**Question:** Những dữ liệu nào tuyệt đối không nên ghi vào application log?<br>
**Target depth:** `D1-D2`<br>
**Interview likelihood:** `HIGH` — secure logging là câu security practice phổ biến.<br>
**Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Credential/PII awareness và log-access threat model.<br>
**Answer outline:** Không log password, raw token, API/stream/webhook key, payment credential hoặc sensitive payload; dùng event type, result reason, non-secret IDs hoặc bounded fingerprint khi thật sự cần correlation.<br>
**Required trade-offs:** Log ít dữ liệu bí mật hơn có thể khó debug, nên thiết kế safe diagnostic fields từ đầu.<br>
**Follow-up ladder:** Request/response logging middleware? Stack trace? SQL bind values? Headers?<br>
**Red flags:** Debug level hoặc “log chỉ nội bộ” được coi là đủ an toàn.<br>
**Evidence:** Theory [Core](../theory/core/secret-exposure-and-audience-boundaries.md); case `SEC-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SECRET-006 — `SENIOR`
**Question:** Một field secret đang nằm trong DTO dùng chung. Bạn ngăn nó xuất hiện ở public JSON response như thế nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — scenario API security rất thực tế.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Sửa contract tại source thay vì dựa vào caller nhớ null field.<br>
**Answer outline:** Tách response DTO theo audience và mapping explicit; public DTO không khai báo secret; owner/create response chỉ chứa secret nếu use case cần; thêm negative serialization/MockMvc tests.<br>
**Required trade-offs:** `@JsonIgnore` nhanh nhưng global/coarse; field null vẫn làm contract mơ hồ; DTO riêng rõ hơn nhưng thêm types.<br>
**Follow-up ladder:** Jackson views? Projection? OpenAPI schema? Cache cũ chứa DTO nào?<br>
**Red flags:** Set `streamKey = null` ở từng controller và tin mọi path đều nhớ.<br>
**Evidence:** Theory [Core](../theory/core/secret-exposure-and-audience-boundaries.md); case `SEC-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SECRET-007 — `SENIOR`
**Question:** Thiết kế response cho create stream, public list/detail và owner “my streams” để chỉ đúng caller nhận stream key.<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — dạng “design endpoint/DTO by use case” thường gặp.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Audience matrix, ownership check và least disclosure.<br>
**Answer outline:** Public list/detail dùng public DTO không key; owner endpoint sau authentication/ownership dùng management DTO; create có thể trả key một lần hoặc owner-only; webhook response cũng không cần echo credential.<br>
**Required trade-offs:** Show-once giảm exposure nhưng cần rotation/recovery UX; always-readable owner key tăng tiện lợi và attack surface.<br>
**Follow-up ladder:** Admin? Support staff? Key regeneration? Shared OBS device?<br>
**Red flags:** Tách URL nhưng vẫn gọi cùng mapper chứa secret.<br>
**Evidence:** Theory [Core](../theory/core/secret-exposure-and-audience-boundaries.md); case `SEC-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SECRET-008 — `SENIOR`
**Question:** Secret có thể rò qua exception và log như thế nào dù business log đã bỏ field đó?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — follow-up secure-logging quen thuộc.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Cross-layer leak path: exception message, access log, tracing, serializer và third-party sink.<br>
**Answer outline:** Không nhét raw secret vào exception/resource lookup message; sanitize request/header logging; tránh DTO/entity `toString`; kiểm soát APM breadcrumbs, SQL bind logs và error response; test captured logs.<br>
**Required trade-offs:** Central sanitizer là safety net, nhưng source-level omission mới giảm blast radius.<br>
**Follow-up ladder:** MDC? Stack trace? Reverse proxy logs? Dead-letter payload?<br>
**Red flags:** Chỉ grep các câu `log.info` trực tiếp.<br>
**Evidence:** Theory [Core](../theory/core/secret-exposure-and-audience-boundaries.md); case `SEC-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SECRET-009 — `SENIOR`
**Question:** Bạn viết test nào để chứng minh secret không xuất hiện trong public API và log?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `HIGH` — “test security fix thế nào” là Senior follow-up tự nhiên.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Negative assertions trên contract thật, không chỉ mapper unit test.<br>
**Answer outline:** MockMvc/contract tests assert JSON path và schema không có field ở list/detail/webhook/error; owner/create positive test theo policy; capture logs qua test appender và assert raw sentinel secret vắng mặt.<br>
**Required trade-offs:** Snapshot toàn response dễ brittle; targeted negative assertions cần duy trì cho mọi audience/path.<br>
**Follow-up ladder:** OpenAPI test? Structured logs? Async log? Property-based secret sentinel?<br>
**Red flags:** Chỉ assert status 200 hoặc chỉ test happy path của owner.<br>
**Evidence:** Theory [Core](../theory/core/secret-exposure-and-audience-boundaries.md); case `SEC-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SECRET-010 — `SENIOR`
**Question:** Stream/API key nên được generate, lưu, hiển thị và rotate theo lifecycle nào?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — common credential-management follow-up.<br>
**Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Entropy, secure generation, at-rest protection, show-once và revocation.<br>
**Answer outline:** Dùng CSPRNG đủ entropy; truyền qua TLS; hạn chế read path; cân nhắc hash nếu chỉ cần verify hoặc encrypt nếu cần retrieve; rotate/revoke có audit và overlap policy rõ.<br>
**Required trade-offs:** Hash an toàn hơn khi DB lộ nhưng không retrieve được; encryption hỗ trợ recovery nhưng cần key management.<br>
**Follow-up ladder:** UUID đủ không? Unique constraint? Rotation grace? Compromised key?<br>
**Red flags:** Dựa vào khó đoán của database ID hoặc chỉ mã hóa Base64.<br>
**Evidence:** Theory [Core](../theory/core/secret-exposure-and-audience-boundaries.md); case `SEC-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SECRET-011 — `SENIOR`
**Question:** Jackson, Lombok và generic request logging có thể vô tình làm lộ secret ra sao?<br>
**Target depth:** `D2-D3`<br>
**Interview likelihood:** `MEDIUM` — Java/Spring-specific follow-up hữu ích.<br>
**Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Serialization/introspection defaults và defense in depth.<br>
**Answer outline:** Getter làm field được serialize; Lombok `@Data` sinh `toString`; logging filter có thể dump body/header; loại field khỏi public type, exclude `toString`, redact ở boundary và kiểm tra config thư viện.<br>
**Required trade-offs:** Annotation tiện nhưng dễ tạo policy phân tán; DTO riêng rõ ownership hơn.<br>
**Follow-up ladder:** `@JsonIgnore`? Record? Deserialization? Actuator env/configprops?<br>
**Red flags:** Tin rằng private field không thể bị Jackson/log framework đọc.<br>
**Evidence:** Theory [Core](../theory/core/secret-exposure-and-audience-boundaries.md); case `SEC-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SECRET-012 — `ARCHITECT`
**Question:** Thiết kế secret lifecycle cho stream key từ issuance tới rotation, revocation, audit và deletion.<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `MEDIUM` — gặp ở security/architecture round hơn là screening.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Ownership, state machine, blast radius và recovery path.<br>
**Answer outline:** Định nghĩa owner/purpose/scope, created/active/rotating/revoked states, version/key ID, show/read policy, audit không chứa secret, overlap bounded và emergency revoke/runbook.<br>
**Required trade-offs:** Zero-overlap rotation an toàn hơn nhưng dễ gián đoạn; grace window tăng exposure.<br>
**Follow-up ladder:** Fleet rollout? Offline encoder? Break-glass? Tenant isolation?<br>
**Red flags:** Rotation chỉ là generate key mới, không xử lý key cũ/consumer.<br>
**Evidence:** Theory [Core](../theory/core/secret-exposure-and-audience-boundaries.md); case `SEC-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SECRET-013 — `ARCHITECT`
**Question:** Nên kiểm soát redaction tại source, logging framework hay observability pipeline?<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `LOW` — câu platform/observability architecture chuyên sâu.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Defense in depth và giới hạn regex-based sanitizer.<br>
**Answer outline:** Omit tại source/type là primary control; structured logger allowlist field; framework/pipeline redaction là safety net; sink access/retention và scanning/alerting giảm residual risk.<br>
**Required trade-offs:** Central policy dễ rollout nhưng không hiểu semantics và secret có thể đã đi qua memory/network trước khi redact.<br>
**Follow-up ladder:** Schema registry cho logs? Sampling? Cost? Third-party APM?<br>
**Red flags:** Một regex toàn cục được coi là đảm bảo không leak.<br>
**Evidence:** Theory [Core](../theory/core/secret-exposure-and-audience-boundaries.md); case `SEC-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SECRET-014 — `ARCHITECT`
**Question:** Threat-model đầy đủ các đường stream key có thể bị exfiltrate trong hệ thống backend.<br>
**Target depth:** `D3-D4`<br>
**Interview likelihood:** `MEDIUM` — security design round thường dùng dạng threat-model scenario.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Data-flow reasoning qua API, log, DB, cache, backup, trace, queue và con người.<br>
**Answer outline:** Vẽ source/sink/trust boundary; kiểm public/owner/webhook/error response, logs/APM, DB dump/backups, admin/support tool, cache/message, screenshots và CI fixtures; áp least privilege, encryption và detection.<br>
**Required trade-offs:** Kiểm soát nhiều lớp tăng cost; ưu tiên theo likelihood × impact và blast radius.<br>
**Follow-up ladder:** Insider threat? Tenant boundary? Backup restore? Data retention?<br>
**Red flags:** Threat model chỉ kiểm controller endpoint.<br>
**Evidence:** Theory [Core](../theory/core/secret-exposure-and-audience-boundaries.md); case `SEC-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SECRET-015 — `EXPERT`
**Question:** Phát hiện stream key đã bị log và có thể đã được truy cập. Bạn containment, rotate, điều tra và phòng tái diễn thế nào?<br>
**Target depth:** `D4`<br>
**Interview likelihood:** `LOW` — incident-response discriminator.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Evidence preservation, containment order và blast-radius reasoning.<br>
**Answer outline:** Hạn chế log access/preserve evidence; xác định keys, sinks, retention và readers; revoke/rotate với service continuity; search downstream copies; audit misuse; fix source plus tests/scanners; document timeline/postmortem.<br>
**Required trade-offs:** Xóa log ngay có thể phá evidence; giữ log tăng exposure nên cần restricted legal/security handling.<br>
**Follow-up ladder:** Không biết key nào lộ? Backup immutable? Customer notification? Credential stuffing?<br>
**Red flags:** Chỉ xóa một log line hoặc chỉ deploy redaction mà không rotate credential.<br>
**Evidence:** Theory [Core](../theory/core/secret-exposure-and-audience-boundaries.md); case `SEC-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### SEC-SECRET-016 — `EXPERT`
**Question:** Tách DTO trong rolling deployment mà client cũ, cache và nhiều service vẫn kỳ vọng field `streamKey`: migration contract thế nào để không tái lộ secret?<br>
**Target depth:** `D4`<br>
**Interview likelihood:** `LOW` — backward-compatibility/security pathological case.<br>
**Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Security fix precedence, staged compatibility và cache/schema migration.<br>
**Answer outline:** Treat exposure as contract-breaking security fix; inventory consumers; thêm owner-only endpoint/version, deploy consumer support trước nếu an toàn, purge/version cached DTO, remove public field, monitor denied/legacy traffic và đặt sunset rõ.<br>
**Required trade-offs:** Compatibility không được giữ secret ở public response vô thời hạn; emergency cutoff có thể chấp nhận client breakage theo severity.<br>
**Follow-up ladder:** CDN cache? OpenAPI SDK? Blue-green? Rollback có reintroduce leak không?<br>
**Red flags:** Giữ field public “temporary” không deadline hoặc rollback về phiên bản leak.<br>
**Evidence:** Theory [Core](../theory/core/secret-exposure-and-audience-boundaries.md); case `SEC-03 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `SEC-03` active: tạo core/deep-dive, link actual DTO/log code path, xác định audience matrix, tạo negative API/log tests và thay marker bằng evidence thật. Webhook authentication/replay thuộc `SEC-05`; generic observability pipeline thuộc `OBS-01`. Stable IDs không tái sử dụng.

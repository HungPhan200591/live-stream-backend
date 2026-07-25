# Spring Interview Question Bank — Configuration Isolation and Production Safety

> Status: `DRAFT`<br>
> Domain owner: `Spring Boot / configuration / security operations`<br>
> Active slice: `NONE`; preview target `CFG-01 — dev/test/prod isolation and production fail-fast`<br>
> Related roadmap: [Stage 0 and CFG-01](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-0---stabilize-the-laboratory)<br>
> Related depth rubric: [Spring Boot](../../../knowledge-depth-rubric.md#35-spring-framework-và-spring-boot--p0-target-d3), [Security](../../../knowledge-depth-rubric.md#38-security-và-identity--p0-target-d3), [Observability](../../../knowledge-depth-rubric.md#311-observability-reliability-và-incident-response--p0-target-d3)<br>
> Related theory: [Configuration and Profile Safety](../theory/core/configuration-and-profile-safety.md), [configuration/auto-configuration deep-dive](../theory/deep-dives/configuration-binding-autoconfiguration-and-secret-safety.md) — `DRAFT`, evidence `NOT RUN`<br>
> Version snapshot checked: `2026-07-26`; re-check exact Spring Boot behavior when `CFG-01` becomes active<br>
> Updated: `2026-07-26`

Question bank này được chuẩn bị trước cho `CFG-01`. Nó không sửa configuration/code, không rotate secret và không chứng minh production đã được harden. Người học trả lời trước khi mở `Answer outline`; mọi test, experiment và interview note giữ `NOT RUN`/`NOT CREATED`.

## Official sources for the snapshot

- [Spring Boot — Externalized Configuration](https://docs.spring.io/spring-boot/3.5/reference/features/external-config.html)
- [Spring Boot — Profiles](https://docs.spring.io/spring-boot/reference/features/profiles.html)
- [Spring Boot Actuator — Endpoints, Exposure and Security](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html)

## Level rubric

| Level | Trọng tâm |
| --- | --- |
| `FOUNDATION` | Property-source precedence, profile/config/secret vocabulary và fail-fast |
| `SENIOR` | Typed validation, conditional surface, diagnostic exposure và safe logging |
| `ARCHITECT` | Environment contract, secret/config lifecycle, rollout và governance |
| `EXPERT` | Hostile precedence, bootstrap dependency và recovery under misconfiguration |

## Coverage

| Topic | Foundation | Senior | Architect | Expert | Theory source |
| --- | ---: | ---: | ---: | ---: | --- |
| Externalized config, precedence và profiles | 2 | 1 | 1 | 1 | `PLANNED` |
| Secrets, defaults và fail-fast validation | 1 | 1 | 1 | 1 | `PLANNED` |
| Dev/test surface, Actuator và logging | 0 | 2 | 0 | 0 | `PLANNED` |
| Rollout, drift và operational governance | 0 | 0 | 1 | 0 | `PLANNED` |
| **Tổng** | **3** | **4** | **3** | **2** | 12 questions |

## Questions

### SPR-CONFIG-001 — `FOUNDATION`

**Question:**

Spring Boot externalized configuration giải quyết vấn đề gì? Vì sao cùng một key có thể nhận giá trị khác nhau từ file, environment variable, system property, command line hoặc test override?

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có mental model về ordered property sources thay vì đoán file nào “được đọc cuối”.

**Answer outline:**

1. Externalized config cho phép cùng artifact chạy ở nhiều environment mà không rebuild code.
2. Property sources có precedence xác định; source ưu tiên cao hơn override source thấp hơn, test sources có rules riêng.
3. Phải biết effective value và origin, không chỉ search repository; command-line/environment có thể thay packaged config.

**Required trade-offs:**

- Override linh hoạt hỗ trợ deployment nhưng tăng risk drift và hidden input.

**Follow-up ladder:**

- Foundation: `application-prod.yml` có luôn thắng OS environment không?
- Senior: Debug effective origin mà không log secret thế nào?
- Architect: Cho phép source nào override security-critical properties?
- Expert: Precedence trở thành injection surface ra sao?

**Red flags:**

- “File profile luôn thắng”; in toàn bộ `Environment` để debug.

**Evidence:**

- Theory: `NOT CREATED`
- Deep-dive: `NOT CREATED`
- Learning case: `CFG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SPR-CONFIG-002 — `FOUNDATION`

**Question:**

Spring profile là gì và không phải là gì? Vì sao profile không tự trở thành security boundary bảo đảm dev endpoint không xuất hiện trong production?

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có phân biệt conditional bean/config activation với authorization/deployment assurance.

**Answer outline:**

1. Profile conditionally activates beans/config documents; active profile vẫn là configuration input có thể sai.
2. Dev component phải bị loại bằng explicit condition/profile và production tests phải chứng minh bean/mapping không tồn tại.
3. URL authorization là lớp khác; endpoint nguy hiểm không nên được tạo rồi chỉ dựa vào `permit/deny` hoặc comment “dev only”.

**Required trade-offs:**

- Profiles dễ dùng nhưng profile proliferation làm combination khó kiểm soát; capability flags rõ nghĩa đôi khi phù hợp hơn.

**Follow-up ladder:**

- Foundation: `@Profile` có áp dụng cho property value không?
- Senior: Test absence của endpoint/bean thế nào?
- Architect: Profile group làm topology đơn giản hay ẩn coupling?
- Expert: Hai profile vô tình cùng active tạo conflict nào?

**Red flags:**

- Dùng profile thay authentication; chỉ đổi route prefix thành `/dev`.

**Evidence:**

- Theory: `NOT CREATED`
- Deep-dive: `NOT CREATED`
- Learning case: `CFG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SPR-CONFIG-003 — `FOUNDATION`

**Question:**

Phân biệt non-secret configuration, secret reference và secret value. Vì sao một default development secret trong repository nguy hiểm dù production “sẽ override bằng environment variable”?

**Target depth:** `D1-D2`

**Interviewer evaluates:**

- Có hiểu fail-open default và secret lifecycle, không chỉ vị trí lưu.

**Answer outline:**

1. Non-secret config có thể version/control; secret reference trỏ tới provider/path; secret value cần confidentiality, rotation và audit.
2. Default secret làm missing production injection vẫn startup với credential biết trước; override intention không phải evidence.
3. Production phải require strong external value, validate placeholder/default bị cấm và không đưa value vào log/error/Actuator.

**Required trade-offs:**

- Local convenience cần fixture riêng hoặc generated ephemeral secret, không được tạo production fallback yếu.

**Follow-up ladder:**

- Foundation: Base64 có phải encryption không?
- Senior: Test production fail khi secret thiếu/default thế nào?
- Architect: Secret reference được promote giữa environments ra sao?
- Expert: Rotation khi old/new instances cùng chạy cần contract gì?

**Red flags:**

- “Secret đã base64 nên an toàn”; default production credential có comment cảnh báo.

**Evidence:**

- Theory: `NOT CREATED`
- Deep-dive: `NOT CREATED`
- Learning case: `CFG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SPR-CONFIG-004 — `SENIOR`

**Question:**

Thiết kế `@ConfigurationProperties` và validation để production fail-fast khi URL, duration, pool size, allowed origin hoặc secret không hợp lệ như thế nào?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có typed binding, cross-field invariant và actionable startup failure.

**Answer outline:**

1. Gom config theo bounded prefix/type; dùng duration/data-size/URI/enum thay string rời và Bean Validation cho constraint.
2. Cross-field validation bảo vệ invariant như production không cho wildcard origin/default secret hoặc timeout âm.
3. Fail trong startup trước khi nhận traffic; error nêu key/reason nhưng redact value nhạy cảm.
4. Test valid/invalid matrix theo profile và override source.

**Required trade-offs:**

- Strict validation giảm silent misconfiguration nhưng cần migration plan khi config contract đổi.

**Follow-up ladder:**

- Foundation: `@Value` và typed properties khác gì?
- Senior: Unknown/deprecated key được phát hiện thế nào?
- Architect: Config schema/version được publish cho platform team ra sao?
- Expert: Validation cần external dependency thì tránh bootstrap cycle thế nào?

**Red flags:**

- Parse thủ công tại request time; catch validation error rồi dùng default.

**Evidence:**

- Theory: `NOT CREATED`
- Deep-dive: `NOT CREATED`
- Learning case: `CFG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SPR-CONFIG-005 — `SENIOR`

**Question:**

Bạn chứng minh dev/test controller, seed data, default user và Swagger không xuất hiện trong production artifact/runtime surface như thế nào?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có negative evidence ở bean/mapping/security/runtime layers.

**Answer outline:**

1. Conditionalize component/config theo explicit non-production profile/property; production default là absent/off.
2. Production-context integration test assert bean/mapping không tồn tại và endpoint trả expected non-exposure result.
3. Kiểm tra packaged resources/dependencies và startup logs/mappings có redact; seed initializer không chạy chỉ vì database rỗng.
4. Network/security control là defense-in-depth, không thay absence test.

**Required trade-offs:**

- Dùng cùng artifact giảm drift nhưng cần robust conditional tests; tách artifact giảm surface nhưng tăng build/promotion complexity.

**Follow-up ladder:**

- Foundation: HTTP 403 và 404 chứng minh khác nhau gì?
- Senior: Conditional bean test cần những profile combinations nào?
- Architect: Swagger có thể mở nội bộ qua control nào?
- Expert: Class vẫn packaged nhưng bean absent còn residual risk gì?

**Red flags:**

- Chỉ kiểm tra UI không hiện; endpoint tồn tại và `permitAll` vì “dev route”.

**Evidence:**

- Theory: `NOT CREATED`
- Deep-dive: `NOT CREATED`
- Learning case: `CFG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SPR-CONFIG-006 — `SENIOR`

**Question:**

Actuator `health`, `env`, `configprops`, `mappings`, `loggers`, heap/thread dump và logfile có risk khác nhau thế nào? Exposure, availability và authorization là các quyết định riêng ra sao?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có threat-model diagnostic surface thay vì bật `*` rồi dựa vào sanitization.

**Answer outline:**

1. Endpoint có thể lộ topology/config/mapping/log/data hoặc cho thay đổi runtime; heap/log có thể chứa secret/PII.
2. Endpoint phải được enabled/available, exposed qua transport và authorized; mỗi lớp không thay lớp còn lại.
3. Allowlist tối thiểu, network separation và dedicated role/matcher; custom `SecurityFilterChain` phải bảo vệ cả actuator lẫn app.
4. Sanitization là defense-in-depth, không lý do công khai endpoint nhạy cảm.

**Required trade-offs:**

- Diagnostic depth giúp incident response nhưng tăng attack surface và data-handling burden.

**Follow-up ladder:**

- Foundation: Vì sao health detail cũng nhạy cảm?
- Senior: Custom security chain làm Boot auto-config back off thì test gì?
- Architect: Management port/network plane nên tách thế nào?
- Expert: Break-glass diagnostics được audit và hết hạn ra sao?

**Red flags:**

- “Actuator tự secure”; public `env/configprops` vì value đã mask.

**Evidence:**

- Theory: `NOT CREATED`
- Deep-dive: `NOT CREATED`
- Learning case: `CFG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SPR-CONFIG-007 — `SENIOR`

**Question:**

Configuration và logging tương tác thế nào để token/password/SQL parameter/secret không bị lộ khi bật debug hoặc xử lý startup failure?

**Target depth:** `D2-D3`

**Interviewer evaluates:**

- Có kiểm soát data flow vào log, early-bootstrap logging và failure message.

**Answer outline:**

1. Không log raw config/environment/request credential; log key/origin/validation reason đã redact.
2. SQL/body/security debug log có thể bypass application redaction; production logging levels/appenders phải allowlist và tested.
3. Startup exception, config trace, Actuator, crash dump và CI command output đều là exfiltration paths.
4. Negative tests/scanner kiểm tra known canary secret không xuất hiện trong captured logs.

**Required trade-offs:**

- Redaction giảm diagnostic detail; dùng correlation/metadata và controlled break-glass thay raw sensitive payload.

**Follow-up ladder:**

- Foundation: Mask sau khi format có đủ không?
- Senior: Exception từ binder có thể lộ rejected value thế nào?
- Architect: Log retention/access policy liên quan config risk ra sao?
- Expert: Structured logging pipeline cần redaction ở producer hay collector?

**Red flags:**

- Log toàn environment khi startup; bật SQL parameter logging production để debug.

**Evidence:**

- Theory: `NOT CREATED`
- Deep-dive: `NOT CREATED`
- Learning case: `CFG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SPR-CONFIG-008 — `ARCHITECT`

**Question:**

Thiết kế environment configuration contract để cùng artifact được promote dev → test → production nhưng vẫn phát hiện drift và override ngoài kiểm soát như thế nào?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có config schema, provenance, ownership và deployment evidence.

**Answer outline:**

1. Version typed config contract/default-safe non-secret values; environment chỉ cung cấp declared differences và secret references.
2. Git/IaC/deployment manifest sở hữu non-secret config; secret manager sở hữu values; record artifact/config revision/provenance.
3. Pre-deploy validation và startup fail-fast; report effective non-sensitive fingerprint/drift, không dump values.
4. Override/break-glass có approval, audit, owner và expiry; rollback ghép artifact với compatible config revision.

**Required trade-offs:**

- Central governance giảm drift nhưng có thể chậm delivery; self-service cần policy-as-code và clear ownership.

**Follow-up ladder:**

- Foundation: Artifact version và config version khác gì?
- Senior: Fingerprint secret mà không lộ value thế nào?
- Architect: Config promotion và environment-specific value tách ra sao?
- Expert: Reconcile manual hotfix mà không overwrite incident mitigation thế nào?

**Red flags:**

- SSH sửa file production không audit; rebuild artifact cho mỗi environment.

**Evidence:**

- Theory: `NOT CREATED`
- Deep-dive: `NOT CREATED`
- Learning case: `CFG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SPR-CONFIG-009 — `ARCHITECT`

**Question:**

Thiết kế secret lifecycle cho database, JWT/webhook hoặc broker credential gồm provisioning, access, rotation, revocation và audit như thế nào?

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có nhìn secret là lifecycle/capability, không chỉ environment variable.

**Answer outline:**

1. Secret external, encrypted at rest/in transit, identity-based least privilege và không nằm trong repository/artifact/log.
2. Rotation cần old/new overlap hoặc key ID/version contract, rollout order, cache/connection refresh và verified revocation.
3. Audit access/change, expiry/owner và incident revoke path; local/test dùng isolated ephemeral fixture.
4. CFG-01 bảo vệ missing/default handling; protocol-specific webhook rotation/replay vẫn thuộc `SEC-05`.

**Required trade-offs:**

- Short-lived credentials giảm exposure nhưng tăng availability dependency và renewal complexity.

**Follow-up ladder:**

- Foundation: Secret reference khác secret value gì?
- Senior: Connection pool nhận rotated password thế nào?
- Architect: Secret-manager outage fail-open hay fail-closed?
- Expert: Compromise detection và mass rotation tránh thundering herd ra sao?

**Red flags:**

- Rotation bằng restart toàn fleet không rehearsal; secret dùng chung mọi environment/service.

**Evidence:**

- Theory: `NOT CREATED`
- Deep-dive: `NOT CREATED`
- Learning case: `CFG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SPR-CONFIG-010 — `ARCHITECT`

**Question:**

Một config change không đổi code vẫn có thể cần canary và rollback như thế nào? Thiết kế compatibility window cho timeout, pool size, feature flag và external endpoint.

**Target depth:** `D3-D4`

**Interviewer evaluates:**

- Có coi config là deployable change có blast radius và observability.

**Answer outline:**

1. Config có thể đổi concurrency/load/security/traffic path; review và risk-classify như code.
2. Schema config backward-compatible trong mixed fleet; unknown/new key handling explicit, default an toàn.
3. Canary với success/error/latency/saturation/security signals, automatic stop/rollback trigger và immutable revision.
4. Feature flag có owner, expiry, access control và removal plan; không dùng flag để che incompatible state indefinitely.

**Required trade-offs:**

- Dynamic config tăng tốc mitigation nhưng giảm reproducibility nếu thiếu revision/audit/atomic rollout.

**Follow-up ladder:**

- Foundation: Restart-required và dynamic property khác nhau gì?
- Senior: Pool-size change có thể khuếch đại DB failure thế nào?
- Architect: Atomic config bundle hay independent keys?
- Expert: Rollback config khi instances observe revisions khác nhau ra sao?

**Red flags:**

- Config change bypass review; feature flag vĩnh viễn không owner.

**Evidence:**

- Theory: `NOT CREATED`
- Deep-dive: `NOT CREATED`
- Learning case: `CFG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SPR-CONFIG-011 — `EXPERT`

**Question:**

Attacker hoặc deployment mistake có thể inject high-precedence config để mở diagnostic endpoint, đổi logging hoặc thay external URL. Thiết kế defense khi application vẫn cần legitimate overrides.

**Target depth:** `D4`

**Interviewer evaluates:**

- Có threat-model configuration supply chain, precedence và runtime verification.

**Answer outline:**

1. Inventory accepted sources/actors; chặn command-line/arbitrary JSON/unknown environment overrides nếu platform không cần.
2. Security-critical properties có allowlist/invariant validation; production policy không thể bị hạ chỉ bằng lower-trust source.
3. Protect deployment manifest/IAM/secret store, sign/audit revisions và expose only redacted origin/fingerprint.
4. Detect drift/config event, canary, revoke access và rollback revision; test malicious override paths.

**Required trade-offs:**

- Giảm override surface tăng assurance nhưng giảm emergency flexibility; break-glass cần narrow scope và expiry.

**Follow-up ladder:**

- Foundation: Precedence và trust có đồng nghĩa không?
- Senior: Key nào phải invariant thay vì overrideable?
- Architect: Platform admission policy kiểm tra config trước startup thế nào?
- Expert: Compromised orchestrator làm application-level guard còn hữu ích ở đâu?

**Red flags:**

- Tin mọi environment variable vì “chỉ ops sửa được”; log effective secrets để audit.

**Evidence:**

- Theory: `NOT CREATED`
- Deep-dive: `NOT CREATED`
- Learning case: `CFG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

### SPR-CONFIG-012 — `EXPERT`

**Question:**

Application cần remote config/secret để startup, nhưng provider outage xảy ra đúng lúc toàn fleet restart. Bạn thiết kế fail-fast, cached value, degraded mode và recovery boundary thế nào?

**Target depth:** `D4`

**Interviewer evaluates:**

- Có xử lý bootstrap circular dependency, freshness/security và fleet availability.

**Answer outline:**

1. Classify config bắt buộc cho correctness/security và config optional; required secret thiếu/stale quá policy phải fail-closed trước traffic.
2. Cache encrypted last-known-good với identity, version, TTL/rotation/revocation policy nếu threat model cho phép; không fallback default biết trước.
3. Stagger restart, local readiness, retry có backoff/jitter và provider capacity/bulkhead để tránh restart storm.
4. Break-glass restore có audited snapshot, owner/expiry; recovery verify revision và rotate/revoke nếu compromise possible.
5. Test provider unavailable, stale cache, partial fleet và rotation-during-outage bằng failure injection.

**Required trade-offs:**

- Strict fail-closed bảo vệ trust nhưng có thể làm outage toàn fleet; last-known-good tăng availability nhưng kéo dài exposure/staleness.

**Follow-up ladder:**

- Foundation: Liveness và readiness phản ánh failure này thế nào?
- Senior: Retry storm được giới hạn bằng gì?
- Architect: Secret provider SLO/RTO trở thành application dependency ra sao?
- Expert: Revocation khẩn cấp xung đột cached credential thế nào?

**Red flags:**

- Retry vô hạn trong startup; fallback hard-coded secret; mọi config đều fail-open để giữ availability.

**Evidence:**

- Theory: `NOT CREATED`
- Deep-dive: `NOT CREATED`
- Learning case: `CFG-01 NOT CREATED`
- Tests/experiment: `NOT RUN`
- Interview note: `NOT CREATED`

**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `CFG-01` thực sự active:

1. Pin exact Spring Boot candidate và re-check property precedence, profiles, Actuator exposure/security và config binding behavior.
2. Đọc core/deep-dive đã link ở header; chỉ thay evidence marker sau khi learner hoàn tất và có kết quả thật.
3. Audit actual profiles, conditional beans, defaults, diagnostic mappings và logging without printing sensitive values.
4. Tạo production-context negative tests và fail-fast reproducer; chỉ ghi evidence từ output thật.
5. Giữ stable IDs; protocol-specific token/webhook/stream-key questions link sang `SEC-*` thay vì làm phình `CFG-01`.

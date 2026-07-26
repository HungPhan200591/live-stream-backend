# Configuration binding, auto-configuration và an toàn secret

> Type: `DEEP_DIVE`<br>
> Domain: `spring`<br>
> Target depth: `D3 — audit effective config/conditions và chứng minh production safety bằng startup matrix`<br>
> Teaching readiness: `TEACHABLE_DRAFT`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Configuration core](../core/configuration-and-profile-safety.md)<br>
> Related cases: [`CONFIG-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`AUTHZ-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

## 0. Mental model và cách học

Audit config bằng provenance graph: canonical key, candidates, winning origin, typed value, validation và bean outcome. Secret value bị che nhưng origin/version vẫn quan sát được. Upgrade dependency được coi là input có thể thay condition graph dù application source không đổi.

```mermaid
flowchart TB
    K["Tên key chuẩn"] --> P["Các nguồn có thể cấp<br/>+ thứ tự ưu tiên"]
    P --> W["Nguồn thắng<br/>giá trị đã che"]
    W --> B["Bind đúng kiểu<br/>validate nhiều field"]
    B --> C["Kết quả condition"]
    C --> G["Bean graph<br/>capability thực tế"]
    style K fill:#4CAF50,stroke:#fff,stroke-width:2px,color:#fff
    style P fill:#2196F3,stroke:#fff,stroke-width:2px,color:#fff
    style W fill:#9C27B0,stroke:#fff,stroke-width:2px,color:#fff
    style B fill:#FF9800,stroke:#fff,stroke-width:2px,color:#fff
    style C fill:#607D8B,stroke:#fff,stroke-width:2px,color:#fff
    style G fill:#F44336,stroke:#fff,stroke-width:2px,color:#fff
```

## 1. Binding and precedence reasoning

Worked example: `pool.size=20` và `queue.size=0` có thể từng field hợp lệ nhưng cặp không phù hợp policy. Typed immutable configuration cần class-level validation. Nếu runtime refresh hai field riêng, intermediate snapshot có thể invalid; publish nguyên snapshot versioned/atomic phù hợp hơn.

Config audit bắt đầu bằng key canonical, bound type/unit, all possible sources và winning source. YAML nesting, environment-variable transformation, command-line/system properties và test overrides có thể tạo effective value khác file đang đọc. Unknown/deprecated key policy cần rõ để typo không silently fall back.

Một nhóm property liên quan như connect/read timeout hoặc pool size/queue size cần cross-field validation. Runtime refresh từng field có thể tạo intermediate invalid state; immutable snapshot/versioned refresh an toàn hơn khi feature hỗ trợ dynamic config.

## 2. Auto-configuration reasoning

Auto-configuration là conditional bean definitions dựa trên classpath, bean absence/presence, properties và environment. User bean có thể làm default back off. Dependency upgrade/classpath change vì vậy có thể đổi graph mà source application không đổi.

Condition evaluation report giúp giải thích match/no-match. Test phải assert capability outcome, không snapshot toàn report dễ brittle. `@ConditionalOnMissingBean` cũng có ordering/search-scope nuance; exact behavior cần xem docs/version active.

## 3. Secret and production safety

Rotation không chỉ thay value: producer/consumer có thể cần overlap key versions, audit active key ID và rollback plan. Missing JWT/webhook key phải fail closed/startup; optional analytics endpoint có thể disable kèm health/metric. Tuyệt đối không “degrade” authentication bằng default secret.

Secret value không nằm trong git, default config, exception, actuator dump hoặc command history. Rotation cần overlap/version/refresh/restart plan. Authorization/webhook/JWT secret missing hoặc placeholder phải fail closed/fail startup; optional analytics endpoint có thể degrade nếu policy ghi rõ.

Production guard nên kiểm tra forbidden profiles/dev endpoints/default credentials, excessive SQL/security logging, schema destructive settings và exposed diagnostics. Guard là defense-in-depth, không thay deployment policy/secret manager.

## 4. Failure injection matrix

| Injection | Expected behavior |
| --- | --- |
| Required key missing | Startup fails with safe actionable message |
| Invalid duration/range | Binding validation fails |
| Dev profile in prod | Guard rejects startup |
| Secret provider unavailable | Fail policy matches criticality |
| User override bean present | Auto-config backs off as designed |
| Optional class absent | Capability disabled and observable |

Evidence `NOT RUN`; bảng là acceptance plan.

## 5. Operability trade-offs

| Choice | Benefit | Cost/risk |
| --- | --- | --- |
| Fail-fast | Không chạy invalid state | Availability giảm nếu dependency config tạm lỗi |
| Default fallback | Startup dễ | Silent unsafe behavior |
| Runtime refresh | No restart | Consistency/rollback/audit complexity |
| Restart rollout | Immutable process config | Deployment latency |
| Central secret/config service | Rotation/audit | Bootstrap dependency |

### 5.1. Pathology A — typo tạo fallback hợp lệ nhưng sai

Deployment đặt `PAYMENT_TIMEOUT_MS`, trong khi canonical key bind là `payment.timeout`. Binding class có default 30 giây nên startup xanh, nhưng production giữ connections quá lâu. Search YAML không thấy nguyên nhân vì winning input đến từ environment và typo bị bỏ qua. Guard đúng cần typed `@ConfigurationProperties`, validation/range, policy cho unknown/deprecated keys và startup diagnostic chỉ log key/origin—not secret value.

Evidence matrix có ba branches: missing required key phải fail; valid override phải bind đúng origin/unit; unknown/typo phải fail hoặc tạo signal theo policy. Test không được chỉ instantiate POJO vì bỏ qua real relaxed binding/property-source precedence.

### 5.2. Pathology B — dependency upgrade silently đổi bean graph

Upgrade starter thêm class vào classpath hoặc đổi condition ordering. Một user bean khiến `@ConditionalOnMissingBean` default back off, hoặc capability trước disabled nay bật với unsafe default. Source application không đổi nhưng actuator/security/executor surface thay. Condition report dùng để chẩn đoán, còn regression test assert capability/bean outcome và forbidden exposure, không snapshot mọi dòng report dễ brittle.

Chạy startup matrix với optional class present/absent, property enabled/disabled/invalid và user override. Pin Spring Boot baseline vì condition names/defaults có thể thay theo version.

### 5.3. Pathology C — secret rotation làm một nửa fleet không verify được

Signer bắt đầu dùng key K2 ngay khi verifier pods khác chỉ biết K1. Requests hợp lệ fail ngẫu nhiên theo route. Rotation protocol cần key ID/version, publish/verify overlap `K1 + K2`, chuyển signer sang K2, quan sát, rồi retire K1 sau token/webhook retry horizon. Rollback giữ K1 trong overlap. Missing provider hoặc placeholder ở auth path phải fail closed/startup, không dùng default secret.

### 5.4. Diagnostic/experiment walkthrough

1. Ghi canonical key, type/unit/default/criticality và allowed sources.
2. Chạy real application context theo environment matrix, capture redacted winning origin và condition outcome.
3. Inject missing/invalid/unknown/profile-forbidden cases; assert startup/capability behavior và safe message.
4. Upgrade candidate dependency, diff capability surface và security exposure—not just config files.
5. Diễn tập rotation overlap/rollback với key IDs, không log material; assert old/new verify window.
6. Ghi exact Boot/config-provider versions. Evidence `NOT RUN` cho tới khi matrix chạy.

### 5.5. Cách lần từ một triệu chứng production về nguồn cấu hình

Khi production dùng timeout khác dự kiến, đừng bắt đầu bằng việc sửa YAML. Hãy lần theo chuỗi: tên property chuẩn → tất cả property source → nguồn thắng theo precedence → giá trị sau relaxed binding/chuyển đơn vị → validation → condition → bean đang thật sự được tạo. Ví dụ `PAYMENT_TIMEOUT=30` có thể được bind thành 30 mili-giây hoặc 30 giây tùy kiểu field và cách viết; một default hợp lệ có thể che việc environment variable sai tên. Log an toàn chỉ nên ghi key, origin, kiểu và key version, không ghi secret value.

Với auto-configuration, cần hỏi capability nào đã thay đổi chứ không chỉ bean nào xuất hiện. Một dependency mới thêm class vào classpath có thể làm condition match, tạo scheduler hoặc endpoint ngoài dự kiến. Ngược lại, một user bean vô tình làm default back off và bỏ mất timeout/retry chuẩn. Regression test nên assert capability quan sát được — endpoint có bị expose, authentication có fail closed, executor có bound — thay vì snapshot toàn bộ condition report vốn dễ đổi giữa patch version.

Version boundary phải pin Spring Boot, Spring Framework và config/secret provider. Khi nâng phiên bản, chạy lại ma trận `missing/invalid/valid/unknown` cùng `class present/absent`, `bean override/no override` và rotation `K1 -> K1+K2 -> K2`. Chỉ khi raw startup outcome, health/metric và log đã redact được lưu lại mới có evidence; nội dung ở đây vẫn là kế hoạch `NOT RUN`.

## 6. Dàn ý phỏng vấn, tóm tắt và phần người học viết lại

Trình bày provenance→binding→validation→condition→capability, rồi production guard và rotation. Nêu startup failure-injection matrix và dependency-upgrade regression test.

- Unknown key/typo policy quan trọng như precedence.
- Condition graph phụ thuộc classpath/bean/property/order.
- Secret criticality quyết định fail policy.
- Runtime refresh cần atomic multi-property snapshot.

`LEARNER TODO — audit một config group và lập enabled/disabled/invalid matrix.`

## 7. Guided self-check

1. **Question:** Chứng minh source thắng an toàn?<br>**Đọc lại nếu bí:** mental model, mục 1.<br>**Rubric:** canonical key, origin/precedence/key version, redacted value.<br>**My answer:** `LEARNER TODO`
2. **Question:** Upgrade đổi graph ra sao?<br>**Đọc lại nếu bí:** mục 2, 4.<br>**Rubric:** classpath/conditions/default backoff and capability assertion.<br>**My answer:** `LEARNER TODO`
3. **Question:** Fail hay degrade?<br>**Đọc lại nếu bí:** mục 3–5.<br>**Rubric:** security/data-critical fail closed; optional observable disable; explicit policy/test.<br>**My answer:** `LEARNER TODO`

## 8. References

- [Spring Boot — Externalized Configuration](https://docs.spring.io/spring-boot/reference/features/external-config.html)
- [Spring Boot — Creating Auto-configuration](https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html)

## 9. Teach-back checklist

- [ ] Tôi audit precedence/binding/condition theo evidence.
- [ ] Tôi có production guard và secret rotation story.
- [ ] Tôi test enabled/disabled/invalid startup branches.
- [ ] Evidence vẫn `NOT RUN`.

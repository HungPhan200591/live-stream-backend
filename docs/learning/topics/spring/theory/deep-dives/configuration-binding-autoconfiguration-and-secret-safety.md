# Configuration Binding, Auto-configuration and Secret Safety

> Type: `DEEP_DIVE`<br>
> Domain: `spring`<br>
> Target depth: `D3 — audit effective config/conditions và chứng minh production safety bằng startup matrix`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Configuration core](../core/configuration-and-profile-safety.md)<br>
> Related cases: [`CONFIG-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`AUTHZ-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

## 1. Binding and precedence reasoning

Config audit bắt đầu bằng key canonical, bound type/unit, all possible sources và winning source. YAML nesting, environment-variable transformation, command-line/system properties và test overrides có thể tạo effective value khác file đang đọc. Unknown/deprecated key policy cần rõ để typo không silently fall back.

Một nhóm property liên quan như connect/read timeout hoặc pool size/queue size cần cross-field validation. Runtime refresh từng field có thể tạo intermediate invalid state; immutable snapshot/versioned refresh an toàn hơn khi feature hỗ trợ dynamic config.

## 2. Auto-configuration reasoning

Auto-configuration là conditional bean definitions dựa trên classpath, bean absence/presence, properties và environment. User bean có thể làm default back off. Dependency upgrade/classpath change vì vậy có thể đổi graph mà source application không đổi.

Condition evaluation report giúp giải thích match/no-match. Test phải assert capability outcome, không snapshot toàn report dễ brittle. `@ConditionalOnMissingBean` cũng có ordering/search-scope nuance; exact behavior cần xem docs/version active.

## 3. Secret and production safety

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

## 6. Self-check

1. **Question:** Làm sao chứng minh source nào thắng mà không log value bí mật?<br>**My answer:** `LEARNER TODO`
2. **Question:** Upgrade dependency có thể đổi auto-config graph ra sao?<br>**My answer:** `LEARNER TODO`
3. **Question:** Config nào phải fail startup, config nào được degrade?<br>**My answer:** `LEARNER TODO`

## 7. References

- [Spring Boot — Externalized Configuration](https://docs.spring.io/spring-boot/reference/features/external-config.html)
- [Spring Boot — Creating Auto-configuration](https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html)

## 8. Teach-back checklist

- [ ] Tôi audit precedence/binding/condition theo evidence.
- [ ] Tôi có production guard và secret rotation story.
- [ ] Tôi test enabled/disabled/invalid startup branches.
- [ ] Evidence vẫn `NOT RUN`.

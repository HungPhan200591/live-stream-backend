# Configuration and Profile Safety

> Type: `CORE`<br>
> Domain: `spring`<br>
> Target depth: `D3 — truy nguồn property, kiểm chứng binding/validation và ngăn cấu hình dev rò sang production`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [IoC and bean lifecycle](ioc-bean-lifecycle-and-dependency-injection.md)<br>
> Related cases: [`CONFIG-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`SPRING-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

Source canonical cho [configuration question bank](../../question-bank/configuration-isolation-and-production-safety.md). Exact precedence/auto-configuration behavior phải re-check theo Spring Boot baseline khi topic active.

## 1. Learning objectives

1. Phân biệt property source, profile, typed binding, validation và conditional bean creation.
2. Truy nguồn effective value mà không log secret.
3. Thiết kế fail-fast, environment isolation và startup tests cho production safety.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — mô tả config đi từ external source qua Environment/binding đến bean và nơi validation/condition thất bại.`

## 3. Cơ chế cốt lõi

Spring `Environment` kết hợp profiles và property sources. Spring Boot thêm externalized configuration, typed `@ConfigurationProperties`, auto-configuration conditions và diagnostic reports. Nhiều source có precedence; effective value phải được xác định từ source/order cụ thể, không suy đoán từ một file YAML.

Typed binding gom cấu hình theo namespace, biểu diễn kiểu/units và hỗ trợ validation. Profile là coarse environment grouping, không phải authorization hoặc secret store. Conditional configuration làm bean graph thay đổi theo classpath/property/bean presence; mọi nhánh quan trọng cần startup test.

Secret phải đến từ kênh phù hợp, được redact và rotate; default credential/dev endpoint không được âm thầm hoạt động ở production. Fail-fast phù hợp cho invariant cấu hình bắt buộc; graceful degradation chỉ dùng khi capability thật sự optional và observable.

## 4. Invariants và boundaries

1. Production không có seed/default credential/dev endpoint hoặc verbose sensitive logging.
2. Required URL, timeout, pool size, key/secret reference có typed validation và unit rõ.
3. Effective config có thể audit theo source nhưng secret value không xuất hiện trong log/error.
4. Profile/condition matrix quan trọng có context-startup test.
5. Runtime refresh không làm vỡ atomicity của multi-property invariant.

## 5. Failure modes

| Failure | Trigger | Symptom |
| --- | --- | --- |
| Precedence surprise | Env/CLI overrides file | Sai endpoint/limit |
| Relaxed-binding typo | Tên gần giống/unknown field | Default nguy hiểm hoặc startup fail |
| Profile leakage | Dev bean/property active | Security/data exposure |
| Secret leakage | Actuator/log/error dump | Credential compromise |
| Conditional drift | Classpath/bean change | Bean graph khác sau upgrade |
| Partial refresh | Related values đổi lệch | Invalid runtime state |

## 6. Patterns và trade-off

| Pattern | Lợi ích | Trade-off |
| --- | --- | --- |
| Typed immutable properties | Contract rõ | Migration khi key đổi |
| Validation + fail-fast | Lỗi sớm | Optional environments cần defaults có chủ ý |
| Explicit production denylist/assertion | Chặn dev capability | Maintenance rule |
| Secret reference/provider | Rotation/audit | Availability/bootstrap dependency |
| Startup context matrix | Chứng minh condition | Test time và fixture |

## 7. Deep-dive và case

- [Configuration binding, auto-configuration and secret safety](../deep-dives/configuration-binding-autoconfiguration-and-secret-safety.md).
- `CONFIG-UC-01`: dev/prod isolation và config regression.
- `SPRING-UC-01`: condition report và bean graph.

## 8. Self-check

1. **Question:** Profile, property source và conditional bean khác nhau thế nào?<br>**My answer:** `LEARNER TODO`
2. **Question:** Bạn xác định effective value mà không lộ secret ra sao?<br>**My answer:** `LEARNER TODO`
3. **Question:** Khi nào fail-fast, khi nào degrade và test matrix nào bắt buộc?<br>**My answer:** `LEARNER TODO`

## 9. Official references

- [Spring — Environment Abstraction](https://docs.spring.io/spring-framework/reference/core/beans/environment.html)
- [Spring Boot — Externalized Configuration](https://docs.spring.io/spring-boot/reference/features/external-config.html)
- [Spring Boot — Auto-configuration](https://docs.spring.io/spring-boot/reference/using/auto-configuration.html)

## 10. Teach-back checklist

- [ ] Tôi truy nguồn precedence và condition thay vì đoán.
- [ ] Tôi tách profile khỏi secret/security control.
- [ ] Tôi thiết kế fail-fast, redaction và startup matrix.
- [ ] Configuration evidence vẫn `NOT RUN`.

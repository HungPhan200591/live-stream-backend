# API Pagination, Versioning and Network Boundaries

> Type: `CORE`<br>
> Domain: `architecture`<br>
> Target depth: `D3 — thiết kế pagination/evolution contract và kiểm chứng behavior qua proxy, retry và concurrent writes`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [HTTP semantics](http-rest-semantics-and-idempotency.md), database ordering fundamentals<br>
> Related cases: [`FEED-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`API-UC-01`](../../../../use-case-catalog.md#32-architect-và-expert-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

Source canonical cho [API boundary question bank](../../question-bank/api-pagination-versioning-and-network-boundaries.md).

## 1. Learning objectives

1. Chọn offset/cursor/keyset theo ordering, mutation và navigation requirement.
2. Tiến hóa request/response có compatibility, deprecation và migration plan.
3. Xử lý proxy/gateway limits, rate quota, timeout và partial/streaming response boundary.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — mô tả client lấy ba trang trong khi item mới được chèn/xóa và cursor phải giữ invariant nào.`

## 3. Cơ chế cốt lõi

Offset pagination dễ random access nhưng cost tăng theo offset và dễ skip/duplicate khi dataset thay đổi. Keyset/cursor dùng stable total order cùng last-seen key; cursor cần encode opaque state, direction/filter context và integrity/version. Sort key không unique phải có tie-breaker.

Compatibility không chỉ là JSON field: status/header/default/order/nullability/error code, pagination và authorization đều là contract. Additive change thường an toàn hơn nhưng client strict parser/schema vẫn có thể vỡ. Breaking change cần version boundary, coexistence, telemetry, deprecation và removal gate.

Network intermediary có request/header/body/time limits, buffering, compression, retry và connection behavior riêng. API design phải xác định max page/body, timeout budget, backpressure/rate quota và correlation; không dựa vào in-process assumption.

## 4. Invariants và boundaries

1. Pagination có deterministic total order và document consistency expectation.
2. Cursor không cho caller sửa filter/tenant/position trái phép và không chứa secret/plain internal state nhạy cảm.
3. Contract change có consumer impact, telemetry và rollback/migration plan.
4. Quota/rate-limit scope đúng actor/tenant/cost; response hướng dẫn retry phù hợp.
5. Gateway/client/server timeout và body/header limits được align và test.

## 5. Failure modes

| Failure | Trigger | Symptom |
| --- | --- | --- |
| Offset under mutation | Insert/delete giữa pages | Skip/duplicate |
| Non-unique cursor sort | Equal timestamps | Missing/repeated rows |
| Cursor tampering | Unsigned filter/tenant state | Data exposure/query abuse |
| Silent breaking change | Rename/default/error drift | Client regression |
| Proxy buffering/limit | Large response/upload | Latency, `413`/`502`/timeout |
| Retry non-idempotent call | Gateway/client retry | Duplicate side effect |

## 6. Trade-off matrix

| Option | Strength | Cost/limit |
| --- | --- | --- |
| Offset/page number | Simple/random access | Deep cost, mutation drift |
| Keyset | Stable/fast forward scan | No arbitrary jump, query complexity |
| Opaque signed cursor | Evolvable/tamper-resistant | Key rotation/versioning |
| URI/header/media version | Explicit coexistence | Routing/cache/client complexity |
| Additive single version | Low overhead | Cannot absorb semantic breaks forever |

## 7. Deep-dive và case

- [Cursor pagination, compatible evolution and proxy boundaries](../deep-dives/cursor-pagination-compatible-evolution-and-proxy-boundaries.md).
- `FEED-UC-01`: stable feed pagination under writes.
- `API-UC-01`: compatibility, gateway and consumer migration.

## 8. Self-check

1. **Question:** Vì sao `createdAt` một mình thường chưa đủ làm cursor?<br>**My answer:** `LEARNER TODO`
2. **Question:** Một additive field khi nào vẫn breaking?<br>**My answer:** `LEARNER TODO`
3. **Question:** Bạn test API qua gateway/network boundary thế nào?<br>**My answer:** `LEARNER TODO`

## 9. Official references

- [RFC 9110 — HTTP Semantics](https://www.rfc-editor.org/rfc/rfc9110.html)
- [Spring MVC — HTTP Message Conversion](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/message-converters.html)

## 10. Teach-back checklist

- [ ] Tôi chứng minh pagination invariant dưới concurrent mutation.
- [ ] Tôi có compatibility/deprecation/telemetry story.
- [ ] Tôi tính gateway limits và retry semantics.
- [ ] API boundary evidence vẫn `NOT RUN`.

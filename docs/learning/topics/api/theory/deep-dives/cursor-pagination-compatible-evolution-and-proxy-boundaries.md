# Cursor Pagination, Compatible Evolution and Proxy Boundaries

> Type: `DEEP_DIVE`<br>
> Domain: `architecture`<br>
> Target depth: `D3 — chứng minh total-order pagination, consumer compatibility và intermediary behavior bằng contract tests`<br>
> Teaching readiness: `OUTLINE_ONLY`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [API boundary core](../core/api-pagination-versioning-and-network-boundaries.md)<br>
> Related cases: [`FEED-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`API-UC-01`](../../../../use-case-catalog.md#32-architect-và-expert-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

## 1. Cursor correctness

Với thứ tự giảm dần `(created_at, id)`, predicate của trang kế thường là phép so sánh từ điển “nhỏ hơn tuple cuối”, giữ nguyên filter và ordering. Tie-breaker làm thứ tự trở thành total order. Đảo chiều, nullable key và đổi filter cần semantics tường minh; index order phải hỗ trợ predicate/order nếu không latency sẽ thoái hóa khi scale.

Cursor payload can include version, last sort tuple, filter hash, tenant/subject scope and direction, then be encoded and integrity-protected. It should be opaque so clients do not construct it. Signing prevents tampering but not confidentiality; encryption is separate if payload sensitive.

Concurrent inserts before current position generally appear on refresh, not later page; deletes can shrink results. Snapshot-consistent traversal requires snapshot/token/storage support and higher cost. API must state whether it offers stable traversal, snapshot or best-effort feed semantics.

## 2. Evolution hazards

“Additive” can still break strict schema consumers, exhaustive enums, signatures, payload limits or clients interpreting unknown fields badly. Changing default sort, timestamp precision/timezone, nullability, numeric width, error status/code or authorization filtering is semantic evolution even if JSON names stay.

Version rollout needs producer/consumer compatibility window, contract tests, usage telemetry, deprecation communication and removal criteria. Dual write/read or tolerant reader patterns may help migrations; they need rollback and data reconciliation.

## 3. Proxy/gateway boundary

Gateway can normalize/drop headers, limit URL/header/body, buffer uploads/responses, retry requests, terminate TLS, compress and enforce timeouts/rate limits. Test should traverse the same proxy path for critical contracts. Correlation/client IP/security headers must have trusted-proxy rules, not accept spoofed public headers.

## 4. Experiment matrix

| Experiment | Assertion |
| --- | --- |
| Equal timestamps across page boundary | No missing/duplicate with tie-breaker |
| Insert/delete between page calls | Behavior matches documented consistency |
| Cursor tamper/filter/tenant change | Rejected safely |
| Old/new consumer against old/new producer | Compatibility matrix passes |
| Oversize body/header and slow response through gateway | Expected status/timeout, no partial side effect |
| Gateway retry of command | Idempotency prevents duplicate |

Toàn bộ evidence vẫn `NOT RUN`.

## 5. Trade-off matrix

| Decision | Benefit | Consequence |
| --- | --- | --- |
| Signed stateless cursor | No server session | Rotation/payload evolution |
| Server-side cursor token | Hide state/revoke | Storage/TTL/affinity |
| Snapshot traversal | Strong consistency | Resource/storage cost |
| Best-effort feed | Simple/scalable | Refresh may reorder/miss historical view |
| Long coexistence versions | Migration safety | Maintenance/observability cost |

## 6. Self-check

1. **Question:** Viết predicate page kế cho sort `(createdAt DESC, id DESC)`.<br>**My answer:** `LEARNER TODO`
2. **Question:** Cursor ký số bảo vệ điều gì và không bảo vệ điều gì?<br>**My answer:** `LEARNER TODO`
3. **Question:** Compatibility matrix và removal gate gồm những gì?<br>**My answer:** `LEARNER TODO`

## 7. References

- [RFC 9110 — HTTP Semantics](https://www.rfc-editor.org/rfc/rfc9110.html)
- [OWASP — REST Security Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/REST_Security_Cheat_Sheet.html)

## 8. Teach-back checklist

- [ ] Tôi chứng minh query/order/index/cursor cùng một invariant.
- [ ] Tôi nhận diện semantic breaking changes.
- [ ] Tôi test qua trusted proxy boundary.
- [ ] Evidence vẫn `NOT RUN`.

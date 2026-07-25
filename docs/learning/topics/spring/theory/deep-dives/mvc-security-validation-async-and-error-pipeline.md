# MVC Security, Validation, Async and Error Pipeline

> Type: `DEEP_DIVE`<br>
> Domain: `spring`<br>
> Target depth: `D3 — tái hiện dispatch/error/async branches và chứng minh authorization + contract không drift`<br>
> Teaching readiness: `OUTLINE_ONLY`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [MVC pipeline core](../core/mvc-request-pipeline-validation-and-error-handling.md)<br>
> Related cases: [`AUTHZ-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`CREATE-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

## 1. Pipeline detail

Filters can run on REQUEST, ASYNC and ERROR dispatch depending on registration. Security authentication/authorization may reject before MVC mapping. Handler interceptors see mapped handler but are not a substitute for the security filter chain or service ownership checks.

Argument resolution includes path/query/header/body conversion and validation. `BindingResult` handling can change whether controller receives errors or framework throws. Method validation and argument validation can raise different exception types across framework baselines, so the global handler must have regression tests for the project version.

Exception resolution order can be affected by local handlers, controller advice ordering, response status mapping and default resolver. Once response is committed or streaming has started, a later exception cannot reliably replace headers/body with the normal error envelope.

## 2. Security and error invariants

1. `401` means authentication needed/invalid; `403` means authenticated request lacks permission under the contract.
2. Resource existence must not be leaked when ownership policy intentionally masks it.
3. Error body is stable, machine-readable and does not include stack trace, SQL, token or internal class names.
4. Validation error order/content is deterministic enough for clients/tests without coupling to provider internals.
5. Async timeout/error path clears context and completes resource exactly once.

## 3. Pathological cases

| Case | Why difficult | Evidence needed |
| --- | --- | --- |
| Error after response committed | Cannot rewrite contract | Streaming/flush integration test |
| Async timeout vs completion race | Double callback/release | Controlled barrier test |
| Ownership check after entity load | Existence/timing leak | 403/404 policy tests |
| Converter exception bypasses controller | Different resolver path | Malformed payload test |
| Multiple advice handlers | Order ambiguity | Exact exception/status assertion |

## 4. Test matrix

- Authentication: missing, malformed, expired/session-invalid, valid.
- Authorization: wrong role, wrong owner, banned/muted/state-invalid.
- Input: malformed JSON, missing field, boundary value, cross-field/business invariant.
- Output: serialization failure, async timeout, exception before/after commit.
- Contract: content type, status, envelope/error code, correlation and no sensitive detail.

Tests/evidence vẫn `NOT RUN`.

## 5. Async boundary

Servlet async frees the container request thread while work completes later; it does not make downstream work non-blocking. Timeout budget, executor capacity, context propagation, cancellation and dispatch type all need configuration. Virtual threads change thread cost, not DB connection/remote service capacity or security correctness.

## 6. Trade-off matrix

| Choice | Advantage | Risk |
| --- | --- | --- |
| One global advice | Consistency | Over-broad catch/order |
| Domain-specific mapper | Precise semantics | More mapping code |
| Mask forbidden as not-found | Reduce enumeration | Debug/support ambiguity |
| Async MVC | Request-thread efficiency | Context/race/timeout complexity |
| Streaming | Time-to-first-byte | Error contract after commit limited |

## 7. Self-check

1. **Question:** Malformed JSON đi qua branch nào và controller có chạy không?<br>**My answer:** `LEARNER TODO`
2. **Question:** Vì sao async timeout có thể race với successful completion?<br>**My answer:** `LEARNER TODO`
3. **Question:** Khi nào 404 thay 403 là policy có chủ ý?<br>**My answer:** `LEARNER TODO`

## 8. References

- [Spring MVC — Processing](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet/sequence.html)
- [Spring MVC — Asynchronous Requests](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-async.html)

## 9. Teach-back checklist

- [ ] Tôi trace REQUEST/ASYNC/ERROR branches.
- [ ] Tôi test security policy và no-leak error contract.
- [ ] Tôi nêu response-commit limitation.
- [ ] Evidence vẫn `NOT RUN`.

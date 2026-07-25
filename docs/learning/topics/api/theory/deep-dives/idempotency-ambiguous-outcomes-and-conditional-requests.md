# Idempotency, Ambiguous Outcomes and Conditional Requests

> Type: `DEEP_DIVE`<br>
> Domain: `architecture`<br>
> Target depth: `D3 — thiết kế state machine/durable claim và fault-inject mọi commit/response boundary`<br>
> Teaching readiness: `OUTLINE_ONLY`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [HTTP/REST core](../core/http-rest-semantics-and-idempotency.md)<br>
> Related cases: [`GIFT-UC-01`](../../../../use-case-catalog.md#gift-uc-01), [`FOLLOW-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

## 1. Ambiguous outcome model

Client quan sát timeout/disconnect chỉ biết không nhận được response; server có thể chưa bắt đầu, đang chạy, đã rollback hoặc đã commit. Vì vậy, retry command cần business identity và outcome recovery, không thể dựa vào trực giác ở transport layer.

Một idempotency record thực tế thường có scoped key, request fingerprint, state, owner/lease/version, result reference/response metadata, timestamps và expiry. Atomic insert/unique constraint elects one owner. Concurrent duplicate either waits/polls, gets an in-progress response, or retrieves completed outcome according to contract.

`FAILED` cần taxonomy: permanent validation failure có thể replay stable result; transient/infrastructure failure có thể cho takeover/retry; unknown crash state phải reconcile với business record. TTL must exceed realistic retry/offline window or business identity must remain protected elsewhere.

## 2. State machine and invariants

`ABSENT -> IN_PROGRESS -> COMPLETED` là đường chính. Lease expiry/takeover không được tạo hai owners cùng commit; fencing/version hoặc DB transaction may be needed. Business row và idempotency outcome tốt nhất commit cùng durable transaction khi cùng database.

Invariants:

1. Same scoped key + same fingerprint has at most one committed business effect.
2. Same key + different fingerprint is a conflict/security signal.
3. Completed result remains authorized for current caller and contract version.
4. Cleanup never removes the only durable duplicate guard too early.

## 3. Conditional requests

`ETag`/`If-Match` can express optimistic precondition for resource state; a failed precondition prevents overwriting a version the client did not observe. It solves concurrent resource update, not arbitrary duplicate command unless command semantics map cleanly to resource replacement/version.

Weak versus strong validators and intermediary/cache behavior matter. Validator generation must change whenever representation semantics requiring freshness change; avoid exposing sensitive internal version/state unnecessarily.

## 4. Fault matrix

| Fault point | State to inspect |
| --- | --- |
| Before idempotency claim | No owner/effect |
| After claim, before business write | Recoverable stale in-progress |
| After business write, before commit | Both rollback if one transaction |
| After commit, before response | Duplicate retrieves committed result |
| During result serialization/storage | Effect/result reconciliation |
| Concurrent same/different payload | One effect / conflict |

Chưa fault injection nào được chạy; evidence `NOT RUN`.

## 5. Trade-off matrix

| Storage/design | Correctness | Operability |
| --- | --- | --- |
| DB unique + same tx | Strong local atomicity | Table growth/cleanup |
| Redis `SET NX` + TTL | Fast claim | Crash/expiry/durability gaps |
| Business natural key | Minimal extra state | Only fits natural identity |
| Full response replay | Stable client outcome | PII/size/version retention |
| Result reference replay | Smaller | Reconstruct/auth/version logic |

## 6. Self-check

1. **Question:** Lease expired owner và old owner cùng chạy thì fencing làm gì?<br>**My answer:** `LEARNER TODO`
2. **Question:** TTL nào đủ và cleanup chứng minh an toàn ra sao?<br>**My answer:** `LEARNER TODO`
3. **Question:** `If-Match` giải quyết gì mà idempotency key không giải quyết, và ngược lại?<br>**My answer:** `LEARNER TODO`

## 7. References

- [RFC 9110 — Idempotent Methods](https://www.rfc-editor.org/rfc/rfc9110.html#name-idempotent-methods)
- [RFC 9110 — Preconditions](https://www.rfc-editor.org/rfc/rfc9110.html#name-preconditions)

## 8. Teach-back checklist

- [ ] Tôi reason từ client-observable ambiguity.
- [ ] Tôi có atomic claim/state/TTL/fencing story.
- [ ] Tôi phân biệt duplicate command với concurrent resource update.
- [ ] Evidence vẫn `NOT RUN`.

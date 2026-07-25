# Retry, Backoff, Jitter and Retry Storms

> Type: `CORE`<br>
> Domain: `distributed-systems`<br>
> Target depth: `D3 — phân loại retryable failure, tái hiện amplification và kiểm chứng budget/backoff/jitter`<br>
> Teaching readiness: `OUTLINE_ONLY`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Timeouts and cancellation](timeouts-cancellation-and-pool-exhaustion.md), [HTTP idempotency](../../../api/theory/core/http-rest-semantics-and-idempotency.md)<br>
> Related cases: [`RECONNECT-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`EVT-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

Source canonical cho [retry question bank](../../question-bank/retry-backoff-jitter-and-retry-storms.md).

## 1. Learning objectives

1. Retry chỉ transient failure và operation idempotent/deduplicated trong remaining deadline.
2. Giải thích exponential backoff, jitter, retry budget và multiplicative retry layers.
3. Thiết kế fault experiment đo attempts, offered load, success, latency và recovery.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — mô tả 3 service, mỗi layer retry 3 lần, rồi tính worst-case attempts và deadline.`

## 3. Cơ chế cốt lõi

Retry biến một logical request thành nhiều attempts. Nó hữu ích khi failure transient và attempt sau có xác suất thành công; nó gây hại khi overload, validation/permanent error, non-idempotent side effect hoặc remaining budget không đủ.

Backoff giãn attempts; jitter phá đồng bộ giữa clients. Cần cap, max attempts, total deadline và retry budget theo traffic/capacity. Nếu gateway, service và client library đều retry, amplification nhân qua layers; nên chọn retry owner gần nơi hiểu semantics và quan sát outcome.

Retry sau timeout gặp ambiguous outcome: attempt đầu có thể đã commit. Idempotency/dedup và stable result là điều kiện business, không được thay bằng niềm tin rằng timeout nghĩa là chưa thực hiện.

## 4. Invariants và boundaries

1. Mỗi retry policy có owner, retryable taxonomy, max attempts, backoff/jitter và total deadline.
2. Không retry validation/auth/permanent conflict mặc định.
3. Side effect phải idempotent/deduplicated hoặc có compensation rõ.
4. Retry budget không làm offered load vượt recovery capacity.
5. Metrics tách logical requests, attempts và terminal outcomes.

## 5. Failure modes

| Failure | Trigger | Symptom |
| --- | --- | --- |
| Retry storm | Outage + immediate synchronized retry | Dependency không hồi phục |
| Layer multiplication | N layers retry | Attempts tăng theo tích |
| Deadline overrun | Attempt mới khi budget gần hết | Work vô ích, tail cao |
| Duplicate side effect | Timeout after commit | Double charge/event |
| Poison retry | Permanent error | Queue lag/cost/log noise |

## 6. Trade-off matrix

| Policy | Strength | Risk |
| --- | --- | --- |
| No retry | Load predictable | Transient failure lộ ra |
| Fixed delay | Simple | Synchronization/thundering herd |
| Exponential + jitter | Recovery friendly | Longer latency/tuning |
| Hedging | Tail reduction cho safe reads | Extra concurrent load |
| Queue redelivery | Durable retry | Lag, ordering, poison handling |

## 7. Deep-dive và case

- [Timeout, retry, circuit, bulkhead and overload control](../deep-dives/timeout-retry-circuit-bulkhead-and-overload-control.md).
- `RECONNECT-UC-01`: reconnect/backoff/jitter under regional recovery.
- `EVT-UC-01`: consumer redelivery, poison message và idempotency.

## 8. Self-check

1. **Question:** Khi nào retry làm availability tệ hơn?<br>**My answer:** `LEARNER TODO`
2. **Question:** Full jitter giải quyết synchronization ra sao?<br>**My answer:** `LEARNER TODO`
3. **Question:** Bạn đo retry amplification và recovery bằng metric nào?<br>**My answer:** `LEARNER TODO`

## 9. References

- [RFC 9110 — Retry-After semantics](https://www.rfc-editor.org/rfc/rfc9110.html#name-retry-after)
- [AWS Builders' Library — Timeouts, retries and backoff](https://aws.amazon.com/builders-library/timeouts-retries-and-backoff-with-jitter/)

## 10. Teach-back checklist

- [ ] Tôi phân loại transient/permanent/ambiguous outcome.
- [ ] Tôi tính amplification xuyên layers.
- [ ] Tôi gắn retry vào deadline, idempotency và capacity.
- [ ] Retry evidence vẫn `NOT RUN`.

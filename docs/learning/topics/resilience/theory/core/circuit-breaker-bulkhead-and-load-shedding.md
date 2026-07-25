# Circuit Breaker, Bulkhead and Load Shedding

> Type: `CORE`<br>
> Domain: `distributed-systems`<br>
> Target depth: `D3 — chọn isolation/overload control theo bottleneck và kiểm chứng state transition/recovery`<br>
> Teaching readiness: `OUTLINE_ONLY`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Timeouts and pool exhaustion](timeouts-cancellation-and-pool-exhaustion.md), [Retry storms](retry-backoff-jitter-and-retry-storms.md)<br>
> Related cases: [`CHAT-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`RATE-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`CACHE-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

Source canonical cho [circuit/bulkhead question bank](../../question-bank/circuit-breaker-bulkhead-and-load-shedding.md).

## 1. Learning objectives

1. Phân biệt circuit breaker, bulkhead, rate limiting, concurrency limiting và load shedding.
2. Chọn signal/window/threshold/fallback và tránh breaker flapping hoặc false recovery.
3. Chứng minh critical path còn capacity khi optional dependency bị chậm/hỏng.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — vẽ traffic đi qua admission control, bulkhead, timeout, breaker và fallback; chỉ control bảo vệ resource nào.`

## 3. Cơ chế cốt lõi

Circuit breaker quan sát outcome; khi failure/slow-call signal vượt threshold trong đủ sample, nó mở để fail fast, sau đó cho số probe giới hạn ở half-open. Nó giảm waste khi dependency unhealthy nhưng không tự giới hạn concurrency trước khi breaker đủ dữ liệu.

Bulkhead chia pool/semaphore/queue để failure của một dependency/tenant/workload không chiếm toàn capacity. Rate limiter giới hạn arrivals theo identity/time; concurrency limiter giới hạn in-flight; load shedding từ chối công việc khi system không còn budget. Chúng bảo vệ các bottleneck khác nhau và thường phải phối hợp.

Fallback chỉ hợp lệ nếu trả semantics degraded nhưng đúng: stale cache có age/authorization rule, optional enrichment có thể bỏ, money/security decision không được giả success. Recovery cần warm-up/probe/ramp thay vì mở floodgate.

## 4. Invariants và boundaries

1. Critical traffic có reserved/bounded capacity hoặc admission priority rõ.
2. Rejection/fallback không vi phạm security, money hoặc freshness invariant.
3. Breaker key/granularity không trộn unrelated dependency/tenant một cách gây blast radius.
4. Half-open probes bounded; recovery không tạo retry surge.
5. Metrics có state, rejection, saturation, fallback quality và downstream outcome.

## 5. Failure modes

| Failure | Trigger | Symptom |
| --- | --- | --- |
| Breaker flapping | Window/threshold/sample sai | Open/close liên tục |
| Shared pool collapse | Optional slow call chiếm threads/connections | Critical path timeout |
| Unsafe fallback | Stale/unauthorized/default success | Invariant/security violation |
| Global breaker | Một tenant/endpoint lỗi | Blast radius toàn service |
| Recovery surge | Half-open/unthrottle quá nhanh | Dependency ngã lại |

## 6. Trade-off matrix

| Control | Bảo vệ chính | Cost/risk |
| --- | --- | --- |
| Circuit breaker | Waste trên unhealthy dependency | Tuning/state/false open |
| Semaphore bulkhead | In-flight slots | Caller thread có thể block/reject |
| Thread/pool bulkhead | Isolation + queue | Context/resource overhead |
| Rate limiter | Arrival quota/fairness | Burst/user identity policy |
| Load shedding | Whole-system survival | Explicit degraded availability |

## 7. Deep-dive và case

- [Timeout, retry, circuit, bulkhead and overload control](../deep-dives/timeout-retry-circuit-bulkhead-and-overload-control.md).
- `CHAT-UC-01`: isolate fan-out/moderation/chat hot path.
- `RATE-UC-01`: fair admission and abuse traffic.
- `CACHE-UC-01`: stale fallback/fail-open/fail-closed decision.

## 8. Self-check

1. **Question:** Breaker khác concurrency limiter và rate limiter thế nào?<br>**My answer:** `LEARNER TODO`
2. **Question:** Half-open/recovery có thể tạo outage lần hai ra sao?<br>**My answer:** `LEARNER TODO`
3. **Question:** Fallback nào không chấp nhận được cho security/money path?<br>**My answer:** `LEARNER TODO`

## 9. References

- [Resilience4j — CircuitBreaker](https://resilience4j.readme.io/docs/circuitbreaker)
- [Resilience4j — Bulkhead](https://resilience4j.readme.io/docs/bulkhead)

## 10. Teach-back checklist

- [ ] Tôi chọn control theo resource/bottleneck cần bảo vệ.
- [ ] Tôi phân tích fallback correctness và recovery surge.
- [ ] Tôi có state/saturation/rejection evidence plan.
- [ ] Resilience evidence vẫn `NOT RUN`.

# Timeout, Retry, Circuit, Bulkhead and Overload Control

> Type: `DEEP_DIVE`<br>
> Domain: `distributed-systems`<br>
> Target depth: `D3 — thiết kế một resilience policy phối hợp và fault-test overload/recovery thay vì tuning từng annotation riêng lẻ`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Timeout core](../core/timeouts-cancellation-and-pool-exhaustion.md), [Retry core](../core/retry-backoff-jitter-and-retry-storms.md), [Circuit/bulkhead core](../core/circuit-breaker-bulkhead-and-load-shedding.md)<br>
> Related cases: [`RECONNECT-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`CHAT-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`RATE-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

## 1. One policy, not independent knobs

A useful order is admission/concurrency control before scarce work, then bounded pool acquisition, per-attempt timeout/cancellation, retry policy within total deadline, circuit observation/fail-fast and semantically safe fallback. Exact decorator order depends on whether breaker counts attempts or logical calls and whether bulkhead permit spans retries; choose and test it explicitly.

Budget must satisfy client deadline: queue + each attempt + backoff + response margin. Maximum attempts and concurrency imply offered load. If base traffic is near capacity, even one retry can prevent recovery. Breaker only reacts after samples; proactive admission/bulkhead protects capacity before it opens.

## 2. Overload feedback loop

Downstream slows → in-flight duration rises → pools/queues fill → requests wait and time out → callers retry → offered load rises → downstream slows further. Unbounded queues hide the first rejection while increasing memory and stale work. Recovery can repeat the loop when synchronized retries or half-open probes flood a cold dependency.

Controls break different edges:

- Deadline/cancellation bounds wasted duration.
- Retry budget/jitter bounds extra arrivals and synchronization.
- Bulkhead limits blast radius/in-flight use.
- Circuit avoids calls likely to fail.
- Rate/concurrency limiter/load shedding keeps admitted work near capacity.

## 3. Sizing and signals

Use measured service time, arrival rate, connection/thread limits and target utilization; do not copy pool sizes. Observe logical request rate, attempt rate, queue/acquire wait, in-use capacity, timeout by phase, cancellation completion, breaker state, rejected/fallback count, downstream latency/error and recovery time.

Breaker minimum sample, window and thresholds need traffic awareness. Low-volume endpoints may stay closed without enough samples or react slowly; high-volume global keys may overreact to one tenant/partition. Slow-call signal can open before hard failures but must align with actual deadline/SLO.

## 4. Fault scenarios

| Injection | Correct response to prove |
| --- | --- |
| Added downstream latency | Queue bounded, deadlines honored, critical capacity remains |
| Connection refusal/DNS failure | Correct phase timeout/retry classification |
| Partial 5xx/429 | Retry respects semantics/`Retry-After`/budget |
| One tenant hot key | Isolation/fairness prevents global breaker/pool collapse |
| Dependency recovery | Bounded probes/ramp, no second surge |
| Caller cancellation | Downstream/task/permit/connection released |

No experiment has run; evidence `NOT RUN`.

## 5. Fallback correctness

| Workload | Possible fallback | Forbidden shortcut |
| --- | --- | --- |
| Public stream metadata | Bounded-age cached snapshot | Cross-tenant/private stale data |
| Optional recommendation | Empty/omit enrichment | Claim personalized result is fresh |
| Chat send | Explicit reject/queue contract | Return success then silently drop |
| Authorization/ban | Fail closed or explicit policy | Allow because cache/dependency down |
| Gift/payment | Pending/idempotent recovery | Fabricated success/double charge |

## 6. Trade-off matrix

| Strategy | Availability | Correctness/capacity consequence |
| --- | --- | --- |
| Aggressive retry | May hide transient fault | Amplification/duplicates |
| Aggressive fail-fast | Protects capacity | More visible errors |
| Large pools/queues | Absorb short burst | Longer tail/memory/downstream pressure |
| Strict bulkheads | Strong isolation | Stranded capacity/rejections |
| Stale fallback | Read availability | Freshness/security policy required |

## 7. Self-check

1. **Question:** Vẽ causal loop của retry storm và control phá từng cạnh.<br>**My answer:** `LEARNER TODO`
2. **Question:** Breaker nên đếm attempt hay logical request trong design của bạn?<br>**My answer:** `LEARNER TODO`
3. **Question:** Fault test nào chứng minh recovery không tạo outage lần hai?<br>**My answer:** `LEARNER TODO`

## 8. References

- [AWS Builders' Library — Timeouts, retries and backoff](https://aws.amazon.com/builders-library/timeouts-retries-and-backoff-with-jitter/)
- [Resilience4j documentation](https://resilience4j.readme.io/docs)

## 9. Teach-back checklist

- [ ] Tôi phối hợp controls theo one deadline/capacity model.
- [ ] Tôi đo logical calls và attempts riêng.
- [ ] Tôi test overload, cancellation và recovery ramp.
- [ ] Evidence vẫn `NOT RUN`.

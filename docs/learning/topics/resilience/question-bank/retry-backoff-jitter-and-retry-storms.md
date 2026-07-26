# Resilience Interview Question Bank — Retry, Backoff, Jitter and Retry Storms

> Status: `DRAFT`<br>
> Domain owner: `Resilience`<br>
> Active slice: `NONE`; preview target: `RES-02`<br>
> Related roadmap: [Stage 2](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-2---spring-internals-http-api-và-transaction-semantics)<br>
> Related depth rubric: [Distributed systems](../../../knowledge-depth-rubric.md#312-distributed-systems-fundamentals--p0-target-d3)<br>
> Related theory: [Core](../theory/core/retry-backoff-jitter-and-retry-storms.md) · [Deep-dive](../theory/deep-dives/retry-amplification-idempotency-and-recovery-budget.md) — DRAFT, evidence NOT RUN<br>
> Updated: `2026-07-26`

Preview only; không active/implement `RES-02`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `RES-RETRY-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### RES-RETRY-001 — `FOUNDATION`
**Question:** Lỗi nào nên retry và lỗi nào không?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Transient vs permanent/business failure.<br>
**Answer outline:** Retry lỗi transient có xác suất hồi phục như timeout/connect reset/selected 5xx/serialization conflict; không retry validation, auth, deterministic bug. Quyết định theo operation/idempotency và server hints.<br>
**Required trade-offs:** Retry tăng availability nhưng tăng latency/load/duplicate risk.<br>
**Follow-up ladder:** 429/Retry-After? 404?<br>
**Red flags:** Retry mọi exception.<br>
**Evidence:** Theory [core](../theory/core/retry-backoff-jitter-and-retry-storms.md) + [deep-dive](../theory/deep-dives/retry-amplification-idempotency-and-recovery-budget.md); case `RES-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-RETRY-002 — `FOUNDATION`
**Question:** Exponential backoff và jitter giải quyết vấn đề gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Load spacing và client synchronization.<br>
**Answer outline:** Backoff tăng khoảng chờ để dependency hồi phục; jitter randomize để client không retry đồng thời. Cần cap, max attempts/elapsed time và deadline.<br>
**Required trade-offs:** Backoff dài giảm load nhưng kéo tail/user wait.<br>
**Follow-up ladder:** Full/equal/decorrelated jitter?<br>
**Red flags:** Fixed delay cho fleet lớn là đủ.<br>
**Evidence:** Theory [core](../theory/core/retry-backoff-jitter-and-retry-storms.md) + [deep-dive](../theory/deep-dives/retry-amplification-idempotency-and-recovery-budget.md); experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-RETRY-003 — `FOUNDATION`
**Question:** Vì sao retry yêu cầu operation idempotent hoặc deduplicated?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Ambiguous success và duplicate side effect.<br>
**Answer outline:** Caller có thể không nhận response dù mutation đã commit; retry tạo duplicate nếu không có key/business invariant. Read thường an toàn hơn write nhưng vẫn xét cost/side effect.<br>
**Required trade-offs:** Dedup state thêm storage/TTL nhưng biến ambiguous retry thành recoverable.<br>
**Follow-up ladder:** Message redelivery? Payment?<br>
**Red flags:** POST không bao giờ retry được.<br>
**Evidence:** Theory [core](../theory/core/retry-backoff-jitter-and-retry-storms.md) + [deep-dive](../theory/deep-dives/retry-amplification-idempotency-and-recovery-budget.md); case `RES-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-RETRY-004 — `FOUNDATION`
**Question:** Max attempts, max elapsed time và retry budget khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Local bound vs fleet-wide amplification.<br>
**Answer outline:** Attempts giới hạn count; elapsed time tôn trọng deadline; retry budget giới hạn tỷ lệ/volume retry toàn hệ thống để không khuếch đại overload. Nên kết hợp cả ba và metrics theo cause.<br>
**Required trade-offs:** Budget thấp bỏ lỡ transient recovery; cao gây storm.<br>
**Follow-up ladder:** Token bucket? Per-tenant?<br>
**Red flags:** Ba lần retry luôn an toàn.<br>
**Evidence:** Theory [core](../theory/core/retry-backoff-jitter-and-retry-storms.md) + [deep-dive](../theory/deep-dives/retry-amplification-idempotency-and-recovery-budget.md); experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-RETRY-005 — `SENIOR`
**Question:** Retry ở gateway, service và client chồng nhau gây amplification bao nhiêu?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Multiplicative attempts và ownership.<br>
**Answer outline:** Ba layer mỗi layer tối đa 3 attempts có thể tạo tới 27 downstream attempts cho một request. Chọn một layer sở hữu retry hoặc share budget/deadline, expose attempt telemetry.<br>
**Required trade-offs:** Retry gần dependency hiểu lỗi; retry ngoài có end-to-end context.<br>
**Follow-up ladder:** Hedging? Service mesh retry?<br>
**Red flags:** Mỗi layer retry độc lập để “an toàn hơn”.<br>
**Evidence:** Theory [core](../theory/core/retry-backoff-jitter-and-retry-storms.md) + [deep-dive](../theory/deep-dives/retry-amplification-idempotency-and-recovery-budget.md); experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-RETRY-006 — `SENIOR`
**Question:** Retry transaction sau deadlock/serialization failure đúng cách thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Retry whole unit và side effects.<br>
**Answer outline:** Rollback rồi retry toàn transaction từ boundary bên ngoài với fresh state, bounded backoff+jitter; operation/side effects phải idempotent và không gọi remote trong attempt. Metrics conflict rate.<br>
**Required trade-offs:** Retry cải thiện success nhưng contention cao có thể livelock.<br>
**Follow-up ladder:** Proxy order với `@Transactional`?<br>
**Red flags:** Retry statement cuối trong transaction rollback-only.<br>
**Evidence:** Theory [core](../theory/core/retry-backoff-jitter-and-retry-storms.md) + [deep-dive](../theory/deep-dives/retry-amplification-idempotency-and-recovery-budget.md); reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-RETRY-007 — `SENIOR`
**Question:** Thiết kế retry metrics/logging nào để phát hiện storm mà không tăng cardinality?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `OBSERVABILITY`<br>
**Interviewer evaluates:** Attempts, outcome, reason và correlation.<br>
**Answer outline:** Counter attempts/exhausted/success-after-retry theo bounded dependency/operation/error class; histogram delay/elapsed; log sampled với trace ID và attempt, không tag request ID/token. Alert amplification ratio.<br>
**Required trade-offs:** Chi tiết tốt cho RCA nhưng label không bounded phá telemetry backend.<br>
**Follow-up ladder:** Retry success có luôn tốt?<br>
**Red flags:** Chỉ log exception cuối.<br>
**Evidence:** Theory [core](../theory/core/retry-backoff-jitter-and-retry-storms.md) + [deep-dive](../theory/deep-dives/retry-amplification-idempotency-and-recovery-budget.md); telemetry `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-RETRY-008 — `SENIOR`
**Question:** Test retry policy với clock/randomness và fault injection thế nào cho deterministic?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `EXPERIMENT_DESIGN`<br>
**Interviewer evaluates:** Injectable clock/sleeper/random và assertions.<br>
**Answer outline:** Tách policy khỏi I/O, inject fake clock/sleeper/random; assert classification, count, cap/deadline, jitter bounds và no-retry cases; integration fault inject dependency rồi đo amplification.<br>
**Required trade-offs:** Fake time nhanh nhưng cần ít test thật để kiểm wiring.<br>
**Follow-up ladder:** Property-based test?<br>
**Red flags:** Unit test thật sự sleep nhiều giây.<br>
**Evidence:** Theory [core](../theory/core/retry-backoff-jitter-and-retry-storms.md) + [deep-dive](../theory/deep-dives/retry-amplification-idempotency-and-recovery-budget.md); Test plan `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-RETRY-009 — `ARCHITECT`
**Question:** Xây retry policy platform-wide mà vẫn cho service ownership thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Defaults, exception taxonomy, budget và governance.<br>
**Answer outline:** Cung cấp safe defaults/max cap/telemetry, dependency-specific override có review; propagate deadlines/budget; service owner phân loại idempotency và failure; chaos/load gate trước rollout.<br>
**Required trade-offs:** Central standard ngăn footgun nhưng policy cứng không hiểu domain.<br>
**Follow-up ladder:** Kill switch? Config rollout?<br>
**Red flags:** Một annotation retry giống nhau cho mọi call.<br>
**Evidence:** Theory [core](../theory/core/retry-backoff-jitter-and-retry-storms.md) + [deep-dive](../theory/deep-dives/retry-amplification-idempotency-and-recovery-budget.md); Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RES-RETRY-010 — `EXPERT`
**Question:** Dependency đang hồi phục nhưng retry traffic làm nó sập lại; điều tra và thoát vòng lặp thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Positive feedback, backlog và recovery control.<br>
**Answer outline:** Correlate original vs retry QPS, queues, saturation và breaker state; giảm/disable retry, shed load, drain gradual, cap concurrency và warm cache có kiểm soát; sau incident thêm global budget/jitter/recovery ramp.<br>
**Required trade-offs:** Drop requests ngắn hạn để khôi phục sustainable throughput.<br>
**Follow-up ladder:** Retry-after dynamic? Brownout?<br>
**Red flags:** Scale clients để retry nhanh hơn.<br>
**Evidence:** Theory [core](../theory/core/retry-backoff-jitter-and-retry-storms.md) + [deep-dive](../theory/deep-dives/retry-amplification-idempotency-and-recovery-budget.md); Incident drill `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `RES-02` active, tạo deterministic retry experiment và evidence thật; không đổi/reuse stable IDs.

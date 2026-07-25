# Resilience Interview Question Bank — Retry, Backoff, Jitter and Retry Storms

> Status: `DRAFT`  
> Domain owner: `Resilience`  
> Active slice: `NONE`; preview target: `RES-02`  
> Related roadmap: [Stage 2](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-2---spring-internals-http-api-và-transaction-semantics)  
> Related depth rubric: [Distributed systems](../../../knowledge-depth-rubric.md#312-distributed-systems-fundamentals--p0-target-d3)  
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/resilience/theory/core/retry-backoff-jitter-and-retry-storms.md`  
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
**Question:** Lỗi nào nên retry và lỗi nào không?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Transient vs permanent/business failure.  
**Answer outline:** Retry lỗi transient có xác suất hồi phục như timeout/connect reset/selected 5xx/serialization conflict; không retry validation, auth, deterministic bug. Quyết định theo operation/idempotency và server hints.  
**Required trade-offs:** Retry tăng availability nhưng tăng latency/load/duplicate risk.  
**Follow-up ladder:** 429/Retry-After? 404?  
**Red flags:** Retry mọi exception.  
**Evidence:** Theory `NOT CREATED`; case `RES-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-RETRY-002 — `FOUNDATION`
**Question:** Exponential backoff và jitter giải quyết vấn đề gì?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Load spacing và client synchronization.  
**Answer outline:** Backoff tăng khoảng chờ để dependency hồi phục; jitter randomize để client không retry đồng thời. Cần cap, max attempts/elapsed time và deadline.  
**Required trade-offs:** Backoff dài giảm load nhưng kéo tail/user wait.  
**Follow-up ladder:** Full/equal/decorrelated jitter?  
**Red flags:** Fixed delay cho fleet lớn là đủ.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-RETRY-003 — `FOUNDATION`
**Question:** Vì sao retry yêu cầu operation idempotent hoặc deduplicated?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Ambiguous success và duplicate side effect.  
**Answer outline:** Caller có thể không nhận response dù mutation đã commit; retry tạo duplicate nếu không có key/business invariant. Read thường an toàn hơn write nhưng vẫn xét cost/side effect.  
**Required trade-offs:** Dedup state thêm storage/TTL nhưng biến ambiguous retry thành recoverable.  
**Follow-up ladder:** Message redelivery? Payment?  
**Red flags:** POST không bao giờ retry được.  
**Evidence:** Theory `NOT CREATED`; case `RES-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-RETRY-004 — `FOUNDATION`
**Question:** Max attempts, max elapsed time và retry budget khác nhau thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Local bound vs fleet-wide amplification.  
**Answer outline:** Attempts giới hạn count; elapsed time tôn trọng deadline; retry budget giới hạn tỷ lệ/volume retry toàn hệ thống để không khuếch đại overload. Nên kết hợp cả ba và metrics theo cause.  
**Required trade-offs:** Budget thấp bỏ lỡ transient recovery; cao gây storm.  
**Follow-up ladder:** Token bucket? Per-tenant?  
**Red flags:** Ba lần retry luôn an toàn.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-RETRY-005 — `SENIOR`
**Question:** Retry ở gateway, service và client chồng nhau gây amplification bao nhiêu?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Multiplicative attempts và ownership.  
**Answer outline:** Ba layer mỗi layer tối đa 3 attempts có thể tạo tới 27 downstream attempts cho một request. Chọn một layer sở hữu retry hoặc share budget/deadline, expose attempt telemetry.  
**Required trade-offs:** Retry gần dependency hiểu lỗi; retry ngoài có end-to-end context.  
**Follow-up ladder:** Hedging? Service mesh retry?  
**Red flags:** Mỗi layer retry độc lập để “an toàn hơn”.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-RETRY-006 — `SENIOR`
**Question:** Retry transaction sau deadlock/serialization failure đúng cách thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Retry whole unit và side effects.  
**Answer outline:** Rollback rồi retry toàn transaction từ boundary bên ngoài với fresh state, bounded backoff+jitter; operation/side effects phải idempotent và không gọi remote trong attempt. Metrics conflict rate.  
**Required trade-offs:** Retry cải thiện success nhưng contention cao có thể livelock.  
**Follow-up ladder:** Proxy order với `@Transactional`?  
**Red flags:** Retry statement cuối trong transaction rollback-only.  
**Evidence:** Theory `NOT CREATED`; reproducer `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-RETRY-007 — `SENIOR`
**Question:** Thiết kế retry metrics/logging nào để phát hiện storm mà không tăng cardinality?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `OBSERVABILITY`  
**Interviewer evaluates:** Attempts, outcome, reason và correlation.  
**Answer outline:** Counter attempts/exhausted/success-after-retry theo bounded dependency/operation/error class; histogram delay/elapsed; log sampled với trace ID và attempt, không tag request ID/token. Alert amplification ratio.  
**Required trade-offs:** Chi tiết tốt cho RCA nhưng label không bounded phá telemetry backend.  
**Follow-up ladder:** Retry success có luôn tốt?  
**Red flags:** Chỉ log exception cuối.  
**Evidence:** Theory `NOT CREATED`; telemetry `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-RETRY-008 — `SENIOR`
**Question:** Test retry policy với clock/randomness và fault injection thế nào cho deterministic?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `EXPERIMENT_DESIGN`  
**Interviewer evaluates:** Injectable clock/sleeper/random và assertions.  
**Answer outline:** Tách policy khỏi I/O, inject fake clock/sleeper/random; assert classification, count, cap/deadline, jitter bounds và no-retry cases; integration fault inject dependency rồi đo amplification.  
**Required trade-offs:** Fake time nhanh nhưng cần ít test thật để kiểm wiring.  
**Follow-up ladder:** Property-based test?  
**Red flags:** Unit test thật sự sleep nhiều giây.  
**Evidence:** Test plan `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-RETRY-009 — `ARCHITECT`
**Question:** Xây retry policy platform-wide mà vẫn cho service ownership thế nào?  
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`  
**Interviewer evaluates:** Defaults, exception taxonomy, budget và governance.  
**Answer outline:** Cung cấp safe defaults/max cap/telemetry, dependency-specific override có review; propagate deadlines/budget; service owner phân loại idempotency và failure; chaos/load gate trước rollout.  
**Required trade-offs:** Central standard ngăn footgun nhưng policy cứng không hiểu domain.  
**Follow-up ladder:** Kill switch? Config rollout?  
**Red flags:** Một annotation retry giống nhau cho mọi call.  
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RES-RETRY-010 — `EXPERT`
**Question:** Dependency đang hồi phục nhưng retry traffic làm nó sập lại; điều tra và thoát vòng lặp thế nào?  
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`  
**Interviewer evaluates:** Positive feedback, backlog và recovery control.  
**Answer outline:** Correlate original vs retry QPS, queues, saturation và breaker state; giảm/disable retry, shed load, drain gradual, cap concurrency và warm cache có kiểm soát; sau incident thêm global budget/jitter/recovery ramp.  
**Required trade-offs:** Drop requests ngắn hạn để khôi phục sustainable throughput.  
**Follow-up ladder:** Retry-after dynamic? Brownout?  
**Red flags:** Scale clients để retry nhanh hơn.  
**Evidence:** Incident drill `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `RES-02` active, tạo deterministic retry experiment và evidence thật; không đổi/reuse stable IDs.

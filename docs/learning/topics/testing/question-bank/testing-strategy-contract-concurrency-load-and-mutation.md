# Testing Interview Question Bank — Strategy, Contract, Concurrency, Load and Mutation

> Status: `DRAFT`<br>
> Domain owner: `Testing/Quality`<br>
> Active slice: `NONE`; preview target: `TEST-02`<br>
> Related roadmap: [Stage 8](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-8---observability-testing-runtime-và-delivery-engineering)<br>
> Related depth rubric: [Testing](../../../knowledge-depth-rubric.md#310-testing-và-quality-strategy--p0-target-d3)<br>
> Related theory: [Core](../theory/core/testing-strategy-contract-concurrency-load-and-mutation.md) · [Deep-dive](../theory/deep-dives/deterministic-concurrency-contract-mutation-and-load-evidence.md)<br>
> Updated: `2026-07-26`

Preview only; không active/implement `TEST-02`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

Lượt đầu học `TEST-STRAT-001..006`; câu hỏi tiếp theo cho senior là `007..008`; phần nâng cao là `009..010`.

## Questions

### TEST-STRAT-001 — `FOUNDATION`
**Question:** Unit, slice, integration, contract và end-to-end test khác nhau ở boundary nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** System under test và confidence/cost.<br>
**Answer outline:** Unit cô lập logic; slice tải framework subset; integration nối real components; contract khóa producer-consumer; E2E đi toàn path. Chọn theo risk, không theo pyramid count máy móc.<br>
**Required trade-offs:** Boundary rộng tăng confidence nhưng chậm/flaky/debug khó.<br>
**Follow-up ladder:** Repository test thuộc loại nào?<br>
**Red flags:** Test có Spring context đều là integration hoàn chỉnh.<br>
**Evidence:** Theory [core](../theory/core/testing-strategy-contract-concurrency-load-and-mutation.md) + [deep-dive](../theory/deep-dives/deterministic-concurrency-contract-mutation-and-load-evidence.md); case `TEST-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TEST-STRAT-002 — `FOUNDATION`
**Question:** Mock, stub, fake và spy khác nhau; khi nào mock tạo false confidence?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Test double semantics và interaction coupling.<br>
**Answer outline:** Stub trả dữ liệu, fake có implementation nhẹ, mock verify interaction, spy bọc object; mock DB/broker/transaction không chứng minh semantics thật.<br>
**Required trade-offs:** Mock nhanh nhưng dễ khóa implementation và bỏ lỡ serialization/SQL/wiring.<br>
**Follow-up ladder:** Mock clock có nên?<br>
**Red flags:** Verify `save()` chứng minh dữ liệu commit.<br>
**Evidence:** Theory [core](../theory/core/testing-strategy-contract-concurrency-load-and-mutation.md) + [deep-dive](../theory/deep-dives/deterministic-concurrency-contract-mutation-and-load-evidence.md); case `TEST-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TEST-STRAT-003 — `FOUNDATION`
**Question:** Một test tốt cần deterministic time, ID và data như thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Control nondeterminism và isolation.<br>
**Answer outline:** Inject Clock/ID generator, fixture tối thiểu explicit, unique namespace/cleanup, không phụ thuộc order/network/shared DB; seed chỉ khi random failure reproducible.<br>
**Required trade-offs:** Randomized/property tests tìm edge nhưng phải lưu seed.<br>
**Follow-up ladder:** Awaitility vs sleep? Locale/timezone?<br>
**Red flags:** Retry test flaky là fix.<br>
**Evidence:** Theory [core](../theory/core/testing-strategy-contract-concurrency-load-and-mutation.md) + [deep-dive](../theory/deep-dives/deterministic-concurrency-contract-mutation-and-load-evidence.md); case `TEST-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TEST-STRAT-004 — `FOUNDATION`
**Question:** Test pyramid hay test portfolio nên được dùng như heuristic ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Risk-based allocation thay vì quota.<br>
**Answer outline:** Nhiều fast tests cho logic, đủ integration/contract cho risky boundaries, ít E2E critical journeys; điều chỉnh theo regression history/blast radius.<br>
**Required trade-offs:** Thêm test tăng maintenance; chọn test bắt loại failure có giá trị.<br>
**Follow-up ladder:** Testing trophy? Coverage target?<br>
**Red flags:** 80% coverage đồng nghĩa chất lượng.<br>
**Evidence:** Theory [core](../theory/core/testing-strategy-contract-concurrency-load-and-mutation.md) + [deep-dive](../theory/deep-dives/deterministic-concurrency-contract-mutation-and-load-evidence.md); case `TEST-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TEST-STRAT-005 — `SENIOR`
**Question:** Viết concurrency test để tái hiện lost update/race đáng tin cậy thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Barrier, repeated execution và invariant assertion.<br>
**Answer outline:** Đồng bộ start bằng barrier/latch, đủ workers/repeats, DB thật nếu race DB; assert invariant/final state và capture seed/timeline, không chỉ mong race xuất hiện.<br>
**Required trade-offs:** Stress tăng xác suất nhưng không chứng minh absence; deterministic scheduler khó hơn.<br>
**Follow-up ladder:** JCStress? Timeout test?<br>
**Red flags:** Dùng `Thread.sleep` 100ms là deterministic.<br>
**Evidence:** Theory [core](../theory/core/testing-strategy-contract-concurrency-load-and-mutation.md) + [deep-dive](../theory/deep-dives/deterministic-concurrency-contract-mutation-and-load-evidence.md); case `TEST-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TEST-STRAT-006 — `SENIOR`
**Question:** Consumer-driven contract test bảo vệ điều gì và không bảo vệ điều gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Wire/semantic expectations giữa independently deployed parties.<br>
**Answer outline:** Consumer examples/schema verify provider compatibility; không chứng minh performance, production routing, auth policy đầy đủ hay business correctness ngoài examples.<br>
**Required trade-offs:** Nhiều consumers tăng governance/version burden.<br>
**Follow-up ladder:** Provider state? Event contract?<br>
**Red flags:** Contract pass nghĩa E2E chắc chắn pass.<br>
**Evidence:** Theory [core](../theory/core/testing-strategy-contract-concurrency-load-and-mutation.md) + [deep-dive](../theory/deep-dives/deterministic-concurrency-contract-mutation-and-load-evidence.md); case `TEST-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TEST-STRAT-007 — `SENIOR`
**Question:** Thiết kế load/fault test có hypothesis và tránh coordinated omission thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Workload model, percentiles và failure injection.<br>
**Answer outline:** Chọn open/closed model đúng traffic, warmup/data đại diện, đo p50/p95/p99/throughput/saturation/errors; inject one fault và lưu raw config/output; generator không được che wait time.<br>
**Required trade-offs:** Realism vs repeatability/cost.<br>
**Follow-up ladder:** Soak/spike/breakpoint?<br>
**Red flags:** Chỉ báo average RPS cuối bài.<br>
**Evidence:** Theory [core](../theory/core/testing-strategy-contract-concurrency-load-and-mutation.md) + [deep-dive](../theory/deep-dives/deterministic-concurrency-contract-mutation-and-load-evidence.md); case `TEST-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TEST-STRAT-008 — `SENIOR`
**Question:** Mutation testing cho biết gì mà line coverage không cho biết?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Assertion strength và surviving mutants.<br>
**Answer outline:** Tool thay operators/conditions; mutant sống chỉ test không quan sát behavior hoặc equivalent; dùng trên domain-critical code, không chạy mọi module vô thức.<br>
**Required trade-offs:** Tăng confidence nhưng CPU/triage cost cao.<br>
**Follow-up ladder:** Mutation score target? Generated code?<br>
**Red flags:** 100% mutation score là mục tiêu tuyệt đối.<br>
**Evidence:** Theory [core](../theory/core/testing-strategy-contract-concurrency-load-and-mutation.md) + [deep-dive](../theory/deep-dives/deterministic-concurrency-contract-mutation-and-load-evidence.md); case `TEST-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TEST-STRAT-009 — `ARCHITECT`
**Question:** Xây quality gate theo blast radius và deployment risk thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Risk tiers, evidence và escape rate.<br>
**Answer outline:** Map change type sang required unit/integration/security/contract/load/migration checks; progressive rollout, rollback signal và flaky-test ownership; theo dõi escaped defects/time-to-detect.<br>
**Required trade-offs:** Gate chặt giảm incident nhưng kéo lead time; parallelize/tier thay vì bỏ.<br>
**Follow-up ladder:** Hotfix policy? Test quarantine?<br>
**Red flags:** Một pipeline giống nhau cho mọi change.<br>
**Evidence:** Theory [core](../theory/core/testing-strategy-contract-concurrency-load-and-mutation.md) + [deep-dive](../theory/deep-dives/deterministic-concurrency-contract-mutation-and-load-evidence.md); case `TEST-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### TEST-STRAT-010 — `EXPERT`
**Question:** Một suite xanh nhưng production sai: dẫn systematic test-gap analysis thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Missing oracle/boundary/data/failure và process learning.<br>
**Answer outline:** Reproduce incident, map path và assumptions, xác định test double/fixture/oracle bỏ sót, thêm smallest regression ở đúng layer rồi điều chỉnh strategy/telemetry; không chỉ thêm E2E khổng lồ.<br>
**Required trade-offs:** Regression cụ thể vs overfitting incident.<br>
**Follow-up ladder:** Fault seeding? Blameless review?<br>
**Red flags:** Đổ lỗi người viết test mà không sửa system.<br>
**Evidence:** Theory [core](../theory/core/testing-strategy-contract-concurrency-load-and-mutation.md) + [deep-dive](../theory/deep-dives/deterministic-concurrency-contract-mutation-and-load-evidence.md); case `TEST-02 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `TEST-02` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.

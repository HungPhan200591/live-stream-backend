# Architecture Interview Question Bank — DDD Aggregates and Modular Boundaries

> Status: `DRAFT`<br>
> Domain owner: `Domain/Modular Architecture`<br>
> Active slice: `NONE`; preview target: `DDD-01`<br>
> Related roadmap: [Stage 10](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-10---modular-monolith-to-microservices)<br>
> Related depth rubric: [Domain/modular architecture](../../../knowledge-depth-rubric.md#317-domain-modeling-và-modular-architecture--p1-target-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/architecture/theory/core/ddd-aggregates-and-modular-boundaries.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `DDD-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `DDD-MOD-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### DDD-MOD-001 — `FOUNDATION`
**Question:** Domain, subdomain và bounded context khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Problem space vs model/language boundary.<br>
**Answer outline:** Domain là business problem; subdomain chia capability; bounded context là boundary nơi model/terms/rules nhất quán và có contract với context khác.<br>
**Required trade-offs:** Boundary rõ giảm ambiguity nhưng tăng mapping/coordination.<br>
**Follow-up ladder:** Core/supporting/generic?<br>
**Red flags:** Mỗi entity/table là một bounded context.<br>
**Evidence:** Theory `NOT CREATED`; case `DDD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DDD-MOD-002 — `FOUNDATION`
**Question:** Entity, value object và aggregate khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Identity, equality/immutability và consistency boundary.<br>
**Answer outline:** Entity có continuity identity; value object định nghĩa bởi attributes thường immutable; aggregate là cluster có root bảo vệ invariant trong transaction.<br>
**Required trade-offs:** Aggregate chặt bảo vệ invariant nhưng quá lớn gây contention/load.<br>
**Follow-up ladder:** Domain service?<br>
**Red flags:** Aggregate là mọi entity có foreign key.<br>
**Evidence:** Theory `NOT CREATED`; case `DDD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DDD-MOD-003 — `FOUNDATION`
**Question:** Ubiquitous language có giá trị gì ngoài naming?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Shared mental model between code/business.<br>
**Answer outline:** Terms and state transitions agreed in conversations, tests, API and code; ambiguity surfaces model boundaries. Language evolves with decisions, not glossary-only.<br>
**Required trade-offs:** Precision may require translation across contexts.<br>
**Follow-up ladder:** Context map?<br>
**Red flags:** Dùng tên tiếng Anh thống nhất là ubiquitous language.<br>
**Evidence:** Theory `NOT CREATED`; case `DDD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DDD-MOD-004 — `FOUNDATION`
**Question:** Module API và data owner trong modular monolith nghĩa là gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Explicit allowed dependency and write ownership.<br>
**Answer outline:** Module exposes application/domain contract; internal packages/data not accessed directly; one owner writes invariant, other modules call API/events/projections. Same process/database doesn't remove boundary.<br>
**Required trade-offs:** Encapsulation adds mapping/query duplication.<br>
**Follow-up ladder:** Shared read model?<br>
**Red flags:** Package khác nhau đủ tạo module boundary.<br>
**Evidence:** Theory `NOT CREATED`; case `DDD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DDD-MOD-005 — `SENIOR`
**Question:** Chọn aggregate boundary từ invariant và transaction ra sao?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** What must change atomically under concurrency.<br>
**Answer outline:** List invariants/commands/conflicts; include only state requiring immediate consistency, reference other aggregates by ID and coordinate eventually. Test concurrent commands.<br>
**Required trade-offs:** Small aggregates scale but require saga/eventual workflow.<br>
**Follow-up ladder:** Order/order lines?<br>
**Red flags:** Object graph tiện truy cập nên làm một aggregate.<br>
**Evidence:** Theory `NOT CREATED`; case `DDD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DDD-MOD-006 — `SENIOR`
**Question:** Domain event khác integration event và application command thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Past fact within model, external contract and intent.<br>
**Answer outline:** Command requests action and may reject; domain event records internal fact; integration event is stable external representation published after commit, often mapped/versioned.<br>
**Required trade-offs:** Mapping prevents leakage but adds latency/code.<br>
**Follow-up ladder:** Notification event? Event sourcing?<br>
**Red flags:** Dùng JPA entity làm event payload.<br>
**Evidence:** Theory `NOT CREATED`; case `DDD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DDD-MOD-007 — `SENIOR`
**Question:** Architecture test/Spring Modulith bảo vệ boundary nào và không bảo vệ gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Static dependency/event/module rules vs semantic ownership.<br>
**Answer outline:** Tests can forbid package cycles/internal access and verify module events; cannot prove business invariant, runtime data coupling or good context design. Combine code review/contracts.<br>
**Required trade-offs:** Automated guard reduces drift but rigid rules need exceptions.<br>
**Follow-up ladder:** Public package? Module integration test?<br>
**Red flags:** Architecture test pass nghĩa DDD đúng.<br>
**Evidence:** Theory `NOT CREATED`; case `DDD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DDD-MOD-008 — `SENIOR`
**Question:** Refactor package-by-layer sang package-by-feature an toàn thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Incremental seams and behavior preservation.<br>
**Answer outline:** Map use cases/data owners, move one vertical slice with facade, characterization tests and dependency rules; remove cross-feature repository access gradually, document temporary adapters.<br>
**Required trade-offs:** Incremental transition has duplicate abstractions; big bang risks regression.<br>
**Follow-up ladder:** Shared/common package?<br>
**Red flags:** Chuyển file/folder là đủ đổi ownership.<br>
**Evidence:** Theory `NOT CREATED`; case `DDD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DDD-MOD-009 — `ARCHITECT`
**Question:** Context mapping và team ownership cho identity/stream/wallet/gift/chat ra sao?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Relationships, contracts and organizational alignment.<br>
**Answer outline:** Define upstream/downstream, conformist/ACL/shared-kernel only intentionally; wallet owns money invariant, identity auth facts, etc.; assign teams/SLO/contracts and avoid shared writes.<br>
**Required trade-offs:** Autonomy vs duplicated data/eventual consistency.<br>
**Follow-up ladder:** Partnership vs customer-supplier?<br>
**Red flags:** Một canonical domain model dùng mọi context.<br>
**Evidence:** Theory `NOT CREATED`; case `DDD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DDD-MOD-010 — `EXPERT`
**Question:** Một aggregate hot gây contention nhưng invariant phải giữ: evolve model thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Invariant decomposition, reservation/ledger and evidence.<br>
**Answer outline:** Identify truly global invariant/linearization point; use conditional updates, reservation/quota buckets or append ledger with reconciliation, partition only independent dimensions; test conflict and failure windows.<br>
**Required trade-offs:** Throughput vs immediate global consistency/complex recovery.<br>
**Follow-up ladder:** Escrow technique? CRDT limit?<br>
**Red flags:** Tách aggregate ngẫu nhiên và chấp nhận oversell không policy.<br>
**Evidence:** Theory `NOT CREATED`; case `DDD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `DDD-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.


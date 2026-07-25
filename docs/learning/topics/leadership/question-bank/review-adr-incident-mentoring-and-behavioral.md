# Leadership Interview Question Bank — Review, ADR, Incident, Mentoring and Behavioral Evidence

> Status: `DRAFT`<br>
> Domain owner: `Technical Leadership`<br>
> Active slice: `NONE`; preview target: `LEAD-01`<br>
> Related roadmap: [Stage 12](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-12---technical-leadership-và-delivery)<br>
> Related depth rubric: [Technical leadership](../../../knowledge-depth-rubric.md#314-technical-leadership-và-delivery--p0-target-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/leadership/theory/core/review-adr-incident-mentoring-and-behavioral.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `LEAD-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `LEAD-ENG-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### LEAD-ENG-001 — `FOUNDATION`
**Question:** Blocker, suggestion và question trong code review khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Severity, actionability and decision ownership.<br>
**Answer outline:** Blocker ties concrete correctness/security/compatibility risk to required change; suggestion is optional improvement; question seeks context. State evidence/location/impact and avoid taste masquerading as blocker.<br>
**Required trade-offs:** Clear severity speeds merge but misclassification erodes trust.<br>
**Follow-up ladder:** Nit? Approval with follow-up?<br>
**Red flags:** Mọi style preference là blocker.<br>
**Evidence:** Theory `NOT CREATED`; case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-002 — `FOUNDATION`
**Question:** ADR tối thiểu cần context, options, decision và consequence gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Why decision was made under constraints.<br>
**Answer outline:** Record problem/constraints, viable alternatives, decision/date/owner, positive/negative consequences and revisit trigger; link evidence, not meeting transcript.<br>
**Required trade-offs:** Documentation cost vs future decision recovery.<br>
**Follow-up ladder:** Superseded ADR?<br>
**Red flags:** ADR chỉ ghi phương án được chọn.<br>
**Evidence:** Theory `NOT CREATED`; case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-003 — `FOUNDATION`
**Question:** Blameless postmortem nghĩa là gì và không nghĩa là gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** System learning with accountability.<br>
**Answer outline:** Avoid personal blame; analyze conditions, controls and decisions, preserve timeline/impact/root-contributing factors; actions have owner/deadline/verification. Accountability remains.<br>
**Required trade-offs:** Psychological safety vs vague actions; precision without punishment.<br>
**Follow-up ladder:** Five whys limit?<br>
**Red flags:** Blameless nghĩa không ai chịu trách nhiệm đóng action.<br>
**Evidence:** Theory `NOT CREATED`; case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-004 — `FOUNDATION`
**Question:** STAR/CAR story cho interview cần evidence nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Situation/task/action/result and personal contribution.<br>
**Answer outline:** Specific constraint/stakes, role and decisions, alternatives/conflict, measurable or verifiable result, failure/learning and what changed; distinguish team outcome from own contribution.<br>
**Required trade-offs:** Conciseness vs enough technical depth.<br>
**Follow-up ladder:** No numeric result?<br>
**Red flags:** Kể hypothetical best practice như trải nghiệm thật.<br>
**Evidence:** Theory `NOT CREATED`; case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-005 — `SENIOR`
**Question:** Review một change cross-layer theo risk order nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Correctness/invariant, security, transaction, compatibility, operability and tests.<br>
**Answer outline:** Understand intent/diff, identify blast radius and data/auth boundaries, inspect failure/recovery and evidence, prioritize critical findings with reproducible scenario; separate scope follow-ups.<br>
**Required trade-offs:** Deep review slows delivery; risk-tier and time-box.<br>
**Follow-up ladder:** Docs drift? Performance claim?<br>
**Red flags:** Bắt naming/style trước rồi mới xem invariant.<br>
**Evidence:** Theory `NOT CREATED`; case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-006 — `SENIOR`
**Question:** Dẫn incident với stakeholder communication thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Command roles, cadence, uncertainty and decisions.<br>
**Answer outline:** Declare commander/tech/comms, share known impact/unknowns/next update, stabilize and log decisions, avoid speculative cause, confirm recovery and follow-up. Tailor detail to users/product/engineers.<br>
**Required trade-offs:** Frequent updates consume focus but reduce uncertainty.<br>
**Follow-up ladder:** When escalate?<br>
**Red flags:** Chờ biết root cause mới thông báo.<br>
**Evidence:** Theory `NOT CREATED`; case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-007 — `SENIOR`
**Question:** Mentor người có mental model sai mà không viết lời giải thay họ thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Questioning, small reproducer and teach-back.<br>
**Answer outline:** Ask prediction/why, create minimal counterexample, let learner run/interpret, request teach-back and spaced follow-up; adapt challenge and record gap/evidence.<br>
**Required trade-offs:** Direct answer nhanh hơn nhưng ownership/retention thấp.<br>
**Follow-up ladder:** Pair programming? Feedback model?<br>
**Red flags:** Sửa code hộ rồi bảo đọc lại.<br>
**Evidence:** Theory `NOT CREATED`; case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-008 — `SENIOR`
**Question:** Ưu tiên technical debt theo risk/value/dependency thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Not all debt equal; decision and revisit.<br>
**Answer outline:** Describe symptom/interest/blast radius, business/reliability impact, blocking dependencies and remediation options; size incremental payoff, owner/revisit trigger and track actual incidents/lead time.<br>
**Required trade-offs:** Paying debt competes features; ignoring compounds risk.<br>
**Follow-up ladder:** Debt budget? Rewrite proposal?<br>
**Red flags:** % sprint cố định giải quyết mọi context.<br>
**Evidence:** Theory `NOT CREATED`; case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-009 — `ARCHITECT`
**Question:** Dẫn architecture decision đa stakeholder khi goals xung đột thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Constraints, reversible choices and transparent tradeoff.<br>
**Answer outline:** Elicit success metrics/non-negotiables, separate reversible experiments from irreversible commitments, quantify options/risks/cost, decide owner and record dissent/revisit; communicate at each altitude.<br>
**Required trade-offs:** Consensus builds buy-in but can delay; decision rights clear.<br>
**Follow-up ladder:** RFC process? Security veto?<br>
**Red flags:** Senior nhất quyết định và thuyết phục sau.<br>
**Evidence:** Theory `NOT CREATED`; case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-010 — `EXPERT`
**Question:** Một quyết định của bạn gây incident: trả lời và thay đổi hệ thống thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Ownership without self-blame theater and closed-loop learning.<br>
**Answer outline:** State original evidence/constraints, missed assumption and impact, immediate ownership/communication/mitigation, specific guardrail/test/process change with owner/result; share lesson and revisit similar decisions.<br>
**Required trade-offs:** Transparency may feel risky but builds trust; avoid overcorrecting.<br>
**Follow-up ladder:** What if team disagreed?<br>
**Red flags:** Đổ cho thiếu requirement hoặc nhận lỗi chung chung.<br>
**Evidence:** Theory `NOT CREATED`; case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `LEAD-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.


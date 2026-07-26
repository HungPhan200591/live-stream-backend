# Leadership Interview Question Bank — Review, ADR, Incident, Mentoring and Behavioral Evidence

> Status: `DRAFT`<br>
> Domain owner: `Technical Leadership`<br>
> Active slice: `NONE`; preview target: `LEAD-01`<br>
> Related roadmap: [Stage 12](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-12---technical-leadership-và-delivery)<br>
> Related depth rubric: [Technical leadership](../../../knowledge-depth-rubric.md#314-technical-leadership-và-delivery--p0-target-d3)<br>
> Related theory: [Core](../theory/core/review-adr-incident-mentoring-and-behavioral.md) · [Deep-dive](../theory/deep-dives/decision-facilitation-incident-command-and-systemic-learning.md)<br>
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
**Answer outline:** Blocker gắn rủi ro correctness/security/compatibility cụ thể với thay đổi bắt buộc; suggestion là cải tiến tùy chọn; question dùng để tìm thêm context. Nêu evidence, vị trí và impact; không biến sở thích cá nhân thành blocker.<br>
**Required trade-offs:** Clear severity speeds merge but misclassification erodes trust.<br>
**Follow-up ladder:** Nit? Approval with follow-up?<br>
**Red flags:** Mọi style preference là blocker.<br>
**Evidence:** Theory [core](../theory/core/review-adr-incident-mentoring-and-behavioral.md) + [deep-dive](../theory/deep-dives/decision-facilitation-incident-command-and-systemic-learning.md); case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-002 — `FOUNDATION`
**Question:** ADR tối thiểu cần context, options, decision và consequence gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Vì sao quyết định được đưa ra dưới các constraint lúc đó.<br>
**Answer outline:** Ghi problem/constraint, các phương án khả thi, quyết định/ngày/owner, hệ quả tích cực/tiêu cực và trigger xem lại; link tới evidence thay vì chép biên bản họp.<br>
**Required trade-offs:** Documentation cost vs future decision recovery.<br>
**Follow-up ladder:** Superseded ADR?<br>
**Red flags:** ADR chỉ ghi phương án được chọn.<br>
**Evidence:** Theory [core](../theory/core/review-adr-incident-mentoring-and-behavioral.md) + [deep-dive](../theory/deep-dives/decision-facilitation-incident-command-and-systemic-learning.md); case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-003 — `FOUNDATION`
**Question:** Blameless postmortem nghĩa là gì và không nghĩa là gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** System learning with accountability.<br>
**Answer outline:** Tránh đổ lỗi cá nhân; phân tích điều kiện, control và quyết định; giữ timeline, impact, root cause và contributing factor. Action phải có owner, deadline và cách verify; blameless không loại bỏ accountability.<br>
**Required trade-offs:** Psychological safety vs vague actions; precision without punishment.<br>
**Follow-up ladder:** Five whys limit?<br>
**Red flags:** Blameless nghĩa không ai chịu trách nhiệm đóng action.<br>
**Evidence:** Theory [core](../theory/core/review-adr-incident-mentoring-and-behavioral.md) + [deep-dive](../theory/deep-dives/decision-facilitation-incident-command-and-systemic-learning.md); case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-004 — `FOUNDATION`
**Question:** STAR/CAR story cho interview cần evidence nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Situation/task/action/result and personal contribution.<br>
**Answer outline:** Nêu constraint/stake cụ thể, vai trò và quyết định của mình, phương án khác hoặc conflict, kết quả đo/kiểm được, failure/bài học và thay đổi sau đó; phân biệt thành quả đội với đóng góp cá nhân.<br>
**Required trade-offs:** Conciseness vs enough technical depth.<br>
**Follow-up ladder:** No numeric result?<br>
**Red flags:** Kể hypothetical best practice như trải nghiệm thật.<br>
**Evidence:** Theory [core](../theory/core/review-adr-incident-mentoring-and-behavioral.md) + [deep-dive](../theory/deep-dives/decision-facilitation-incident-command-and-systemic-learning.md); case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-005 — `SENIOR`
**Question:** Review một change cross-layer theo risk order nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Correctness/invariant, security, transaction, compatibility, operability and tests.<br>
**Answer outline:** Hiểu intent và diff; xác định blast radius cùng ranh giới data/auth; kiểm failure/recovery và evidence; ưu tiên finding nghiêm trọng có scenario tái hiện; tách follow-up ngoài scope.<br>
**Required trade-offs:** Deep review slows delivery; risk-tier and time-box.<br>
**Follow-up ladder:** Docs drift? Performance claim?<br>
**Red flags:** Bắt naming/style trước rồi mới xem invariant.<br>
**Evidence:** Theory [core](../theory/core/review-adr-incident-mentoring-and-behavioral.md) + [deep-dive](../theory/deep-dives/decision-facilitation-incident-command-and-systemic-learning.md); case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-006 — `SENIOR`
**Question:** Dẫn incident với stakeholder communication thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Command roles, cadence, uncertainty and decisions.<br>
**Answer outline:** Chỉ định incident commander, tech lead và đầu mối communication; thông báo impact đã biết, điều chưa biết và thời điểm update tiếp; ổn định hệ thống, ghi quyết định, tránh đoán cause; xác nhận recovery và follow-up. Điều chỉnh mức chi tiết cho user, product và engineer.<br>
**Required trade-offs:** Frequent updates consume focus but reduce uncertainty.<br>
**Follow-up ladder:** When escalate?<br>
**Red flags:** Chờ biết root cause mới thông báo.<br>
**Evidence:** Theory [core](../theory/core/review-adr-incident-mentoring-and-behavioral.md) + [deep-dive](../theory/deep-dives/decision-facilitation-incident-command-and-systemic-learning.md); case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-007 — `SENIOR`
**Question:** Mentor người có mental model sai mà không viết lời giải thay họ thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Questioning, small reproducer and teach-back.<br>
**Answer outline:** Hỏi learner dự đoán và giải thích vì sao; tạo counterexample tối thiểu; để learner tự chạy/diễn giải; yêu cầu teach-back và ôn lại theo khoảng cách. Điều chỉnh độ khó và ghi gap/evidence.<br>
**Required trade-offs:** Direct answer nhanh hơn nhưng ownership/retention thấp.<br>
**Follow-up ladder:** Pair programming? Feedback model?<br>
**Red flags:** Sửa code hộ rồi bảo đọc lại.<br>
**Evidence:** Theory [core](../theory/core/review-adr-incident-mentoring-and-behavioral.md) + [deep-dive](../theory/deep-dives/decision-facilitation-incident-command-and-systemic-learning.md); case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-008 — `SENIOR`
**Question:** Ưu tiên technical debt theo risk/value/dependency thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Không phải technical debt nào cũng ngang nhau; cách quyết định và trigger xem lại.<br>
**Answer outline:** Mô tả symptom, lãi tích lũy và blast radius; impact tới business/reliability, dependency đang chặn và phương án khắc phục; ước lượng lợi ích theo bước, gán owner/trigger xem lại và theo dõi incident hoặc lead time thật.<br>
**Required trade-offs:** Paying debt competes features; ignoring compounds risk.<br>
**Follow-up ladder:** Debt budget? Rewrite proposal?<br>
**Red flags:** % sprint cố định giải quyết mọi context.<br>
**Evidence:** Theory [core](../theory/core/review-adr-incident-mentoring-and-behavioral.md) + [deep-dive](../theory/deep-dives/decision-facilitation-incident-command-and-systemic-learning.md); case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-009 — `ARCHITECT`
**Question:** Dẫn architecture decision đa stakeholder khi goals xung đột thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Constraints, reversible choices and transparent tradeoff.<br>
**Answer outline:** Làm rõ success metric và điều không thể thỏa hiệp; tách thí nghiệm reversible khỏi cam kết khó đảo ngược; định lượng option/risk/cost, chốt owner và ghi dissent/trigger xem lại; giao tiếp đúng mức với từng stakeholder.<br>
**Required trade-offs:** Consensus builds buy-in but can delay; decision rights clear.<br>
**Follow-up ladder:** RFC process? Security veto?<br>
**Red flags:** Senior nhất quyết định và thuyết phục sau.<br>
**Evidence:** Theory [core](../theory/core/review-adr-incident-mentoring-and-behavioral.md) + [deep-dive](../theory/deep-dives/decision-facilitation-incident-command-and-systemic-learning.md); case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### LEAD-ENG-010 — `EXPERT`
**Question:** Một quyết định của bạn gây incident: trả lời và thay đổi hệ thống thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Ownership thực chất, không trình diễn tự trách, và vòng học khép kín.<br>
**Answer outline:** Nêu evidence/constraint ban đầu, giả định bị bỏ sót và impact; nhận ownership, giao tiếp và mitigate ngay; thêm guardrail/test/process cụ thể có owner và kết quả; chia sẻ bài học và xem lại quyết định tương tự.<br>
**Required trade-offs:** Transparency may feel risky but builds trust; avoid overcorrecting.<br>
**Follow-up ladder:** What if team disagreed?<br>
**Red flags:** Đổ cho thiếu requirement hoặc nhận lỗi chung chung.<br>
**Evidence:** Theory [core](../theory/core/review-adr-incident-mentoring-and-behavioral.md) + [deep-dive](../theory/deep-dives/decision-facilitation-incident-command-and-systemic-learning.md); case `LEAD-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `LEAD-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.

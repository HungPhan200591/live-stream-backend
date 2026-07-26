# Java Interview Question Bank — Object-Oriented Design, SOLID and Patterns

> Status: `DRAFT`<br>
> Domain owner: `Object-Oriented Design`<br>
> Active slice: `NONE`; preview target: `JAVA-01`<br>
> Related roadmap: [Stage 1](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-1---java-core-state-và-concurrency)<br>
> Related depth rubric: [OOD/refactoring](../../../knowledge-depth-rubric.md#32-object-oriented-design-và-refactoring--p0-target-d3)<br>
> Related theory: [Core theory](../theory/core/object-oriented-design-solid-and-patterns.md), [Deep-dive](../theory/deep-dives/ood-substitutability-boundaries-and-pattern-overengineering.md) — `TEACHABLE_DRAFT`, evidence `NOT RUN`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `JAVA-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `OOD-DES-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### OOD-DES-001 — `FOUNDATION`
**Question:** Encapsulation khác việc chỉ đặt field `private` như thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Invariant được bảo vệ qua behavior/API, không chỉ access modifier.<br>
**Answer outline:** Object kiểm soát state transition và không để caller tạo trạng thái bất hợp lệ; immutable value object là một cách.<br>
**Required trade-offs:** API chặt tăng safety nhưng có thể tăng số method/domain type.<br>
**Follow-up ladder:** Anemic model có luôn xấu? Package-private dùng khi nào?<br>
**Red flags:** Getter/setter cho mọi field được coi là encapsulation.<br>
**Evidence:** [Core theory](../theory/core/object-oriented-design-solid-and-patterns.md) · [Deep-dive](../theory/deep-dives/ood-substitutability-boundaries-and-pattern-overengineering.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OOD-DES-002 — `FOUNDATION`
**Question:** Composition và inheritance khác nhau; khi nào ưu tiên composition?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Is-a contract, substitutability và runtime delegation.<br>
**Answer outline:** Inheritance phù hợp subtype ổn định tuân LSP; composition ghép behavior, giảm coupling vào hierarchy và dễ thay/test.<br>
**Required trade-offs:** Composition thêm object/wiring; inheritance ngắn nhưng dễ fragile base class.<br>
**Follow-up ladder:** Template Method vs Strategy? Final class?<br>
**Red flags:** Luôn dùng inheritance để tái sử dụng code.<br>
**Evidence:** [Core theory](../theory/core/object-oriented-design-solid-and-patterns.md) · [Deep-dive](../theory/deep-dives/ood-substitutability-boundaries-and-pattern-overengineering.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OOD-DES-003 — `FOUNDATION`
**Question:** Coupling, cohesion và dependency direction nghĩa là gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Module responsibility và change propagation.<br>
**Answer outline:** High cohesion gom behavior cùng lý do thay đổi; low coupling giảm knowledge giữa modules; dependency hướng về abstraction/policy ổn định.<br>
**Required trade-offs:** Thêm interface khắp nơi có thể tạo abstraction rỗng.<br>
**Follow-up ladder:** Afferent/efferent coupling? Package-by-feature?<br>
**Red flags:** Nhiều class nhỏ tự động là low coupling.<br>
**Evidence:** [Core theory](../theory/core/object-oriented-design-solid-and-patterns.md) · [Deep-dive](../theory/deep-dives/ood-substitutability-boundaries-and-pattern-overengineering.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OOD-DES-004 — `FOUNDATION`
**Question:** SOLID gồm những heuristic nào và giới hạn của chúng?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** SRP/OCP/LSP/ISP/DIP không bị đọc như luật máy móc.<br>
**Answer outline:** Dùng SOLID để hỏi reason-to-change, substitutability, client-specific contract và dependency direction; cân bằng simplicity/YAGNI.<br>
**Required trade-offs:** Tách quá mức tăng indirection và cognitive load.<br>
**Follow-up ladder:** Ví dụ vi phạm LSP? DIP có cần DI framework?<br>
**Red flags:** Một class chỉ được có một method.<br>
**Evidence:** [Core theory](../theory/core/object-oriented-design-solid-and-patterns.md) · [Deep-dive](../theory/deep-dives/ood-substitutability-boundaries-and-pattern-overengineering.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OOD-DES-005 — `SENIOR`
**Question:** Nhận diện và refactor một service có quá nhiều trách nhiệm thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Evidence từ change coupling, test pain và transaction boundary.<br>
**Answer outline:** Nhóm behavior theo invariant/use case, extract policy/value object/collaborator tại seam thật; giữ behavior bằng characterization tests.<br>
**Required trade-offs:** Refactor lớn sạch hơn nhưng tăng regression; incremental seam dễ rollback.<br>
**Follow-up ladder:** God service hay orchestration service?<br>
**Red flags:** Đếm số dòng rồi chia đều class.<br>
**Evidence:** [Core theory](../theory/core/object-oriented-design-solid-and-patterns.md) · [Deep-dive](../theory/deep-dives/ood-substitutability-boundaries-and-pattern-overengineering.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OOD-DES-006 — `SENIOR`
**Question:** State, Strategy, Adapter và Decorator giải quyết các forces khác nhau thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Chọn pattern theo variation/boundary, không theo tên.<br>
**Answer outline:** State đổi behavior theo lifecycle; Strategy thay policy; Adapter đổi contract ngoài; Decorator thêm cross-cutting quanh contract.<br>
**Required trade-offs:** Pattern làm intent rõ nhưng thêm types/wiring.<br>
**Follow-up ladder:** Proxy khác Decorator? Enum state machine đủ khi nào?<br>
**Red flags:** Gắn pattern để CV đẹp dù chỉ có một implementation.<br>
**Evidence:** [Core theory](../theory/core/object-oriented-design-solid-and-patterns.md) · [Deep-dive](../theory/deep-dives/ood-substitutability-boundaries-and-pattern-overengineering.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OOD-DES-007 — `SENIOR`
**Question:** LSP bị phá trong API/service contract có biểu hiện production nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Pre/postcondition, exception và semantic compatibility.<br>
**Answer outline:** Subtype không được siết input, nới output failure hoặc phá invariant caller dựa vào; contract tests theo base type phát hiện.<br>
**Required trade-offs:** Contract chặt giảm flexibility của subtype.<br>
**Follow-up ladder:** Rectangle-square? Repository specialization?<br>
**Red flags:** Compile được nghĩa là substitutable.<br>
**Evidence:** [Core theory](../theory/core/object-oriented-design-solid-and-patterns.md) · [Deep-dive](../theory/deep-dives/ood-substitutability-boundaries-and-pattern-overengineering.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OOD-DES-008 — `SENIOR`
**Question:** Review một refactor để chứng minh không đổi behavior thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Characterization, invariant, public contract và performance.<br>
**Answer outline:** Khóa observable behavior bằng tests, compare API/SQL/events/log-sensitive contract, rollout nhỏ và đo regression; không coi snapshot test là đủ.<br>
**Required trade-offs:** Giữ behavior cũ có thể giữ cả bug nên phải phân loại intended behavior.<br>
**Follow-up ladder:** Golden master? Mutation test?<br>
**Red flags:** Code đẹp hơn là bằng chứng correctness.<br>
**Evidence:** [Core theory](../theory/core/object-oriented-design-solid-and-patterns.md) · [Deep-dive](../theory/deep-dives/ood-substitutability-boundaries-and-pattern-overengineering.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OOD-DES-009 — `ARCHITECT`
**Question:** Thiết kế module boundaries khi cùng domain có nhiều team và cadence khác nhau thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Ownership, dependency rule, data/invariant và evolution.<br>
**Answer outline:** Boundary theo business capability/invariant, explicit module API/data owner, architecture tests và event/API contract; theo dõi coupling/cadence trước khi tách deploy.<br>
**Required trade-offs:** Boundary mạnh giảm accidental coupling nhưng tăng duplication/coordination.<br>
**Follow-up ladder:** Conway's Law? Shared kernel?<br>
**Red flags:** Vẽ package diagram là đủ tạo modularity.<br>
**Evidence:** [Core theory](../theory/core/object-oriented-design-solid-and-patterns.md) · [Deep-dive](../theory/deep-dives/ood-substitutability-boundaries-and-pattern-overengineering.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### OOD-DES-010 — `EXPERT`
**Question:** Khi nào một abstraction trở thành decision debt và nên collapse hoặc evolve ra sao?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Abstraction fitness dưới variation thật và migration safety.<br>
**Answer outline:** Đo consumers, duplicated conditionals, leaky concepts và change cost; collapse speculative interface hoặc version/migrate contract bằng characterization evidence.<br>
**Required trade-offs:** Giữ abstraction sai tốn cognitive load; xóa sớm có migration cost.<br>
**Follow-up ladder:** Rule of three? Anti-corruption layer lifetime?<br>
**Red flags:** Mọi abstraction phải giữ vĩnh viễn vì OCP.<br>
**Evidence:** [Core theory](../theory/core/object-oriented-design-solid-and-patterns.md) · [Deep-dive](../theory/deep-dives/ood-substitutability-boundaries-and-pattern-overengineering.md); case `JAVA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `JAVA-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.

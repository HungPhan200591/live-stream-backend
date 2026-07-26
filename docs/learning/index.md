# Senior Java Learning System

> Trạng thái: `CANONICAL LEARNING ENTRY POINT`<br>
> Phạm vi: Senior Java, Spring Boot, data, distributed systems và solution architecture<br>
> Cập nhật: 2026-07-26

> **Bạn là người học và chưa biết bắt đầu từ đâu?** Đọc [Hướng dẫn học và implement cùng AI Agent](guide.md), rồi dùng prompt bắt đầu chuẩn trong guide. Dùng [Knowledge Depth Rubric](knowledge-depth-rubric.md) để biết D1-D4 cụ thể cho từng capability và tự theo dõi mình đang ở đâu. File này chủ yếu lưu trạng thái/checkpoint cho session hiện tại.

Learning system nối kiến thức có thể tái sử dụng với failure thật trong project theo chu trình:

`Core theory -> Deep-dive -> Question bank -> Learning case -> Reproducer -> Design/trade-off -> Implementation -> Experiment/evidence -> Review -> Interview note/teach-back`

Lý thuyết chỉ được viết đầy đủ một lần. Question bank và learning case phải link tới theory; interview note phải dựa trên evidence, không sao chép lại textbook hoặc AI output.

Theory chỉ được dùng để tự học khi có `Teaching readiness: TEACHABLE_DRAFT` hoặc `LEARNER_VALIDATED`. `OUTLINE_ONLY` chỉ là coverage map/cheat sheet cần viết lại theo [theory quality audit](theory-quality-audit.md), không phải giáo trình hoàn chỉnh.

[Livestream Domain Use-case Catalog](use-case-catalog.md) là nơi tra bài toán concrete như 100.000 viewers, gift spike trong sự kiện livestream, reconnect storm hoặc global ban. Roadmap vẫn sở hữu thứ tự; catalog không tự active case và không thay session cursor.

## 1. Current session cursor

Block này là con trỏ để session sau tiếp tục. `Current checkpoint` là phase **chưa hoàn tất** đang cần xử lý, không phải phase gần nhất đã làm xong.

| Field | Value |
| --- | --- |
| Active case | [JDK-01 - Java 21 platform baseline and virtual-thread decision](cases/jdk-01-java21-platform-baseline.md) |
| Case status | `ACTIVE`; chưa có Java 21 compatibility, build hoặc runtime evidence |
| Current checkpoint | `THEORY_CORE` |
| Next action | Người học đọc [Java 21 platform baseline](topics/java/theory/core/java21-platform-baseline.md) từ mục 0 đến 13; sau đó viết mục 14 và trả lời guided self-check mục 15, rồi yêu cầu Agent phản biện để quyết định có đạt `THEORY_CORE` gate hay chưa |
| Required reading | `AGENTS.md`; [Senior Roadmap](../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md); active JDK-01 case; [Java 21 platform baseline](topics/java/theory/core/java21-platform-baseline.md) |
| Write target | `docs/learning/topics/java/theory/core/java21-platform-baseline.md` — learner write-back tại mục 14 và guided self-check mục 15; không sửa code |
| Latest evidence | Corpus có đủ `115` theory/deep-dive và `58` question-bank slices, nhưng quality audit đang được mở lại sau learner feedback: nhiều prose còn hoàn toàn bằng tiếng Anh và một số deep-dive nén causal chain quá mức. Không dùng readiness cũ làm bằng chứng cho tới khi language/depth review và audit chéo hoàn tất. Learner/case/runtime evidence vẫn `NOT RUN`, nên cursor/checkpoint/depth không đổi |
| Implementation gate | `LOCKED` cho tới khi theory, question rubric, compatibility audit, case review, baseline capture và design gate đạt |
| Blocker | Learner write-back/self-check của core theory chưa hoàn tất; declared Java 17/runtime drift chưa được chụp evidence; virtual-thread workload chưa được thiết kế |
| Updated | `2026-07-26` |

Khi kết thúc mỗi session, cập nhật block này với next action đủ nhỏ để thực hiện ngay. Nếu dừng giữa phase, giữ nguyên checkpoint và ghi rõ phần còn lại.

## 2. Resume protocol

1. Kiểm tra `git status` và đọc `AGENTS.md`.
2. Đọc block `Current session cursor`, active case và template của `Write target`.
3. Chỉ nạp theory, deep-dive, question bank, concern docs, code và test được cursor/case liên kết.
4. Xác minh evidence link tồn tại trước khi coi checkpoint trước đã hoàn tất.
5. Dùng `$run-senior-java-learning` để tiếp tục đúng checkpoint.
6. Không tự active case thứ hai; không tự đi tới implementation nếu scope session chưa cho phép.

## 3. Artifact ownership

| Artifact | Chứa gì | Không chứa gì |
| --- | --- | --- |
| `use-case-catalog.md` | Scenario Livestream concrete, invariant, output và owner learning item | Execution order hoặc trạng thái active |
| `topics/<domain>/theory/core` | Mental model, cơ chế, invariant và boundary nền tảng | Current code path riêng của project |
| `topics/<domain>/theory/deep-dives` | Internals, edge case, failure mode, scale, security, cross-layer interaction | Bản sao core theory |
| `topics/<domain>/question-bank` | Câu hỏi theo slice, level, interviewer intent, outline, follow-up và red flags | Bài luận hoặc câu trả lời cá nhân đầy đủ |
| `cases` | Project problem, invariant, code path, reproducer, alternatives và acceptance criteria | Toàn bộ textbook dùng lại |
| `experiments` | Environment, dataset, procedure, raw result, metric và interpretation | Claim không có số liệu hoặc lệnh tái lập |
| `interview-notes` | Câu trả lời cá nhân sau evidence, bản 2 phút/15 phút và teach-back gaps | Output AI chưa được người học sở hữu |
| `architecture/adr` | Quyết định dài hạn, alternatives và consequences | Quyết định cục bộ dễ đảo ngược không cần ADR |

Kiến thức dùng lại cho từ hai case trở lên phải đưa vào theory. Case và question bank link tới source of truth thay vì duplicate. Chỉ tạo folder khi artifact đầu tiên được viết; không scaffold hàng loạt cây rỗng.

## 4. Checkpoint gates

| Checkpoint | Evidence tối thiểu để đi tiếp |
| --- | --- |
| `THEORY_CORE` | Mental model do người học viết lại, mechanism/invariant và self-check foundation |
| `THEORY_DEEP_DIVE` | Internals, failure modes, edge cases, scale/security/operability và cross-layer trade-off |
| `QUESTION_BANK` | Có câu hỏi `FOUNDATION`, `SENIOR`, `ARCHITECT`, `EXPERT` với rubric và theory link |
| `LEARNING_CASE` | Failure thật trong project, scope lock, invariant, acceptance criteria và verification plan |
| `REPRODUCER` | Actual failing result cùng command/procedure chạy lại |
| `DESIGN_TRADE_OFF` | Ít nhất hai alternative thật, decision và rejected option |
| `IMPLEMENTATION` | Vertical slice, relevant tests và contract/docs sync |
| `EXPERIMENT_EVIDENCE` | Raw results, before/after hoặc fault evidence và interpretation |
| `REVIEW` | Không còn finding critical/high; residual risk được ghi |
| `INTERVIEW_NOTE_TEACH_BACK` | Câu trả lời 2 phút, deep-dive 15 phút và architect/expert answers có evidence |
| `CLOSED` | Case closure gate, roadmap/maturity và artifact links đã đồng bộ |

Question bank xuất hiện ở hai thời điểm: tạo ladder/rubric trước case, rồi trả lời lại câu `ARCHITECT`/`EXPERT` sau experiment và review.

## 5. Interview question levels

- `FOUNDATION`: khái niệm và cơ chế cơ bản.
- `SENIOR`: failure mode, debugging, transaction boundary, test và trade-off.
- `ARCHITECT`: scale, HA, consistency, migration, ownership, security, cost và vận hành.
- `EXPERT`: pathological case, cross-layer interaction, formal invariant, failure injection và giới hạn solution.

Level nằm trên từng câu hỏi, không chia folder theo level.

## 6. Coverage map và depth self-assessment

[Senior Roadmap](../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md) sở hữu thứ tự và độ phủ. [Knowledge Depth Rubric](knowledge-depth-rubric.md) sở hữu criteria D1-D4, current-depth tracker và evidence cụ thể theo capability. Bảng này chỉ route knowledge domain; không chứng minh đã học nếu chưa có artifact/evidence.

| Knowledge domain | Roadmap coverage | Trạng thái knowledge hiện tại |
| --- | --- | --- |
| Java Core, JMM và concurrency | Stage 0/1 | `10` core + `11` deep-dive là `TEACHABLE_DRAFT`, nối `166/166` câu qua `11` slices; JDK-01 đang `IN_PROGRESS`, compatibility evidence pending |
| Build, dependency lifecycle và CI/runtime | Stage 0/8 | CI/CD + Linux/JVM/container core/deep là `TEACHABLE_DRAFT`; JDK-01 toolchain evidence và delivery/runtime labs vẫn `NOT RUN` |
| Spring internals và transactions | Stage 2 | `5` core + `5` deep-dive là `TEACHABLE_DRAFT`, nối `52/52` câu; chưa active/học và evidence `NOT RUN` |
| HTTP/API semantics và compatibility | Stage 2 | `2` core + `2` deep-dive là `TEACHABLE_DRAFT`, nối `20/20` câu; chưa active/học và evidence `NOT RUN` |
| PostgreSQL và data lifecycle | Stage 3, 9 | `10` core + `10` deep-dive là `TEACHABLE_DRAFT`, nối `106/106` câu qua `10/10` slices; chưa active/học, case và runtime evidence `NOT RUN` |
| Redis và distributed state | Stage 4 | `2` core + `2` deep-dive là `TEACHABLE_DRAFT`, nối `20/20` câu; exact Redis runtime chưa pin và evidence `NOT RUN` |
| RabbitMQ, Kafka và event workflow | Stage 5, 6 | `3/3` core + `3/3` deep-dive là `TEACHABLE_DRAFT`, nối `30/30` câu hỏi; RabbitMQ là stack project, Kafka chỉ là comparison/preview và chưa thêm dependency; case/lab evidence `NOT RUN` |
| Security, realtime và abuse resistance | Stage 0, 7 | Security `7` pairs/`91` câu và Realtime `1` pair/`10` câu đều là `TEACHABLE_DRAFT` với per-question links đầy đủ; WebSocket/security labs và learner/case evidence `NOT RUN` |
| Testing, observability và performance | Stage 0, 8 | Testing `2` pairs/`26` câu và Observability `1` pair/`10` câu đều `TEACHABLE_DRAFT` với per-question links đầy đủ; TEST-01 vẫn đứng sau JDK-01 và mọi lab evidence `NOT RUN` |
| Resilience và distributed failure | Stage 2, 8, 10 | `3` core + `3` deep-dive là `TEACHABLE_DRAFT`, nối `30/30` câu; chưa active/học và evidence `NOT RUN` |
| Modular monolith và microservices | Stage 10 | Core/deep coverage cho DDD/module, distributed consistency và microservice extraction là `TEACHABLE_DRAFT`; question links đầy đủ, case evidence `NOT RUN` |
| Solution architecture | Stage 11 | Capacity/storage plus Cloud, Reactive và Platform option pairs là `TEACHABLE_DRAFT`; question links đầy đủ, capstone/load/fault/adoption evidence `NOT RUN` |
| Technical leadership và delivery | Xuyên suốt/Stage 12 | Core/deep theory cho review, ADR, incident, mentoring và decision facilitation là `TEACHABLE_DRAFT`, nối `10/10` câu; behavioral/interview evidence chỉ được ghi từ work thật và hiện `NOT RUN` |

Không tạo toàn bộ topic files từ bảng này. Chỉ tạo artifact của active checkpoint, rồi cập nhật status bằng evidence link.

## 7. Templates

- [Theory Note Template](../templates/theory-note-template.md): dùng cho core theory và deep-dive.
- [Question Bank Template](../templates/question-bank-template.md): dùng cho một domain, level gắn trên từng câu.
- [Learning Case Template](../templates/learning-case-template.md): nối knowledge với failure cụ thể của project.
- [Experiment Template](../templates/experiment-template.md): lưu procedure, raw result và interpretation.
- [Interview Note Template](../templates/interview-note-template.md): lưu câu trả lời cá nhân và teach-back sau evidence.

## 8. Current active slice

JDK-01 là item đầu tiên của Stage 0. Sau khi đóng, thứ tự còn lại của Stage 0 là `TEST-01 -> JDK-02 -> MIG-01 -> CFG-01 -> SEC-01 -> SEC-06 -> SEC-02 -> SEC-03 -> SEC-05`; sau đó mới sang Stage 1 `JAVA-01 -> JVM-01 -> CON-01` theo [Roadmap mục 6.1](../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#61-execution-queue-theo-stage-0-12). Index không duy trì execution queue thứ hai. Preview pack đã được tạo để chuẩn hóa format, nhưng cursor vẫn ở `THEORY_CORE` cho tới khi learner write-back/self-check đạt gate:

1. [Core theory Java 21 platform baseline](topics/java/theory/core/java21-platform-baseline.md) — `TEACHABLE_DRAFT`, current write target; learner viết mục 14–15.
2. [Deep-dive virtual threads/pinning](topics/java/theory/deep-dives/virtual-threads-and-pinning.md) — `TEACHABLE_DRAFT` cho checkpoint kế tiếp, chưa học/chưa evidence.
3. [JDK platform question bank](topics/java/question-bank/jdk-platform.md) — preview cho `QUESTION_BANK`.
4. Cập nhật case JDK-01, chụp baseline Maven/runtime và tạo compatibility reproducer khi người dùng yêu cầu thực thi.
5. Chỉ sau experiment mới tạo `experiments/jdk-01-java21-compatibility-and-virtual-thread-lab.md` và `interview-notes/jdk-01-java21-platform-baseline.md`.

File tồn tại chỉ chứng minh artifact đã được tạo; không chứng minh checkpoint hoặc depth gate đã hoàn tất.

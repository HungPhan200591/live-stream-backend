# Senior Java Learning System

> Trạng thái: `CANONICAL LEARNING ENTRY POINT`<br>
> Phạm vi: Senior Java, Spring Boot, data, distributed systems và solution architecture<br>
> Cập nhật: 2026-07-25

> **Bạn là người học và chưa biết bắt đầu từ đâu?** Đọc [Hướng dẫn học và implement cùng AI Agent](guide.md), rồi dùng prompt bắt đầu chuẩn trong guide. Dùng [Knowledge Depth Rubric](knowledge-depth-rubric.md) để biết D1-D4 cụ thể cho từng capability và tự theo dõi mình đang ở đâu. File này chủ yếu lưu trạng thái/checkpoint cho session hiện tại.

Learning system nối kiến thức có thể tái sử dụng với failure thật trong project theo chu trình:

`Core theory -> Deep-dive -> Question bank -> Learning case -> Reproducer -> Design/trade-off -> Implementation -> Experiment/evidence -> Review -> Interview note/teach-back`

Lý thuyết chỉ được viết đầy đủ một lần. Question bank và learning case phải link tới theory; interview note phải dựa trên evidence, không sao chép lại textbook hoặc AI output.

## 1. Current session cursor

Block này là con trỏ để session sau tiếp tục. `Current checkpoint` là phase **chưa hoàn tất** đang cần xử lý, không phải phase gần nhất đã làm xong.

| Field | Value |
| --- | --- |
| Active case | [SEC-01 - Access token vs refresh token confusion](cases/sec-01-access-vs-refresh-token.md) |
| Case status | `ACTIVE`; chưa có implementation hoặc runtime evidence |
| Current checkpoint | `THEORY_CORE` |
| Next action | Tạo core theory canonical về JWT token lifecycle; sau đó người học đọc, viết lại mental model và làm self-check trước khi mở rộng deep-dive |
| Required reading | `AGENTS.md`; [Senior Roadmap](../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md); active case; [Theory Note Template](../templates/theory-note-template.md) |
| Write target | `docs/learning/theory/core/security/jwt-token-lifecycle.md` — chỉ tạo khi bắt đầu artifact thật |
| Latest evidence | Case SEC-01 là bản nháp AI-assisted; reproducer và experiment vẫn `NOT RUN` |
| Implementation gate | `LOCKED` cho tới khi theory, question rubric, case review, reproducer và design gate đạt |
| Blocker | Core theory chưa tồn tại và người học chưa hoàn tất mental model/teach-back foundation bằng lời của mình |
| Updated | `2026-07-25` |

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
| `theory/core` | Mental model, cơ chế, invariant và boundary nền tảng | Current code path riêng của project |
| `theory/deep-dives` | Internals, edge case, failure mode, scale, security, cross-layer interaction | Bản sao core theory |
| `question-bank` | Câu hỏi, level, interviewer intent, outline, follow-up và red flags | Bài luận hoặc câu trả lời cá nhân đầy đủ |
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
| Java Core, JMM và concurrency | Stage 1 | `NOT_STARTED` |
| Spring internals và transactions | Stage 2 | `NOT_STARTED` |
| PostgreSQL và data lifecycle | Stage 3, 9 | `NOT_STARTED` |
| Redis và distributed state | Stage 4 | `NOT_STARTED` |
| RabbitMQ, Kafka và event workflow | Stage 5, 6 | `NOT_STARTED` |
| Security, realtime và abuse resistance | Stage 0, 7 | `IN_PROGRESS`: SEC-01 active; theory artifacts pending |
| Testing, observability và performance | Stage 0, 8 | `NOT_STARTED` |
| Modular monolith, microservices và resilience | Stage 10 | `NOT_STARTED` |
| Solution architecture | Stage 11 | `NOT_STARTED` |

Không tạo toàn bộ topic files từ bảng này. Chỉ tạo artifact của active checkpoint, rồi cập nhật status bằng evidence link.

## 7. Templates

- [Theory Note Template](../templates/theory-note-template.md): dùng cho core theory và deep-dive.
- [Question Bank Template](../templates/question-bank-template.md): dùng cho một domain, level gắn trên từng câu.
- [Learning Case Template](../templates/learning-case-template.md): nối knowledge với failure cụ thể của project.
- [Experiment Template](../templates/experiment-template.md): lưu procedure, raw result và interpretation.
- [Interview Note Template](../templates/interview-note-template.md): lưu câu trả lời cá nhân và teach-back sau evidence.

## 8. Current active slice

SEC-01 sẽ là lượt chạy thử đầu tiên của system, nhưng task chuẩn hóa hiện tại không implement case. Artifact được tạo dần theo checkpoint:

1. `theory/core/security/jwt-token-lifecycle.md`.
2. `theory/deep-dives/security/session-backed-jwt.md`.
3. `question-bank/security.md`.
4. Cập nhật case SEC-01 và tạo reproducer khi người dùng yêu cầu thực thi.
5. Chỉ sau experiment mới tạo `experiments/sec-01-token-purpose-negative-tests.md` và `interview-notes/sec-01-jwt-token-lifecycle.md`.

Các path trên là routing target, không phải bằng chứng file đã tồn tại.

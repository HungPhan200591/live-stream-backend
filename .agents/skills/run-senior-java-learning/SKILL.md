---
name: run-senior-java-learning
description: Điều phối và chuẩn hóa artifact cho toàn bộ chu trình học/phỏng vấn Senior Java/Spring Boot của live-stream-backend từ core theory, deep-dive, question bank theo level, learning case, reproducer, design/trade-off, implementation, experiment/evidence, review đến interview note và teach-back. Dùng khi bắt đầu/tiếp tục topic hoặc case, hỏi học gì tiếp theo, yêu cầu tạo learning pack theo format chuẩn, đào sâu lý thuyết rồi vận dụng vào project, hoặc khôi phục phiên từ checkpoint. Không dùng cho bug fix, feature implementation hay code review độc lập không có mục tiêu học.
---

# Điều phối học Senior Java

Tuân thủ `AGENTS.md`. Dùng roadmap để kiểm soát độ phủ kiến thức và dùng evidence gate để kiểm soát tiến độ; không lấy số lượng tài liệu hoặc code làm bằng chứng đã học.

## 1. Source of truth

- Dùng `docs/001_SENIOR_JAVA_INTERVIEW_ROADMAP.md` để biết phạm vi Senior Java/Spring Boot và case ưu tiên.
- Dùng `docs/learning/index.md` làm learning entry point và session cursor.
- Dùng learning case `ACTIVE` làm owner của problem, invariant, project code path và closure gate.
- Dùng code, test, raw experiment result và runtime signal làm bằng chứng hành vi.
- Dùng `docs/000_DOCUMENTATION_ORCHESTRATOR.md` để route concern docs và xử lý drift.

Chỉ một learning case chính được `ACTIVE`. Nếu roadmap, index và case lệch nhau, nêu drift và chưa chuyển checkpoint cho tới khi xác định lại source đúng.

## 2. Khởi động hoặc khôi phục session

1. Chạy `git status` và giữ nguyên thay đổi chưa commit.
2. Đọc `AGENTS.md`, `docs/learning/index.md` và block `Current session cursor`.
3. Đọc active case, checkpoint hiện tại và đúng template của artifact sắp ghi.
4. Chỉ đọc roadmap section, knowledge artifact trong `topics/<domain>`, concern docs, code và test được cursor hoặc active case liên kết; không nạp toàn bộ `docs/`.
5. Kiểm tra evidence link thực sự tồn tại trước khi coi phase trước đã hoàn thành.
6. Tiếp tục từ `Current checkpoint`; không tự suy đoán tiến độ từ một checklist hoặc bản nháp do AI tạo.

Nếu chưa có active case, chọn một case có prerequisite phù hợp từ roadmap và chỉ kích hoạt sau khi ghi rõ objective, scope và next checkpoint. Không tự active nhiều case để “cover roadmap” nhanh hơn.

## 3. Chu trình knowledge-to-evidence

Thực hiện tuần tự, nhưng chỉ xử lý checkpoint người dùng yêu cầu hoặc checkpoint gần nhất trong một session:

| Checkpoint | Công việc chính | Exit gate |
| --- | --- | --- |
| `THEORY_CORE` | Tạo/đọc source canonical về mental model, mechanism, invariant và boundary có thể tái sử dụng; người học viết lại phần tự kiểm tra | Người học tự diễn giải được foundation và link nguồn chính thức |
| `THEORY_DEEP_DIVE` | Đào internals, failure mode, edge case, security, scale và cross-layer interaction | Có causal explanation và trade-off, không chỉ định nghĩa |
| `QUESTION_BANK` | Tạo câu hỏi/rubric `FOUNDATION`, `SENIOR`, `ARCHITECT`, `EXPERT`; trả lời foundation/senior trước | Mỗi câu có interviewer intent, follow-up, red flags và theory link |
| `LEARNING_CASE` | Chọn failure thật trong project; khóa scope, invariant, acceptance criteria | Case nối theory với current code path và có verification plan |
| `REPRODUCER` | Tạo red test, manual request hoặc experiment nhỏ trước khi sửa | Failure có actual result và lệnh chạy lại |
| `DESIGN_TRADE_OFF` | So sánh alternatives theo correctness, complexity, performance, operability và cost | Decision cùng rejected options được bảo vệ bằng invariant/evidence |
| `IMPLEMENTATION` | Thực hiện vertical slice nhỏ nhất, test và docs sync | Relevant checks pass; không mở rộng sang case kế tiếp |
| `EXPERIMENT_EVIDENCE` | Inject failure, đo before/after và giữ raw result | Hypothesis được hỗ trợ hoặc bác bỏ bằng artifact tái lập được |
| `REVIEW` | Review correctness, security, transaction/concurrency, failure recovery và docs drift | Không còn finding critical/high chưa xử lý; residual risk được ghi |
| `INTERVIEW_NOTE_TEACH_BACK` | Trả lời lại `ARCHITECT`/`EXPERT` bằng evidence; viết bản 2 phút và deep-dive 15 phút | Người học teach-back không cần đọc AI output |
| `CLOSED` | Đồng bộ case status, roadmap/maturity và link artifacts | Closure gate của case hoàn tất |

Question bank được dùng hai lần: tạo ladder và rubric tại `QUESTION_BANK`, rồi trả lời lại câu `ARCHITECT`/`EXPERT` bằng evidence ở `INTERVIEW_NOTE_TEACH_BACK`.

Khi người dùng yêu cầu rõ một **preview learning pack** để chuẩn hóa format, có thể tạo trước core theory, deep-dive và question bank trong cùng change, nhưng:

- mọi artifact giữ `DRAFT`, learner section giữ `LEARNER TODO` và evidence giữ `NOT RUN`;
- cursor vẫn trỏ checkpoint sớm nhất chưa đạt, không nhảy theo số file đã tạo;
- deep-dive/question bank là preview cho phase sau, không chứng minh phase đó hoàn tất;
- không tạo experiment result, interview note hoặc claim depth D3/D4 trước evidence.

Không tự chuyển sang `IMPLEMENTATION` nếu request chỉ yêu cầu học lý thuyết, thiết kế hoặc chuẩn bị case. Khi implementation được yêu cầu, dùng `$implement-livestream-feature`; khi review được yêu cầu, dùng `$review-livestream-change`.

## 4. Ghi kiến thức đúng chỗ

| Artifact | Owner của nội dung | Vị trí và template |
| --- | --- | --- |
| Core theory | Mental model, mechanism, invariant dùng lại | `docs/learning/topics/<domain>/theory/core/<topic>.md` từ `docs/templates/theory-note-template.md` |
| Deep-dive | Internals, failure, scale, security, cross-layer behavior | `docs/learning/topics/<domain>/theory/deep-dives/<topic>.md` từ theory template |
| Question bank | Câu hỏi, level, rubric, follow-up, red flags | `docs/learning/topics/<domain>/question-bank/<slice>.md` từ `docs/templates/question-bank-template.md` |
| Learning case | Chi tiết riêng project, reproducer, design, implementation gate | `docs/learning/cases/<case-id>-<slug>.md` từ learning-case template |
| Experiment | Environment, procedure, raw result, metric, interpretation | `docs/learning/experiments/<case-id>-<slug>.md` từ experiment template |
| Interview note | Câu trả lời cá nhân đã cô đọng sau evidence | `docs/learning/interview-notes/<case-id>-<topic>.md` từ interview-note template |
| ADR | Quyết định kiến trúc dài hạn và consequences | `docs/architecture/adr/<decision>.md` khi có quyết định thật |

Áp dụng các rule:

- Đưa kiến thức dự kiến dùng lại cho từ hai case vào core theory.
- Đưa internals hoặc phân tích nâng cao, pathological/cross-layer cases vào deep-dive.
- Chọn một `primary domain` theo capability sở hữu mental model; concern phụ được link cross-domain, không copy cùng kiến thức vào nhiều topic tree.
- Chia question bank theo slice ổn định như `jdk-platform.md`, `language-collections.md`; không dồn toàn domain vào một file tăng vô hạn.
- Giữ project code path và current failure trong case; không biến case thành textbook.
- Giữ câu trả lời đầy đủ trong theory và câu trả lời cá nhân trong interview note; question bank chỉ giữ outline/rubric.
- Link đến source of truth thay vì sao chép nội dung.
- Khi phát hiện misconception mới, cập nhật theory hoặc thêm negative test.
- Không tạo trước cây folder/file rỗng; tạo directory khi ghi artifact đầu tiên.

### Artifact contract bắt buộc

- **Core theory:** có target depth, scope, mental model/mechanism/invariant/boundary, misconceptions, self-check, official sources; phần learner write-back không được Agent điền thay.
- **Deep-dive:** link core theory, chỉ thêm internals, pathological/failure cases, version boundary, cross-layer/operability và experiment implication; không copy core.
- **Question bank:** dùng ID ổn định `<DOMAIN>-<TOPIC>-NNN`; mỗi câu có level, target depth, interviewer intent, answer outline, trade-off/follow-up/red flags và evidence status. Outline không phải full answer.
- **Learning case:** chỉ giữ current project path, invariant, hypothesis, reproducer/design gate và links tới reusable knowledge.
- **Experiment:** được tạo `PLANNED` khi procedure/hypothesis cụ thể; raw results phải là output thật, không sinh số liệu mẫu như evidence.
- **Interview note:** chỉ tạo sau evidence; dùng giọng người học và link claim tới test/experiment/review.
- **Version-sensitive topic:** pin release/runtime/framework version trong claim và dùng primary/official source; ghi rõ khi behavior thay đổi giữa version.

## 5. Phân cấp câu hỏi

- `FOUNDATION`: khái niệm, vocabulary và cơ chế nền tảng.
- `SENIOR`: failure mode, debugging, test, transaction boundary và trade-off.
- `ARCHITECT`: scale, HA, consistency, migration, ownership, security, cost và vận hành.
- `EXPERT`: pathological case, cross-layer interaction, formal invariant, failure injection và giới hạn solution.

Không hạ level chỉ vì câu hỏi dùng thuật ngữ khó. Level được xác định bởi depth của reasoning, failure boundary và trade-off phải bảo vệ.

Khi tạo hoặc luyện question bank, không chia đều máy móc theo level:

- Gắn `Interview likelihood: HIGH | MEDIUM | LOW` cho từng câu. Đây là heuristic trong phạm vi topic/role, không phải thống kê thị trường.
- Đi theo thứ tự `HIGH FOUNDATION -> HIGH/MEDIUM SENIOR -> project application -> ARCHITECT/EXPERT stretch`; không mở đầu bằng pathological hoặc multi-region case.
- Ưu tiên câu hỏi core thường gặp và cách diễn đạt tự nhiên của interviewer; dùng câu riêng của project để chứng minh khả năng vận dụng, không thay thế foundation.
- Một bank mặc định nên có nhiều câu `FOUNDATION`/`SENIOR` xác suất cao hơn câu `ARCHITECT`/`EXPERT` xác suất thấp. Với slice hẹp, ghi rõ giới hạn thay vì bịa thêm câu phổ biến.
- Mỗi file có `Recommended practice order` để người học biết câu nào phải trả lời trước. Câu `LOW` vẫn được giữ làm stretch discriminator nhưng không nằm trong first pass.

## 6. Kết thúc session và lưu checkpoint

Trước khi bàn giao:

1. Cập nhật artifact đã làm và evidence link; không ghi `DONE` cho bước chưa kiểm chứng.
2. Cập nhật `Current session cursor` trong `docs/learning/index.md` với checkpoint chưa hoàn tất kế tiếp, next action nguyên tử, required reading, write target, evidence mới nhất, blocker và ngày cập nhật.
3. Cập nhật active case nếu scope, hypothesis, reproducer, decision, findings hoặc closure gate thay đổi.
4. Chỉ cập nhật roadmap/maturity khi evidence gate tương ứng đã tồn tại.
5. Báo verification đã chạy, chưa chạy và điều người học phải tự teach-back.

6. Với docs-only learning pack, validate tối thiểu: frontmatter/status, stable question IDs, relative links/anchors, không có evidence giả, cursor vẫn ở earliest incomplete checkpoint và `git diff --check`.

Nếu session dừng giữa checkpoint, giữ nguyên checkpoint và ghi next action cụ thể; không nhảy phase để tạo cảm giác tiến bộ.

## 7. Trigger calibration

Nên trigger cho các request như:

- “Tiếp tục learning case `ACTIVE` từ checkpoint gần nhất.”
- “Cho tôi học sâu transaction isolation rồi nối vào một reproducer trong project.”
- “Học gì tiếp theo để cover Senior Java/Spring Boot và luyện câu hỏi architect?”
- “Đóng learning case này và luyện teach-back.”

Không trigger cho các request độc lập như “fix test auth đang đỏ”, “thêm endpoint”, “review commit này” hoặc “giải phóng port 8080” nếu người dùng không đặt chúng trong một learning case.

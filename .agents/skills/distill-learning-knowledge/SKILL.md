---
name: distill-learning-knowledge
description: Cô đọng kiến thức vừa được giải thích hoặc phát hiện trong lúc học vào đúng artifact của live-stream-backend. Dùng khi người dùng muốn lưu một khái niệm, misconception, failure chain hoặc trade-off; cần quyết định nên ghi vào theory core, deep-dive, learning case, question bank hay learner write-back; hoặc muốn tránh duplicate/note rời. Không dùng để tự tạo runtime evidence, tự viết thay learner, mở case mới hoặc chuyển checkpoint.
---

# Cô đọng kiến thức học

Tuân thủ `AGENTS.md`. Lời gọi skill này là quyền cập nhật learning artifact được xác định, trừ khi người dùng nói rõ chỉ muốn đề xuất. Trước khi ghi, chạy `git status`, giữ nguyên thay đổi không liên quan và chỉ đọc source liên kết trực tiếp.

## Phân loại trước khi ghi

| Kiến thức vừa làm rõ | Nơi sở hữu | Không ghi vào |
| --- | --- | --- |
| Mental model, vocabulary, mechanism, invariant hoặc boundary tái dùng từ hai case trở lên | `topics/<domain>/theory/core/` | Case/project note |
| Internals, pathological/failure chain, version boundary, cross-layer diagnostic hoặc architecture trade-off nâng cao | `topics/<domain>/theory/deep-dives/` | Core nếu làm loãng foundation |
| Câu hỏi/rubric/follow-up/red flag mới, không phải bài giảng đầy đủ | `topics/<domain>/question-bank/` | Theory như một hàng keyword |
| Code path, configuration, decision, failure hoặc acceptance criteria chỉ của case hiện tại | `docs/learning/cases/` | Reusable theory |
| Procedure, environment, raw command output, metric và interpretation đã thực sự chạy | `docs/learning/experiments/` | Theory/case như evidence giả |
| Lời giải thích bằng từ của người học | Mục learner write-back/self-check của target | Agent không được tự điền câu trả lời thay |
| Câu trả lời phỏng vấn cá nhân có claim đã được evidence | `docs/learning/interview-notes/` | Trước experiment/review evidence |

Nếu insight chỉ là cách diễn đạt lại phần target đã dạy đủ, không tạo duplicate. Trả lời ngắn trong chat hoặc hướng người học quay lại section hiện có.

## Workflow

1. Nắm claim tối thiểu: người học đang hỏi gì, câu trả lời đã được xác minh bằng nguồn nào, phạm vi tái sử dụng và có evidence runtime hay chỉ là knowledge.
2. Đọc `AGENTS.md`, artifact đang được học và template phù hợp; đọc `docs/learning/index.md`/active case chỉ khi insight liên quan session hoặc case đó.
3. Dùng `rg` để tìm section/term đã tồn tại trong candidate artifact trước khi tạo section/file. Ưu tiên mở rộng prose hiện có đúng chỗ thay vì thêm “Notes” hoặc lặp lại định nghĩa.
4. Chọn owner theo bảng. Nếu ranh giới chưa rõ, nêu hai option, khuyến nghị theo khả năng tái sử dụng và không ghi cho đến khi người dùng chọn nếu khác biệt có ý nghĩa.
5. Cô đọng theo nhịp `vấn đề -> trực giác -> định nghĩa -> cơ chế -> ví dụ -> giới hạn`; với failure ghi causal chain `trigger -> state -> symptom -> evidence -> mitigation -> residual risk`.
6. Giữ tiếng Việt tự nhiên; giải nghĩa term mới lần đầu, không biến phần bổ sung thành keyword dump. Nếu thêm/sửa Mermaid, dùng `$mermaid-styling`.
7. Không suy diễn output, test pass, metric hoặc learner understanding. Giữ nguyên trạng thái chưa chạy và phần trả lời dành cho learner khi evidence chưa tồn tại.
8. Cập nhật cursor/case/roadmap chỉ khi insight thật sự thay đổi next action, scope, hypothesis hoặc evidence; một clarification thông thường không làm checkpoint tiến lên.

## Kiểm tra trước bàn giao

- Nội dung mới có owner duy nhất, không duplicate source of truth.
- Relative links/anchors, metadata và stable question IDs (nếu có) vẫn hợp lệ.
- Nếu sửa theory, thực hiện lượt đọc riêng về language/depth; kiểm tra example/failure mới có prose giải thích chứ không chỉ table.
- Chạy `git diff --check`; báo rõ validation nào không áp dụng.

## Output

Báo ngắn: insight đã cô đọng, artifact/section được chọn và lý do, nội dung nào cố ý không ghi, evidence/checkpoint có thay đổi hay không, cùng validation đã chạy.

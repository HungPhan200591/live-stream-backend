---
name: audit-learning-theory
description: Đánh giá chất lượng tự học của tài liệu theory `CORE` hoặc `DEEP_DIVE` trong live-stream-backend. Dùng khi người dùng hỏi tài liệu đã đủ kiến thức chưa, có thể trả lời self-check/phỏng vấn chưa, cần audit teaching readiness, hoặc muốn chỉ ra gap về mental model, cơ chế, ví dụ, failure, trade-off hay tiếng Việt. Không dùng để tự mở learning case, triển khai code, tạo evidence runtime hoặc tự điền learner write-back.
---

# Đánh giá tài liệu theory

Tuân thủ `AGENTS.md`. Mặc định đây là review chỉ đọc: không sửa artifact, không đổi `Teaching readiness`, status, cursor hay checkpoint trừ khi người dùng yêu cầu rõ một thay đổi.

## Nguồn và phạm vi

1. Chạy `git status` trước mọi khả năng ghi file và giữ nguyên thay đổi của người dùng.
2. Đọc `AGENTS.md`, target theory, [`docs/templates/theory-note-template.md`](../../../docs/templates/theory-note-template.md) và [`docs/learning/theory-quality-audit.md`](../../../docs/learning/theory-quality-audit.md).
3. Chỉ đọc active case, question bank, deep-dive/core liên kết trực tiếp khi cần kiểm một claim, self-check hoặc project application; không nạp toàn bộ docs/corpus.
4. Kiểm tra target là `CORE` hay `DEEP_DIVE` từ metadata. Không yêu cầu core phải dạy internals vốn thuộc deep-dive; không cho deep-dive bỏ qua prerequisite của core.

## Hai lượt audit độc lập

### 1. Content, structure và truthfulness

Đối chiếu target với template theo đúng loại artifact.

- Với `CORE`, kiểm tra: vấn đề/đối tượng học, learning objectives, thuật ngữ được giải nghĩa, mental model do Agent dạy, cơ chế theo sequence, ít nhất hai worked examples và phản ví dụ, invariant/boundary, failure causal chain, solution/trade-off, project application, interview outline, self-check có read-back/rubric.
- Với `DEEP_DIVE`, kiểm tra: link/recap core có giới hạn, internals hoặc state/sequence, ít nhất hai pathological cases kể đủ trigger -> state -> symptom -> evidence -> mitigation -> residual risk, version/cross-layer boundary, diagnostic hoặc experiment walkthrough, architecture trade-off và advanced self-check.
- Nếu có diagram lifecycle/state/causal chain, đánh giá nó có giải thích được bằng prose và dễ đọc trong viewport hay không. Chỉ khi sửa Mermaid mới dùng `$mermaid-styling`.
- Kiểm tra claim evidence: tài liệu có phân biệt kiến thức, hypothesis và runtime/test result; không coi file/link/status là evidence đã chạy.

### 2. Ngôn ngữ và khả năng tự học

Đọc lại toàn bộ prose như developer Việt chưa biết topic.

- Kiểm tra mỗi concept mới đi từ vấn đề/trực giác tới định nghĩa, cơ chế, ví dụ và giới hạn.
- Đánh dấu đoạn điện tín, keyword/table chưa có causal narrative, English nối câu không cần thiết, hoặc term chưa được giải thích lần đầu.
- Với mỗi failure quan trọng, kiểm tra người đọc có thể theo liên tục trigger -> internal state -> symptom -> evidence -> mitigation mà không tự đoán mắt xích hay không.
- Phân biệt “tài liệu đủ để học” với “người học đã hiểu”: learner write-back/self-check chưa được trả lời thì không được claim `LEARNER_VALIDATED`.

## Đánh giá khả năng trả lời câu hỏi

Khi người dùng hỏi “đọc xong có trả lời được hết không?”, map từng self-check hoặc câu hỏi được nêu tới section dạy kiến thức đó và rubric cần có.

- Kết luận `Có nền tảng để trả lời` khi câu hỏi được dạy trực tiếp, có prose giải thích và rubric/read-back tương ứng.
- Kết luận `Chưa đủ` khi câu hỏi đòi prerequisite, deep-dive, project evidence hoặc knowledge không tồn tại trong target; nêu chính xác phần cần đọc/viết thêm.
- Không hứa chắc người học sẽ trả lời tốt chỉ vì tài liệu đủ. Đề xuất nhịp: đọc teaching content -> đóng file và write-back -> tự trả lời -> chỉ mở lại section được gợi ý -> nhận phản biện.

## Output bắt buộc

Trả lời ngắn, evidence-backed và theo thứ tự:

1. Phạm vi đã đọc và loại artifact.
2. Verdict: `TEACHABLE_DRAFT`, `NEEDS_REVISION`, hoặc `OUTLINE_ONLY`; nói rõ đây là verdict của nội dung, không phải learner/runtime evidence.
3. Điểm mạnh đã kiểm chứng.
4. Gap/blocker có vị trí section/heading và tác động lên người học.
5. Bảng map question/self-check -> section dạy -> mức sẵn sàng, khi người dùng hỏi về khả năng trả lời.
6. Next action nhỏ nhất; không tự nhảy checkpoint hay sang implementation.

Nếu phát hiện mâu thuẫn giữa target, active case, index hoặc quality audit, nêu rõ drift và giữ nguyên checkpoint/readiness cho tới khi có quyết định hoặc evidence phù hợp.

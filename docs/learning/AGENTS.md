# Rule cho hệ learning và phỏng vấn

## Phạm vi và checkpoint

- Áp dụng cho mọi artifact dưới `docs/learning/`. Đọc cùng `AGENTS.md` ở repository root.
- Mỗi learning session phải đọc và cập nhật cursor trong `docs/learning/index.md` khi task thực sự thay đổi tiến độ.
- Chỉ một learning case chính được `ACTIVE`. Không tự mở case kế tiếp khi case hiện tại chưa đóng hoặc pause có lý do.
- Use-case priority không thay đổi Stage order. Khi tạo case mới, chọn scenario có prerequisite phù hợp từ `use-case-catalog.md` và đặt tên bằng actor/tải/failure/outcome concrete, không dùng tên công nghệ làm problem statement.
- Không tăng checkpoint, maturity hoặc tạo evidence nếu chưa đạt gate thực tế.

## Tổ chức artifact

- Core theory: `topics/<domain>/theory/core`.
- Internals/cross-layer analysis: `topics/<domain>/theory/deep-dives`.
- Question/rubric: `topics/<domain>/question-bank`.
- Project detail: `cases`; số đo: `experiments`; teach-back cá nhân sau evidence: `interview-notes`.
- Link tới source of truth thay vì sao chép. Misconception mới phải cập nhật theory hoặc negative test liên quan.

## Chất lượng theory và deep-dive

- Dùng `docs/templates/theory-note-template.md` làm content contract và `docs/learning/theory-quality-audit.md` làm readiness audit.
- Tài liệu cho người chưa biết topic phải dạy mental model, mechanism, ví dụ, failure và trade-off trước; không viết thành cheat sheet hoặc matrix keyword.
- `LEARNER TODO` chỉ dành cho learner write-back/self-check ở cuối tài liệu; Agent không viết thay learner.
- Giữ tiếng Việt tự nhiên, có dấu. English chỉ giữ cho API, type, lệnh, metric hoặc thuật ngữ ngành và phải giải nghĩa khi cần.
- Table chỉ cô đọng phần đã giải thích. Lifecycle/state/sequence/causal chain nên dùng Mermaid dễ đọc theo skill `$mermaid-styling`.
- Đọc lại như developer Việt chưa biết topic: mỗi đoạn phải trả lời được điều gì xảy ra, vì sao, evidence nào chứng minh và xử lý ra sao.
- Nội dung còn điện tín, chuỗi keyword hoặc bảng chưa giải thích phải giữ `Teaching readiness: OUTLINE_ONLY` hoặc tiếp tục hoàn thiện.

## Boundary

- Không dùng số file, heading hoặc word count thay cho teaching gate.
- Không tự tạo runtime evidence, tự điền learner write-back hoặc implement case chỉ vì đang sửa theory.
- Preview chưa đủ teaching content phải ghi `OUTLINE_ONLY`; không giới thiệu là tài liệu tự học hoàn chỉnh.

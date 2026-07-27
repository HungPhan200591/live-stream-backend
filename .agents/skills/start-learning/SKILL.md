---
name: start-learning
description: Khởi đầu hoặc khôi phục một phiên học trong live-stream-backend bằng cách đọc session cursor, xác định active case/checkpoint và trả về các link trực tiếp tới mọi file cần mở. Dùng khi người dùng nói “bắt đầu học”, “bắt đầu session”, “tiếp tục học”, hoặc gọi `$start-learning`. Không dùng cho bug fix, implementation hay review không có mục tiêu học.
---

# Bắt đầu phiên học

Tuân thủ `AGENTS.md` và dùng skill này trước `$run-senior-java-learning` khi người dùng bắt đầu hoặc quay lại một session. Mục đích là đưa người học đến đúng nguồn canonical trước khi dạy hoặc yêu cầu write-back.

## Workflow

1. Chạy `git status --short` và chỉ báo các thay đổi có sẵn; không sửa chúng.
2. Đọc `AGENTS.md`, `docs/learning/index.md`, block `Current session cursor`, active case và mọi file trong `Required reading`. Chỉ đọc section roadmap liên quan tới active case. Nếu cursor, index và case mâu thuẫn, nêu drift và giữ nguyên checkpoint.
3. Xác minh mỗi link evidence mà cursor dùng để kết luận phase trước tồn tại. Không tự nâng checkpoint, tạo learner write-back hoặc chạy infrastructure/code khi người dùng chỉ bắt đầu học.
4. Trước bất kỳ nội dung giảng dạy nào, trả lời bằng **Bản đồ mở tài liệu**. Mỗi file cần đọc phải là Markdown link local tuyệt đối, gồm tối thiểu:
   - `AGENTS.md`;
   - `docs/learning/index.md` tại `Current session cursor`;
   - active case;
   - section roadmap liên quan;
   - mọi artifact trong `Required reading`;
   - `Write target` khi cursor yêu cầu người học viết.
5. Ghi rõ checkpoint, mục tiêu của session, hành động người học kế tiếp và giới hạn hiện tại. Sau đó hand off cho `$run-senior-java-learning` để dạy đúng checkpoint.

## Contract trả lời

Dùng format ngắn này; không thay link bằng tên file thuần văn bản:

```markdown
Checkpoint: `...` — ...

Mở theo thứ tự:

1. [AGENTS.md](/absolute/path/AGENTS.md)
2. [Learning cursor](/absolute/path/docs/learning/index.md:line)
3. [Active case](/absolute/path/docs/learning/cases/...md)
4. [Roadmap — section](/absolute/path/docs/...md:line)
5. [Theory/write target](/absolute/path/docs/...md)

Hôm nay: ...
Bạn cần làm tiếp: ...
```

Chỉ liệt kê file thực sự liên quan tới cursor. Link line number là tùy chọn khi đã xác minh; không bịa line/anchor. Nếu một file không tồn tại, ghi rõ file thiếu và coi đó là blocker thay vì đưa link hỏng.

## Guardrails

- Không coi file `DRAFT`/`OUTLINE_ONLY` là bài tự học đủ chất lượng.
- Không đi qua `THEORY_CORE` khi learner chưa tự write-back và tự trả lời self-check.
- Không tự mở active case thứ hai, không tự chuyển sang implementation hay tạo evidence giả.
- Khi bắt đầu một session, link là phần bắt buộc của câu trả lời, kể cả khi người học chỉ viết “Bắt đầu học”.

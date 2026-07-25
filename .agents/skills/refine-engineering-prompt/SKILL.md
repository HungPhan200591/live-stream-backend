---
name: refine-engineering-prompt
description: Làm rõ yêu cầu kỹ thuật thô bằng tiếng Việt hoặc tiếng Anh cho live-stream-backend thành prompt có thể thực thi với phạm vi, context, ràng buộc, Acceptance Criteria và cách xác minh. Dùng khi người dùng yêu cầu tối ưu, làm rõ, viết lại, cấu trúc hoặc cải thiện prompt backend trước khi triển khai.
---

# Tối ưu prompt kỹ thuật

Chỉ dùng skill này để làm rõ yêu cầu; không sửa file hoặc triển khai task nếu người dùng chưa yêu cầu thực thi.

## Quy trình

1. Trích xuất objective, repository context, constraint, ngôn ngữ đầu ra mong muốn và quyết định còn thiếu.
2. Giữ nguyên yêu cầu cụ thể của người dùng. Không tự tạo API, infrastructure, deadline hoặc acceptance criteria.
3. Trả về một prompt có thể sao chép theo cấu trúc ngắn gọn sau khi phù hợp:

```text
Ngữ cảnh
Mục tiêu
Phạm vi và ràng buộc
Hành vi mong đợi
Tiêu chí chấp nhận
Kiểm chứng
Định dạng phản hồi
```

4. Liệt kê ngắn gọn assumption quan trọng hoặc một blocking question. Chỉ hỏi khi câu trả lời có thể thay đổi implementation đáng kể.
5. Chỉ đề nghị thực thi prompt sau khi đã trình bày bản tối ưu.

## Mặc định theo dự án

Khi liên quan, đưa vào prompt các ràng buộc từ `AGENTS.md`: current/target Java-Spring baseline lấy từ POM và learning cursor, DTO-first API, không dùng JPA relationship annotation, authorization hai tầng, Redis TTL/invalidation, OpenAPI cùng `.http`, simulation-first và test phù hợp. Không hard-code version target đã cũ vào prompt mới.

---
name: review-livestream-change
description: Review diff, commit, pull request hoặc local change của live-stream-backend để phát hiện lỗi chức năng, API contract drift, authorization flaw, transaction/concurrency, JPA/query, Redis consistency, RabbitMQ/WebSocket reliability, thiếu test và documentation mismatch. Dùng khi cần code review, risk assessment, regression review hoặc pre-merge validation.
---

# Review thay đổi livestream

Tuân thủ `AGENTS.md`. Chỉ review; không sửa file nếu người dùng chưa yêu cầu fix.

## Xác định phạm vi review

1. Xác định diff/base được yêu cầu và kiểm tra `git status`.
2. Đọc file thay đổi cùng caller, callee, test, configuration và contract đủ để xác thực hành vi.
3. Dùng tài liệu liên quan từ `AGENTS.md`, nhưng kiểm chứng nhận định bằng code và test đang chạy.

## Review theo thứ tự rủi ro

1. Functional correctness và business state transition.
2. Authentication, role/ownership authorization, secret handling và public route ngoài ý muốn.
3. Data integrity: transaction boundary, concurrency, uniqueness, explicit foreign ID và query behavior.
4. Distributed state: Redis TTL/invalidation/fallback; RabbitMQ/WebSocket/webhook idempotency, ordering, retry và acknowledgement.
5. API compatibility: method/path/status/payload, DTO validation, `ApiResponse`, exception mapping, OpenAPI và `.http` example.
6. Test: coverage cho hành vi và failure, không chỉ line execution.
7. Maintainability issue cụ thể do thay đổi này tạo ra.

Không báo style preference là defect. Không giả định method an toàn nếu chưa lần theo caller và security context.

## Xác minh finding

- Nêu file và line chính xác.
- Giải thích tình huống kích hoạt và impact cho người dùng/hệ thống.
- Kiểm tra test hiện có đã cover scenario hay chưa.
- Chạy kiểm tra tập trung, không làm thay đổi dữ liệu nếu có thể xác nhận hoặc bác bỏ finding.
- Loại bỏ finding suy đoán không thể chứng minh bằng code hoặc reproducer.

## Kết quả

Liệt kê finding trước, theo severity `critical`, `high`, `medium`, `low`. Mỗi finding gồm location, vấn đề, tình huống kích hoạt/impact và hướng remediation ngắn. Sau đó nêu open question cùng tóm tắt test/coverage. Nếu không có finding, nói rõ điều đó và nêu residual risk hoặc check chưa chạy.

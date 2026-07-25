---
name: diagnose-livestream-backend
description: Chẩn đoán build failure, test regression, HTTP error, authorization, JWT/session, PostgreSQL/JPA, Redis cache, RabbitMQ, WebSocket, webhook và Docker connectivity cục bộ của live-stream-backend. Dùng khi cần điều tra, giải thích, tái hiện, khắc phục hoặc troubleshoot lỗi backend.
---

# Chẩn đoán backend livestream

Tuân thủ `AGENTS.md`. Phân biệt bằng chứng đã xác nhận với giả thuyết; không triển khai fix nếu người dùng chỉ yêu cầu chẩn đoán.

## 1. Tái hiện tối thiểu

1. Ghi nhận triệu chứng, hành vi mong đợi, điểm vào và môi trường liên quan.
2. Kiểm tra `git status` và code liên quan trước khi chạy lệnh.
3. Dùng cách tái hiện nhỏ nhất: test tập trung, Maven phase đơn lẻ, một `.http` request hoặc log mục tiêu.
4. Che credential, JWT, session ID, webhook secret và dữ liệu cá nhân trong output.

## 2. Lần theo từng subsystem

- Build/startup: declared/target/runtime JDK lấy từ POM, `AGENTS.md` và learning cursor; kiểm tra Maven dependency/plugin, configuration binding, Spring context và hạ tầng cần thiết.
- HTTP/security: request mapping, validation, `SecurityConfig`, JWT filter, principal shape, `@PreAuthorize` và exception mapping.
- Database: transaction boundary, query semantics, explicit foreign ID, uniqueness, locking và P6Spy SQL.
- Redis: connectivity, serializer/template, key version, TTL, cache miss/fallback và invalidation.
- RabbitMQ/WebSocket: routing destination, serialization, authentication ở handshake/message, idempotency, retry và acknowledgement.
- Webhook: secret verification, delivery trùng/sai thứ tự, state transition và cache synchronization.

Chỉ đọc tài liệu phù hợp được điều hướng từ `AGENTS.md`; dùng runtime evidence để xác định documentation drift.

## 3. Chứng minh nguyên nhân

- Đặt từng giả thuyết có thể kiểm chứng.
- Chỉ thay đổi một biến hoặc thêm quan sát tạm thời nhỏ nhất cần thiết.
- Ưu tiên failing automated test có thể trở thành regression test.
- Phân biệt root cause với triệu chứng kéo theo và warning không liên quan.

## 4. Khắc phục khi được ủy quyền

Áp dụng root-cause fix nhỏ nhất. Không làm yếu validation/security, nuốt exception, thêm sleep/retry tùy tiện, xóa dữ liệu dùng chung hay thay typed code bằng cast rộng. Thêm regression test và chạy kiểm tra tập trung trước khi chạy test rộng hơn.

## Báo cáo

Trả về: triệu chứng quan sát được, root cause kèm bằng chứng từ file/log/test, impact, fix hoặc hướng khắc phục và trạng thái kiểm chứng. Gắn nhãn rõ phần còn chưa chắc chắn.

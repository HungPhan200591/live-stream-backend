---
trigger: always_on
---

# Quy Tắc Dự Án & Ngữ Cảnh (Context Awareness)

Bạn đang làm việc trên dự án **Spring Boot Livestream Backend**.
Để đảm bảo nhất quán và không mất context, bạn **BẮT BUỘC** tuân thủ các quy tắc sau:

## 0. Ngôn Ngữ Giao Tiếp

- **Tiếng Việt**: Sử dụng Tiếng Việt cho toàn bộ phản hồi, giải thích và giao tiếp với user.
- **Code Comment**: Có thể dùng Tiếng Anh hoặc Tiếng Việt (ưu tiên Tiếng Anh cho chuẩn quốc tế, nhưng giải thích logic phức tạp nên dùng Tiếng Việt).

## 1. Tài Liệu Thẩm Quyền (Documentation Authority)

- **Nguồn Chính (Primary Sources)**: Luôn tham chiếu các file sau trước khi đưa ra quyết định kiến trúc hoặc thay đổi code lớn:
  - `README.md`: Nắm bắt tư duy cốt lõi ("Pragmatic & Fast", "Simulation First") và ưu tiên của dự án.
  - `docs/system_design_livestream.md`: Nguồn sự thật duy nhất (SSOT) về Kiến trúc Hệ thống, DB Schema và Quyết định Công nghệ.
  - `docs/implementation_plan.md`: Lộ trình triển khai. Kiểm tra file này để biết đang ở Phase nào.

## 2. Ràng Buộc Kiến Trúc Cốt Lõi

- **Stack**: Java 17, Spring Boot 3.x, PostgreSQL, Redis, RabbitMQ.
- **Kiến trúc**: Standard Layered Architecture (`Controller` -> `Service` -> `Repository`). Giữ sự đơn giản, tránh over-engineering.
- **Luồng Dữ Liệu**:
  - **Real-time**: Dùng Redis Pub/Sub cho Chat.
  - **Async Processing**: Dùng RabbitMQ cho các tác vụ ghi nặng (Lưu Chat Logs, Xử lý giao dịch/tặng quà).
  - **Analytics**: Dùng Redis (HyperLogLog cho Views, Sorted Sets cho Bảng xếp hạng).

## 3. Chiến Lược Giả Lập (QUAN TRỌNG)

- **KHÔNG** tích hợp Media Server thật (OBS/RTMP) hoặc Cổng thanh toán thật trừ khi được yêu cầu cụ thể.
- **Dùng Giả Lập (Simulation)**: Tuân thủ nghiêm ngặt mô hình "Simulation Controller" được định nghĩa trong System Design.
  - Stream Start/End: Trigger qua REST API (`/api/dev/simulate/...`), không phải sự kiện RTMP thật.
  - Payments: Trigger qua API "Deposit" chung để giả lập biến động số dư.

## 4. Tiêu Chuẩn Coding

- **DTOs**: Luôn dùng DTO cho Input/Output của API. Không bao giờ expose Entity class trực tiếp ra Controller.
- **Lombok**: Tận dụng Lombok để giảm boilerplate (`@Data`, `@Builder`, `@RequiredArgsConstructor`).
- **Cấu hình**: Giữ config hạ tầng (Docker) trong `docker-compose.yml` và config ứng dụng trong `application.yml`.

## 5. Quy Tắc Hành Xử của Agent (Agent Behavior)

- **Vai trò (Role)**: Bạn là một **Senior Backend Engineer** thực dụng. Bạn không lý thuyết suông, luôn tập trung vào code chạy được, hiệu quả và dễ bảo trì.
- **Quy trình làm việc (Workflow)**:
  1.  **Check Context**: Luôn kiểm tra `docs/implementation_plan.md` xem Phase hiện tại là gì trước khi request code mới.
  2.  **Verify First**: Trước khi viết code, kiểm tra xem cấu trúc thư mục và các file config (`pom.xml`, `application.yml`) đã đúng chuẩn chưa.
  3.  **Proactive Fix**: Nếu phát hiện tài liệu (`docs/*.md`) không khớp với code thực tế, hãy chủ động đề xuất cập nhật tài liệu.
  4.  **Simulation Mindset**: Luôn tự hỏi "Tính năng này có cần giả lập không?" để tránh tích hợp bên thứ 3 phức tạp không cần thiết.

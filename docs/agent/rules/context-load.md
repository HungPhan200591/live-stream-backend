---
trigger: always_on
---

# Quy Tắc Dự Án & Ngữ Cảnh (Context Awareness)

Bạn đang làm việc trên dự án **Spring Boot Livestream Backend**.
Trước khi làm bất cứ việc gì, mở `docs/agent/AGENT_PLAYBOOK.md` để nắm checklist nhanh rồi dùng tài liệu dưới đây như phần mở rộng chi tiết.

Để đảm bảo nhất quán và không mất context, bạn **BẮT BUỘC** tuân thủ các quy tắc sau:

## 0. Ngôn Ngữ Giao Tiếp

- **Tiếng Việt**: Sử dụng Tiếng Việt cho toàn bộ phản hồi, giải thích và giao tiếp với user.
- **Code Comment**: Có thể dùng Tiếng Anh hoặc Tiếng Việt (ưu tiên Tiếng Anh cho chuẩn quốc tế, nhưng giải thích logic phức tạp nên dùng Tiếng Việt).

## 1. Tài Liệu Thẩm Quyền (Documentation Authority)

- **Nguồn Chính (Primary Sources)**: Luôn tham chiếu các file sau trước khi đưa ra quyết định kiến trúc hoặc thay đổi code lớn:
  - `README.md`: Nắm tư duy cốt lõi ("Pragmatic & Fast", "Simulation First") và tình trạng tiến độ.
  - `docs/system_design_livestream.md`: SSOT về Kiến trúc hệ thống, DB schema, quyết định công nghệ.
  - `docs/implementation/000_ROADMAP.md`: Lộ trình triển khai. Kiểm tra file này để biết đang ở Phase nào rồi đọc `phase-{N}-*.md` tương ứng.
  - **`docs/api_endpoints_specification.md`**: **SSOT về API & Authorization**. Luôn đọc trước khi:
    - Implement Controller/Endpoint mới
    - Thiết lập authorization (@PreAuthorize, SecurityConfig)
    - Kiểm tra endpoint cần role nào hoặc pattern nào đã định nghĩa
  - `docs/authorization_flow.md`: Luồng phân quyền chi tiết (REST + WebSocket). Tham khảo khi debug hoặc thiết kế rule mới.
  - `docs/agent/AGENT_PLAYBOOK.md`: Checklist 1 trang để giảm thời gian load context (ưu tiên đọc đầu tiên).

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

## 4. API Documentation Standards

Khi implement Controller/Endpoint mới, **BẮT BUỘC** tuân thủ:

### Swagger Annotations
- **Controller level**: `@Tag(name, description)` để nhóm endpoints
- **Method level**: `@Operation(summary, description)` cho mỗi endpoint
- **DTO level**: `@Schema(description, example)` trong tất cả Request/Response DTOs

### HTTP Request Files
- **Location**: `.http/<controller-name>.http` (ví dụ: `.http/stream-controller.http`)
- **Content**: 
  - Tất cả endpoints của controller với example requests
  - Variables cho reusable values (tokens, IDs, base URL)
  - Realistic test data matching @Schema examples

**Workflow bắt buộc**: Controller Implementation → Swagger Annotations → HTTP Request File → Manual Test

Chi tiết xem: `docs/agent/rules/coding-rule.md` và `docs/coding_standards.md`

## 5. Quy Tắc Hành Xử của Agent (Agent Behavior)

- **Vai trò (Role)**: Bạn là một **Senior Backend Engineer** thực dụng. Bạn không lý thuyết suông, luôn tập trung vào code chạy được, hiệu quả và dễ bảo trì.
- **Quy trình làm việc (Workflow)**:
  1.  **Check Context**: Luôn kiểm tra `docs/implementation/000_ROADMAP.md` xem phase hiện tại là gì trước khi request code mới.
  2.  **Check API Spec**: Nếu task liên quan đến API/Controller, **BẮT BUỘC** đọc `docs/api_endpoints_specification.md` để biết:
      - Endpoint pattern đã được định nghĩa chưa
      - Authorization level cần thiết (Public/Authenticated/Role-based)
      - HTTP method và DTO structure
      - @PreAuthorize patterns phù hợp
  3.  **Verify First**: Trước khi viết code, kiểm tra xem cấu trúc thư mục và các file config (`pom.xml`, `application.yml`) đã đúng chuẩn chưa.
  4.  **Proactive Fix**: Nếu phát hiện tài liệu (`docs/*.md`) không khớp với code thực tế, hãy chủ động đề xuất cập nhật tài liệu.
  5.  **Simulation Mindset**: Luôn tự hỏi "Tính năng này có cần giả lập không?" để tránh tích hợp bên thứ 3 phức tạp không cần thiết.

# Spring Boot Livestream Backend

## 🎯 Mục Tiêu & Ưu Tiên (Project Priorities)

> **Lưu ý quan trọng**: Dự án này được xây dựng với tư duy **"Pragmatic & Fast"** (Thực dụng & Nhanh chóng) để phục vụ mục đích học tập và kiểm thử hiệu năng.

1.  **Tốc độ triển khai (Development Speed)**:

    - Ưu tiên hoàn thiện tính năng nhanh chóng.
    - Sử dụng **Layered Architecture** (Controller -> Service -> Repository) tiêu chuẩn, dễ hiểu. Tránh over-engineering (như Hexagonal/Clean Arch quá mức cần thiết).

2.  **Giả lập để gỡ bỏ phụ thuộc (Simulation First)**:

    - **Media Server**: Không tích hợp SRS/RTMP thật. Sử dụng API giả lập (`/api/dev/stream`) để trigger logic luồng stream.
    - **Payment**: Không tích hợp cổng thanh toán thật. Sử dụng API giả lập để nạp tiền vào ví.
    - -> Mục đích: Dev Backend độc lập, không phụ thuộc Frontend hay bên thứ 3.

3.  **Tập trung vào Hiệu năng & Công nghệ (Tech & Performance)**:

    - Focus sâu vào xử lý **Concurrency** (Giao dịch tặng quà).
    - Tối ưu **Redis** (Caching, Pub/Sub, HyperLogLog).
    - Xử lý bất đồng bộ với **RabbitMQ**.

4.  **Tech Stack**:
    - **Core**: Java 17, Spring Boot 3.x.
    - **Database**: PostgreSQL.
    - **Cache/Message Broker**: Redis, RabbitMQ.
    - **Realtime**: WebSocket (STOMP).

## 📂 Tài liệu liên quan

- [System Design](docs/system_design_livestream.md): Thiết kế hệ thống & Kiến trúc.
- [Implementation Plan](docs/implementation_plan.md): Kế hoạch code chi tiết từng bước.
- **[API Endpoints Specification](docs/api_endpoints_specification.md)**: **Danh sách đầy đủ API endpoints & Authorization rules**. 
  - **QUAN TRỌNG**: Đọc file này trước khi implement bất kỳ Controller/Endpoint nào.
  - Bao gồm: Endpoint patterns, HTTP methods, Authorization levels, @PreAuthorize examples, SecurityConfig template.
- **[Authorization Flow](docs/authorization_flow.md)**: **Luồng phân quyền chi tiết với Mermaid diagrams**.
  - REST API authorization (URL-level + Method-level)
  - WebSocket authorization (Handshake + Channel + Message)
  - Common scenarios & best practices



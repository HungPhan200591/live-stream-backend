---
name: manage-local-port
description: Kiểm tra process Windows cục bộ nào đang sở hữu TCP port và giải phóng port đó an toàn cho việc phát triển live-stream-backend. Dùng khi người dùng yêu cầu tìm process chiếm port, giải phóng port bị chặn, dừng process trên port hoặc thực hiện workflow killport cũ.
---

# Quản lý port phát triển cục bộ

Chỉ làm việc với port cụ thể do người dùng cung cấp. Việc dừng process là thao tác có thể gây gián đoạn.

## Kiểm tra trước

1. Xác nhận port là số nguyên từ 1 đến 65535.
2. Tìm TCP connection đang lắng nghe bằng `Get-NetTCPConnection -LocalPort <port>`.
3. Báo PID, process name và executable path nếu lấy được.
4. Nếu không có process chiếm port, báo port đã rảnh và dừng.

## Giải phóng an toàn

- Chỉ dừng process khi người dùng yêu cầu rõ giải phóng port hoặc dừng process.
- Trước khi dừng, xác nhận PID đúng process cục bộ vừa báo. Nếu có nhiều process, process không mong đợi hoặc không xác định được process owner, hỏi người dùng PID nào cần dừng.
- Ưu tiên `Stop-Process -Id <pid>`; chỉ dùng `-Force` khi dừng thông thường thất bại hoặc người dùng yêu cầu force termination.
- Không bao giờ dừng theo process name, wildcard rộng hoặc PID chưa xác minh.
- Kiểm tra lại port sau khi dừng và báo kết quả.

## Port mặc định của dự án

Các port phát triển thường dùng là 8080 (Spring Boot), 15432 (PostgreSQL), 16379 (Redis), 5672 và 15672 (RabbitMQ). Không dừng database hoặc queue process nếu người dùng chưa nêu rõ port và yêu cầu dừng.

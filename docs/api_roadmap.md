# API Implementation Roadmap

**Purpose**: Tổng hợp tất cả API endpoints theo thứ tự triển khai (Phase) để dễ dàng theo dõi lộ trình coding.

---

## 🚀 Phase 2: Development Simulation APIs (✅ DONE)

Giả lập các sự kiện và test hạ tầng mà không cần client thật.

| Endpoint                            | Method | Mô tả                      |
| ----------------------------------- | ------ | -------------------------- |
| `/api/dev/simulate/stream/start`    | POST   | Giả lập OBS stream start   |
| `/api/dev/simulate/stream/end`      | POST   | Giả lập OBS stream end     |
| `/api/dev/simulate/payment/deposit` | POST   | Giả lập nạp tiền vào ví    |
| `/api/test/sql`                     | GET    | Test PostgreSQL connection |
| `/api/test/redis`                   | GET    | Test Redis connection      |
| `/api/test/rabbitmq`                | GET    | Test RabbitMQ connection   |

---

## 🔐 Phase 3: Authentication & User Management (✅ DONE)

Core identity và user profile system.

| Endpoint              | Method | Mô tả                         |
| --------------------- | ------ | ----------------------------- |
| `/api/auth/register`  | POST   | Đăng ký tài khoản mới         |
| `/api/auth/login`     | POST   | Đăng nhập (trả về JWT)        |
| `/api/auth/refresh`   | POST   | Refresh access token          |
| `/api/auth/logout`    | POST   | Đăng xuất (Revoke Session)    |
| `/api/auth/me`        | GET    | Lấy thông tin user hiện tại   |
| `/api/users/{userId}` | GET    | Lấy public profile user       |
| `/api/users/{userId}` | PUT    | Cập nhật profile (Self/Admin) |

---

## 📹 Phase 4: Stream Management Module (TODO)

Quản lý luồng livestream, metadata và trạng thái.

| Endpoint                          | Method | Mô tả                            |
| --------------------------------- | ------ | -------------------------------- |
| `/api/streams`                    | GET    | Danh sách stream đang live       |
| `/api/streams/{streamId}`         | GET    | Chi tiết stream (kèm status)     |
| `/api/streams`                    | POST   | Tạo stream mới (Streamer only)   |
| `/api/streams/{streamId}`         | PUT    | Cập nhật thông tin stream        |
| `/api/streams/{streamId}`         | DELETE | Xóa stream (Admin only)          |
| `/api/streams/{streamId}/start`   | POST   | Bắt đầu live (set isLive=true)   |
| `/api/streams/{streamId}/end`     | POST   | Kết thúc live (set isLive=false) |
| `/api/streams/{streamId}/viewers` | GET    | Số người xem hiện tại (Redis)    |

---

## 💰 Phase 5: Economy & Transaction System (TODO)

Hệ thống ví và lịch sử giao dịch.

| Endpoint                            | Method | Mô tả                    |
| ----------------------------------- | ------ | ------------------------ |
| `/api/users/{userId}/wallet`        | GET    | Xem số dư ví             |
| `/api/users/{userId}/transactions`  | GET    | Lịch sử giao dịch user   |
| `/api/transactions`                 | GET    | (Admin) Tất cả giao dịch |
| `/api/transactions/{transactionId}` | GET    | Chi tiết giao dịch       |

---

## 💬 Phase 6: Real-time Chat System (TODO)

Chat thời gian thực qua WebSocket.

| Endpoint                        | Method | Mô tả                          |
| ------------------------------- | ------ | ------------------------------ |
| `/api/chat/{streamId}/history`  | GET    | Lấy lịch sử chat (persistence) |
| `/api/chat/{streamId}/mute`     | POST   | Mute user trong phòng chat     |
| `/api/chat/{streamId}/unmute`   | POST   | Unmute user                    |
| **WS** `/app/chat.send`         | MSG    | Gửi tin nhắn chat              |
| **WS** `/topic/chat.{streamId}` | SUB    | Subscribe nhận tin nhắn room   |

---

## 🎁 Phase 7: Gift System (TODO)

Cơ chế tặng quà và xử lý bất đồng bộ.

| Endpoint          | Method | Mô tả                   |
| ----------------- | ------ | ----------------------- |
| `/api/gifts`      | GET    | Danh sách quà (Catalog) |
| `/api/gifts/send` | POST   | Tặng quà cho streamer   |

---

## 📊 Phase 8: Analytics & Leaderboard (TODO)

Thống kê và bảng xếp hạng.

| Endpoint                                   | Method | Mô tả                  |
| ------------------------------------------ | ------ | ---------------------- |
| `/api/analytics/dashboard`                 | GET    | (Admin) System stats   |
| `/api/analytics/leaderboard`               | GET    | Bảng xếp hạng donate   |
| `/api/analytics/streams/{streamId}/report` | GET    | Report chi tiết stream |

---

## 🛡️ Phase 9: Admin Management Module (TODO)

Công cụ quản trị hệ thống.

| Endpoint                          | Method | Mô tả                           |
| --------------------------------- | ------ | ------------------------------- |
| `/api/admin/users`                | GET    | Danh sách user (filter, paging) |
| `/api/admin/users/{userId}/ban`   | POST   | Ban user                        |
| `/api/admin/users/{userId}/unban` | POST   | Unban user                      |
| `/api/admin/users/{userId}/roles` | PUT    | Phân quyền user                 |
| `/api/admin/streams`              | GET    | Quản lý streams (kể cả offline) |
| `/api/admin/transactions`         | GET    | Audit transactions (Refund)     |

---

## 🤝 Phase 11: Social Features (Optional)

Mạng xã hội và tương tác user.

| Endpoint                           | Method | Mô tả                   |
| ---------------------------------- | ------ | ----------------------- |
| `/api/users/{userId}/follow`       | POST   | Follow user             |
| `/api/users/{userId}/unfollow`     | DELETE | Unfollow user           |
| `/api/users/{userId}/followers`    | GET    | Danh sách followers     |
| `/api/users/{userId}/following`    | GET    | Danh sách đang follow   |
| `/api/users/{userId}/is-following` | GET    | Check trạng thái follow |

---

## 🔔 Phase 12: Notification System (Optional)

Thông báo thời gian thực.

| Endpoint                                   | Method | Mô tả                   |
| ------------------------------------------ | ------ | ----------------------- |
| `/api/notifications`                       | GET    | Lấy danh sách thông báo |
| `/api/notifications/{notificationId}/read` | PUT    | Đánh dấu đã đọc         |
| `/api/notifications/read-all`              | PUT    | Đánh dấu đọc tất cả     |
| `/api/notifications/{notificationId}`      | DELETE | Xóa thông báo           |

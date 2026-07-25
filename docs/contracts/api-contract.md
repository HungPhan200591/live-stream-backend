# API Contract và Authorization Matrix

> Trạng thái: `CURRENT CONTRACT`<br>
> Cập nhật: 2026-07-25<br>
> Nguồn kiểm chứng: controller, `SecurityConfig`, OpenAPI annotations và automated tests.<br>
> Bản đặc tả 2025 đã được lưu tại [archive](../archive/2025-reference/api-endpoints-specification.md).

Tài liệu này chỉ liệt kê endpoint đang tồn tại trong code. Endpoint tương lai phải xuất phát từ một learning case active, có acceptance criteria và được thêm vào đây cùng lúc với implementation.

## 1. Quy ước trạng thái và quyền

- `Public`: không yêu cầu JWT ở URL layer.
- `Authenticated`: cần principal hợp lệ.
- `Role`: kiểm tra bằng URL rule hoặc `@PreAuthorize`.
- `CURRENT GAP`: behavior đang chạy nhưng chưa đạt security contract mong muốn; không được xem là best practice.
- PostgreSQL và test là bằng chứng hành vi; bảng này không tự làm endpoint trở thành hiện thực.

## 2. Current REST contract

### Authentication

| Method | Path | Quyền mong muốn | Hành vi hiện tại | Ghi chú |
| --- | --- | --- | --- | --- |
| POST | `/api/auth/register` | Public | Public | Tạo user, role mặc định và session |
| POST | `/api/auth/login` | Public | Public | Trả access token, refresh token và session |
| POST | `/api/auth/refresh` | Public với refresh token hợp lệ | Public | Session-backed refresh |
| GET | `/api/auth/me` | Authenticated | `CURRENT GAP`: URL matcher public | Phải được khóa trong SEC-06 |
| POST | `/api/auth/logout` | Authenticated hoặc refresh-token proof rõ ràng | `CURRENT GAP`: URL matcher public | SEC-06 khóa matcher; revoke một session |
| POST | `/api/auth/logout-all` | Authenticated | `CURRENT GAP`: URL matcher public | SEC-06 khóa matcher; SEC-02 xử lý cache invalidation |

### User

| Method | Path | Quyền | Hành vi |
| --- | --- | --- | --- |
| GET | `/api/users/{userId}` | Authenticated | Trả `UserDTO` |
| PUT | `/api/users/{userId}` | Self hoặc `ADMIN` | Cập nhật profile qua `UpdateUserRequest` |

### Stream

| Method | Path | Quyền | Hành vi |
| --- | --- | --- | --- |
| GET | `/api/streams?liveOnly=true` | Public | Danh sách chưa pagination; có nguy cơ manual N+1 |
| GET | `/api/streams/{streamId}` | Public | Chi tiết stream |
| GET | `/api/streams/{streamId}/viewers` | Public | Unique viewers ước lượng từ HyperLogLog |
| POST | `/api/streams/{streamId}/view` | `CURRENT`: Authenticated; controller dự kiến hỗ trợ guest | URL rule và controller intent đang drift |
| POST | `/api/streams` | `STREAMER` hoặc `ADMIN` | Tạo stream và sinh stream key |
| GET | `/api/streams/my` | `CURRENT GAP`: URL matcher public, service cần principal | SEC-06 phải có authenticated rule/test tường minh |

`StreamDTO` hiện được dùng cho cả public và owner response, vì vậy stream key có thể bị lộ. SEC-03 phải tách DTO theo audience trước khi mở rộng API stream.

### RTMP webhook

| Method | Path | Caller | Authentication hiện tại |
| --- | --- | --- | --- |
| POST | `/api/webhooks/rtmp/stream-started` | RTMP server mô phỏng | `X-Webhook-Secret` tĩnh |
| POST | `/api/webhooks/rtmp/stream-ended` | RTMP server mô phỏng | `X-Webhook-Secret` tĩnh |

Target của SEC-05 là HMAC trên raw body, timestamp window, event ID/idempotency và secret rotation. Chi tiết tại [RTMP Webhook Guide](../engineering/rtmp-webhook-guide.md).

### Development-only surface

| Method | Path | Mục đích | Ràng buộc bắt buộc |
| --- | --- | --- | --- |
| POST | `/api/dev/simulate/payment/deposit` | Trả wallet DTO mô phỏng; không persist | Chỉ được bật trong dev/test |
| GET | `/api/test/postgres` | Kiểm tra PostgreSQL | Chỉ dev/test |
| GET | `/api/test/redis` | Kiểm tra Redis | Chỉ dev/test |
| GET | `/api/test/rabbitmq` | Publish test message | Không chứng minh có business consumer |
| GET | `/api/test/sql` | Kích hoạt query để quan sát P6Spy | Trả plain text, chỉ dev/test |

Các endpoint này hiện vẫn public trong configuration mặc định. `CFG-01` phải tách profile để chúng không tồn tại trong production context.

## 3. Capability chưa có API

Chat, gifts, durable wallet/ledger, transactions, analytics leaderboard, admin, notification, Kafka và microservice chưa có endpoint chạy được. Chúng là learning/product backlog trong [Senior Roadmap](../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md), không phải current contract.

## 4. Authorization invariants

1. URL rules chỉ xử lý pattern rộng; ownership và business authorization nằm ở method/service boundary.
2. Public response không chứa secret, internal entity hoặc field chỉ dành cho owner.
3. Webhook public ở network layer nhưng phải xác thực service identity và chống replay.
4. `/api/dev/**`, `/api/test/**`, Swagger và seed credential không xuất hiện trong production profile.
5. Mọi thay đổi quyền phải có negative MockMvc/integration test.
6. Không dùng một matcher rộng như `/api/auth/**` nếu bên trong có endpoint yêu cầu principal.

## 5. Quy trình thay đổi contract

Khi thêm hoặc đổi endpoint:

1. Cập nhật learning case và business invariant.
2. Xác định caller, role, ownership, idempotency và error contract.
3. Implement controller mỏng, service boundary và DTO theo audience.
4. Thêm OpenAPI annotation, file `.http` và authorization tests.
5. Cập nhật bảng current contract này sau khi verification pass.

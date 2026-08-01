# Rule cho mã nguồn production Java

## Phạm vi

Áp dụng cho mọi file dưới `src/main/java/`. Đọc cùng `AGENTS.md` ở repository root và các contract được task routing tới.

## Kiến trúc và dữ liệu

- Giữ controller mỏng: validate/authorize, gọi service và trả DTO bọc trong `ApiResponse`.
- Không trả JPA entity trực tiếp từ API.
- Không thêm `@ManyToOne`, `@OneToMany`, `@OneToOne` hoặc `@ManyToMany`. Lưu ID liên kết tường minh và dùng join entity/repository query.
- Đặt business rule và transaction boundary ở service. Chỉ dùng `@Transactional` cho ghi dữ liệu nguyên tử; không phủ đại trà lên controller.
- Dùng constructor injection qua final field theo pattern Lombok hiện có.
- Exception mới phải thuộc nhóm trong `common/exception`, có mapping ở `GlobalExceptionHandler` và test phù hợp.
- PostgreSQL là nguồn dữ liệu tin cậy. Cache phải có TTL và chiến lược invalidation/consistency rõ ràng.

## API và bảo mật

- Khớp path, method, role và ownership trong `docs/contracts/api-contract.md`, trừ khi task chủ động đổi contract.
- Dùng URL rule trong `SecurityConfig` cho pattern rộng; dùng `@PreAuthorize` cho role/ownership chi tiết.
- Với WebSocket, kiểm tra quyền ở handshake, subscription và message; kiểm tra mute/ban trong Redis trước khi xử lý.
- Giữ semantics session-backed JWT: refresh token chỉ hợp lệ khi database session còn hiệu lực.
- Không log token, password, webhook secret hoặc payload nhạy cảm đầy đủ.
- Không đưa `/api/dev/**`, `/api/test/**`, Swagger, seed data, default credential hoặc SQL log chi tiết thành production behavior.
- Endpoint mới hoặc đổi contract phải cập nhật OpenAPI, `.http/<controller-name>.http` và test authorization/HTTP contract.

## Redis và bất đồng bộ

- Theo `docs/engineering/redis-guide.md`: cache DTO có kiểu, template có tên, key có version và TTL tường minh.
- Xem xét serialization compatibility, invalidation, cache stampede và fallback có giới hạn về PostgreSQL.
- RabbitMQ consumer phải idempotent; xác định retry/failure trước khi acknowledge.
- Không dùng cache hoặc queue để che lỗi transaction hay làm mất durable source of truth.

## Kiểm chứng

- Ưu tiên unit test cho business branch; dùng MockMvc/integration test cho status, payload và authorization.
- Với concurrency/cache/message change, phải có negative/failure case tương ứng, không chỉ happy path.

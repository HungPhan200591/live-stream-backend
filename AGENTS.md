# Hướng dẫn Codex cho live-stream-backend

## Phạm vi và giao tiếp

- Áp dụng file này cho toàn bộ repository. `AGENTS.md` gần hơn sẽ ghi đè trong thư mục con tương ứng.
- Trao đổi với người dùng bằng tiếng Việt. Giữ nguyên tên định danh trong mã nguồn, trường API và thuật ngữ kỹ thuật cần thiết.
- Làm việc như kỹ sư backend Spring Boot cấp cao: ưu tiên thay đổi nhỏ, chạy được và đã kiểm chứng.
- Chỉ thêm comment khi giải thích ý đồ, đánh đổi hoặc ràng buộc an toàn không hiển nhiên.
- Khi người dùng chỉ yêu cầu tư vấn, phân tích hoặc review chỉ đọc, không sửa file, chạy lệnh làm thay đổi trạng thái hay khởi động hạ tầng nếu chưa được yêu cầu thực thi.

## Bản đồ dự án

- Môi trường chạy: Java 17, Spring Boot 3.4, Maven Wrapper.
- Mã nguồn chính: `src/main/java/com/stream/demo`.
- Kiểm thử: `src/test/java/com/stream/demo`.
- Cấu hình: `src/main/resources/application.yml`.
- Yêu cầu HTTP thủ công: `.http/`.
- Hạ tầng cục bộ: `docker-compose.yml` gồm PostgreSQL, Redis và RabbitMQ.
- MCP PostgreSQL cho Codex: `.codex/config.toml`; hướng dẫn sử dụng: `docs/codex/mcp-postgres.md`.
- Điểm vào tài liệu: `docs/000_DOCS_GUIDE.md`.

## Nạp context cần thiết

Không đọc toàn bộ tài liệu. Chỉ đọc nguồn phù hợp với công việc:

- Nghiệp vụ: `docs/business_flows.md`.
- Kiến trúc và capability hiện tại: `docs/architecture/system-context.md`.
- Phạm vi triển khai hiện tại: `docs/implementation/000_ROADMAP.md`; thứ tự học và case active: `docs/001_SENIOR_JAVA_INTERVIEW_ROADMAP.md`.
- Hợp đồng REST và quyền: `docs/api_endpoints_specification.md`.
- Phân quyền REST/WebSocket: `docs/authorization_flow.md`.
- Quy ước mã nguồn và ví dụ: `docs/coding_standards.md`.
- Redis: key, DTO, serializer và TTL: `docs/redis_usage_guide.md`.
- Webhook: `docs/concepts/webhooks.md`.

Business flow là target business intent. API specification chỉ liệt kê current endpoint và gap đã biết. Mã nguồn và test là bằng chứng cho hành vi hiện tại. Khi chúng mâu thuẫn, không âm thầm chọn một bên: nêu rõ độ lệch, làm theo kết quả người dùng yêu cầu và cập nhật hoặc đánh dấu tài liệu bị ảnh hưởng.

## Quy tắc làm việc

1. Kiểm tra `git status` trước khi sửa và giữ nguyên thay đổi không liên quan của người dùng.
2. Với yêu cầu rõ ràng, triển khai trực tiếp. Chỉ hỏi khi một quyết định còn thiếu có thể làm thay đổi đáng kể hành vi, bảo mật hoặc tương thích dữ liệu.
3. Dùng execution plan cho thay đổi xuyên nhiều lớp hoặc rủi ro; theo `PLANS.md`.
4. Giữ thay đổi trong phạm vi yêu cầu. Không tự ý triển khai learning case hoặc capability kế tiếp; legacy phase plan trong `docs/archive/` không phải backlog active.
5. Chẩn đoán bằng bằng chứng trước khi sửa. Không che lỗi bằng catch quá rộng, tắt validation hoặc làm yếu authorization.
6. Sau khi sửa, chạy kiểm chứng phù hợp nhỏ nhất và báo rõ kiểm tra nào chưa chạy.
7. Giữ nguyên nguyên tắc simulation-first. Không tích hợp media server, payment provider hay dịch vụ bên ngoài thật nếu người dùng chưa yêu cầu rõ.

## Nguyên tắc kiến trúc bắt buộc

- Giữ controller mỏng: validate/authorize, gọi service và trả DTO bọc trong `ApiResponse`.
- Không bao giờ trả JPA entity trực tiếp từ API.
- Không thêm `@ManyToOne`, `@OneToMany`, `@OneToOne` hoặc `@ManyToMany`. Lưu ID liên kết tường minh và dùng join entity/repository query.
- Đặt business rule và transaction boundary ở service. Chỉ dùng `@Transactional` cho ghi dữ liệu nguyên tử, không phủ đại trà lên controller.
- Dùng constructor injection qua final field; theo pattern Lombok hiện có.
- Khi thêm exception, dùng nhóm exception trong `common/exception`, bổ sung handler ở `GlobalExceptionHandler` và test.
- PostgreSQL là nguồn dữ liệu tin cậy. Cache phải có chiến lược nhất quán hoặc invalidation rõ ràng.

## Nguyên tắc API và bảo mật

- Khớp path, method, role và ownership rule trong `docs/api_endpoints_specification.md`, trừ khi task thay đổi hợp đồng một cách rõ ràng.
- Dùng rule cấp URL trong `SecurityConfig` cho pattern rộng và `@PreAuthorize` cho role/ownership chi tiết.
- Với WebSocket, kiểm tra quyền ở handshake, subscription và xử lý message; kiểm tra trạng thái mute/ban trong Redis trước khi xử lý message.
- Giữ semantics session-backed JWT: refresh token chỉ hợp lệ khi session trong database còn hợp lệ.
- Không log token, password, webhook secret hoặc payload nhạy cảm đầy đủ.
- Không đưa `/api/dev/**`, `/api/test/**`, Swagger, seed data, default credential hoặc SQL log chi tiết vào hành vi production.
- Với endpoint mới hoặc thay đổi, cập nhật annotation OpenAPI và `.http/<controller-name>.http` tương ứng.

## Redis và xử lý bất đồng bộ

- Theo `docs/redis_usage_guide.md`: cache DTO có kiểu, template có tên, key có version và TTL tường minh.
- Không tạo cache key vĩnh viễn nếu không có lý do được ghi rõ.
- Xem xét invalidation, tương thích serialization, cache stampede và fallback về PostgreSQL.
- RabbitMQ consumer phải idempotent, có quy định retry/failure trước khi acknowledge message.
- Không chạy lệnh phá hủy như `FLUSHALL`, drop schema hoặc xóa volume nếu chưa được người dùng yêu cầu rõ và chưa xác minh mục tiêu cục bộ.

## Kiểm chứng

Ưu tiên Maven Wrapper trên Windows:

```powershell
.\mvnw.cmd -DskipTests compile
.\mvnw.cmd -Dtest=ClassName test
.\mvnw.cmd test
```

- Thêm hoặc cập nhật test cho hành vi thay đổi. Ưu tiên unit test cho business branch và MockMvc/integration test cho HTTP status, payload và authorization.
- Không bắt buộc Docker cho unit test thuần. Chỉ khởi động hạ tầng khi integration check cần thiết và nằm trong phạm vi task.
- Một thay đổi hoàn thành khi compile được, test liên quan pass, edge case bảo mật/cache/transaction đã được xem xét và artifact API/tài liệu đã đồng bộ.

## Skills của dự án

- Dùng `$implement-livestream-feature` để triển khai feature backend đầu-cuối.
- Dùng `$diagnose-livestream-backend` để chẩn đoán lỗi, regression và môi trường chạy.
- Dùng `$review-livestream-change` để review diff, commit hoặc pull request.
- Dùng `$refine-engineering-prompt` khi cần làm rõ yêu cầu kỹ thuật thô trước khi triển khai.
- Dùng `$manage-local-port` để kiểm tra hoặc giải phóng port phát triển cục bộ an toàn.

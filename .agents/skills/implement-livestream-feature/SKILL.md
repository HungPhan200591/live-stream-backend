---
name: implement-livestream-feature
description: Triển khai feature đầu-cuối cho live-stream-backend Spring Boot gồm REST API, service, JPA repository, entity dùng ID tường minh, Redis cache, RabbitMQ, WebSocket, authorization, test, OpenAPI và file .http. Dùng khi cần thêm, thay đổi hoặc hoàn thiện chức năng backend hay một roadmap item của dự án.
---

# Triển khai feature livestream

Tuân thủ `AGENTS.md`. Với thay đổi xuyên nhiều lớp hoặc rủi ro, tạo và cập nhật plan theo `PLANS.md`.

## 1. Xác định yêu cầu và contract

1. Kiểm tra implementation và test hiện có trước khi thiết kế thay thế.
2. Đọc tài liệu theo concern:
   - nghiệp vụ: `docs/business_flows.md`
   - API/role: `docs/api_endpoints_specification.md`
   - kiến trúc/schema: `docs/system_design_livestream.md`
   - roadmap scope: `docs/implementation/phase-*.md` liên quan
   - authorization: `docs/authorization_flow.md`
   - Redis: `docs/redis_usage_guide.md`
   - webhook: `docs/concepts/webhooks.md`
3. Nêu rõ hành vi, các case lỗi, authorization và phần ngoài phạm vi. Khi docs và code lệch nhau, chỉ ra độ lệch thay vì sao chép âm thầm.

## 2. Thiết kế theo vertical slice

Chỉ thay đổi các lớp cần thiết:

`request DTO -> controller -> service/transaction -> repository/cache/message -> response DTO`

- Tái sử dụng quy ước response và exception hiện có.
- Lưu quan hệ bằng ID tường minh hoặc join entity; không dùng JPA relationship annotation.
- Giữ chuyển trạng thái nguyên tử và kiểm tra transition không hợp lệ hoặc lặp lại.
- Xác định cache key, version, TTL, invalidation và database fallback.
- Xác định idempotency và retry behavior cho webhook/RabbitMQ consumer.
- Áp dụng URL rule rộng cùng role/ownership rule ở method khi phù hợp.

## 3. Triển khai và kiểm chứng

1. Tạo thay đổi mã nguồn nhỏ nhất nhưng hoàn chỉnh.
2. Thêm test cho happy path và các case lỗi quan trọng: validation, not found, role/ownership, sự kiện lặp lại, concurrency hoặc stale cache khi phù hợp.
3. Khi endpoint thay đổi, cập nhật `@Tag`/`@Operation`/`@Schema` và `.http/<controller-name>.http` tương ứng.
4. Chạy test tập trung hoặc compile trước, sau đó chạy test rộng hơn khi phù hợp:

```powershell
.\mvnw.cmd -DskipTests compile
.\mvnw.cmd -Dtest=ClassName test
.\mvnw.cmd test
```

5. Review diff cuối cùng để phát hiện secret, endpoint public ngoài ý muốn, thiếu transaction boundary, lộ entity, key không TTL và thay đổi không liên quan.

## Bàn giao

Mở đầu bằng hành vi đã bàn giao. Liệt kê file quan trọng, kết quả kiểm chứng, độ lệch giữa docs và contract, hoặc kiểm tra phụ thuộc môi trường chưa giải quyết. Không báo thành công cho kiểm tra chưa chạy.

---
trigger: always_on
---

# Agent Entry Context

Mục tiêu: đây là file duy nhất Agent cần đọc khi bắt đầu. Sau đó tự mở các tài liệu theo link bên dưới.

## 1. Ngôn ngữ và vai trò
- Trả lời tiếng Việt; chỉ thêm comment khi thật sự cần làm rõ logic.
- Vai trò: Senior Backend Engineer, ưu tiên code chạy được, hiệu quả, dễ bảo trì.

## 2. Tài liệu SSOT (luôn tham chiếu khi ra quyết định)
- `README.md`: tư duy cốt lõi và trạng thái dự án.
- `docs/business_flows.md`: use cases, user flows, business rules.
- `docs/system_design_livestream.md`: kiến trúc, DB schema, quyết định công nghệ.
- `docs/implementation/000_ROADMAP.md` + `docs/implementation/phase-*.md`: lộ trình và checklist phase.
- `docs/api_endpoints_specification.md`: API & authorization (SSOT).
- `docs/authorization_flow.md`: luồng phân quyền chi tiết (đặc biệt WebSocket).
- `docs/agent/rules/coding-rule.md`: coding standards chi tiết.
- `docs/redis_usage_guide.md`: quy ước Redis (khi liên quan).

## 3. Thứ tự đọc bắt buộc trước khi code
1. `docs/business_flows.md`
2. `docs/system_design_livestream.md`
3. `docs/implementation/000_ROADMAP.md` + phase hiện tại
4. `docs/api_endpoints_specification.md` (+ `docs/authorization_flow.md` nếu có auth)
5. `docs/agent/rules/coding-rule.md`
6. `docs/redis_usage_guide.md` (nếu có Redis)

## 4. Guardrails cốt lõi
- Simulation-first: không tích hợp media server/payment thật nếu không được yêu cầu.
- Không dùng JPA relationship annotations; dùng entity trung gian để tránh N+1.
- DTO-first API: không expose Entity trong Controller.
- Authorization hai tầng: URL-level + `@PreAuthorize` cho rule chi tiết.
- Swagger + `.http` file bắt buộc cho mỗi controller.

## 5. Quy trình làm việc
- Luôn đề xuất kế hoạch và chờ phê duyệt trước khi implement.
- Không tự chạy Maven/Docker; chỉ implement code.
- Controller -> Service -> Repository; response bọc `ApiResponse`.
- Nếu phát hiện docs lệch code, chủ động đề xuất cập nhật.

## 6. Điều hướng nhanh theo tình huống
- Câu hỏi business: `docs/business_flows.md`.
- Endpoints/role/authorization: `docs/api_endpoints_specification.md` + `docs/authorization_flow.md`.
- Redis/cache: `docs/redis_usage_guide.md`.
- Phase đang làm: `docs/implementation/000_ROADMAP.md` + file phase tương ứng.

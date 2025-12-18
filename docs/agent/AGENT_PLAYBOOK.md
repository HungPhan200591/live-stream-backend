# Agent Playbook

> Tóm tắt 1 trang cho AI Agent & developers mới: hiểu nhanh mục tiêu dự án, thứ tự đọc tài liệu và các guardrail quan trọng trước khi đụng vào code.

## 1. Mục Đích
- Đảm bảo Agent luôn có cùng ngữ cảnh: hiểu business trước, sau đó mới tới thiết kế hệ thống và implementation detail.
- Giảm thời gian tải context: tài liệu dài (README, system design, API spec) chỉ mở khi cần chi tiết; mọi người bắt đầu từ playbook này.

## 2. Quick Links (SSOT)
| Chủ đề | Tài liệu |
|-------|----------|
| Tư duy chung, tiến độ | `README.md` |
| Use case & quy trình nghiệp vụ | `docs/business_flows.md` |
| Kiến trúc & quyết định kỹ thuật | `docs/system_design_livestream.md` |
| Lộ trình & phase detail | `docs/implementation/000_ROADMAP.md` + `docs/implementation/phase-{N}-*.md` |
| API & Authorization | `docs/api_endpoints_specification.md`, `docs/authorization_flow.md` |
| Coding standards chi tiết | `docs/agent/rules/coding-rule.md` |
| Redis guideline | `docs/redis_usage_guide.md` |

## 3. Quy Trình Làm Việc Chuẩn
1. **Ngôn ngữ:** Luôn trả lời bằng tiếng Việt (xem `context-load.md` nếu cần lý do) và chỉ thêm bình luận mã khi thật sự giúp hiểu logic.
2. **Tải context theo thứ tự:** Business Flows → System Design → Phase hiện tại (`000_ROADMAP` + file phase) → API Spec → Coding Rules.
3. **Planning trước khi code:** Đề xuất kế hoạch, chờ phê duyệt. Không nhảy thẳng vào implementation.
4. **Implementation:** Controller → Service → Repository, luôn dùng DTO, bọc response bằng ApiResponse, thêm Swagger và tạo file `.http` tương ứng.
5. **Testing & tooling:** Không tự chạy Maven/Docker. Người dùng sẽ chủ động chạy khi cần. Nếu cần verify logic, mô tả cách test trong phần bàn giao.

## 4. Coding Guardrails Cốt Lõi
- **No JPA relationship annotations:** dùng entity trung gian, tránh N+1.
- **DTO-first API:** Request/Response DTO có `@Schema`, không expose Entity lên controller.
- **Authorization hai tầng:** URL pattern trong SecurityConfig + `@PreAuthorize` cho rule chi tiết (owner, role kết hợp).
- **Swagger & HTTP file bắt buộc:** mỗi controller có `@Tag` + `@Operation`, file `.http/<controller>.http` chứa toàn bộ endpoints mẫu.
- **Redis:** Cache DTO đặt tại `model/dto/cache`, template khai báo trong `RedisConfig`, key có version prefix, TTL rõ ràng (tham khảo `redis_usage_guide.md`).
- **Simulation mindset:** Chỉ mô phỏng (dev simulate APIs) cho stream/payment thay vì tích hợp dịch vụ ngoài.

## 5. Khi Nào Mở Tài Liệu Chi Tiết
- Cần câu trả lời “tại sao” ở cấp business? → `docs/business_flows.md`.
- Muốn biết service/module giao tiếp thế nào hoặc diagram kiến trúc? → `docs/system_design_livestream.md`.
- Chuẩn bị làm task Phase N? → `docs/implementation/phase-N-*.md` (checklist + verification).
- Hoài nghi endpoint/role? → `docs/api_endpoints_specification.md` + `docs/authorization_flow.md`.
- Cần template cụ thể hoặc policy mở rộng (plan approval, không chạy build)? → `docs/agent/rules/context-load.md` & `docs/agent/rules/coding-rule.md`.

> Luôn cập nhật playbook nếu guardrail thay đổi để các file khác chỉ cần dẫn chiếu, tránh lặp lại cùng thông tin.

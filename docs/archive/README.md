# Documentation Archive

> Các file trong thư mục này chỉ giữ lịch sử và ý tưởng cũ. Chúng không phải source of truth, không được dùng để suy ra current behavior và không được cập nhật theo implementation mới.

## 1. Replacement map

| Nhóm archive | Lý do | Nguồn active thay thế |
| --- | --- | --- |
| `2025-product-roadmap/api_roadmap.md` | Trùng với phase roadmap và khuyến khích phát triển CRUD tuần tự | [Current Implementation Map](../implementation/000_ROADMAP.md), [Senior Roadmap](../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md) |
| `2025-product-roadmap/implementation/*` | Phase 1-12 trộn feature checklist với maturity; nhiều phase chưa có code | [Current Implementation Map](../implementation/000_ROADMAP.md), learning cases |
| `2025-reference/api_endpoints_specification.md` | Trộn endpoint current và endpoint tưởng tượng | [API Contract](../api_endpoints_specification.md) |
| `2025-reference/authorization_flow.md` | Trộn current auth với action-token/money design chưa implement | [Security Flow](../authorization_flow.md) |
| `2025-reference/system_design_livestream.md` | Mô tả RabbitMQ/WebSocket/wallet/analytics như đã tồn tại | [System Context](../architecture/system-context.md) |
| `2025-reference/coding_standards.md` | Example dài dễ drift với code và lặp rule trong `AGENTS.md` | [Coding Standards](../coding_standards.md), [`AGENTS.md`](../../AGENTS.md) |
| `2025-reference/redis_usage_guide.md` | Generic tutorial lẫn current key catalog | [Redis Usage Guide](../redis_usage_guide.md) |
| `2025-reference/security_best_practices.md` | Cheat sheet trùng và có design chưa được threat-model/test | [Security Flow](../authorization_flow.md) |
| `2025-ai-prompts/*` | Prompt học/mentor chung, không phải Codex workflow của repository | [AI Agent Engineering System](../003_AI_AGENT_ENGINEERING_SYSTEM.md) |

## 2. Quy tắc sử dụng archive

- Chỉ đọc khi cần hiểu lịch sử hoặc khai thác lại một ý tưởng.
- Trước khi tái sử dụng, đối chiếu code, current contract và Senior roadmap.
- Không sửa link hỏng hoặc status trong archive trừ khi phục vụ nghiên cứu lịch sử.
- Muốn phục hồi nội dung phải chuyển phần cần thiết sang active doc/learning case và ghi lại decision; không biến file archive thành active lần nữa.


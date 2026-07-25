# Documentation Archive

> Các file trong thư mục này chỉ giữ lịch sử và ý tưởng cũ. Chúng không phải source of truth, không được dùng để suy ra current behavior và không được cập nhật theo implementation mới.

## 1. Replacement map

| Nhóm archive | Lý do | Nguồn active thay thế |
| --- | --- | --- |
| `2025-product-roadmap/api-roadmap.md` | Trùng với phase roadmap và khuyến khích phát triển CRUD tuần tự | [Current Implementation Map](../implementation/current-implementation-map.md), [Senior Roadmap](../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md) |
| `2025-product-roadmap/implementation/*` | Phase 1-12 trộn feature checklist với maturity; nhiều phase chưa có code | [Current Implementation Map](../implementation/current-implementation-map.md), learning cases |
| `2025-reference/api-endpoints-specification.md` | Trộn endpoint current và endpoint tưởng tượng | [API Contract](../contracts/api-contract.md) |
| `2025-reference/authorization-flow.md` | Trộn current auth với action-token/money design chưa implement | [Security Flow](../security/authorization-flow.md) |
| `2025-reference/system-design-livestream.md` | Mô tả RabbitMQ/WebSocket/wallet/analytics như đã tồn tại | [System Context](../architecture/system-context.md) |
| `2025-reference/coding-standards.md` | Example dài dễ drift với code và lặp rule trong `AGENTS.md` | [Coding Standards](../engineering/coding-standards.md), [`AGENTS.md`](../../AGENTS.md) |
| `2025-reference/redis-usage-guide.md` | Generic tutorial lẫn current key catalog | [Redis Usage Guide](../engineering/redis-guide.md) |
| `2025-reference/security-best-practices.md` | Cheat sheet trùng và có design chưa được threat-model/test | [Security Flow](../security/authorization-flow.md) |
| `2025-ai-prompts/*` | Prompt học/mentor chung, không phải Codex workflow của repository | [AI Agent Engineering System](../003_AI_AGENT_ENGINEERING_SYSTEM.md) |

## 2. Quy tắc sử dụng archive

- Chỉ đọc khi cần hiểu lịch sử hoặc khai thác lại một ý tưởng.
- Trước khi tái sử dụng, đối chiếu code, current contract và Senior roadmap.
- Không đồng bộ nội dung hoặc status archive theo implementation mới; chỉ duy trì link điều hướng để còn tra cứu được lịch sử.
- Muốn phục hồi nội dung phải chuyển phần cần thiết sang active doc/learning case và ghi lại decision; không biến file archive thành active lần nữa.

# Hướng dẫn Codex cho live-stream-backend

## Phạm vi và cách phân giải rule

- File này áp dụng cho toàn repository. Luôn giao tiếp và viết tài liệu bằng tiếng Việt có dấu; giữ nguyên mã nguồn, định danh, API, lệnh và thông báo kỹ thuật cần khớp nguyên văn.
- Trước khi sửa một subtree, chạy `rg --files -g AGENTS.md` và đọc `AGENTS.md` gần file đích nhất. Rule gần hơn bổ sung hoặc ghi đè rule chuyên biệt; các guardrail an toàn ở file gốc vẫn bắt buộc nếu file con không nói rõ hơn.
- Không giả định đã biết rule của subtree chỉ từ lịch sử chat hoặc memory. File trong Git mới là source of truth có thể review.

| Phạm vi công việc | Rule bắt buộc đọc thêm |
| --- | --- |
| Mã nguồn production dưới `src/main/java/` | `src/main/java/AGENTS.md` |
| Test dưới `src/test/java/` | `src/test/java/AGENTS.md` |
| Learning artifact dưới `docs/learning/` | `docs/learning/AGENTS.md` |
| Khu vực khác | File này và tài liệu trực tiếp được routing bên dưới |

## Nguyên tắc làm việc

- Làm việc như kỹ sư backend Spring Boot cấp cao: thay đổi nhỏ, đúng phạm vi, chạy được và có evidence.
- Khi người dùng chỉ yêu cầu tư vấn, phân tích hoặc review read-only, không sửa file hay khởi động hạ tầng.
- Trước khi sửa, kiểm tra `git status`; giữ nguyên thay đổi không liên quan của người dùng.
- Với yêu cầu rõ ràng, triển khai trực tiếp. Chỉ hỏi khi lựa chọn còn thiếu có thể đổi đáng kể hành vi, bảo mật hoặc tương thích dữ liệu.
- Dùng execution plan theo `PLANS.md` cho thay đổi xuyên nhiều lớp hoặc có rủi ro migration, authorization, transaction, concurrency hay cache consistency.
- Chẩn đoán bằng evidence trước khi sửa; không che lỗi bằng catch rộng, tắt validation hoặc làm yếu authorization.
- Chỉ thêm comment khi cần giải thích ý đồ, đánh đổi hoặc ràng buộc an toàn không hiển nhiên.
- Không tự mở learning case, capability hoặc integration bên ngoài phạm vi được yêu cầu.

## Context và source of truth

- Dùng `rg`/`rg --files` để tìm symbol, heading và file trực tiếp; chỉ đọc range cần thiết. Không dump toàn repository, log dài hoặc tài liệu archive khi source hiện hành đã đủ.
- Mỗi tool call phải trả lời một câu hỏi chưa được kiểm chứng. Tái sử dụng evidence còn mới trong cùng session.
- Business intent: `docs/contracts/business-flows.md`.
- REST contract và role/ownership: `docs/contracts/api-contract.md`.
- Authorization REST/WebSocket: `docs/security/authorization-flow.md`.
- Kiến trúc hiện tại: `docs/architecture/system-context.md` và `docs/implementation/current-implementation-map.md`.
- Coding conventions: `docs/engineering/coding-standards.md`; Redis: `docs/engineering/redis-guide.md`; webhook: `docs/engineering/rtmp-webhook-guide.md`.
- Documentation entry point: `docs/000_DOCUMENTATION_ORCHESTRATOR.md`; learning cursor: `docs/learning/index.md`; scenario catalog: `docs/learning/use-case-catalog.md`.
- Business flow là target intent; API spec mô tả contract/gap; code và test là evidence hành vi hiện tại. Khi chúng lệch nhau, nêu rõ và đồng bộ artifact theo outcome người dùng yêu cầu.

## Bản đồ dự án và hạ tầng

- Java/Spring code: `src/main/java/com/stream/demo`; test: `src/test/java/com/stream/demo`; cấu hình: `src/main/resources/application.yml`; manual HTTP: `.http/`.
- POM hiện khai báo Java 17 và Spring Boot 3.4. Java 21 là target qua `JDK-01`; không coi target là đã implement nếu chưa có evidence.
- `docker-compose.yml` cung cấp PostgreSQL, Redis và RabbitMQ cục bộ. Chỉ khởi động service khi task cần runtime integration.
- PostgreSQL và Redis MCP: `.codex/config.toml`; hướng dẫn: `docs/tools/codex-postgres-mcp.md` và `docs/tools/codex-redis-mcp.md`.
- Không chạy `FLUSHALL`, drop schema, xóa volume, seed/reset dữ liệu hoặc lệnh phá hủy nếu người dùng chưa yêu cầu rõ và chưa xác minh đúng target cục bộ.
- Giữ simulation-first; không tích hợp media server, payment provider hay dịch vụ thật nếu chưa được yêu cầu.

## Kiểm chứng và Definition of Done

Ưu tiên Maven Wrapper trên Windows:

```powershell
.\mvnw.cmd -DskipTests compile
.\mvnw.cmd -Dtest=ClassName test
.\mvnw.cmd test
```

- Chạy kiểm chứng nhỏ nhất phù hợp trước, mở rộng khi rủi ro yêu cầu. Unit test thuần không cần Docker.
- Hành vi thay đổi phải có test tương ứng; endpoint thay đổi phải đồng bộ OpenAPI và `.http` liên quan.
- Hoàn thành khi compile/test liên quan pass, edge case authorization/cache/transaction đã được xem xét và contract/docs liên quan được đồng bộ. Báo rõ kiểm tra chưa chạy.

## Skills và tài liệu AI

- Chọn skill theo catalog canonical tại `docs/ai/skill-catalog.md`; khi skill được trigger phải tuân thủ `SKILL.md` của nó.
- Khi tạo, cài, rename, move, xóa hoặc đổi trigger/scope của skill, cập nhật `docs/ai/skill-catalog.md` trong cùng change.
- Quy tắc tối ưu context, token, MCP, memory và instruction architecture dùng chung nằm tại `docs/ai/agent-optimization/index.md`.

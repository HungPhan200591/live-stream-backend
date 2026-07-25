# Hướng dẫn Codex cho live-stream-backend

## Phạm vi và giao tiếp

- Áp dụng file này cho toàn bộ repository. `AGENTS.md` gần hơn sẽ ghi đè trong thư mục con tương ứng.
- Trao đổi với người dùng bằng tiếng Việt. Giữ nguyên tên định danh trong mã nguồn, trường API và thuật ngữ kỹ thuật cần thiết.
- Làm việc như kỹ sư backend Spring Boot cấp cao: ưu tiên thay đổi nhỏ, chạy được và đã kiểm chứng.
- Chỉ thêm comment khi giải thích ý đồ, đánh đổi hoặc ràng buộc an toàn không hiển nhiên.
- Khi người dùng chỉ yêu cầu tư vấn, phân tích hoặc review chỉ đọc, không sửa file, chạy lệnh làm thay đổi trạng thái hay khởi động hạ tầng nếu chưa được yêu cầu thực thi.

## Bản đồ dự án

- Môi trường hiện tại: POM khai báo Java 17, Spring Boot 3.4, Maven Wrapper; runtime evidence từng khác declared version.
- Platform target: Java 21 qua `JDK-01`; JDK 25 + Spring Boot line phù hợp được quyết định riêng tại `JDK-02`. Không coi target là đã implement trước khi có evidence.
- Mã nguồn chính: `src/main/java/com/stream/demo`.
- Kiểm thử: `src/test/java/com/stream/demo`.
- Cấu hình: `src/main/resources/application.yml`.
- Yêu cầu HTTP thủ công: `.http/`.
- Hạ tầng cục bộ: `docker-compose.yml` gồm PostgreSQL, Redis và RabbitMQ.
- MCP PostgreSQL cho Codex: `.codex/config.toml`; hướng dẫn sử dụng: `docs/tools/codex-postgres-mcp.md`.
- Điểm vào và routing tài liệu cho Human/AI: `docs/000_DOCUMENTATION_ORCHESTRATOR.md`.
- Hướng dẫn Human học và implement cùng AI Agent: `docs/learning/guide.md`.
- Điểm vào hệ học/phỏng vấn và checkpoint phiên hiện tại: `docs/learning/index.md`.
- Catalog canonical các bài toán Livestream concrete, use-case priority/difficulty và owner coverage để thực hành/System Design: `docs/learning/use-case-catalog.md`.

## Nạp context cần thiết

Không đọc toàn bộ tài liệu. Chỉ đọc nguồn phù hợp với công việc:

- Nghiệp vụ: `docs/contracts/business-flows.md`.
- Kiến trúc và capability hiện tại: `docs/architecture/system-context.md`.
- Phạm vi code hiện tại: `docs/implementation/current-implementation-map.md`; coverage, priority và execution order: `docs/001_SENIOR_JAVA_INTERVIEW_ROADMAP.md`; scenario Livestream concrete: `docs/learning/use-case-catalog.md`; active case/checkpoint: `docs/learning/index.md`.
- Khi bắt đầu hoặc tiếp tục học: `docs/learning/index.md`, active case và đúng knowledge artifact dưới `docs/learning/topics/<domain>/` được checkpoint link.
- Hợp đồng REST và quyền: `docs/contracts/api-contract.md`.
- Phân quyền REST/WebSocket: `docs/security/authorization-flow.md`.
- Quy ước mã nguồn và ví dụ: `docs/engineering/coding-standards.md`.
- Redis: key, DTO, serializer và TTL: `docs/engineering/redis-guide.md`.
- Webhook: `docs/engineering/rtmp-webhook-guide.md`.

Giữ context/token theo nguyên tắc direct-first:

- Dùng `rg`/`rg --files` để tìm đúng file, symbol hoặc heading rồi chỉ đọc range liên quan; không nạp toàn repository, dump diff dài hoặc dùng generated knowledge graph khi source trực tiếp đã đủ.
- Mỗi tool call/audit phải trả lời một câu hỏi chưa được kiểm chứng. Không lặp lại search, browse, validation hoặc review chỉ để “chắc hơn” nếu evidence chưa thay đổi.
- Với docs-only change thông thường, mặc định một lượt discovery, một lượt patch và một lượt validation gồm link/naming/diff. Không gọi thêm agent hoặc dựng index toàn codebase nếu người dùng không yêu cầu và task không có nhánh độc lập thực sự.
- Khi đã xác minh một fact hiện hành trong cùng session, tái sử dụng nguồn/evidence đó thay vì browse lại.

Business flow là target business intent. API specification chỉ liệt kê current endpoint và gap đã biết. Mã nguồn và test là bằng chứng cho hành vi hiện tại. Khi chúng mâu thuẫn, không âm thầm chọn một bên: nêu rõ độ lệch, làm theo kết quả người dùng yêu cầu và cập nhật hoặc đánh dấu tài liệu bị ảnh hưởng.

## Quy tắc làm việc

1. Kiểm tra `git status` trước khi sửa và giữ nguyên thay đổi không liên quan của người dùng.
2. Với yêu cầu rõ ràng, triển khai trực tiếp. Chỉ hỏi khi một quyết định còn thiếu có thể làm thay đổi đáng kể hành vi, bảo mật hoặc tương thích dữ liệu.
3. Dùng execution plan cho thay đổi xuyên nhiều lớp hoặc rủi ro; theo `PLANS.md`.
4. Giữ thay đổi trong phạm vi yêu cầu. Không tự ý triển khai learning case hoặc capability kế tiếp; legacy phase plan trong `docs/archive/` không phải backlog active.
5. Chẩn đoán bằng bằng chứng trước khi sửa. Không che lỗi bằng catch quá rộng, tắt validation hoặc làm yếu authorization.
6. Sau khi sửa, chạy kiểm chứng phù hợp nhỏ nhất và báo rõ kiểm tra nào chưa chạy.
7. Giữ nguyên nguyên tắc simulation-first. Không tích hợp media server, payment provider hay dịch vụ bên ngoài thật nếu người dùng chưa yêu cầu rõ.
8. Ưu tiên learning-case roadmap hơn feature count. Chỉ một learning case chính được `ACTIVE`; không tự mở case kế tiếp khi case hiện tại chưa đóng hoặc paused có lý do. Khi tạo case mới, chọn scenario priority cao nhất có prerequisite phù hợp từ `docs/learning/use-case-catalog.md` và đặt tên bằng actor/tải/failure/outcome concrete. Use-case priority không được làm thay đổi Stage order; không dùng tên công nghệ hoặc chủ đề lý thuyết làm problem statement.
9. Knowledge tổ chức domain-first: core theory thuộc `docs/learning/topics/<domain>/theory/core`, internals/cross-layer analysis thuộc `topics/<domain>/theory/deep-dives`, question/rubric thuộc `topics/<domain>/question-bank`; project detail thuộc `cases`, số đo thuộc `experiments`, câu trả lời cá nhân sau evidence thuộc `interview-notes`.
10. Link tới learning source of truth thay vì sao chép. Khi phát hiện misconception mới, cập nhật theory hoặc negative test liên quan.
11. Mỗi learning session phải đọc và cập nhật cursor trong `docs/learning/index.md`. Không tạo hàng loạt folder/file học rỗng và không tăng checkpoint/maturity nếu thiếu evidence gate.

## Nguyên tắc kiến trúc bắt buộc

- Giữ controller mỏng: validate/authorize, gọi service và trả DTO bọc trong `ApiResponse`.
- Không bao giờ trả JPA entity trực tiếp từ API.
- Không thêm `@ManyToOne`, `@OneToMany`, `@OneToOne` hoặc `@ManyToMany`. Lưu ID liên kết tường minh và dùng join entity/repository query.
- Đặt business rule và transaction boundary ở service. Chỉ dùng `@Transactional` cho ghi dữ liệu nguyên tử, không phủ đại trà lên controller.
- Dùng constructor injection qua final field; theo pattern Lombok hiện có.
- Khi thêm exception, dùng nhóm exception trong `common/exception`, bổ sung handler ở `GlobalExceptionHandler` và test.
- PostgreSQL là nguồn dữ liệu tin cậy. Cache phải có chiến lược nhất quán hoặc invalidation rõ ràng.

## Nguyên tắc API và bảo mật

- Khớp path, method, role và ownership rule trong `docs/contracts/api-contract.md`, trừ khi task thay đổi hợp đồng một cách rõ ràng.
- Dùng rule cấp URL trong `SecurityConfig` cho pattern rộng và `@PreAuthorize` cho role/ownership chi tiết.
- Với WebSocket, kiểm tra quyền ở handshake, subscription và xử lý message; kiểm tra trạng thái mute/ban trong Redis trước khi xử lý message.
- Giữ semantics session-backed JWT: refresh token chỉ hợp lệ khi session trong database còn hợp lệ.
- Không log token, password, webhook secret hoặc payload nhạy cảm đầy đủ.
- Không đưa `/api/dev/**`, `/api/test/**`, Swagger, seed data, default credential hoặc SQL log chi tiết vào hành vi production.
- Với endpoint mới hoặc thay đổi, cập nhật annotation OpenAPI và `.http/<controller-name>.http` tương ứng.

## Redis và xử lý bất đồng bộ

- Theo `docs/engineering/redis-guide.md`: cache DTO có kiểu, template có tên, key có version và TTL tường minh.
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

- Catalog canonical về tất cả project/global/system skills: `docs/ai/skill-catalog.md`.
- Dùng `$implement-livestream-feature` để triển khai feature backend đầu-cuối.
- Dùng `$diagnose-livestream-backend` để chẩn đoán lỗi, regression và môi trường chạy.
- Dùng `$review-livestream-change` để review diff, commit hoặc pull request.
- Dùng `$refine-engineering-prompt` khi cần làm rõ yêu cầu kỹ thuật thô trước khi triển khai.
- Dùng `$manage-local-port` để kiểm tra hoặc giải phóng port phát triển cục bộ an toàn.
- Dùng `$run-senior-java-learning` để bắt đầu/tiếp tục chu trình theory -> deep-dive -> question bank -> case -> implementation/evidence -> teach-back hoặc tạo preview learning pack chuẩn mà không vượt checkpoint/evidence gate.
- Khi tạo, cài, rename, move, xóa hoặc đổi trigger/scope của skill, bắt buộc cập nhật `docs/ai/skill-catalog.md` trong cùng change. Skill change chưa hoàn thành nếu catalog chưa đồng bộ.

## Chất lượng tài liệu học

- Theory/deep-dive dành cho người chưa biết topic phải là bài giảng, không phải cheat sheet hoặc matrix keyword. Dùng `docs/templates/theory-note-template.md` và `$run-senior-java-learning` làm quality contract.
- Agent phải dạy mental model, mechanism, ví dụ và failure trước; `LEARNER TODO` chỉ dành cho learner write-back/self-check ở cuối tài liệu.
- Giữ tiếng Việt tự nhiên, có dấu; giữ và giải nghĩa IT English term cần thiết.
- Table chỉ cô đọng phần đã giải thích. Với lifecycle/state/sequence/causal chain, dùng Mermaid dễ đọc theo `$mermaid-styling`.
- Preview chưa đủ teaching content phải ghi `Teaching readiness: OUTLINE_ONLY`; không giới thiệu nó là tài liệu tự học hoặc tiếp tục sinh thêm outline trước khi nâng cấp topic active.

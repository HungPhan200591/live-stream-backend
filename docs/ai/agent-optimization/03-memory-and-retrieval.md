# Memory: native Codex, Basic Memory và Mem0

## Nếu không dùng memory

Trong cùng chat, Codex vẫn có lịch sử cho tới khi context được compact. Khi mở chat mới, Agent chủ yếu dựa vào prompt, `AGENTS.md`, repository, IDE context và tool bạn cấp. Nó không tự biết một quyết định chỉ tồn tại trong chat cũ.

Workflow không memory vẫn tốt nếu:

- durable decision đã ở code, test, ADR hoặc checkpoint;
- mỗi task có prompt/outcome rõ;
- Agent đọc source trực tiếp thay vì dựa vào hồi ức;
- bạn không phải lặp lại nhiều preference/context cá nhân giữa các project.

## Native Codex memories

Native memories là local recall layer của Codex host. Khi bật, Codex có thể trích xuất context hữu ích từ eligible prior chats, tổng hợp ở background và chèn memory liên quan vào future session. CLI, desktop app và IDE extension nối cùng Codex host có thể dùng cùng local store.

Khác biệt thực tế:

| Không dùng | Dùng native memories |
| --- | --- |
| Chat mới bắt đầu từ prompt/repo/tool hiện tại | Chat mới có thể nhận thêm context hữu ích từ chat cũ |
| Không có background extraction | Extraction/consolidation có thể dùng quota khi thread đã idle |
| Ít nguy cơ stale recall | Có nguy cơ memory cũ, thiếu hoặc không liên quan được chèn |
| Phải ghi checkpoint hoặc giải thích lại | Bớt lặp preference, convention và decision context |

Native memory phù hợp cho preference, cách làm việc, quyết định còn giá trị và project context khó đặt hết trong prompt. Không dùng nó làm nguồn duy nhất cho security rule, API contract, current external fact hay secret. Prompt hiện tại và repository source of truth luôn ưu tiên hơn memory.

Codex local memories mặc định tắt; cấu hình bằng `[features] memories = true` và điều khiển từng chat qua `/memories`. Memory có thể chưa xuất hiện ngay khi chat kết thúc vì quá trình tạo chạy sau khi thread idle. Nguồn: [Codex Memories](https://learn.chatgpt.com/docs/customization/memories).

## Basic Memory

Basic Memory là knowledge base local-first: knowledge nằm trong Markdown do người dùng sở hữu, còn database/index phục vụ tìm kiếm. MCP cung cấp các thao tác search/read/write/build context để nhiều AI client dùng chung.

Điểm mạnh:

- Markdown dễ đọc, review, backup và chỉnh bằng editor/Obsidian.
- Local mode không yêu cầu cloud account; knowledge có thể dùng chung giữa Codex, Cursor, Claude và client MCP khác.
- Phù hợp project notes, research, quyết định và knowledge base cần con người kiểm soát.

Trade-off:

- Cần taxonomy/note hygiene; ghi quá nhiều sẽ tạo stale hoặc duplicate knowledge.
- MCP tool schema và kết quả retrieval làm tăng context nếu luôn bật.
- Agent cần instruction/skill để biết khi nào search và khi nào write.
- Local project dùng AGPL-3.0; cần đánh giá license nếu nhúng hoặc sửa để cung cấp qua network.

Nguồn: [Basic Memory overview](https://docs.basicmemory.com/start-here/what-is-basic-memory), [technical information](https://docs.basicmemory.com/reference/technical-information).

## Mem0 và OpenMemory

Mem0 là memory engine dùng extraction, semantic search, metadata và tùy chọn graph/reranking để biến hội thoại thành memory ngắn rồi truy xuất theo user/agent/session. Mem0 MCP hiện hành là cloud-hosted; OpenMemory cung cấp hướng self-hosted/local trong hệ Mem0.

Điểm mạnh:

- Tự động hóa capture, deduplicate, update và semantic retrieval tốt hơn note thủ công.
- Có user/agent/run scope, filter và lifecycle integration cho nhiều ứng dụng.
- Phù hợp assistant/chatbot nhiều phiên, nhiều user hoặc cần personalization ở quy mô sản phẩm.

Trade-off:

- Hosted Mem0 đưa memory lên dịch vụ bên ngoài và cần API key/governance phù hợp.
- Self-hosted cần database/vector store, embedding và có thể cần LLM; vận hành và chi phí cao hơn Markdown.
- Automatic extraction khó audit hơn note do con người kiểm soát; stale/wrong memory vẫn cần update/delete policy.
- MCP server, lifecycle hook và retrieval result đều có overhead. Con số tiết kiệm so với “full conversation context” trong benchmark không tự động áp dụng cho coding agent đã có Git, compact và source routing.
- Repository `mem0ai/mem0-mcp` cũ đã archive; integration mới nên theo endpoint/documentation hiện hành.

Nguồn: [Mem0 MCP](https://docs.mem0.ai/platform/mem0-mcp), [Mem0 Platform](https://docs.mem0.ai/platform/overview), [Mem0 research](https://arxiv.org/abs/2504.19413).

## Chọn giải pháp

| Nhu cầu | Lựa chọn đầu tiên |
| --- | --- |
| Chỉ cần Codex nhớ context hữu ích giữa chat | Native Codex memories |
| Knowledge phải review được bằng Markdown và dùng chung nhiều AI | Basic Memory |
| Sản phẩm nhiều user cần semantic personalization tự động | Mem0 Platform |
| Cần self-hosted semantic memory và chấp nhận vận hành thêm | OpenMemory/Mem0 OSS |
| Contract/rule/checkpoint của một repository | Git docs, ADR, test và nested `AGENTS.md`, không phải memory |

Không bật đồng thời nhiều memory layer trong lần thử đầu. Hãy A/B một lớp, đo retrieval quality và token overhead, rồi mới quyết định.

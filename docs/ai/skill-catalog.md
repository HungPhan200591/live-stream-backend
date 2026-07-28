# Codex Skill Catalog

> Trạng thái: `CANONICAL SKILL INVENTORY`<br>
> Snapshot: 2026-07-28<br>
> Đối tượng: Human, Codex và AI Agent khác

File này là mục lục canonical cho các Codex skill được cài hoặc được project sử dụng. Dùng catalog để biết skill làm gì, khi nào nên trigger và scope của skill; dùng chính `SKILL.md` làm source of truth cho workflow chi tiết sau khi skill đã trigger.

Runtime vẫn là authority cuối cùng về skill nào được expose trong một session. Catalog không làm một skill tự động khả dụng nếu Codex chưa discover hoặc expose skill đó.

## 1. Inventory summary

| Scope | Vị trí | Số lượng | Ý nghĩa |
| --- | --- | ---: | --- |
| Project | `.agents/skills/` | 10 | Workflow riêng của `live-stream-backend` |
| User global | `$CODEX_HOME/skills/` | 2 | Skill dùng được giữa nhiều repository |
| Codex system | `$CODEX_HOME/skills/.system/` | 6 | Skill do môi trường Codex cung cấp |
| **Tổng cài đặt** |  | **18** | 15 skill được expose trong session hiện tại; skill mới cần session discovery để xuất hiện; `review-agent` chỉ được cài trên filesystem |

## 2. Project skills

| Skill | Mô tả ngắn | Khi dùng | Không dùng khi |
| --- | --- | --- | --- |
| [`implement-livestream-feature`](../../.agents/skills/implement-livestream-feature/SKILL.md) | Triển khai vertical slice Spring Boot gồm API, service, persistence, cache/messaging/realtime khi liên quan, authorization, test và API artifacts. | Thêm, thay đổi hoặc hoàn thiện feature/backend learning case đầu-cuối. | Chỉ cần tư vấn, chẩn đoán hoặc review read-only. |
| [`diagnose-livestream-backend`](../../.agents/skills/diagnose-livestream-backend/SKILL.md) | Điều tra build/test/runtime failure và lỗi JWT, PostgreSQL/JPA, Redis, RabbitMQ, WebSocket, webhook, Docker. | Cần tái hiện, tìm nguyên nhân, giải thích hoặc khắc phục lỗi backend. | Yêu cầu chỉ là feature mới chưa có failure cần chẩn đoán. |
| [`review-livestream-change`](../../.agents/skills/review-livestream-change/SKILL.md) | Review defect-first cho diff/commit/PR, bao gồm security, transaction/concurrency, query/cache/event reliability, test và docs drift. | Code review, risk assessment, regression hoặc pre-merge validation. | Người dùng yêu cầu trực tiếp triển khai feature thay vì review. |
| [`commit-livestream-change`](../../.agents/skills/commit-livestream-change/SKILL.md) | Kiểm tra phạm vi, staged diff, secret và validation evidence rồi tạo Git commit cục bộ; không push. | Người dùng yêu cầu commit, local commit hoặc commit các file/change cụ thể. | Chỉ cần sinh commit message, review read-only hoặc chưa cho phép thay đổi Git history. |
| [`refine-engineering-prompt`](../../.agents/skills/refine-engineering-prompt/SKILL.md) | Chuyển yêu cầu kỹ thuật thô thành prompt có scope, context, constraints, Acceptance Criteria và verification. | Cần làm rõ, viết lại hoặc cấu trúc yêu cầu trước khi thực thi. | Yêu cầu đã đủ rõ và người dùng muốn triển khai ngay. |
| [`manage-local-port`](../../.agents/skills/manage-local-port/SKILL.md) | Tìm process sở hữu TCP port trên Windows và giải phóng port an toàn. | Port local bị chiếm, cần xác định hoặc dừng đúng process. | Debug network/application không liên quan ownership của local port. |
| [`run-senior-java-learning`](../../.agents/skills/run-senior-java-learning/SKILL.md) | Điều phối knowledge-to-evidence domain-first, chọn scenario concrete theo owner/use-case priority/difficulty mà không nhảy Stage, và áp content/reference gate từ theory template + quality audit. | Bắt đầu/tiếp tục topic hoặc case, chọn bài toán Livestream để thực hành, tạo/audit learning pack hoặc luyện từ foundation tới Senior/Architect/Expert dựa trên project evidence. | Bug fix, feature implementation hoặc review độc lập không có learning objective. |
| [`start-learning`](../../.agents/skills/start-learning/SKILL.md) | Khôi phục learning cursor và luôn trả link trực tiếp tới tài liệu cần mở trước khi dạy. | Người dùng nói bắt đầu/tiếp tục session học hoặc gọi `$start-learning`. | Bug fix, implementation hoặc review không có mục tiêu học. |
| [`audit-learning-theory`](../../.agents/skills/audit-learning-theory/SKILL.md) | Audit read-only theory `CORE`/`DEEP_DIVE` theo teaching gate, self-check coverage và language/depth review. | Hỏi tài liệu đã đủ để tự học/trả lời câu hỏi chưa, cần verdict readiness hoặc gap cụ thể. | Muốn triển khai case/code, tạo runtime evidence hoặc tự điền learner write-back. |
| [`distill-learning-knowledge`](../../.agents/skills/distill-learning-knowledge/SKILL.md) | Phân loại và cô đọng kiến thức vừa làm rõ vào đúng theory/case/question/evidence artifact. | Muốn lưu khái niệm, misconception, failure chain hoặc trade-off mà không tạo duplicate hay evidence giả. | Chỉ cần giải thích thoáng qua, triển khai code hoặc tự điền learner write-back. |

## 3. User-global skills

| Skill | Mô tả ngắn | Khi dùng | Ghi chú |
| --- | --- | --- | --- |
| `mermaid-styling` | Tạo hoặc sửa Mermaid dễ đọc với layout an toàn chiều rộng và palette tương phản cao. | Bất kỳ task nào tạo hoặc chỉnh Mermaid. | Phải giữ syntax tương thích renderer mục tiêu. |

## 4. Codex system skills

| Skill | Availability | Mô tả ngắn | Khi dùng |
| --- | --- | --- | --- |
| `imagegen` | Active | Tạo hoặc chỉnh sửa raster image bằng AI. | Cần ảnh bitmap mới, biến đổi ảnh hoặc tạo variant từ reference; không dùng thay SVG/HTML/CSS native. |
| `openai-docs` | Active | Tra cứu cách dùng OpenAI/Codex bằng nguồn chính thức và thông tin hiện hành. | Hỏi về OpenAI API, ChatGPT, Codex, model hoặc prompting hiện tại. |
| `plugin-creator` | Active | Scaffold và cập nhật Codex plugin/manifest/marketplace metadata. | Tạo hoặc phát triển personal Codex plugin. |
| `skill-creator` | Active | Thiết kế, khởi tạo, chỉnh sửa và validate Codex skill. | Tạo mới hoặc thay đổi skill, trigger, workflow hay bundled resources. |
| `skill-installer` | Active | Liệt kê và cài skill từ curated source hoặc GitHub repository. | Người dùng yêu cầu tìm hoặc cài thêm skill. |
| `review-agent` | Installed, not exposed | Read-only defect-first review cho change được agent khác giao. | Chỉ dùng khi runtime expose skill và một agent giao review cụ thể; không giả định khả dụng trong session hiện tại. |

## 5. Chọn skill nhanh

| Intent | Skill ưu tiên |
| --- | --- |
| Implement backend feature | `implement-livestream-feature` |
| Diagnose lỗi backend | `diagnose-livestream-backend` |
| Review change | `review-livestream-change` |
| Tạo Git commit local, không push | `commit-livestream-change` |
| Làm rõ prompt kỹ thuật | `refine-engineering-prompt` |
| Tìm process chiếm port | `manage-local-port` |
| Bắt đầu/tiếp tục case hoặc tạo learning pack Senior Java | `run-senior-java-learning` |
| Bắt đầu/khôi phục session và cần link tài liệu phải mở | `start-learning` -> `run-senior-java-learning` |
| Đánh giá theory core/deep-dive có đủ để tự học/trả lời self-check | `audit-learning-theory` |
| Cô đọng kiến thức vừa làm rõ vào đúng learning artifact | `distill-learning-knowledge` |
| Hiểu codebase/architecture/file relationships | `rg` + source/docs trực tiếp; không dùng skill mặc định |
| Tạo hoặc sửa Mermaid | `mermaid-styling` |
| Tạo hoặc sửa skill | `skill-creator` |
| Cài skill | `skill-installer` |
| Tạo plugin | `plugin-creator` |
| Hỏi về OpenAI/Codex | `openai-docs` |
| Tạo/chỉnh bitmap image | `imagegen` |

Nếu nhiều skill cùng trigger, dùng tập nhỏ nhất bao phủ task và công bố thứ tự sử dụng. Project skill điều khiển workflow nghiệp vụ; global/system skill bổ sung capability chuyên biệt.

## 6. Khi nào nên tạo skill mới

Chỉ tạo skill mới khi có ít nhất một điều kiện:

- Workflow đã lặp lại từ hai lần và có procedural knowledge ổn định.
- Thao tác có rủi ro cao hoặc cần trình tự deterministic, guardrail và validation cố định.
- Task cần tool integration, script, reference hoặc asset tái sử dụng mà rule/doc thông thường không đủ.
- Output có contract lặp lại và cần evaluation riêng.
- Mở rộng skill hiện có sẽ làm trigger mơ hồ hoặc trộn hai responsibility độc lập.

Không tạo skill cho kiến thức công nghệ chung, một task dùng một lần, một prompt ngắn có thể đặt trong docs, hoặc chỉ để bọc lại rule đã thuộc `AGENTS.md`.

## 7. Mandatory catalog update rule

Mọi thay đổi skill phải cập nhật file này trong cùng change. Quy tắc áp dụng khi:

- tạo hoặc cài skill mới;
- rename, move hoặc xóa skill;
- thay đổi description/trigger/scope/availability;
- tách hoặc hợp nhất skill;
- promote skill từ project lên global hoặc ngược lại.

Change chưa hoàn thành nếu catalog chưa được đồng bộ. Khi cập nhật:

1. Thêm hoặc sửa đúng row trong inventory.
2. Cập nhật số lượng và availability snapshot.
3. Cập nhật bảng chọn nhanh nếu routing thay đổi.
4. Cập nhật `AGENTS.md` nếu đây là project skill mà Agent phải route thường xuyên.
5. Cập nhật `003_AI_AGENT_ENGINEERING_SYSTEM.md` nếu lifecycle, backlog hoặc evaluation thay đổi.
6. Cập nhật Documentation Orchestrator nếu skill làm thay đổi reading/write-back route.

## 8. Skill Definition of Done

- Tên folder và frontmatter `name` dùng lowercase kebab-case và khớp nhau.
- Frontmatter `description` nói rõ capability và trigger; negative trigger được kiểm tra khi dễ nhầm.
- `SKILL.md` giữ workflow cốt lõi; chi tiết lớn đặt trong `references/`, logic lặp lại đặt trong `scripts/`.
- `agents/openai.yaml` được tạo/cập nhật khi skill cần UI metadata.
- Skill validation pass; script mới đã được chạy thử.
- Có prompt nên trigger, không nên trigger và forward-test phù hợp rủi ro.
- Catalog này và các routing artifact liên quan đã được cập nhật trong cùng change.

## 9. Maintenance

- Owner của project skill: repository này.
- Owner của user-global/system skill: Codex installation; project chỉ ghi nhận cách dùng và availability.
- Project skills phải đọc Java/Spring baseline từ POM/`AGENTS.md` và active learning case từ `docs/learning/index.md`; không hard-code case ID hoặc version target vào ví dụ/instruction dễ drift.
- `run-senior-java-learning` phải dùng `docs/templates/theory-note-template.md` và `docs/learning/theory-quality-audit.md` khi tạo/nâng readiness theory; không suy readiness từ số file/word count và không nâng learner/runtime evidence. Skill phải tách lượt kiểm ngôn ngữ/chiều sâu khỏi lượt kiểm cấu trúc: tiếng Việt tự nhiên, English term được giải nghĩa và deep-dive có causal narrative hoàn chỉnh là gate bắt buộc.
- Kiểm kê lại catalog khi nâng cấp Codex, cài plugin/skill hoặc trước một đợt refactor Agent system lớn.
- Nếu catalog và runtime khác nhau, báo drift, dùng runtime làm bằng chứng availability và cập nhật catalog.

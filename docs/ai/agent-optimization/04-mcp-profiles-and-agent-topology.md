# MCP, profile và multi-agent

## MCP không phải memory mặc định

MCP là giao thức để Agent khám phá/call tool hoặc đọc resource từ external system. Memory MCP chỉ là một loại MCP server cung cấp thao tác ghi/tìm memory.

Mỗi server có thể bổ sung:

- server instructions;
- tool name, description và JSON schema;
- quyết định chọn tool và tool-call round trip;
- kết quả được đưa trở lại context.

Do đó, server hiếm dùng nên tắt. Server cần bật phải giới hạn bằng `enabled_tools` hoặc `disabled_tools`, đồng thời giới hạn page size/depth/result ở lời gọi. Nguồn: [Codex MCP](https://learn.chatgpt.com/docs/extend/mcp), [MCP tools](https://modelcontextprotocol.io/specification/2025-11-25/server/tools).

## Rule không đủ để bảo vệ tool nguy hiểm

Với database/cache/production integration, ưu tiên theo thứ tự:

1. Credential/ACL read-only.
2. MCP allowlist chỉ expose tool cần thiết.
3. Approval cho write/destructive tool.
4. Prompt/`AGENTS.md` giải thích ý đồ và prohibited action.
5. Audit/log/backup để phát hiện và phục hồi.

Nếu server expose `delete`, `flush`, `drop` hoặc bulk write bằng credential toàn quyền, một câu “đừng xóa” trong prompt không phải boundary đủ mạnh.

## Vì sao subagent tốn hơn

Mỗi subagent tự thực hiện model và tool work, thường nhận lại một phần instruction/repository context. Agent chính còn phải spawn, chờ và tổng hợp. Vì thế:

```text
single-agent cost ≈ một context + một tool loop
multi-agent cost  ≈ context/tool loop của parent
                    + tổng context/tool loop của từng worker
                    + summary/coordination
```

Parallelism có thể giảm wall-clock khi các nhánh độc lập và đủ lớn, nhưng không đảm bảo giảm token. Không dùng subagent cho task tuyến tính, thay đổi nhỏ, một call chain hoặc các worker sẽ đọc/sửa cùng file. Nguồn: [Codex Subagents](https://learn.chatgpt.com/docs/agent-configuration/subagents).

## “Lean” là gì

`lean` không phải mode tích hợp của Codex. Đó chỉ là tên profile do người dùng tự đặt, ví dụ `~/.codex/lean.config.toml`, rồi CLI chọn bằng:

```powershell
codex --profile lean
```

Nếu không tạo/chọn profile, Codex dùng config base và project config theo precedence thông thường. Vì vậy “không dùng lean” không làm mất tính năng nào; nó chỉ có nghĩa bạn chỉnh trực tiếp cấu hình mặc định thay vì duy trì một preset ít tool/reasoning thấp.

Codex CLI profile file nằm cạnh `config.toml` và chỉ được overlay khi gọi `--profile`. Nguồn: [Codex config profiles](https://learn.chatgpt.com/docs/config-file/config-advanced#profiles).

## VS Code/IDE extension áp dụng config thế nào

Local Codex clients dùng Codex host và chia sẻ `~/.codex/config.toml`; trusted repository có thể bổ sung `.codex/config.toml`. Sau khi đổi MCP/feature, restart extension hoặc mở thread mới nếu UI chưa cập nhật.

CLI `--profile` là selector lúc khởi động CLI, không phải một mode tự động áp lên mọi IDE chat. Nếu muốn VS Code/IntelliJ luôn dùng cấu hình đó, đặt các key cần thiết vào global `~/.codex/config.toml` hoặc project `.codex/config.toml`, hoặc dùng màn hình MCP/settings mà extension hiện tại cung cấp. Kiểm tra `/mcp`, `/status`, `/memories` hoặc menu `/` vì khả dụng UI phụ thuộc phiên bản.

## Baseline tool topology

- Global: chỉ giữ tool dùng ở hầu hết project.
- Project: database/service MCP chỉ cho repository cần nó.
- Server nhiều tool: dùng allowlist read-only mặc định; mở write theo task rõ ràng.
- Multi-agent: tắt mặc định nếu workflow chủ yếu tuyến tính; bật lại có chủ đích cho review/research song song.
- Memory: chỉ bật một lớp thử nghiệm và theo dõi retrieval quality.

# Tối ưu AI Agent: token, context và chất lượng

> Phạm vi: nguyên tắc dùng chung cho repository phần mềm, không phụ thuộc live-stream-backend.<br>
> Mục tiêu: giảm context không cần thiết nhưng vẫn giữ đủ rule, evidence và source of truth để Agent làm đúng.<br>
> Cập nhật: 2026-08-01.

## Bắt đầu từ đâu

Không có một memory server hay model nhỏ nào tự động tối ưu toàn bộ workflow. Thứ tự hiệu quả thường là:

1. Giảm context **luôn được nạp**: prompt lặp, root `AGENTS.md`, MCP/tool không dùng.
2. Chuyển rule chuyên biệt sang nested `AGENTS.md` hoặc skill để chỉ nạp khi đúng scope.
3. Dùng source of truth trong Git cho contract, quyết định và checkpoint; dùng memory như lớp recall, không thay rule.
4. Giữ một agent cho task tuyến tính; chỉ dùng subagent khi có nhánh độc lập đủ lớn.
5. Đo input, cached input, output và reasoning thay vì đánh giá theo cảm giác hoặc tổng token duy nhất.

## Bộ tài liệu

| File | Câu hỏi trả lời |
| --- | --- |
| [Context và mô hình chi phí token](01-context-token-model.md) | Token bị dùng ở đâu, cache/compact/memory tác động thế nào? |
| [Kiến trúc instruction không làm mất rule](02-instruction-architecture.md) | Đặt rule ở prompt, `AGENTS.md`, skill, docs, hook hay MCP? |
| [Memory: native, Basic Memory và Mem0](03-memory-and-retrieval.md) | Memory dùng để làm gì, khác gì không dùng, chọn giải pháp nào? |
| [MCP, profile và multi-agent](04-mcp-profiles-and-agent-topology.md) | Vì sao MCP/subagent tăng token, “lean” là gì, IDE áp dụng config ra sao? |
| [Playbook vận hành và đo lường](05-operating-playbook.md) | Bắt đầu, tiếp tục, compact, kết thúc và A/B test một phiên thế nào? |

## Baseline khuyến nghị

- Root `AGENTS.md` ngắn nhưng chứa universal guardrail, rule discovery và source routing.
- Nested `AGENTS.md` chứa rule của subtree; root liệt kê rõ đường dẫn để Agent không bỏ sót.
- MCP mặc định tắt nếu hiếm dùng; server đang bật phải có `enabled_tools`/`disabled_tools` tối thiểu.
- Rule phá hủy dữ liệu phải được chặn thêm bằng quyền, ACL, sandbox hoặc approval; không chỉ dựa vào prompt.
- Memory chỉ lưu preference, quyết định và context có khả năng tái sử dụng; không lưu secret hay current external fact.
- Chat mới cho outcome mới; `/compact` tại milestone của cùng outcome; durable decision phải được ghi vào code/docs/ADR.

## Nguồn chính

- [Codex best practices](https://learn.chatgpt.com/docs/best-practices)
- [Codex AGENTS.md](https://learn.chatgpt.com/docs/agent-configuration/agents-md)
- [Codex Memories](https://learn.chatgpt.com/docs/customization/memories)
- [Codex MCP](https://learn.chatgpt.com/docs/extend/mcp)
- [Codex Subagents](https://learn.chatgpt.com/docs/agent-configuration/subagents)
- [Model Context Protocol specification](https://modelcontextprotocol.io/specification/2025-11-25)

# Kiến trúc instruction không làm mất rule

## Chọn đúng bề mặt

| Nội dung | Nơi nên đặt | Lý do |
| --- | --- | --- |
| Outcome/ràng buộc chỉ cho task hiện tại | Prompt | Không làm phình các task sau |
| Guardrail áp dụng toàn repository | Root `AGENTS.md` | Luôn có mặt và review được bằng Git |
| Rule của module/subtree | Nested `AGENTS.md` | Chỉ áp dụng khi Agent làm trong scope đó |
| Workflow tái sử dụng có nhiều bước | Skill | Chỉ nạp body khi trigger, có thể kèm script/reference |
| Contract, architecture, ADR, runbook | `docs/` hoặc code/test | Source of truth chi tiết, đọc on demand |
| Dữ liệu sống từ DB/SaaS | MCP/connector | Truy xuất current external state có kiểm soát |
| Preference và context hữu ích từ chat cũ | Memory | Recall có chọn lọc, không phải authority |
| Chặn hành động cơ học | ACL, sandbox, approval, hook | Không phụ thuộc model nhớ và tuân thủ câu chữ |

## Root `AGENTS.md` cần giữ gì

Root không nên biến thành wiki, nhưng cũng không được chỉ chứa link mơ hồ. Tối thiểu phải có:

- rule discovery: trước khi sửa subtree phải tìm/read nested `AGENTS.md`;
- universal safety: secret, destructive data, authorization và phạm vi thay đổi;
- source hierarchy và routing tới contract chính;
- lệnh build/test tối thiểu và Definition of Done;
- bảng scope → nested rules để Agent biết file nào tồn tại.

Rule mà vi phạm có thể gây mất dữ liệu, lỗ hổng hoặc contract drift không nên chỉ nằm sâu trong một tài liệu ít được đọc. Giữ bản ngắn, hành động được ở root; phần giải thích và ví dụ nằm trong nested rules/docs.

## Nested `AGENTS.md` an toàn

Mỗi nested file nên:

1. Nêu phạm vi subtree ngay đầu file.
2. Yêu cầu đọc cùng root `AGENTS.md`.
3. Chỉ chứa rule chuyên biệt của subtree.
4. Không sao chép universal rules nếu không cần làm rõ hơn.
5. Có verification phù hợp với loại file trong subtree.

Ví dụ cấu trúc:

```text
AGENTS.md                    # universal rules + routing
src/main/java/AGENTS.md      # architecture, API, security, transaction
src/test/java/AGENTS.md      # deterministic tests, negative cases
docs/learning/AGENTS.md      # checkpoint, evidence và teaching quality
```

## Ba lớp chống miss rule

Rule quan trọng nên có nhiều hơn một cơ chế bảo vệ:

| Lớp | Ví dụ |
| --- | --- |
| Instruction | “Không chạy `FLUSHALL` hoặc drop schema khi chưa được yêu cầu” |
| Capability boundary | Redis ACL read-only, MCP `enabled_tools`, database read-only role |
| Verification | Negative test, diff review, migration check hoặc approval prompt |

Instruction giúp Agent hiểu ý đồ. Capability boundary giảm blast radius nếu instruction bị miss. Verification phát hiện lỗi trước khi handoff.

## Khi nào chuyển rule thành skill

Chuyển sang skill khi workflow:

- có trigger rõ như review, diagnose, commit hoặc tạo learning pack;
- cần nhiều bước/reference/script nhưng không áp dụng mọi turn;
- được lặp ở nhiều project hoặc nhiều lần trong một project.

Không chuyển universal safety thành skill chỉ để giảm token: skill có thể không trigger. Root vẫn phải giữ guardrail ngắn.

Nguồn: [Codex customization](https://learn.chatgpt.com/docs/customization), [AGENTS.md](https://learn.chatgpt.com/docs/agent-configuration/agents-md), [Skills](https://learn.chatgpt.com/docs/skills).

# Playbook vận hành và đo lường AI Agent

## Bắt đầu task

1. Mở chat mới nếu outcome khác task trước.
2. Nêu outcome, phạm vi, guardrail và verification; không dán toàn bộ lịch sử.
3. Chỉ rõ file/contract đầu tiên cần đọc và lý do.
4. Agent kiểm tra `git status`, root/nested `AGENTS.md` và source trực tiếp.
5. Bật MCP/hạ tầng chỉ khi task cần current external state.

Prompt mẫu:

```text
Outcome: <kết quả cần có>.
Scope: <module/file/endpoint>.
Guardrail: <authorization/data compatibility/no destructive action>.
Đọc trước: <file/symbol/source of truth>.
Verification: <compile/test/runtime evidence>.
Không mở rộng sang <out-of-scope>.
```

## Trong task

- Mỗi search/tool call phải giải quyết một uncertainty cụ thể.
- Với log dài, giữ error gốc, stack liên quan và lệnh reproducer; không đưa toàn log vào chat.
- Sau mỗi milestone, kiểm tra diff/test/source of truth thay vì hỏi Agent nhớ đã làm gì.
- Nếu cùng outcome nhưng context cao, tạo Context Ledger rồi `/compact`.
- Nếu outcome đã đổi, mở chat mới thay vì compact một transcript nhiễu.

Context Ledger tối thiểu:

```text
1. Outcome và Definition of Done
2. Guardrail/contract đang áp dụng
3. Decision đã chốt và lý do
4. File đã sửa hoặc cần đọc tiếp
5. Test/evidence đã chạy
6. Việc còn lại, risk và UNKNOWN
```

## Khi nào dùng subagent

Chỉ cân nhắc khi tất cả điều kiện đúng:

- Có ít nhất hai nhánh độc lập, không tranh chấp cùng file/state.
- Mỗi nhánh đủ lớn để bù startup và coordination overhead.
- Worker có input/output boundary rõ và trả summary ngắn.
- Giảm wall-clock hoặc tăng coverage có giá trị hơn tổng token tăng.

Ví dụ phù hợp: security review, test-gap analysis và documentation review độc lập. Không phù hợp: sửa một service nhỏ, trace một call chain hoặc chạy ba worker cùng scan repository.

## Kết thúc task

1. Chạy validation nhỏ nhất phù hợp rồi mở rộng theo rủi ro.
2. Đọc diff để tìm scope creep, secret, contract drift và missing failure case.
3. Ghi durable decision vào code/docs/ADR/checkpoint; không chỉ để trong chat hoặc memory.
4. Tóm tắt outcome, file thay đổi, evidence pass và việc chưa chạy.
5. Chỉ lưu memory cho fact có khả năng tái sử dụng; không lưu log tạm, secret hay trạng thái dễ stale.

## A/B test cấu hình

Dùng cùng một prompt read-only, cùng repository state và chat mới cho mỗi biến thể:

| Biến thể | Mục đích |
| --- | --- |
| Baseline hiện tại | Đo prefix/tool/context thực tế |
| Tắt MCP/subagent không dùng | Đo always-on overhead |
| Bật native memory | Đo retrieval benefit và background cost |
| Nested `AGENTS.md` | Đo giảm prefix nhưng vẫn đạt rule-compliance |

Theo dõi:

- input/cached input/output/reasoning token;
- số tool call và lượng output;
- wall-clock;
- số lần hỏi lại hoặc đọc lặp;
- rule/contract bị miss;
- test và review outcome.

Trong CLI dùng `/status`, `/usage daily` hoặc `codex exec --json`. Trong IDE dùng `/status` nếu phiên hiện tại hỗ trợ và đối chiếu transcript/tool output. Chạy ít nhất vài task cùng loại trước khi kết luận; một turn có variance lớn.

## Review định kỳ

Mỗi tháng hoặc khi usage tăng bất thường:

- đo kích thước global/root `AGENTS.md`;
- liệt kê MCP đang bật và số tool được expose;
- xóa/disable server không còn dùng;
- kiểm tra memory stale/secret và retrieval noise;
- chuyển workflow lặp sang skill;
- archive docs cũ và sửa link routing;
- kiểm tra subagent có thực sự giảm wall-clock hay chỉ tăng coordination.

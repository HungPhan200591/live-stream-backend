# Dùng slash command và quản lý context trong Codex IDE

> Phạm vi: Codex extension trong IntelliJ/VS Code. Các lệnh hiện ra còn tùy phiên bản extension, model, gói tài khoản và chính sách workspace. Gõ `/` trong ô chat để xem đúng danh sách đang khả dụng.
>
> Cập nhật: 2026-07-28. Nguồn sản phẩm: [Codex IDE extension slash commands](https://learn.chatgpt.com/docs/developer-commands.md?surface=ide), [Projects and chats](https://learn.chatgpt.com/docs/projects), [Long-running work](https://learn.chatgpt.com/docs/long-running-work).

## Cách gọi

Trong ô composer của Codex, gõ `/`, chọn lệnh trong menu hoặc gõ đầy đủ rồi nhấn Enter. Đây là lệnh điều khiển phiên chat/UI, khác với `$skill-name` là workflow cho một loại công việc.

Ví dụ:

```text
/status
/compact
/plan Lập kế hoạch chuyển đổi module auth, chưa sửa mã.
/goal Hoàn thiện API stream title; owner-only, có test và .http.
```

## Các lệnh đáng dùng trong dự án

| Lệnh | Dùng khi | Lưu ý thực tế |
| --- | --- | --- |
| `/status` | Cần xem trạng thái chat, mức dùng context và rate limit. | Đây là điểm kiểm tra đầu tiên trước/sau một phiên dài. |
| `/compact` | Chat dài, nhiều log/diff/đoạn điều tra đã xong. | Nén context của **chat hiện tại**; Codex cũng có thể tự compact khi cần. |
| `/fork` | Muốn thử hướng sửa/thiết kế khác nhưng giữ nguyên luồng gốc. | Tạo bản sao local chat; đừng dùng để xử lý tiếp cùng một file theo hai nhánh song song nếu không có worktree. |
| `/side` | Cần hỏi nhanh về trạng thái/khái niệm mà không làm gián đoạn main chat. | Phù hợp cho câu hỏi phụ, không phải nơi lưu quyết định chính. |
| `/plan` | Việc nhiều bước hoặc có rủi ro; muốn thống nhất hướng đi trước khi sửa. | Có thể truyền yêu cầu ngay sau lệnh. |
| `/goal` | Công việc dài có kết quả, ràng buộc và điều kiện hoàn thành rõ. | Goal giữ mục tiêu trong chat, nhưng không tự mở rộng quyền thao tác. |
| `/ide-context` | Muốn bật/tắt context tự động từ file đang mở, selection và IDE. | Tắt khi context editor đang nhiễu; thay vào đó đính kèm/chỉ rõ file cần đọc. |
| `/init` | Repository chưa có hướng dẫn bền vững cho Codex. | Sinh scaffold `AGENTS.md`; luôn review rồi chỉnh theo dự án, không xem output là cấu hình hoàn chỉnh. Repo này đã có `AGENTS.md`, thường không cần gọi lại. |
| `/review` | Review thay đổi chưa commit hoặc so với base branch. | Chỉ review; không mặc định là yêu cầu sửa lỗi. |
| `/worktree` | Cần một checkout tách biệt cho thay đổi độc lập. | Dùng khi chạy việc song song có ghi file hoặc muốn cô lập thử nghiệm. |
| `/local`, `/cloud`, `/cloud-environment` | Chọn nơi thực thi. | Cloud chỉ khả dụng khi tài khoản/workspace hỗ trợ; kiểm tra dữ liệu và quyền truy cập trước khi chuyển. |
| `/mcp` | Kiểm tra MCP server/connector. | Dùng khi thiếu tool hoặc tool tích hợp lỗi xác thực. |
| `/model`, `/reasoning`, `/fast`, `/personality` | Điều chỉnh model, mức suy luận, service tier hoặc phong cách trả lời. | Chỉ hiện khi model/tài khoản hỗ trợ. |
| `/memories` | Cấu hình việc dùng/tạo memory giữa các chat. | Memory là tiện ích tùy chọn; yêu cầu trong chat và `AGENTS.md` vẫn ưu tiên hơn. |

## `/compact` hoạt động thế nào?

`/compact` yêu cầu Codex nén lịch sử hội thoại cũ thành một bản tóm tắt để chat còn tiếp tục được. Nó giảm lượng lịch sử chi tiết phải mang theo; **không** xóa hay sửa file trong repository, không commit Git, và không đổi `AGENTS.md`.

Về mặt vận hành, đây là một lần chuyển đổi context: các quyết định, trạng thái công việc, ràng buộc và kết quả quan trọng cần được giữ ở dạng cô đọng; chi tiết cũ ít liên quan như log dài, output lệnh hoặc các hướng đã loại có thể không còn xuất hiện nguyên văn. Tài liệu Codex xác nhận compact có thể diễn ra khi gọi lệnh hoặc tự động khi chat dài; app-server chỉ công bố sự kiện `contextCompaction`, không công bố một API/UI chuẩn để xem toàn bộ nội dung summary nội bộ.

Do đó, không nên coi `/compact` là cơ chế lưu trữ kiến thức duy nhất. Nội dung phải sống lâu hơn chat cần đặt vào nơi bền vững:

- Quy ước repository: `AGENTS.md` hoặc tài liệu phù hợp dưới `docs/`.
- Quyết định thiết kế: ADR/design doc có link từ tài liệu điều phối.
- Trạng thái code: source, test, `.http`, commit/diff.
- Một quyết định chỉ dùng cho task hiện tại: prompt recap ngắn ở chat.

## Có xem được context hiện tại đang lưu gì không?

Không có màn hình công khai trong IDE hiển thị toàn bộ prompt/context đã nạp hoặc bản tóm tắt nội bộ sau compact. Điều có thể kiểm tra:

1. Gọi `/status` để xem mức sử dụng context của chat (nếu UI phiên hiện tại hiển thị).
2. Đọc lại transcript: message, tool output và file thay đổi đang thấy trong chat là evidence người dùng kiểm tra được.
3. Hỏi Codex tạo một **context ledger** trước hoặc sau compact. Đây là bản tóm tắt do model trả lời, không phải dump context nội bộ.
4. Kiểm tra trực tiếp source of truth: `git diff`, test, `AGENTS.md`, các file docs/contract mà task đang dựa vào.

Prompt đề xuất trước khi compact:

```text
Trước khi compact, hãy lập Context Ledger ngắn, chỉ gồm:
1) mục tiêu và Definition of Done;
2) ràng buộc/authorization/API contract đang áp dụng;
3) quyết định đã chốt và lý do;
4) file đã sửa hoặc cần đọc tiếp;
5) test/evidence đã chạy và kết quả;
6) việc còn lại, rủi ro, câu hỏi mở.
Nêu rõ mục nào là fact từ file/test và mục nào là assumption.
```

Ngay sau compact, kiểm tra khả năng khôi phục bằng prompt:

```text
Hãy tái hiện Context Ledger của task hiện tại theo 6 mục. Không suy đoán: mục nào không chắc phải ghi UNKNOWN. Sau đó đối chiếu với git diff và các file source of truth được nêu.
```

Nếu ledger sau compact thiếu một ràng buộc quan trọng, không cần cố "compact lại". Gửi lại phần thiếu thật ngắn, kèm đường dẫn/file và yêu cầu rõ Codex phải áp dụng nó.

## Cách đánh giá compact đúng hay sai

Không thể chứng minh trực tiếp chất lượng summary nội bộ, nhưng có thể kiểm chứng outcome. Compact được xem là đủ tốt khi chat vẫn trả lời đúng các điểm sau mà không bịa:

- Mục tiêu và Definition of Done không đổi.
- Các guardrail quan trọng còn nguyên: API contract, quyền/ownership, migration/data compatibility, không sửa phạm vi ngoài yêu cầu.
- Nó biết file nào là source of truth và trạng thái thực tế lấy từ Git/test, không lấy từ trí nhớ chat.
- Nó phân biệt decision đã chốt với hypothesis/câu hỏi mở.
- Bước tiếp theo khớp `git diff`, test output và code đang có.

Tín hiệu compact thiếu chất lượng: Codex đề xuất lại hướng đã bị loại, quên requirement bảo mật, nhầm endpoint/file, báo test pass khi chưa chạy, hoặc tiếp tục từ assumption mà không đánh dấu. Khi gặp các dấu hiệu này, hãy nạp lại **fact tối thiểu** (ledger + link/file) thay vì dán lại toàn transcript.

## Quy trình gọn cho task dài

1. Bắt đầu chat với outcome, ràng buộc và verification; dùng `/plan` nếu chưa rõ cách làm.
2. Chỉ nạp file/selection liên quan. Trong IDE, mở đúng file hoặc select đúng đoạn trước khi hỏi; tránh để nhiều tab/log không liên quan trở thành IDE context.
3. Khi hoàn thành một mốc, yêu cầu Context Ledger và ghi quyết định bền vững vào source of truth nếu cần.
4. Chạy `/status`. Nếu context đã cao hoặc chat bắt đầu lặp/quên chi tiết, gọi `/compact` ở ranh giới mốc, không phải giữa một quyết định đang tranh luận.
5. Sau compact, dùng prompt tái hiện ledger và đối chiếu `git diff`/test/source; nạp lại chỉ phần thiếu.
6. Khi công việc thật sự rẽ nhánh, dùng `/fork`; khi cần chat song song có thay đổi file, dùng `/worktree` để tách checkout.

### Nạp context đúng

Ưu tiên một chỉ dẫn nêu mục đích của nguồn thay vì đổ cả repository vào chat:

```text
Đọc `docs/contracts/api-contract.md` để lấy API contract hiện tại và
`src/main/java/.../StreamService.java` để xác minh hành vi. Không đọc các module khác
trừ khi hai nguồn này chỉ ra dependency. Sau đó nêu những fact có thể ảnh hưởng đến sửa đổi.
```

Hãy nạp: requirement hiện hành, file/symbol liên quan, contract, test lỗi, log đã rút gọn và diff đang review. Tránh nạp: toàn bộ repository, log đầy đủ nhiều nghìn dòng, transcript của một chat khác, docs historical không liên quan và nhiều phương án đã loại. Với log, giữ lỗi gốc, stack trace liên quan và lệnh tái hiện.

### Khi nên bắt đầu chat mới thay vì compact

Dùng chat mới khi outcome đã đổi hẳn (ví dụ chuyển từ implement feature sang học một topic khác), khi cần quyền/scope khác, hoặc transcript chứa nhiều nhánh không còn liên quan. Giữ chat cũ khi task vẫn cùng một vấn đề và context quyết định còn hữu ích. `/fork` phù hợp khi cần bảo toàn lịch sử gốc để thử một hướng thay thế.

## Checklist trước khi gọi `/compact`

- [ ] Đã có Context Ledger ngắn.
- [ ] Quyết định bền vững đã nằm trong code/docs/`AGENTS.md`, không chỉ ở chat.
- [ ] Biết các file, diff và test nào là evidence hiện tại.
- [ ] Không đang chờ một câu trả lời then chốt ngay giữa luồng tranh luận.
- [ ] Có thể dùng prompt tái hiện ledger để kiểm tra sau compact.

## Giới hạn cần nhớ

- `/status` cho biết mức sử dụng/trạng thái, không phải bản kê đầy đủ context hay summary nội bộ.
- Compact tối ưu khả năng tiếp tục chat, không bảo đảm giữ từng câu chữ và không thay thế review/test.
- Context tự động từ IDE là context tạm thời theo turn; `AGENTS.md` và tài liệu trong repo mới là hướng dẫn bền vững có thể kiểm tra bằng Git.
- Tính khả dụng của slash command có thể thay đổi; menu `/` trong IDE hiện tại là authority cho phiên của bạn.

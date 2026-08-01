# Context và mô hình chi phí token

## Mental model

Ở mỗi turn, model không chỉ đọc câu hỏi mới. Context hiệu dụng có thể gồm:

```text
system/product instructions
+ global và project AGENTS.md
+ skill/tool/MCP schemas đang khả dụng
+ lịch sử chat hoặc bản compact
+ file, selection và IDE context
+ kết quả tool vừa gọi
+ memory được truy xuất
```

Output gồm câu trả lời nhìn thấy và, tùy model/surface, reasoning output. Vì vậy một prompt người dùng ngắn vẫn có thể tạo request lớn.

## Năm nhóm cần đo

| Nhóm | Nguồn thường gặp | Cách giảm |
| --- | --- | --- |
| Always-on prefix | Root rules, global config, tool schema | Rút gọn root, nested rules, tắt tool/MCP không dùng |
| Lịch sử chat | Quyết định, log, diff và hướng đã loại | Chat theo outcome, context ledger, `/compact` ở milestone |
| Retrieved context | File, search result, MCP response, memory | Query hẹp, giới hạn page/depth/result, chỉ đọc range cần thiết |
| Output | Giải thích, code, báo cáo | Đặt format/độ dài và Definition of Done rõ |
| Reasoning/tool loop | Mức reasoning, số lần thử và validation | Chọn effort theo rủi ro, tránh search/test lặp khi evidence không đổi |

## Cached input không đồng nghĩa token miễn phí

Prefix lặp lại có thể được tính là cached input và thường rẻ hơn uncached input, nhưng nó vẫn chiếm context window và có thể ảnh hưởng usage/latency theo surface. Khi tối ưu, theo dõi riêng:

- `input_tokens`
- `cached_input_tokens`
- `output_tokens`
- `reasoning_output_tokens`

Nếu tổng input cao nhưng phần lớn được cache, ưu tiên xử lý context rot và context window trước khi kết luận chi phí tiền tăng tương ứng. Nếu uncached input cao, tìm file/log/tool response bị nạp lặp.

## Compact giải quyết gì

`/compact` thay lịch sử chi tiết cũ bằng summary để cùng một chat tiếp tục. Nó không:

- làm ngắn `AGENTS.md` hoặc tool schema;
- ghi quyết định vào repository;
- bảo đảm giữ từng chi tiết hay log nguyên văn;
- thay test, Git diff hoặc source of truth.

Trước compact, durable fact phải nằm trong code, test, ADR hoặc checkpoint. Summary chỉ giữ context điều phối của chat.

## Memory giải quyết gì

Memory có lợi khi nó thay lượng lịch sử/phần giải thích phải lặp bằng một tập fact liên quan nhỏ hơn:

```text
lợi ích ròng ≈ context không phải gửi lại
              - chi phí ghi/tổng hợp memory
              - schema và tool call retrieval
              - memory result được chèn vào prompt
```

Nếu chat vốn ngắn và repository đã có checkpoint tốt, memory có thể không tiết kiệm đáng kể. Nếu memory truy xuất quá rộng hoặc sai, nó vừa tốn token vừa giảm chất lượng.

## Tín hiệu context đang có vấn đề

- Agent hỏi lại requirement đã chốt hoặc áp dụng một rule của task cũ.
- Search, đọc file và chạy test lặp dù evidence không đổi.
- Tool trả hàng nghìn dòng nhưng chỉ vài dòng được dùng.
- Agent nhầm chat history với current code/contract.
- Cùng prompt nhỏ nhưng uncached input tăng đều theo tuổi chat.

Khi đó, ưu tiên source routing, chat mới hoặc compact; không mặc định cài thêm memory MCP.

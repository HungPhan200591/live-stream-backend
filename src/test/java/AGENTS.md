# Rule cho test Java

## Phạm vi

Áp dụng cho mọi file dưới `src/test/java/`. Đọc cùng `AGENTS.md` ở repository root và contract của hành vi đang test.

## Chất lượng test

- Test hành vi và invariant quan sát được, không khóa chặt implementation detail không cần thiết.
- Ưu tiên unit test cho business branch; dùng MockMvc/integration test cho HTTP status, payload, validation và authorization.
- Mỗi thay đổi security phải có negative test cho anonymous, sai role hoặc sai ownership phù hợp.
- Với concurrency, cache, transaction hoặc message consumer, thêm failure/retry/idempotency case theo rủi ro.
- Test phải deterministic: kiểm soát clock, dữ liệu, async boundary và state dùng chung; không phụ thuộc service cục bộ còn sót từ lần chạy trước nếu không phải integration test có chủ đích.
- Không làm test pass bằng cách nới assertion, tắt validation/security hoặc catch exception quá rộng.

## Chạy test

- Chạy test class liên quan trước: `.\mvnw.cmd -Dtest=ClassName test`.
- Chạy full suite khi thay đổi có phạm vi rộng hoặc trước khi kết luận không có regression.
- Báo rõ test nào không chạy và dependency hạ tầng nào cần thiết.

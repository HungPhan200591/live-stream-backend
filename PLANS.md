# Kế hoạch triển khai (Execution Plan)

Dùng Execution Plan khi thay đổi đi qua nhiều lớp hoặc có rủi ro migration, bảo mật, concurrency, cache consistency hay API compatibility. Thay đổi nhỏ và độc lập không cần plan.

Plan là tài liệu sống. Giữ ngắn gọn, cập nhật trạng thái khi làm và ghi nhận phát hiện làm đổi cách tiếp cận. Không biến thông tin đã biết thành câu hỏi mở.

## Cấu trúc bắt buộc

```markdown
# <Tiêu đề hướng đến kết quả>

## Mục tiêu
Kết quả người dùng nhận được và các phần không thực hiện.

## Hành vi hiện tại
Code path, hợp đồng và bằng chứng liên quan.

## Quyết định và rủi ro
Tương thích dữ liệu/API, authorization, transaction, cache, xử lý bất đồng bộ và rollback.

## Các bước
- [ ] Một bước triển khai nhỏ, có thể kiểm chứng
- [ ] Test và tài liệu

## Kiểm chứng
Lệnh cụ thể và các kịch bản quan trọng.

## Ghi chú tiến độ
Phát hiện ngoài dự kiến, quyết định thay đổi và việc còn lại.
```

## Quy trình

1. Lần theo request path hiện tại từ controller tới service, repository, cache/message infrastructure và test.
2. Chỉ đọc tài liệu nghiệp vụ, API, authorization, phase, Redis hoặc webhook phù hợp được liệt kê trong `AGENTS.md`.
3. Xác định hành vi và các case lỗi trước khi sửa, bao gồm authorization và concurrency boundary.
4. Triển khai theo vertical slice để code vẫn compile được ở mỗi bước.
5. Thêm test tập trung, chạy kiểm tra hẹp nhất rồi mở rộng khi phù hợp.
6. Cập nhật OpenAPI, `.http`, phase hoặc contract document khi hành vi thay đổi.
7. Đọc lại diff để phát hiện thay đổi ngoài phạm vi, lộ secret, thiếu invalidation hoặc failure chưa xử lý.

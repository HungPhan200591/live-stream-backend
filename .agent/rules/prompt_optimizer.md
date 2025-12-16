---
description: Giúp tối ưu hóa prompt nháp của người dùng để đạt kết quả tốt nhất.
---

1.  **Phân tích (Analyze)**: Đọc prompt nháp của người dùng để hiểu Mục đích (Intent), Ngữ cảnh (Context) và Các ràng buộc (Constraints).
2.  **Tối ưu hóa (Optimize)**: Viết lại prompt sử dụng framework **CO-STAR** (Context, Objective, Style, Tone, Audience, Response) hoặc cấu trúc tương tự để đảm bảo tính rõ ràng và đầy đủ.
    - _Lưu ý: Giữ nguyên ngôn ngữ mà người dùng mong muốn cho output (hiện tại tôi luôn dùng tiếng Việt)._
3.  **Trình bày (Present)**: Hiển thị **Prompt Đã Tối Ưu** (Optimized Prompt) trong một code block để người dùng dễ copy hoặc review. Giải thích ngắn gọn các thay đổi cải tiến.
4.  **Review**: Hỏi người dùng: "Bạn có hài lòng với prompt này không? Bạn muốn sửa gì thêm hay để tôi thực thi luôn?"
5.  **Thực thi (Execute)**:
    - Nếu người dùng đồng ý: Thực thi prompt đã tối ưu.
    - Nếu người dùng chỉnh sửa: Cập nhật prompt và quay lại bước thực thi hoặc tối ưu lại nếu cần.

# Development Data Initialization

> Trạng thái: `CURRENT DEV UTILITY`<br>
> Cập nhật: 2026-07-25

`DataInitializer` tạo ba role và ba development user khi `app.data.init-default-data=true`. Mặc định property là `false`.

## Chạy một lần trên PowerShell

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--app.data.init-default-data=true"
```

Hoặc đặt environment variable chỉ trong terminal hiện tại:

```powershell
$env:APP_DATA_INIT_DEFAULT_DATA='true'
.\mvnw.cmd spring-boot:run
```

## Dữ liệu development

| Username | Role | Default password |
| --- | --- | --- |
| `admin` | `ROLE_ADMIN` | `Password123!` |
| `streamer001` | `ROLE_STREAMER` | `Password123!` |
| `user001` | `ROLE_USER` | `Password123!` |

Đây là credential local development. Stage 0 phải bảo đảm initializer và default credential không tồn tại trong production context.

## Safety

- Initializer không phải migration tool và không thay Flyway.
- Không xóa/truncate schema chỉ để initializer chạy lại nếu chưa xác minh đúng local database và có chủ đích reset dữ liệu.
- Với test tự động, dùng deterministic fixture/Testcontainers thay vì phụ thuộc seed local.
- Sau lần chạy, bỏ environment variable hoặc đóng terminal; property trong repository vẫn giữ `false`.

## Troubleshooting

- Không chạy: kiểm tra đúng property `app.data.init-default-data=true` và application log.
- User đã tồn tại: initializer hiện không phải reset workflow; giữ data hoặc chủ động chuẩn bị database test riêng.
- Sai role/password: kiểm tra `DataInitializer.java`; code là bằng chứng current behavior.

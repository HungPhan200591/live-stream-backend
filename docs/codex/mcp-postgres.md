# MCP PostgreSQL cho Codex

Repository này cấu hình sẵn PostgreSQL MCP server cho Codex tại `.codex/config.toml`.

## Thông tin kết nối

- Host: `localhost`
- Port: `15432`
- Database: `livestream`
- Username: `admin`
- Password: `password`
- MCP server: `@modelcontextprotocol/server-postgres`

Đây là credential của môi trường demo cục bộ và được commit cùng repository theo quy ước dự án.

## Khởi động hạ tầng

```powershell
docker-compose up -d postgres
```

## Sử dụng trong Codex

Codex tự nạp `.codex/config.toml` khi repository được đánh dấu là trusted.

- ChatGPT desktop app: mở **Settings → MCP servers**, sau đó restart ứng dụng hoặc mở thread mới.
- Codex CLI: chạy `codex mcp list` để kiểm tra; trong phiên tương tác dùng `/mcp` để xem server đang kết nối.
- Codex IDE extension: mở **MCP servers**, lưu và restart extension nếu server chưa xuất hiện.

Tên server là `livestream_postgres`. Sau khi kết nối, có thể yêu cầu Codex đọc schema hoặc truy vấn dữ liệu, ví dụ:

```text
Cho tôi xem schema bảng users.
Liệt kê 10 stream mới nhất.
Kiểm tra các role hiện có trong database.
```

## Xử lý sự cố

1. Kiểm tra PostgreSQL container đang chạy: `docker-compose ps postgres`.
2. Kiểm tra Node.js/NPM: `node --version` và `npm --version`.
3. Xem danh sách server: `codex mcp list`.
4. Restart Codex sau khi thay đổi `.codex/config.toml`.


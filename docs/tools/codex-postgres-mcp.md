# MCP PostgreSQL cho Codex

Repository cấu hình PostgreSQL MCP server cho Codex tại `.codex/config.toml`.

## Thông tin kết nối

- Host: `localhost`
- Port: `15432`
- Database: `livestream`
- Username: `admin`
- Password: `password`
- MCP server: `@modelcontextprotocol/server-postgres`
- Tool allowlist: `query`

Đây là credential của môi trường demo cục bộ và được commit cùng repository theo quy ước dự án.

## Khởi động hạ tầng

```powershell
docker compose up -d postgres
```

Không cần khởi động lại nếu `docker compose ps postgres` đã báo container đang chạy.

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

1. Render cấu hình Compose: `docker compose config`.
2. Kiểm tra PostgreSQL container đang chạy: `docker compose ps postgres`.
3. Kiểm tra database sẵn sàng: `docker compose exec -T postgres pg_isready -U admin -d livestream`.
4. Kiểm tra Node.js/NPM: `node --version` và `npm --version`.
5. Xem danh sách server: `codex mcp list`.
6. Restart Codex/IDE extension sau khi thay đổi `.codex/config.toml`.

## Trạng thái compatibility

Smoke test ngày 2026-08-01 xác nhận MCP kết nối qua host port `15432`, expose tool `query` và trả về đúng database `livestream`, user `admin`.

Package `@modelcontextprotocol/server-postgres@0.6.2` đồng thời phát cảnh báo deprecated/no longer supported. Cấu hình hiện vẫn hoạt động cho local read-only diagnostics, nhưng không nên xem đây là dependency dài hạn. Khi thay server, phải giữ các điều kiện:

- kết nối đúng Compose host port `15432`;
- credential local tách khỏi production;
- read-only hoặc allowlist query an toàn;
- smoke test schema/query và cập nhật tài liệu trong cùng change.

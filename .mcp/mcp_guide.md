# MCP PostgreSQL Server Configuration

## Giới thiệu

File này chứa cấu hình MCP (Model Context Protocol) server custom để kết nối đến PostgreSQL database của dự án Livestream Backend.

## Thông tin kết nối

Thông tin database được lấy từ `docker-compose.yml`:

- **Host**: `localhost`
- **Port**: `15432` (mapped từ container port 5432)
- **Database**: `livestream`
- **Username**: `admin`
- **Password**: `password`

## Cách sử dụng

### 1. Cấu hình trong Gemini Code Assist

Để sử dụng MCP server này trong Gemini Code Assist, bạn cần thêm cấu hình vào settings:

**Windows**: `C:\Users\Admin\AppData\Roaming\Gemini\settings.json`

Thêm nội dung từ file `server-config.json` vào phần `mcpServers`:

```json
{
  "mcpServers": {
    "livestream-postgres": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-postgres",
        "postgresql://admin:password@localhost:15432/livestream"
      ],
      "env": {
        "PGHOST": "localhost",
        "PGPORT": "15432",
        "PGDATABASE": "livestream",
        "PGUSER": "admin",
        "PGPASSWORD": "password"
      }
    }
  }
}
```

### 2. Khởi động Database

Đảm bảo PostgreSQL container đang chạy:

```bash
# Từ thư mục gốc dự án
docker-compose up -d postgres
```

### 3. Kiểm tra kết nối

Sau khi restart Gemini Code Assist, bạn có thể test kết nối bằng cách:

```bash
# Test kết nối PostgreSQL
docker exec -it livestream-postgres psql -U admin -d livestream
```

### 4. Sử dụng trong Chat

Khi MCP server đã được cấu hình, bạn có thể:

- Query database trực tiếp từ chat
- Yêu cầu AI phân tích schema
- Tự động generate code dựa trên structure database

**Ví dụ câu lệnh**:
- "Show me all tables in the database"
- "What is the schema of users table?"
- "Query top 10 users by created_at"

## Lưu ý bảo mật

> [!WARNING]
> File cấu hình này chứa thông tin nhạy cảm (password). 
> - **KHÔNG** commit file này vào Git
> - Đảm bảo `.mcp/` đã được thêm vào `.gitignore`

## Troubleshooting

### Không kết nối được database

1. Kiểm tra PostgreSQL container đang chạy:
   ```bash
   docker ps | grep livestream-postgres
   ```

2. Kiểm tra port 15432 đang lắng nghe:
   ```bash
   netstat -an | findstr 15432
   ```

3. Test kết nối thủ công:
   ```bash
   psql -h localhost -p 15432 -U admin -d livestream
   ```

### MCP server không start

1. Kiểm tra Node.js đã được cài đặt:
   ```bash
   node --version
   npm --version
   ```

2. Clear npx cache:
   ```bash
   npx clear-npx-cache
   ```

3. Xem logs trong Gemini Code Assist console (Ctrl+Shift+I)

## Tham khảo

- [Model Context Protocol](https://modelcontextprotocol.io/)
- [MCP PostgreSQL Server](https://github.com/modelcontextprotocol/servers/tree/main/src/postgres)

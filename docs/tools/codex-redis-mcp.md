# MCP Redis cho Codex

Repository cấu hình Redis MCP server tại `.codex/config.toml` để đọc và chẩn đoán Redis cục bộ.

## Kết nối hiện tại

- Redis Compose service: `redis`
- Host/port từ máy phát triển: `localhost:16379`
- Logical database: `0`
- MCP implementation: [redis/mcp-redis](https://github.com/redis/mcp-redis)
- Runtime: `uvx`

`docker-compose.yml` map `16379:6379`; `application.yml` cũng dùng `localhost:16379`, nên application và MCP cùng quan sát một Redis instance.

## Compatibility pin

Tại ngày 2026-08-01, `redis-mcp-server==0.5.0` chạy lỗi nếu resolver lấy MCP Python 2.x vì package vẫn import `mcp.server.fastmcp`. Config pin:

```text
redis-mcp-server==0.5.0
mcp[cli]>=1.0.0,<2.0.0
```

Pin đã được kiểm chứng bằng MCP `initialize` và `tools/list`. Khi nâng version, bỏ pin chỉ sau khi chạy lại cùng smoke test.

## Tool boundary

Server đầy đủ expose 47 tool, gồm cả write/delete. Project chỉ allowlist tool đọc/quan sát:

```text
get, hget, hgetall, hexists, json_get,
lrange, llen, smembers, zrange, xrange,
type, scan_keys, dbsize, info
```

Các tool như `set`, `delete`, `hdel`, `json_del`, `xdel`, `expire`, `rename` không được expose cho Codex. Đây là boundary cấu hình, bổ sung cho rule không phá hủy dữ liệu trong `AGENTS.md`.

## Kiểm tra

```powershell
docker compose ps redis
docker compose exec -T redis redis-cli PING
uvx --with "mcp[cli]>=1.0.0,<2.0.0" --from redis-mcp-server==0.5.0 redis-mcp-server --help
codex mcp list
```

Kết quả kỳ vọng: container `livestream-redis` đang chạy, `PING` trả `PONG`, và `livestream_redis` hiện trong danh sách MCP sau khi restart Codex/IDE extension.

## Giới hạn

- Đây là Redis local không có authentication; không dùng cấu hình này cho production.
- `scan_keys` phải dùng pattern hẹp và result nhỏ để tránh context bloat.
- Không mở lại write tool chỉ để tiện debug. Nếu task cần write, yêu cầu phải nêu rõ target, dữ liệu phục hồi và approval; ưu tiên test fixture hoặc `redis-cli` command có phạm vi kiểm soát.
- Với môi trường chia sẻ/production, dùng Redis ACL read-only ngoài MCP allowlist.

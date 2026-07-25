# Redis Usage Guide

> Trạng thái: `CURRENT IMPLEMENTATION + LEARNING TARGETS`<br>
> Cập nhật: 2026-07-25<br>
> Bản hướng dẫn 2025 được lưu tại [archive](../archive/2025-reference/redis-usage-guide.md).

## 1. Vai trò của Redis

Redis là derived/ephemeral state. PostgreSQL vẫn là nguồn tin cậy cho session durable state và mọi invariant tiền tệ. Khi Redis down hoặc mất dữ liệu, hệ thống phải có behavior được quyết định trước thay vì âm thầm trả dữ liệu sai.

## 2. Key catalog hiện tại

| Key | Kiểu | Giá trị | TTL | Owner/invalidation | Trạng thái |
| --- | --- | --- | --- | --- | --- |
| `session:v1:{sessionId}` | String JSON | `SessionCacheDTO` | Thời gian còn lại của session | `SessionCacheService`; xóa khi revoke một session | Có code; logout-all invalidation còn thiếu |
| `stream:{streamId}:live` | String | `true` | 24 giờ | `LiveStreamCacheService`; xóa khi stream end | Có code |
| `stream:{streamId}:viewers` | HyperLogLog | viewer identity | Chưa có TTL | Reset khi stream end | Có code; cần đánh giá leak khi crash |
| `test:ping` | String | `pong` | 10 giây | Test endpoint | Dev/test only |

## 3. Semantics cần hiểu đúng

- HyperLogLog cho `unique reach` xấp xỉ, không phải concurrent viewer count.
- Live-status cache không tự chứng minh DB đang live; phải xác định fallback/cache-rebuild policy.
- Session cache là security-sensitive: cache hit không được vượt qua durable revoke state.
- TTL không thay thế invalidation; invalidation không thay thế rebuild/recovery strategy.

## 4. Quy tắc thêm key

Mỗi key mới phải ghi:

1. Namespace và version.
2. Data type và serializer.
3. Cardinality dự kiến.
4. TTL và jitter nếu cần.
5. Writer, reader và invalidation owner.
6. Source of truth và rebuild method.
7. Behavior khi timeout/down.
8. Metrics: hit/miss/error/latency hoặc cardinality phù hợp.

DTO cache có typed `RedisTemplate` riêng khi schema không phải string đơn giản. Không dùng một generic object template rồi cast ở caller.

## 5. Learning cases

- SEC-02: logout-all và stale session cache.
- TX-01: DB commit so với Redis side effect.
- RED-01: cache stampede, TTL jitter, single-flight và stale-while-revalidate.
- Stage 4: ZSET heartbeat cho current viewers, rate limit bằng Lua và degraded mode.

## 6. Safety và debugging

- Dùng `SCAN` có giới hạn thay vì `KEYS *` trên dataset lớn.
- Kiểm tra `TYPE`, `TTL/PTTL`, memory và serializer trước khi xóa key.
- Không chạy `FLUSHALL`, `FLUSHDB` hoặc xóa volume nếu chưa xác minh đúng local target và có yêu cầu rõ.
- Không log toàn bộ session/token cache payload.

## 7. Verification

- Serializer round-trip test.
- TTL boundary test với clock có kiểm soát.
- Cache hit/miss/invalidation integration test.
- Redis unavailable/timeout test và recovery verification.
- Security test chứng minh revoked session không hợp lệ dù cache từng chứa `ACTIVE`.

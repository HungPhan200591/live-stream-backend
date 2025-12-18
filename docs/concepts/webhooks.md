# Webhooks: Khái niệm và Cách sử dụng

> **Mục đích**: Tài liệu kiến thức về Webhooks cho AI Agent và developers  
> **Target Audience**: AI Agent, Backend Developers  
> **Áp dụng trong project**: RTMP Server callbacks khi stream start/end

---

## 1. Webhook là gì?

### Định nghĩa đơn giản
**Webhook** = Một **API endpoint** trên server của bạn mà **external service** sẽ **tự động gọi** khi có sự kiện xảy ra.

### So sánh với API thông thường

| Aspect          | API thông thường                  | Webhook                                |
| --------------- | --------------------------------- | -------------------------------------- |
| **Ai gọi**      | Client/Frontend của bạn          | External service (RTMP, Payment, etc)  |
| **Khi nào**     | User action (click button)       | Event xảy ra (stream started, paid)    |
| **Direction**   | Client → Server                  | External Server → Your Server          |
| **Auth**        | JWT Token (user identity)        | Secret Key (service identity)          |

### Ví dụ thực tế

```
┌─────────────────┐                      ┌─────────────────┐
│   RTMP Server   │                      │  Your Backend   │
│   (External)    │                      │                 │
│                 │   Stream detected!   │                 │
│    Detects      │ ──────────────────►  │   /api/webhooks │
│    OBS Live     │   POST with JSON     │   /rtmp/started │
│                 │                      │                 │
└─────────────────┘                      └─────────────────┘
```

---

## 2. Tại sao cần Webhook?

### Use Case: Livestream Platform

**Không có Webhook (SAI)**:
```
1. User nhấn "Go Live" trên web app
2. Backend set isLive=true
3. ❌ VẤN ĐỀ: OBS chưa thực sự stream!
   - User có thể nhấn nút nhưng OBS chưa connect
   - Backend nghĩ stream live nhưng thực tế không có
```

**Có Webhook (ĐÚNG)**:
```
1. User nhấn "Start Streaming" trong OBS
2. OBS connect tới RTMP server
3. RTMP server detect stream thực sự đang live
4. RTMP server gọi webhook: POST /api/webhooks/rtmp/stream-started
5. ✅ Backend biết CHẮC CHẮN stream đang live
```

---

## 3. Webhook trong project Livestream

### Flow Diagram

```
sequenceDiagram
    participant OBS as 📹 OBS Studio
    participant RTMP as 📡 RTMP Server
    participant Backend as ⚙️ Backend API
    participant DB as 💾 PostgreSQL

    OBS->>RTMP: Connect với streamKey
    RTMP->>RTMP: Detect stream live
    RTMP->>Backend: POST /api/webhooks/rtmp/stream-started
    Note right of Backend: {streamKey: "abc123"}
    Backend->>DB: UPDATE isLive=true
    Backend-->>RTMP: 200 OK
    
    Note over OBS,DB: ... streaming ...
    
    OBS->>RTMP: Disconnect
    RTMP->>Backend: POST /api/webhooks/rtmp/stream-ended
    Backend->>DB: UPDATE isLive=false
```

### Webhook Endpoints

| Endpoint                              | Method | Caller      | Purpose                    |
| ------------------------------------- | ------ | ----------- | -------------------------- |
| `/api/webhooks/rtmp/stream-started`   | POST   | RTMP Server | Stream bắt đầu live        |
| `/api/webhooks/rtmp/stream-ended`     | POST   | RTMP Server | Stream kết thúc            |

### Request Format

```json
// RTMP Server gửi khi stream start
POST /api/webhooks/rtmp/stream-started
Content-Type: application/json
X-Webhook-Secret: your-secret-key

{
  "streamKey": "abc123xyz",
  "timestamp": "2025-12-18T21:00:00Z"
}
```

---

## 4. Security cho Webhook

### Vấn đề
Webhook endpoint là **public** (external service cần gọi được). Làm sao biết request thực sự từ RTMP server, không phải attacker?

### Giải pháp: Secret Key Verification

```java
@PostMapping("/rtmp/stream-started")
public ApiResponse<Void> handleStreamStarted(
        @RequestHeader("X-Webhook-Secret") String secret,
        @RequestBody RtmpWebhookRequest request) {
    
    // Verify secret
    if (!secret.equals(expectedRtmpSecret)) {
        throw new UnauthorizedException("Invalid webhook secret");
    }
    
    // Process webhook
    streamService.startStreamByKey(request.getStreamKey());
    return ApiResponse.success(null);
}
```

### Các phương pháp bảo mật khác

| Method              | Mô tả                                    | Complexity |
| ------------------- | ---------------------------------------- | ---------- |
| **Secret Header**   | X-Webhook-Secret trong header            | ⭐ Simple  |
| **HMAC Signature**  | Sign payload với shared secret           | ⭐⭐ Medium |
| **IP Whitelist**    | Chỉ cho phép IP của RTMP server          | ⭐ Simple  |
| **mTLS**            | Mutual TLS certificate verification      | ⭐⭐⭐ Complex |

**Recommendation cho project này**: Secret Header (đủ cho MVP)

---

## 5. Webhook vs User API

### Khi nào dùng Webhook?

| Scenario                                | Webhook | User API |
| --------------------------------------- | ------- | -------- |
| RTMP server thông báo stream live       | ✅      | ❌       |
| Payment gateway confirm transaction     | ✅      | ❌       |
| CI/CD pipeline notify build status      | ✅      | ❌       |
| User nhấn nút "Create Stream"           | ❌      | ✅       |
| User cập nhật profile                   | ❌      | ✅       |

### Rule of Thumb
- **External service trigger event** → Webhook
- **User action từ your app** → User API

---

## 6. Development Testing

### Không có RTMP Server thực?

Dev có thể test webhook bằng cách gọi trực tiếp:

```http
### Giả lập RTMP server gọi webhook
POST {{host}}/api/webhooks/rtmp/stream-started
Content-Type: application/json
X-Webhook-Secret: dev-secret-key

{
  "streamKey": "abc123xyz"
}
```

**Đây chính xác là những gì SimulationController làm**, nhưng bây giờ dev gọi trực tiếp webhook endpoint thay vì qua simulation layer không cần thiết.

---

## 7. Best Practices

### DO ✅
- Verify webhook source (secret key, signature)
- Return 200 OK nhanh nhất có thể
- Process async nếu logic phức tạp
- Log webhook calls để debug
- Handle idempotency (cùng event gọi 2 lần)

### DON'T ❌
- Đừng expose webhook không có authentication
- Đừng block response waiting for heavy processing
- Đừng assume webhook chỉ gọi 1 lần
- Đừng return sensitive data trong response

---

## 8. Implement Checklist

Khi implement webhook mới trong project:

```markdown
- [ ] Tạo DTO cho webhook request
- [ ] Tạo endpoint trong controller
- [ ] Add secret verification
- [ ] Update SecurityConfig (permitAll cho webhook path)
- [ ] Tạo HTTP test file
- [ ] Document trong api_endpoints_specification.md
```

---

**End of Document**

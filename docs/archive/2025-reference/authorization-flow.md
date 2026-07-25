# Authorization Flow - Livestream Backend

> **ARCHIVED 2026-07-25** — Trộn implementation hiện tại với money/action-token target. Dùng [Security Flow](../../security/authorization-flow.md).

> **Phiên bản**: Final (17/12/2024)
> **Kiến trúc**: JWT + Session-backed Refresh Token
> **Phù hợp**: Hệ thống Livestream có Donate/Withdraw

---

## Table of Contents

1. [Nguyên Tắc Cốt Lõi](#1-nguyên-tắc-cốt-lõi)
2. [Kiến Trúc 3 Tầng](#2-kiến-trúc-3-tầng)
3. [Database Schema](#3-database-schema)
4. [Flow Chi Tiết](#4-flow-chi-tiết)
5. [Action Token (Money Flow)](#5-action-token-money-flow)
6. [Redis Usage](#6-redis-usage)
7. [Security Matrix](#7-security-matrix)

---

## 1. Nguyên Tắc Cốt Lõi

```
┌─────────────────────────────────────────────────────────────────────┐
│                         NGUYÊN TẮC VÀNG                             │
├─────────────────────────────────────────────────────────────────────┤
│  1. SESSION LÀ NGUỒN SỰ THẬT    →  DB lưu session, không trust JWT  │
│  2. JWT CHỈ LÀ CARRIER          →  Mang session_id đi xa            │
│  3. TIỀN DÙNG ACTION TOKEN      →  One-time, Redis, 60s TTL         │
│  4. KHÔNG BLOCKLIST TOKEN       →  Revoke session, không revoke JWT │
└─────────────────────────────────────────────────────────────────────┘
```

> ❝ Big tech không "trust token", họ trust server-side state ❞

---

## 2. Kiến Trúc 3 Tầng

```mermaid
flowchart TD
    subgraph Tokens
        AT[Access Token<br/>JWT, 15 phút<br/>Stateless]
        RT[Refresh Token<br/>JWT, 30 ngày<br/>Chứa session_id]
        ActionT[Action Token<br/>Redis, 60 giây<br/>One-time]
    end

    subgraph Storage
        DB[(Database<br/>user_sessions<br/>SOURCE OF TRUTH)]
        Redis[(Redis Cache<br/>Optional<br/>Hot-path)]
    end

    RT -->|Check session| DB
    DB -.->|Cache| Redis
    ActionT -->|Store| Redis

    style AT fill:#2ecc71,color:#fff
    style RT fill:#3498db,color:#fff
    style ActionT fill:#e74c3c,color:#fff
    style DB fill:#9b59b6,color:#fff
    style Redis fill:#f39c12,color:#fff
```

### Chi tiết từng tầng

| Tầng  | Thành phần    | Đặc điểm                             | Dùng cho              |
| ----- | ------------- | ------------------------------------ | --------------------- |
| **1** | Access Token  | JWT, 15m, stateless, KHÔNG revoke    | View, Chat, WebSocket |
| **2** | Refresh Token | JWT, 30d, chứa session_id → check DB | Lấy AT mới            |
| **3** | Action Token  | Redis, 60s, one-time                 | Donate, Withdraw      |

---

## 3. Database Schema

### Bảng `user_sessions`

```sql
CREATE TABLE user_sessions (
    session_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         BIGINT NOT NULL,
    device_id       VARCHAR(255),
    device_name     VARCHAR(255),
    ip_address      VARCHAR(45),
    status          VARCHAR(20) DEFAULT 'ACTIVE',  -- ACTIVE, REVOKED
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_used_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at      TIMESTAMP NOT NULL,

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_sessions_user_id ON user_sessions(user_id);
CREATE INDEX idx_sessions_status ON user_sessions(status);
CREATE INDEX idx_sessions_expires ON user_sessions(expires_at);
```

### Entity Java

```java
@Entity
@Table(name = "user_sessions")
@Data
@Builder
public class UserSession {
    @Id
    private UUID sessionId;

    @Column(nullable = false)
    private Long userId;

    private String deviceId;
    private String deviceName;
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    private SessionStatus status = SessionStatus.ACTIVE;

    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;
    private LocalDateTime expiresAt;

    public enum SessionStatus {
        ACTIVE, REVOKED
    }

    public boolean isValid() {
        return status == SessionStatus.ACTIVE
            && expiresAt.isAfter(LocalDateTime.now());
    }
}
```

---

## 4. Flow Chi Tiết

### 4.1 LOGIN

```mermaid
sequenceDiagram
    participant Client
    participant API
    participant DB
    participant Redis

    Client->>API: POST /api/auth/login<br/>{username, password}
    API->>DB: Validate credentials
    DB-->>API: User valid ✅

    API->>DB: INSERT user_sessions<br/>(session_id, user_id, status=ACTIVE)

    opt Có Redis Cache
        API->>Redis: SET session:{session_id} = ACTIVE<br/>TTL = 30 days
    end

    API->>API: Generate Access Token (15m)<br/>Generate Refresh Token (30d, chứa session_id)

    API-->>Client: 200 OK<br/>{accessToken, refreshToken}

    Note over Client: Lưu RT trong httpOnly cookie<br/>Lưu AT trong memory
```

**Refresh Token chứa:**

```json
{
  "sub": "user_123",
  "session_id": "550e8400-e29b-41d4-a716-446655440000",
  "device_id": "browser_chrome_win",
  "exp": 1705420800
}
```

---

### 4.2 REFRESH

```mermaid
sequenceDiagram
    participant Client
    participant API
    participant Redis
    participant DB

    Note over Client: Access Token hết hạn

    Client->>API: POST /api/auth/refresh<br/>{refreshToken}
    API->>API: Verify RT signature + expiry

    alt RT invalid
        API-->>Client: 401 Unauthorized
    else RT valid
        API->>API: Extract session_id từ RT

        alt Có Redis Cache
            API->>Redis: GET session:{session_id}
            alt Cache hit
                Redis-->>API: status = ACTIVE/REVOKED
            else Cache miss
                API->>DB: SELECT * FROM user_sessions
                DB-->>API: Session data
                API->>Redis: SET session:{session_id}
            end
        else Không có Redis
            API->>DB: SELECT * FROM user_sessions
            DB-->>API: Session data
        end

        alt Session REVOKED hoặc expired
            API-->>Client: 401 Unauthorized<br/>"Session revoked"
        else Session ACTIVE
            API->>DB: UPDATE last_used_at
            API->>API: Generate NEW Access Token (15m)
            API-->>Client: 200 OK<br/>{accessToken, SAME refreshToken}
            Note over API: ❌ KHÔNG cấp RT mới
        end
    end
```

---

### 4.3 LOGOUT

```mermaid
sequenceDiagram
    participant Client
    participant API
    participant DB
    participant Redis

    Client->>API: POST /api/auth/logout<br/>Authorization: Bearer {accessToken}<br/>Cookie: refreshToken

    API->>API: Extract session_id từ RT
    API->>DB: UPDATE user_sessions<br/>SET status = 'REVOKED'<br/>WHERE session_id = ?

    opt Có Redis Cache
        API->>Redis: DEL session:{session_id}
    end

    API-->>Client: 200 OK "Logged out"

    Note over Client: Xóa tokens ở client

    Note over Client,Redis: SAU LOGOUT
    Client->>API: POST /api/auth/refresh<br/>{old refreshToken}
    API->>API: Extract session_id
    API->>DB: SELECT status
    DB-->>API: status = REVOKED
    API-->>Client: 401 Unauthorized
    Note over API: RT tự chết dù còn hạn!
```

---

### 4.4 LOGOUT ALL DEVICES

```mermaid
sequenceDiagram
    participant Client
    participant API
    participant DB
    participant Redis

    Client->>API: POST /api/auth/logout-all<br/>Authorization: Bearer {accessToken}

    API->>API: Extract user_id từ AT
    API->>DB: UPDATE user_sessions<br/>SET status = 'REVOKED'<br/>WHERE user_id = ?

    opt Có Redis Cache
        API->>Redis: DEL session:* cho user này
    end

    API-->>Client: 200 OK "All sessions revoked"

    Note over Client,Redis: Tất cả devices bị logout
```

---

## 5. Action Token (Money Flow)

### 5.1 Tại sao cần Action Token?

| Vấn đề                   | Giải pháp                     |
| ------------------------ | ----------------------------- |
| AT/RT leak → mất tiền?   | ❌ Không đủ, cần Action Token |
| Replay attack donate     | Action Token one-time         |
| Withdraw cần bảo mật cao | Action Token + OTP + 2FA      |

---

### 5.2 DONATE Flow

```mermaid
sequenceDiagram
    participant Client
    participant API
    participant Redis
    participant DB

    Note over Client,DB: Bước 1: Prepare
    Client->>API: POST /payments/prepare<br/>{streamerId, amount}<br/>Authorization: Bearer {AT}

    API->>API: Validate user balance

    alt Amount < threshold
        API->>Redis: SET action:donate:{uuid}<br/>{userId, amount}<br/>TTL = 60s
        API-->>Client: 200 {actionToken, requireOTP: false}
    else Amount >= threshold
        API-->>Client: 200 {requireOTP: true}
        Client->>API: POST /auth/verify-otp<br/>{otpCode}
        API->>Redis: SET action:donate:{uuid}<br/>TTL = 60s
        API-->>Client: 200 {actionToken}
    end

    Note over Client,DB: Bước 2: Execute
    Client->>API: POST /payments/donate<br/>{actionToken}<br/>Authorization: Bearer {AT}

    API->>Redis: GET action:donate:{uuid}
    alt Token exists
        Redis-->>API: {userId, amount}
        API->>Redis: DEL action:donate:{uuid}
        Note over API: One-time! Dùng xong xóa
        API->>DB: Process transaction
        API-->>Client: 200 {success}
    else Token not found / expired
        API-->>Client: 400 "Invalid action token"
    end
```

---

### 5.3 WITHDRAW Flow

```mermaid
sequenceDiagram
    participant Client
    participant API
    participant Redis
    participant DB

    Note over Client,DB: Bước 1: Prepare (bắt buộc 2FA + OTP)
    Client->>API: POST /withdraw/prepare<br/>{amount}<br/>Authorization: Bearer {AT}

    API->>DB: Check KYC status
    alt KYC not verified
        API-->>Client: 403 "KYC required"
    else KYC verified
        API-->>Client: 200 {require2FA: true, requireOTP: true}
    end

    Note over Client,DB: Bước 2: Verify
    Client->>API: POST /auth/2fa-verify<br/>{2faCode, otpCode}
    API->>Redis: SET action:withdraw:{uuid}<br/>TTL = 60s
    API-->>Client: 200 {withdrawToken}

    Note over Client,DB: Bước 3: Execute
    Client->>API: POST /withdraw/execute<br/>{withdrawToken, bankInfo}<br/>Authorization: Bearer {AT}

    API->>Redis: GET + DEL action:withdraw:{uuid}

    alt Amount > manual review threshold
        API->>DB: Queue for manual review
        API-->>Client: 200 {status: "pending_review"}
    else Amount <= threshold
        API->>DB: Process withdrawal
        API-->>Client: 200 {status: "processing"}
    end
```

---

## 6. Redis Usage

### Phân biệt rõ ràng

| Mục đích                    | Có dùng Redis? | Key pattern                                      |
| --------------------------- | -------------- | ------------------------------------------------ |
| ❌ Blocklist Access Token   | KHÔNG          | -                                                |
| ❌ Blocklist Refresh Token  | KHÔNG          | -                                                |
| ❌ Rotate RT mỗi refresh    | KHÔNG          | -                                                |
| ✅ Cache Session (optional) | CÓ             | `session:{session_id}`                           |
| ✅ Action Token             | CÓ             | `action:donate:{uuid}`, `action:withdraw:{uuid}` |
| ✅ Rate Limiting            | CÓ             | `rate:chat:{userId}`, `rate:donate:{userId}`     |

### Redis Key Schema

```
# Session cache (optional, TTL = session expiry)
session:{session_id}     → {"status": "ACTIVE", "userId": 123}

# Action tokens (one-time, 60s TTL)
action:donate:{uuid}     → {"userId": 123, "streamerId": 456, "amount": 100}
action:withdraw:{uuid}   → {"userId": 123, "amount": 500}

# Rate limiting
rate:chat:{userId}       → count (TTL 60s, max 30/min)
rate:donate:{userId}     → count (TTL 3600s, max 10/hour)
```

---

## 7. Security Matrix

| Hành vi          | Access Token | Session Check | Action Token | OTP | 2FA | Manual Review |
| ---------------- | :----------: | :-----------: | :----------: | :-: | :-: | :-----------: |
| View stream      |      ❌      |      ❌       |      ❌      | ❌  | ❌  |      ❌       |
| Chat             |      ✅      |      ❌       |      ❌      | ❌  | ❌  |      ❌       |
| Update profile   |      ✅      |      ❌       |      ❌      | ❌  | ❌  |      ❌       |
| Refresh token    |      ❌      |      ✅       |      ❌      | ❌  | ❌  |      ❌       |
| Donate < $10     |      ✅      |      ❌       |      ✅      | ❌  | ❌  |      ❌       |
| Donate >= $10    |      ✅      |      ❌       |      ✅      | ✅  | ❌  |      ❌       |
| Withdraw < $100  |      ✅      |      ❌       |      ✅      | ✅  | ✅  |      ❌       |
| Withdraw >= $100 |      ✅      |      ❌       |      ✅      | ✅  | ✅  |      ✅       |
| Change password  |      ✅      |      ❌       |      ✅      | ✅  | ❌  |      ❌       |

---

## 8. Tóm Tắt Cuối Cùng

### Những thứ KHÔNG làm

- ❌ Rotate Refresh Token mỗi lần refresh
- ❌ Redis blocklist cho Access Token
- ❌ Redis blocklist cho Refresh Token
- ❌ Nhét permissions vào JWT
- ❌ Dùng RT/AT cho money flow

### Những thứ CÓ làm

- ✅ Session trong DB (source of truth)
- ✅ Logout = Revoke session trong DB
- ✅ Action Token cho Donate/Withdraw
- ✅ Step-up auth (OTP, 2FA) cho tiền lớn
- ✅ Redis cache session (optional, cho scale)

---

> 📌 **Xem thêm**: [Security Best Practices](security-best-practices.md)

**End of Document**

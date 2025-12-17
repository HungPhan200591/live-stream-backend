# Phase 6: Real-time Chat System

> **Status**: 🔄 TODO  
> **Dependencies**: Phase 4 (Streaming)

---

## Business Goals

### Use Cases Covered
- **UC-04**: Real-time Chat Interaction

### Business Value Delivered
- ✅ Community engagement through real-time chat
- ✅ Moderation tools (mute/unmute)
- ✅ Chat persistence for history
- ✅ Scalable architecture (Redis Pub/Sub)

### User Flows Supported
- [Viewer Journey](../business_flows.md#flow-2-viewer-journey) (Chat part)

---

## Technical Implementation

### 6.1. Architecture

```
Client → WebSocket → ChatService
                ↓
         Redis Pub/Sub (Broadcast)
                ↓
         All Connected Clients

ChatService → RabbitMQ → Consumer → DB (Persistence)
```

---

### 6.2. Key Components

**WebSocket Configuration**:
- STOMP over WebSocket: `/ws`
- Message broker: `/topic/chat.{streamId}`
- Application prefix: `/app`

**ChatService**:
- `sendMessage(streamId, userId, content)`: Publish to Redis + RabbitMQ
- `getChatHistory(streamId, pageable)`: Load from DB
- `muteUser(streamId, userId, duration)`: Add to Redis Set
- `isMuted(streamId, userId)`: Check Redis Set

**Redis Pub/Sub**:
```redis
# Publish message
PUBLISH chat:room:{streamId} {messageJson}

# Mute user
SADD muted:{streamId} {userId}
EXPIRE muted:{streamId} {duration}
```

---

### 6.3. Business Rules

- **BR-11**: Chỉ authenticated users mới được gửi chat ✅
- **BR-12**: Muted users không thể gửi message ✅
- **BR-13**: Chỉ stream owner/ADMIN mới được mute users ✅
- **BR-14**: Mute có thời hạn (TTL), tự động unmute khi hết hạn ✅
- **BR-15**: Chat messages phải được persist vào DB (async) ✅

---

### 6.4. API Endpoints

**REST APIs**:
- `GET /api/chat/{streamId}/history` - Chat history (Public)
- `POST /api/chat/{streamId}/mute` - Mute user (Owner + ADMIN)
- `POST /api/chat/{streamId}/unmute` - Unmute user (Owner + ADMIN)

**WebSocket**:
- `/app/chat.send` - Send message
- `/topic/chat.{streamId}` - Subscribe to room

---

### 6.5. Verification Plan

**Test Scenarios**:
1. **WebSocket Connection**: Connect with JWT → Success
2. **Message Broadcast**: Send message → All subscribers receive
3. **Mute Logic**: Muted user tries to send → Blocked
4. **Persistence**: Messages saved to DB asynchronously

---

## Dependencies

### Required
- Phase 4: Streaming (chat rooms tied to streams)

### Enables
- Phase 7: Gift System (donation alerts in chat)

---

## Reference
- [Business Flows - UC-04](../business_flows.md#uc-04-real-time-chat-interaction)
- [API Specification - Chat](../api_endpoints_specification.md#24-chat-apichat)

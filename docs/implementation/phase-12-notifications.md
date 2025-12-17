# Phase 12: Notification System (Optional)

> **Status**: 🔄 OPTIONAL  
> **Dependencies**: Phase 11 (Social)

---

## Business Goals

### Business Value Delivered
- ✅ Real-time notifications cho users
- ✅ User engagement through timely alerts
- ✅ Notification history
- ✅ Customizable notification preferences

---

## Technical Implementation

### 12.1. Entity

**Notification Entity**:
```java
@Entity
public class Notification {
    private Long id;
    private Long userId;
    private NotificationType type; // STREAM_STARTED, NEW_FOLLOWER, GIFT_RECEIVED
    private Long relatedEntityId;
    private String message;
    private Boolean isRead = false;
    private LocalDateTime createdAt;
}
```

---

### 12.2. Key Services

**NotificationService**:
- `createNotification(request)`: Save to DB + Push via WebSocket
- `markAsRead(notificationId, userId)`: Update isRead status
- `getNotifications(userId, pageable)`: Get user's notifications
- `getUnreadCount(userId)`: Count unread (Redis cached)

---

### 12.3. Event Consumers (RabbitMQ)

**NotificationConsumer**:
- Listen `notifications.stream.started`: Notify all followers
- Listen `notifications.user.followed`: Notify user being followed
- Listen `notifications.gift.received`: Notify streamer

---

### 12.4. WebSocket

**Topic**: `/topic/notifications.{userId}`

**Message Format**:
```json
{
  "type": "STREAM_STARTED",
  "message": "User123 started streaming!",
  "relatedEntityId": 456,
  "timestamp": "2025-12-18T00:00:00"
}
```

---

### 12.5. API Endpoints

- `GET /api/notifications` - Get notification history
- `PUT /api/notifications/{id}/read` - Mark as read
- `PUT /api/notifications/read-all` - Mark all as read
- `DELETE /api/notifications/{id}` - Delete notification

---

### 12.6. Redis Caching

```redis
# Cache unread count
SET notifications:{userId}:unread {count} EX 60
```

---

## Dependencies

### Required
- Phase 11: Social (follower notifications)

### Enables
- Complete platform with full engagement features

---

## Reference
- [API Roadmap - Notifications](../api_roadmap.md#-phase-12-notification-system-optional)

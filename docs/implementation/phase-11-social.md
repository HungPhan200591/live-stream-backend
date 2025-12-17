# Phase 11: Social Features (Optional)

> **Status**: 🔄 OPTIONAL  
> **Dependencies**: Phase 4 (Streaming)

---

## Business Goals

### Business Value Delivered
- ✅ Social networking features
- ✅ User engagement through follows
- ✅ Notification triggers cho followers
- ✅ Social graph analytics

---

## Technical Implementation

### 11.1. Entity

**UserFollow Entity**:
```java
@Entity
public class UserFollow {
    private Long id;
    private Long followerId;
    private Long followingId;
    private LocalDateTime createdAt;
    
    // Composite index: (followerId, followingId) UNIQUE
}
```

---

### 11.2. Key Services

**SocialService**:
- `followUser(followerId, followingId)`: Create follow relationship
- `unfollowUser(followerId, followingId)`: Remove follow
- `getFollowers(userId, pageable)`: List followers
- `getFollowing(userId, pageable)`: List following
- `isFollowing(followerId, followingId)`: Check relationship

---

### 11.3. API Endpoints

- `POST /api/users/{userId}/follow` - Follow user
- `DELETE /api/users/{userId}/unfollow` - Unfollow user
- `GET /api/users/{userId}/followers` - Get followers list
- `GET /api/users/{userId}/following` - Get following list
- `GET /api/users/{userId}/is-following` - Check follow status

---

### 11.4. Redis Caching

```redis
# Cache followers list
SET user:{userId}:followers {followerIds} EX 300
```

---

### 11.5. Integration with Notifications

**Trigger Events**:
- User follows → Notify followed user
- Streamer goes live → Notify all followers

---

## Dependencies

### Required
- Phase 4: Streaming

### Enables
- Phase 12: Notifications (follower notifications)

---

## Reference
- [API Roadmap - Social Features](../api_roadmap.md#-phase-11-social-features-optional)

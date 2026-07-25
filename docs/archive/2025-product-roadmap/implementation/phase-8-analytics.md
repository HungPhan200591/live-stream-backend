# Phase 8: Analytics & Leaderboard

> **ARCHIVED 2026-07-25** — Không phải active backlog. Dùng Kafka/analytics cases trong [Senior Roadmap](../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md).

> **Status**: 🔄 TODO  
> **Dependencies**: Phase 7 (Gifts)

---

## Business Goals

### Use Cases Covered
- **UC-06**: Analytics & Leaderboard

### Business Value Delivered
- ✅ Data insights cho streamers và admins
- ✅ Gamification qua leaderboards
- ✅ Real-time viewer tracking
- ✅ Revenue analytics

### User Flows Supported
- Analytics dashboards

---

## Technical Implementation

### 8.1. Redis Data Structures

**HyperLogLog** (Viewer Tracking):
```redis
PFADD stream:{streamId}:viewers {userId}
PFCOUNT stream:{streamId}:viewers
```

**Sorted Sets** (Leaderboard):
```redis
ZINCRBY leaderboard:daily:{date} {amount} {userId}
ZREVRANGE leaderboard:daily:{date} 0 9 WITHSCORES
```

---

### 8.2. Key Services

**AnalyticsService**:
- `trackStreamView(streamId, userId)`: Add to HyperLogLog
- `getStreamViewerCount(streamId)`: Count unique viewers
- `updateLeaderboard(userId, amount)`: Update daily/weekly/alltime
- `getDailyLeaderboard(limit)`: Top N gifters
- `getSystemDashboard()`: Admin dashboard (ADMIN only)

**StreamAnalyticsService**:
- `getStreamReport(streamId, currentUser)`: Stream analytics (Owner + ADMIN)
- `calculateStreamRevenue(streamId)`: Total gifts received

---

### 8.3. Business Rules

- **BR-21**: Viewer count dùng HyperLogLog (unique users) ✅
- **BR-22**: Leaderboard update real-time khi có gift ✅
- **BR-23**: Stream analytics chỉ owner/ADMIN xem được ✅
- **BR-24**: System dashboard chỉ ADMIN xem được ✅

---

### 8.4. API Endpoints

- `GET /api/analytics/leaderboard` - Leaderboard (Public)
- `GET /api/analytics/dashboard` - System dashboard (ADMIN)
- `GET /api/analytics/streams/{id}/report` - Stream report (Owner + ADMIN)

---

### 8.5. Scheduled Tasks

**LeaderboardCleanupScheduler**:
- Daily: Archive yesterday's leaderboard to DB
- Weekly: Cleanup old leaderboards (keep last 4 weeks)

---

### 8.6. Verification Plan

**Test Scenarios**:
1. **HyperLogLog Accuracy**: Add 10k unique users → Count ~10k (±2% error)
2. **Leaderboard Update**: Send gift → Leaderboard updates immediately
3. **Stream Report**: Owner views report → See peak viewers, revenue

---

## Dependencies

### Required
- Phase 7: Gifts (leaderboard data source)

### Enables
- Phase 10: Production (monitoring metrics)

---

## Reference
- [Business Flows - UC-06](../../../contracts/business-flows.md#uc-06-analytics--leaderboard)
- [API Specification - Analytics](../../2025-reference/api-endpoints-specification.md#26-analytics-apianalytics)

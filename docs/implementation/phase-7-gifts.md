# Phase 7: Gift System & Async Processing

> **Status**: 🔄 TODO  
> **Dependencies**: Phase 5 (Economy), Phase 6 (Chat)

---

## Business Goals

### Use Cases Covered
- **UC-05**: Gift Sending & Wallet Management (Part 2 - Gift flow)

### Business Value Delivered
- ✅ Monetization mechanism cho streamers
- ✅ Async processing không block user
- ✅ Donation alerts trong chat
- ✅ Leaderboard updates

### User Flows Supported
- [Gift Transaction Flow](../business_flows.md#flow-3-gift-transaction-flow)

---

## Technical Implementation

### 7.1. Gift Flow

```
1. User selects gift → Calculate total
2. ATOMIC: Deduct from sender wallet
3. ASYNC: Publish GiftEvent to RabbitMQ
4. Consumer: 
   - Credit streamer wallet
   - Create transaction record
   - Broadcast donation alert
   - Update leaderboard
```

---

### 7.2. Key Services

**GiftService**:
- `getAllGifts()`: Gift catalog (Redis cached)
- `sendGift(request, currentUser)`:
  1. Validate gift exists
  2. **Atomic deduct** (synchronous)
  3. **Publish event** (async via RabbitMQ)
  4. Return success response

**GiftEventConsumer**:
- Consume from `gift.transaction.queue`
- Process: Credit wallet + Create transaction + Alert + Leaderboard
- Error handling: Retry 3x → DLQ

---

### 7.3. RabbitMQ Configuration

```java
// Queue
gift.transaction.queue (durable)

// Dead Letter Queue
gift.transaction.dlq (for failed processing)

// Retry: 3 attempts with exponential backoff
```

---

### 7.4. Business Rules

- **BR-18**: Gift transaction phải atomic (deduct + publish) ✅
- **BR-19**: Nếu deduct fail, không được publish event ✅

---

### 7.5. API Endpoints

- `GET /api/gifts` - Gift catalog (Public)
- `POST /api/gifts/send` - Send gift (Authenticated)

---

### 7.6. Verification Plan

**Test Scenarios**:
1. **End-to-End**: Send gift → Deduct → Publish → Consume → Credit wallet
2. **Insufficient Balance**: Try to send gift > balance → Error before publish
3. **Retry Logic**: Simulate consumer failure → Retry 3x → DLQ

---

## Dependencies

### Required
- Phase 5: Economy (wallet operations)
- Phase 6: Chat (donation alerts)

### Enables
- Phase 8: Analytics (gift data for leaderboard)

---

## Reference
- [Business Flows - UC-05](../business_flows.md#uc-05-gift-sending--wallet-management)
- [API Specification - Gifts](../api_endpoints_specification.md#25-gifts--transactions-apigifts-apitransactions)

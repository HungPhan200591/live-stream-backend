# Phase 5: Economy & Transaction System

> **Status**: 🔄 TODO  
> **Dependencies**: Phase 3 (Authentication)

---

## Business Goals

### Use Cases Covered
- **UC-05**: Gift Sending & Wallet Management (Part 1 - Wallet foundation)

### Business Value Delivered
- ✅ Virtual currency foundation cho monetization
- ✅ Atomic wallet transactions (prevent double-spend)
- ✅ Transaction audit trail
- ✅ Balance tracking và history

### User Flows Supported
- [Gift Transaction Flow](../business_flows.md#flow-3-gift-transaction-flow) (Wallet part)

---

## Technical Implementation

### 5.1. Entities

**Wallet Entity**:
```java
@Entity
public class Wallet {
    private Long id;
    private Long userId; // NO @OneToOne
    private BigDecimal balance;
    private String currency = "COINS";
    @Version
    private Long version; // Optimistic Locking
    private LocalDateTime updatedAt;
}
```

**Transaction Entity**:
```java
@Entity
public class Transaction {
    private Long id;
    private Long fromUserId; // nullable for deposits
    private Long toUserId;
    private BigDecimal amount;
    private TransactionType type; // DEPOSIT, GIFT, WITHDRAWAL, REFUND
    private TransactionStatus status; // PENDING, COMPLETED, FAILED
    private String metadata; // JSON
    private LocalDateTime createdAt;
}
```

---

### 5.2. Key Services

**WalletService**:
- `createWallet(Long userId)`: Auto-create on registration
- `deposit(userId, amount, metadata)`: Add funds (atomic)
- `deduct(userId, amount, reason)`: Remove funds (atomic, check balance)
- `transfer(fromUserId, toUserId, amount)`: P2P transfer
- `getBalance(userId)`: Quick balance check (Redis cached)

**TransactionService**:
- `getTransactionHistory(userId, pageable)`: User's transactions
- `getTransactionById(id, currentUser)`: Details (auth check)
- `getTotalRevenue(userId)`: Sum of gifts received

---

### 5.3. Business Rules

- **BR-16**: Mỗi user chỉ có 1 wallet duy nhất ✅
- **BR-17**: Balance không được âm ✅
- **BR-18**: Gift transaction phải atomic (deduct + publish) ✅
- **BR-19**: Nếu deduct fail, không được publish event ✅
- **BR-20**: Transaction status: PENDING → COMPLETED/FAILED ✅

---

### 5.4. API Endpoints

**Wallet APIs**:
- `GET /api/users/{userId}/wallet` - View balance (Self + ADMIN)
- `GET /api/users/{userId}/transactions` - Transaction history (Self + ADMIN)

**Transaction APIs**:
- `GET /api/transactions/{id}` - Transaction details (Involved users + ADMIN)

---

### 5.5. Redis Caching

```redis
# Cache balance (TTL 60s)
SET wallet:{userId}:balance {amount} EX 60
```

---

### 5.6. Verification Plan

**Test Scenarios**:
1. **Concurrent Transactions**: 2 users send gifts simultaneously → No race condition
2. **Insufficient Balance**: Try to send gift > balance → Exception thrown
3. **Optimistic Locking**: Simulate version conflict → Retry logic works

---

## Dependencies

### Required
- Phase 3: Authentication

### Enables
- Phase 7: Gift System (wallet deduction)

---

## Reference
- [Business Flows - UC-05](../business_flows.md#uc-05-gift-sending--wallet-management)
- [API Specification - Transactions](../api_endpoints_specification.md#25-gifts--transactions-apigifts-apitransactions)

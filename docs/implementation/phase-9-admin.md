# Phase 9: Admin Management Module

> **Status**: 🔄 TODO  
> **Dependencies**: Phase 4 (Streaming), Phase 5 (Economy)

---

## Business Goals

### Use Cases Covered
- **UC-07**: Admin Moderation

### Business Value Delivered
- ✅ Platform governance tools
- ✅ User management (ban/unban, role changes)
- ✅ Content moderation
- ✅ Audit trail for admin actions

---

## Technical Implementation

### 9.1. Key Services

**AdminUserService**:
- `getAllUsers(pageable)`: List all users (pagination + filters)
- `banUser(userId, reason)`: Ban user + revoke sessions
- `unbanUser(userId)`: Unban user
- `changeUserRole(userId, newRole)`: Promote/demote users
- `deleteUser(userId)`: Soft delete user

**AdminStreamService**:
- `getAllStreams(pageable)`: All streams (include inactive)
- `forceEndStream(streamId)`: Emergency stop
- `deleteStream(streamId)`: Hard delete

**AdminTransactionService**:
- `getAllTransactions(pageable)`: Audit trail
- `refundTransaction(transactionId)`: Reverse transaction

---

### 9.2. Business Rules

- Only ADMIN can access admin endpoints
- All admin actions must be logged
- Ban user → Revoke all active sessions
- Role changes must be validated (USER → STREAMER → ADMIN)

---

### 9.3. API Endpoints

**User Management**:
- `GET /api/admin/users` - List users (ADMIN)
- `POST /api/admin/users/{id}/ban` - Ban user (ADMIN)
- `POST /api/admin/users/{id}/unban` - Unban user (ADMIN)
- `PUT /api/admin/users/{id}/roles` - Change roles (ADMIN)
- `DELETE /api/admin/users/{id}` - Delete user (ADMIN)

**Stream Management**:
- `GET /api/admin/streams` - All streams (ADMIN)
- `DELETE /api/admin/streams/{id}` - Delete stream (ADMIN)

**Transaction Management**:
- `GET /api/admin/transactions` - All transactions (ADMIN)
- `POST /api/admin/transactions/{id}/refund` - Refund (ADMIN)

---

### 9.4. Verification Plan

**Test Scenarios**:
1. **Ban User**: Ban user → All sessions revoked → Cannot login
2. **Role Change**: Promote USER to STREAMER → Can create streams
3. **Audit Trail**: Admin actions logged → Queryable for compliance

---

## Dependencies

### Required
- Phase 4: Streaming (stream moderation)
- Phase 5: Economy (transaction management)

### Enables
- Phase 10: Production (admin monitoring)

---

## Reference
- [Business Flows - UC-07](../business_flows.md#uc-07-admin-moderation)
- [API Specification - Admin](../api_endpoints_specification.md#27-admin-apiadmin)

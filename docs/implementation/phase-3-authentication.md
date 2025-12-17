# Phase 3: Authentication & User Management

> **Status**: ✅ COMPLETED  
> **Dependencies**: Phase 1 (Foundation)

---

## Business Goals

### Use Cases Covered
- **UC-01**: User Registration & Authentication

### Business Value Delivered
- ✅ Users có thể đăng ký và đăng nhập
- ✅ Role-based access control (USER, STREAMER, ADMIN)
- ✅ Session management với refresh tokens
- ✅ Secure JWT-based authentication

### User Flows Supported
- [Authentication Flow](../business_flows.md#flow-4-authentication--session-management)

---

## Technical Implementation Summary

### Entities
- ✅ `User` entity (NO `@ManyToMany`)
- ✅ `Role` entity (ROLE_USER, ROLE_STREAMER, ROLE_ADMIN)
- ✅ `UserRole` explicit join table entity
- ✅ `UserSession` entity (session-backed refresh tokens)

### Security Components
- ✅ `SecurityConfig`: Two-Tier Authorization Strategy
- ✅ `JwtAuthenticationFilter`: Extract & validate JWT
- ✅ `JwtTokenProvider`: Generate access/refresh tokens
- ✅ `CustomUserDetailsService`: Load user with roles

### API Endpoints
- ✅ `POST /api/auth/register`
- ✅ `POST /api/auth/login`
- ✅ `POST /api/auth/refresh`
- ✅ `POST /api/auth/logout`
- ✅ `GET /api/auth/me`

### Services
- ✅ `AuthService`: register, login, refresh, logout
- ✅ `UserService`: getUserById, getCurrentUser, assignRole
- ✅ `SessionService`: Session lifecycle management

---

## Key Features

### JWT Token Strategy

**Access Token**:
- TTL: 15 minutes
- Stateless
- Contains: userId, username, roles

**Refresh Token**:
- TTL: 30 days
- Session-backed (validated against DB)
- Contains: userId, sessionId, deviceId

### Session Management

**UserSession Table**:
```java
@Entity
public class UserSession {
    private Long id;
    private Long userId;
    private String deviceId;
    private SessionStatus status; // ACTIVE, EXPIRED, REVOKED
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
```

**Benefits**:
- ✅ Revoke tokens on logout
- ✅ "Logout all devices" capability
- ✅ Track active sessions per user
- ✅ Security: Detect suspicious activity

---

## Verification Results

### Automated Tests
- ✅ Unit tests for AuthService
- ✅ Integration tests for AuthController
- ✅ Security configuration tests

### Manual Testing
- ✅ Registration flow
- ✅ Login flow
- ✅ Token refresh flow
- ✅ Logout flow
- ✅ Authorization rules (RBAC)

---

## Business Rules Enforced

- **BR-01**: Mọi user mới đăng ký đều có role `ROLE_USER` mặc định ✅
- **BR-02**: Chỉ ADMIN mới có thể promote user lên `ROLE_STREAMER` ✅
- **BR-03**: Access Token có TTL 15 phút, Refresh Token 30 ngày ✅
- **BR-04**: Refresh Token phải được validate với session trong DB ✅
- **BR-05**: Logout phải revoke session (set status=REVOKED) ✅

---

## Dependencies

### Required
- Phase 1: Foundation

### Enables
- Phase 4: Stream Management (creator identification)
- Phase 5: Economy (wallet ownership)
- All future phases (authentication required)

---

## Notes

### Security Highlights

1. **Password Hashing**: BCrypt with strength 10
2. **JWT Signing**: HS512 algorithm
3. **Session Validation**: Every refresh token request checks DB
4. **Role Hierarchy**: No automatic role escalation

### Data Seeding

**Default Users** (via `DataInitializer`):
- Admin: `admin` / `admin123` (ROLE_ADMIN)
- Streamer: `streamer` / `streamer123` (ROLE_STREAMER)
- User: `user` / `user123` (ROLE_USER)

### API Documentation

- ✅ Swagger UI: `http://localhost:8080/swagger-ui.html`
- ✅ HTTP test file: `.http/auth-controller.http`
- ✅ All endpoints documented with `@Operation`

---

## Reference

- [Business Flows - UC-01](../business_flows.md#uc-01-user-registration--authentication)
- [API Specification - Authentication](../api_endpoints_specification.md#21-authentication-apiauth)
- [Authorization Flow](../authorization_flow.md)

# Spring Boot Livestream Backend

> **Backend hiệu năng cao cho nền tảng Livestream**  
> Java 17 | Spring Boot 3.x | PostgreSQL | Redis | RabbitMQ | WebSocket

---

## 🎯 Project Overview

### Mục Tiêu
- Xây dựng backend livestream platform với focus vào **performance** và **scalability**
- Học tập và thực hành các công nghệ: Redis, RabbitMQ, WebSocket, Concurrency
- Simulation-first approach: Dev độc lập không cần external services

### Tech Stack
| Component | Technology | Purpose |
|-----------|------------|---------|
| **Backend** | Java 17, Spring Boot 3.x | Core application |
| **Database** | PostgreSQL 16 | Primary data store |
| **Cache** | Redis 7 | Caching, Pub/Sub, HyperLogLog, Sorted Sets |
| **Message Queue** | RabbitMQ 3 | Async processing, Event-driven |
| **Real-time** | WebSocket (STOMP) | Chat, Notifications |
| **API Docs** | Swagger/OpenAPI | Auto-generated documentation |

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose

### Setup & Run

```bash
# 1. Clone repository
git clone <repository-url>
cd live-stream-backend

# 2. Start infrastructure (PostgreSQL, Redis, RabbitMQ)
docker-compose up -d

# 3. Run application
mvn spring-boot:run

# 4. Access Swagger UI
open http://localhost:8080/swagger-ui.html
```

### Default Users (Seeded)
| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | ROLE_ADMIN |
| `streamer` | `streamer123` | ROLE_STREAMER |
| `user` | `user123` | ROLE_USER |

---

## 📚 Documentation Structure

### Core Documents (Đọc theo thứ tự)

1. **[Business Flows](docs/business_flows.md)** ⭐ START HERE
   - 7 core use cases (User Auth, Streaming, Chat, Gifts, Analytics, Admin)
   - Sequence diagrams cho user journeys
   - Business rules và state machines
   - **Đọc trước để hiểu "WHY" trước "HOW"**

2. **[System Design](docs/system_design_livestream.md)**
   - Architecture overview với business context
   - Technology choices (tại sao dùng Redis, RabbitMQ)
   - Component interactions
   - Database schema

3. **[Implementation Roadmap](docs/implementation/000_ROADMAP.md)**
   - 12 phases implementation plan
   - Phase dependencies diagram
   - Current progress: 3/12 phases (25%)
   - **Next**: Phase 4 - Stream Management

4. **[API Specification](docs/api_endpoints_specification.md)**
   - Complete API reference
   - Authorization rules (Two-Tier strategy)
   - Endpoint patterns và examples
   - **Đọc trước khi implement Controller**

### Implementation Phases (docs/implementation/)

| Phase | Status | Description |
|-------|--------|-------------|
| Phase 1 | ✅ DONE | Foundation & Infrastructure |
| Phase 2 | ✅ DONE | Development Simulation APIs |
| Phase 3 | ✅ DONE | Authentication & User Management |
| **Phase 4** | **🔄 NEXT** | **Stream Management Module** |
| Phase 5 | 🔄 TODO | Economy & Transaction System |
| Phase 6 | 🔄 TODO | Real-time Chat System |
| Phase 7 | 🔄 TODO | Gift System & Async Processing |
| Phase 8 | 🔄 TODO | Analytics & Leaderboard |
| Phase 9 | 🔄 TODO | Admin Management Module |
| Phase 10 | 🔄 TODO | Production Readiness |
| Phase 11 | 🔮 OPTIONAL | Social Features |
| Phase 12 | 🔮 OPTIONAL | Notification System |

**Chi tiết từng phase**: Xem `docs/implementation/phase-{N}-*.md`

---

## 🏗️ Architecture Highlights

### Layered Architecture
```
Controller → Service → Repository
     ↓          ↓          ↓
   DTOs    Business    Entities
           Logic
```

### Key Design Decisions

**1. No JPA Relationships**
- ❌ Không dùng `@ManyToMany`, `@OneToMany`, `@ManyToOne`, `@OneToOne`
- ✅ Dùng explicit join table entities
- **Why**: Giảm coupling, tránh N+1, dễ control performance

**2. DTO-First API**
- ❌ Không expose Entity trực tiếp
- ✅ Luôn dùng Request/Response DTOs
- **Why**: Separation of concerns, API stability

**3. Session-Backed JWT**
- Access Token: 15 phút (stateless)
- Refresh Token: 30 ngày (session-backed, check DB)
- **Why**: Revoke capability, security

**4. Redis Pub/Sub for Chat**
- Real-time broadcast qua Redis
- Async persistence qua RabbitMQ
- **Why**: Horizontal scaling, decouple concerns

**5. Atomic Wallet + Async Rewards**
- Deduct wallet: Synchronous (atomic)
- Credit streamer: Asynchronous (RabbitMQ)
- **Why**: User experience + data integrity

---

## 🔑 Key Features

### Implemented (Phases 1-3)
- ✅ JWT Authentication với RBAC (USER, STREAMER, ADMIN)
- ✅ Session management (logout, refresh tokens)
- ✅ User registration & profile management
- ✅ Swagger API documentation
- ✅ Development simulation APIs
- ✅ Docker Compose infrastructure

### Next Up (Phase 4)
- 🔄 Stream CRUD operations
- 🔄 Live status tracking (Redis)
- 🔄 Viewer count (HyperLogLog)
- 🔄 Stream lifecycle management

### Planned (Phases 5-12)
- 📋 Virtual wallet & transactions
- 📋 Real-time chat (WebSocket + Redis Pub/Sub)
- 📋 Gift system với async processing
- 📋 Analytics & leaderboards
- 📋 Admin moderation tools
- 📋 Production hardening

---

## 📖 Development Guidelines

### Coding Standards
- **Required Reading**: `docs/agent/rules/coding-rule.md`
- **Key Rules**:
  - No JPA relationship annotations
  - Always use DTOs for API
  - Follow Two-Tier authorization (URL + Method level)
  - Redis cache DTOs trong `model/dto/cache/`
  - Swagger annotations cho all endpoints

### API Development Workflow
1. Read business flows → Understand use case
2. Read phase document → Get implementation details
3. Check API specification → Follow endpoint patterns
4. Implement: Entity → Repository → Service → Controller
5. Create `.http` file for testing
6. Verify via Swagger UI

### Testing Strategy
- **Unit Tests**: Business logic trong Services
- **Integration Tests**: API endpoints với authorization
- **Manual Tests**: HTTP files + Swagger UI
- **No auto-run**: User tự run build/test khi cần

---

## 🛠️ Useful Commands

```bash
# Build
mvn clean package -DskipTests

# Run tests
mvn test

# Run with profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Docker services
docker-compose up -d          # Start all
docker-compose down           # Stop all
docker-compose logs -f redis  # View logs
```

---

## 📊 Project Status

**Current Phase**: 3/12 (25% complete)  
**Next Milestone**: Phase 4 - Stream Management  
**Last Updated**: 2025-12-18

### Recent Updates
- ✅ Completed Phase 3: Authentication & User Management
- ✅ Implemented session-backed JWT refresh tokens
- ✅ Created comprehensive documentation structure
- ✅ Extracted all 12 implementation phases

---

## 🤝 Contributing

### For New Developers
1. **Onboarding** (~2 hours):
   - Read `docs/business_flows.md` (30 mins)
   - Skim `docs/system_design_livestream.md` (45 mins)
   - Review `docs/agent/rules/coding-rule.md` (20 mins)
   - Check current phase in `docs/implementation/ROADMAP.md` (10 mins)

2. **Start Coding**:
   - Pick a task from current phase
   - Follow phase document checklist
   - Create HTTP test file
   - Submit for review

---

## 📝 Notes

### Philosophy: Pragmatic & Fast
- **Simulation First**: Không cần OBS, Payment Gateway thật
- **KISS Principle**: Layered Architecture, không over-engineer
- **Performance Focus**: Redis, RabbitMQ, Concurrency handling
- **Learning Goal**: Hands-on với modern backend stack

### Production Disclaimer
⚠️ **Development/Learning Project**
- Simulation APIs (`/api/dev/**`) phải disable trong production
- Security hardening cần thiết trước deploy
- Load testing required cho production readiness

---

## 📞 Support

- **Documentation**: Start with `docs/business_flows.md`
- **API Reference**: `docs/api_endpoints_specification.md`
- **Implementation Guide**: `docs/implementation/ROADMAP.md`
- **Coding Standards**: `docs/agent/rules/coding-rule.md`

---

**Built with ❤️ for learning and performance optimization**

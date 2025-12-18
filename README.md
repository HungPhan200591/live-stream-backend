# Spring Boot Livestream Backend

> Backend hiệu năng cao cho nền tảng Livestream
> Java 17 | Spring Boot 3.x | PostgreSQL | Redis | RabbitMQ | WebSocket

---

## Overview
- Xây dựng backend tập trung vào performance và scalability.
- Simulation-first: phát triển và test qua API mô phỏng, không phụ thuộc dịch vụ ngoài.
- Mục tiêu học tập và thực hành Redis, RabbitMQ, WebSocket, concurrency.

## Tech Stack
| Component | Technology | Purpose |
|-----------|------------|---------|
| Backend | Java 17, Spring Boot 3.x | Core application |
| Database | PostgreSQL 16 | Primary data store |
| Cache | Redis 7 | Caching, Pub/Sub, HyperLogLog, Sorted Sets |
| Message Queue | RabbitMQ 3 | Async processing, event-driven |
| Real-time | WebSocket (STOMP) | Chat, notifications |
| API Docs | Swagger/OpenAPI | Auto-generated documentation |

---

## Quick Start

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

## Documentation
- Entry cho developer: `docs/000_DOCS_GUIDE.md`
- Entry cho AI agent: `docs/agent/rules/context-load.md`
- Roadmap & phase: `docs/implementation/000_ROADMAP.md`
- API & authorization: `docs/api_endpoints_specification.md`

---

## Architecture Highlights
- No JPA relationships: dùng entity trung gian để tránh N+1 và giảm coupling.
- DTO-first API: không expose Entity lên controller.
- Session-backed JWT: access token 15 phút, refresh token kiểm tra DB để revoke được.
- Redis Pub/Sub cho chat realtime, RabbitMQ cho lưu trữ async.
- Wallet xử lý atomic, reward streamer async để cân bằng UX và integrity.

---

## Development Guidelines
- Coding standards: `docs/agent/rules/coding-rule.md`.
- Workflow: Business Flows -> Phase doc -> API spec -> implement -> tạo `.http` -> verify.
- Testing: unit, integration, manual; người dùng chủ động chạy khi cần.

---

## Useful Commands

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

## Project Status
**Current Phase**: 3/12 (25% complete)
**Next Milestone**: Phase 4 - Stream Management
**Last Updated**: 2025-12-18

---

## Contributing
1. Onboarding (~2 hours)
   - Read `docs/business_flows.md`
   - Skim `docs/system_design_livestream.md`
   - Review `docs/agent/rules/coding-rule.md`
   - Check current phase in `docs/implementation/000_ROADMAP.md`
2. Start coding
   - Pick task from current phase
   - Follow phase checklist
   - Create `.http` file
   - Submit for review

---

## Notes
- Simulation APIs (`/api/dev/**`) phải disable trong production.
- Security hardening và load testing cần thiết trước khi deploy.

---

## Support
- Documentation entry: `docs/000_DOCS_GUIDE.md`
- API reference: `docs/api_endpoints_specification.md`
- Implementation guide: `docs/implementation/000_ROADMAP.md`
- Coding standards: `docs/agent/rules/coding-rule.md`

---

Built for learning and performance optimization.

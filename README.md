# Live Stream Backend — Senior Java Interview Lab

Project dùng domain livestream để luyện các bài toán Senior Backend bằng code, test, experiment và failure analysis. Trọng tâm là Java Core, Spring Boot, transaction, concurrency, security, PostgreSQL, Redis, messaging, observability, architecture và microservice evolution; không lấy số lượng CRUD endpoint làm mục tiêu.

## Current baseline

| Thành phần | Hiện trạng |
| --- | --- |
| Runtime hiện tại | Java 17 khai báo trong POM, Spring Boot 3.4, Maven Wrapper; runtime evidence từng là Java 22 nên chưa tái lập |
| Platform target | Java 21 qua `JDK-01`; sau safety net chạy `JDK-02` decision gate cho JDK 25 + Spring Boot line được hỗ trợ |
| Durable data | PostgreSQL; Hibernate hiện còn `ddl-auto=update` |
| Redis | Session cache, live status, HyperLogLog unique viewers |
| Authentication | Session-backed JWT; còn P0 token/matcher/cache gaps |
| Stream | Create/list/detail/my và RTMP start/end webhook |
| Wallet | Deposit mô phỏng, không persist; chưa có ledger |
| RabbitMQ | Dependency/config/test publish; chưa có business flow |
| WebSocket | Dependency only |
| Kafka/microservice/replica/partition | Chưa implement |
| Testing | Một context smoke test; Stage 0 phải tạo hermetic harness |

Chi tiết bằng chứng và gap: [Current State & Gap Analysis](docs/002_CURRENT_STATE_AND_GAP_ANALYSIS.md).

## Roadmap

Roadmap/order chuẩn là [Senior Java Interview Roadmap](docs/001_SENIOR_JAVA_INTERVIEW_ROADMAP.md); active case và checkpoint chỉ lấy từ [Learning System](docs/learning/index.md). Hiện `JDK-01` là case `ACTIVE`, `TEST-01` là safety-net case kế tiếp và `SEC-01` đang `PAUSED`. Không sao chép toàn bộ execution queue vào README để tránh tạo backlog cạnh tranh.

[Current Implementation Map](docs/implementation/current-implementation-map.md) chỉ mô tả code coverage; không phải backlog cạnh tranh với learning roadmap.

## Quick start trên Windows

Yêu cầu hiện tại: JDK 17, Docker Desktop và PowerShell. Đây là requirement theo POM trước khi `JDK-01` đóng, không phải target dài hạn.

```powershell
docker compose up -d postgres redis rabbitmq
.\mvnw.cmd spring-boot:run
```

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- PostgreSQL: `localhost:15432`
- Redis: `localhost:16379`
- RabbitMQ management: `http://localhost:15672`

Configuration mặc định là development-oriented và chưa phải production profile. `/api/dev/**`, `/api/test/**`, Swagger, P6Spy và seed data phải được cô lập trong Stage 0.

## Verification

```powershell
.\mvnw.cmd -DskipTests compile
.\mvnw.cmd test
```

Test hiện có thể phụ thuộc PostgreSQL local. Đây là known gap, không phải điều kiện setup lý tưởng.

## Documentation

- [Documentation Orchestrator](docs/000_DOCUMENTATION_ORCHESTRATOR.md)
- [Senior Roadmap](docs/001_SENIOR_JAVA_INTERVIEW_ROADMAP.md)
- [System Context](docs/architecture/system-context.md)
- [Business Flows](docs/contracts/business-flows.md)
- [API Contract](docs/contracts/api-contract.md)
- [Security Flow](docs/security/authorization-flow.md)
- [Redis Guide](docs/engineering/redis-guide.md)
- [AI Agent Engineering System](docs/003_AI_AGENT_ENGINEERING_SYSTEM.md)
- [Learning Guide](docs/learning/guide.md)
- [Learning System & Session Cursor](docs/learning/index.md)
- [Codex Skill Catalog](docs/ai/skill-catalog.md)
- Repository guardrails: [AGENTS.md](AGENTS.md)

Legacy phase plans, generic prompts và design giả định đã được giữ tại [Documentation Archive](docs/archive/index.md).

## Engineering constraints

- Controller mỏng, DTO-only API, service-owned transaction boundary.
- Không dùng JPA relationship annotations; lưu foreign ID tường minh.
- PostgreSQL là source of truth; cache/event side effect có consistency policy rõ.
- Simulation-first cho media/payment/external integration.
- Mọi scale/performance claim phải có workload và measurement.
- Mọi state/money/security case phải có negative hoặc concurrency/failure test phù hợp.

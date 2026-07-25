# Live Stream Backend — Senior Java Interview Lab

Project dùng domain livestream để luyện các bài toán Senior Backend bằng code, test, experiment và failure analysis. Trọng tâm là Java Core, Spring Boot, transaction, concurrency, security, PostgreSQL, Redis, messaging, observability, architecture và microservice evolution; không lấy số lượng CRUD endpoint làm mục tiêu.

## Current baseline

| Thành phần | Hiện trạng |
| --- | --- |
| Runtime khai báo | Java 17, Spring Boot 3.4, Maven Wrapper |
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

Roadmap chuẩn là [Senior Java Interview Roadmap](docs/001_SENIOR_JAVA_INTERVIEW_ROADMAP.md). Priority hiện tại:

`SEC-01 -> TEST-01 -> SEC-02 -> CON-01 -> DB-01 -> WAL-01`

[Current Implementation Map](docs/implementation/000_ROADMAP.md) chỉ mô tả code coverage; không phải backlog cạnh tranh với learning roadmap.

## Quick start trên Windows

Yêu cầu: JDK 17, Docker Desktop và PowerShell.

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

- [Documentation Guide](docs/000_DOCS_GUIDE.md)
- [Senior Roadmap](docs/001_SENIOR_JAVA_INTERVIEW_ROADMAP.md)
- [System Context](docs/architecture/system-context.md)
- [Business Flows](docs/business_flows.md)
- [API Contract](docs/api_endpoints_specification.md)
- [Security Flow](docs/authorization_flow.md)
- [Redis Guide](docs/redis_usage_guide.md)
- [AI Agent Engineering System](docs/003_AI_AGENT_ENGINEERING_SYSTEM.md)
- Repository guardrails: [AGENTS.md](AGENTS.md)

Legacy phase plans, generic prompts và design giả định đã được giữ tại [Documentation Archive](docs/archive/README.md).

## Engineering constraints

- Controller mỏng, DTO-only API, service-owned transaction boundary.
- Không dùng JPA relationship annotations; lưu foreign ID tường minh.
- PostgreSQL là source of truth; cache/event side effect có consistency policy rõ.
- Simulation-first cho media/payment/external integration.
- Mọi scale/performance claim phải có workload và measurement.
- Mọi state/money/security case phải có negative hoặc concurrency/failure test phù hợp.

# Current Implementation Map

> Trạng thái: `CURRENT CODE COVERAGE`<br>
> Cập nhật: 2026-07-25<br>
> Đây không còn là roadmap điều phối công việc. Roadmap học chuẩn là [Senior Java Interview Roadmap](../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md).<br>
> Product phase plan 1-12 cũ được lưu tại [archive](../archive/2025-product-roadmap/implementation/overview.md).

## 1. Từ phase cũ sang learning cases

```mermaid
flowchart TB
    P["Legacy phases 1-4<br/>demo coverage"] --> S["Stage 0<br/>stabilize lab"]
    S --> C["Correctness cases<br/>state and data"]
    C --> E["Distributed cases<br/>cache and events"]
    E --> O["Operations cases<br/>observe and recover"]
    O --> M["Architecture cases<br/>scale and extract"]

    style P fill:#FF9800,stroke:#fff,stroke-width:2px,color:#fff
    style S fill:#E91E63,stroke:#fff,stroke-width:2px,color:#fff
    style C fill:#2196F3,stroke:#fff,stroke-width:2px,color:#fff
    style E fill:#009688,stroke:#fff,stroke-width:2px,color:#fff
    style O fill:#9C27B0,stroke:#fff,stroke-width:2px,color:#fff
    style M fill:#4CAF50,stroke:#fff,stroke-width:2px,color:#fff
```

## 2. Capability coverage hiện tại

| Capability | Evidence trong code | Status |
| --- | --- | --- |
| Foundation | Spring Boot 3.4, Java 17 declared trong POM, PostgreSQL, Redis, RabbitMQ config, Docker Compose | Demo available; JDK toolchain/runtime drift và Java 21 baseline pending |
| Simulation | Deposit DTO, infra test endpoints, RTMP webhook có thể gọi thủ công | Demo available; `CFG-01` profile isolation pending |
| Authentication | Register/login/refresh/logout/session/RBAC | Demo available; SEC-01, SEC-06, SEC-02 pending |
| Stream | Create/list/detail/my, webhook start/end, HLL viewers | Demo available; SEC-03, SEC-05, CON-01, DB-01 pending |
| Durable wallet/gift | Không có wallet entity, ledger, gift hoặc transaction | Not implemented |
| Chat/WebSocket | Dependency only | Not implemented |
| Business messaging | RabbitMQ test publish only | Not implemented |
| Kafka/microservice/replica/partition | Không có implementation | Not implemented |
| Testing/observability | Một smoke test, application/P6Spy log | Laboratory not stable |

## 3. Learning routing (không sở hữu cursor)

- Implementation map chỉ sở hữu capability/code evidence hiện tại; không sao chép active case, checkpoint hoặc next action.
- Full execution queue, item kind và priority lấy từ [Senior Roadmap](../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#61-execution-queue-hiện-tại).
- Active case/checkpoint/next action lấy từ [Learning System](../learning/index.md); case header sở hữu status và pause/closure reason của chính case đó.

Không phục hồi Phase 5-12 thành active checklist. Business idea hữu ích từ chúng được map vào case backlog trong Senior roadmap khi case được kích hoạt.

## 4. Status rules

- `Demo available`: happy path có code, không đồng nghĩa correct/resilient/operable.
- `Not implemented`: dependency, TODO hoặc document không tính là implementation.
- Capability chỉ tăng maturity khi có link tới test, experiment, metric hoặc runbook tương ứng.
- Current behavior lấy từ code/test; intended behavior lấy từ business/API contract.

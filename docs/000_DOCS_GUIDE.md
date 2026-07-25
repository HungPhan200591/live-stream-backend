# Documentation Guide

> Cập nhật: 2026-07-25
> Project được tổ chức theo hai mục tiêu song song: mô phỏng business livestream và luyện năng lực Senior Java Backend bằng case có bằng chứng.

## 1. Bắt đầu ở đâu

| Nhu cầu | Đọc trước | Sau đó |
| --- | --- | --- |
| Xem roadmap học Senior | [Senior Interview Roadmap](001_SENIOR_JAVA_INTERVIEW_ROADMAP.md) | [Current State & Gaps](002_CURRENT_STATE_AND_GAP_ANALYSIS.md) |
| Chọn case tiếp theo | [Case backlog](001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#6-case-backlog-ưu-tiên) | [Learning Case Template](templates/LEARNING_CASE_TEMPLATE.md) |
| Hiểu hiện trạng code | [Current State & Gaps](002_CURRENT_STATE_AND_GAP_ANALYSIS.md) | source code + tests |
| Hiểu business | [Business Flows](business_flows.md) | [System Design](system_design_livestream.md) |
| Implement feature | phase file liên quan trong `implementation/` | API spec + `AGENTS.md` |
| Làm việc với Codex | [AI Agent Engineering System](003_AI_AGENT_ENGINEERING_SYSTEM.md) | `AGENTS.md`, `PLANS.md`, project skill |
| Tra API/quyền | [API Specification](api_endpoints_specification.md) | [Authorization Flow](authorization_flow.md) |
| Điều tra Redis | [Redis Usage Guide](redis_usage_guide.md) | code/test/runtime evidence |

## 2. Hai roadmap, hai trách nhiệm

### Learning roadmap

[001_SENIOR_JAVA_INTERVIEW_ROADMAP.md](001_SENIOR_JAVA_INTERVIEW_ROADMAP.md) quyết định thứ tự học và độ sâu kỹ thuật. Nó trả lời:

- case nào nên làm trước;
- theory/invariant/failure nào cần hiểu;
- test, metric, query plan hoặc runbook nào phải có;
- khi nào mới nên thêm Kafka, replica, partition hoặc microservice.

### Product implementation roadmap

[implementation/000_ROADMAP.md](implementation/000_ROADMAP.md) và phase files mô tả feature business dự kiến. Chúng trả lời:

- domain capability nào cần có;
- API và flow nào thuộc phase;
- dependency business giữa các phase.

`DONE` trong product roadmap chỉ có nghĩa feature demo đã có. Nó không tự động tương đương correctness, resilience hoặc production readiness. Dùng maturity M0-M4 trong [current-state assessment](002_CURRENT_STATE_AND_GAP_ANALYSIS.md#6-maturity-model-dùng-cho-project).

## 3. Nguồn chuẩn theo loại thông tin

`Business flows → API contract + system design → product phase → learning case → ADR/experiment + code/tests → runtime evidence → interview notes`

`Senior roadmap → learning case`

- Business intent: `business_flows.md`.
- Expected REST contract: `api_endpoints_specification.md`.
- Current behavior: source code và tests.
- Architecture decision: ADR.
- Performance claim: reproducible experiment.
- Agent behavior: `AGENTS.md` và skill đã trigger.
- Status: roadmap tương ứng kèm link tới evidence gate.

Khi docs và code mâu thuẫn, ghi nhận drift. Không sửa một bên âm thầm để làm cho checklist trông đồng nhất.

## 4. Tài liệu hiện có

### Core contracts

- [Business Flows](business_flows.md)
- [System Design](system_design_livestream.md)
- [API Endpoints Specification](api_endpoints_specification.md)
- [API Roadmap](api_roadmap.md)
- [Authorization Flow](authorization_flow.md)
- [Coding Standards](coding_standards.md)
- [Redis Usage Guide](redis_usage_guide.md)
- [Webhook Concepts](concepts/webhooks.md)

### Implementation backlog

- [Implementation Overview](implementation/000_ROADMAP.md)
- `implementation/phase-1-foundation.md` đến `phase-12-notifications.md`

Phase docs là backlog/implementation guide, không phải bằng chứng duy nhất cho behavior hiện tại.

### Usage/reference

- [Data Initialization](usage/data-initialization.md)
- [P6Spy SQL Logging](usage/p6spy-sql-logging.md)
- [Security Best Practices](usage/security_best_practices.md)
- [Codex PostgreSQL MCP](codex/mcp-postgres.md)

Các file trong `usage/prompt/` được giữ như learning reference cũ. Workflow Codex chuẩn nằm trong [AI Agent Engineering System](003_AI_AGENT_ENGINEERING_SYSTEM.md) và `.agents/skills/`.

## 5. Reading path theo công việc

### Bóc tách một learning case

1. Đọc stage/case liên quan trong Senior roadmap.
2. Đọc snapshot hiện trạng và code path thực tế.
3. Chỉ đọc business/API/Redis/webhook docs liên quan.
4. Tạo case từ template.
5. Tái hiện failure trước khi chọn solution.
6. Implement, đo, inject failure, review và tự trình bày lại.

### Implement một product feature

1. Đọc business use case.
2. Đọc API/authorization contract.
3. Đọc phase file liên quan.
4. Kiểm tra feature có learning case active hay không.
5. Dùng `$implement-livestream-feature` và plan theo `PLANS.md` nếu rủi ro.
6. Cập nhật code, tests, OpenAPI, `.http` và docs cùng behavior.

### Chẩn đoán lỗi

1. Dùng `$diagnose-livestream-backend`.
2. Tái hiện tối thiểu và thu runtime evidence.
3. Phân biệt root cause, symptom và warning không liên quan.
4. Chỉ implement fix khi task đã ủy quyền.
5. Nếu lỗi tạo learning value mới, bổ sung vào active case.

### Review

1. Dùng `$review-livestream-change`.
2. Review theo correctness, security, transaction/concurrency, distributed state, API, test và docs.
3. So maturity claim với evidence gate.

## 6. Quy ước tài liệu mới

- Tên case: `CASE-<DOMAIN>-<NN>-<slug>.md`.
- Tên ADR: `ADR-<NNNN>-<slug>.md`.
- Tên experiment: `EXP-<DOMAIN>-<NN>-<slug>.md`.
- Tên runbook: `RUNBOOK-<SYSTEM>-<failure>.md`.
- Mỗi file có status, owner/người học, ngày cập nhật và link tới source evidence.
- Không copy theory dài từ official docs; tự diễn giải và link nguồn.
- Không paste raw benchmark rất lớn vào case; link artifact và giữ summary có context.
- Không tạo file/folder rỗng chỉ để khớp architecture tương lai.

## 7. Khi nào cập nhật

| Event | Artifact cần cập nhật |
| --- | --- |
| Behavior/API thay đổi | code/test, OpenAPI, `.http`, API spec |
| Hoàn thành product feature | phase file + implementation overview + evidence link |
| Hoàn thành learning case | case file + Senior roadmap maturity/status |
| Chọn architecture trade-off | ADR + system/module design |
| Có performance conclusion | experiment + case + relevant ADR |
| Thay key/event/schema | Redis/event catalog + compatibility plan |
| Có incident/fault drill | runbook + observability doc + case |
| Tạo/nâng skill | skill validation + eval suite + skill catalog section |

## 8. Known drift tại snapshot này

- Phase 4 đã có code và phase/API docs ghi `DONE`; implementation overview đã được đồng bộ trong snapshot này nhưng README vẫn ghi Phase 3/Next Phase 4.
- Docs guide cũ trỏ đến `docs/agent/rules/*` không tồn tại; nguồn rule thật là `AGENTS.md`.
- Nhiều capability trong phase docs mới ở mức thiết kế/dependency, chưa có test/runtime evidence.

Chi tiết và priority remediation nằm trong [Current State & Gap Analysis](002_CURRENT_STATE_AND_GAP_ANALYSIS.md).

## 9. Definition of healthy documentation

- Người mới tìm được nguồn chuẩn trong dưới 5 phút.
- Một fact có một active owner; docs khác link tới owner đó.
- Status có evidence link và maturity rõ.
- Diagram có cả boundary/failure khi quyết định phụ thuộc chúng.
- Lệnh verification đã chạy được ghi khác với lệnh chỉ đề xuất.
- Agent chỉ nạp docs theo concern, không cần đọc toàn bộ repository.

# Coding Standards cho Senior Backend Lab

> Trạng thái: `ACTIVE`<br>
> Cập nhật: 2026-07-25<br>
> Rule bắt buộc nằm ở [`AGENTS.md`](../../AGENTS.md); tài liệu này giải thích cách áp dụng.<br>
> Bản example-heavy 2025 được lưu tại [archive](../archive/2025-reference/coding-standards.md).

## 1. Thứ tự ưu tiên

1. Business invariant và security contract.
2. Correct transaction/concurrency behavior.
3. Test và observability chứng minh behavior.
4. Readability và khả năng thay đổi.
5. Optimization sau khi có measurement.

## 2. Boundary chuẩn

| Layer | Trách nhiệm | Không làm |
| --- | --- | --- |
| Controller | Parse/validate request, authorization annotation, gọi service, trả `ApiResponse<DTO>` | Business transaction, entity response, catch-all |
| Service | Invariant, orchestration, transaction boundary, cache/event policy | Phụ thuộc HTTP object nếu không cần thiết |
| Repository | Query/lock/update có chủ đích | Chứa business workflow |
| DTO | Contract theo audience | Dùng chung public DTO với secret/internal field |
| Entity | Durable state và constraint mapping | JPA relationship graph tự động |

## 3. Persistence và transaction

- Lưu foreign ID tường minh; không thêm `@ManyToOne`, `@OneToMany`, `@OneToOne`, `@ManyToMany`.
- Dùng Flyway cho schema khi Stage 0 được triển khai; `ddl-auto=update` hiện là gap.
- `@Transactional` đặt ở service write boundary, ngắn và không bao remote call.
- Cache/broker side effect không atomic với DB. Ghi rõ after-commit, invalidation hoặc outbox strategy.
- Mọi money/state transition phải có database constraint hoặc conditional update phù hợp, không chỉ `if` trong Java.

## 4. API và security

- Không trả entity trực tiếp; DTO phải phù hợp caller audience.
- URL rule cho pattern rộng, `@PreAuthorize` cho role/ownership chi tiết.
- Endpoint thay đổi phải đồng bộ OpenAPI, `.http`, contract và negative tests.
- Không log credential, token, stream key, webhook secret hoặc payload nhạy cảm.
- Development endpoint phải dùng profile/condition, không chỉ ghi comment “dev only”.

## 5. Redis và messaging

- Key có namespace/version/owner, value type, TTL và invalidation rule.
- PostgreSQL là source of truth cho durable money/security state.
- Consumer phải có idempotency, retry budget, poison-message/DLQ và ACK/offset policy.
- Event payload là versioned contract DTO, không phải JPA entity.

## 6. Test strategy

- Unit: branch và invariant thuần.
- Slice/MockMvc: HTTP status, payload, validation và authorization.
- Integration: PostgreSQL/Redis/broker semantics bằng môi trường tái lập.
- Concurrency: nhiều actor, start barrier và invariant query; không dựa vào `sleep` duy nhất.
- Performance: dataset/workload/baseline/before-after; không kết luận từ một lần chạy.

## 7. Definition of Done

- Compile và test liên quan pass.
- Happy path, negative path và failure/concurrency path phù hợp đã được kiểm tra.
- Security, transaction, cache và async crash window đã được giải thích.
- Docs/API artifacts không drift với behavior mới.
- Learning case có evidence và phần teach-back nếu thay đổi thuộc Senior roadmap.

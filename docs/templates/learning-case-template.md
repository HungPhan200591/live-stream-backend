# CASE-XXX: <Tên bài toán>

> Status: `PROPOSED | ACTIVE | PAUSED | EVIDENCE_READY | CLOSED`<br>
> Maturity target: `M1 | M2 | M3 | M4`<br>
> Roadmap stage: `<stage>`<br>
> Prerequisites: `<case/doc/test>`<br>
> Owner: `<name>`<br>
> Updated: `YYYY-MM-DD`

## 1. Interview objective

### Câu hỏi chính

`<Câu hỏi phỏng vấn case này phải giúp trả lời>`

### Follow-up dự kiến

- `<Why?>`
- `<Failure case?>`
- `<Alternative?>`
- `<Scale/operation?>`

### Năng lực cần chứng minh

- Theory:
- Implementation:
- Measurement:
- Trade-off communication:

## 2. Problem và invariant

### Hành vi mong đợi

`<Observable behavior>`

### Invariant

1. `<Điều luôn phải đúng>`
2. `<Điều luôn phải đúng>`

### Không nằm trong scope

- `<Non-goal>`

## 3. Knowledge links và case-specific interpretation

### Reusable knowledge

- Core theory:
- Deep-dive:
- Question bank:

Không sao chép theory đầy đủ vào case. Nếu knowledge cần dùng lại cho case khác, cập nhật theory source of truth rồi link về đây.

### Áp dụng vào case này

`<Theory/invariant nào giải thích current project failure và boundary nào chỉ riêng case này>`

### Misconception phát hiện từ case

- `<Link theory update hoặc negative test; không tạo bản giải thích cạnh tranh>`

## 4. Current baseline

### Code path

`request -> controller -> service -> database/cache/broker`

### Bằng chứng hiện tại

- Source:
- Test:
- Runtime/log/query:
- Documentation drift:

### Failure reproducer

1. Setup:
2. Action:
3. Expected failure:
4. Verification command:

## 5. Hypothesis

> Nếu `<change>`, thì `<metric/invariant>` sẽ `<expected result>` dưới `<workload/failure>`, vì `<mechanism>`.

### Success criteria

| Signal | Baseline | Target | Cách đo |
| --- | --- | --- | --- |
| | | | |

## 6. Alternatives và decision

| Option | Correctness | Complexity | Performance | Operability | Khi nên dùng |
| --- | --- | --- | --- | --- | --- |
| A | | | | | |
| B | | | | | |

### Chọn

`<Decision và lý do>`

### Không chọn

`<Rejected option và điều kiện có thể khiến quyết định thay đổi>`

### ADR

`<Link hoặc N/A nếu quyết định chỉ cục bộ>`

## 7. Design

### Happy path

`Client → Application → Database: durable change → Application: commit confirmed → Client: response`

### Failure/crash points

| Point | Failure | Expected behavior | Recovery |
| --- | --- | --- | --- |
| F1 | | | |

### Data/API/event changes

- Migration:
- API contract:
- Cache key/TTL:
- Event schema/key/order:
- Compatibility:

### Security

- Actor/role/ownership:
- Asset/secret:
- Abuse/replay threat:
- Audit/redaction:

## 8. Implementation checkpoints

- [ ] Minimal failing test/reproducer
- [ ] Domain invariant/constraint
- [ ] Transaction boundary
- [ ] Infrastructure integration
- [ ] Negative/concurrency/failure tests
- [ ] OpenAPI/`.http`/docs sync
- [ ] Observability
- [ ] Diff review

## 9. Verification matrix

| Level | Scenario | Tool/command | Expected |
| --- | --- | --- | --- |
| Unit | | | |
| Integration | | | |
| Security | | | |
| Concurrency | | | |
| Contract | | | |
| Load | | | |
| Fault | | | |

Không bắt buộc mọi hàng cho mọi case. Ghi `N/A` kèm lý do thay vì bỏ trống.

## 10. Experiment report

### Environment

- Git commit:
- JDK/application version:
- CPU/RAM/OS:
- Infrastructure versions:
- Dataset size/distribution:
- Workload/concurrency/duration/warm-up:

### Raw results

`<Link đến artifact hoặc bảng số liệu chưa diễn giải>`

### Summary

| Metric | Before | After | Delta |
| --- | --- | --- | --- |
| | | | |

### Interpretation

- Hypothesis được hỗ trợ/bác bỏ:
- Confounding factors:
- Điều chưa đo được:

## 11. Observability và operations

- Log event + fields:
- Metric + labels/cardinality:
- Trace spans:
- SLI/SLO impact:
- Alert:
- Runbook/replay/recovery:

## 12. Review findings và residual risk

| Severity | Finding | Resolution/status |
| --- | --- | --- |
| | | |

## 13. Interview debrief

### Canonical interview note

`<Link docs/learning/interview-notes/...; chỉ tạo sau evidence>`

### Câu trả lời 2 phút

`<Tóm tắt Problem -> invariant -> decision -> evidence -> trade-off; full personal answer nằm trong interview note>`

### Deep dive 15 phút

1. Context và failure.
2. Theory/mechanism.
3. Alternatives.
4. Implementation.
5. Test/measurement.
6. Scale/operation/evolution.

### Điều tôi trả lời chưa tốt

- `<Gap cần quay lại>`

### Flash questions

1. Q:
   A:

## 14. Closure gate

- [ ] Invariant có automated evidence
- [ ] Failure đã được tái hiện trước hoặc bằng fault test
- [ ] Claim về performance có số liệu
- [ ] Decision có alternatives/trade-off
- [ ] Security/transaction/cache/event concerns đã review
- [ ] Recovery/observability phù hợp maturity target
- [ ] Docs/contract/status đã sync
- [ ] Tôi tự giải thích được mà không đọc AI output

## 15. Links

- Code:
- Tests:
- ADR:
- Experiment:
- Dashboard/runbook:
- Official references:

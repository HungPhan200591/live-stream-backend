# Timeouts, Cancellation and Pool Exhaustion

> Type: `CORE`<br>
> Domain: `distributed-systems`<br>
> Target depth: `D3 — phân bổ deadline, tái hiện pool exhaustion và chứng minh cancellation giải phóng resource`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: HTTP client lifecycle, threads/executors and connection pools<br>
> Related cases: [`RECONNECT-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases), [`API-UC-01`](../../../../use-case-catalog.md#32-architect-và-expert-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

Source canonical cho [timeout question bank](../../question-bank/timeouts-cancellation-and-pool-exhaustion.md).

## 1. Learning objectives

1. Phân biệt DNS, connect, TLS, pool-acquire, read/write và end-to-end deadline.
2. Propagate cancellation/deadline qua downstream work và resource ownership.
3. Tái hiện queue/pool saturation, đo wait/in-use/timeout và chọn bounded overload behavior.

## 2. Mental model bằng lời của tôi

`LEARNER TODO — vẽ một request budget qua queue, pool acquire, connect và downstream response; chỉ remaining budget ở mỗi hop.`

## 3. Cơ chế cốt lõi

Timeout riêng lẻ giới hạn một phase; deadline giới hạn toàn operation. Nếu mỗi downstream hop nhận full timeout mới, tổng latency có thể vượt client budget. Pool acquire timeout khác connect/read timeout: request có thể chưa ra network nhưng đã hết thời gian trong queue.

Cancellation là protocol/cooperation, không phải chỉ trả response sớm. Khi caller bỏ cuộc, task/downstream call/DB query phải được signal và resource phải được release; nếu work tiếp tục, hệ thống tiêu capacity cho kết quả không ai dùng.

Little's Law cho thấy concurrency xấp xỉ arrival rate nhân time-in-system; latency tăng kéo concurrency/pool demand tăng và có thể tạo feedback loop. Bounded queue/pool làm overload lộ ra; unbounded queue đổi rejection thành memory/latency collapse.

## 4. Invariants và boundaries

1. Mỗi external dependency có phase timeout và end-to-end budget phù hợp SLO.
2. Pool/queue bounded; acquisition wait có metric và failure behavior rõ.
3. Timeout/cancellation luôn release connection, permit, lock và task ownership.
4. Remote call không nằm trong DB transaction/lock nếu không có bounded justification.
5. Timeout outcome có thể ambiguous; retry chỉ khi operation semantics cho phép.

## 5. Failure modes

| Failure | Causal chain | Symptom |
| --- | --- | --- |
| Pool starvation | Slow downstream giữ connections | Acquire timeout dù host reachable |
| Deadline inflation | Mỗi hop reset timeout | Client hết hạn, backend vẫn làm |
| Cancellation leak | Future/socket/query không cancel | Zombie work, pool không hồi |
| Unbounded queue | Arrival > service rate | Heap/p99 collapse |
| Transaction + remote wait | Connection/lock giữ lâu | DB pool/lock amplification |

## 6. Trade-off matrix

| Control | Bảo vệ | Trade-off |
| --- | --- | --- |
| Short timeout | Capacity/fail fast | False timeout khi tail hợp lệ |
| Deadline propagation | End-to-end bound | Cross-service contract/context |
| Bounded pool/queue | Resource cap | Rejection cần xử lý |
| Cancellation | Reclaim wasted work | Driver/protocol cooperation |
| Separate pool/bulkhead | Fault isolation | Lower utilization, tuning cost |

## 7. Deep-dive và case

- [Timeout, retry, circuit, bulkhead and overload control](../deep-dives/timeout-retry-circuit-bulkhead-and-overload-control.md).
- `RECONNECT-UC-01`: reconnect surge, handshake/downstream budgets.
- `API-UC-01`: client/gateway/server timeout alignment.

## 8. Self-check

1. **Question:** Pool-acquire, connect, read timeout và deadline khác nhau thế nào?<br>**My answer:** `LEARNER TODO`
2. **Question:** Cancellation đã trả response nhưng query vẫn chạy gây hậu quả gì?<br>**My answer:** `LEARNER TODO`
3. **Question:** Metric nào phân biệt downstream chậm với pool cấu hình thiếu?<br>**My answer:** `LEARNER TODO`

## 9. References

- [Java SE 21 — `HttpClient`](https://docs.oracle.com/en/java/javase/21/docs/api/java.net.http/java/net/http/HttpClient.html)
- [Reactive Streams specification](https://www.reactive-streams.org/)

## 10. Teach-back checklist

- [ ] Tôi phân bổ deadline thay vì đặt một timeout tùy ý.
- [ ] Tôi nối cancellation với resource release.
- [ ] Tôi giải thích queue/pool feedback loop bằng số đo.
- [ ] Timeout/pool evidence vẫn `NOT RUN`.

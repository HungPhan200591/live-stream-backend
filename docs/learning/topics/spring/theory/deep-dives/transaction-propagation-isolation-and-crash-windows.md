# Transaction Propagation, Isolation and Crash Windows

> Type: `DEEP_DIVE`<br>
> Domain: `spring`<br>
> Target depth: `D3 — dựng lịch concurrent/commit/crash và chứng minh invariant bằng integration test`<br>
> Status: `DRAFT`<br>
> Evidence status: `NOT RUN`<br>
> Prerequisites: [Transaction core](../core/transaction-rollback-and-propagation.md)<br>
> Related cases: [`GIFT-UC-01`](../../../../use-case-catalog.md#gift-uc-01), [`EVT-UC-01`](../../../../use-case-catalog.md#31-foundation-và-senior-cases)<br>
> Owner: `Project learner; Codex assists`<br>
> Updated: `2026-07-26`

## 1. Physical resource reasoning

Propagation phải được kể bằng connections/resources thực tế. Outer `REQUIRED` giữ connection A; inner `REQUIRES_NEW` suspend A và cần connection B. Khi nhiều request cùng giữ A rồi chờ B, pool nhỏ có thể đứng dù database không deadlock theo row locks. `NESTED` dùng savepoint trên A nếu resource manager hỗ trợ; rollback savepoint không phát event/side effect bên ngoài ngược lại.

Read-only metadata thường là optimization/hint và intent, không phải security guarantee tuyệt đối. Flush có thể xảy ra trước commit hoặc query tùy persistence context; SQL executed không đồng nghĩa transaction committed.

## 2. Isolation anomalies

| Anomaly/problem | Câu hỏi cần trả lời |
| --- | --- |
| Dirty/non-repeatable/phantom read | DB isolation cụ thể ngăn gì? |
| Lost update | Write có condition/version/lock không? |
| Write skew | Invariant trải nhiều rows được khóa/constraint thế nào? |
| Stale persistence context | Query/refresh/clear semantics? |
| Deadlock | Lock order và retry owner? |

Tên isolation level không đủ; PostgreSQL implementation và access pattern cụ thể quyết định outcome. Unique/check/exclusion constraint, conditional update, optimistic version hoặc pessimistic lock thường là phần proof của invariant.

## 3. Crash windows

| Sequence | Gap |
| --- | --- |
| DB commit -> publish broker | Crash làm mất event |
| Publish -> DB commit | Consumer thấy event của rollback |
| DB commit -> cache update | Stale cache khi crash/update fail |
| Remote call -> DB commit | External success, local rollback |
| DB commit -> HTTP response | Client timeout, outcome ambiguous |

Outbox đưa state change và event intent vào cùng DB transaction; relay vẫn at-least-once nên consumer/idempotency cần xử lý duplicate. Cache thường dùng invalidate/update-after-commit với stale window và recovery policy, không phải atomicity giả.

## 4. Reproducer plan

1. Propagation test assert outer/inner commit/rollback combinations và `UnexpectedRollbackException`.
2. Pool test: concurrent outer transactions gọi `REQUIRES_NEW`, đo in-use/pending/timeouts.
3. Isolation test dùng barriers để ép lịch lost update/write skew/lock wait.
4. Kill/fault injection tại từng dual-write boundary; query DB/outbox/cache/broker state sau restart.

Không có experiment nào đã chạy; evidence `NOT RUN`.

## 5. Design decisions

| Invariant | Preferred owner |
| --- | --- |
| Unique business identity | DB constraint + mapped conflict |
| Aggregate concurrent update | Version/conditional update/lock |
| DB state + event intent | Transactional outbox |
| Cache freshness | Version/TTL/invalidation/reconciliation |
| External payment + local ledger | Idempotency + state machine + reconciliation |

## 6. Self-check

1. **Question:** Vì sao `REQUIRES_NEW` có thể làm cạn pool?<br>**My answer:** `LEARNER TODO`
2. **Question:** Isolation level và optimistic locking giải quyết hai lớp vấn đề nào?<br>**My answer:** `LEARNER TODO`
3. **Question:** Vẽ crash matrix cho DB, broker và HTTP response.<br>**My answer:** `LEARNER TODO`

## 7. References

- [Spring — Transaction Propagation](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-propagation.html)
- [PostgreSQL — Transaction Isolation](https://www.postgresql.org/docs/current/transaction-iso.html)

## 8. Teach-back checklist

- [ ] Tôi reason bằng connection/savepoint/commit thực tế.
- [ ] Tôi ép được concurrency schedule bằng barrier.
- [ ] Tôi có crash-window và reconciliation story.
- [ ] Evidence vẫn `NOT RUN`.

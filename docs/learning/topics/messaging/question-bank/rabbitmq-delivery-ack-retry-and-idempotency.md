# Messaging Interview Question Bank — RabbitMQ Delivery, Ack, Retry and Idempotency

> Status: `DRAFT`<br>
> Domain owner: `RabbitMQ`<br>
> Active slice: `NONE`; preview target: `MQ-01`<br>
> Related roadmap: [Stage 5](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-5---messaging-fundamentals-rabbitmq-và-kafka)<br>
> Related depth rubric: [Messaging](../../../knowledge-depth-rubric.md#316-rabbitmq-kafka-và-event-driven-workflow--p1-target-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/messaging/theory/core/rabbitmq-delivery-ack-retry-and-idempotency.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `MQ-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `RMQ-DEL-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### RMQ-DEL-001 — `FOUNDATION`
**Question:** Exchange, queue, binding và routing key có vai trò gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Publish route và storage/consumption.<br>
**Answer outline:** Producer publish exchange với routing key; bindings quyết định queue nhận; queue giữ message cho consumers. Direct/topic/fanout/headers exchange có routing semantics khác nhau.<br>
**Required trade-offs:** Flexible routing tăng topology/governance complexity.<br>
**Follow-up ladder:** Default exchange? Competing consumers?<br>
**Red flags:** Producer gửi thẳng consumer.<br>
**Evidence:** Theory `NOT CREATED`; case `MQ-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-002 — `FOUNDATION`
**Question:** Consumer ack, nack/reject và requeue khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Delivery lifecycle và poison loop.<br>
**Answer outline:** Ack xác nhận xử lý xong; nack/reject báo thất bại, có thể requeue hoặc dead-letter theo config. Ack sau durable side effects; requeue vô hạn poison message tạo hot loop.<br>
**Required trade-offs:** Early ack tăng throughput nhưng mất message; late ack tăng duplicate/redelivery.<br>
**Follow-up ladder:** Multiple ack? Consumer crash?<br>
**Red flags:** Ack ngay khi nhận để tránh duplicate.<br>
**Evidence:** Theory `NOT CREATED`; case `MQ-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-003 — `FOUNDATION`
**Question:** At-most-once và at-least-once delivery khác nhau; RabbitMQ có exactly-once không?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Loss vs duplicate và end-to-end semantics.<br>
**Answer outline:** At-most-once có thể mất không duplicate; at-least-once redeliver nên consumer phải idempotent. Broker ack/publisher confirm không tạo exactly-once business side effect xuyên DB/network.<br>
**Required trade-offs:** At-least-once ưu tiên durability đổi dedup cost.<br>
**Follow-up ladder:** Redelivered flag đáng tin tới đâu?<br>
**Red flags:** Durable queue + manual ack = exactly-once.<br>
**Evidence:** Theory `NOT CREATED`; case `RMQ-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-004 — `FOUNDATION`
**Question:** Durable queue, persistent message và publisher confirm bảo vệ các failure khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Topology survival, message persistence và publish acceptance.<br>
**Answer outline:** Durable giữ queue definition qua restart; persistent yêu cầu message được lưu theo broker guarantees; confirms cho producer biết broker accepted/nack nhưng producer vẫn cần retry/idempotency và handle unroutable returns.<br>
**Required trade-offs:** Durability/confirm giảm throughput/tăng latency.<br>
**Follow-up ladder:** Mandatory flag? Quorum queue?<br>
**Red flags:** Persistent flag một mình chống mọi loss.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-005 — `SENIOR`
**Question:** Consumer idempotency được thiết kế bằng inbox/business key thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Atomic dedup với side effect.<br>
**Answer outline:** Stable event/message ID hoặc business operation key; unique inbox record và domain write trong cùng DB transaction; duplicate trả success/skip. Định nghĩa retention, payload version và concurrent claims.<br>
**Required trade-offs:** Inbox storage/cleanup vs duplicate safety horizon.<br>
**Follow-up ladder:** External side effect? Bloom filter?<br>
**Red flags:** In-memory Set dedupe production.<br>
**Evidence:** Theory `NOT CREATED`; design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-006 — `SENIOR`
**Question:** Retry queue/DLX nên thiết kế thế nào để tránh poison-message loop?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Bounded attempts, delay, classification và parking lot.<br>
**Answer outline:** Phân loại transient/permanent; bounded attempt metadata, delayed retry queues với backoff+jitter, cuối cùng DLQ/parking lot; alert, inspect/redrive có auth/audit và idempotency.<br>
**Required trade-offs:** Retry tăng recovery nhưng trì hoãn queue và khuếch đại load.<br>
**Follow-up ladder:** TTL/DLX cycle? Error headers?<br>
**Red flags:** Nack requeue=true vô hạn.<br>
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-007 — `SENIOR`
**Question:** Prefetch/concurrency ảnh hưởng throughput, fairness và memory thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Unacked window, task duration và backpressure.<br>
**Answer outline:** Prefetch cao giữ nhiều unacked trên consumer, tăng utilization nhưng giảm fairness/redelivery burst/memory; tune theo processing time/DB capacity, bounded concurrency và observe unacked/lag.<br>
**Required trade-offs:** Throughput vs fairness/failure blast radius.<br>
**Follow-up ladder:** Per-consumer/channel QoS?<br>
**Red flags:** Max prefetch luôn tốt nhất.<br>
**Evidence:** Theory `NOT CREATED`; load test `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-008 — `SENIOR`
**Question:** DB commit và publish RabbitMQ tránh lost event bằng outbox thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Dual-write window, relay confirms và duplicates.<br>
**Answer outline:** Ghi business row+outbox cùng DB transaction; relay claim/publish, chờ confirm rồi mark; crash có thể republish nên consumer idempotent; monitor lag/failures và cleanup.<br>
**Required trade-offs:** Reliability đổi polling/CDC, latency và ops complexity.<br>
**Follow-up ladder:** Multiple relay workers? Ordering?<br>
**Red flags:** Publish trong `@Transactional` là atomic với DB.<br>
**Evidence:** Theory `NOT CREATED`; design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-009 — `ARCHITECT`
**Question:** Thiết kế event contract/versioning/topology ownership cho nhiều team thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Schema compatibility, routing và lifecycle.<br>
**Answer outline:** Event owner/schema/version, additive evolution, immutable semantic type, consumer contract tests; topology as code với queue/DLX ownership, permissions, retention và deprecation telemetry.<br>
**Required trade-offs:** Governance giảm coupling rủi ro nhưng tăng coordination.<br>
**Follow-up ladder:** Event notification vs event-carried state?<br>
**Red flags:** Dùng chung một queue cho mọi consumer.<br>
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-010 — `EXPERT`
**Question:** Queue backlog tăng dù consumer CPU thấp: điều tra từ broker tới DB thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Publish/deliver/ack rates, unacked, waits và poison retries.<br>
**Answer outline:** Correlate ready/unacked, rates, consumer count/prefetch, redelivery/DLQ, connection/channel flow control; thread dumps, pool waits, DB locks/latency. CPU thấp thường là I/O wait. Contain retries và scale chỉ sau bottleneck.<br>
**Required trade-offs:** Tăng consumers có thể làm DB sập nhanh hơn.<br>
**Follow-up ladder:** Quorum disk alarm? Hot routing key?<br>
**Red flags:** Consumer CPU thấp nên chắc thiếu CPU.<br>
**Evidence:** Incident drill `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `MQ-01` active, fault-inject ack/crash/retry và lưu broker/DB evidence; không đổi/reuse stable IDs.

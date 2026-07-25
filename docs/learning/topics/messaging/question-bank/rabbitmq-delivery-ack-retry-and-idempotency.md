# Messaging Interview Question Bank — RabbitMQ Delivery, Ack, Retry and Idempotency

> Status: `DRAFT`  
> Domain owner: `RabbitMQ`  
> Active slice: `NONE`; preview target: `MQ-01`  
> Related roadmap: [Stage 5](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-5---messaging-fundamentals-rabbitmq-và-kafka)  
> Related depth rubric: [Messaging](../../../knowledge-depth-rubric.md#316-rabbitmq-kafka-và-event-driven-workflow--p1-target-d3)  
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/messaging/theory/core/rabbitmq-delivery-ack-retry-and-idempotency.md`  
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
**Question:** Exchange, queue, binding và routing key có vai trò gì?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Publish route và storage/consumption.  
**Answer outline:** Producer publish exchange với routing key; bindings quyết định queue nhận; queue giữ message cho consumers. Direct/topic/fanout/headers exchange có routing semantics khác nhau.  
**Required trade-offs:** Flexible routing tăng topology/governance complexity.  
**Follow-up ladder:** Default exchange? Competing consumers?  
**Red flags:** Producer gửi thẳng consumer.  
**Evidence:** Theory `NOT CREATED`; case `MQ-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-002 — `FOUNDATION`
**Question:** Consumer ack, nack/reject và requeue khác nhau thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Delivery lifecycle và poison loop.  
**Answer outline:** Ack xác nhận xử lý xong; nack/reject báo thất bại, có thể requeue hoặc dead-letter theo config. Ack sau durable side effects; requeue vô hạn poison message tạo hot loop.  
**Required trade-offs:** Early ack tăng throughput nhưng mất message; late ack tăng duplicate/redelivery.  
**Follow-up ladder:** Multiple ack? Consumer crash?  
**Red flags:** Ack ngay khi nhận để tránh duplicate.  
**Evidence:** Theory `NOT CREATED`; case `MQ-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-003 — `FOUNDATION`
**Question:** At-most-once và at-least-once delivery khác nhau; RabbitMQ có exactly-once không?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Loss vs duplicate và end-to-end semantics.  
**Answer outline:** At-most-once có thể mất không duplicate; at-least-once redeliver nên consumer phải idempotent. Broker ack/publisher confirm không tạo exactly-once business side effect xuyên DB/network.  
**Required trade-offs:** At-least-once ưu tiên durability đổi dedup cost.  
**Follow-up ladder:** Redelivered flag đáng tin tới đâu?  
**Red flags:** Durable queue + manual ack = exactly-once.  
**Evidence:** Theory `NOT CREATED`; case `RMQ-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-004 — `FOUNDATION`
**Question:** Durable queue, persistent message và publisher confirm bảo vệ các failure khác nhau thế nào?  
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`  
**Interviewer evaluates:** Topology survival, message persistence và publish acceptance.  
**Answer outline:** Durable giữ queue definition qua restart; persistent yêu cầu message được lưu theo broker guarantees; confirms cho producer biết broker accepted/nack nhưng producer vẫn cần retry/idempotency và handle unroutable returns.  
**Required trade-offs:** Durability/confirm giảm throughput/tăng latency.  
**Follow-up ladder:** Mandatory flag? Quorum queue?  
**Red flags:** Persistent flag một mình chống mọi loss.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-005 — `SENIOR`
**Question:** Consumer idempotency được thiết kế bằng inbox/business key thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Atomic dedup với side effect.  
**Answer outline:** Stable event/message ID hoặc business operation key; unique inbox record và domain write trong cùng DB transaction; duplicate trả success/skip. Định nghĩa retention, payload version và concurrent claims.  
**Required trade-offs:** Inbox storage/cleanup vs duplicate safety horizon.  
**Follow-up ladder:** External side effect? Bloom filter?  
**Red flags:** In-memory Set dedupe production.  
**Evidence:** Theory `NOT CREATED`; design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-006 — `SENIOR`
**Question:** Retry queue/DLX nên thiết kế thế nào để tránh poison-message loop?  
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Bounded attempts, delay, classification và parking lot.  
**Answer outline:** Phân loại transient/permanent; bounded attempt metadata, delayed retry queues với backoff+jitter, cuối cùng DLQ/parking lot; alert, inspect/redrive có auth/audit và idempotency.  
**Required trade-offs:** Retry tăng recovery nhưng trì hoãn queue và khuếch đại load.  
**Follow-up ladder:** TTL/DLX cycle? Error headers?  
**Red flags:** Nack requeue=true vô hạn.  
**Evidence:** Theory `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-007 — `SENIOR`
**Question:** Prefetch/concurrency ảnh hưởng throughput, fairness và memory thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `COMMON_SCENARIO`  
**Interviewer evaluates:** Unacked window, task duration và backpressure.  
**Answer outline:** Prefetch cao giữ nhiều unacked trên consumer, tăng utilization nhưng giảm fairness/redelivery burst/memory; tune theo processing time/DB capacity, bounded concurrency và observe unacked/lag.  
**Required trade-offs:** Throughput vs fairness/failure blast radius.  
**Follow-up ladder:** Per-consumer/channel QoS?  
**Red flags:** Max prefetch luôn tốt nhất.  
**Evidence:** Theory `NOT CREATED`; load test `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-008 — `SENIOR`
**Question:** DB commit và publish RabbitMQ tránh lost event bằng outbox thế nào?  
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`  
**Interviewer evaluates:** Dual-write window, relay confirms và duplicates.  
**Answer outline:** Ghi business row+outbox cùng DB transaction; relay claim/publish, chờ confirm rồi mark; crash có thể republish nên consumer idempotent; monitor lag/failures và cleanup.  
**Required trade-offs:** Reliability đổi polling/CDC, latency và ops complexity.  
**Follow-up ladder:** Multiple relay workers? Ordering?  
**Red flags:** Publish trong `@Transactional` là atomic với DB.  
**Evidence:** Theory `NOT CREATED`; design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-009 — `ARCHITECT`
**Question:** Thiết kế event contract/versioning/topology ownership cho nhiều team thế nào?  
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`  
**Interviewer evaluates:** Schema compatibility, routing và lifecycle.  
**Answer outline:** Event owner/schema/version, additive evolution, immutable semantic type, consumer contract tests; topology as code với queue/DLX ownership, permissions, retention và deprecation telemetry.  
**Required trade-offs:** Governance giảm coupling rủi ro nhưng tăng coordination.  
**Follow-up ladder:** Event notification vs event-carried state?  
**Red flags:** Dùng chung một queue cho mọi consumer.  
**Evidence:** Design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

### RMQ-DEL-010 — `EXPERT`
**Question:** Queue backlog tăng dù consumer CPU thấp: điều tra từ broker tới DB thế nào?  
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`  
**Interviewer evaluates:** Publish/deliver/ack rates, unacked, waits và poison retries.  
**Answer outline:** Correlate ready/unacked, rates, consumer count/prefetch, redelivery/DLQ, connection/channel flow control; thread dumps, pool waits, DB locks/latency. CPU thấp thường là I/O wait. Contain retries và scale chỉ sau bottleneck.  
**Required trade-offs:** Tăng consumers có thể làm DB sập nhanh hơn.  
**Follow-up ladder:** Quorum disk alarm? Hot routing key?  
**Red flags:** Consumer CPU thấp nên chắc thiếu CPU.  
**Evidence:** Incident drill `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.  
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `MQ-01` active, fault-inject ack/crash/retry và lưu broker/DB evidence; không đổi/reuse stable IDs.

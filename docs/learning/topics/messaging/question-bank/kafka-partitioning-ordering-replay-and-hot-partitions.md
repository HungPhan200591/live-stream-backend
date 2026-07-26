# Messaging Interview Question Bank — Kafka Partitioning, Ordering, Replay and Hot Partitions

> Status: `DRAFT`<br>
> Domain owner: `Kafka`<br>
> Active slice: `NONE`; preview target: `KFK-01`<br>
> Related roadmap: [Stage 5](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-5---messaging-fundamentals-rabbitmq-và-kafka)<br>
> Related depth rubric: [Messaging](../../../knowledge-depth-rubric.md#316-rabbitmq-kafka-và-event-driven-workflow--p1-target-d3)<br>
> Related theory: [Core](../theory/core/kafka-partitioning-ordering-replay-and-hot-partitions.md) · [Deep-dive](../theory/deep-dives/kafka-rebalance-offsets-hot-partitions-and-exactly-once-boundaries.md)<br>
> Updated: `2026-07-26`

Preview only; không active/implement `KFK-01`; Kafka là comparison/roadmap topic, không thêm dependency. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `KAFKA-CORE-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### KAFKA-CORE-001 — `FOUNDATION`
**Question:** Topic, partition, offset, broker và consumer group là gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Append log và parallel consumption.<br>
**Answer outline:** Topic chia partitions là ordered append logs; offset là vị trí trong partition; brokers host replicas; trong một group mỗi partition tại thời điểm được assign một consumer, groups độc lập đọc.<br>
**Required trade-offs:** Nhiều partition tăng parallelism nhưng metadata/rebalance/order complexity.<br>
**Follow-up ladder:** Leader/follower? ISR?<br>
**Red flags:** Offset là global sequence toàn topic.<br>
**Evidence:** Theory [core](../theory/core/kafka-partitioning-ordering-replay-and-hot-partitions.md) + [deep-dive](../theory/deep-dives/kafka-rebalance-offsets-hot-partitions-and-exactly-once-boundaries.md); case `KFK-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### KAFKA-CORE-002 — `FOUNDATION`
**Question:** Kafka bảo đảm ordering ở phạm vi nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Per-partition order và key routing.<br>
**Answer outline:** Thứ tự chỉ trong một partition; cùng key thường route cùng partition nếu partition count/partitioner ổn định. Cross-partition không có total order tự nhiên. Consumer concurrency phải giữ per-key semantics.<br>
**Required trade-offs:** Strong per-key order giới hạn parallelism cho hot key.<br>
**Follow-up ladder:** Retry làm reorder? Add partitions?<br>
**Red flags:** Kafka đảm bảo thứ tự toàn topic.<br>
**Evidence:** Theory [core](../theory/core/kafka-partitioning-ordering-replay-and-hot-partitions.md) + [deep-dive](../theory/deep-dives/kafka-rebalance-offsets-hot-partitions-and-exactly-once-boundaries.md); case `KFK-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### KAFKA-CORE-003 — `FOUNDATION`
**Question:** Consumer commit offset trước và sau processing khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Loss vs duplicate.<br>
**Answer outline:** Commit trước xử lý có thể mất khi crash; commit sau side effect tạo redelivery/duplicate nếu crash trước commit. At-least-once cần idempotent consumer/inbox; commit theo batch phải hiểu partial failure.<br>
**Required trade-offs:** Throughput batch vs replay granularity.<br>
**Follow-up ladder:** Auto commit? Sync/async commit?<br>
**Red flags:** Manual commit tạo exactly-once DB effect.<br>
**Evidence:** Theory [core](../theory/core/kafka-partitioning-ordering-replay-and-hot-partitions.md) + [deep-dive](../theory/deep-dives/kafka-rebalance-offsets-hot-partitions-and-exactly-once-boundaries.md); case `KAFKA-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### KAFKA-CORE-004 — `FOUNDATION`
**Question:** Retention và log compaction khác nhau thế nào; replay hoạt động ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Time/size deletion, latest-by-key và offset reset.<br>
**Answer outline:** Retention xóa segment theo time/size; compaction giữ latest value per key về lâu dài (tombstone cho delete), không phải immediate. Replay reset/consumer group mới đọc offsets còn retained; handler phải replay-safe.<br>
**Required trade-offs:** Retention dài hỗ trợ recovery/audit nhưng tăng storage/privacy.<br>
**Follow-up ladder:** Tombstone retention? Earliest/latest?<br>
**Red flags:** Compacted topic chỉ chứa đúng một record/key mọi lúc.<br>
**Evidence:** Theory [core](../theory/core/kafka-partitioning-ordering-replay-and-hot-partitions.md) + [deep-dive](../theory/deep-dives/kafka-rebalance-offsets-hot-partitions-and-exactly-once-boundaries.md); experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### KAFKA-CORE-005 — `SENIOR`
**Question:** Chọn partition key thế nào để cân bằng ordering và distribution?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Business ordering domain, cardinality và skew.<br>
**Answer outline:** Key theo entity/invariant cần order (stream/account), đủ cardinality và stable; đo distribution/top keys. Không key cho round-robin khi không cần order; composite/bucketing chỉ khi downstream reconcile order.<br>
**Required trade-offs:** Locality/order vs hotspot/parallelism.<br>
**Follow-up ladder:** Null key? Changing partition count?<br>
**Red flags:** Random UUID luôn là key tốt.<br>
**Evidence:** Theory [core](../theory/core/kafka-partitioning-ordering-replay-and-hot-partitions.md) + [deep-dive](../theory/deep-dives/kafka-rebalance-offsets-hot-partitions-and-exactly-once-boundaries.md); design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### KAFKA-CORE-006 — `SENIOR`
**Question:** Consumer lag nghĩa là gì và chẩn đoán lag tăng thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `DIAGNOSTIC`<br>
**Interviewer evaluates:** Produce-consume rates, partition skew và processing waits.<br>
**Answer outline:** Lag là end offset trừ committed/processed offset, không tự bằng wall-clock delay. Xem per-partition lag/rates, rebalance, processing latency/errors, poll gaps, DB pool và hot keys; scale chỉ tới partition/capacity limit.<br>
**Required trade-offs:** More consumers không giúp nếu partitions ít hoặc downstream saturated.<br>
**Follow-up ladder:** Lag zero nhưng event chậm?<br>
**Red flags:** Tổng lag cao luôn do thiếu consumers.<br>
**Evidence:** Theory [core](../theory/core/kafka-partitioning-ordering-replay-and-hot-partitions.md) + [deep-dive](../theory/deep-dives/kafka-rebalance-offsets-hot-partitions-and-exactly-once-boundaries.md); experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### KAFKA-CORE-007 — `SENIOR`
**Question:** Idempotent producer, transactions và exactly-once semantics có giới hạn gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Broker dedup, read-process-write và external DB boundary.<br>
**Answer outline:** Idempotent producer ngăn duplicate do producer retries trong session/partition; Kafka transactions atomically publish/offset trong Kafka với isolation config. Không atomic với arbitrary PostgreSQL/HTTP; vẫn cần outbox/inbox/idempotency.<br>
**Required trade-offs:** EOS tăng coordination/latency/config discipline.<br>
**Follow-up ladder:** Transactional ID fencing? `read_committed`?<br>
**Red flags:** Kafka EOS làm toàn enterprise exactly-once.<br>
**Evidence:** Theory [core](../theory/core/kafka-partitioning-ordering-replay-and-hot-partitions.md) + [deep-dive](../theory/deep-dives/kafka-rebalance-offsets-hot-partitions-and-exactly-once-boundaries.md); design `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### KAFKA-CORE-008 — `SENIOR`
**Question:** Replay/backfill consumer mà không phá side effects production thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Separate group/sink, idempotency, throttle và verification.<br>
**Answer outline:** New group hoặc explicit offsets, versioned replay job, write tới shadow/new table hoặc idempotent upsert; disable external notifications, throttle downstream, checkpoint/metrics, compare then cutover.<br>
**Required trade-offs:** Online replay nhanh nhưng tranh capacity và có duplicate risk.<br>
**Follow-up ladder:** Schema evolution old events?<br>
**Red flags:** Reset offset production group trong giờ cao điểm.<br>
**Evidence:** Theory [core](../theory/core/kafka-partitioning-ordering-replay-and-hot-partitions.md) + [deep-dive](../theory/deep-dives/kafka-rebalance-offsets-hot-partitions-and-exactly-once-boundaries.md); replay plan `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### KAFKA-CORE-009 — `ARCHITECT`
**Question:** Khi nào chọn Kafka thay RabbitMQ cho live-stream backend?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Log/replay/throughput vs routing/work queue semantics.<br>
**Answer outline:** Kafka khi cần retained ordered event log, replay, stream processing, high throughput/multiple independent groups; RabbitMQ phù hợp command/work queue, flexible routing/per-message ack/retry. Chọn theo requirements/ops, không trend.<br>
**Required trade-offs:** Kafka replay/scale vs partition/schema/operations; Rabbit routing simplicity vs long replay model.<br>
**Follow-up ladder:** Dùng cả hai? Migration cost?<br>
**Red flags:** Kafka luôn thay thế RabbitMQ vì scale hơn.<br>
**Evidence:** Theory [core](../theory/core/kafka-partitioning-ordering-replay-and-hot-partitions.md) + [deep-dive](../theory/deep-dives/kafka-rebalance-offsets-hot-partitions-and-exactly-once-boundaries.md); current project RabbitMQ `EXISTS`; Kafka dependency `NOT ADDED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### KAFKA-CORE-010 — `EXPERT`
**Question:** Một hot partition làm lag lớn trong khi partitions khác rảnh: xử lý mà giữ ordering thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Skew evidence, key semantics và migration.<br>
**Answer outline:** Xác minh bytes/records/processing time theo partition/key; tối ưu handler/downstream trước. Nếu một key thật sự hot, shard key theo bucket chỉ khi business có sequence/reassembly, hoặc isolate dedicated topic/partition; migration/version routing và drain old offsets.<br>
**Required trade-offs:** Repartition tăng throughput nhưng mất simple per-entity order và gây state migration.<br>
**Follow-up ladder:** Heavy hitter detection? Add partitions effect?<br>
**Red flags:** Thêm partitions tự di chuyển/giải quyết existing hot key.<br>
**Evidence:** Theory [core](../theory/core/kafka-partitioning-ordering-replay-and-hot-partitions.md) + [deep-dive](../theory/deep-dives/kafka-rebalance-offsets-hot-partitions-and-exactly-once-boundaries.md); reproducer `NOT CREATED`; experiment `NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `KFK-01` active, chỉ tạo lab nếu roadmap kích hoạt; không thêm Kafka vào project và không đổi/reuse stable IDs.

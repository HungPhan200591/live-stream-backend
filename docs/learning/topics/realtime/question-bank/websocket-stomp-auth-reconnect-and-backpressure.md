# Realtime Interview Question Bank — WebSocket/STOMP Auth, Reconnect and Backpressure

> Status: `DRAFT`<br>
> Domain owner: `Realtime/WebSocket`<br>
> Active slice: `NONE`; preview target: `RT-01`<br>
> Related roadmap: [Stage 7](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-7---realtime-security-và-abuse-resistance)<br>
> Related depth rubric: [Distributed systems](../../../knowledge-depth-rubric.md#312-distributed-systems-fundamentals--p0-target-d3)<br>
> Related theory: `NOT CREATED`; planned target `docs/learning/topics/realtime/theory/core/websocket-stomp-auth-reconnect-and-backpressure.md`<br>
> Updated: `2026-07-26`

Preview only; không active/implement `RT-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `RT-WS-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### RT-WS-001 — `FOUNDATION`
**Question:** HTTP polling, SSE và WebSocket khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Direction, connection lifecycle và delivery semantics.<br>
**Answer outline:** Polling request/response; SSE server-to-client text stream/reconnect ID; WebSocket full-duplex frames. Chọn theo bidirectionality, scale, proxy/browser support và failure model.<br>
**Required trade-offs:** Persistent connections giảm polling overhead nhưng tăng state/resource ops.<br>
**Follow-up ladder:** HTTP/2 changes? STOMP adds what?<br>
**Red flags:** WebSocket luôn nhanh và đáng dùng hơn REST.<br>
**Evidence:** Theory `NOT CREATED`; case `RT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RT-WS-002 — `FOUNDATION`
**Question:** WebSocket handshake, STOMP CONNECT, SUBSCRIBE và SEND là các authorization boundary nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Transport upgrade vs protocol/session operations.<br>
**Answer outline:** Handshake xác thực origin/token baseline; CONNECT bind principal/session; SUBSCRIBE kiểm quyền destination; SEND kiểm role/ownership/mute/ban/payload mỗi action. Không tin destination do client gửi.<br>
**Required trade-offs:** Checks lặp tăng cost nhưng quyền có thể đổi trong session.<br>
**Follow-up ladder:** ChannelInterceptor? SockJS?<br>
**Red flags:** Auth handshake một lần đủ cho mọi frame.<br>
**Evidence:** Theory `NOT CREATED`; case `RT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RT-WS-003 — `FOUNDATION`
**Question:** Heartbeat, idle timeout và reconnect giải quyết điều gì?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Detect dead connection và session recovery.<br>
**Answer outline:** Heartbeat phát hiện half-open; idle timeout giải phóng resources; client reconnect bằng exponential backoff+jitter, resubscribe và resume token/last ID nếu protocol hỗ trợ.<br>
**Required trade-offs:** Nhanh phát hiện tăng heartbeat traffic/false disconnect.<br>
**Follow-up ladder:** Mobile sleep? Proxy timeout?<br>
**Red flags:** Reconnect ngay không delay là UX tốt nhất.<br>
**Evidence:** Theory `NOT CREATED`; case `RT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RT-WS-004 — `FOUNDATION`
**Question:** At-most/at-least-once và ordering trong realtime message path được hiểu ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Transport delivery không đồng nghĩa business delivery.<br>
**Answer outline:** WebSocket/STOMP không tự bảo đảm durable exactly-once; disconnect có loss/duplicate; sequence/message ID và server durable store/replay nếu requirement, order thường per room/partition.<br>
**Required trade-offs:** Durability/replay tăng latency/storage.<br>
**Follow-up ladder:** ACK modes? Duplicate chat?<br>
**Red flags:** TCP ordered nên app không bao giờ duplicate/mất message.<br>
**Evidence:** Theory `NOT CREATED`; case `RT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RT-WS-005 — `SENIOR`
**Question:** Token hết hạn hoặc user bị ban/mute giữa connection xử lý thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Continuous authorization và revocation propagation.<br>
**Answer outline:** Short session revalidation/event/cache flag; check ban/mute tại SUBSCRIBE/SEND, disconnect or restrict on revocation; PostgreSQL truth và bounded Redis staleness.<br>
**Required trade-offs:** Per-frame DB check đúng hơn nhưng không scale; cache/event có stale window.<br>
**Follow-up ladder:** Key rotation? Existing subscriptions?<br>
**Red flags:** Principal tạo lúc connect không bao giờ cần cập nhật.<br>
**Evidence:** Theory `NOT CREATED`; case `RT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RT-WS-006 — `SENIOR`
**Question:** Slow consumer gây unbounded queue và memory collapse thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Producer-consumer rate mismatch và backpressure policy.<br>
**Answer outline:** Bound per-session/outbound buffers, message/room quotas, drop/coalesce/disconnect policy theo message semantics, monitor queue age/bytes; fan-out không chờ client chậm.<br>
**Required trade-offs:** Drop giảm correctness; disconnect ảnh hưởng UX; durable replay có cost.<br>
**Follow-up ladder:** TCP send buffer? Reactive backpressure?<br>
**Red flags:** Thêm RAM chữa slow consumer.<br>
**Evidence:** Theory `NOT CREATED`; case `RT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RT-WS-007 — `SENIOR`
**Question:** Reconnect storm sau deploy/network outage được load-test và giảm thiểu thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Synchronized clients, auth/cache/DB surge.<br>
**Answer outline:** Clients exponential backoff+jitter, server admission/rate limit, session resume lightweight, stagger deploy; load model connections/sec, auth QPS, subscriptions, memory/FD and recovery time.<br>
**Required trade-offs:** Recovery chậm có UX cost nhưng tránh cascade.<br>
**Follow-up ladder:** Thundering herd on token refresh?<br>
**Red flags:** Autoscale sau storm là đủ.<br>
**Evidence:** Theory `NOT CREATED`; case `RT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RT-WS-008 — `SENIOR`
**Question:** WebSocket authorization negative tests cần phủ những gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Handshake, subscribe, send, ownership and lifecycle.<br>
**Answer outline:** Test missing/expired/wrong-audience token, forbidden room, spoofed sender, muted/banned user, oversized frame, revocation after connect, reconnect duplicate; assert close/error and no broadcast/side effect.<br>
**Required trade-offs:** Full broker integration chậm nhưng interceptor unit test không đủ.<br>
**Follow-up ladder:** Origin test? Fuzz frame?<br>
**Red flags:** Test HTTP endpoint auth đại diện cho WebSocket.<br>
**Evidence:** Theory `NOT CREATED`; case `RT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RT-WS-009 — `ARCHITECT`
**Question:** Thiết kế chat fan-out 100k viewers với hot room và failure isolation thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Connection gateways, partition/fan-out, backpressure và presence.<br>
**Answer outline:** Stateless gateways with connection registry partitioned by room, broker/topic shard, hot-room broadcast tree/batching, bounded per-node/session queues, presence approximate, drain/reconnect and SLO/capacity math.<br>
**Required trade-offs:** Low latency vs ordering/durability/cost.<br>
**Follow-up ladder:** Regional rooms? Celebrity problem?<br>
**Red flags:** Một Redis pub/sub channel và một node đủ khi scale ngang.<br>
**Evidence:** Theory `NOT CREATED`; case `RT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### RT-WS-010 — `EXPERT`
**Question:** Diagnose duplicate/out-of-order frames khi reconnect qua nhiều nodes thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Session epochs, sequence, broker redelivery và race timeline.<br>
**Answer outline:** Correlate connection/session epoch, message ID/room sequence, publish/consume/ACK and client resume cursor; dedup by stable ID, reject stale epoch and define gap replay. Clock timestamp không đủ order.<br>
**Required trade-offs:** Strict sequence coordination limits availability/scale.<br>
**Follow-up ladder:** Split brain gateways? Exactly-once UI?<br>
**Red flags:** Sort client messages by wall-clock timestamp.<br>
**Evidence:** Theory `NOT CREATED`; case `RT-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `RT-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.


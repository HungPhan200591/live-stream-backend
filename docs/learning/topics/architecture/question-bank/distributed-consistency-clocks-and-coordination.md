# Architecture Interview Question Bank — Distributed Consistency, Clocks and Coordination

> Status: `DRAFT`<br>
> Domain owner: `Distributed Systems`<br>
> Active slice: `NONE`; preview target: `DS-01`<br>
> Related roadmap: [Stage 10](../../../../001_SENIOR_JAVA_INTERVIEW_ROADMAP.md#stage-10---modular-monolith-to-microservices)<br>
> Related depth rubric: [Distributed systems](../../../knowledge-depth-rubric.md#312-distributed-systems-fundamentals--p0-target-d3)<br>
> Related theory: [Core](../theory/core/distributed-consistency-clocks-and-coordination.md) · [Deep-dive](../theory/deep-dives/partitions-leases-fencing-and-divergent-writer-recovery.md)<br>
> Updated: `2026-07-26`

Preview only; không active/implement `DS-01`. Likelihood là heuristic. Mọi câu `UNANSWERED`, tests `NOT RUN`.

## Coverage

| Level | Foundation | Senior | Architect | Expert |
| --- | ---: | ---: | ---: | ---: |
| Questions | 4 | 4 | 1 | 1 |

## Recommended practice order

First pass `DS-CONS-001..006`; senior follow-up `007..008`; stretch `009..010`.

## Questions

### DS-CONS-001 — `FOUNDATION`
**Question:** Partial failure và unbounded delay khác local exception thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Cannot know remote state from timeout.<br>
**Answer outline:** Some nodes/links fail while others work; response delay indistinguishable from failure within finite wait. Caller timeout means unknown outcome, requiring idempotency/status/reconciliation.<br>
**Required trade-offs:** Waiting longer gains certainty chance but holds resources.<br>
**Follow-up ladder:** Two generals? Failure detector?<br>
**Red flags:** Timeout chứng minh remote operation thất bại.<br>
**Evidence:** Theory [core](../theory/core/distributed-consistency-clocks-and-coordination.md) + [deep-dive](../theory/deep-dives/partitions-leases-fencing-and-divergent-writer-recovery.md); case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-002 — `FOUNDATION`
**Question:** Strong, eventual, read-your-writes và monotonic consistency khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Guarantees visible to client per operation/session.<br>
**Answer outline:** Operation linearizable trông như có một thứ tự hiện tại duy nhất; eventual consistency hội tụ theo thời gian; read-your-writes và monotonic read là bảo đảm theo session. Phải nêu guarantee theo từng use case, không gắn slogan cho cả database.<br>
**Required trade-offs:** Stronger consistency costs latency/availability/coordination.<br>
**Follow-up ladder:** Causal consistency?<br>
**Red flags:** Eventual consistency nghĩa data tùy ý.<br>
**Evidence:** Theory [core](../theory/core/distributed-consistency-clocks-and-coordination.md) + [deep-dive](../theory/deep-dives/partitions-leases-fencing-and-divergent-writer-recovery.md); case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-003 — `FOUNDATION`
**Question:** CAP và PACELC nên dùng trong context cụ thể ra sao?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Partition choice and normal latency-consistency trade-off.<br>
**Answer outline:** Khi network partition, mỗi operation phải ưu tiên availability hoặc consistency; khi mạng bình thường, PACELC nhấn mạnh đánh đổi latency với consistency. Hệ thống thật chọn theo từng path/quorum/config, không dán một nhãn cho toàn sản phẩm.<br>
**Required trade-offs:** Theorem frames limits but doesn't select product.<br>
**Follow-up ladder:** What is partition?<br>
**Red flags:** NoSQL là AP, SQL là CP mọi lúc.<br>
**Evidence:** Theory [core](../theory/core/distributed-consistency-clocks-and-coordination.md) + [deep-dive](../theory/deep-dives/partitions-leases-fencing-and-divergent-writer-recovery.md); case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-004 — `FOUNDATION`
**Question:** Wall clock, monotonic clock và logical/version clock khác nhau thế nào?<br>
**Target depth:** `D1-D2` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_CORE`<br>
**Interviewer evaluates:** Duration vs timestamp vs causality/order.<br>
**Answer outline:** Monotonic clock dùng đo thời lượng/deadline; wall clock phục vụ thời gian con người nhưng có thể nhảy/lệch; logical clock hoặc version counter ghi causal order trong phạm vi. Timestamp đơn lẻ không chứng minh nhân quả hay thứ tự toàn cục.<br>
**Required trade-offs:** Coordination for order costs latency/availability.<br>
**Follow-up ladder:** NTP? Lamport/vector clock?<br>
**Red flags:** System.currentTimeMillis tạo unique total order.<br>
**Evidence:** Theory [core](../theory/core/distributed-consistency-clocks-and-coordination.md) + [deep-dive](../theory/deep-dives/partitions-leases-fencing-and-divergent-writer-recovery.md); case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-005 — `SENIOR`
**Question:** Quorum read/write với N/R/W reasoning thế nào và có giới hạn gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Overlap, replica freshness and sloppy/conflict behavior.<br>
**Answer outline:** `R+W>N` có thể tạo quorum giao nhau dưới các giả định nhất định; write quorum ảnh hưởng durability. Tuy nhiên latency, node lỗi/stale, hinted handoff và conflict resolution vẫn quan trọng; quorum không tự tạo linearizability nếu thiếu protocol phù hợp.<br>
**Required trade-offs:** Higher quorum consistency/durability vs availability/latency.<br>
**Follow-up ladder:** Read repair? Leaderless?<br>
**Red flags:** Majority vote luôn trả latest value.<br>
**Evidence:** Theory [core](../theory/core/distributed-consistency-clocks-and-coordination.md) + [deep-dive](../theory/deep-dives/partitions-leases-fencing-and-divergent-writer-recovery.md); case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-006 — `SENIOR`
**Question:** Leader, lease và fencing token bảo vệ coordination thế nào?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `HIGH` · **Question type:** `COMMON_SCENARIO`<br>
**Interviewer evaluates:** Single authority and stale leader writes.<br>
**Answer outline:** Consensus/election chooses term; lease expiry alone may leave paused old leader; downstream accepts monotonically increasing fencing term and rejects stale writes. Business constraints remain safety net.<br>
**Required trade-offs:** Fencing requires downstream support and coordination.<br>
**Follow-up ladder:** GC pause? Clock lease?<br>
**Red flags:** Redis lock TTL bảo đảm old owner dừng.<br>
**Evidence:** Theory [core](../theory/core/distributed-consistency-clocks-and-coordination.md) + [deep-dive](../theory/deep-dives/partitions-leases-fencing-and-divergent-writer-recovery.md); case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-007 — `SENIOR`
**Question:** Duplicate/out-of-order events được xử lý bằng version/state machine ra sao?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Idempotency, causal scope and gap policy.<br>
**Answer outline:** Dùng event ID ổn định để dedup; apply có điều kiện theo aggregate version, bỏ duplicate cũ, buffer/retry/repair khi thiếu version; chọn partition key nếu cần ordering. Định nghĩa retention và xử lý poison event.<br>
**Required trade-offs:** Strict gap waiting can block progress.<br>
**Follow-up ladder:** Commutative events?<br>
**Red flags:** Sort all events by timestamp.<br>
**Evidence:** Theory [core](../theory/core/distributed-consistency-clocks-and-coordination.md) + [deep-dive](../theory/deep-dives/partitions-leases-fencing-and-divergent-writer-recovery.md); case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-008 — `SENIOR`
**Question:** Thiết kế failure matrix cho remote interaction gồm những gì?<br>
**Target depth:** `D2-D3` · **Interview likelihood:** `MEDIUM` · **Question type:** `PROJECT_APPLICATION`<br>
**Interviewer evaluates:** Các mốc trước/trong/sau commit, timeout/retry/cancel và cách phục hồi.<br>
**Answer outline:** Enumerate DNS/connect/TLS/timeout, server crash pre/post side effect, response loss, duplicate/reorder, dependency overload; for each define deadline, idempotency, retry budget, state query, compensation, telemetry/runbook.<br>
**Required trade-offs:** More safeguards add state/ops cost; prioritize invariant/blast radius.<br>
**Follow-up ladder:** Byzantine failures?<br>
**Red flags:** Happy path plus one timeout test đủ.<br>
**Evidence:** Theory [core](../theory/core/distributed-consistency-clocks-and-coordination.md) + [deep-dive](../theory/deep-dives/partitions-leases-fencing-and-divergent-writer-recovery.md); case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-009 — `ARCHITECT`
**Question:** Chọn consistency model cho multi-region wallet, profile và analytics khác nhau thế nào?<br>
**Target depth:** `D3-D4` · **Interview likelihood:** `LOW` · **Question type:** `ARCHITECT_STRETCH`<br>
**Interviewer evaluates:** Per-domain invariant and regional topology.<br>
**Answer outline:** Wallet có thể dùng single writer/consensus hoặc regional ownership với ledger; profile có thể chấp nhận last-write-wins/read-your-writes; analytics xử lý async và replay. Nêu RPO/RTO/latency cùng policy conflict/reconciliation cho từng domain.<br>
**Required trade-offs:** Global correctness vs local latency/availability.<br>
**Follow-up ladder:** Follow-the-sun ownership?<br>
**Red flags:** Một multi-region database setting cho mọi data.<br>
**Evidence:** Theory [core](../theory/core/distributed-consistency-clocks-and-coordination.md) + [deep-dive](../theory/deep-dives/partitions-leases-fencing-and-divergent-writer-recovery.md); case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

### DS-CONS-010 — `EXPERT`
**Question:** Dẫn incident network partition với two writers và divergent side effects thế nào?<br>
**Target depth:** `D4` · **Interview likelihood:** `LOW` · **Question type:** `EXPERT_DIAGNOSTIC`<br>
**Interviewer evaluates:** Fencing, source of truth and reconciliation.<br>
**Answer outline:** Chặn hoặc giới hạn write, xác lập epoch/ledger có thẩm quyền, giữ cả hai lịch sử, đối chiếu idempotency/business ID và external effect, compensate có audit; rebuild replica, sửa routing/election rồi cập nhật bài diễn tập partition.<br>
**Required trade-offs:** Availability during partition may have created costly conflicts.<br>
**Follow-up ladder:** Split-brain detection delay?<br>
**Red flags:** Merge rows bằng latest timestamp.<br>
**Evidence:** Theory [core](../theory/core/distributed-consistency-clocks-and-coordination.md) + [deep-dive](../theory/deep-dives/partitions-leases-fencing-and-divergent-writer-recovery.md); case `DS-01 NOT CREATED`; tests `NOT RUN`; note `NOT CREATED`.<br>
**Self-assessment:** `UNANSWERED`

## Deferred normalization

Khi `DS-01` active, link theory/case và gắn evidence thật; không đổi/reuse stable IDs.
